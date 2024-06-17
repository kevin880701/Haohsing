package com.clockworkorange.domain.usecase.task

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.TaskRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.notification.Duration
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDateTime
import javax.inject.Inject

class GetInstallRecordUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Duration, List<InstallRecord>>(dispatcher) {

    override suspend fun execute(parameters: Duration): List<InstallRecord> {
        return taskRepository.getInstallRecordList(parameters)
    }
}

data class InstallRecord(
    val model: String,
    val mac: String,
    val place: String,
    val date: LocalDateTime
)