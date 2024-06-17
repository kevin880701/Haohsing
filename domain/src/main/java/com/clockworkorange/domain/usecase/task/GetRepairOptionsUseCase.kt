package com.clockworkorange.domain.usecase.task

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetRepairOptionsUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, RepairOptions>(dispatcher) {

    override suspend fun execute(parameters: Unit): RepairOptions {
        return taskRepository.getRepairOptions()
    }
}

data class RepairOptions(
    val errorCodeOptions: List<FormOption>,
    val repairContentOptions: List<FormOption>,
    val changePartOptions: ChangePartOptions
)

data class ChangePartOptions(
    val control: List<FormOption>,
    val heat: List<FormOption>,
    val cool: List<FormOption>,
    val pipeline: List<FormOption>,
    val ro: List<FormOption>,
)