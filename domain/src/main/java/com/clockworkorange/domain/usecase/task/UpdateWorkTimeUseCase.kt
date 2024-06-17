package com.clockworkorange.domain.usecase.task

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateWorkTimeUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Int): Boolean {
        return taskRepository.updateWorkTime(parameters)
    }
}