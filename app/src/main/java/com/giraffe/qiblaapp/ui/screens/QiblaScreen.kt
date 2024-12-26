package com.giraffe.qiblaapp.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.giraffe.qiblaapp.common.RequestLocationPermission
import com.giraffe.qiblaapp.common.calculateBearing
import com.giraffe.qiblaapp.common.getCurrentLocation
import com.giraffe.qiblaapp.common.justTwoDigits
import com.giraffe.qiblaapp.ui.components.Compass

@Composable
fun QiblaScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    var azimuthAngle by remember { mutableFloatStateOf(0f) }
    var pitchAngle by remember { mutableFloatStateOf(0f) }
    var rollAngle by remember { mutableFloatStateOf(0f) }

    var actualNorthAngle by remember { mutableFloatStateOf(0f) }


    val meccaLocation = remember { Pair(21.422512, 39.826184) }
    val currentLocation = remember { mutableStateOf(Pair(0.0, 0.0)) }
    val locationErrorText = remember { mutableStateOf<String?>(null) }
    val qiblaAngle = remember { mutableFloatStateOf(0f) }


    RequestLocationPermission(
        onPermissionGranted = {
            getCurrentLocation(
                context = context,
                onGetCurrentLocationSuccess = {
                    currentLocation.value = Pair(it.first, it.second)
                    qiblaAngle.floatValue = calculateBearing(
                        firstLocation = currentLocation.value,
                        secondLocation = meccaLocation
                    )
                },
                onGetCurrentLocationFailed = {
                    locationErrorText.value =
                        it.localizedMessage ?: "Error getting current location"
                },
            )
        },
        onPermissionDenied = { locationErrorText.value = "Permission denied :(" }
    )

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
                        azimuthAngle = Math.toDegrees(azimuth.toDouble()).toFloat()
                        pitchAngle = Math.toDegrees(pitch.toDouble()).toFloat()
                        rollAngle = Math.toDegrees(roll.toDouble()).toFloat()
                        actualNorthAngle =
                            if (azimuthAngle < 0) azimuthAngle + 360 else azimuthAngle
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
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "(Pitch) Angle: ${pitchAngle.justTwoDigits()}°")
            Text(text = "(Roll) Angle: ${rollAngle.justTwoDigits()}°")
            Text(text = "(Azimuth) Angle: ${azimuthAngle.justTwoDigits()}°")
            Text(text = "(North) Angle: ${actualNorthAngle.justTwoDigits()}°")
            Text("(Qibla) Angle degree: ${qiblaAngle.floatValue.justTwoDigits()}°")
        }
        Compass(
            rotationAngle = azimuthAngle, qiblaAngle = qiblaAngle.floatValue,
            pitchAngle = pitchAngle,
            rollAngle = rollAngle,
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Mecca location: ${meccaLocation.first.justTwoDigits()} , ${meccaLocation.second.justTwoDigits()}")
                locationErrorText.value?.let {
                    Text("Current location: $it", color = MaterialTheme.colorScheme.error)
                }
                    ?: Text(text = "Current location: ${currentLocation.value.first.justTwoDigits()} , ${currentLocation.value.second.justTwoDigits()}")
            }
        }
    }
}


