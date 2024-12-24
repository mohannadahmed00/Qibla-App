package com.giraffe.qiblaapp.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.giraffe.qiblaapp.ui.components.Compass
import kotlin.math.floor


@Composable
fun QiblaScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    var northAngle by remember { mutableFloatStateOf(0f) }
    var actualNorthAngle by remember { mutableFloatStateOf(0f) }
    DisposableEffect(Unit) {
        val sensorEventListener =
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                        val rotationMatrix = FloatArray(9)
                        val orientationAngles = FloatArray(3)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        /*
                            /  R[ 0]   R[ 1]   R[ 2]   \
                            |  R[ 3]   R[ 4]   R[ 5]   |
                            \  R[ 6]   R[ 7]   R[ 8]   /
                        */
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)
                        val azimuth =
                            orientationAngles[0]//angle of rotation about the -z axis. This value represents the angle between the device's y axis and the magnetic north pole.
                        northAngle = Math.toDegrees(azimuth.toDouble()).toFloat()
                        actualNorthAngle = if (northAngle < 0) northAngle + 360 else northAngle
                    }
                }

                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
            }
        sensorManager.registerListener(
            sensorEventListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_UI
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
            Text(text = "(North) Angle: ${floor(northAngle)}°")
            Text(
                text = "(North) Actual angle: ${floor(actualNorthAngle)}°"
            )
        }
        Compass(rotationAngle = northAngle)
    }
}


