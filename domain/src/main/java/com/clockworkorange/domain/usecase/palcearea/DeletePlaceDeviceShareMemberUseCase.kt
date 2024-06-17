package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeletePlaceDeviceShareMemberUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<DeletePlaceDeviceShareMemberParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: DeletePlaceDeviceShareMemberParam): Boolean {
        return placeRepository.deletePlaceDeviceShareMember(parameters.placeId, parameters.userId)
    }
}

data class DeletePlaceDeviceShareMemberParam(
    val placeId: Int,
    val userId: Int
)