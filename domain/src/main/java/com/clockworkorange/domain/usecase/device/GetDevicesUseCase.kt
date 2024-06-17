package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.FlowUseCase
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.Device
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class GetDevicesUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<Unit, List<Device>>(dispatcher){

    override fun execute(parameters: Unit): Flow<Result<List<Device>>> {
        return deviceRepository.getDevices()
            .map { Result.Success(it) }
    }
}