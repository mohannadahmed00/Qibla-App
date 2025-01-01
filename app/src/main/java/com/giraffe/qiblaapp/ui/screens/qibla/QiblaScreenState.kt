package com.giraffe.qiblaapp.ui.screens.qibla

import android.Manifest
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class QiblaScreenState(
    val isPermissionsGranted: Boolean = true,
    val isDialogVisible: Boolean = false,
    val isPermanentlyDeclined: Boolean = false,
    val visiblePermissionDialogQueue: SnapshotStateList<String> = mutableStateListOf(),
    val permissionsToRequest: List<String> = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ),
    val qiblaAngle: Float = 0f,
    val locationErrorText: String? = null,
    val currentLocation: Pair<Double, Double> = Pair(0.0, 0.0),
    val meccaLocation: Pair<Double, Double> = Pair(21.422512, 39.826184),
    val azimuthAngle: Float = 0f,
    val pitchAngle: Float = 0f,
    val rollAngle: Float = 0f,
)