package com.clockworkorange.haohsing.ui.main.user.setting

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.usecase.device.*
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import com.clockworkorange.haohsing.ui.main.user.devicedetail.requireworkorder.AddEdit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class PowerScheduleViewModel : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val name: String? = null,
        val powerOnTime: String? = null,
        val powerOffTime: String? = null,
        val selectedWeekDay: List<Int> = emptyList(),
        val previewNotificationContent: String? = null,
        val eventPop: Boolean = false
    )

    protected val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    protected var scheduleId: Int = -1
    protected var addEdit: AddEdit? = null

    abstract fun set(scopeId: Int, addedit: AddEdit, scheduleId: Int)

    abstract fun saveSchedule()

    abstract fun deleteSchedule()

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
        checkNotification()
    }

    fun setPowerOnTime(time: String) {
        _uiState.update { it.copy(powerOnTime = time) }
        checkNotification()
    }

    fun setPowerOffTime(time: String) {
        _uiState.update { it.copy(powerOffTime = time) }
        checkNotification()
    }

    protected fun checkNotification() {
        if (isAllFilled()) {
            val content = "在 %s- %s ，已自動幫您執行了「%s 排程」".format(
                uiState.value.powerOnTime,
                uiState.value.powerOffTime,
                uiState.value.name
            )

            _uiState.update { it.copy(previewNotificationContent = content) }
        }
    }

    protected fun isAllFilled(): Boolean {
        return with(uiState.value) {
            name != null && powerOnTime != null && powerOffTime != null && selectedWeekDay.isNotEmpty()
        }
    }

    fun toggleWeekDay(weekDay: Int) {
        val current = _uiState.value.selectedWeekDay.toMutableList()
        if (weekDay in current) {
            current.remove(weekDay)
        } else {
            current.add(weekDay)
        }

        _uiState.update { it.copy(selectedWeekDay = current) }
        checkNotification()
    }

}

@HiltViewModel
class DevicePowerScheduleViewModel @Inject constructor(
    private val deleteDeviceScheduleUseCase: DeleteDeviceScheduleUseCase,
    private val getPowerScheduleListUseCase: GetPowerScheduleListUseCase,
    private val addPowerScheduleUseCase: AddPowerScheduleUseCase,
    private val editPowerScheduleUseCase: EditDeviceScheduleUseCase
) : PowerScheduleViewModel() {

    private var deviceId: Int = -1

    /**
     * @param scopeId : deviceId
     */
    override fun set(scopeId: Int, addedit: AddEdit, scheduleId: Int) {
        this.addEdit = addedit
        this.deviceId = scopeId
        this.scheduleId = scheduleId

        if (addedit == AddEdit.Edit) {
            viewModelScope.launch {
                getPowerScheduleListUseCase.invoke(deviceId).data
                    ?.firstOrNull { it.id == scheduleId }
                    ?.let { schedule ->
                        _uiState.update {
                            it.copy(
                                name = schedule.name,
                                powerOnTime = schedule.powerOnTime,
                                powerOffTime = schedule.sleepTime,
                                selectedWeekDay = schedule.weekDays.toList()
                            )
                        }

                        checkNotification()
                    }
            }
        }
    }

    override fun saveSchedule() {
        if (!isAllFilled()) {
            showToast("尚有欄位未填寫")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (addEdit) {
                AddEdit.Add -> {
                    val addParam = AddPowerScheduleParam(
                        deviceId = deviceId,
                        name = uiState.value.name!!,
                        powerOnTime = uiState.value.powerOnTime!!,
                        sleepTime = uiState.value.powerOffTime!!,
                        weekDays = uiState.value.selectedWeekDay
                    )

                    when (addPowerScheduleUseCase(addParam)) {
                        is Result.Success -> {
                            showToast("新增成功")
                            _uiState.update { it.copy(isLoading = false, eventPop = true) }
                        }
                        is Result.Error -> {
                            showToast("新增失敗，請再試一次")
                            _uiState.update { it.copy(isLoading = false) }
                        }
                        Result.Loading -> {}
                    }

                }
                AddEdit.Edit -> {
                    val editParam = EditPowerScheduleParam(
                        scheduleId,
                        uiState.value.name!!,
                        uiState.value.powerOnTime!!,
                        uiState.value.powerOffTime!!,
                        uiState.value.selectedWeekDay
                    )
                    when (editPowerScheduleUseCase(editParam)) {
                        is Result.Success -> {
                            showToast("編輯成功")
                            _uiState.update { it.copy(isLoading = false, eventPop = true) }
                        }
                        is Result.Error -> {
                            showToast("編輯失敗，請再試一次")
                            _uiState.update { it.copy(isLoading = false) }
                        }
                        Result.Loading -> {}
                    }

                }
                else -> {}
            }
        }
    }

    override fun deleteSchedule() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (deleteDeviceScheduleUseCase.invoke(scheduleId)) {
                is Result.Success -> {
                    showToast("刪除成功")
                    _uiState.update { it.copy(isLoading = false, eventPop = true) }
                }
                is Result.Error -> {
                    showToast("刪除失敗，請再試一次")
                    _uiState.update { it.copy(isLoading = false) }
                }
                else -> {}
            }
        }
    }
}

@HiltViewModel
class AreaPowerScheduleViewModel @Inject constructor(
    private val deleteAreaScheduleUseCase: DeleteAreaScheduleUseCase,
    private val getAreaScheduleUseCase: GetAreaScheduleUseCase,
    private val addAreaScheduleUseCase: AddAreaScheduleUseCase,
    private val editAreaScheduleUseCase: EditAreaScheduleUseCase,
) : PowerScheduleViewModel() {

    private var areaId: Int = -1

    /**
     * @param scopeId areaId
     */
    override fun set(scopeId: Int, addedit: AddEdit, scheduleId: Int) {
        this.addEdit = addedit
        this.areaId = scopeId
        this.scheduleId = scheduleId

        if (addedit == AddEdit.Edit) {
            viewModelScope.launch {
                getAreaScheduleUseCase.invoke(scheduleId).data
                    ?.let { schedule ->
                        _uiState.update {
                            it.copy(
                                name = schedule.name,
                                powerOnTime = schedule.powerOnTime,
                                powerOffTime = schedule.sleepTime,
                                selectedWeekDay = schedule.weekDays.toList()
                            )
                        }

                        checkNotification()
                    }
            }
        }
    }

    override fun saveSchedule() {
        if (!isAllFilled()) {
            showToast("尚有欄位未填寫")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (addEdit) {
                AddEdit.Add -> {
                    val addParam = AddAreaScheduleParam(
                        areaId = areaId,
                        name = uiState.value.name!!,
                        powerOnTime = uiState.value.powerOnTime!!,
                        sleepTime = uiState.value.powerOffTime!!,
                        weekDays = uiState.value.selectedWeekDay
                    )

                    when (addAreaScheduleUseCase(addParam)) {
                        is Result.Success -> {
                            showToast("新增成功")
                            _uiState.update { it.copy(isLoading = false, eventPop = true) }
                        }
                        is Result.Error -> {
                            showToast("新增失敗，請再試一次")
                            _uiState.update { it.copy(isLoading = false) }
                        }
                        Result.Loading -> {}
                    }

                }
                AddEdit.Edit -> {
                    val editParam = EditPowerScheduleParam(
                        scheduleId,
                        uiState.value.name!!,
                        uiState.value.powerOnTime!!,
                        uiState.value.powerOffTime!!,
                        uiState.value.selectedWeekDay
                    )
                    when (editAreaScheduleUseCase(editParam)) {
                        is Result.Success -> {
                            showToast("編輯成功")
                            _uiState.update { it.copy(isLoading = false, eventPop = true) }
                        }
                        is Result.Error -> {
                            showToast("編輯失敗，請再試一次")
                            _uiState.update { it.copy(isLoading = false) }
                        }
                        Result.Loading -> {}
                    }

                }
                else -> {}
            }
        }
    }

    override fun deleteSchedule() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (deleteAreaScheduleUseCase.invoke(scheduleId)) {
                is Result.Success -> {
                    showToast("刪除成功")
                    _uiState.update { it.copy(isLoading = false, eventPop = true) }
                }
                is Result.Error -> {
                    showToast("刪除失敗，請再試一次")
                    _uiState.update { it.copy(isLoading = false) }
                }
                else -> {}
            }
        }
    }

}