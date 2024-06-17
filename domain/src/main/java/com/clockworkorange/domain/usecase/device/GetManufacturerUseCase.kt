package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.FlowUseCase
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.ManufacturerInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetManufacturerUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<Int, ManufacturerInfo>(dispatcher) {

    override fun execute(parameters: Int): Flow<Result<ManufacturerInfo>> = flow {
        emit(Result.Success(deviceRepository.getManufacturerInfo(parameters)))
    }
}