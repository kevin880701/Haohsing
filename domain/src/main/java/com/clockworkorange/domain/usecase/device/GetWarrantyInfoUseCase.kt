package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.Warranty
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetWarrantyInfoUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, Warranty?>(dispatcher) {

    override suspend fun execute(parameters: Int): Warranty? {
        return deviceRepository.getWarrantyInfo(parameters)
    }
}