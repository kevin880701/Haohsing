package com.clockworkorange.haohsing.ui.main.user.placemanagement.area

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.entity.AreaInfo
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
class AreaManagementViewModel @Inject constructor(
    private val getAreaListUseCase: GetAreaListUseCase,
    private val addAreaUseCase: AddAreaUseCase,
    private val editAreaNameUseCase: EditAreaNameUseCase,
    private val deleteAreaUseCase: DeleteAreaUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val areaList: List<AreaInfo> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var placeId: Int? = null

    init {
        viewModelScope.launch {

        }
    }

    fun setPlaceId(placeId: Int) {
        this.placeId = placeId
        viewModelScope.launch { fetchAreaList() }

    }

    private suspend fun fetchAreaList() {
        val placeId = placeId ?: return
        when (val result = getAreaListUseCase.invoke(placeId)) {
            is Result.Success -> {
                _uiState.update { it.copy(areaList = result.data) }
            }
            is Result.Error -> {
                showToast("無法取得區域列表")
            }
        }
    }

    fun addArea(name: String, successCallback: () -> Unit) {
        viewModelScope.launch {
            val placeId = placeId ?: return@launch
            when (addAreaUseCase.invoke(AddAreaParam(placeId, name))) {
                is Result.Success -> {
                    fetchAreaList()
                    successCallback.invoke()
                }
                is Result.Error -> {
                    showToast("新增區域失敗")
                }
            }
        }
    }

    fun editAreaName(areaInfo: AreaInfo, newName: String) {
        viewModelScope.launch {
            when (editAreaNameUseCase.invoke(EditAreaNameParam(areaInfo.id, newName))) {
                is Result.Success -> {
                    fetchAreaList()
                }
                is Result.Error -> {
                    showToast("編輯區域名稱失敗")
                }
            }
        }
    }

    fun deleteArea(areaId: Int) {
        viewModelScope.launch {
            when (deleteAreaUseCase.invoke(areaId)) {
                is Result.Success -> {
                    fetchAreaList()
                    showToast("刪除區域成功")
                }
                is Result.Error -> {
                    showToast("刪除區域失敗")
                }
                Result.Loading -> {}
            }
        }
    }

}