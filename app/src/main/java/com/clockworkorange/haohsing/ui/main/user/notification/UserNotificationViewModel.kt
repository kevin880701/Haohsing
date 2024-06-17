package com.clockworkorange.haohsing.ui.main.user.notification

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.usecase.device.GetDevicesUseCase
import com.clockworkorange.domain.usecase.notification.*
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserNotificationViewModel @Inject constructor(
    private val getInviteListUseCase: GetInviteListUseCase,
    private val getNotificationListUseCase: GetNotificationListUseCase,
    private val acceptOrRejectInviteUseCase: AcceptOrRejectInviteUseCase,
    private val markAllNotificationAsReadUseCase: MarkAllNotificationAsReadUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getDevicesUseCase: GetDevicesUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val inviteList: List<UiInvite> = emptyList(),
        val duration: Duration = Duration.Recent1Month,
        val notificationList: List<UiNotification> = emptyList(),
        val role: UserInfo.Role? = null,
        val devicesIds: List<Int> = emptyList()
    )

    data class UiInvite(
        val invite: Invite,
        val rejectAction: () -> Unit,
        val acceptAction: () -> Unit
    )

    data class UiNotification(
        val notification: Notification,
        val showDateHeader: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var duration = Duration.Recent1Month

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            fetchInviteList()
            fetchNotificationList()
            _uiState.update { it.copy(isLoading = false) }
        }

        viewModelScope.launch {
            getUserInfoUseCase.invoke(Unit).collect { result ->
                result.data.let { userInfo ->
                    _uiState.update { it.copy(role = userInfo?.role) }
                }
            }
        }

        viewModelScope.launch {
            getDevicesUseCase.invoke(Unit)
                .map { it.data?.map { it.id } ?: emptyList() }
                .distinctUntilChanged()
                .collect { ids ->
                    _uiState.update { it.copy(devicesIds = ids) }
                }
        }
    }

    private suspend fun fetchInviteList() {
        when (val result = getInviteListUseCase.invoke(Unit)) {
            is Result.Success -> {
                val data = result.data.map {
                    UiInvite(
                        it,
                        rejectAction = { replayInvite(it, false) },
                        acceptAction = { replayInvite(it, true) }
                    )
                }
                _uiState.update { it.copy(inviteList = data) }
            }
            is Result.Error -> {
                showToast("無法取得邀請列表")
            }
        }
    }

    fun setNotificationDuration(duration: Duration) {
        viewModelScope.launch {
            this@UserNotificationViewModel.duration = duration
            _uiState.update { it.copy(isLoading = true) }
            fetchNotificationList()
            _uiState.update { it.copy(duration = this@UserNotificationViewModel.duration) }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun fetchNotificationList() {
        when (val result = getNotificationListUseCase.invoke(duration)) {
            is Result.Success -> {
                val data = mutableListOf<UiNotification>()
                val dateGroup = result.data.groupBy { it.time.toLocalDate() }
                val keyList = dateGroup.keys.toList().sortedByDescending { it }
                keyList.forEachIndexed { index, key ->
                    val sameDayList = dateGroup[key]!!.sortedByDescending { it.time }
                    sameDayList.forEachIndexed { index, notification ->
                        data.add(UiNotification(notification, index == 0))
                    }
                }
                _uiState.update { it.copy(notificationList = data) }
            }
            is Result.Error -> {
                showToast("無法取得通知列表")
            }
        }
    }

    fun markAllNotificationAsRead() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            markAllNotificationAsReadUseCase.invoke(Unit)
            fetchNotificationList()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun replayInvite(invite: Invite, isAccept: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            acceptOrRejectInviteUseCase.invoke(AcceptOrRejectInviteParam(invite.id, isAccept))
            fetchInviteList()
            fetchNotificationList()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

}