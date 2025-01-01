package com.giraffe.qiblaapp.core

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onGetCurrentLocationSuccess: (Pair<Double, Double>) -> Unit,
    onGetCurrentLocationFailed: (Exception) -> Unit,
    priority: Boolean = true
) {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val accuracy = if (priority) Priority.PRIORITY_HIGH_ACCURACY
    else Priority.PRIORITY_BALANCED_POWER_ACCURACY
    if (areLocationPermissionsGranted(context)) {
        fusedLocationProviderClient.getCurrentLocation(
            accuracy, CancellationTokenSource().token,
        ).addOnSuccessListener { location ->
            location?.let {
                onGetCurrentLocationSuccess(Pair(it.latitude, it.longitude))
            }
        }.addOnFailureListener { exception ->
            onGetCurrentLocationFailed(exception)
        }
    }
}

fun areLocationPermissionsGranted(context: Context): Boolean {
    return (ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
}

fun isLocationServicesEnabled(context: Context, onEnabled: () -> Unit): Boolean {
    val localeManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = try {
        localeManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (ex: Exception) {
        false
    }

    val isNetworkEnabled = try {
        localeManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    } catch (ex: Exception) {
        false
    }

    if (isGpsEnabled || isNetworkEnabled) {
        onEnabled()
    } else {
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    return isGpsEnabled || isNetworkEnabled
}

fun launchPermissionSettings(context: Context) {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.fromParts("package", context.packageName, null)
    context.startActivity(intent)
}

fun calculateBearing(
    firstLocation: Pair<Double, Double>,
    secondLocation: Pair<Double, Double>,
): Float {
    val lat1Rad = Math.toRadians(firstLocation.first)
    val lon1Rad = Math.toRadians(firstLocation.second)
    val lat2Rad = Math.toRadians(secondLocation.first)
    val lon2Rad = Math.toRadians(secondLocation.second)

    val deltaLon = lon2Rad - lon1Rad

    val y = sin(deltaLon) * cos(lat2Rad)
    val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLon)
    var bearing = atan2(y, x)

    bearing = Math.toDegrees(bearing)

    if (bearing < 0) {
        bearing += 360
    }

    return bearing.toFloat()
}