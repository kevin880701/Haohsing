package com.clockworkorange.domain.usecase.user

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.login.FormatChecker
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class PasswordFormatCheckerUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<String, Boolean>(dispatcher) {

    override suspend fun execute(parameters: String): Boolean {
        return FormatChecker.isPasswordValid(parameters)
    }
}