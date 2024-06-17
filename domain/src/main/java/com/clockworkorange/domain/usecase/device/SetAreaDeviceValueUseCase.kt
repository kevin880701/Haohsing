package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SetAreaDeviceValueUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<SetAreaDeviceValueParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: SetAreaDeviceValueParam): Boolean {
        return deviceRepository.setAreaDeviceValue(parameters.areaId, parameters.code, parameters.value)
    }
}

data class SetAreaDeviceValueParam(
    val areaId: Int,
    val code: String,
    val value: String
)