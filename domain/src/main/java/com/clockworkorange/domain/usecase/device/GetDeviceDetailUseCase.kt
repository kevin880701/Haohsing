package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.FlowUseCase
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.Device
import com.clockworkorange.domain.entity.DeviceDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetDeviceDetailUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<Int, DeviceDetail>(dispatcher){

    override fun execute(parameters: Int): Flow<Result<DeviceDetail>> {
        return deviceRepository.getDeviceDetailFlow(parameters)
            .filterNotNull()
            .map { Result.Success(it) }
    }
}