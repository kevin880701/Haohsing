package com.clockworkorange.domain.usecase.task

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.WorkOrderRequirement
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.random.Random

class GetMonthTaskSummaryListUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<GetMonthTaskSummaryListParam, List<TaskSummary>>(dispatcher) {

    override suspend fun execute(parameters: GetMonthTaskSummaryListParam): List<TaskSummary> {
        return taskRepository.getMonthTaskSummaryList(parameters.year, parameters.month)
    }
}

data class GetMonthTaskSummaryListParam(val year: Int, val month: Int)

data class TaskSummary(
    val taskId: Int,
    val dateTime: LocalDateTime,
    val name: String,
    val address: String,
    val requirement: WorkOrderRequirement,
    val statusCategory: TaskStatusCategory,
    val isStatusErrorNeedResend: Boolean = false,
    val status: TaskStatus
)


enum class TaskStatusCategory{
    UnFinish, Finish, Error;
    companion object{

        fun fromStatus(status: TaskStatus): TaskStatusCategory {
            return when(status) {
                TaskStatus.Temp, TaskStatus.Pending, TaskStatus.Scheduled -> UnFinish
                TaskStatus.Finished, TaskStatus.Cancel -> Finish
                TaskStatus.Error -> Error
            }
        }

        fun fromStatusId(id: Int): TaskStatusCategory{
            return when(id){
                -1, 0, 1 -> UnFinish
                2, 3 -> Finish
                4 -> Error
                else -> throw IllegalArgumentException("$id not found")
            }
        }
    }
}

enum class TaskStatus {
    Temp, Pending, Scheduled, Finished, Cancel, Error;

    companion object {

        fun getRandom(): TaskStatus = listOf(Temp, Pending, Scheduled, Finished, Cancel, Error).random()

        fun fromId(id: Int): TaskStatus {
            return when(id){
                -1 -> Temp
                0 -> Pending
                1 -> Scheduled
                2 -> Finished
                3 -> Cancel
                4 -> Error
                else -> throw IllegalArgumentException("$id not found")
            }
        }
    }
}