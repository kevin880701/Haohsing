package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateDeviceAreaUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<UpdateDeviceAreaParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: UpdateDeviceAreaParam): Boolean {
        val updateAreaResult = deviceRepository.updateDeviceArea(parameters.deviceId, parameters.placeId, parameters.newAreaId)
        if (!updateAreaResult) return false
        deviceRepository.refreshDevices()
        return true
    }
}

data class UpdateDeviceAreaParam(
    val deviceId: Int,
    val placeId: Int,
    val newAreaId: Int
)