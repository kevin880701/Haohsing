package com.clockworkorange.haohsing.ui.main.engineer.task

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.notification.Duration
import com.clockworkorange.domain.usecase.task.GetTaskHistoryUseCase
import com.clockworkorange.domain.usecase.task.TaskStatusCategory
import com.clockworkorange.domain.usecase.task.TaskSummary
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EngineerTaskViewModel @Inject constructor(
    private val getTaskHistoryUseCase: GetTaskHistoryUseCase
) : BaseViewModel() {

    private val _filterRequirement = MutableStateFlow<WorkOrderRequirement?>(null)

    data class UiState(
        val isLoading: Boolean = false,
        val duration: Duration = Duration.Recent1Month,
        val searchKeyword: String? = null,
        val errorTaskList: List<UiTaskSummary> = emptyList(),
        val normalTaskList: List<UiTaskSummary> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val taskHistoryList = _uiState.map { it.duration }
        .distinctUntilChanged()
        .map { getTaskHistoryUseCase.invoke(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    private val filterParam = _filterRequirement.combine(uiState.map { it.searchKeyword }
        .distinctUntilChanged()) { requirement, keyword ->
        return@combine requirement to keyword
    }.stateIn(viewModelScope, SharingStarted.Lazily, null to null)


    init {
        viewModelScope.launch {
            taskHistoryList.combine(filterParam) { taskListResult, (requirement, keyword) ->
                when (taskListResult) {
                    is Result.Success -> {
                        processTaskHistory(taskListResult.data, requirement, keyword)
                    }
                    is Result.Error -> _uiState.update { it.copy(isLoading = false) }
                    Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }.collect()
        }
    }

    fun selectDuration(duration: Duration) {
        _uiState.update { it.copy(duration = duration) }
    }

    fun filterRequirement(requirement: WorkOrderRequirement) {
        _filterRequirement.value = requirement
    }

    fun clearFilterRequirement() {
        _filterRequirement.value = null
    }

    fun searchTaskIdOrName(keyword: String) {
        _uiState.update { it.copy(searchKeyword = keyword) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchKeyword = null) }
    }

    private fun processTaskHistory(
        data: List<TaskSummary>,
        requirement: WorkOrderRequirement?,
        keyword: String?
    ) {

        val taskList = data
            .filter {
                return@filter requirement?.let { filterRequirement -> it.requirement == filterRequirement }
                    ?: true
            }
            .filter {
                return@filter keyword?.let { keyword ->
                    it.taskId.toString().contains(keyword) || it.name.contains(
                        keyword
                    )
                } ?: true
            }

        val errorTaskList = taskList.filterNot { it.statusCategory == TaskStatusCategory.Finish }
        val errorTaskDateSet = errorTaskList.map { it.dateTime.toLocalDate() }.toMutableSet()
        val uiErrorTaskList = errorTaskList.sortedBy { it.dateTime }.map {
            val taskDate = it.dateTime.toLocalDate()
            val showDateHeader = errorTaskDateSet.contains(taskDate)
            if (showDateHeader) errorTaskDateSet.remove(taskDate)
            UiTaskSummary(it, showDateHeader)
        }

        val normalTaskList = taskList.filter { it.statusCategory == TaskStatusCategory.Finish }
        val normalTaskDateSet = normalTaskList.map { it.dateTime.toLocalDate() }.toMutableSet()
        val uiNormalTaskList = normalTaskList.sortedBy { it.dateTime }.map {
            val taskDate = it.dateTime.toLocalDate()
            val showDateHeader = normalTaskDateSet.contains(taskDate)
            if (showDateHeader) normalTaskDateSet.remove(taskDate)
            UiTaskSummary(it, showDateHeader)
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                errorTaskList = uiErrorTaskList,
                normalTaskList = uiNormalTaskList
            )
        }
    }
}

data class UiTaskSummary(
    val taskSummary: TaskSummary,
    val showDateHeader: Boolean
)