package com.clockworkorange.domain.usecase.notification

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.data.NotificationRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import javax.inject.Inject

class AcceptOrRejectInviteUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<AcceptOrRejectInviteParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: AcceptOrRejectInviteParam): Boolean {
        return notificationRepository.replyInvite(parameters.inviteId, parameters.isAccept).also {
            if (parameters.isAccept){
                delay(500)
                deviceRepository.refreshDevices()
            }
        }
    }
}

data class AcceptOrRejectInviteParam(
    val inviteId: Int,
    val isAccept: Boolean
)