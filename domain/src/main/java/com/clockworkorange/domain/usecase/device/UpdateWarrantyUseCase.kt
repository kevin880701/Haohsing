package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDate
import javax.inject.Inject

class UpdateWarrantyUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<UpdateWarrantyParam, Boolean>(dispatcher) {
    override suspend fun execute(parameters: UpdateWarrantyParam): Boolean {
        return deviceRepository.updateWarranty(parameters)
    }
}

data class UpdateWarrantyParam(
    val deviceId: Int,
    val ownerName: String,
    val ownerEmail: String,
    val ownerPhone: String,
    val buyDate: LocalDate
)