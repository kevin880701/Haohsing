package com.clockworkorange.haohsing.ui.main.engineer.main

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.usecase.task.*
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class EngineerMainViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getMonthTaskSummaryListUseCase: GetMonthTaskSummaryListUseCase,
    private val getTaskDetailUseCase: GetTaskDetailUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val userName: String? = null,
        val userImage: String? = null,
        val selectedDay: CalendarDay? = null,
        val monthTaskCount: List<MonthTaskCount> = emptyList(),
        val selectedDayTaskSummaryList: List<TaskSummary> = emptyList(),
        val filter: TaskStatusCategory = TaskStatusCategory.UnFinish
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // when month change event (because date changed from notification page) came from viewModel,
    // we didn't change date to first day of month
    private var selectFirstDayLock: Boolean = false

    private val refreshFlow = flow {
        while (true) {
            emit(Unit)
            delay(30.seconds)
        }
    }

    private val monthTaskSummaryListFlow = _uiState
        .mapNotNull { it.selectedDay?.let { date -> date.year to date.month } }
        .distinctUntilChanged()
        .combine( refreshFlow ) { day, _-> day }
        .map {
            getMonthTaskSummaryListUseCase.invoke(
                GetMonthTaskSummaryListParam(
                    it.first,
                    it.second
                )
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            launch {
                getUserInfoUseCase.invoke(Unit).collect { result ->
                    result.data?.let { info ->
                        _uiState.update {
                            it.copy(
                                userName = info.name,
                                userImage = info.image
                            )
                        }
                    }
                }
            }

            launch {
                monthTaskSummaryListFlow
                    .mapNotNull { it.data }
                    .collect { data ->
                        Timber.d("taskSummary $data")
                        val monthTaskCountList = data.groupBy { it.dateTime.toLocalDate() }
                            .map { (date, list) ->
                                MonthTaskCount(
                                    CalendarDay.from(date.year, date.monthValue, date.dayOfMonth),
                                    list.count()
                                )
                            }

                        _uiState.update { it.copy(monthTaskCount = monthTaskCountList) }
                    }
            }

            launch {
                monthTaskSummaryListFlow
                    .mapNotNull { it.data }
                    .combine(_uiState.mapNotNull { it.selectedDay }) { data, day ->
                        data.filter {
                            it.dateTime.year == day.year
                                    && it.dateTime.monthValue == day.month
                                    && it.dateTime.dayOfMonth == day.day
                        }
                    }.combine( _uiState.map { it.filter } ) { data, filterStatus ->
                        data.filter { it.statusCategory == filterStatus }
                    }
                    .collect { data ->
                        _uiState.update {
                            it.copy(selectedDayTaskSummaryList = data)
                        }
                    }
            }
        }
    }

    fun selectDay(day: CalendarDay) {
        _uiState.update { it.copy(selectedDay = day) }
    }

    fun selectFirstDayInMonth(day: CalendarDay) {
        if (selectFirstDayLock) selectFirstDayLock = false
        else selectDay(day)
    }

    fun filterTaskStatus(taskStatus: TaskStatusCategory) {
        _uiState.update { it.copy(filter = taskStatus) }
    }

    fun selectDayByTaskId(taskId: Int) {
        viewModelScope.launch {
            getTaskDetailUseCase(taskId).data?.let {
                val taskDay =
                    it.appointedDatetime.run { CalendarDay.from(year, monthValue, dayOfMonth) }
                val currentYearMonth = uiState.value.selectedDay?.run { year to month }
                val newYearMonth = taskDay.run { year to month }
                if (currentYearMonth != newYearMonth) selectFirstDayLock = true // 月分有改變，但不希望日期因為 monthChangedListener 而改
                _uiState.update { state ->
                    state.copy(selectedDay = taskDay, filter = it.status)
                }
            } ?: showToast("找不到此任務")
        }
    }

    data class MonthTaskCount(val day: CalendarDay, val taskCount: Int)
}