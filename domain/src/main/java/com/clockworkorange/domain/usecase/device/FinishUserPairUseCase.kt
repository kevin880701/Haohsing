package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDate
import javax.inject.Inject

class FinishUserPairUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<FinishUserPairParam, Boolean>(dispatcher) {
    override suspend fun execute(parameters: FinishUserPairParam): Boolean {
        return deviceRepository.finishUserPair(parameters)
    }
}

data class FinishUserPairParam(
    val deviceId: Int,
    val sn: String?,
    val mac: String,
    val deviceName: String,
    val placeId: Int,
    val areaId: Int,
    val ownerName: String?,
    val ownerEmail: String?,
    val ownerPhone: String?,
    val buyDate: LocalDate?
)