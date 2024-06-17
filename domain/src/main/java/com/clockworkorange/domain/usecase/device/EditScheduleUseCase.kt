package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class EditDeviceScheduleUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<EditPowerScheduleParam, Boolean>(dispatcher) {
    override suspend fun execute(parameters: EditPowerScheduleParam): Boolean {
        return deviceRepository.editDeviceSchedule(
            parameters.scheduleId,
            parameters.name,
            parameters.powerOnTime,
            parameters.sleepTime,
            parameters.weekDays
        )
    }
}

class EditAreaScheduleUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<EditPowerScheduleParam, Boolean>(dispatcher) {
    override suspend fun execute(parameters: EditPowerScheduleParam): Boolean {
        return deviceRepository.editAreaSchedule(
            parameters.scheduleId,
            parameters.name,
            parameters.powerOnTime,
            parameters.sleepTime,
            parameters.weekDays
        )
    }
}

data class EditPowerScheduleParam(
    val scheduleId: Int,
    val name: String,
    val powerOnTime: String,
    val sleepTime: String,
    val weekDays: List<Int>
)