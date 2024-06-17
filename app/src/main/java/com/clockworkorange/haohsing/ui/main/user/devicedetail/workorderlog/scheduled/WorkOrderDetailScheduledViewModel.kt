package com.clockworkorange.haohsing.ui.main.user.devicedetail.workorderlog.scheduled

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.data
import com.clockworkorange.domain.usecase.workorder.GetScheduledWorkOrderDetailUseCase
import com.clockworkorange.domain.usecase.workorder.ScheduledWorkOrderDetail
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkOrderDetailScheduledViewModel @Inject constructor(
    private val getScheduledWorkOrderDetailUseCase: GetScheduledWorkOrderDetailUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val scheduledWorkOrderDetail: ScheduledWorkOrderDetail? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    fun setId(orderId: Int) {
        viewModelScope.launch {
            getScheduledWorkOrderDetailUseCase.invoke(orderId).data?.let { detail ->
                _uiState.update { it.copy(scheduledWorkOrderDetail = detail) }
            }
        }
    }

}