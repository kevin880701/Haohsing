package com.clockworkorange.haohsing.ui.main.engineer.install

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.Device
import com.clockworkorange.domain.usecase.device.GetDevicesUseCase
import com.clockworkorange.domain.usecase.device.TransferOwnerParam
import com.clockworkorange.domain.usecase.device.TransferOwnerUseCase
import com.clockworkorange.domain.usecase.notification.Duration
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EngineerTransferOwnerViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase,
    private val transferOwnerUseCase: TransferOwnerUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val devices: MutableList<UiDevice> = mutableListOf(),
        val isTransferSuccess: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            getDevicesUseCase(Unit).map { it.data }.filterNotNull().collect { devices ->
                val dateSet = devices.map { it.installDateTime?.toLocalDate() }.toMutableSet()
                val uiDevices = devices.sortedBy { it.installDateTime }.map {
                    // let first record on the same date show header
                    val date = it.installDateTime?.toLocalDate()
                    val showDateHeader = dateSet.contains(date)
                    if (showDateHeader) dateSet.remove(date)
                    UiDevice(it, showDateHeader)
                }
                _uiState.update { it.copy(devices = uiDevices.toMutableList()) }
            }
        }
    }

    fun transferOwner(account: String) {
        viewModelScope.launch {
            val ids = uiState.value.devices.filter { it.isSelected }.map { it.device.id }
            if (ids.isEmpty()) return@launch
            val result = transferOwnerUseCase(TransferOwnerParam(account, ids))
            if (result.data == true) {
                _uiState.update { it.copy(isTransferSuccess = true) }
            } else {
                showToast("轉移失敗")
            }
        }
    }
}

data class UiDevice(
    val device: Device,
    val showDateHeader: Boolean,
    var isSelected: Boolean = false,
)