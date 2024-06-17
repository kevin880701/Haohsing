package com.clockworkorange.haohsing.ui.main.user.devicedetail.share

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.usecase.device.*
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareMemberViewModel @Inject constructor(
    private val getDeviceShareMemberListUseCase: GetDeviceShareMemberListUseCase,
    private val addDeviceShareMemberUseCase: AddDeviceShareMemberUseCase,
    private val deleteDeviceShareMemberUseCase: DeleteDeviceShareMemberUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val shareMemberList: List<UiShareMember> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var deviceId: Int = 0


    fun setDeviceId(deviceId: Int) {
        this.deviceId = deviceId
        fetchShareMemberList()
    }

    private fun fetchShareMemberList() {
        viewModelScope.launch {
            when (val result = getDeviceShareMemberListUseCase.invoke(deviceId)) {
                is Result.Success -> {
                    val data = result.data.mapIndexed { index, shareMember ->
                        UiShareMember(shareMember, index == result.data.size - 1)
                    }
                    _uiState.update { it.copy(shareMemberList = data) }
                }
                is Result.Error -> {
                    showToast("取得分享成員清單失敗")
                }
            }
        }
    }

    fun addShareMember(mail: String) {
        viewModelScope.launch {
            when (val result =
                addDeviceShareMemberUseCase.invoke(AddDeviceShareMemberParam(deviceId, mail))) {
                is Result.Success -> {
                    showToast("邀請成功")
                    fetchShareMemberList()
                }
                is Result.Error -> {
                    if (result.exception is UserNotExistException) {
                        showToast("邀請失敗，使用者不存在")
                    } else {
                        showToast("邀請失敗")
                    }
                }
            }
        }
    }

    fun deleteMember(member: ShareMember) {
        viewModelScope.launch {
            when (deleteDeviceShareMemberUseCase.invoke(
                DeleteDeviceShareMemberParam(
                    deviceId,
                    member.userId
                )
            )) {
                is Result.Success -> {
                    showToast("刪除成功")
                    fetchShareMemberList()
                }
                is Result.Error -> {
                    showToast("刪除失敗")
                }
            }
        }
    }

}

data class UiShareMember(
    val shareMember: ShareMember,
    val isLastItem: Boolean
)