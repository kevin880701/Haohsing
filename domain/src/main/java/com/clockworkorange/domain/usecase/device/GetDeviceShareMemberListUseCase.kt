package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetDeviceShareMemberListUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, List<ShareMember>>(dispatcher){

    override suspend fun execute(parameters: Int): List<ShareMember> {
        return deviceRepository.getDeviceShareMemberList(parameters)
    }
}

data class ShareMember(
    val userId: Int,
    val userName: String,
    val userMail: String,
    val isAccept: Boolean
){
    override fun hashCode(): Int {
        return userId
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ShareMember) return false
        return this.userId == other.userId
    }
}