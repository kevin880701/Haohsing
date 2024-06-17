package com.clockworkorange.domain.usecase.user

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetUserShareDeviceIdListUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<GetUserShareDeviceIdListParam, List<Int>>(dispatcher) {
    override suspend fun execute(parameters: GetUserShareDeviceIdListParam): List<Int> {
        return userRepository.getUserShareDeviceIdList(parameters.userId, parameters.placeId)
    }
}

data class GetUserShareDeviceIdListParam(
    val placeId: Int,
    val userId: Int
)