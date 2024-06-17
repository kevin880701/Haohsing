package com.clockworkorange.domain.usecase.user

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File
import javax.inject.Inject

class UpdateUserImageUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<File, String>(dispatcher) {

    override suspend fun execute(parameters: File): String {
        return userRepository.updateUserImage(parameters) ?: ""
    }
}