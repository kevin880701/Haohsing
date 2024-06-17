package com.clockworkorange.haohsing.ui.main.user.devicedetail.troubleshooting

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TroubleShootingViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

        }
    }

    fun setDeviceId(deviceId: Int) {

    }

}