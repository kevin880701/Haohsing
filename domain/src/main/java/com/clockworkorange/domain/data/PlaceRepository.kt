package com.clockworkorange.domain.data

import com.clockworkorange.domain.data.remote.ServiceWrapper
import com.clockworkorange.domain.data.remote.model.UpdatePlaceDevicePermissionParam
import com.clockworkorange.domain.di.ApplicationScope
import com.clockworkorange.domain.entity.*
import com.clockworkorange.domain.usecase.device.ShareMember
import com.clockworkorange.domain.usecase.palcearea.PlaceShareMember
import com.clockworkorange.domain.usecase.palcearea.PlaceSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

interface PlaceRepository {
    suspend fun getPlaceSummary(): List<PlaceSummary>

    suspend fun updatePlaceName(placeId: Int, newName: String):Boolean
    suspend fun addPlace(name: String): Int
    suspend fun deletePlace(placeId: Int): Boolean

    suspend fun getAreaList(placeId: Int): List<AreaInfo>
    suspend fun updateAreaName(areaId: Int, newName: String): Boolean
    suspend fun addArea(placeId: Int, name: String): Int
    suspend fun deleteArea(areaId: Int): Boolean
    suspend fun getPlaceDeviceShareMemberList(placeId: Int): List<PlaceShareMember>
    suspend fun addPlaceDeviceShareMember(placeId: Int, mail: String): Boolean
    suspend fun deletePlaceDeviceShareMember(placeId: Int, userId: Int): Boolean
    suspend fun getPlaceDeviceList(placeId: Int): List<Device>
    suspend fun updatePlaceDeviceShareMember(placeId: Int, userId: Int, addShareIds: List<Int>, removeShareIds: List<Int>): Boolean
    fun getPlaceList(): Flow<List<PlaceInfo>>
}

class PlaceRepositoryImpl constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val service: ServiceWrapper
): PlaceRepository{

    override suspend fun getPlaceSummary(): List<PlaceSummary> {
        return service.getPlaceDetailList().map { it.toPlaceSummary() }
    }

    override suspend fun updatePlaceName(placeId: Int, newName: String): Boolean {
        return service.updatePlaceName(placeId, newName).also { externalScope.launch { fetchPlaceList() } }
    }

    override suspend fun addPlace(name: String): Int {
        return service.addPlace(name).also { externalScope.launch { fetchPlaceList() } }
    }

    override suspend fun deletePlace(placeId: Int): Boolean {
        return service.deletePlace(placeId).also { externalScope.launch { fetchPlaceList() } }
    }

    override suspend fun updateAreaName(areaId: Int, newName: String): Boolean {
        return service.updateAreaName(areaId, newName)
    }

    override suspend fun addArea(placeId: Int, name: String): Int {
        return service.addArea(placeId, name)
    }

    override suspend fun deleteArea(areaId: Int): Boolean {
        return service.deleteArea(areaId)
    }

    override suspend fun getAreaList(placeId: Int): List<AreaInfo> {
        return service.getAreaList(placeId).map { it.toAreaInfo() }
    }

    override suspend fun getPlaceDeviceShareMemberList(placeId: Int): List<PlaceShareMember> {
        return service.getPlaceShareMemberList(placeId).map { it.toPlaceShareMember() }
    }

    override suspend fun addPlaceDeviceShareMember(placeId: Int, mail: String): Boolean {
        return service.sharePlaceDevice(placeId, mail)
    }

    override suspend fun deletePlaceDeviceShareMember(placeId: Int, userId: Int): Boolean {
        return service.deletePlaceDeviceShareMember(userId, placeId)
    }

    override suspend fun getPlaceDeviceList(placeId: Int): List<Device> {
        return service.getPlaceDeviceList(placeId).map { it.toDevice() }
    }

    override suspend fun updatePlaceDeviceShareMember(
        placeId: Int,
        userId: Int,
        addShareIds: List<Int>,
        removeShareIds: List<Int>
    ): Boolean {
        val data = mutableListOf<UpdatePlaceDevicePermissionParam>()

        addShareIds.forEach {  deviceId ->
            data.add(UpdatePlaceDevicePermissionParam(deviceId, userId, true))
        }

        removeShareIds.forEach {  deviceId ->
            data.add(UpdatePlaceDevicePermissionParam(deviceId, userId, false))
        }

        return service.updatePlaceDevicePermission(data)
    }

    private val placeListFlow = MutableStateFlow<List<PlaceInfo>>(emptyList())

    override fun getPlaceList(): Flow<List<PlaceInfo>> {
        externalScope.launch { fetchPlaceList() }
        return placeListFlow
    }
    private suspend fun fetchPlaceList(){
        placeListFlow.tryEmit(service.getPlaceList().map { it.toPlaceInfo() })
    }

}

class FakePlaceRepository: PlaceRepository{
    override suspend fun getPlaceSummary(): List<PlaceSummary> {
        return FakeData.places.values.map { place ->
            val areaCount = FakeData.areas.values.filter { area -> area.placeId == place.id }.size
            val shareMemberCount = FakeData.devices.values.filter { it.placeId == place.id }
                .map { FakeData.deviceShareMemberList[it.id]!! }.flatten().toHashSet().size

            PlaceSummary(
                place.id,
                place.name,
                areaCount,
                shareMemberCount
            )
        }
    }

    override suspend fun updatePlaceName(placeId: Int, newName: String): Boolean {
        val place = FakeData.places[placeId] ?: return false
        FakeData.places[placeId] = place.copy(name = newName)
        FakeData.devices.filter { (id, device) -> device.placeId == placeId }
            .forEach { (id, device) ->
                FakeData.devices[id] = device.copy(placeInfo = FakeData.places[placeId]!!)
            }
        return true
    }

    override suspend fun addPlace(name: String): Int {
        val place = PlaceInfo(Random.nextInt(), name)
        FakeData.places[place.id] = place
        return place.id
    }

    override suspend fun deletePlace(placeId: Int): Boolean {
        FakeData.places.remove(placeId)
        return true
    }

    override suspend fun getAreaList(placeId: Int): List<AreaInfo> {
        return FakeData.areas.values.filter { it.placeId == placeId }
            .map { AreaInfo(it.id, it.name, it.placeId) }
    }

    override suspend fun updateAreaName(areaId: Int, newName: String): Boolean {
        val area = FakeData.areas[areaId] ?: return false
        FakeData.areas[areaId] = area.copy(name = newName)
        FakeData.devices.filter { (id, device) -> device.areaId == areaId }
            .forEach { (id, device) ->
                FakeData.devices[id] = device.copy(areaInfo = FakeData.areas[areaId])
            }
        return true
    }

    override suspend fun addArea(placeId: Int, name: String): Int {
        val area = AreaInfo(Random.nextInt(), name, placeId)
        FakeData.areas[area.id] = area
        return area.id
    }

    override suspend fun deleteArea(areaId: Int): Boolean {
        FakeData.areas.remove(areaId)
        return true
    }

    override suspend fun getPlaceDeviceShareMemberList(placeId: Int): List<PlaceShareMember> {
        return FakeData.devices.values.filter { it.placeId == placeId }
            .map { FakeData.deviceShareMemberList[it.id]!! }.flatten().toHashSet()
            .map { shareMember ->
                val deviceCount = FakeData.devices.values.count {
                    it.placeId == placeId
                            && FakeData.deviceShareMemberList[it.id]?.find { it.userId ==  shareMember.userId} != null }
                PlaceShareMember(shareMember, deviceCount)
            }.toList()
    }

    override suspend fun addPlaceDeviceShareMember(placeId: Int, mail: String): Boolean {
        val user = FakeData.users.find { it.mail == mail } ?: return false
        val placeDevices = FakeData.devices.values.filter { it.placeId == placeId }

        placeDevices.forEach {
            FakeData.deviceShareMemberList[it.id]?.add(ShareMember(user.userId, user.name, user.mail, false))
        }

        return true
    }

    override suspend fun deletePlaceDeviceShareMember(placeId: Int, userId: Int): Boolean {
        val placeDevices = FakeData.devices.values.filter { it.placeId == placeId }
        placeDevices.forEach {
            FakeData.deviceShareMemberList[it.id]?.removeIf { it.userId == userId }
        }
        return true
    }

    override suspend fun getPlaceDeviceList(placeId: Int): List<Device> {
        return FakeData.devices.values.filter { it.placeId == placeId }
    }

    override suspend fun updatePlaceDeviceShareMember(
        placeId: Int,
        userId: Int,
        addShareIds: List<Int>,
        removeShareIds: List<Int>
    ): Boolean {
        //更新區域裝置分享成員
        val user = FakeData.users.find { it.userId == userId } ?: return false
        val allPlaceDevices = FakeData.devices.values.filter { it.placeId == placeId }
        //update permission
        allPlaceDevices.forEach { device ->
            val deviceShareMemberList = FakeData.deviceShareMemberList[device.id]
            if (device.id in addShareIds){
                //add permission
                if (deviceShareMemberList?.find { it.userId == userId } != null){
                    //已有權限
                    return@forEach
                }else{
                    deviceShareMemberList?.add(ShareMember(user.userId, user.name, user.mail, true))
                }
            }

            if (device.id in removeShareIds){
                //remove permission
                if (deviceShareMemberList?.find { it.userId == userId } == null){
                    //已無權限
                    return@forEach
                }else{
                    deviceShareMemberList.removeIf { it.userId == userId }
                }
            }
        }
        return true
    }

    override fun getPlaceList(): Flow<List<PlaceInfo>> {
        TODO("Not yet implemented")
    }
}