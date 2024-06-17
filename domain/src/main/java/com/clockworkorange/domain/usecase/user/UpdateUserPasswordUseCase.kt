package com.clockworkorange.domain.usecase.user

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.login.FormatChecker
import com.clockworkorange.domain.usecase.login.PasswordFormatInValidException
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

class UpdateUserPasswordUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<UpdateUserPasswordParam, Boolean>(dispatcher) {

    override suspend fun execute(parameters: UpdateUserPasswordParam): Boolean {
        if (!FormatChecker.isPasswordValid(parameters.newPassword)){
            throw PasswordFormatInValidException()
        }
        return try {
            userRepository.updateUserPassword(parameters.oldPassword, parameters.newPassword)
        }catch (e: AccountPasswordErrorException){
            throw e
        }catch (e: Exception){
            Timber.e(e)
            false
        }
    }
}

data class UpdateUserPasswordParam(
    val oldPassword:String,
    val newPassword:String
)

class AccountPasswordErrorException: Exception()

