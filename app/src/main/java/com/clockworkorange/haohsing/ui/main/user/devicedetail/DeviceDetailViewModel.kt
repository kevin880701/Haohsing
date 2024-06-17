package com.clockworkorange.haohsing.ui.main.user.devicedetail

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.DeviceDetail
import com.clockworkorange.domain.entity.FilterStatus
import com.clockworkorange.domain.usecase.device.*
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val getDeviceDetailUseCase: GetDeviceDetailUseCase,
    private val getFilterStatusUseCase: GetFilterStatusUseCase,
    private val updateDeviceNameUseCase: UpdateDeviceNameUseCase,
    private val setDeviceValueUseCase: SetDeviceValueUseCase,
    private val quitDeviceUseCase: QuitDeviceUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val previousButtonVisible: Boolean = false,
        val nextButtonVisible: Boolean = false,
        val device: DeviceDetail? = null,
        val deviceFilterStatus: List<FilterStatus> = emptyList(),
        val isDeviceDeleted: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val currentIndexAndDeviceList =
        MutableStateFlow<Pair<Int, IntArray>>(Pair(0, intArrayOf()))

    private val currentDeviceId = currentIndexAndDeviceList
        .filter { it.second.isNotEmpty() }
        .map { (index, list) ->
            _uiState.update {
                it.copy(
                    previousButtonVisible = index != 0,
                    nextButtonVisible = index != (list.size - 1)
                )
            }
            list[index]
        }.stateIn(viewModelScope, SharingStarted.Eagerly, -1)
        .filter { it > 0 }

    init {
        viewModelScope.launch {
            currentDeviceId
                .flatMapLatest { id -> getDeviceDetailUseCase.invoke(id) }
                .collect { result ->
                    result.data?.let { deviceDetail ->
                        _uiState.update { it.copy(device = deviceDetail) }
                    }
                }
        }

        viewModelScope.launch {
            currentDeviceId
                .flatMapLatest { deviceId -> getFilterStatusUseCase.invoke(deviceId) }
                .collect { result ->
                    _uiState.update { it.copy(deviceFilterStatus = result.data ?: emptyList()) }
                }
        }
    }

    fun setDevice(deviceId: Int, deviceIdList: IntArray) {

        val index = deviceIdList.indexOf(deviceId)

        if (index == -1) {
            currentIndexAndDeviceList.tryEmit(Pair(0, deviceIdList))
        } else {
            currentIndexAndDeviceList.tryEmit(Pair(index, deviceIdList))
        }
    }

    fun previousDevice() {
        val index = currentIndexAndDeviceList.value.first - 1
        if (index < 0) return
        currentIndexAndDeviceList.update { it.copy(first = index) }
    }

    fun nextDevice() {
        val index = currentIndexAndDeviceList.value.first + 1
        if (index > currentIndexAndDeviceList.value.second.size - 1) return
        currentIndexAndDeviceList.update { it.copy(first = index) }
    }

    fun editDeviceName(newName: String) {
        viewModelScope.launch {
            val deviceId = uiState.value.device?.id ?: return@launch
            when (updateDeviceNameUseCase.invoke(UpdateDeviceNameParam(deviceId, newName))) {
                is Result.Success -> {
                    showToast("編輯名稱成功")
                    refresh()
                }
                is Result.Error -> {
                    showToast("編輯名稱失敗")
                }
                else -> {}
            }

        }
    }

    fun setDeviceValue(code: String, enable: Boolean) {
        viewModelScope.launch {
            val deviceId = _uiState.value.device?.id
            deviceId ?: return@launch
            val value = if (enable) "1" else "0"
            val setResult = setDeviceValueUseCase.invoke(SetDeviceValueParam(deviceId, code, value))
            if (setResult.data == true) {
                showToast("設定成功")
                refresh()
            } else {
                showToast("設定失敗")
            }
        }
    }

    fun quitDevice() {
        val currentDeviceId = uiState.value.device?.id ?: return
        viewModelScope.launch {
            val result = quitDeviceUseCase(currentDeviceId)
            if (result.data == true) _uiState.update { it.copy(isDeviceDeleted = true) }
        }
    }

    private suspend fun refresh() {
        val currentDeviceId = uiState.value.device?.id ?: return
        getDeviceDetailUseCase.invoke(currentDeviceId).first().data?.let { deviceDetail ->
            _uiState.update { it.copy(device = deviceDetail) }
        }
    }

}