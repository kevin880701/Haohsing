package com.clockworkorange.domain.data

import com.clockworkorange.domain.data.remote.ServiceWrapper
import com.clockworkorange.domain.data.remote.model.AddDeviceManualParam
import com.clockworkorange.domain.data.remote.model.ApiDeviceStatistics
import com.clockworkorange.domain.data.remote.model.ApiEditScheduleParam
import com.clockworkorange.domain.data.remote.model.ApiPlaceStatistics
import com.clockworkorange.domain.data.remote.model.CreateAreaScheduleParam
import com.clockworkorange.domain.data.remote.model.CreateScheduleParam
import com.clockworkorange.domain.data.remote.model.FilterParam
import com.clockworkorange.domain.data.remote.model.UpdateDevicePlaceAreaParam
import com.clockworkorange.domain.di.ApplicationScope
import com.clockworkorange.domain.entity.Device
import com.clockworkorange.domain.entity.DeviceDetail
import com.clockworkorange.domain.entity.FilterStatus
import com.clockworkorange.domain.entity.ManufacturerInfo
import com.clockworkorange.domain.entity.OwnerInfo
import com.clockworkorange.domain.entity.PowerSchedule
import com.clockworkorange.domain.entity.Warranty
import com.clockworkorange.domain.usecase.device.AddDeviceManualUseCaseParam
import com.clockworkorange.domain.usecase.device.FinishUserPairParam
import com.clockworkorange.domain.usecase.device.SearchedDevice
import com.clockworkorange.domain.usecase.device.ShareMember
import com.clockworkorange.domain.usecase.device.TransferOwnerParam
import com.clockworkorange.domain.usecase.device.UpdateWarrantyParam
import com.clockworkorange.domain.usecase.device.UserNotExistException
import com.clockworkorange.domain.usecase.device.WaterTempType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.YearMonth
import java.time.format.DateTimeFormatter

interface DeviceRepository {
    fun getDevices(): StateFlow<List<Device>>
    fun refreshDevices()

    suspend fun getDeviceDetail(id: Int): DeviceDetail
    fun getDeviceDetailFlow(id: Int): Flow<DeviceDetail?>
    suspend fun getFilterStatus(id: Int): List<FilterStatus>
    suspend fun getOwnerInfo(id: Int): OwnerInfo
    suspend fun getManufacturerInfo(id: Int): ManufacturerInfo
    suspend fun getWarrantyInfo(id: Int): Warranty?

    suspend fun updateDevicePlace(deviceId: Int, newPlaceId: Int): Boolean
    suspend fun updateDeviceArea(deviceId: Int, placeId: Int, newAreaId: Int): Boolean
    suspend fun updateDeviceName(deviceId: Int, newName: String): Boolean
    suspend fun getDeviceShareMemberList(deviceId: Int): List<ShareMember>
    suspend fun addDeviceShareMember(deviceId: Int, userMail: String): Boolean
    suspend fun deleteDeviceShareMember(deviceId: Int, userId: Int): Boolean
    suspend fun setDeviceValue(deviceId: Int, code: String, value: String): Boolean
    suspend fun setAreaDeviceValue(areaId: Int, code: String, value: String): Boolean
    suspend fun getWaterTempOptions(type: WaterTempType): List<String>
    fun getDevicesUnderPlaceArea(placeId: Int, areaId: Int): StateFlow<List<Device>>
    suspend fun searchDeviceByMAC(mac: String): SearchedDevice
    suspend fun finishUserPair(parameters: FinishUserPairParam): Boolean
    suspend fun updateWarranty(parameters: UpdateWarrantyParam): Boolean
    suspend fun addDeviceManually(param: AddDeviceManualUseCaseParam): Int
    suspend fun getPowerScheduleList(deviceId: Int): List<PowerSchedule>
    suspend fun getAreaScheduleList(areaId: Int): List<PowerSchedule>
    suspend fun getAreaSchedule(scheduleId: Int): PowerSchedule
    suspend fun deleteDeviceSchedule(scheduleId: Int): Boolean
    suspend fun addDeviceSchedule(
        deviceId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean

    suspend fun editDeviceSchedule(
        scheduleId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean

    suspend fun deleteAreaSchedule(scheduleId: Int): Boolean
    suspend fun addAreaSchedule(
        areaId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean

    suspend fun editAreaSchedule(
        scheduleId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean

    suspend fun getPlaceDataAnalysis(placeId: Int, yearMonth: YearMonth): ApiPlaceStatistics
    suspend fun getDeviceStatistics(deviceId: Int): ApiDeviceStatistics
    suspend fun transferOwner(transferOwnerParam: TransferOwnerParam): Boolean
    suspend fun quitDevice(deviceId: Int): Boolean

}

class DeviceRepositoryImpl(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val serviceWrapper: ServiceWrapper
    ): DeviceRepository{

    private val devices = MutableStateFlow<List<Device>>(emptyList())

    override fun getDevices(): StateFlow<List<Device>> {
        refreshDevices()
        return devices
    }

    override fun refreshDevices() {
        externalScope.launch {
            val data = try {
                serviceWrapper.getUserDeviceList().map { it.toDevice() }
            } catch (e: Exception) {
                emptyList()
            }
            devices.emit(data)
        }

        updateDeviceUnderPlaceAreaFlow?.invoke()
    }

    override suspend fun getDeviceDetail(id: Int): DeviceDetail {
        return serviceWrapper.getDeviceDetail(id).toDeviceDetail()
    }

    private val deviceDetailFlow = MutableStateFlow<DeviceDetail?>(null)
    private var updateDeviceDetailFlow: (() -> Unit)? = null

    override fun getDeviceDetailFlow(id: Int): Flow<DeviceDetail?> {
        updateDeviceDetailFlow = {
            externalScope.launch {
                try {
                    deviceDetailFlow.emit(getDeviceDetail(id))
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        updateDeviceDetailFlow?.invoke()
        return deviceDetailFlow
    }


    override suspend fun getFilterStatus(id: Int): List<FilterStatus> {
        return serviceWrapper.getFilterList(id).map { it.toFilterStatus() }
    }

    override suspend fun getOwnerInfo(id: Int): OwnerInfo {
        val deviceDetail = serviceWrapper.getDeviceDetail(id)
        return OwnerInfo(deviceDetail.customerName ?: "", deviceDetail.customerAddress ?: "")
    }

    override suspend fun getManufacturerInfo(id: Int): ManufacturerInfo {
        val deviceInfo = getDeviceDetail(id)
        return serviceWrapper.getVendorInfo(deviceInfo.vendorId).toManufacturerInfo()
    }

    override suspend fun getWarrantyInfo(id: Int): Warranty? {
        return serviceWrapper.getWarranty(id).takeIf { it.isWarrantyExist() }?.toWarranty()
    }

    override suspend fun updateDevicePlace(deviceId: Int, newPlaceId: Int): Boolean {
        return serviceWrapper.updateDevicePlaceArea(UpdateDevicePlaceAreaParam(deviceId, newPlaceId, 0)).also {
            updateDeviceDetailFlow?.invoke()
        }
    }

    override suspend fun updateDeviceArea(deviceId: Int, placeId: Int, newAreaId: Int): Boolean {
        return serviceWrapper.updateDevicePlaceArea(UpdateDevicePlaceAreaParam(deviceId, placeId, newAreaId)).also {
            updateDeviceDetailFlow?.invoke()
        }
    }

    override suspend fun updateDeviceName(deviceId: Int, newName: String): Boolean {
        return serviceWrapper.updateDeviceName(deviceId, newName).also { updateDeviceDetailFlow?.invoke() }
    }

    override suspend fun getDeviceShareMemberList(deviceId: Int): List<ShareMember> {
        return serviceWrapper.getDeviceShareMemberList(deviceId).map { it.toShareMember() }
    }

    override suspend fun addDeviceShareMember(deviceId: Int, userMail: String): Boolean {
        return serviceWrapper.shareDevice(deviceId, userMail)
    }

    override suspend fun deleteDeviceShareMember(deviceId: Int, userId: Int): Boolean {
        return serviceWrapper.deleteDeviceShareMember(userId, deviceId)
    }

    override suspend fun setDeviceValue(deviceId: Int, code: String, value: String): Boolean {
        return serviceWrapper.setDeviceValue(deviceId, code, value).also {
            externalScope.launch {
                delay(4000)
                updateDeviceDetailFlow?.invoke()
            }
        }
    }

    override suspend fun setAreaDeviceValue(areaId: Int, code: String, value: String): Boolean {
        return serviceWrapper.setAreaDeviceValue(areaId, code, value)
    }

    override suspend fun getWaterTempOptions(type: WaterTempType): List<String> {
        return when(type){
            WaterTempType.Hot -> serviceWrapper.getHotTempOptions()
            WaterTempType.Cold -> serviceWrapper.getColdTempOptions()
        }
    }

    private val deviceUnderPlaceAreaFlow = MutableStateFlow<List<Device>>(emptyList())
    private var updateDeviceUnderPlaceAreaFlow: (() -> Unit)? = null

    override fun getDevicesUnderPlaceArea(placeId: Int, areaId: Int): StateFlow<List<Device>> {
        updateDeviceUnderPlaceAreaFlow = {
            externalScope.launch {
                val data = try {
                    serviceWrapper.getDeviceUnderPlaceArea(placeId, areaId).map { it.toDevice() }
                } catch (e: Exception) {
                    emptyList()
                }
                deviceUnderPlaceAreaFlow.emit(data)
            }
        }
        updateDeviceUnderPlaceAreaFlow?.invoke()
        return deviceUnderPlaceAreaFlow
    }

    override suspend fun searchDeviceByMAC(mac: String): SearchedDevice {
        return serviceWrapper.searchDevice(mac).toSearchedDevice()
    }

    override suspend fun addDeviceManually(param: AddDeviceManualUseCaseParam): Int {
        val data = with(param) {
            AddDeviceManualParam(areaId, filters?.map {
                FilterParam(
                    it.type.code, it.installDate.format(
                        DateTimeFormatter.ISO_LOCAL_DATE
                    ), it.lifeMonth, it.name
                )
            }, brand, model, name, placeId, vendorId)
        }
        val result = serviceWrapper.addDeviceManually(data)
        refreshDevices()
        return result
    }

    override suspend fun finishUserPair(parameters: FinishUserPairParam): Boolean {
        //更新保固資訊
        if (parameters.ownerName != null
            && parameters.ownerEmail != null
            && parameters.ownerPhone != null
            && parameters.buyDate != null){

            serviceWrapper.updateWarranty(
                parameters.deviceId,
                parameters.ownerName,
                parameters.ownerEmail,
                parameters.ownerEmail,
                parameters.buyDate
            )
        }

        // 配對裝置(user)
        val pairResult = serviceWrapper.pairDeviceUser(
            parameters.sn,
            parameters.mac,
            parameters.deviceName,
            parameters.placeId,
            parameters.areaId
        )
        refreshDevices()
        return pairResult
    }

    override suspend fun updateWarranty(parameters: UpdateWarrantyParam): Boolean {
        return serviceWrapper.updateWarranty(
            parameters.deviceId,
            parameters.ownerName,
            parameters.ownerEmail,
            parameters.ownerEmail,
            parameters.buyDate
        )
    }

    override suspend fun getPowerScheduleList(deviceId: Int): List<PowerSchedule> {
        return serviceWrapper.getDeviceScheduleList(deviceId).map { it.toPowerSchedule() }
    }

    override suspend fun getAreaScheduleList(areaId: Int): List<PowerSchedule> {
        return serviceWrapper.getAreaScheduleList(areaId).map { it.toPowerSchedule() }
    }

    override suspend fun getAreaSchedule(scheduleId: Int): PowerSchedule {
        return serviceWrapper.getAreaSchedule(scheduleId).toPowerSchedule()
    }

    override suspend fun deleteDeviceSchedule(scheduleId: Int): Boolean {
        return serviceWrapper.deleteDeviceSchedule(scheduleId)
    }

    override suspend fun addDeviceSchedule(
        deviceId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean {
        val data = CreateScheduleParam(
            deviceId = deviceId,
            name = name,
            weeks = weekDays.joinToString(","),
            openTimes = powerOnTime,
            sleepTimes = sleepTime
        )
        return serviceWrapper.createDeviceSchedule(data) != 0
    }

    override suspend fun editDeviceSchedule(
        scheduleId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean {
        val data = ApiEditScheduleParam(
            scheduleId,
            name,
            weekDays.joinToString(","),
            powerOnTime,
            sleepTime
        )
        return serviceWrapper.editDeviceSchedule(data)
    }

    override suspend fun deleteAreaSchedule(scheduleId: Int): Boolean {
        return serviceWrapper.deleteAreaSchedule(scheduleId)
    }

    override suspend fun addAreaSchedule(
        areaId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean {
        val data = CreateAreaScheduleParam(
            areaId = areaId,
            name = name,
            weeks = weekDays.joinToString(","),
            openTimes = powerOnTime,
            sleepTimes = sleepTime
        )
        return serviceWrapper.createAreaSchedule(data) != 0
    }

    override suspend fun editAreaSchedule(
        scheduleId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean {
        val data = ApiEditScheduleParam(
            scheduleId,
            name,
            weekDays.joinToString(","),
            powerOnTime,
            sleepTime
        )
        return serviceWrapper.editAreaSchedule(data)
    }

    override suspend fun getPlaceDataAnalysis(placeId: Int, yearMonth: YearMonth): ApiPlaceStatistics {
        return serviceWrapper.getPlaceDataAnalysis(placeId, yearMonth.year, yearMonth.monthValue)
    }

    override suspend fun getDeviceStatistics(deviceId: Int): ApiDeviceStatistics {
        return serviceWrapper.getDeviceStatistics(deviceId)
    }

    override suspend fun transferOwner(transferOwnerParam: TransferOwnerParam): Boolean {
        return serviceWrapper.transferOwner(transferOwnerParam.account, transferOwnerParam.deviceIds)
    }

    override suspend fun quitDevice(deviceId: Int): Boolean {
        val result = serviceWrapper.quitDevice(deviceId)
        refreshDevices()
        return result
    }
}

class FakeDeviceRepository: DeviceRepository{

    private val devices = MutableStateFlow<List<Device>>(emptyList())

    init {
        refreshDevices()
    }

    override fun getDevices(): StateFlow<List<Device>> {
        return devices
    }

    override fun refreshDevices() {
        val emitResult = devices.tryEmit(FakeData.devices.values.toList())
        Timber.d("refresh: $emitResult")
    }

    override suspend fun getFilterStatus(id: Int): List<FilterStatus> {
        return FakeData.deviceFilterStatus[id]!!
    }

    override suspend fun getOwnerInfo(id: Int): OwnerInfo {
        return FakeData.deviceOwnerInfo[id]!!
    }

    override suspend fun getManufacturerInfo(id: Int): ManufacturerInfo {
        return FakeData.deviceManufacturerInfo[id]!!
    }

    override suspend fun getWarrantyInfo(id: Int): Warranty {
        return FakeData.deviceWarranty[id]!!
    }

    override suspend fun updateDevicePlace(deviceId: Int, newPlaceId: Int): Boolean {
        val device = FakeData.devices[deviceId] ?: return false
        val newPlace = FakeData.places[newPlaceId] ?: return false
        FakeData.devices[deviceId] = device.copy(placeInfo = newPlace)
        FakeData.devices[deviceId] = device.copy(areaInfo = null)

        return true
    }

    override suspend fun updateDeviceArea(deviceId: Int, placeId: Int, newAreaId: Int): Boolean {
        val device = FakeData.devices[deviceId] ?: return false
        val newArea = FakeData.areas[newAreaId] ?: return false
        FakeData.devices[deviceId] = device.copy(areaInfo = newArea)
        return true
    }

    override suspend fun updateDeviceName(deviceId: Int, newName: String): Boolean {
        val device = FakeData.devices[deviceId] ?: return false
        FakeData.devices[deviceId] = device.copy(name = newName)
        return true
    }

    override suspend fun getDeviceShareMemberList(deviceId: Int): List<ShareMember> {
        return FakeData.deviceShareMemberList[deviceId]!!.toList()
    }

    override suspend fun addDeviceShareMember(deviceId: Int, userMail: String): Boolean {
        val user = FakeData.users.find { it.mail == userMail } ?: throw UserNotExistException()
        val shareMember = ShareMember(user.userId, user.name, user.mail, false)
        FakeData.deviceShareMemberList[deviceId]?.add(shareMember)
        return true
    }

    override suspend fun deleteDeviceShareMember(deviceId: Int, userId: Int): Boolean {
        return FakeData.deviceShareMemberList[deviceId]?.removeIf { it.userId == userId } == true
    }

    override suspend fun getDeviceDetail(id: Int): DeviceDetail {
        TODO("Not yet implemented")
    }

    override suspend fun setDeviceValue(deviceId: Int, code: String, value: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setAreaDeviceValue(areaId: Int, code: String, value: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getWaterTempOptions(type: WaterTempType): List<String> {
        TODO("Not yet implemented")
    }

    override fun getDevicesUnderPlaceArea(placeId: Int, areaId: Int): StateFlow<List<Device>> {
        TODO("Not yet implemented")
    }

    override fun getDeviceDetailFlow(id: Int): Flow<DeviceDetail?> {
        TODO("Not yet implemented")
    }

    override suspend fun searchDeviceByMAC(mac: String): SearchedDevice {
        TODO("Not yet implemented")
    }

    override suspend fun finishUserPair(parameters: FinishUserPairParam): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateWarranty(parameters: UpdateWarrantyParam): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun addDeviceManually(parameters: AddDeviceManualUseCaseParam): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getPowerScheduleList(deviceId: Int): List<PowerSchedule> {
        TODO("Not yet implemented")
    }

    override suspend fun getAreaScheduleList(areaId: Int): List<PowerSchedule> {
        TODO("Not yet implemented")
    }

    override suspend fun getAreaSchedule(scheduleId: Int): PowerSchedule {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDeviceSchedule(scheduleId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun addDeviceSchedule(
        deviceId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun editDeviceSchedule(
        scheduleId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAreaSchedule(scheduleId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun addAreaSchedule(
        areaId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun editAreaSchedule(
        scheduleId: Int,
        name: String,
        powerOnTime: String,
        sleepTime: String,
        weekDays: List<Int>
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaceDataAnalysis(placeId: Int, yearMonth: YearMonth): ApiPlaceStatistics {
        TODO("Not yet implemented")
    }

    override suspend fun getDeviceStatistics(deviceId: Int): ApiDeviceStatistics {
        TODO("Not yet implemented")
    }

    override suspend fun transferOwner(transferOwnerParam: TransferOwnerParam): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun quitDevice(deviceId: Int): Boolean {
        TODO("Not yet implemented")
    }
}