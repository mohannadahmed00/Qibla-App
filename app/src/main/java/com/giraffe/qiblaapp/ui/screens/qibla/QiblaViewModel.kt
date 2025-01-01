package com.giraffe.qiblaapp.ui.screens.qibla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QiblaViewModel : ViewModel(), QiblaScreenEvents {

    private val _state = MutableStateFlow(QiblaScreenState())
    val state = _state.asStateFlow()

    override fun dismissDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isDialogVisible = false) }
        }
    }

    override fun onPermissionResult(isGranted: Boolean, isPermanentlyDeclined: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(
                    isPermissionsGranted = isGranted,
                    isDialogVisible = !isGranted,
                    isPermanentlyDeclined = isPermanentlyDeclined
                )
            }
        }
    }

    override fun setAzimuthAngle(value: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(azimuthAngle = value) }
        }
    }

    override fun setPitchAngle(value: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(pitchAngle = value) }
        }
    }

    override fun setRollAngle(value: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(rollAngle = value) }
        }
    }

    override fun setCurrentLocation(point: Pair<Double, Double>) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(currentLocation = point) }
        }
    }

    override fun setLocationErrorText(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(locationErrorText = text) }
        }
    }

    override fun setQiblaAngle(value: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(qiblaAngle = value) }
        }
    }
}