package com.clockworkorange.haohsing.ui.main.engineer.install

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.usecase.notification.Duration
import com.clockworkorange.domain.usecase.task.GetInstallRecordUseCase
import com.clockworkorange.domain.usecase.task.InstallRecord
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EngineerInstallRecordViewModel @Inject constructor(
    private val getInstallRecordUseCase: GetInstallRecordUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val duration: Duration = Duration.Recent1Month,
        val searchKeyword: String? = null,
        val installRecord: List<UiInstallRecord> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val recordList =
        _uiState.map { state -> getInstallRecordUseCase.invoke(state.duration) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, Result.Success(emptyList()))

    private val keyword = _uiState.map { it.searchKeyword }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val filterKeywordList = recordList.combine(keyword) { result, keyword ->
        return@combine when (result) {
            is Result.Success -> {
                val data = result.data
                if (data.isEmpty()) {
                    emptyList<InstallRecord>()
                } else {
                    data.filter { it.searchKeyword(keyword) }
                }
            }
            is Result.Error -> emptyList<InstallRecord>()
            Result.Loading -> emptyList<InstallRecord>()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList<InstallRecord>())


    init {
        viewModelScope.launch {
            filterKeywordList.collect { recordList ->
                val dateSet = recordList.map { it.date.toLocalDate() }.toMutableSet()
                val uiRecordList = recordList.sortedBy { it.date }.map {
                    // let first record on the same date show header
                    val date = it.date.toLocalDate()
                    val showDateHeader = dateSet.contains(date)
                    if (showDateHeader) dateSet.remove(date)
                    UiInstallRecord(it, showDateHeader)
                }
                _uiState.update { it.copy(installRecord = uiRecordList) }
            }
        }
    }

    fun selectDuration(duration: Duration) {
        _uiState.update { it.copy(duration = duration) }
    }

    fun search(keyword: String) {
        _uiState.update { it.copy(searchKeyword = keyword) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchKeyword = null) }
    }

}

data class UiInstallRecord(
    val installRecord: InstallRecord,
    val showDateHeader: Boolean
)

fun InstallRecord.searchKeyword(keyword: String?): Boolean {
    if (keyword == null) return true
    return model.contains(keyword) || mac.contains(keyword) || place.contains(keyword)
}