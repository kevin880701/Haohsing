package com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog.finished

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.data
import com.clockworkorange.domain.usecase.workorder.FinishedWorkOrderDetail
import com.clockworkorange.domain.usecase.workorder.GetFinishedWorkOrderDetailUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkOrderDetailFinishedViewModel @Inject constructor(
    private val getFinishedWorkOrderDetailUseCase: GetFinishedWorkOrderDetailUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val finishedWorkOrderDetail: FinishedWorkOrderDetail? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setId(deviceId: Int, orderId: Int) {
        viewModelScope.launch {
            getFinishedWorkOrderDetailUseCase.invoke(orderId).data?.let { detail ->
                _uiState.update { it.copy(finishedWorkOrderDetail = detail) }
            }
        }
    }

}