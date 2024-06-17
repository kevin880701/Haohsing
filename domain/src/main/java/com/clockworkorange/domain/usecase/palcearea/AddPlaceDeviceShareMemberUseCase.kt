package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddPlaceDeviceShareMemberUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<AddPlaceDeviceShareMemberParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: AddPlaceDeviceShareMemberParam): Boolean {
        return placeRepository.addPlaceDeviceShareMember(parameters.placeId, parameters.mail)
    }
}

data class AddPlaceDeviceShareMemberParam(
    val placeId: Int,
    val mail: String
)