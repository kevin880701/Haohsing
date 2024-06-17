package com.clockworkorange.domain.usecase.login

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CheckLoginStatusUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Unit): Boolean {
        return userRepository.isLoginStatusAvailable()
    }
}