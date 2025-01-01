package com.giraffe.qiblaapp.ui.screens.qibla

interface QiblaScreenEvents {
    fun dismissDialog()
    fun onPermissionResult(isGranted: Boolean, isPermanentlyDeclined: Boolean)
    fun setAzimuthAngle(value: Float)
    fun setPitchAngle(value: Float)
    fun setRollAngle(value: Float)
    fun setCurrentLocation(point: Pair<Double, Double>)
    fun setLocationErrorText(text: String)
    fun setQiblaAngle(value: Float)
}