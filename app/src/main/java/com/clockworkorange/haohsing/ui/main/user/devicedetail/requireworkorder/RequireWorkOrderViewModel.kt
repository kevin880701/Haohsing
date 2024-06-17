package com.clockworkorange.haohsing.ui.main.user.devicedetail.requireworkorder

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.ManufacturerInfo
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.device.GetManufacturerUseCase
import com.clockworkorange.domain.usecase.task.FormOption
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.domain.usecase.workorder.CreateWorkOrderParam
import com.clockworkorange.domain.usecase.workorder.CreateWorkOrderUseCase
import com.clockworkorange.domain.usecase.workorder.EditWorkOrderParam
import com.clockworkorange.domain.usecase.workorder.EditWorkOrderUseCase
import com.clockworkorange.domain.usecase.workorder.GetPendingWorkOrderDetailUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequireWorkOrderViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getManufacturerUseCase: GetManufacturerUseCase,
    private val createWorkOrderUseCase: CreateWorkOrderUseCase,
    private val getPendingWorkOrderDetailUseCase: GetPendingWorkOrderDetailUseCase,
    private val editWorkOrderUseCase: EditWorkOrderUseCase
) : BaseViewModel() {


    data class UiState(
        val isLoading: Boolean = false,
        val userName: String? = null,
        val userPhone: String? = null,
        val requirement: WorkOrderRequirement? = null,
        val deliveryTime: String? = null,
        val errorReasonList: HashSet<FormOption> = hashSetOf(),
        val photoVideos: HashSet<Uri> = hashSetOf(),
        val note: String? = null,
        val manufacturerInfo: ManufacturerInfo? = null,
        val isAddWorkOrderSuccess: Boolean = false,
        val isEditWorkOrderSuccess: Boolean = false
    ) {
        fun isFillEnough(): Boolean {
            return userName != null && userPhone != null && requirement != null
                    && deliveryTime != null && errorReasonList.isNotEmpty()
        }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var addEdit: AddEdit? = null
    private var deviceId: Int? = null
    private var workOrderId: Int? = null

    init {
        viewModelScope.launch {
            getUserInfoUseCase.invoke(Unit)
                .filter { it is Result.Success }
                .map { (it as Result.Success).data }
                .collect { userInfo ->
                    _uiState.update {
                        it.copy(
                            userName = userInfo.name,
                            userPhone = userInfo.phone
                        )
                    }
                }
        }

    }

    fun set(addEdit: AddEdit, deviceId: Int, mId: Int) {
        this.addEdit = addEdit
        when (addEdit) {
            AddEdit.Add -> setDeviceId(deviceId)
            AddEdit.Edit -> setEditWorkOrderId(deviceId, mId)
        }
    }

    private fun setDeviceId(deviceId: Int) {
        this.deviceId = deviceId
        viewModelScope.launch {
            fetchManufacturer(deviceId)
        }
    }

    private fun setEditWorkOrderId(deviceId: Int, mId: Int) {
        this.deviceId = deviceId
        this.workOrderId = mId
        viewModelScope.launch {
            fetchManufacturer(deviceId)
            val result = getPendingWorkOrderDetailUseCase.invoke(mId)
            result.data?.let { detail ->
                _uiState.update {
                    it.copy(
                        requirement = detail.requirement,
                        deliveryTime = detail.deliveryTime,
                        errorReasonList = detail.errorReasons.toHashSet(),
                        note = detail.note,
                        photoVideos = detail.photoVideos.map { Uri.parse(it) }.toHashSet()
                    )
                }
            }

        }
    }

    private suspend fun fetchManufacturer(deviceId: Int) {
        getManufacturerUseCase.invoke(deviceId).collect { result ->
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(manufacturerInfo = result.data) }
                }
                is Result.Error -> {
                    showToast("無法取得廠商資訊")
                }
                Result.Loading -> {}
            }
        }
    }

    fun setUserName(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    fun setUserPhone(phone: String) {
        _uiState.update { it.copy(userPhone = phone) }
    }

    fun setRequirement(requirement: WorkOrderRequirement) {
        _uiState.update { it.copy(requirement = requirement) }
    }


    fun setDeliveryTime(deliveryTime: String) {
        _uiState.update { it.copy(deliveryTime = deliveryTime) }
    }

    fun selectFailScenario(selectFailScenario: HashSet<FormOption>) {
        _uiState.update { it.copy(errorReasonList = selectFailScenario) }
    }


    fun addSelectedPhotoVideos(fileUri: Uri) {
        val newSet = _uiState.value.photoVideos
        if (newSet.size >= 6) {
            showToast("最多選擇6個")
            return
        }

        newSet.add(fileUri)
        _uiState.update { it.copy(photoVideos = newSet) }
    }

    fun removeSelectedPhotoVideo(itemUri: Uri) {
        val newSet = mutableSetOf<Uri>()
        newSet.addAll(_uiState.value.photoVideos.filter { it != itemUri })
        _uiState.update { it.copy(photoVideos = newSet.toHashSet()) }
    }

    fun submit(note: String) {
        viewModelScope.launch {
            when (addEdit) {
                AddEdit.Add -> createWorkOrder(note)
                AddEdit.Edit -> editWorkOrder(note)
                else -> {}
            }
        }
    }

    private suspend fun createWorkOrder(note: String) {
        val param = CreateWorkOrderParam(
            deviceId!!,
            uiState.value.userName!!,
            uiState.value.userPhone!!,
            uiState.value.requirement!!,
            uiState.value.deliveryTime!!,
            uiState.value.errorReasonList.toList(),
            uiState.value.photoVideos.map { it.toString() },
            note
        )
        _uiState.update { it.copy(isLoading = true) }

        when (createWorkOrderUseCase.invoke(param)) {
            is Result.Success -> {
                _uiState.update { it.copy(isLoading = false, isAddWorkOrderSuccess = true) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(isLoading = false) }
                showToast("新增報修與保養失敗")
            }
            else -> {}
        }
    }

    private suspend fun editWorkOrder(note: String) {
        val param = EditWorkOrderParam(
            workOrderId!!,
            deviceId!!,
            uiState.value.userName!!,
            uiState.value.userPhone!!,
            uiState.value.requirement!!,
            uiState.value.deliveryTime!!,
            uiState.value.errorReasonList.toList(),
            uiState.value.photoVideos.map { it.toString() },
            note
        )
        _uiState.update { it.copy(isLoading = true) }

        when (editWorkOrderUseCase.invoke(param)) {
            is Result.Success -> {
                _uiState.update { it.copy(isLoading = false, isEditWorkOrderSuccess = true) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(isLoading = false) }
                showToast("編輯報修與保養失敗")
            }
            else -> {}
        }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

}