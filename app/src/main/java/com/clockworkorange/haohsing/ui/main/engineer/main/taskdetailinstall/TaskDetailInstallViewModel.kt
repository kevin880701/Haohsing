package com.clockworkorange.haohsing.ui.main.engineer.main.taskdetailinstall

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
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
class TaskDetailInstallViewModel @Inject constructor(
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val doneInstallTaskUseCase: DoneInstallTaskUseCase,
    private val updateWorkTimeUseCase: UpdateWorkTimeUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val taskDetail: TaskDetail? = null,
        val fee: String? = null,
        val customerSignature: File? = null,
        val isDoneTask: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var taskId: Int = 0

    fun setId(taskId: Int) {
        this.taskId = taskId
        viewModelScope.launch {
            getTaskDetailUseCase.invoke(taskId).data?.let { detail ->
                _uiState.update { it.copy(taskDetail = detail) }
            }
        }
    }

    fun setFee(fee: String) {
        _uiState.update { it.copy(fee = fee) }
    }

    fun setCustomerSignature(signature: File) {
        _uiState.update { it.copy(customerSignature = signature) }

    }

    fun doneTask() {
        viewModelScope.launch {

            val (fee, customerSignatureFile) = try {
                checkConfirmFieldIsFill()
            } catch (e: Exception) {
                return@launch
            }

            val param = DoneInstallTaskUseCaseParam(
                DoneTaskType.Finish,
                taskId,
                fee,
                customerSignatureFile
            )

            _uiState.update { it.copy(isLoading = true) }

            updateWorkTimeUseCase.invoke(taskId)

            when (val result = doneInstallTaskUseCase.invoke(param)) {
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

    fun reportErrorTask(reason: String) {
        viewModelScope.launch {

            val (fee, customerSignatureFile) = try {
                checkConfirmFieldIsFill()
            } catch (e: Exception) {
                return@launch
            }

            val param = DoneInstallTaskUseCaseParam(
                DoneTaskType.ReportError,
                taskId,
                fee,
                customerSignatureFile,
                reason
            )

            _uiState.update { it.copy(isLoading = true) }

            updateWorkTimeUseCase.invoke(taskId)

            when (val result = doneInstallTaskUseCase.invoke(param)) {
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

    private fun checkConfirmFieldIsFill(): Pair<String, File> {
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

        return Pair(fee, customerSignatureFile)
    }

}