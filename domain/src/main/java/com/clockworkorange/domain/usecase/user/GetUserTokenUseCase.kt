package com.clockworkorange.domain.usecase.user

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetUserTokenUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, String>(dispatcher) {

    override suspend fun execute(parameters: Unit): String {
        return userRepository.getUserToken()
    }
}