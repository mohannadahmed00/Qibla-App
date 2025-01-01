package com.giraffe.qiblaapp.ui.screens.qibla

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.giraffe.qiblaapp.LocalActivity
import com.giraffe.qiblaapp.core.calculateBearing
import com.giraffe.qiblaapp.core.getCurrentLocation
import com.giraffe.qiblaapp.core.isLocationServicesEnabled
import com.giraffe.qiblaapp.core.justTwoDigits
import com.giraffe.qiblaapp.core.launchPermissionSettings
import com.giraffe.qiblaapp.ui.components.Compass
import com.giraffe.qiblaapp.ui.components.DisposableEffectWithLifecycle
import com.giraffe.qiblaapp.ui.components.LocationTextProvider
import com.giraffe.qiblaapp.ui.components.PermissionDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun QiblaScreen(
    viewModel: QiblaViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    QiblaContent(state = state, events = viewModel, modifier = modifier)
}

@Composable
fun QiblaContent(
    state: QiblaScreenState,
    events: QiblaScreenEvents,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    val multiplePermissionsResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = !permissions.containsValue(false)
            val isPermanentlyDeclined = if (!isGranted) {
                val isFinePermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    activity,
                    state.permissionsToRequest.first()
                )
                val isCoarsePermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    activity,
                    state.permissionsToRequest.last()
                )
                isFinePermanentlyDeclined || isCoarsePermanentlyDeclined
            } else {
                isLocationServicesEnabled(
                    context = context
                ) {
                    getCurrentLocation(
                        context = context,
                        onGetCurrentLocationSuccess = {
                            events.setCurrentLocation(Pair(it.first, it.second))
                            events.setQiblaAngle(
                                calculateBearing(
                                    firstLocation = state.currentLocation,
                                    secondLocation = state.meccaLocation
                                )
                            )
                        },
                        onGetCurrentLocationFailed = {
                            events.setLocationErrorText(
                                it.localizedMessage ?: "Error getting current location"
                            )
                        },
                    )
                }
                false
            }
            events.onPermissionResult(isGranted, isPermanentlyDeclined)
        },
    )
    DisposableEffectWithLifecycle(onStart = {
        multiplePermissionsResultLauncher.launch(state.permissionsToRequest.toTypedArray())
    })
    DisposableEffect(Unit) {
        val sensorEventListener =
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                        val rotationMatrix = FloatArray(9)
                        val orientationAngles = FloatArray(3)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)
                        val azimuth =
                            orientationAngles[0]//angle of rotation about the -z axis. This value represents the angle between the device's y axis and the magnetic north pole.
                        val pitch =
                            orientationAngles[1]//angle of rotation about the x axis. This value represents the angle between a plane parallel to the device's screen and a plane parallel to the ground.
                        val roll =
                            orientationAngles[2]//angle of rotation about the y axis. This value represents the angle between a plane perpendicular to the device's screen and a plane perpendicular to the ground.
                        events.setAzimuthAngle(Math.toDegrees(azimuth.toDouble()).toFloat())
                        events.setPitchAngle(Math.toDegrees(pitch.toDouble()).toFloat())
                        events.setRollAngle(Math.toDegrees(roll.toDouble()).toFloat())
                    }
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
            }
        sensorManager.registerListener(
            sensorEventListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "(Pitch) Angle: ${state.pitchAngle.justTwoDigits()}°")
            Text(text = "(Roll) Angle: ${state.rollAngle.justTwoDigits()}°")
            Text(text = "(Azimuth) Angle: ${state.azimuthAngle.justTwoDigits()}°")
            state.azimuthAngle.let {
                Text(text = "(North) Angle: ${(if (it < 0) it + 360 else it).justTwoDigits()}°")
            }
            Text("(Qibla) Angle degree: ${state.qiblaAngle.justTwoDigits()}°")
            Text(text = "Mecca location: ${state.meccaLocation.first.justTwoDigits()} , ${state.meccaLocation.second.justTwoDigits()}")
            state.locationErrorText?.let {
                Text("Current location: $it", color = MaterialTheme.colorScheme.error)
            }
                ?: Text(text = "Current location: ${state.currentLocation.first.justTwoDigits()} , ${state.currentLocation.second.justTwoDigits()}")
        }
        Compass(
            rotationAngle = state.azimuthAngle, qiblaAngle = state.qiblaAngle,
            pitchAngle = state.pitchAngle,
            rollAngle = state.rollAngle,
        )
    }
    if (state.isDialogVisible) {
        PermissionDialog(
            permission = LocationTextProvider(),
            isPermanentlyDeclined = state.isPermanentlyDeclined,
            onDismiss = events::dismissDialog,
            onOkClick = {
                events.dismissDialog()
                multiplePermissionsResultLauncher.launch(state.permissionsToRequest.toTypedArray())
            },
            onGoToAppSettingsClick = { launchPermissionSettings(context) },
        )
    }
}