package com.clockworkorange.domain.entity

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

data class Device(
    val id: Int,
    val name: String,
    val placeInfo: PlaceInfo,
    val areaInfo: AreaInfo?,
    val modelInfo: ModelInfo,
    val status: DeviceStatus,
    val isOwner: Boolean = false,
    val mac: String? = null,
    val installDateTime: LocalDateTime?,
){
    val placeId: Int
        get() = placeInfo.id

    val placeName: String
        get() = placeInfo.name

    val areaId: Int?
        get() = areaInfo?.id

    val areaName: String
        get() = areaInfo?.name ?: ""

    val isIoTDevice get() =  mac != null
}

data class DeviceDetail(
    val id: Int,
    val mac: String?,
    val name: String,
    val installAddress: String?,
    val placeInfo: PlaceInfo,
    val areaInfo: AreaInfo?,
    val modelInfo: ModelInfo,
    val status: DeviceStatus,
    val isOwner: Boolean = false,
    val ownerInfo: OwnerInfo,
    val deviceValue: DeviceValue?,
    val deviceSetting: List<DeviceSetting>,
    val advancedSetting: AdvancedSetting?,
    val vendorName: String?,
    val vendorId: Int,
    val schedules: List<PowerSchedule>,
    val noMaintainTime: Int,
    val manualUrl: String?,
    val errorCode: String?
){
    val placeId: Int
        get() = placeInfo.id

    val placeName: String
        get() = placeInfo.name

    val areaId: Int?
        get() = areaInfo?.id

    val areaName: String
        get() = areaInfo?.name ?: ""
}


sealed class DeviceStatus(val displayString: String) {
    object Normal : DeviceStatus("連線中")
    class Error(val errorCode: Int? = null, val errorMessage: String? = null) : DeviceStatus("異常")
    object Offline : DeviceStatus("未連線")
    object Loading : DeviceStatus("載入中")
    object Off : DeviceStatus("關機")
}

data class PlaceInfo(
    val id: Int,
    val name: String
){
    companion object{
        val ALL = PlaceInfo(-1, "所有")
    }
}

data class AreaInfo(
    val id: Int,
    val name: String,
    val placeId: Int
){
    companion object{
        val ALL = AreaInfo(-1, "所有", 0)
    }
}

data class ModelInfo(
    val id: Int,
    val name: String?,
    val imageUrl: String
)

data class DeviceValue(
     val waterLevel: WaterLeveL,
     val cleanWaterTDS: Int,
     val originWaterTDS: Int,
     val tempCool: Int,
     val tempHot: Int,
     val tempNormal: Int
){

    enum class WaterLeveL{
        High, Middle, Low, UnKnown
    }

    companion object{
        fun createRandom(): DeviceValue{
            val waterLevel = when(Random.nextInt(3)){
                0 -> WaterLeveL.High
                1 -> WaterLeveL.Middle
                2 -> WaterLeveL.Low
                else -> WaterLeveL.Low
            }

            return DeviceValue(
                waterLevel,
                Random.nextInt(0, 150),
                Random.nextInt(0, 100),
                Random.nextInt(0, 10),
                Random.nextInt(25, 60),
                Random.nextInt(70, 99)
            )
        }
    }
}

data class FilterStatus(
    val name: String,
    val health: Int
){
    companion object{
        fun createRandom() = listOf<FilterStatus>(
            FilterStatus("濾芯1", Random.nextInt(0, 100)),
            FilterStatus("濾芯2", Random.nextInt(0, 100)),
            FilterStatus("濾芯3", Random.nextInt(0, 100)),
            FilterStatus("濾芯4", Random.nextInt(0, 100)),
            FilterStatus("濾芯5", Random.nextInt(0, 100)),
        )
    }
}

data class DeviceSetting(
    val code: String,
    val name: String,
    val enabled: Boolean
){
    companion object{
        fun createRandom() = listOf<DeviceSetting>(
            DeviceSetting("電源","電源", Random.nextBoolean()),
            DeviceSetting("熱水出水解鎖","熱水出水解鎖", Random.nextBoolean()),
            DeviceSetting("加熱器啟動","加熱器啟動", Random.nextBoolean()),
            DeviceSetting("製冷器啟動","製冷器啟動", Random.nextBoolean()),
            DeviceSetting("熱出水功能","熱出水功能", Random.nextBoolean()),
            DeviceSetting("溫出水功能","溫出水功能", Random.nextBoolean()),
            DeviceSetting("冰出水功能","冰出水功能", Random.nextBoolean()),
            DeviceSetting("語音功能","語音功能", Random.nextBoolean()),
        )
    }
}

/**
 * 進階設定
 * @property reheatEnable Boolean 再加熱功能
 * @property hotWaterTemp Int 熱水保溫溫度
 * @property coolWaterTemp Int 冰水保溫溫度
 * @property sleepSchedule  定時睡眠
 * @property powerOnSchedule  定時睡眠
 * @constructor
 */
data class AdvancedSetting(
    val reheatEnable: Boolean,
    val hotWaterTemp: Int?,
    val coolWaterTemp: Int?,
    val sleepTimerEnable: Boolean = false,
    val sleepTimerHour: String? = null,
    val sleepTimerMin: String? = null,
    val powerOnTimerEnable: Boolean = false,
    val powerOnTimerHour: String? = null,
    val powerOnTimerMin: String? = null,
)

enum class ScheduleType {
    Area, Device
}

data class PowerSchedule(
    val id: Int,
    val name: String,
    val powerOnTime: String? = null,
    val sleepTime: String? = null,
    val weekDays: List<Int>,
    val areaId: Int? = null,
    val deviceId: Int? = null,
){

    fun getScheduleType(): ScheduleType {
        return if(areaId != null) {
            ScheduleType.Area
        } else {
            ScheduleType.Device
        }
    }
    
    companion object{
        fun createDemo() = PowerSchedule(
            Random.nextInt(),
            "排程#${Random.nextInt()}",
            "08:00",
            "23:00",
            listOf(1,2,3)
        )
    }

}

data class OwnerInfo(val name: String, val address: String){
    companion object{
        fun createDemo() = OwnerInfo("豪星科技股份有限公司", "台中市烏日區高鐵五路166號")
    }
}

/**
 * 廠商資訊
 * @property name String
 * @property phone String
 * @constructor
 */
data class ManufacturerInfo(val name: String, val phone: String){
    companion object{
        fun createDemo() = ManufacturerInfo("ＯＯ有限公司", "04-42345678")
    }
}

//報修與保養
enum class WorkOrderRequirement(val displayString: String, val id: Int){
    Install("安裝", 1),
    Maintain("保養", 2),
    Repair("維修", 3),
    FieldSurvey("場勘",4),
    UnKnown("未知",-1);
    companion object{
        fun getRandom(): WorkOrderRequirement{
            return when(Random.nextInt(0,4)){
                0 -> Install
                1 -> Maintain
                2 -> Repair
                3 -> FieldSurvey
                else -> Install
            }
        }

        fun fromName(name: String?): WorkOrderRequirement{
            return when(name){
                "安裝" -> Install
                "保養" -> Maintain
                "維修" -> Repair
                "場勘" -> FieldSurvey
                else -> UnKnown
            }
        }

        fun fromId(id: Int): WorkOrderRequirement{
            return when(id){
                1 -> Install
                2 -> Maintain
                3 -> Repair
                4 -> FieldSurvey
                else -> UnKnown
            }
        }

    }
}

//保固資訊
data class Warranty(
    val name: String,
    val email: String,
    val phone: String,
    val startDate: LocalDate
){
    companion object{
        fun createRandom(): Warranty{
            return Warranty(
                "使用者${Random.nextInt(0, 1000)}",
                "user${Random.nextInt(0, 1000)}@mail.com",
                "09${Random.nextInt(0, 100000000)}",
                LocalDate.of(2022,2,10)
            )
        }
    }
}

