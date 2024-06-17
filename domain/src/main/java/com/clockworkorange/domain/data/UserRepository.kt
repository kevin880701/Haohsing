package com.clockworkorange.domain.data

import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.data.remote.ServiceWrapper
import com.clockworkorange.domain.data.remote.model.*
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.usecase.user.AccountPasswordErrorException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

interface UserRepository {

    suspend fun isLoginStatusAvailable(): Boolean

    suspend fun login(account: String, password: String, keepLoginStatus: Boolean): Boolean

    suspend fun googleLogin(token: String): Boolean

    suspend fun appleLogin(authCode: String): Boolean

    suspend fun logout(fcmToken: String)

    suspend fun forgetPassword(account: String): Boolean

    suspend fun resendActiveEmail(account: String): Boolean

    suspend fun updateUserPassword(oldPassword: String, newPassword: String): Boolean

    suspend fun updateUserName(name: String): Boolean

    suspend fun updatePhone(phone: String): Boolean

    suspend fun updateCityRegion(city: String, region: String): Boolean

    suspend fun updateUserImage(file: File): String?

    fun getUserInfo(): StateFlow<Result<UserInfo>>

    suspend fun getUserShareDeviceIdList(userId: Int, placeId: Int): List<Int>

    suspend fun uploadFcmToken(fcmToken: String): Boolean
    suspend fun getUserToken(): String
}

class AccountNotActiveException: Exception()
class SystemMaintainException: Exception()
class WrongAccountPasswordException: Exception()
class NoNetworkException: Exception()
class ServerErrorException: Exception()
class SessionExpiredException: Exception()

class UserRepositoryImpl @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val preferenceStorage: PreferenceStorage,
    private val service: ServiceWrapper,
): UserRepository{

    private val userInfoStateFlow = MutableStateFlow<Result<UserInfo>>(Result.Loading)

    override suspend fun login(
        account: String,
        password: String,
        keepLoginStatus: Boolean
    ): Boolean {
        try {
            //登入成功
            val result = service.login(LoginParam(account, password))

            if (result.isErrorWrongAccountPassword()) {
                throw WrongAccountPasswordException()
            }

            if (result.isErrorAccountNotActive()) {
                throw AccountNotActiveException()
            }
            preferenceStorage.setKeepLoginStatus(keepLoginStatus)
            refreshUserInfo()
            return true
        }catch (e: Exception){
            e.printStackTrace()
            preferenceStorage.setKeepLoginStatus(false)
            when (e) {
                is WrongAccountPasswordException -> throw e
                is AccountNotActiveException -> throw e
                is NoNetworkException -> throw e
                else -> throw SystemMaintainException()
            }
        }
    }

    override suspend fun isLoginStatusAvailable(): Boolean {
        val keepLoginStatus = preferenceStorage.isKeepLoginStatus()
        if (!keepLoginStatus){
            return false
        }
        try {
            service.refreshSession()
            refreshUserInfo()
        }catch (e: Exception){
            return false
        }

        return true
    }

    override suspend fun googleLogin(token: String): Boolean {
        try {
            //登入成功
            val result = service.googleLogin(token)

            if (result.isErrorWrongAccountPassword()) {
                throw WrongAccountPasswordException()
            }

            if (result.isErrorAccountNotActive()) {
                throw AccountNotActiveException()
            }
            preferenceStorage.setKeepLoginStatus(true)
            refreshUserInfo()
            return true
        }catch (e: Exception){
            e.printStackTrace()
            preferenceStorage.setKeepLoginStatus(false)
            when (e) {
                is WrongAccountPasswordException -> throw e
                is AccountNotActiveException -> throw e
                is NoNetworkException -> throw e
                else -> throw SystemMaintainException()
            }
        }
    }

    override suspend fun appleLogin(authCode: String): Boolean {
        try {
            //登入成功
            val result = service.appleLogin(authCode)
            if (result.isErrorWrongAccountPassword()) {
                throw WrongAccountPasswordException()
            }

            if (result.isErrorAccountNotActive()) {
                throw AccountNotActiveException()
            }
            preferenceStorage.setKeepLoginStatus(true)
            refreshUserInfo()
            return true
        }catch (e: Exception){
            e.printStackTrace()
            preferenceStorage.setKeepLoginStatus(false)
            when (e) {
                is WrongAccountPasswordException -> throw e
                is AccountNotActiveException -> throw e
                is NoNetworkException -> throw e
                else -> throw SystemMaintainException()
            }
        }
    }

    override suspend fun forgetPassword(account: String): Boolean {
        return try {
            service.forgetPassword(ForgetPasswordParam(account)).isSuccess()
        }catch (e: Exception){
            false
        }
    }

    override suspend fun resendActiveEmail(account: String): Boolean {
        return try {
            service.resendActiveMail(ResendActiveMailParam(account)).isSuccess()
        }catch (e: Exception){
            false
        }
    }

    override suspend fun updateUserName(name: String): Boolean {
        return try {
            service.updateUserInfo(mapOf("name" to name)).also { refreshUserInfo() }
        }catch (e: Exception){
            false
        }
    }

    override suspend fun updateUserPassword(oldPassword: String, newPassword: String): Boolean {

        val result = service.updateUserPassword(UpdateUserPasswordParam(oldPassword, newPassword))
        if (result.result == ApiResult.Login__AccountPasswordError){
            throw AccountPasswordErrorException()
        }
        return result.isSuccess()
    }

    override suspend fun updatePhone(phone: String): Boolean {
        return try {
            service.updateUserInfo(mapOf("tel" to phone)).also { refreshUserInfo() }
        }catch (e: Exception){
            false
        }
    }

    override suspend fun updateCityRegion(city: String, region: String): Boolean {
        return try {
            service.updateUserInfo(mapOf("city" to city, "region" to region)).also { refreshUserInfo() }
        }catch (e: Exception){
            false
        }
    }

    override suspend fun updateUserImage(file: File): String? {
        return try {
            service.updateUserImage(file).also { refreshUserInfo() }
        }catch (e: Exception){
            Timber.e(e)
            null
        }
    }


    override fun getUserInfo(): StateFlow<Result<UserInfo>> {
        if (userInfoStateFlow.value is Result.Loading){
            applicationScope.launch { refreshUserInfo() }
        }
        return userInfoStateFlow
    }

    private suspend fun refreshUserInfo(){
        val userInfoResponse = service.getUserInfo()
        userInfoStateFlow.tryEmit(Result.Success(userInfoResponse.toUserInfo()))
    }

    override suspend fun logout(fcmToken: String) {
        service.logout(fcmToken)
        preferenceStorage.setKeepLoginStatus(false)
        userInfoStateFlow.tryEmit(Result.Loading)
    }

    override suspend fun getUserShareDeviceIdList(userId: Int, placeId: Int): List<Int> {
        return service.getPlaceDevicePermission(placeId, userId).filter { it.type == 1 }.map { it.deviceId }
    }

    override suspend fun uploadFcmToken(fcmToken: String): Boolean {
        return service.uploadFcmToken(fcmToken)
    }

    override suspend fun getUserToken(): String {
        return service.getToken()
    }
}
