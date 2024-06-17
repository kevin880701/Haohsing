package com.clockworkorange.domain.usecase.notification

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.NotificationRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class MarkAllNotificationAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, Boolean>(dispatcher) {
    override suspend fun execute(parameters: Unit): Boolean {
        return notificationRepository.markAllNotificationAsRead()
    }
}