package com.clockworkorange.haohsing.ui.main.user.analysis.device

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.usecase.device.*
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceStatisticsViewModel @Inject constructor(
    private val getDeviceStatisticsUseCase: GetDeviceStatisticsUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val deviceStatistics: DeviceStatistics? = null,
        val suggestionsSavingTime: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun getStatistics(deviceId: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        when(val result = getDeviceStatisticsUseCase.invoke(deviceId)) {
            is Result.Success -> {
                _uiState.update { it.copy(isLoading = false, deviceStatistics = result.data) }
            }
            else -> {
                _uiState.update { it.copy(isLoading = false) }
            }
        }

    }
}