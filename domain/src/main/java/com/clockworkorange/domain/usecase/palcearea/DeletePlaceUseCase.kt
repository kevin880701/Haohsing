package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeletePlaceUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Int): Boolean {
        val result = placeRepository.deletePlace(parameters)
        deviceRepository.refreshDevices()
        return result
    }
}

