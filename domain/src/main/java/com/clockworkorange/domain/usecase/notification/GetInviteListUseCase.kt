package com.clockworkorange.domain.usecase.notification

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.NotificationRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetInviteListUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, List<Invite>>(dispatcher) {

    override suspend fun execute(parameters: Unit): List<Invite> {
        return notificationRepository.getInviteList()
    }

}


data class Invite(
    val id: Int,
    val senderName: String,
    val placeName: String?
)