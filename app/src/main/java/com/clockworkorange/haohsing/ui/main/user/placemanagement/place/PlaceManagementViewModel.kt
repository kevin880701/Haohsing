package com.clockworkorange.haohsing.ui.main.user.placemanagement.place

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.PlaceInfo
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
class PlaceManagementViewModel @Inject constructor(
    private val getPlaceListUsesCase: GetPlaceSummaryListUseCase,
    private val addPlaceUseCase: AddPlaceUseCase,
    private val editPlaceNameUseCase: EditPlaceNameUseCase,
    private val deletePlaceUseCase: DeletePlaceUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val placeInfoList: List<PlaceSummary> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    private suspend fun fetchPlaceList() {
        getPlaceListUsesCase.invoke(Unit).data?.let { placeList ->
            _uiState.update { it.copy(placeInfoList = placeList) }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            fetchPlaceList()
        }
    }

    fun addNewPlace(successCallback: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (addPlaceUseCase.invoke("新單位")) {
                is Result.Success -> {
                    fetchPlaceList()
                    _uiState.update { it.copy(isLoading = false) }
                    successCallback.invoke()
                }
                is Result.Error -> {
                    showToast("新增單位失敗")
                    _uiState.update { it.copy(isLoading = false) }
                }
            }

        }
    }

    fun editPlaceName(place: PlaceInfo, newName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (editPlaceNameUseCase.invoke(EditPlaceNameParam(place.id, newName))) {
                is Result.Success -> {
                    fetchPlaceList()
                    _uiState.update { it.copy(isLoading = false) }
                }
                is Result.Error -> {
                    showToast("編輯單位名稱失敗")
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun removePlace(place: PlaceInfo) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (deletePlaceUseCase.invoke(place.id)) {
                is Result.Success -> {
                    fetchPlaceList()
                    _uiState.update { it.copy(isLoading = false) }
                }
                is Result.Error -> {
                    showToast("移除單位失敗")
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }


}

