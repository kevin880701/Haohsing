package com.clockworkorange.domain.usecase.user

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.login.FormatChecker
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateUserPhoneUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<String, Boolean>(dispatcher){

    override suspend fun execute(phone: String): Boolean {
        if (!FormatChecker.isPhoneNumberValid(phone)){
            throw PhoneFormatInValidException()
        }else{
            return userRepository.updatePhone(phone)
        }
    }
}

class PhoneFormatInValidException: Exception("電話號碼格式錯誤，長度需為10碼且為09開頭")
