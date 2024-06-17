package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteDeviceScheduleUseCase @Inject constructor(
    val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Int): Boolean {
        return deviceRepository.deleteDeviceSchedule(parameters)
    }
}

class DeleteAreaScheduleUseCase @Inject constructor(
    val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Int): Boolean {
        return deviceRepository.deleteAreaSchedule(parameters)
    }
}