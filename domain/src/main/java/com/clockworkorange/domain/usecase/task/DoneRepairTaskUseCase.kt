package com.clockworkorange.domain.usecase.task

import android.net.Uri
import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File
import javax.inject.Inject

class DoneRepairTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<DoneRepairTaskUseCaseParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: DoneRepairTaskUseCaseParam): Boolean {
        return taskRepository.doneRepairTask(parameters)
    }
}

data class DoneRepairTaskUseCaseParam(
    val doneTaskType: DoneTaskType,
    val taskId: Int,
    val errorCodeOptions: List<FormOption>,
    val errorCodeOtherMessage: String,
    val repairContentOptions: List<FormOption>,
    val repairContentOtherMessage: String,
    val changePartOptions: List<FormOption>,
    val photoNewParts: Uri,
    val photoOldParts: Uri,
    val isWork: Boolean,
    val fee: String,
    val customerSignature: File,
    val reportErrorReason: String = ""
)