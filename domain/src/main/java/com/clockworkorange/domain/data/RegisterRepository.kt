package com.clockworkorange.domain.data

import com.clockworkorange.domain.data.remote.ServiceWrapper
import com.clockworkorange.domain.data.remote.model.RegisterParam
import kotlinx.coroutines.delay
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

interface RegisterRepository {

    suspend fun sendVerifyCodeEmail(mail: String): Boolean

    suspend fun checkVerifyCode(mail: String, code: String): Boolean

    suspend fun register(mail: String, password: String): Boolean

    suspend fun isAccountExist(mail: String): Boolean
}

class RegisterRepositoryImpl @Inject constructor(
    private val service: ServiceWrapper
): RegisterRepository{
    override suspend fun sendVerifyCodeEmail(mail: String): Boolean {
        delay(500)
        return true
    }

    override suspend fun checkVerifyCode(mail: String, code: String): Boolean {
        delay(500)
        return true
    }

    override suspend fun register(mail: String, password: String): Boolean {
        return try {
            val param = RegisterParam(mail, password)
            val result = service.register(param)
            result.isSuccess()
        }catch (e: Exception){
            false
        }
    }

    override suspend fun isAccountExist(mail: String): Boolean {
        return try {
            service.isAccountExit(mail)
        }catch (e: HttpException){
            val statusCode = e.code()
            val body = e.response()?.errorBody()?.string()
            Timber.d("code: $statusCode, body: $body")
            false
        }catch (e: Exception){
            false
        }
    }
}

class FakeRegisterRepository: RegisterRepository{

    override suspend fun sendVerifyCodeEmail(mail: String): Boolean {
        delay(500)
        return true
    }

    override suspend fun checkVerifyCode(mail: String, code: String): Boolean {
        delay(500)
        return code == "8716"
    }

    override suspend fun register(mail: String, password: String): Boolean {
        delay(500)
        return true
    }

    override suspend fun isAccountExist(mail: String): Boolean {
        delay(500)
        return mail.startsWith("exist")
    }
}