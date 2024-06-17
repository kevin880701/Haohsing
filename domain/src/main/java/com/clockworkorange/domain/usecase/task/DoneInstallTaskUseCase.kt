package com.clockworkorange.domain.usecase.task

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File
import javax.inject.Inject

class DoneInstallTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<DoneInstallTaskUseCaseParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: DoneInstallTaskUseCaseParam): Boolean {
        return taskRepository.doneInstallTask(parameters)
    }
}

data class DoneInstallTaskUseCaseParam(
    val doneTaskType: DoneTaskType,
    val taskId: Int,
    val fee: String,
    val customerSignature: File,
    val reportErrorReason: String = ""
)