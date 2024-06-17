package com.clockworkorange.domain.usecase.register

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.RegisterRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class VerifyCodeCorrectUseCase @Inject constructor(
    private val registerRepository: RegisterRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<VerifyCodeCorrectParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: VerifyCodeCorrectParam): Boolean {
        val (mail, code) = parameters
        return registerRepository.checkVerifyCode(mail, code)
    }
}

data class VerifyCodeCorrectParam(
    val mail: String,
    val verifyCode: String
)