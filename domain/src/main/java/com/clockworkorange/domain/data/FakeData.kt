package com.clockworkorange.domain.data

import com.clockworkorange.domain.entity.*
import com.clockworkorange.domain.usecase.device.ShareMember
import java.time.LocalDateTime
import kotlin.random.Random

object FakeData {

    val places = HashMap<Int, PlaceInfo>()
    val areas = HashMap<Int, AreaInfo>()

    val devices = HashMap<Int, Device>()
    val deviceValues = HashMap<Int, DeviceValue>()
    val deviceFilterStatus = HashMap<Int, List<FilterStatus>>()
    val deviceSettings = HashMap<Int, List<DeviceSetting>>()
//    val deviceAdvancedSetting = HashMap<Int, AdvancedSetting>()
    val deviceOwnerInfo = HashMap<Int, OwnerInfo>()
    val deviceManufacturerInfo = HashMap<Int, ManufacturerInfo>()
    val deviceWarranty = HashMap<Int, Warranty>()

    val users = mutableListOf<FakeUser>()
    data class FakeUser(val userId:Int, val name: String, val mail: String)
    val deviceShareMemberList = HashMap<Int, MutableList<ShareMember>>()

    init {
        fillPlaceArea()
        fillDevice()
        fillValue()
        fillUsers()
        fillShareMember()
    }

    var globalId: Int = 0
    fun createId(): Int{
        return globalId++
    }

    private fun fillValue() {
        devices.values.forEach {
            deviceValues[it.id] = DeviceValue.createRandom()
            deviceFilterStatus[it.id] = FilterStatus.createRandom()
            deviceSettings[it.id] = DeviceSetting.createRandom()
            deviceOwnerInfo[it.id] = OwnerInfo.createDemo()
            deviceManufacturerInfo[it.id] = ManufacturerInfo.createDemo()
            deviceWarranty[it.id] = Warranty.createRandom()
        }
    }

    private fun fillDevice() {
        fun createDevice(name: String):Device{
            val place = places.values.random()
            val area = areas.values.filter { it.placeId == place.id }.random()

            return Device(
                id = createId(),
                name = name,
                PlaceInfo(place.id, place.name),
                AreaInfo(area.id, area.name, place.id),
                ModelInfo(1, "HM-2681", "http://35.201.190.135:8180/image/HM-2681.png"),
                createRandomState(),
                installDateTime = LocalDateTime.now()
            )
        }

        val data = mutableListOf<Device>()
        repeat(100){
            data.add(createDevice("飲水機${Random.nextInt(0, 1000)}"))
        }

        data.forEach {
            devices[it.id] = it
        }
    }

    private fun createRandomState():DeviceStatus{
        return when(Random.nextInt(0, 4)){
            0 -> DeviceStatus.Normal
            1 -> DeviceStatus.Error(null, "")
            2 -> DeviceStatus.Offline
            3 -> DeviceStatus.Loading
            else -> DeviceStatus.Loading
        }
    }

    private fun fillPlaceArea(){
        val placeTTC = PlaceInfo(
            createId(),
            "TTC大樓"
        )


        places[placeTTC.id] = placeTTC
        listOf(
            AreaInfo(createId(), "1樓", placeTTC.id),
            AreaInfo(createId(), "2樓", placeTTC.id),
            AreaInfo(createId(), "3樓", placeTTC.id),
            AreaInfo(createId(), "4樓", placeTTC.id),
            AreaInfo(createId(), "5樓", placeTTC.id),
            AreaInfo(createId(), "6樓", placeTTC.id),
            AreaInfo(createId(), "7樓", placeTTC.id),
            AreaInfo(createId(), "8樓", placeTTC.id),
            AreaInfo(createId(), "9樓", placeTTC.id),
            AreaInfo(createId(), "10樓", placeTTC.id),
        ).forEach {
            areas[it.id] = it
        }

        val placeHotel = PlaceInfo(
            createId(),
            "礁溪老爺"
        )

        places[placeHotel.id] = placeHotel
        listOf(
            AreaInfo(createId(), "101", placeHotel.id),
            AreaInfo(createId(), "102", placeHotel.id),
            AreaInfo(createId(), "103", placeHotel.id),
            AreaInfo(createId(), "201", placeHotel.id),
            AreaInfo(createId(), "202", placeHotel.id),
            AreaInfo(createId(), "203", placeHotel.id),
        ).forEach {
            areas[it.id] = it
        }

        val placeStation = PlaceInfo(
            createId(),
            "彰化火車站"
        )

        places[placeStation.id] = placeStation
        listOf(
            AreaInfo(createId(), "車站大廳", placeStation.id),
            AreaInfo(createId(), "司機休息室", placeStation.id),
            AreaInfo(createId(), "員工休息室", placeStation.id),
            AreaInfo(createId(), "售票口", placeStation.id),
            AreaInfo(createId(), "販賣部", placeStation.id),
            AreaInfo(createId(), "遊客中心", placeStation.id),
        ).forEach {
            areas[it.id] = it
        }
    }

    private fun fillUsers() {
        for (i in 0 until 1000){
            users.add(
                FakeUser(
                    Random.nextInt(),
                    "user${Random.nextInt(10000)}",
                    "mail${Random.nextInt(10000)}@mail.com"
                )
            )
        }
    }

    private fun fillShareMember() {
        devices.forEach { (deviceId, device) ->
            val members = hashSetOf<ShareMember>()
            for (i in 0 until Random.nextInt(5)){
                val user = users.random()
                members.add(
                    ShareMember(
                        user.userId,
                        user.name,
                        user.mail,
                        Random.nextBoolean()
                    )
                )
            }
            deviceShareMemberList[deviceId] = members.toMutableList()
        }
    }
}
