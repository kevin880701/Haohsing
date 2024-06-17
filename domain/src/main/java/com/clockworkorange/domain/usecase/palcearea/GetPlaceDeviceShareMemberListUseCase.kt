package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.device.ShareMember
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetPlaceDeviceShareMemberListUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, List<PlaceShareMember>>(dispatcher){

    override suspend fun execute(parameters: Int): List<PlaceShareMember> {
        val userId = userRepository.getUserInfo().value.data?.id
        return placeRepository.getPlaceDeviceShareMemberList(parameters).filter { it.member.userId != userId }
    }
}

data class PlaceShareMember(
    val member: ShareMember,
    val deviceCount: Int
)