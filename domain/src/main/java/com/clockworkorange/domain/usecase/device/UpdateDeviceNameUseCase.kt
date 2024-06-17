package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateDeviceNameUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<UpdateDeviceNameParam, Boolean>(dispatcher) {
    override suspend fun execute(parameters: UpdateDeviceNameParam): Boolean {
        val updateDeviceResult = deviceRepository.updateDeviceName(parameters.deviceId, parameters.newName)
        if (updateDeviceResult){
            deviceRepository.refreshDevices()
        }
        return updateDeviceResult
    }
}

data class UpdateDeviceNameParam(val deviceId: Int, val newName: String)