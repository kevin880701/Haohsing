package com.clockworkorange.domain.usecase.notification

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.NotificationRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDateTime
import javax.inject.Inject

class GetNotificationListUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Duration, List<Notification>>(dispatcher) {

    override suspend fun execute(parameters: Duration): List<Notification> {
        return notificationRepository.getNotificationList(parameters).filter { it.type != Notification.Type.UnKnown }
    }
}

enum class Duration(val num: Int){
    Recent1Month(1),
    Recent2Month(2),
    Recent3Month(3)
}

data class Notification(
    val id: Int,
    val time: LocalDateTime,
    val type: Type,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val deviceId: Int? = null,
    val taskId: Int? = null
){
    enum class Type{
        Alert, //告警
        DataAnalysis, //數據分析
        Maintenance, //保養
        TaskDelay,
        UnKnown
    }
}