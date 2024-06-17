package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateDevicePlaceUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<UpdateDevicePlaceParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: UpdateDevicePlaceParam): Boolean {
        val updatePlaceResult = deviceRepository.updateDevicePlace(parameters.deviceId, parameters.newPlaceId)
        if (!updatePlaceResult) return false
        deviceRepository.refreshDevices()
        return true
    }
}

data class UpdateDevicePlaceParam(
    val deviceId: Int,
    val newPlaceId: Int
)