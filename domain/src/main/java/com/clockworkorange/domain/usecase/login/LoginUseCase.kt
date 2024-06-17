package com.clockworkorange.domain.usecase.login

import com.clockworkorange.domain.BuildConfig
import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<LoginUserCaseParams, Boolean>(dispatcher) {

    override suspend fun execute(parameters: LoginUserCaseParams): Boolean {
        val (mail, password, keepLoginStatus) = parameters


        if (!BuildConfig.DEBUG){
            if (!FormatChecker.isPasswordValid(password)){
                throw PasswordFormatInValidException()
            }
        }

        return userRepository.login(mail, password, keepLoginStatus)
    }
}

data class LoginUserCaseParams(
    val account: String,
    val password: String,
    val keepLoginStatus: Boolean
)

class EmailFormatInValidException: Exception()
class PasswordFormatInValidException: Exception()