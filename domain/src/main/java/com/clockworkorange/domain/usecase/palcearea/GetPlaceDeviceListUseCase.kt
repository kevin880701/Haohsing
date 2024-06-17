package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.Device
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetPlaceDeviceListUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, List<Device>>(dispatcher) {

    override suspend fun execute(parameters: Int): List<Device> {
        return placeRepository.getPlaceDeviceList(parameters)
    }
}