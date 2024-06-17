package com.clockworkorange.domain.usecase.user

import com.clockworkorange.domain.FlowUseCase
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data.UserRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.UserInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<Unit, UserInfo>(dispatcher) {

    override fun execute(parameters: Unit): Flow<Result<UserInfo>> {
        return userRepository.getUserInfo()
    }
}