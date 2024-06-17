package com.clockworkorange.haohsing.ui.main.user.devicedetail.manufacturer

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.entity.ManufacturerInfo
import com.clockworkorange.domain.usecase.device.GetManufacturerUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ManufacturerInfoViewModel @Inject constructor(
    private val getManufacturerUseCase: GetManufacturerUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val manufacturerInfo: ManufacturerInfo? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    fun setDeviceId(deviceId: Int) {
        viewModelScope.launch {
            getManufacturerUseCase.invoke(deviceId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(manufacturerInfo = result.data) }
                    }
                    is Result.Error -> {
                        showToast("無法取得廠商資訊")
                    }
                }
            }
        }
    }

}