package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetWaterTempOptionUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<WaterTempType, List<String>>(dispatcher) {

    override suspend fun execute(parameters: WaterTempType): List<String> {
        return deviceRepository.getWaterTempOptions(parameters)
    }
}

enum class WaterTempType{
    Hot, Cold
}