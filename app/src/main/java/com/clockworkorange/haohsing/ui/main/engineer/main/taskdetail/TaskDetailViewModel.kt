package com.clockworkorange.haohsing.ui.main.engineer.main.taskdetail

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.data
import com.clockworkorange.domain.usecase.task.GetTaskDetailUseCase
import com.clockworkorange.domain.usecase.task.TaskDetail
import com.clockworkorange.domain.usecase.task.UpdateWorkTimeUseCase
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
class TaskDetailViewModel @Inject constructor(
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val updateWorkTimeUseCase: UpdateWorkTimeUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val taskDetail: TaskDetail? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

        }
    }

    fun setTaskId(taskId: Int) {
        viewModelScope.launch {
            getTaskDetailUseCase.invoke(taskId).data?.let { taskDetail ->
                _uiState.update { it.copy(taskDetail = taskDetail) }
            }
        }
    }

    fun startWork(taskId: Int) {
        viewModelScope.launch {
            val result = updateWorkTimeUseCase.invoke(taskId).data == true
            Timber.d("startWork:$result")
        }
    }

}