package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteDeviceShareMemberUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<DeleteDeviceShareMemberParam, Boolean>(dispatcher){

    override suspend fun execute(parameters: DeleteDeviceShareMemberParam): Boolean {
        return deviceRepository.deleteDeviceShareMember(parameters.deviceId, parameters.userId)
    }
}

data class DeleteDeviceShareMemberParam(
    val deviceId: Int,
    val userId: Int
)