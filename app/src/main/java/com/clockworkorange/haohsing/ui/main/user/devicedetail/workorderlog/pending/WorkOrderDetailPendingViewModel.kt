package com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog.pending

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.ManufacturerInfo
import com.clockworkorange.domain.usecase.device.GetDeviceDetailUseCase
import com.clockworkorange.domain.usecase.device.GetManufacturerUseCase
import com.clockworkorange.domain.usecase.workorder.CancelWorkOrderUseCase
import com.clockworkorange.domain.usecase.workorder.GetPendingWorkOrderDetailUseCase
import com.clockworkorange.domain.usecase.workorder.PendingWorkOrderDetail
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import com.clockworkorange.haohsing.ui.base.NavAction
import com.clockworkorange.haohsing.ui.base.NavUp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkOrderDetailPendingViewModel @Inject constructor(
    private val getWorkOrderDetailUseCase: GetPendingWorkOrderDetailUseCase,
    private val getManufacturerUseCase: GetManufacturerUseCase,
    private val getDeviceDetailUseCase: GetDeviceDetailUseCase,
    private val cancelWorkOrderUseCase: CancelWorkOrderUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val pendingWorkOrderDetail: PendingWorkOrderDetail? = null,
        val manufacturerInfo: ManufacturerInfo? = null,
        val isOwner: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _navAction = Channel<NavAction>(capacity = Channel.CONFLATED)
    val navAction = _navAction.receiveAsFlow()

    private var orderId: Int = 0


    fun setOrderId(orderId: Int) {
        this.orderId = orderId
        viewModelScope.launch {
            when (val result = getWorkOrderDetailUseCase.invoke(orderId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(pendingWorkOrderDetail = result.data) }
                }
                is Result.Error -> {
                    showToast("無法取得紀錄")
                }
            }
        }
    }

    var job: Job? = null
    fun setDeviceId(deviceId: Int) {
        job?.cancel()
        job = viewModelScope.launch {
            launch {
                getManufacturerUseCase.invoke(deviceId).collect {
                    it.data?.let { info ->
                        _uiState.update { it.copy(manufacturerInfo = info) }
                    }
                }
            }

            launch {
                getDeviceDetailUseCase.invoke(deviceId).collect { result ->
                    _uiState.update { it.copy(isOwner = result.data?.isOwner == true) }
                }
            }

        }
    }

    fun cancelWorkOrder() {
        viewModelScope.launch {
            if (orderId == 0) return@launch

            val result = cancelWorkOrderUseCase.invoke(orderId)
            if (result.data == true) {
                showToast("取消成功")
                _navAction.trySend(NavUp)
            }
        }
    }
}

