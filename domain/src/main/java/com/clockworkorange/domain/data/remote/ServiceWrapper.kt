package com.clockworkorange.domain.data.remote

import com.clockworkorange.domain.data.NoNetworkException
import com.clockworkorange.domain.data.PreferenceStorage
import com.clockworkorange.domain.data.ServerErrorException
import com.clockworkorange.domain.data.SessionExpiredException
import com.clockworkorange.domain.data.remote.model.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface ServiceWrapper {

    suspend fun getToken(): String

    suspend fun login(param: LoginParam): LoginResponse

    suspend fun refreshSession()

    suspend fun logout(fcmToken: String): Boolean

    suspend fun forgetPassword(param: ForgetPasswordParam): SimpleApiResult

    suspend fun updateUserPassword(param: UpdateUserPasswordParam): SimpleApiResult

    suspend fun register(param: RegisterParam): SimpleApiResult

    suspend fun getUserInfo(): UserInfoResponse

    suspend fun updateUserInfo(data: Map<String, String>): Boolean

    suspend fun updateUserImage(file: File): String

    suspend fun getCityList(): List<String>

    suspend fun getRegionList(city: String): List<String>

    suspend fun isAccountExit(account: String): Boolean

    suspend fun resendActiveMail(param: ResendActiveMailParam): SimpleApiResult

    suspend fun getUserPlaceList(): List<UserPlaceResponse>

    suspend fun updatePlaceName(placeId: Int, newName: String): Boolean

    suspend fun addPlace(name: String): Int

    suspend fun deletePlace(placeId: Int): Boolean

    suspend fun updateAreaName(areaId: Int, newName: String): Boolean

    suspend fun addArea(placeId: Int, name: String): Int

    suspend fun deleteArea(areaId: Int): Boolean

    suspend fun getPlaceDetailList(): List<PlaceDetailResponse>

    suspend fun getAreaList(placeId: Int): List<AreaResponse>

    suspend fun addWorkOrder(deviceId: Int): AddWorkOrderResponse

    suspend fun editWorkOrder(data: ApiEditWorkOrderParam): Boolean

    suspend fun uploadWorkOrderImage(workOrderId: Int, file: File): String

    suspend fun getWorkOrderOption(type: Int): List<WorkOrderReasonResponse>

    suspend fun getUserDeviceList(): List<ApiDevice>

    suspend fun updateDeviceName(deviceId: Int, newName: String): Boolean

    suspend fun getDeviceShareMemberList(deviceId: Int): List<ApiMember>

    suspend fun shareDevice(deviceId: Int, mail: String): Boolean

    suspend fun deleteDeviceShareMember(userId: Int, deviceId: Int): Boolean

    suspend fun getPlaceShareMemberList(placeId: Int): List<ApiPlaceMember>

    suspend fun sharePlaceDevice(placeId: Int, account: String): Boolean

    suspend fun deletePlaceDeviceShareMember(userId: Int, placeId: Int): Boolean

    suspend fun getPlaceDeviceList(placeId: Int): List<ApiDevice>

    suspend fun getDeviceWorkOrderList(deviceId: Int, type: Int): List<ApiWorkOrderStatus>

    suspend fun updatePlaceDevicePermission(data: List<UpdatePlaceDevicePermissionParam>): Boolean

    suspend fun cancelWorkOrder(id: Int): Boolean

    suspend fun getWorkOrderDetail(workOrderId: Int): ApiWorkOrderDetail

    suspend fun getDeviceDetail(deviceId: Int): ApiDeviceDetail

    suspend fun getVendorInfo(vendorId: Int): ApiVendorInfo

    suspend fun getVendorList(): List<ApiVendorInfo>

    suspend fun getWarranty(deviceId: Int): ApiWarranty

    suspend fun updateDevicePlaceArea(data: UpdateDevicePlaceAreaParam): Boolean

    suspend fun getEngineerWorkOrderList(year: Int, month: Int): List<ApiTaskSummary>

    suspend fun uploadWorkOrderEngineerImage(workOrderId: Int, kind: Int, file: File): String

    suspend fun doneWorkOrder(data: DoneWorkOrderParam): Boolean

    suspend fun uploadSignImage(workOrderId: Int, file: File): String

    suspend fun updateWorkTime(workOrderId: Int): Boolean

    suspend fun getFilterList(deviceId: Int): List<ApiDeviceFilter>

    suspend fun changeFilter(data: ChangeFilterParam): Boolean

    suspend fun getMsgList(): List<ApiMsg>

    suspend fun getInviteList(): List<ApiInvite>

    suspend fun acceptOrRejectInvite(deviceId: Int, accept: Boolean): Boolean

    suspend fun uploadFcmToken(token: String): Boolean

    suspend fun getTaskHistory(month: Int): List<ApiTaskSummary>

    suspend fun googleLogin(idToken: String): LoginResponse

    suspend fun appleLogin(authCode: String): LoginResponse

    suspend fun getInstallRecordList(month: Int): List<ApiInstallRecord>

    suspend fun setDeviceValue(deviceId: Int, id: String, value: String): Boolean

    suspend fun setAreaDeviceValue(areaId: Int, id: String, value: String): Boolean

    suspend fun getHotTempOptions(): List<String>

    suspend fun getColdTempOptions(): List<String>

    suspend fun getPlaceList(): List<ApiPlace>

    suspend fun getDeviceUnderPlaceArea(placeId: Int, areaId: Int): List<ApiDevice>

    suspend fun searchDevice(mac: String): ApiDeviceByMAC

    suspend fun getCustomerList(): List<ApiCustomer>

    suspend fun updateWarranty(deviceId: Int, name: String, email: String, phone: String, date: LocalDate): Boolean

    suspend fun pairDeviceUser(sn: String?, mac: String, name: String, placeId: Int? = null, areaId: Int? = null, customerId: Int? = null,
                               customerAddress: String? = null): Boolean

    suspend fun addDeviceManually(param: AddDeviceManualParam): Int

    suspend fun uploadInstallPhoto(deviceId: Int, kind: Int, file: File): String

    suspend fun uploadInstallForm(deviceId: Int, formOption: List<ApiFormOptionField>): Boolean

    suspend fun getPlaceDevicePermission(placeId: Int, userId: Int): List<ApiPlaceDevicePermission>

    suspend fun getDeviceScheduleList(deviceId: Int): List<ApiDeviceSchedule>

    suspend fun getAreaScheduleList(areaId: Int): List<ApiAreaSchedule>

    suspend fun getDeviceSchedule(qId: Int): List<ApiDeviceSchedule>

    suspend fun createDeviceSchedule(data: CreateScheduleParam): Int

    suspend fun editDeviceSchedule(data: ApiEditScheduleParam): Boolean

    suspend fun deleteDeviceSchedule(qId: Int): Boolean

    suspend fun getAreaSchedule(qId: Int): ApiAreaSchedule

    suspend fun createAreaSchedule(data: CreateAreaScheduleParam): Int

    suspend fun editAreaSchedule(data: ApiEditScheduleParam): Boolean

    suspend fun deleteAreaSchedule(qId: Int): Boolean

    suspend fun getPlaceDataAnalysis(placeId: Int, year: Int, month: Int): ApiPlaceStatistics

    suspend fun getDeviceStatistics(deviceId: Int): ApiDeviceStatistics

    suspend fun transferOwner(account: String, ids: List<Int>): Boolean

    suspend fun quitDevice(deviceId: Int): Boolean
}

class ServiceWrapperImpl(
    private val service: HaohsingService,
    private val preferenceStorage: PreferenceStorage,
    private val globalErrorHandler: GlobalErrorHandler,
    private val gson: Gson
) : ServiceWrapper {

    private var cacheToken: String? = null
    private var cacheRefreshToken: String? = null

    override suspend fun getToken(): String {
        return cacheToken ?: preferenceStorage.getToken() ?: ""
    }

    private suspend fun getRefreshToken(): String {
        return cacheRefreshToken ?: preferenceStorage.getRefreshToken() ?: ""
    }

    private suspend fun saveToken(token: String, refreshToken: String) {
        this.cacheToken = token
        this.cacheRefreshToken = refreshToken
        preferenceStorage.saveToken(token)
        preferenceStorage.saveRefreshToken(refreshToken)
    }

    override suspend fun login(param: LoginParam): LoginResponse = wrapperError {
        val result = service.login(param)
        if (result.isSuccess()) {
            val longTimeToken = service.makeLongTimeToken(result.token)
            saveToken(longTimeToken.token, "")
        }
        return@wrapperError result
    }

    override suspend fun refreshSession() {
        val result = service.makeLongTimeToken(getToken())
        saveToken(result.token, "")
    }

    override suspend fun logout(fcmToken: String): Boolean = wrapperError {
        val result = service.logout(getToken(), LogoutParam(fcmToken))
        saveToken("", "")
        return@wrapperError result
    }

    private suspend fun refreshToken(): Boolean = wrapperError {
        val result = service.refreshToken(getToken(), RefreshTokenParam(getRefreshToken()))
        if (result.isSuccess()) {
            saveToken(result.token, result.refreshToken)
        }
        return@wrapperError result.isSuccess()
    }

    override suspend fun forgetPassword(param: ForgetPasswordParam): SimpleApiResult =
        wrapperError {
            return@wrapperError service.forgetPassword(param)
        }

    override suspend fun updateUserPassword(param: UpdateUserPasswordParam): SimpleApiResult =
        wrapperError {
            return@wrapperError service.updateUserPassword(getToken(), param)
        }

    override suspend fun register(param: RegisterParam): SimpleApiResult = wrapperError {
        return@wrapperError service.register(param)
    }

    override suspend fun getUserInfo(): UserInfoResponse = wrapperError {
        return@wrapperError service.getUserInfo(getToken())
    }

    override suspend fun updateUserInfo(data: Map<String, String>): Boolean = wrapperError {
        return@wrapperError service.updateUserInfo(getToken(), data)
    }

    override suspend fun updateUserImage(file: File): String = wrapperError {
        val data = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("image/*".toMediaType())
        )
        return@wrapperError service.updateUserImage(getToken(), data)
    }

    override suspend fun getCityList(): List<String> = wrapperError {
        return@wrapperError service.getCityList()
    }

    override suspend fun getRegionList(city: String): List<String> = wrapperError {
        return@wrapperError service.getRegionList(city)
    }

    override suspend fun isAccountExit(account: String): Boolean = wrapperError {
        return@wrapperError service.isAccountExit(account)
    }

    override suspend fun resendActiveMail(param: ResendActiveMailParam): SimpleApiResult =
        wrapperError {
            return@wrapperError service.resendActiveMail(param)
        }

    override suspend fun getUserPlaceList(): List<UserPlaceResponse> = wrapperError {
        return@wrapperError service.getUserPlaceList(getToken())
    }

    override suspend fun updatePlaceName(placeId: Int, newName: String): Boolean = wrapperError {
        return@wrapperError service.updatePlaceName(
            getToken(),
            UpdatePlaceNameParam(placeId, newName)
        )
    }

    override suspend fun addPlace(name: String): Int = wrapperError {
        return@wrapperError service.addPlace(getToken(), mapOf("name" to name))
    }

    override suspend fun deletePlace(placeId: Int): Boolean = wrapperError {
        return@wrapperError service.deletePlace(getToken(), placeId)
    }

    override suspend fun updateAreaName(areaId: Int, newName: String): Boolean = wrapperError {
        return@wrapperError service.updateAreaName(getToken(), UpdateAreaNameParam(areaId, newName))
    }

    override suspend fun addArea(placeId: Int, name: String): Int = wrapperError {
        return@wrapperError service.addArea(getToken(), AddAreaParam(placeId, name))
    }

    override suspend fun deleteArea(areaId: Int): Boolean = wrapperError {
        return@wrapperError service.deleteArea(getToken(), areaId)
    }

    override suspend fun getPlaceDetailList(): List<PlaceDetailResponse> = wrapperError {
        return@wrapperError service.getPlaceDetailList(getToken())
    }

    override suspend fun getAreaList(placeId: Int): List<AreaResponse> = wrapperError {
        return@wrapperError service.getAreaList(getToken(), placeId)
    }

    override suspend fun addWorkOrder(deviceId: Int): AddWorkOrderResponse = wrapperError {
        return@wrapperError service.addWorkOrder(
            getToken(),
            mapOf("device_id" to deviceId.toString())
        )
    }

    override suspend fun editWorkOrder(data: ApiEditWorkOrderParam): Boolean = wrapperError {
        return@wrapperError service.editWorkOrder(getToken(), data)
    }

    override suspend fun uploadWorkOrderImage(workOrderId: Int, file: File): String = wrapperError {
        val data = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        return@wrapperError service.uploadWorkOrderImage(getToken(), workOrderId, data)
    }

    override suspend fun updateDevicePlaceArea(data: UpdateDevicePlaceAreaParam): Boolean =
        wrapperError {
            return@wrapperError service.updateDevicePlaceArea(getToken(), data)
        }

    override suspend fun getWorkOrderOption(type: Int): List<WorkOrderReasonResponse> =
        wrapperError {
            return@wrapperError service.getWorkOrderOption(getToken(), type)
        }

    override suspend fun getUserDeviceList(): List<ApiDevice> = wrapperError {
        return@wrapperError service.getUserDeviceList(getToken())
    }

    override suspend fun updateDeviceName(deviceId: Int, newName: String): Boolean = wrapperError {
        return@wrapperError service.updateDeviceName(
            getToken(),
            UpdateDeviceNameParam(deviceId, newName)
        )
    }

    override suspend fun getDeviceShareMemberList(deviceId: Int): List<ApiMember> = wrapperError {
        return@wrapperError service.getDeviceShareMemberList(getToken(), deviceId)
    }

    override suspend fun shareDevice(deviceId: Int, mail: String): Boolean = wrapperError {
        return@wrapperError service.shareDevice(getToken(), ShareDeviceParam(deviceId, mail))
    }

    override suspend fun deleteDeviceShareMember(userId: Int, deviceId: Int): Boolean =
        wrapperError {
            return@wrapperError service.deleteDeviceShareMember(getToken(), userId, deviceId)
        }

    override suspend fun getPlaceShareMemberList(placeId: Int): List<ApiPlaceMember> =
        wrapperError {
            return@wrapperError service.getPlaceShareMemberList(getToken(), placeId)
        }

    override suspend fun sharePlaceDevice(placeId: Int, account: String): Boolean = wrapperError {
        return@wrapperError service.sharePlaceDevice(
            getToken(),
            SharePlaceDeviceParam(placeId, account)
        )
    }

    override suspend fun deletePlaceDeviceShareMember(userId: Int, placeId: Int): Boolean =
        wrapperError {
            return@wrapperError service.deletePlaceDeviceShareMember(getToken(), userId, placeId)
        }

    override suspend fun getPlaceDeviceList(placeId: Int): List<ApiDevice> = wrapperError {
        return@wrapperError service.getPlaceDeviceList(getToken(), placeId)
    }

    override suspend fun getDeviceWorkOrderList(
        deviceId: Int,
        type: Int
    ): List<ApiWorkOrderStatus> = wrapperError {
        return@wrapperError service.getDeviceWorkOrderList(getToken(), deviceId, type)
    }

    override suspend fun updatePlaceDevicePermission(data: List<UpdatePlaceDevicePermissionParam>): Boolean =
        wrapperError {
            return@wrapperError service.updatePlaceDevicePermission(getToken(), data)
        }

    override suspend fun cancelWorkOrder(id: Int): Boolean = wrapperError {
        return@wrapperError service.cancelWorkOrder(getToken(), CancelWorkOrderParam(id))
    }

    override suspend fun getWorkOrderDetail(workOrderId: Int): ApiWorkOrderDetail = wrapperError {
        return@wrapperError service.getWorkOrderDetail(getToken(), workOrderId)
    }

    override suspend fun getDeviceDetail(deviceId: Int): ApiDeviceDetail = wrapperError {
        return@wrapperError service.getDeviceDetail(getToken(), deviceId)
    }

    override suspend fun getVendorInfo(vendorId: Int): ApiVendorInfo = wrapperError {
        return@wrapperError service.getVendorInfo(getToken(), vendorId)
    }

    override suspend fun getVendorList(): List<ApiVendorInfo> = wrapperError {
        return@wrapperError service.getVendorList(getToken())
    }

    override suspend fun getWarranty(deviceId: Int): ApiWarranty = wrapperError {
        return@wrapperError service.getWarranty(getToken(), deviceId)
    }

    override suspend fun getEngineerWorkOrderList(year: Int, month: Int): List<ApiTaskSummary> =
        wrapperError {
            return@wrapperError service.getEngineerWorkOrderList(getToken(), year, month)
        }

    override suspend fun uploadWorkOrderEngineerImage(
        workOrderId: Int,
        kind: Int,
        file: File
    ): String = wrapperError {
        val data = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        return@wrapperError service.uploadWorkOrderEngineerImage(
            getToken(),
            workOrderId,
            kind,
            data
        )
    }

    override suspend fun doneWorkOrder(data: DoneWorkOrderParam): Boolean = wrapperError {
        return@wrapperError service.doneWorkOrder(getToken(), data)
    }

    override suspend fun uploadSignImage(workOrderId: Int, file: File): String = wrapperError {
        val data = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        return@wrapperError service.uploadSignImage(getToken(), workOrderId, data)
    }

    override suspend fun updateWorkTime(workOrderId: Int): Boolean = wrapperError {
        return@wrapperError service.updateWorkTime(getToken(), workOrderId)
    }

    override suspend fun getFilterList(deviceId: Int): List<ApiDeviceFilter> = wrapperError {
        return@wrapperError service.getFilterList(getToken(), deviceId)
    }

    override suspend fun changeFilter(data: ChangeFilterParam): Boolean = wrapperError {
        return@wrapperError service.changeFilter(getToken(), data)
    }

    override suspend fun getMsgList(): List<ApiMsg> = wrapperError {
        return@wrapperError service.getMsgList(getToken())
    }

    override suspend fun getInviteList(): List<ApiInvite> = wrapperError {
        return@wrapperError service.getInviteList(getToken())
    }

    override suspend fun acceptOrRejectInvite(deviceId: Int, accept: Boolean): Boolean =
        wrapperError {
            return@wrapperError service.acceptOrRejectInvite(
                getToken(),
                deviceId,
                if (accept) 1 else 0
            )
        }

    override suspend fun uploadFcmToken(token: String): Boolean = wrapperError {
        val param = UploadFcmTokenParam(token)
        return@wrapperError service.uploadFcmToken(getToken(), param)
    }

    override suspend fun getTaskHistory(month: Int): List<ApiTaskSummary> = wrapperError {
        return@wrapperError service.getTaskHistory(getToken(), month)
    }

    override suspend fun googleLogin(idToken: String): LoginResponse = wrapperError {

        val result = service.googleLogin(idToken)
        if (result.isSuccess()) {
            val longTimeToken = service.makeLongTimeToken(result.token)
            saveToken(longTimeToken.token, "")
        }

        return@wrapperError result
    }

    override suspend fun appleLogin(authCode: String): LoginResponse = wrapperError {
        val result = service.appleLogin("https://service.cloud-nest.com/LoginAPI/DoAppleLogin", authCode)
        if (result.isSuccess()) {
            val longTimeToken = service.makeLongTimeToken(result.token)
            saveToken(longTimeToken.token, "")
        }

        return@wrapperError result
    }

    override suspend fun getInstallRecordList(month: Int): List<ApiInstallRecord> = wrapperError {
        return@wrapperError service.getInstallRecordList(getToken(), month)
    }

    override suspend fun setDeviceValue(deviceId: Int, id: String, value: String): Boolean =
        wrapperError {
            return@wrapperError service.setDeviceValue(
                getToken(),
                deviceId,
                SetDeviceValueParam(id, value)
            )
        }

    override suspend fun setAreaDeviceValue(areaId: Int, id: String, value: String): Boolean =
        wrapperError {
            return@wrapperError service.setAreaDeviceValue(
                getToken(),
                areaId,
                SetAreaDeviceValueParam(id, value)
            )
        }

    override suspend fun getHotTempOptions(): List<String> = wrapperError {
        return@wrapperError service.getHotTempOptions(getToken())
    }

    override suspend fun getColdTempOptions(): List<String> = wrapperError {
        return@wrapperError service.getColdTempOptions(getToken())
    }

    override suspend fun getPlaceList(): List<ApiPlace> = wrapperError {
        return@wrapperError service.getPlaceList(getToken())
    }

    override suspend fun getDeviceUnderPlaceArea(placeId: Int, areaId: Int): List<ApiDevice> =
        wrapperError {
            return@wrapperError service.getDeviceUnderPlaceArea(getToken(), placeId, areaId)
        }

    override suspend fun searchDevice(mac: String): ApiDeviceByMAC = wrapperError {
        return@wrapperError service.searchDevice(getToken(), mac)
    }

    override suspend fun getCustomerList(): List<ApiCustomer> = wrapperError {
        return@wrapperError service.getCustomerList(getToken())
    }

    override suspend fun updateWarranty(
        deviceId: Int,
        name: String,
        email: String,
        phone: String,
        date: LocalDate
    ): Boolean = wrapperError {
        val param = UpdateWarrantyParam(
            deviceId,
            name,
            email,
            phone,
            date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
        return@wrapperError service.updateWarranty(getToken(), param)
    }

    override suspend fun pairDeviceUser(
        sn: String?,
        mac: String,
        name: String,
        placeId: Int?,
        areaId: Int?,
        customerId: Int?,
        customerAddress: String?,
    ): Boolean = wrapperError{
        val param = PairDeviceUserParam(sn, mac, name, placeId, areaId, customerId, customerAddress)
        return@wrapperError service.pairDeviceUser(getToken(), param)
    }

    override suspend fun addDeviceManually(param: AddDeviceManualParam): Int = wrapperError {
        return@wrapperError service.addDeviceManually(getToken(), param)
    }

    override suspend fun uploadInstallPhoto(deviceId: Int, kind: Int, file: File): String = wrapperError {
        val data = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        return@wrapperError service.uploadInstallPhoto(getToken(), deviceId, kind, data)
    }

    override suspend fun uploadInstallForm(
        deviceId: Int,
        formOption: List<ApiFormOptionField>
    ): Boolean = wrapperError {
        return@wrapperError service.uploadInstallForm(getToken(), UploadInstallFormParam(deviceId, formOption))
    }

    override suspend fun getPlaceDevicePermission(
        placeId: Int,
        userId: Int
    ): List<ApiPlaceDevicePermission> = wrapperError {
        return@wrapperError service.getPlaceDevicePermission(getToken(), placeId, userId)
    }

    override suspend fun getDeviceScheduleList(deviceId: Int): List<ApiDeviceSchedule> = wrapperError {
        return@wrapperError service.getDeviceScheduleList(getToken(), deviceId)
    }

    override suspend fun getAreaScheduleList(areaId: Int): List<ApiAreaSchedule> = wrapperError {
        return@wrapperError service.getAreaScheduleList(getToken(), areaId)
    }

    override suspend fun getDeviceSchedule(qId: Int): List<ApiDeviceSchedule> = wrapperError {
        return@wrapperError service.getDeviceSchedule(getToken(), qId)
    }

    override suspend fun createDeviceSchedule(data: CreateScheduleParam): Int = wrapperError {
        return@wrapperError service.createDeviceSchedule(getToken(), data)
    }

    override suspend fun editDeviceSchedule(data: ApiEditScheduleParam): Boolean = wrapperError {
        return@wrapperError service.editDeviceSchedule(getToken(), data)
    }

    override suspend fun deleteDeviceSchedule(qId: Int): Boolean = wrapperError {
        return@wrapperError service.deleteDeviceSchedule(getToken(), qId)
    }

    override suspend fun getAreaSchedule(qId: Int): ApiAreaSchedule = wrapperError {
        return@wrapperError service.getAreaSchedule(getToken(), qId)
    }

    override suspend fun createAreaSchedule(data: CreateAreaScheduleParam): Int = wrapperError {
        return@wrapperError service.createAreaSchedule(getToken(), data)
    }

    override suspend fun editAreaSchedule(data: ApiEditScheduleParam): Boolean = wrapperError {
        return@wrapperError service.editAreaSchedule(getToken(), data)
    }

    override suspend fun deleteAreaSchedule(qId: Int): Boolean = wrapperError {
        return@wrapperError service.deleteAreaSchedule(getToken(), qId)
    }

    override suspend fun getPlaceDataAnalysis(placeId: Int, year: Int, month: Int) = wrapperError {
        return@wrapperError service.getPlaceDataAnalysis(getToken(), placeId, year, month)
    }

    override suspend fun getDeviceStatistics(deviceId: Int): ApiDeviceStatistics = wrapperError {
        return@wrapperError service.getDeviceStatistics(getToken(), deviceId)
    }

    override suspend fun transferOwner(account: String, ids: List<Int>): Boolean = wrapperError{
        return@wrapperError service.transferOwner(getToken(), TransferOwnerParam(account, ids))
    }

    override suspend fun quitDevice(deviceId: Int): Boolean = wrapperError {
        return@wrapperError service.quitDevice(getToken(), deviceId)
    }

    private suspend fun <T> wrapperError(block: suspend () -> T): T {

        return try {
            block.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is UnknownHostException,
                is SocketTimeoutException -> {
                    globalErrorHandler.onNetworkUnavailable()
                    throw NoNetworkException()
                }
                is ConnectException -> {
                    globalErrorHandler.onNetworkUnavailable()
                    throw ServerErrorException()
                }
                is HttpException -> {
                    val statusCode = e.code()
                    if (statusCode != 401) {
                        val response = e.response()
                        Timber.e("url : ${response?.raw()?.request?.url?.toString()}")
                        val errorBody = response?.errorBody()?.string() ?: ""
                        errorBody.ifEmpty { throw e }
                        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        globalErrorHandler.onErrorMessage(errorResponse)
                        throw e
                    }
                    if (!refreshToken()) {
                        globalErrorHandler.onLoginSessionExpired()
                        throw SessionExpiredException()
                    }
                    return block.invoke()
                }
                else -> throw e
            }
        }

    }
}