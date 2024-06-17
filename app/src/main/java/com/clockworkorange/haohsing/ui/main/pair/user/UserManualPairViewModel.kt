package com.clockworkorange.haohsing.ui.main.pair.user

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.domain.usecase.device.AddDeviceManualUseCaseParam
import com.clockworkorange.domain.usecase.device.AddDeviceManuallyUseCase
import com.clockworkorange.domain.usecase.palcearea.*
import com.clockworkorange.domain.usecase.task.ChangedFilter
import com.clockworkorange.domain.usecase.vendor.Vendor
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import com.clockworkorange.haohsing.ui.dialog.AreaDialogType
import com.clockworkorange.haohsing.ui.dialog.PlaceDialogType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManualPairViewModel @Inject constructor(
    private val getPlaceListUseCase: GetPlaceListUseCase,
    private val addPlaceUseCase: AddPlaceUseCase,
    private val getAreaListUseCase: GetAreaListUseCase,
    private val addAreaUseCase: AddAreaUseCase,
    private val addDeviceManuallyUseCase: AddDeviceManuallyUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val brand: String? = null,
        val model: String? = null,
        val deviceName: String? = null,
        val place: PlaceInfo? = null,
        val area: AreaInfo? = null,
        val vendor: Vendor? = null,
        val extraFilterCount: Int = 0,
        val changedFilter1: ChangedFilter? = null,
        val changedFilter2: ChangedFilter? = null,
        val changedFilter3: ChangedFilter? = null,
        val changedFilter4: ChangedFilter? = null,
        val changedFilter5: ChangedFilter? = null,
        val placeDialogType: PlaceDialogType? = null,
        val areaDialogType: AreaDialogType? = null,
        val isAddDeviceSuccess: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setBrand(name: String) {
        _uiState.update { it.copy(brand = name) }
    }

    fun setModel(name: String) {
        _uiState.update { it.copy(model = name) }
    }

    fun setDeviceName(name: String) {
        _uiState.update { it.copy(deviceName = name) }
    }

    fun clickEditArea() {
        viewModelScope.launch {
            val selectedPlace = _uiState.value.place
            if (selectedPlace == null) {
                showToast("尚未選擇單位")
                return@launch
            }
            val placeId = selectedPlace.id
            val areaList = getAreaListUseCase.invoke(placeId).data
            if (areaList?.isEmpty() == true) {
                _uiState.update { it.copy(areaDialogType = AreaDialogType.Add) }
            } else {
                _uiState.update { it.copy(areaDialogType = AreaDialogType.Select(areaList!!)) }
            }
        }
    }

    fun selectVendor(vendor: Vendor) {
        _uiState.update { it.copy(vendor = vendor) }
    }

    fun selectArea(selectedArea: AreaInfo) {
        _uiState.update { it.copy(areaDialogType = null, area = selectedArea) }
    }

    fun addArea(name: String) {
        viewModelScope.launch {
            val selectedPlace = _uiState.value.place
            if (selectedPlace == null) {
                showToast("尚未選擇單位")
                return@launch
            }
            val placeId = selectedPlace.id

            when (val result = addAreaUseCase.invoke(AddAreaParam(placeId, name))) {
                is Result.Success -> {
                    val id = result.data
                    val newAreaInfo =
                        getAreaListUseCase.invoke(placeId).data?.firstOrNull { it.id == id }
                    _uiState.update { it.copy(areaDialogType = null, area = newAreaInfo) }
                }
                is Result.Error -> {
                    showToast("新增區域失敗")
                    _uiState.update { it.copy(areaDialogType = null) }
                }
                is Result.Loading -> {}
            }

        }
    }

    fun clickEditPlace() {
        viewModelScope.launch {
            val placeList = getPlaceListUseCase.invoke(Unit).first().data
            if (placeList?.isEmpty() == true) {
                _uiState.update { it.copy(placeDialogType = PlaceDialogType.Add) }
            } else {
                _uiState.update { it.copy(placeDialogType = PlaceDialogType.Select(placeList!!)) }
            }
        }
    }

    fun addPlace(name: String) {
        viewModelScope.launch {
            when (val result = addPlaceUseCase.invoke(name)) {
                is Result.Success -> {
                    val id = result.data
                    val newPlaceInfo =
                        getPlaceListUseCase.invoke(Unit).first().data?.firstOrNull { it.id == id }
                    _uiState.update { it.copy(placeDialogType = null, place = newPlaceInfo) }
                }
                is Result.Error -> {
                    showToast("新增單位失敗")
                    _uiState.update { it.copy(placeDialogType = null) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun selectPlace(selectedPlace: PlaceInfo) {
        _uiState.update { it.copy(placeDialogType = null, place = selectedPlace) }
    }

    fun addExtraFilter() {
        val newCount = uiState.value.extraFilterCount + 1
        if (newCount >= 5) return
        _uiState.update { it.copy(extraFilterCount = newCount) }
    }

    fun setChangedFilter1(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter1 = filter) }
    }

    fun setChangedFilter2(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter2 = filter) }
    }

    fun setChangedFilter3(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter3 = filter) }
    }

    fun setChangedFilter4(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter4 = filter) }
    }

    fun setChangedFilter5(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter5 = filter) }
    }

    fun addDeviceManually() {
        if(!isFillEnough()) return
        _uiState.update { it.copy(isLoading = true) }


        val param = with(uiState.value) {
             AddDeviceManualUseCaseParam(
                area?.id,
                listOfNotNull(changedFilter1, changedFilter2, changedFilter3, changedFilter4, changedFilter5),
                 brand!!,
                 model!!,
                 deviceName!!,
                 place?.id,
                 vendor!!.id
             )
        }

        viewModelScope.launch {
            when (addDeviceManuallyUseCase.invoke(param)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isAddDeviceSuccess = true) }
                }
                is Result.Error -> {
                    showToast("新增失敗，請再試一次")
                    _uiState.update { it.copy(isLoading = false) }
                }
                Result.Loading -> {}
            }
        }
    }

    private fun isFillEnough(): Boolean {
        if (uiState.value.brand == null) {
            showToast("尚未填寫品牌")
            return false
        }

        if (uiState.value.model == null) {
            showToast("尚未填寫型號")
            return false
        }

        val deviceName = uiState.value.deviceName
        if (deviceName == null) {
            showToast("尚未填寫名稱")
            return false
        }

        val vendor = uiState.value.vendor
        if (vendor == null) {
            showToast("尚未填寫服務經銷商")
            return false
        }
        return true
    }

}