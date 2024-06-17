package com.clockworkorange.domain.usecase.register

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.RegisterRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SendVerifyCodeUseCase @Inject constructor(
    private val registerRepository: RegisterRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<String, Boolean>(dispatcher){

    override suspend fun execute(mail: String): Boolean {
        return registerRepository.sendVerifyCodeEmail(mail)
    }
}