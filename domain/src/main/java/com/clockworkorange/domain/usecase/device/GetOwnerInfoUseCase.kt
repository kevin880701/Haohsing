package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.FlowUseCase
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.OwnerInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetOwnerInfoUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<Int, OwnerInfo>(dispatcher) {

    override fun execute(parameters: Int): Flow<Result<OwnerInfo>> = flow {
        emit(Result.Success(deviceRepository.getOwnerInfo(parameters)))
    }
}