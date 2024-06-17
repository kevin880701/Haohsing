package com.clockworkorange.haohsing.ui.main.user.placemanagement.placepermission

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.Device
import com.clockworkorange.domain.usecase.palcearea.GetPlaceDeviceListUseCase
import com.clockworkorange.domain.usecase.palcearea.UpdatePlaceDeviceShareMemberParam
import com.clockworkorange.domain.usecase.palcearea.UpdatePlaceDeviceShareMemberUseCase
import com.clockworkorange.domain.usecase.user.GetUserShareDeviceIdListParam
import com.clockworkorange.domain.usecase.user.GetUserShareDeviceIdListUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPlacePermissionManagementViewModel @Inject constructor(
    private val getPlaceDeviceListUseCase: GetPlaceDeviceListUseCase,
    private val getUserShareDeviceListUseCase: GetUserShareDeviceIdListUseCase,
    private val updatePlaceDeviceShareMemberUseCase: UpdatePlaceDeviceShareMemberUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val areaDevices: List<AreaDevice> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var placeId: Int = 0
    private var userId: Int = 0

    fun setPlaceUserId(placeId: Int, userId: Int) {
        this.placeId = placeId
        this.userId = userId
        fetchAreaDeviceList()
    }

    private fun fetchAreaDeviceList() {
        viewModelScope.launch {
            val userShareDeviceIds = getUserShareDeviceListUseCase.invoke(
                GetUserShareDeviceIdListParam(
                    placeId,
                    userId
                )
            ).data ?: return@launch
            val placeDevices = getPlaceDeviceListUseCase.invoke(placeId).data ?: return@launch

            val data = mutableListOf<AreaDevice>()
            val areaGroupDevices = placeDevices.groupBy { it.areaInfo }

            areaGroupDevices
                .forEach { (areaInfo, areaDevice) ->
                    areaInfo ?: return@forEach
                    data.add(
                        AreaDevice(
                            areaInfo,
                            areaDevice.map { UiCheckStatusDevice(it, it.id in userShareDeviceIds) }
                                .sortedBy { it.device.id }
                        )
                    )
                }

            data.sortBy { it.areaInfo.id }
            data.last().isLast = true
            _uiState.update { it.copy(areaDevices = data) }
        }
    }

    fun toggleDevice(item: UiCheckStatusDevice) {
        val areaDevices = uiState.value.areaDevices.toMutableList()
        val target = areaDevices.find { it.areaDevices.contains(item) } ?: return
        areaDevices.remove(target)
        val deviceList = target.areaDevices.toMutableList()
        deviceList.remove(item)
        deviceList.add(item.copy(isCheck = !item.isCheck))
        deviceList.sortBy { it.device.id }
        val newAreaDevice = AreaDevice(
            target.areaInfo,
            deviceList,
            target.isLast
        )
        areaDevices.add(newAreaDevice)
        areaDevices.sortBy { it.areaInfo.id }
        _uiState.update { it.copy(areaDevices = areaDevices) }
    }

    fun saveChange(placeId: Int, userId: Int) {
        viewModelScope.launch {
            val checkedIds = uiState.value.areaDevices.map { it.areaDevices }
                .flatten().filter { it.isCheck }.map { it.device.id }

            val userShareDeviceIds = getUserShareDeviceListUseCase.invoke(
                GetUserShareDeviceIdListParam(
                    placeId,
                    userId
                )
            ).data ?: return@launch
            val placeDevices = getPlaceDeviceListUseCase.invoke(placeId).data ?: return@launch

            val userOriginPermissionIds =
                placeDevices.filter { it.id in userShareDeviceIds }.map { it.id }

            val addShareIds = checkedIds.filterNot { it in userOriginPermissionIds }
            val removeIds = userOriginPermissionIds.filterNot { it in checkedIds }

            val param = UpdatePlaceDeviceShareMemberParam(placeId, userId, addShareIds, removeIds)

            when (updatePlaceDeviceShareMemberUseCase.invoke(param)) {
                is Result.Success -> {
                    showToast("更新權限成功")
                }
                is Result.Error -> {
                    showToast("更新權限失敗")
                }
            }
        }
    }
}

data class UiCheckStatusDevice(
    val device: Device,
    val isCheck: Boolean
)

data class AreaDevice(
    val areaInfo: AreaInfo,
    val areaDevices: List<UiCheckStatusDevice>,
    var isLast: Boolean = false
)

