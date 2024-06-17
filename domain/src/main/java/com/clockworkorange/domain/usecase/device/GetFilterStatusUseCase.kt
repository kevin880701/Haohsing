package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.FlowUseCase
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.FilterStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFilterStatusUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<Int, List<FilterStatus>>(dispatcher) {

    override fun execute(parameters: Int): Flow<Result<List<FilterStatus>>> = flow {
        emit(Result.Success(deviceRepository.getFilterStatus(parameters)))
    }
}