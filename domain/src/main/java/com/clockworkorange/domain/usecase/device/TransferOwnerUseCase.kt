package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class TransferOwnerUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<TransferOwnerParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: TransferOwnerParam): Boolean {
        return deviceRepository.transferOwner(parameters)
    }
}


data class TransferOwnerParam(
    val account: String,
    val deviceIds: List<Int>
)