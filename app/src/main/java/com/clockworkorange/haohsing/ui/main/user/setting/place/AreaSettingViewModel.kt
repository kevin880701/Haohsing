package com.clockworkorange.haohsing.ui.main.user.setting.place

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.PowerSchedule
import com.clockworkorange.domain.usecase.device.*
import com.clockworkorange.domain.usecase.palcearea.GetAreaListUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AreaSettingViewModel @Inject constructor(
    private val setAreaDeviceValueUseCase: SetAreaDeviceValueUseCase,
    private val getWaterTempOptionUseCase: GetWaterTempOptionUseCase,
    private val getAreaScheduleListUseCase: GetAreaScheduleListUseCase,
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val hotWaterTempOptions: List<String> = emptyList(),
        val coldWaterTempOptions: List<String> = emptyList(),
        val schedules: List<PowerSchedule> = emptyList(),
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var areaId: Int = 0

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

    fun setAreaId(areaId: Int) {
        this.areaId = areaId
        fetchSetting()
    }

    private fun fetchSetting() {
        viewModelScope.launch {
            val result = getAreaScheduleListUseCase.invoke(areaId)
            result.data?.let { list ->
                _uiState.update { it.copy(schedules = list) }
            }
        }
    }

    fun setFeatEnableOrNot(code: String, enable: Boolean) {
        viewModelScope.launch {
            val value = if (enable) "1" else "0"
            val setResult = setAreaDeviceValueUseCase(SetAreaDeviceValueParam(areaId, code, value))
            if (setResult.data == true) {
                showToast("設定成功")
            } else {
                showToast("設定失敗")
            }
        }
    }

    fun selectHotWaterTemp(temp: String) {
        viewModelScope.launch {
            val result = setAreaDeviceValueUseCase(SetAreaDeviceValueParam(areaId, "h05", temp))
            if (result.data == true) {
                showToast("設定成功")
            }
        }
    }

    fun selectColdWaterTemp(temp: String) {
        viewModelScope.launch {
            val result = setAreaDeviceValueUseCase(SetAreaDeviceValueParam(areaId, "h07", temp))
            if (result.data == true) {
                showToast("設定成功")
            }
        }
    }
}