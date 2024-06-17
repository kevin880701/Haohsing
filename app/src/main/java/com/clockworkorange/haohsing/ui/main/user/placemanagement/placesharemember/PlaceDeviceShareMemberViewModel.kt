package com.clockworkorange.haohsing.ui.main.user.placemanagement.placesharemember

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.usecase.device.UserNotExistException
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
class PlaceDeviceShareMemberViewModel @Inject constructor(
    private val getPlaceDeviceShareMemberListUseCase: GetPlaceDeviceShareMemberListUseCase,
    private val addPlaceDeviceShareMemberUseCase: AddPlaceDeviceShareMemberUseCase,
    private val deletePlaceDeviceShareMemberUseCase: DeletePlaceDeviceShareMemberUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val shareMemberList: List<UiPlaceShareMember> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var placeId: Int = 0

    fun setPlaceId(placeId: Int) {
        this.placeId = placeId
        fetchDeviceShareMemberList()
    }

    private fun fetchDeviceShareMemberList() {
        viewModelScope.launch {
            when (val result = getPlaceDeviceShareMemberListUseCase.invoke(placeId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(shareMemberList = result.data.mapIndexed { index, placeShareMember ->
                            UiPlaceShareMember(
                                placeShareMember,
                                index == result.data.size - 1
                            )
                        })
                    }
                }
                is Result.Error -> {
                    showToast("取得列表失敗")
                }
            }
        }
    }

    fun addPlaceDeviceShareMember(mail: String) {
        viewModelScope.launch {
            when (val result = addPlaceDeviceShareMemberUseCase.invoke(
                AddPlaceDeviceShareMemberParam(
                    placeId,
                    mail
                )
            )) {
                is Result.Success -> {
                    showToast("新增成功")
                    fetchDeviceShareMemberList()
                }
                is Result.Error -> {
                    if (result.exception is UserNotExistException) {
                        showToast("新增失敗，使用者不存在")
                    } else {
                        showToast("新增失敗")
                    }
                }
            }
        }
    }

    fun deletePlaceDeviceMember(member: UiPlaceShareMember) {
        viewModelScope.launch {
            when (val result = deletePlaceDeviceShareMemberUseCase.invoke(
                DeletePlaceDeviceShareMemberParam(placeId, member.placeShareMember.member.userId)
            )) {
                is Result.Success -> {
                    showToast("刪除成功")
                    fetchDeviceShareMemberList()
                }
                is Result.Error -> {
                    showToast("刪除失敗")
                }
            }
        }
    }

}

data class UiPlaceShareMember(
    val placeShareMember: PlaceShareMember,
    val isLast: Boolean
)