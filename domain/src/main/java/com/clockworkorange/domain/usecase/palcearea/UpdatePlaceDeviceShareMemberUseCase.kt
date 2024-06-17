package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdatePlaceDeviceShareMemberUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<UpdatePlaceDeviceShareMemberParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: UpdatePlaceDeviceShareMemberParam): Boolean {
        return placeRepository.updatePlaceDeviceShareMember(
            parameters.placeId,
            parameters.userId,
            parameters.addShareDeviceIds,
            parameters.removeShareDeviceIds
        )
    }
}

data class UpdatePlaceDeviceShareMemberParam(
    val placeId: Int,
    val userId: Int,
    val addShareDeviceIds: List<Int>,
    val removeShareDeviceIds: List<Int>
)