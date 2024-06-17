package com.clockworkorange.haohsing.ui.main.user.devicedetail.placearea

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.DeviceDetail
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.domain.usecase.device.*
import com.clockworkorange.domain.usecase.palcearea.*
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceAreaViewModel @Inject constructor(
    private val getDeviceDetailUseCase: GetDeviceDetailUseCase,
    private val addPlaceUseCase: AddPlaceUseCase,
    private val addAreaUseCase: AddAreaUseCase,
    private val updateDevicePlaceUseCase: UpdateDevicePlaceUseCase,
    private val updateDeviceAreaUseCase: UpdateDeviceAreaUseCase,
    private val getPlaceSummaryListUseCase: GetPlaceSummaryListUseCase,
    private val getAreaListUseCase: GetAreaListUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val place: String? = null,
        val area: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var device: DeviceDetail? = null

    fun setDeviceId(deviceId: Int) {
        viewModelScope.launch {
            getDeviceDetailUseCase(deviceId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        this@PlaceAreaViewModel.device = result.data
                        _uiState.update {
                            it.copy(
                                place = result.data.placeInfo.name,
                                area = result.data.areaInfo?.name
                            )
                        }
                    }
                    is Result.Error -> {
                        showToast("無法取得裝置")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun refresh() {
        device?.id?.let {
            setDeviceId(it)
        }
    }

    fun addPlace(name: String) {
        viewModelScope.launch {
            val device = device ?: return@launch
            val addPlaceResult = addPlaceUseCase.invoke(name)
            if (addPlaceResult is Result.Error) {
                showToast("新增單位失敗")
                return@launch
            }

            if (addPlaceResult is Result.Success) {
                val deviceId = device.id
                val newPlaceId = addPlaceResult.data
                val updatePlaceResult =
                    updateDevicePlaceUseCase.invoke(UpdateDevicePlaceParam(deviceId, newPlaceId))
                if (updatePlaceResult is Result.Error) {
                    showToast("更新單位失敗")
                    return@launch
                }
                if (updatePlaceResult is Result.Success) {
                    showToast("更新單位成功")
                    refresh()
                    return@launch
                }
            }
        }
    }

    fun addArea(name: String) {
        viewModelScope.launch {
            val device = device ?: return@launch
            val addAreaResult = addAreaUseCase.invoke(AddAreaParam(device.placeId, name))
            if (addAreaResult is Result.Error) {
                showToast("新增區域失敗")
                return@launch
            }

            if (addAreaResult is Result.Success) {
                val deviceId = device.id
                val newAreaId = addAreaResult.data
                val updateAreaResult = updateDeviceAreaUseCase.invoke(
                    UpdateDeviceAreaParam(
                        deviceId,
                        device.placeId,
                        newAreaId
                    )
                )
                if (updateAreaResult is Result.Error) {
                    showToast("更新區域失敗")
                    return@launch
                }
                if (updateAreaResult is Result.Success) {
                    showToast("更新區域成功")
                    refresh()
                    return@launch
                }
            }
        }
    }

    suspend fun getPlaceList(): List<PlaceInfo> {
        val result = getPlaceSummaryListUseCase.invoke(Unit)
        return if (result is Result.Success) {
            result.data.map { it.toPlaceInfo() }
        } else {
            emptyList()
        }
    }

    suspend fun getAreaList(): List<AreaInfo> {
        val device = device ?: return emptyList()
        val result = getAreaListUseCase.invoke(device.placeId)
        return if (result is Result.Success) {
            result.data
        } else {
            emptyList()
        }
    }

    fun updateDevicePlace(placeInfo: PlaceInfo) {
        viewModelScope.launch {
            val device = device ?: return@launch
            when (val result =
                updateDevicePlaceUseCase.invoke(UpdateDevicePlaceParam(device.id, placeInfo.id))) {
                is Result.Success -> {
                    showToast("更新單位成功")
                    refresh()
                }
                is Result.Error -> {
                    showToast("更新單位失敗")
                }
                else -> {}
            }
        }
    }

    fun updateDeviceArea(areaInfo: AreaInfo) {
        viewModelScope.launch {
            val device = device ?: return@launch
            when (val result = updateDeviceAreaUseCase.invoke(
                UpdateDeviceAreaParam(
                    device.id,
                    device.placeId,
                    areaInfo.id
                )
            )) {
                is Result.Success -> {
                    showToast("更新區域成功")
                    refresh()
                }
                is Result.Error -> {
                    showToast("更新區域失敗")
                }
                else -> {}
            }
        }
    }

}