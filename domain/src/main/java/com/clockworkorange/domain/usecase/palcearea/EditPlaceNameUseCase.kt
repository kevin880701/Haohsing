package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class EditPlaceNameUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<EditPlaceNameParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: EditPlaceNameParam): Boolean {
        return placeRepository.updatePlaceName(parameters.placeId, parameters.newName).also {
            deviceRepository.refreshDevices()
        }
    }
}

data class EditPlaceNameParam(
    val placeId: Int,
    val newName: String
)