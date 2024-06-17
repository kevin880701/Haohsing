package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddDeviceShareMemberUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<AddDeviceShareMemberParam, Boolean>(dispatcher){

    override suspend fun execute(parameters: AddDeviceShareMemberParam): Boolean {
        return deviceRepository.addDeviceShareMember(parameters.deviceId, parameters.userMail)
    }
}

data class AddDeviceShareMemberParam(
    val deviceId: Int,
    val userMail: String
)

class UserNotExistException: Exception()