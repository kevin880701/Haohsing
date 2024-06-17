package com.clockworkorange.domain.data

import com.clockworkorange.domain.data.remote.ApiEndPoint
import com.clockworkorange.domain.data.remote.model.ApiAreaHourUsage
import com.clockworkorange.domain.data.remote.model.ApiAreaSchedule
import com.clockworkorange.domain.data.remote.model.ApiAreaValue
import com.clockworkorange.domain.data.remote.model.ApiCustomer
import com.clockworkorange.domain.data.remote.model.ApiDevice
import com.clockworkorange.domain.data.remote.model.ApiDeviceByMAC
import com.clockworkorange.domain.data.remote.model.ApiDeviceDetail
import com.clockworkorange.domain.data.remote.model.ApiDeviceFilter
import com.clockworkorange.domain.data.remote.model.ApiDeviceSchedule
import com.clockworkorange.domain.data.remote.model.ApiDeviceStatistics
import com.clockworkorange.domain.data.remote.model.ApiDeviceStatus
import com.clockworkorange.domain.data.remote.model.ApiHourValue
import com.clockworkorange.domain.data.remote.model.ApiInstallRecord
import com.clockworkorange.domain.data.remote.model.ApiInvite
import com.clockworkorange.domain.data.remote.model.ApiMember
import com.clockworkorange.domain.data.remote.model.ApiMsg
import com.clockworkorange.domain.data.remote.model.ApiPlace
import com.clockworkorange.domain.data.remote.model.ApiPlaceMember
import com.clockworkorange.domain.data.remote.model.ApiPlaceStatistics
import com.clockworkorange.domain.data.remote.model.ApiTaskSummary
import com.clockworkorange.domain.data.remote.model.ApiVendorInfo
import com.clockworkorange.domain.data.remote.model.ApiWarranty
import com.clockworkorange.domain.data.remote.model.ApiWorkOrderDetail
import com.clockworkorange.domain.data.remote.model.ApiWorkOrderStatus
import com.clockworkorange.domain.data.remote.model.AreaResponse
import com.clockworkorange.domain.data.remote.model.PlaceDetailResponse
import com.clockworkorange.domain.data.remote.model.UserInfoResponse
import com.clockworkorange.domain.entity.AdvancedSetting
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.Device
import com.clockworkorange.domain.entity.DeviceDetail
import com.clockworkorange.domain.entity.DeviceSetting
import com.clockworkorange.domain.entity.DeviceStatus
import com.clockworkorange.domain.entity.DeviceValue
import com.clockworkorange.domain.entity.FilterStatus
import com.clockworkorange.domain.entity.ManufacturerInfo
import com.clockworkorange.domain.entity.ModelInfo
import com.clockworkorange.domain.entity.OwnerInfo
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.domain.entity.PowerSchedule
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.entity.Warranty
import com.clockworkorange.domain.entity.WorkOrderRequirement
import com.clockworkorange.domain.usecase.customer.CustomerAgency
import com.clockworkorange.domain.usecase.device.AreaHourUsage
import com.clockworkorange.domain.usecase.device.AreaValue
import com.clockworkorange.domain.usecase.device.DayMonthUsage
import com.clockworkorange.domain.usecase.device.DeviceStatistics
import com.clockworkorange.domain.usecase.device.HourValue
import com.clockworkorange.domain.usecase.device.PlaceStatistics
import com.clockworkorange.domain.usecase.device.SearchedDevice
import com.clockworkorange.domain.usecase.device.ShareMember
import com.clockworkorange.domain.usecase.notification.Invite
import com.clockworkorange.domain.usecase.notification.Notification
import com.clockworkorange.domain.usecase.palcearea.PlaceShareMember
import com.clockworkorange.domain.usecase.palcearea.PlaceSummary
import com.clockworkorange.domain.usecase.task.ConfirmRecord
import com.clockworkorange.domain.usecase.task.CustomerInfo
import com.clockworkorange.domain.usecase.task.EngineerInfo
import com.clockworkorange.domain.usecase.task.FormOption
import com.clockworkorange.domain.usecase.task.InstallRecord
import com.clockworkorange.domain.usecase.task.MaintainRecord
import com.clockworkorange.domain.usecase.task.RepairRecord
import com.clockworkorange.domain.usecase.task.TaskDetail
import com.clockworkorange.domain.usecase.task.TaskStatus
import com.clockworkorange.domain.usecase.task.TaskStatusCategory
import com.clockworkorange.domain.usecase.task.TaskSummary
import com.clockworkorange.domain.usecase.vendor.Vendor
import com.clockworkorange.domain.usecase.workorder.FinishedWorkOrderDetail
import com.clockworkorange.domain.usecase.workorder.PendingWorkOrderDetail
import com.clockworkorange.domain.usecase.workorder.ScheduledWorkOrderDetail
import com.clockworkorange.domain.usecase.workorder.WorkOrderInfo
import com.clockworkorange.domain.usecase.workorder.WorkOrderStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun ApiPlaceStatistics.toPlaceStatistics(): PlaceStatistics {
    val filterCompareResult = when {
        filterLife == null || filterLifeAverage == null -> ""
        filterLife > filterLifeAverage -> ">"
        filterLife == filterLifeAverage -> "="
        else -> "<"
    } + "地區平均"
    return PlaceStatistics(
        deviceCount ?: 0,
        filterLife ?: 0,
        filterCompareResult,
        waterDayMonthUsage = DayMonthUsage(
            waterToday ?: 0,
            waterTodayCompare ?: 0,
            waterMonth ?: 0,
            waterMonthCompare ?: 0,
        ),
        waterAreas?.map { it.toAreaValue() },
        powerDayMonthUsage = if (isPowerDayMonthNotExist()) null else
            DayMonthUsage(
                powerToday ?: 0,
                powerTodayCompare ?: 0,
                powerMonth ?: 0,
                powerMonthCompare ?: 0,
            ),
        powerAreas.map { it.toAreaValue() },
        waterAreaHours.map { it.toAreaHourUsage() },
        powerAreaHours.map { it.toAreaHourUsage() }
    )
}

fun ApiDeviceStatistics.toDeviceStatistics(): DeviceStatistics {
    val waterHourUsage = waterAreaHours.map { it.toAreaHourUsage() }.firstOrNull()
    val powerHourUsage = powerAreaHours.map { it.toAreaHourUsage() }.firstOrNull()
    val suggestMsg = StringBuffer().apply {
        waterHourUsage?.suggest?.forEach { append(it) }
        powerHourUsage?.suggest?.forEach { append(it) }
    }.toString().takeIf { it.isNotEmpty() }

    return DeviceStatistics(
        powerDayMonthUsage = if (isPowerDayMonthNotExist()) null else {
            DayMonthUsage(
                powerToday,
                powerTodayCompare ?: 0,
                powerMonth,
                powerMonthCompare ?: 0
            ) },
        waterDayMonthUsage = DayMonthUsage(waterToday, waterTodayCompare ?: 0, waterMonth, waterMonthCompare ?: 0),
        waterHourValues = waterHourUsage?.hourValues,
        powerHourValues = powerHourUsage?.hourValues,
        suggest = suggestMsg,
    )
}

fun ApiAreaHourUsage.toAreaHourUsage(): AreaHourUsage {
    return AreaHourUsage(areaId, name, hours.map { it.toHourValue() }, suggests)
}

fun ApiHourValue.toHourValue(): HourValue {
    return HourValue(hour, value)
}

fun ApiAreaValue.toAreaValue(): AreaValue {
    return AreaValue(areaId, name, value)
}

fun UserInfoResponse.toUserInfo(): UserInfo{
    return UserInfo(
        userId,
        name ?: "",
        account,
        headshot ?: "",
        tel ?: "",
        city ?: "",
        region ?: "",
        UserInfo.Role.fromName(rolesName),
        vendorName,
        vendorId
    )
}

fun PlaceDetailResponse.toPlaceSummary(): PlaceSummary {
    return PlaceSummary(placeId, name, areaCount, shareCount)
}

fun AreaResponse.toAreaInfo(): AreaInfo {
    return AreaInfo(areaId, name, placeId)
}

fun ApiDevice.toDevice(): Device{

    val placeInfo = try {
        PlaceInfo(placeId, placeName)
    }catch (e: Exception){
        PlaceInfo.ALL
    }

    val areaInfo: AreaInfo? = try {
        AreaInfo(areaId, areaName, placeId)
    }catch (e: Exception){
        AreaInfo.ALL
    }

    // 未連線 → 關機 → 異常 → 正常
    val devStatus = when {
        online == 0 -> DeviceStatus.Offline
        power == 0 -> DeviceStatus.Off
        error == 1 -> DeviceStatus.Error()
        else -> DeviceStatus.Normal
    }

    return Device(
        deviceId,
        name ?: "",
        placeInfo,
        areaInfo,
        ModelInfo(modelId, modelName, modelImageUrl),
        devStatus,
        type == 0,
        mac,
        installationDate?.let { LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").parse(it)) }
    )
}

fun ApiDeviceDetail.toDeviceDetail(): DeviceDetail{

    val placeInfo = try {
        PlaceInfo(placeId, placeName)
    }catch (e: Exception){
        PlaceInfo.ALL
    }

    val areaInfo: AreaInfo? = try {
        AreaInfo(areaId, areaName, placeId)
    }catch (e: Exception){
        AreaInfo.ALL
    }

    val deviceStatus = if (online == 1) DeviceStatus.Normal else DeviceStatus.Offline

    return DeviceDetail(
        deviceId,
        mac,
        name ?: "",
        installAddress,
        placeInfo,
        areaInfo,
        ModelInfo(modelId, modelName, modelImageUrl),
        deviceStatus,
        type == 0,
        OwnerInfo(customerName ?: "", customerAddress ?: ""),
        statistics?.extraDeviceValue(),
        statistics?.extraDeviceSetting() ?: emptyList(),
        statistics?.extraAdvancedSetting(),
        vendorName,
        vendorId,
        schedules = (deviceSchedules?.map { it.toPowerSchedule() } ?: listOf()) + (areaSchedules?.map { it.toPowerSchedule() } ?: listOf()),
        noMaintainTime,
        manualUrl,
        statistics?.h2en
    )
}

fun ApiDeviceStatus.extraDeviceValue(): DeviceValue{

    val waterLevel = when(h12a){
        "高" -> DeviceValue.WaterLeveL.High
        "正常" -> DeviceValue.WaterLeveL.Middle
        "低" -> DeviceValue.WaterLeveL.Low
        else -> DeviceValue.WaterLeveL.UnKnown
    }

    return DeviceValue(
        waterLevel,
        0,
        0,
        h0a,
        h08,
        h09
    )
}


fun ApiDeviceStatus.extraDeviceSetting(): List<DeviceSetting>{
    val settingList = mutableListOf<DeviceSetting>()
    settingList.add(DeviceSetting("h00", "電源", h00 == 1))
    settingList.add(DeviceSetting("h03", "熱水出水解鎖", h03 == 1))
    settingList.add(DeviceSetting("h04", "製冷器啟動", h04 == 1))
    settingList.add(DeviceSetting("p01", "語音功能", p01 == 1))

    return settingList
}

fun ApiDeviceStatus.extraAdvancedSetting(): AdvancedSetting{
    val regex = Regex("(\\d{1,2})\\/(\\d{1,2})")

    var sleepTimerEnable: Boolean = false
    var sleepTimerHour: String? = null
    var sleepTimerMin: String? = null
    var powerOnTimerEnable: Boolean = false
    var powerOnTimerHour: String? = null
    var powerOnTimerMin: String? = null

    if (h25 != null && regex.matches(h25)){
        val matchResult = regex.find(h25)
        val groupValues = matchResult?.groupValues
        val hourString = groupValues?.get(1)
        val minString = groupValues?.get(2)
        sleepTimerEnable = true
        sleepTimerHour = hourString
        sleepTimerMin = minString
    }

    if (h24 != null && regex.matches(h24)){
        val matchResult = regex.find(h24)
        val groupValues = matchResult?.groupValues
        val hourString = groupValues?.get(1)
        val minString = groupValues?.get(2)
        powerOnTimerEnable = true
        powerOnTimerHour = hourString
        powerOnTimerMin = minString
    }

    return AdvancedSetting(
        h01 == 1,
        h05,
        h07,
        sleepTimerEnable, sleepTimerHour, sleepTimerMin,
        powerOnTimerEnable, powerOnTimerHour, powerOnTimerMin
    )
}

fun ApiMember.toShareMember(): ShareMember{
    return ShareMember(
        userId,
        name ?: "--",
        account,
        status == 1
    )
}

fun ApiPlaceMember.toPlaceShareMember(): PlaceShareMember{
    return PlaceShareMember(
        ShareMember(userId, name?:"--", account, invite),
        deviceNum
    )
}

fun ApiWorkOrderStatus.toWorkOrderInfo(): WorkOrderInfo? {
    return try {
        WorkOrderInfo(
            workOrderId,
            WorkOrderStatus.fromId(status),
            WorkOrderRequirement.fromName(typeName),
            LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").parse(addedTime))
        )
    }catch (e: Exception){null}
}

fun ApiWorkOrderDetail.toPendingWorkOrderDetail(): PendingWorkOrderDetail{

    val errorReasons = values.filter { it.group1 == "客戶反應內容" }
        .map { FormOption(it.code, it.group2, it.item) }

    return PendingWorkOrderDetail(
        mId,
        deviceId,
        WorkOrderRequirement.fromId(type),
        expectedDaysOfWeek?.split(",")?.map { it.toInt() } ?: listOf(),
        expectedTimeOfWeek,
        errorReasons,
        description,
        userImages.map { ApiEndPoint.DOMAIN+it.imageUrl }
    )
}

fun ApiWorkOrderDetail.toScheduledWorkOrderDetail(): ScheduledWorkOrderDetail {

    val errorReasons = values.filter { it.group1 == "客戶反應內容" }
        .map { FormOption(it.code, it.group2, it.item) }

    return ScheduledWorkOrderDetail(
        mId,
        deviceId,
        EngineerInfo(engineerId, vendorName, engineerName, vendorTel),
        LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").parse(appointedDatetime)),
        WorkOrderRequirement.fromId(type),
        errorReasons,
        description
    )
}

fun ApiWorkOrderDetail.toTaskDetail(): TaskDetail {

    val errorReasons = values.filter { it.group1 == "客戶反應內容" }
        .map { FormOption(it.code, it.group2, it.item) }

    val photoVideos = userImages.map { ApiEndPoint.DOMAIN+it.imageUrl }

    val tds = values.firstOrNull { it.code == "BA1" }?.value ?: "--"
    val checkTDS = values.firstOrNull { it.code == "BA2" }?.value ?: "--"
    val filterChanged = values.filter { it.group2 == "更換濾芯" }.map { FormOption(it.code, it.group2, it.item) }
    val basicMaintain = values.filter { it.group2 == "基礎保養" }.map { FormOption(it.code, it.group2, it.item) }

    val maintainRecord = MaintainRecord(tds, checkTDS, filterChanged, basicMaintain)

    val errorCode = values.filter { it.group2 == "故障代碼" }.map { FormOption(it.code, it.group2, it.item, it.remark) }
    val repairParts = values.filter { it.group1 == "維修內容" }.map { FormOption(it.code, it.group2, it.item, it.remark) }
    val changeParts = values.filter { it.group1 == "更換零件" }.map { FormOption(it.code, it.group2, it.item) }
    val repairRecord = RepairRecord(errorCode, repairParts, changeParts)


    val customerSign: String = if (signImage != null) ApiEndPoint.DOMAIN + signImage else ""
    val quotationPDF: String? = attachmentUrl
    val confirmRecord = ConfirmRecord(fee, customerSign, engineerId, errorReason, quotationPDF, work == 1)

    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val addTime = LocalDateTime.from(dateTimeFormatter.parse(addedTime))
    val appointedDatetime = LocalDateTime.from(dateTimeFormatter.parse(appointedDatetime))

    val workStartDatetime = if (workStartDatetime != null) LocalDateTime.from(dateTimeFormatter.parse(workStartDatetime)) else null
    val hasCustomerFeedback = addedType == "app"

    return TaskDetail(
        mId,
        deviceId,
        TaskStatusCategory.fromStatusId(status),
        continuance == 1,
        appointedDatetime,
        workStartDatetime,
        EngineerInfo(engineerId, vendorName, engineerName, vendorTel),
        CustomerInfo(customerName, name, customerTel, customerAddress),
        WorkOrderRequirement.fromId(type),
        errorReasons,
        photoVideos,
        description,
        maintainRecord,
        repairRecord,
        confirmRecord,
        hasCustomerFeedback,
        vendorDescription
    )
}


fun ApiVendorInfo.toManufacturerInfo(): ManufacturerInfo{
    return ManufacturerInfo(name, tel)
}

fun ApiVendorInfo.toVendor(): Vendor {
    return Vendor(vendorId, name)
}

fun ApiWarranty.toWarranty(): Warranty{
    return Warranty(
        warrantyName!!,
        warrantyEmail!!,
        warrantyTel!!,
        LocalDate.from(DateTimeFormatter.ISO_DATE.parse(invDate))
    )
}

fun ApiTaskSummary.toTaskSummary(): TaskSummary {
    return TaskSummary(
        mId,
        LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").parse(appointedDatetime)),
        name,
        address ?: "",
        WorkOrderRequirement.fromName(typeName),
        TaskStatusCategory.fromStatusId(status),
        continuance == 1,
        TaskStatus.fromId(status)
    )
}

fun ApiWorkOrderDetail.toFinishedWorkOrderDetail(): FinishedWorkOrderDetail{

    val errorReasons = values.filter { it.group1 == "客戶反應內容" }
        .map { FormOption(it.code, it.group2, it.item) }

    val tds = values.firstOrNull { it.code == "BA1" }?.value ?: "--"
    val checkTDS = values.firstOrNull { it.code == "BA2" }?.value ?: "--"
    val filterChanged = values.filter { it.group2 == "更換濾芯" }.map { FormOption(it.code, it.group2, it.item) }
    val basicMaintain = values.filter { it.group2 == "基礎保養" }.map { FormOption(it.code, it.group2, it.item) }

    val maintainRecord = MaintainRecord(tds, checkTDS, filterChanged, basicMaintain)

    val errorCode = values.filter { it.group2 == "故障代碼" }.map { FormOption(it.code, it.group2, it.item, it.remark) }
    val repairParts = values.filter { it.group1 == "維修內容" }.map { FormOption(it.code, it.group2, it.item, it.remark) }
    val changeParts = values.filter { it.group1 == "更換零件" }.map { FormOption(it.code, it.group2, it.item) }
    val repairRecord = RepairRecord(errorCode, repairParts, changeParts)

    val customerSign: String = if (signImage != null) ApiEndPoint.DOMAIN + signImage else ""
    val quotationPDF: String? = attachmentUrl
    val confirmRecord = ConfirmRecord(fee, customerSign, engineerId, errorReason, quotationPDF, work == 1)

    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val workStartDatetime = if (workStartDatetime != null) LocalDateTime.from(dateTimeFormatter.parse(workStartDatetime)) else null

    return FinishedWorkOrderDetail(
        mId,
        TaskStatusCategory.fromStatusId(status),
        workStartDatetime,
        EngineerInfo(engineerId, vendorName, engineerName, vendorTel),
        CustomerInfo(customerName, name, customerTel, customerAddress),
        WorkOrderRequirement.fromId(type),
        errorReasons,
        description,
        maintainRecord,
        repairRecord,
        confirmRecord
    )
}

fun ApiDeviceFilter.toFilterStatus(): FilterStatus{
    return FilterStatus(
        name,
        lifeRate.toInt()
    )
}

fun ApiMsg.toNotification(readIds: List<Int> = emptyList()): Notification?{
    return try {
        val notificationType = when(type){
            1 -> Notification.Type.Alert
            2 -> Notification.Type.Maintenance
            3 -> Notification.Type.TaskDelay
            4 -> Notification.Type.DataAnalysis
            else -> Notification.Type.UnKnown
        }

        Notification(
            msgId,
            LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").parse(addedTime)),
            notificationType,
            subject,
            body,
            msgId in readIds,
            deviceId,
            mId
        )
    }catch (e: Exception){
        null
    }

}

fun ApiInvite.toInvite(): Invite{
    return Invite(
        deviceId,
        owner,
        placeName
    )
}

fun ApiInstallRecord.toInstallRecord(): InstallRecord{
    return InstallRecord(
        modelName,
        mac,
        "$customerArea\n$customerName",
        LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").parse(installationDate)),
        )
}


fun ApiPlace.toPlaceInfo(): PlaceInfo{
    return PlaceInfo(placeId, name)
}

fun ApiDeviceByMAC.toSearchedDevice(): SearchedDevice {
    return SearchedDevice(
        deviceId,
        sn,
        mac,
        name,
        "$modelId $modelName",
        userId,
        modelImageUrl,
        address,
        vendorName,
        if (customerId != null && customerName != null) CustomerAgency(customerId, customerName) else null,
        warranty == 1
    )
}

fun ApiCustomer.toCustomer(): CustomerAgency{
    return CustomerAgency(customerId, name)
}

fun ApiDeviceSchedule.toPowerSchedule(): PowerSchedule{
    val weekList = try {
        weeks.split(",").map { it.toInt() }
    }catch (e: Exception){
        emptyList<Int>()
    }

    return PowerSchedule(
        qId,
        name,
        openTimes,
        sleepTimes,
        weekList,
        deviceId = deviceId
    )
}

fun ApiAreaSchedule.toPowerSchedule(): PowerSchedule{
    val weekList = try {
        weeks.split(",").map { it.toInt() }
    }catch (e: Exception){
        emptyList<Int>()
    }

    return PowerSchedule(
        qId,
        name,
        openTimes,
        sleepTimes,
        weekList,
        areaId = areaId
    )
}