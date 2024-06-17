package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddPowerScheduleUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<AddPowerScheduleParam, Boolean>(dispatcher) {
    override suspend fun execute(parameters: AddPowerScheduleParam): Boolean {
        return deviceRepository.addDeviceSchedule(
            deviceId = parameters.deviceId,
            parameters.name,
            parameters.powerOnTime,
            parameters.sleepTime,
            parameters.weekDays
        )
    }
}

data class AddPowerScheduleParam(
    val deviceId: Int,
    val name: String,
    val powerOnTime: String,
    val sleepTime: String,
    val weekDays: List<Int>
)

class AddAreaScheduleUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<AddAreaScheduleParam, Boolean>(dispatcher) {
    override suspend fun execute(parameters: AddAreaScheduleParam): Boolean {
        return deviceRepository.addAreaSchedule(
            areaId = parameters.areaId,
            parameters.name,
            parameters.powerOnTime,
            parameters.sleepTime,
            parameters.weekDays
        )
    }
}

data class AddAreaScheduleParam(
    val areaId: Int,
    val name: String,
    val powerOnTime: String,
    val sleepTime: String,
    val weekDays: List<Int>
)