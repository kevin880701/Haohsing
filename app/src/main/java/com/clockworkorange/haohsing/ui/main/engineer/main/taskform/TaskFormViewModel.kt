package com.clockworkorange.haohsing.ui.main.engineer.main.taskform

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.task.*
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TaskFormViewModel @Inject constructor(
    private val getMaintainOptionsUseCase: GetMaintainOptionsUseCase,
    private val getRepairOptionsUseCase: GetRepairOptionsUseCase,
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val doneMaintainTaskUseCase: DoneMaintainTaskUseCase,
    private val doneRepairTaskUseCase: DoneRepairTaskUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val requirement: WorkOrderRequirement? = null,
        val changeFiltersOptions: List<UiFormOption> = emptyList(),
        val basicMaintainOptions: List<UiFormOption> = emptyList(),
        val errorCodeOptions: List<UiFormOption> = emptyList(),
        val repairContentOptions: List<UiFormOption> = emptyList(),
        val controlOptions: List<UiFormOption> = emptyList(),
        val heatOptions: List<UiFormOption> = emptyList(),
        val coolOptions: List<UiFormOption> = emptyList(),
        val pipelineOptions: List<UiFormOption> = emptyList(),
        val roOptions: List<UiFormOption> = emptyList(),
        val tds: String? = null,
        val checkTDS: String? = null,
        val photoOldFilter: Uri? = null,
        val photoNewFilter: Uri? = null,
        val photoNewParts: Uri? = null,
        val photoOldParts: Uri? = null,
        val isRepair: Boolean? = null,
        val fee: String? = null,
        val customerSignature: File? = null,
        val engineerId: Int? = null,
        val isDoneTask: Boolean = false,
        val changedFilter: List<ChangedFilter> = listOf()
    )

    data class UiFormOption(val option: FormOption, val isSelect: Boolean = false)

    private fun FormOption.toUiFormOption() = UiFormOption(this)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var taskId: Int = 0
    private var deviceId: Int = 0

    init {
        viewModelScope.launch {
            fetchMaintainOptions()
            fetchRepairOptions()
        }
    }

    private suspend fun fetchMaintainOptions() {
        getMaintainOptionsUseCase.invoke(Unit).data?.let { options ->
            _uiState.update {
                it.copy(
                    changeFiltersOptions = options.changeFilters.map { it.toUiFormOption() },
                    basicMaintainOptions = options.basicMaintain.map { it.toUiFormOption() }
                )
            }
        }
    }

    private suspend fun fetchRepairOptions() {
        getRepairOptionsUseCase.invoke(Unit).data?.let { options ->
            _uiState.update {
                it.copy(
                    errorCodeOptions = options.errorCodeOptions.map { it.toUiFormOption() },
                    repairContentOptions = options.repairContentOptions.map { it.toUiFormOption() },
                    controlOptions = options.changePartOptions.control.map { it.toUiFormOption() },
                    heatOptions = options.changePartOptions.heat.map { it.toUiFormOption() },
                    coolOptions = options.changePartOptions.cool.map { it.toUiFormOption() },
                    pipelineOptions = options.changePartOptions.pipeline.map { it.toUiFormOption() },
                    roOptions = options.changePartOptions.ro.map { it.toUiFormOption() },
                )
            }
        }
    }

    fun setTaskId(taskId: Int) {
        this.taskId = taskId
        viewModelScope.launch {
            getTaskDetailUseCase.invoke(taskId).data?.let { detail ->
                deviceId = detail.deviceId
                _uiState.update {
                    it.copy(
                        requirement = detail.requirement,
                        engineerId = detail.engineerInfo?.id
                    )
                }
            }
        }
    }

    fun setTDS(tds: String) {
        _uiState.update { it.copy(tds = tds) }
    }

    fun setCheckTDS(tds: String) {
        _uiState.update { it.copy(checkTDS = tds) }
    }

    fun setNewFilterPhotos(fileUri: Uri) {
        _uiState.update { it.copy(photoNewFilter = fileUri) }
    }

    fun removeNewFilterPhotos() {
        _uiState.update { it.copy(photoNewFilter = null) }
    }

    fun setOldFilterPhotos(fileUri: Uri) {
        _uiState.update { it.copy(photoOldFilter = fileUri) }
    }

    fun removeOldFilterPhotos() {
        _uiState.update { it.copy(photoOldFilter = null) }
    }

    fun setNewPartsPhotos(fileUri: Uri) {
        _uiState.update { it.copy(photoNewParts = fileUri) }
    }

    fun removeNewPartsPhotos() {
        _uiState.update { it.copy(photoNewParts = null) }
    }

    fun setOldPartsPhotos(fileUri: Uri) {
        _uiState.update { it.copy(photoOldParts = fileUri) }
    }

    fun removeOldPartsPhotos() {
        _uiState.update { it.copy(photoOldParts = null) }
    }

    fun setIsRepair(isRepair: Boolean) {
        _uiState.update { it.copy(isRepair = isRepair) }
    }

    fun setFee(fee: String) {
        _uiState.update { it.copy(fee = fee) }
    }

    fun setCustomerSignature(signature: File) {
        _uiState.update { it.copy(customerSignature = signature) }
    }

    fun doneMaintainTask(doneTaskType: DoneTaskType, reportErrorReason: String = "") {
        viewModelScope.launch {
            val (tds, checkTds) = try {
                checkMaintainTaskFieldIsFill()
            } catch (e: Exception) {
                return@launch
            }

            val (oldFilterPhotoUri, newFilterPhotoUri) = try {
                checkIsTakeFilterPhoto()
            } catch (e: Exception) {
                return@launch
            }

            val (isWork, fee, customerSignatureFile) = try {
                checkConfirmFieldIsFill()
            } catch (e: Exception) {
                return@launch
            }

            val filterOption =
                uiState.value.changeFiltersOptions.filter { it.isSelect }.map { it.option }
            val basicMaintainOptions =
                uiState.value.basicMaintainOptions.filter { it.isSelect }.map { it.option }

            val changedFilter = mutableListOf<ChangedFilter>()

            filterOption.forEach {
                when (it.code) {
                    "BB1" -> {
                        uiState.value.changedFilter.firstOrNull { it.type == ChangedFilter.FilterType.F1 }
                            ?.let {
                                changedFilter.add(it)
                            }
                    }
                    "BB2" -> {
                        uiState.value.changedFilter.firstOrNull { it.type == ChangedFilter.FilterType.F2 }
                            ?.let {
                                changedFilter.add(it)
                            }
                    }
                    "BB3" -> {
                        uiState.value.changedFilter.firstOrNull { it.type == ChangedFilter.FilterType.F3 }
                            ?.let {
                                changedFilter.add(it)
                            }
                    }
                    "BB4" -> {
                        uiState.value.changedFilter.firstOrNull { it.type == ChangedFilter.FilterType.F4 }
                            ?.let {
                                changedFilter.add(it)
                            }
                    }
                    "BB5" -> {
                        uiState.value.changedFilter.firstOrNull { it.type == ChangedFilter.FilterType.F5 }
                            ?.let {
                                changedFilter.add(it)
                            }
                    }
                }
            }

            val param = DoneMaintainTaskUseCaseParam(
                doneTaskType,
                taskId,
                deviceId,
                tds,
                checkTds,
                filterOption,
                basicMaintainOptions,
                oldFilterPhotoUri,
                newFilterPhotoUri,
                isWork,
                fee,
                customerSignatureFile,
                reportErrorReason,
                changedFilter
            )

            _uiState.update { it.copy(isLoading = true) }

            when (val result = doneMaintainTaskUseCase.invoke(param)) {
                is Result.Success -> {
                    showToast("儲存成功")
                    _uiState.update { it.copy(isLoading = false, isDoneTask = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    showToast("儲存失敗:${result.exception.message}")
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }

        }
    }


    fun doneRepairTask(
        errorCodeOtherMessage: String,
        repairContentOtherMessage: String,
        doneTaskType: DoneTaskType,
        reportErrorReason: String = ""
    ) {
        viewModelScope.launch {
            val (oldPartsPhotoUri, newPartsPhotoUri) = try {
                checkIsTakePartsPhoto()
            } catch (e: Exception) {
                return@launch
            }

            val (isWork, fee, customerSignatureFile) = try {
                checkConfirmFieldIsFill()
            } catch (e: Exception) {
                return@launch
            }

            val errorCodeOption =
                uiState.value.errorCodeOptions.filter { it.isSelect }.map { it.option }
            val repairContentOption =
                uiState.value.repairContentOptions.filter { it.isSelect }.map { it.option }
            val changePartOption =
                uiState.value.changeFiltersOptions.filter { it.isSelect }.map { it.option }

            val param = DoneRepairTaskUseCaseParam(
                doneTaskType,
                taskId,
                errorCodeOption,
                errorCodeOtherMessage,
                repairContentOption,
                repairContentOtherMessage,
                changePartOption,
                newPartsPhotoUri,
                oldPartsPhotoUri,
                isWork,
                fee,
                customerSignatureFile,
                reportErrorReason
            )

            _uiState.update { it.copy(isLoading = true) }

            when (val result = doneRepairTaskUseCase.invoke(param)) {
                is Result.Success -> {
                    showToast("儲存成功")
                    _uiState.update { it.copy(isLoading = false, isDoneTask = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    showToast("儲存失敗:${result.exception.message}")
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }

        }

    }


    private fun checkMaintainTaskFieldIsFill(): Pair<String, String> {
        val tds = uiState.value.tds ?: ""
        tds.ifEmpty {
            showToast("未填寫TDS")
            throw IllegalArgumentException()
        }

        val checkTds = uiState.value.checkTDS ?: ""
        checkTds.ifEmpty {
            showToast("未填寫驗水")
            throw IllegalArgumentException()
        }

        return Pair(tds, checkTds)
    }

    private fun checkIsTakeFilterPhoto(): Pair<Uri, Uri> {
        val photoOldFilterUri = uiState.value.photoOldFilter
        if (photoOldFilterUri == null) {
            showToast("尚未選擇舊濾芯照片")
            throw IllegalArgumentException()
        }

        val photoNewFilterUri = uiState.value.photoNewFilter
        if (photoNewFilterUri == null) {
            showToast("尚未選擇新濾芯照片")
            throw IllegalArgumentException()
        }
        return Pair(photoOldFilterUri, photoNewFilterUri)
    }

    private fun checkIsTakePartsPhoto(): Pair<Uri, Uri> {
        val photoOldPartsUri = uiState.value.photoOldParts
        if (photoOldPartsUri == null) {
            showToast("尚未選擇舊零件照片")
            throw IllegalArgumentException()
        }

        val photoNewPartsUri = uiState.value.photoNewParts
        if (photoNewPartsUri == null) {
            showToast("尚未選擇新零件照片")
            throw IllegalArgumentException()
        }
        return Pair(photoOldPartsUri, photoNewPartsUri)
    }

    private fun checkConfirmFieldIsFill(): Triple<Boolean, String, File> {
        val isWork = uiState.value.isRepair
        if (isWork == null) {
            showToast("未填寫是否執行維修")
            throw IllegalArgumentException()
        }

        val fee = uiState.value.fee ?: ""
        fee.ifEmpty {
            showToast("未填寫費用")
            throw IllegalArgumentException()
        }

        val customerSignatureFile = uiState.value.customerSignature
        if (customerSignatureFile == null) {
            showToast("客戶未簽名")
            throw IllegalArgumentException()
        }

        return Triple(isWork, fee, customerSignatureFile)
    }

    fun addChangedFilter(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter = it.changedFilter + filter) }
    }

    fun clickFilterOption(option: UiFormOption) {
        val current = uiState.value.changeFiltersOptions.toMutableList()
        current.replaceAll {
            if (it.option.code == option.option.code) {
                option.copy(isSelect = !option.isSelect)
            } else {
                it
            }
        }

        _uiState.update { it.copy(changeFiltersOptions = current) }
    }

    fun clickBasicMaintainOptions(option: UiFormOption) {
        val current = uiState.value.basicMaintainOptions.toMutableList()
        current.replaceAll {
            if (it.option.code == option.option.code) {
                option.copy(isSelect = !option.isSelect)
            } else {
                it
            }
        }

        _uiState.update { it.copy(basicMaintainOptions = current) }
    }

    fun clickErrorCodeOptions(option: UiFormOption) {
        val current = uiState.value.errorCodeOptions.toMutableList()
        current.replaceAll {
            if (it.option.code == option.option.code) {
                option.copy(isSelect = !option.isSelect)
            } else {
                it
            }
        }

        _uiState.update { it.copy(errorCodeOptions = current) }
    }

    fun clickRepairContentOptions(option: UiFormOption) {
        val current = uiState.value.repairContentOptions.toMutableList()
        current.replaceAll {
            if (it.option.code == option.option.code) {
                option.copy(isSelect = !option.isSelect)
            } else {
                it
            }
        }

        _uiState.update { it.copy(repairContentOptions = current) }
    }

    fun clickControlOptions(option: UiFormOption) {
        val current = uiState.value.controlOptions.toMutableList()
        current.replaceAll {
            if (it.option.code == option.option.code) {
                option.copy(isSelect = !option.isSelect)
            } else {
                it
            }
        }

        _uiState.update { it.copy(controlOptions = current) }
    }

    fun clickHeatOptions(option: UiFormOption) {
        val current = uiState.value.heatOptions.toMutableList()
        current.replaceAll {
            if (it.option.code == option.option.code) {
                option.copy(isSelect = !option.isSelect)
            } else {
                it
            }
        }

        _uiState.update { it.copy(heatOptions = current) }
    }


    fun clickCoolOptions(option: UiFormOption) {
        val current = uiState.value.coolOptions.toMutableList()
        current.replaceAll {
            if (it.option.code == option.option.code) {
                option.copy(isSelect = !option.isSelect)
            } else {
                it
            }
        }

        _uiState.update { it.copy(coolOptions = current) }
    }


    fun clickPipelineOptions(option: UiFormOption) {
        val current = uiState.value.pipelineOptions.toMutableList()
        current.replaceAll {
            if (it.option.code == option.option.code) {
                option.copy(isSelect = !option.isSelect)
            } else {
                it
            }
        }

        _uiState.update { it.copy(pipelineOptions = current) }
    }


    fun clickRoOptions(option: UiFormOption) {
        val current = uiState.value.roOptions.toMutableList()
        current.replaceAll {
            if (it.option.code == option.option.code) {
                option.copy(isSelect = !option.isSelect)
            } else {
                it
            }
        }

        _uiState.update { it.copy(roOptions = current) }
    }


}
