package com.clockworkorange.haohsing.ui.main.user.main

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.Device
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.domain.usecase.device.GetDevicesUnderPlaceAreaParam
import com.clockworkorange.domain.usecase.device.GetDevicesUnderPlaceAreaUseCase
import com.clockworkorange.domain.usecase.device.GetDevicesUseCase
import com.clockworkorange.domain.usecase.palcearea.GetAreaListUseCase
import com.clockworkorange.domain.usecase.palcearea.GetPlaceListUseCase
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserMainViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPlaceListUseCase: GetPlaceListUseCase,
    private val getAreaListUseCase: GetAreaListUseCase,
    private val getDevicesUnderPlaceAreaUseCase: GetDevicesUnderPlaceAreaUseCase,
    private val getDevicesUseCase: GetDevicesUseCase
) : BaseViewModel() {

    data class UiState(
        val userGreeting: String = "Hello, ",
        val userImage: String = "",
        val placeList: List<PlaceInfo> = emptyList(),
        val areaList: List<AreaInfo> = emptyList(),
        val devices: List<Device> = emptyList(),
        val isSwipeRefreshing: Boolean = false
    )

    private val selectPlaceFlow = MutableStateFlow<PlaceInfo?>(null)
    private val selectAreaFlow = MutableStateFlow<AreaInfo?>(null)
    private val refreshFlow = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val userDevices = getDevicesUseCase.invoke(Unit)
        .map { it.data ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        viewModelScope.launch {
            launch {
                getUserInfoUseCase.invoke(Unit).collect { result ->
                    if (result is Result.Success) {
                        _uiState.update {
                            it.copy(
                                userGreeting = "Hello, ${result.data.name}",
                                userImage = result.data.image
                            )
                        }
                    }
                }
            }

            launch {
                getPlaceListUseCase.invoke(Unit)
                    .filter { it is Result.Success }
                        // 被分享的裝置所在單位由 userDevices 取得
                    .combine(userDevices) { myPlaceListResult, userDevices ->
                        (myPlaceListResult.data ?: emptyList()) to userDevices.map { it.placeInfo }
                    }.collect { (myPlaceInfoList, shareDevicePlace) ->
                        val allPlaces = (myPlaceInfoList + shareDevicePlace)
                            .toHashSet()
                            .toMutableList()
                            .apply {
                                // 把 "所有" 放在最前面
                                remove(PlaceInfo.ALL)
                                add(0, PlaceInfo.ALL)
                            }
                        _uiState.update { it.copy(placeList = allPlaces) }
                    }
            }

            launch {
                selectPlaceFlow
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collect { updateAreaList(it.id) }
            }

            launch {
                selectPlaceFlow
                    .filterNotNull()
                    .distinctUntilChanged()
                    .combine(
                        selectAreaFlow
                            .filterNotNull()
                            .distinctUntilChanged()
                    ) { place, area ->
                        place.id to area.id
                    }
                    .combine(refreshFlow) { placeArea, _ -> placeArea }
                    .flatMapLatest { (placeId, areaId) ->
                        getDevicesUnderPlaceAreaUseCase.invoke(
                            GetDevicesUnderPlaceAreaParam(
                                placeId,
                                areaId
                            )
                        )
                    }.collect {
                        it.data?.let { deviceList ->
                            _uiState.update { it.copy(devices = deviceList, isSwipeRefreshing = false) }
                        }
                    }
            }
        }
    }

    fun swipeRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSwipeRefreshing = true) }
            refreshFlow.emit(Unit)
        }
    }

    fun selectPlace(place: PlaceInfo?) {
        place ?: return
        selectAreaFlow.tryEmit(null)
        selectPlaceFlow.tryEmit(place)
    }

    fun selectArea(area: AreaInfo?) {
        area ?: return
        selectAreaFlow.tryEmit(area)
    }


    private fun updateAreaList(placeId: Int) {
        viewModelScope.launch {
            val result = getAreaListUseCase.invoke(placeId)
            result.data?.let { areaList ->
                _uiState.update { it.copy(areaList = listOf(AreaInfo.ALL) + areaList) }
            }
        }
    }

}