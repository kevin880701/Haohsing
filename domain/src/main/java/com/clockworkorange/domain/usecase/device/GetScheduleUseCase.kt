package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.PowerSchedule
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetAreaScheduleUseCase @Inject constructor(
    val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, PowerSchedule>(dispatcher) {

    override suspend fun execute(parameters: Int): PowerSchedule {
        return deviceRepository.getAreaSchedule(parameters)
    }
}