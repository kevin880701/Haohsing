package com.clockworkorange.domain.data.remote


object ApiEndPoint {

    const val DOMAIN = "https://cipw.cwoiot.com:8180"
    const val BASE_URL = "$DOMAIN/cipw/"

    //device
    const val ShareDevice = "v1/device/share"
    const val PairDevice = "v1/device/register/user"
    const val ManualAddDevice = "v1/device/register/manual"     // for 手動新增裝置
    const val Device = "v1/device"
    const val DeviceUserList = "v1/device/user"
    const val DevicePlace = "v1/device/place"
    const val DeviceUserPlaceList = "v1/device/user/place"
    const val DeviceUserPlaceAreaList = "v1/device/user/place/area"
    const val UpdateDeviceName = "v1/device/name"
    const val DeviceShareMemberList = "v1/device/share/users"
    const val PlaceShareMemberList = "v1/device/place/users"
    const val SharePlaceDevice = "v1/device/share/place"
    const val PlaceDevice = "v1/device/place/devices"
    const val Warranty = "v1/device/warranty"
    const val DeviceFilter = "v1/device/filter"
    const val DeviceInstallRecord = "v1/device/engineer"
    const val SetDeviceValue = "v1/device/{device_id}/value"
    const val SetAreaDeviceValue = "v1/device/area/value"
    const val HotTempOptions = "v1/device/droplist/hots"
    const val IceTempOptions = "v1/device/droplist/ices"
    const val ScanQECode = "v1/device/scan"
    const val DevicePowerScheduleList = "v1/device/q/query"
    const val AreaPowerScheduleList = "v1/device/q/area/query"
    const val DeviceSchedule = "v1/device/q"
    const val AreaSchedule = "v1/device/q/area"
    const val TransferOwner = "v1/device/transfer/owner"

    //invite
    const val InviteList = "v1/device/shares"
    const val AcceptOrReject = "v1/device/share"

    //account
    const val UpdateUserInfo = "v1/user/upd"
    const val UpdateUserPassword = "v1/user/upd/password"
    const val UpdateUserImage = "v1/user/upd/headshot"
    const val RefreshToken = "v1/user/token/refresh"
    const val Login = "v1/user/login"
    const val ForgetPassword = "v1/user/forget/password"
    const val RegisterUserAccount = "v1/user/add"
    const val UserInfo = "v1/user"
    const val UserExist = "v1/user/exist"
    const val ResendActiveMail = "v1/user/resendActiveMail"
    const val LongTimeToken = "v1/user/token/longtime"
    const val FcmToken = "v1/user/firebase"
    const val GoogleLogin = "v1/user/login/google"
    const val UserLogout = "v1/user/logout"

    //zip code
    const val CityList = "v1/zipcode/cities"
    const val RegionList = "v1/zipcode/regions"

    //work order 保修
    const val WorkOrder = "v1/m"
    const val WorkTime = "v1/m/work"
    const val WorkOrderUserImage = "v1/m/user/image"
    const val WorkOrderOption = "v1/m/form"
    const val DeviceWorkOrderList = "v1/m/device"
    const val WorkOrderDone = "v1/m/done"
    const val EngineerWorkOrder = "v1/m/engineer"
    const val DoneWorkOrder = "v1/m/done"
    const val WorkOrderEngineerImage = "v1/m/engineer/image"
    const val UploadSignImage = "v1/m/sign/image"
    const val TaskHistory = "v1/m/engineer/period"

    const val UploadInstallPhoto = "v1/device/register/image"
    const val UploadInstallForm = "v1/device/register/form"

    //place area
    const val Place = "v1/place"
    const val Area = "v1/place/area"
    const val PlaceDetail = "v1/place/dtls"
    const val PlaceAreas = "v1/place/areas"

    //經銷商
    const val Vendor = "v1/v"
    const val VendorList = "v1/v/query"

    //顧客
    const val GetCustomers = "v1/c/query"

    //msg
    const val Msg = "v1/msg"

    // 統計
    const val PlaceDataAnalysis = "v1/s/place"
    const val DeviceDataAnalysis = "v1/s/device"

}