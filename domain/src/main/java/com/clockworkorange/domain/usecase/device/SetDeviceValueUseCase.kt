package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SetDeviceValueUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<SetDeviceValueParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: SetDeviceValueParam): Boolean {
        return deviceRepository.setDeviceValue(parameters.deviceId, parameters.code, parameters.value)
    }
}

data class SetDeviceValueParam(
    val deviceId: Int,
    val code: String,
    val value: String
)