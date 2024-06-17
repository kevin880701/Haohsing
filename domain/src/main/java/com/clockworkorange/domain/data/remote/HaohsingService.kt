package com.clockworkorange.domain.data.remote

import com.clockworkorange.domain.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface HaohsingService {

    @POST(ApiEndPoint.Login)
    suspend fun login(@Body param: LoginParam): LoginResponse

    @POST(ApiEndPoint.RefreshToken)
    suspend fun refreshToken(@Header("Authorization") token: String, @Body param: RefreshTokenParam): LoginResponse

    @GET(ApiEndPoint.LongTimeToken)
    suspend fun makeLongTimeToken(@Header("Authorization") token: String): LoginResponse

    @POST(ApiEndPoint.ForgetPassword)
    suspend fun forgetPassword(@Body param: ForgetPasswordParam): SimpleApiResult

    @POST(ApiEndPoint.UpdateUserPassword)
    suspend fun updateUserPassword(@Header("Authorization") token: String,@Body param: UpdateUserPasswordParam): SimpleApiResult

    @POST(ApiEndPoint.RegisterUserAccount)
    suspend fun register(@Body param: RegisterParam): SimpleApiResult

    @GET(ApiEndPoint.UserInfo)
    suspend fun getUserInfo(@Header("Authorization") token: String): UserInfoResponse

    @PUT(ApiEndPoint.UpdateUserInfo)
    suspend fun updateUserInfo(@Header("Authorization") token: String, @Body data: Map<String, String>): Boolean

    @Multipart
    @POST(ApiEndPoint.UpdateUserImage)
    suspend fun updateUserImage(@Header("Authorization") token: String, @Part data: MultipartBody.Part): String

    @GET(ApiEndPoint.CityList)
    suspend fun getCityList(): List<String>

    @GET(ApiEndPoint.RegionList)
    suspend fun getRegionList(@Query("countyname") city: String): List<String>

    @GET(ApiEndPoint.UserExist)
    suspend fun isAccountExit(@Query("account") account: String): Boolean

    @POST(ApiEndPoint.ResendActiveMail)
    suspend fun resendActiveMail(@Body param: ResendActiveMailParam): SimpleApiResult

    @GET(ApiEndPoint.Place)
    suspend fun getUserPlaceList(@Header("Authorization") token: String): List<UserPlaceResponse>

    @PUT(ApiEndPoint.Place)
    suspend fun updatePlaceName(@Header("Authorization") token: String, @Body data: UpdatePlaceNameParam):Boolean

    @POST(ApiEndPoint.Place)
    suspend fun addPlace(@Header("Authorization") token: String, @Body data: Map<String, String>): Int

    @DELETE(ApiEndPoint.Place)
    suspend fun deletePlace(@Header("Authorization") token: String, @Query("place_id") placeId: Int): Boolean

    @PUT(ApiEndPoint.Area)
    suspend fun updateAreaName(@Header("Authorization") token: String, @Body data: UpdateAreaNameParam): Boolean

    @POST(ApiEndPoint.Area)
    suspend fun addArea(@Header("Authorization") token: String,  @Body data: AddAreaParam): Int

    @DELETE(ApiEndPoint.Area)
    suspend fun deleteArea(@Header("Authorization") token: String, @Query("area_id") areaId: Int): Boolean

    @GET(ApiEndPoint.PlaceDetail)
    suspend fun getPlaceDetailList(@Header("Authorization") token: String): List<PlaceDetailResponse>

    @GET(ApiEndPoint.PlaceAreas)
    suspend fun getAreaList(@Header("Authorization") token: String, @Query("place_id") placeId: Int): List<AreaResponse>

    @GET(ApiEndPoint.WorkOrder)
    suspend fun getWorkOrderDetail(@Header("Authorization") token: String, @Query("m_id") workOrderId: Int):ApiWorkOrderDetail

    @POST(ApiEndPoint.WorkOrder)
    suspend fun addWorkOrder(@Header("Authorization") token: String, @Body data: Map<String, String>): AddWorkOrderResponse

    @PUT(ApiEndPoint.WorkOrder)
    suspend fun editWorkOrder(@Header("Authorization") token: String, @Body data: ApiEditWorkOrderParam): Boolean

    @Multipart
    @PUT(ApiEndPoint.WorkOrderUserImage)
    suspend fun uploadWorkOrderImage(@Header("Authorization") token: String, @Query("m_id") workOrderId: Int, @Part data: MultipartBody.Part): String

    @GET(ApiEndPoint.WorkOrderOption)
    suspend fun getWorkOrderOption(@Header("Authorization") token: String, @Query("type") type: Int): List<WorkOrderReasonResponse>

    @GET(ApiEndPoint.DeviceUserList)
    suspend fun getUserDeviceList(@Header("Authorization") token: String): List<ApiDevice>

    @PUT(ApiEndPoint.UpdateDeviceName)
    suspend fun updateDeviceName(@Header("Authorization") token: String, @Body data: UpdateDeviceNameParam): Boolean

    @GET(ApiEndPoint.DeviceShareMemberList)
    suspend fun getDeviceShareMemberList(@Header("Authorization") token: String, @Query("device_id") deviceId: Int): List<ApiMember>

    @POST(ApiEndPoint.ShareDevice)
    suspend fun shareDevice(@Header("Authorization") token: String, @Body data: ShareDeviceParam): Boolean

    @DELETE(ApiEndPoint.ShareDevice)
    suspend fun deleteDeviceShareMember(@Header("Authorization") token: String, @Query("user_id") userId:Int, @Query("device_id") deviceId: Int): Boolean

    @GET(ApiEndPoint.PlaceShareMemberList)
    suspend fun getPlaceShareMemberList(@Header("Authorization") token: String, @Query("place_id") placeId:Int): List<ApiPlaceMember>

    @POST(ApiEndPoint.SharePlaceDevice)
    suspend fun sharePlaceDevice(@Header("Authorization") token: String, @Body data: SharePlaceDeviceParam): Boolean

    @DELETE(ApiEndPoint.SharePlaceDevice)
    suspend fun deletePlaceDeviceShareMember(@Header("Authorization") token: String, @Query("user_id") userId:Int, @Query("place_id") placeId: Int): Boolean

    @GET(ApiEndPoint.DeviceUserPlaceList)
    suspend fun getPlaceDeviceList(@Header("Authorization") token: String, @Query("place_id") placeId: Int): List<ApiDevice>

    @GET(ApiEndPoint.DeviceWorkOrderList)
    suspend fun getDeviceWorkOrderList(@Header("Authorization") token: String, @Query("device_id") deviceId: Int, @Query("type") type: Int): List<ApiWorkOrderStatus>

    @PUT(ApiEndPoint.PlaceDevice)
    suspend fun updatePlaceDevicePermission(@Header("Authorization") token: String, @Body data: List<UpdatePlaceDevicePermissionParam>): Boolean

    @GET(ApiEndPoint.PlaceDevice)
    suspend fun getPlaceDevicePermission(@Header("Authorization") token: String, @Query("place_id") placeId: Int, @Query("user_id") userId: Int): List<ApiPlaceDevicePermission>

    @PUT(ApiEndPoint.WorkOrderDone)
    suspend fun cancelWorkOrder(@Header("Authorization") token: String, @Body data: CancelWorkOrderParam): Boolean

    @GET(ApiEndPoint.Device)
    suspend fun getDeviceDetail(@Header("Authorization") token: String, @Query("device_id") deviceId: Int): ApiDeviceDetail

    @GET(ApiEndPoint.Vendor)
    suspend fun getVendorInfo(@Header("Authorization") token: String, @Query("vendor_id") vendorId: Int): ApiVendorInfo

    @GET(ApiEndPoint.VendorList)
    suspend fun getVendorList(@Header("Authorization") token: String): List<ApiVendorInfo>

    @GET(ApiEndPoint.Warranty)
    suspend fun getWarranty(@Header("Authorization") token: String, @Query("device_id") deviceId: Int): ApiWarranty

    @PUT(ApiEndPoint.DevicePlace)
    suspend fun updateDevicePlaceArea(@Header("Authorization") token: String, @Body data: UpdateDevicePlaceAreaParam): Boolean

    @GET(ApiEndPoint.EngineerWorkOrder)
    suspend fun getEngineerWorkOrderList(@Header("Authorization") token: String, @Query("year") year: Int, @Query("month") month: Int): List<ApiTaskSummary>

    @Multipart
    @PUT(ApiEndPoint.WorkOrderEngineerImage)
    suspend fun uploadWorkOrderEngineerImage(
        @Header("Authorization") token: String,
        @Query("m_id") workOrderId: Int,
        @Query("kind") kind: Int,
        @Part data: MultipartBody.Part
    ): String

    @PUT(ApiEndPoint.DoneWorkOrder)
    suspend fun doneWorkOrder(@Header("Authorization") token: String, @Body data: DoneWorkOrderParam): Boolean

    @Multipart
    @PUT(ApiEndPoint.UploadSignImage)
    suspend fun uploadSignImage(@Header("Authorization") token: String, @Query("m_id") workOrderId: Int, @Part data: MultipartBody.Part): String

    @PUT(ApiEndPoint.WorkTime)
    suspend fun updateWorkTime(@Header("Authorization") token: String, @Query("m_id") workOrderId: Int): Boolean

    @GET(ApiEndPoint.DeviceFilter)
    suspend fun getFilterList(@Header("Authorization") token: String, @Query("device_id") deviceId: Int): List<ApiDeviceFilter>

    @PUT(ApiEndPoint.DeviceFilter)
    suspend fun changeFilter(@Header("Authorization") token: String, @Body data: ChangeFilterParam): Boolean

    @GET(ApiEndPoint.Msg)
    suspend fun getMsgList(@Header("Authorization") token: String): List<ApiMsg>

    @GET(ApiEndPoint.InviteList)
    suspend fun getInviteList(@Header("Authorization") token: String): List<ApiInvite>

    @PUT(ApiEndPoint.AcceptOrReject)
    suspend fun acceptOrRejectInvite(@Header("Authorization") token: String, @Query("device_id") deviceId: Int, @Query("accept") accept: Int): Boolean

    @POST(ApiEndPoint.FcmToken)
    suspend fun uploadFcmToken(@Header("Authorization") token: String, @Body data: UploadFcmTokenParam): Boolean

    @GET(ApiEndPoint.TaskHistory)
    suspend fun getTaskHistory(@Header("Authorization") token: String, @Query("period") month: Int): List<ApiTaskSummary>

    @GET(ApiEndPoint.GoogleLogin)
    suspend fun googleLogin(@Query("id_token") idToken: String): LoginResponse

    @POST(ApiEndPoint.UserLogout)
    suspend fun logout(@Header("Authorization") token: String, @Body data: LogoutParam): Boolean

    @GET(ApiEndPoint.DeviceInstallRecord)
    suspend fun getInstallRecordList(@Header("Authorization") token: String, @Query("period") period: Int): List<ApiInstallRecord>

    @POST(ApiEndPoint.SetDeviceValue)
    suspend fun setDeviceValue(@Header("Authorization") token: String, @Path("device_id") deviceId: Int, @Body data: SetDeviceValueParam): Boolean

    @POST(ApiEndPoint.SetAreaDeviceValue)
    suspend fun setAreaDeviceValue(@Header("Authorization") token: String, @Query("area_id") areaId: Int, @Body data: SetAreaDeviceValueParam): Boolean

    @GET(ApiEndPoint.HotTempOptions)
    suspend fun getHotTempOptions(@Header("Authorization") token: String): List<String>

    @GET(ApiEndPoint.IceTempOptions)
    suspend fun getColdTempOptions(@Header("Authorization") token: String): List<String>

    @GET(ApiEndPoint.Place)
    suspend fun getPlaceList(@Header("Authorization") token: String): List<ApiPlace>

    @GET(ApiEndPoint.DeviceUserPlaceAreaList)
    suspend fun getDeviceUnderPlaceArea(@Header("Authorization") token: String, @Query("place_id") placeId: Int, @Query("area_id") areaId: Int): List<ApiDevice>

    @GET(ApiEndPoint.ScanQECode)
    suspend fun searchDevice(@Header("Authorization") token: String, @Query("mac") mac: String): ApiDeviceByMAC

    @GET(ApiEndPoint.GetCustomers)
    suspend fun getCustomerList(@Header("Authorization") token: String): List<ApiCustomer>

    @PUT(ApiEndPoint.Warranty)
    suspend fun updateWarranty(@Header("Authorization") token: String, @Body data: UpdateWarrantyParam): Boolean

    @POST(ApiEndPoint.PairDevice)
    suspend fun pairDeviceUser(@Header("Authorization") token: String, @Body data: PairDeviceUserParam): Boolean

    @POST(ApiEndPoint.ManualAddDevice)
    suspend fun addDeviceManually(@Header("Authorization") token: String, @Body data: AddDeviceManualParam): Int

    @Multipart
    @PUT(ApiEndPoint.UploadInstallPhoto)
    suspend fun uploadInstallPhoto(@Header("Authorization") token: String, @Query("device_id") deviceId: Int, @Query("kind") kind: Int, @Part data: MultipartBody.Part): String

    @PUT(ApiEndPoint.UploadInstallForm)
    suspend fun uploadInstallForm(@Header("Authorization") token: String, @Body data: UploadInstallFormParam): Boolean

    @POST
    suspend fun appleLogin(@Url url: String, @Query("code") authCode: String): LoginResponse

    @GET(ApiEndPoint.DevicePowerScheduleList)
    suspend fun getDeviceScheduleList(@Header("Authorization") token: String, @Query("device_id") deviceId: Int): List<ApiDeviceSchedule>

    @GET(ApiEndPoint.AreaPowerScheduleList)
    suspend fun getAreaScheduleList(@Header("Authorization") token: String, @Query("area_id") areaId: Int): List<ApiAreaSchedule>

    @GET(ApiEndPoint.DeviceSchedule)
    suspend fun getDeviceSchedule(@Header("Authorization") token: String, @Query("q_id") qId: Int): List<ApiDeviceSchedule>

    @POST(ApiEndPoint.DeviceSchedule)
    suspend fun createDeviceSchedule(@Header("Authorization") token: String, @Body data: CreateScheduleParam): Int

    @PUT(ApiEndPoint.DeviceSchedule)
    suspend fun editDeviceSchedule(@Header("Authorization") token: String, @Body data: ApiEditScheduleParam): Boolean

    @DELETE(ApiEndPoint.DeviceSchedule)
    suspend fun deleteDeviceSchedule(@Header("Authorization") token: String, @Query("q_id") qId: Int): Boolean

    @GET(ApiEndPoint.AreaSchedule)
    suspend fun getAreaSchedule(@Header("Authorization") token: String, @Query("q_id") qId: Int): ApiAreaSchedule

    @POST(ApiEndPoint.AreaSchedule)
    suspend fun createAreaSchedule(@Header("Authorization") token: String, @Body data: CreateAreaScheduleParam): Int

    @PUT(ApiEndPoint.AreaSchedule)
    suspend fun editAreaSchedule(@Header("Authorization") token: String, @Body data: ApiEditScheduleParam): Boolean

    @DELETE(ApiEndPoint.AreaSchedule)
    suspend fun deleteAreaSchedule(@Header("Authorization") token: String, @Query("q_id") qId: Int): Boolean

    @GET(ApiEndPoint.PlaceDataAnalysis)
    suspend fun getPlaceDataAnalysis(@Header("Authorization") token: String, @Query("place_id") placeId: Int, @Query("year") year: Int, @Query("month") month: Int): ApiPlaceStatistics

    @GET(ApiEndPoint.DeviceDataAnalysis)
    suspend fun getDeviceStatistics(@Header("Authorization") token: String, @Query("device_id") placeId: Int): ApiDeviceStatistics

    @POST(ApiEndPoint.TransferOwner)
    suspend fun transferOwner(@Header("Authorization") token: String, @Body data: TransferOwnerParam): Boolean

    @DELETE(ApiEndPoint.Device)
    suspend fun quitDevice(@Header("Authorization") token: String, @Query("device_id") deviceId: Int): Boolean

    companion object{

        fun create(enableLog: Boolean = false): HaohsingService {

            val builder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

            if (enableLog){
                val httpLoggingInterceptor = MyHttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(MyHttpLoggingInterceptor.Level.BODY)
                builder.addInterceptor(httpLoggingInterceptor)
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(ApiEndPoint.BASE_URL)
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(HaohsingService::class.java)
        }
    }
}