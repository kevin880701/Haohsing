package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class EditAreaNameUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<EditAreaNameParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: EditAreaNameParam): Boolean {
        return placeRepository.updateAreaName(parameters.areaId, parameters.newName).also {
            deviceRepository.refreshDevices()
        }
    }
}

data class EditAreaNameParam(
    val areaId: Int,
    val newName: String
)