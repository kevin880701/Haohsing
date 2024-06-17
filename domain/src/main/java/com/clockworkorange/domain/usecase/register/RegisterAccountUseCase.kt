package com.clockworkorange.domain.usecase.register

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.RegisterRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class RegisterAccountUseCase @Inject constructor(
    private val registerRepository: RegisterRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<RegisterAccountParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: RegisterAccountParam): Boolean {
        val (account, password) = parameters
        return registerRepository.register(account, password)
    }
}

data class RegisterAccountParam(
    val account: String,
    val password: String
)