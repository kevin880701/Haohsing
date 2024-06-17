package com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.data
import com.clockworkorange.domain.usecase.workorder.GetWorkOrderParam
import com.clockworkorange.domain.usecase.workorder.GetWorkOrderUseCase
import com.clockworkorange.domain.usecase.workorder.WorkOrderInfo
import com.clockworkorange.domain.usecase.workorder.WorkOrderStatus
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WorkOrderLogViewModel @Inject constructor(
    private val getWorkOrderUseCase: GetWorkOrderUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val workOrderInfoList: List<WorkOrderInfo> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val deviceIdFlow = MutableStateFlow<Int?>(null)
    private val selectStateFlow =
        MutableStateFlow<List<WorkOrderStatus>>(listOf(WorkOrderStatus.Pending))

    init {
        deviceIdFlow.combine(selectStateFlow) { id, stateList ->
            if (id == null) {
                _uiState.update { it.copy(workOrderInfoList = emptyList()) }
                return@combine
            }

            val dataList = mutableListOf<WorkOrderInfo>()
            stateList.forEach { state ->
                val result = getWorkOrderUseCase.invoke(GetWorkOrderParam(id, state))
                result.data?.let { dataList.addAll(it) }
            }
            _uiState.update { it.copy(workOrderInfoList = dataList) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, Unit)
    }

    fun setDeviceId(deviceId: Int) {
        deviceIdFlow.tryEmit(deviceId)
    }

    fun selectState(vararg states: WorkOrderStatus) {
        selectStateFlow.tryEmit(states.toList())
    }

    fun refresh() {
        val deviceId = deviceIdFlow.value
        deviceIdFlow.tryEmit(null)
        deviceIdFlow.tryEmit(deviceId)
    }

}