package com.clockworkorange.haohsing.ui.main.user.setting.device

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.DeviceDetail
import com.clockworkorange.domain.usecase.device.*
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdvancedSettingViewModel @Inject constructor(
    private val getDeviceDetailUseCase: GetDeviceDetailUseCase,
    private val setDeviceValueUseCase: SetDeviceValueUseCase,
    private val getWaterTempOptionUseCase: GetWaterTempOptionUseCase,
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val deviceDetail: DeviceDetail? = null,
        val hotWaterTempOptions: List<String> = emptyList(),
        val coldWaterTempOptions: List<String> = emptyList(),
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var deviceId: Int = 0

    init {
        viewModelScope.launch {
            getWaterTempOptionUseCase.invoke(WaterTempType.Hot).data?.let { options ->
                _uiState.update { it.copy(hotWaterTempOptions = options) }
            }
        }

        viewModelScope.launch {
            getWaterTempOptionUseCase.invoke(WaterTempType.Cold).data?.let { options ->
                _uiState.update { it.copy(coldWaterTempOptions = options) }
            }
        }
    }

    fun setDeviceId(deviceId: Int) {
        this.deviceId = deviceId
        fetchSetting()
    }

    private fun fetchSetting() {
        viewModelScope.launch {
            getDeviceDetailUseCase.invoke(deviceId)
                .collect { result ->
                    result.data.let { detail ->
                        _uiState.update { it.copy(deviceDetail = detail) }
                    }
                }
        }
    }

    fun toggleReheat() {
        viewModelScope.launch {
            val currentState = _uiState.value.deviceDetail?.advancedSetting?.reheatEnable ?: false
            val newState = !currentState
            val value = if (newState) "1" else "0"

            val result = setDeviceValueUseCase.invoke(SetDeviceValueParam(deviceId, "h01", value))
            if (result.data == true) {
                showToast("設定成功")
            }
        }
    }

    fun selectHotWaterTemp(temp: String) {
        viewModelScope.launch {
            val result = setDeviceValueUseCase.invoke(SetDeviceValueParam(deviceId, "h05", temp))
            if (result.data == true) {
                showToast("設定成功")
            }
        }
    }

    fun selectColdWaterTemp(temp: String) {
        viewModelScope.launch {
            val result = setDeviceValueUseCase.invoke(SetDeviceValueParam(deviceId, "h07", temp))
            if (result.data == true) {
                showToast("設定成功")
            }
        }
    }

    fun disableSleepTimer() {
        viewModelScope.launch {
            val result = setDeviceValueUseCase.invoke(SetDeviceValueParam(deviceId, "h25", "0"))
            if (result.data == true) {
                showToast("設定成功")
            }
        }
    }

    fun disablePowerOnTimer() {
        viewModelScope.launch {
            val result = setDeviceValueUseCase.invoke(SetDeviceValueParam(deviceId, "h24", "0"))
            if (result.data == true) {
                showToast("設定成功")
            }
        }
    }

    fun enableSleepTimer(time: String) {
        viewModelScope.launch {
            val formattedTime = time.replace(":", "/")
            val result =
                setDeviceValueUseCase.invoke(SetDeviceValueParam(deviceId, "h25", formattedTime))
            if (result.data == true) {
                showToast("設定成功")
            }
        }
    }

    fun enablePowerOnTimer(time: String) {
        viewModelScope.launch {
            val formattedTime = time.replace(":", "/")
            val result =
                setDeviceValueUseCase.invoke(SetDeviceValueParam(deviceId, "h24", formattedTime))
            if (result.data == true) {
                showToast("設定成功")
            }
        }
    }

}