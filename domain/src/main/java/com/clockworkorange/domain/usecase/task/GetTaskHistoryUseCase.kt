package com.clockworkorange.domain.usecase.task

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.notification.Duration
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetTaskHistoryUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Duration, List<TaskSummary>>(dispatcher) {

    override suspend fun execute(parameters: Duration): List<TaskSummary> {
        return taskRepository.getTaskHistory(parameters)
    }
}