package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiPlaceDevicePermission(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("area_id")
    val areaId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("area_name")
    val areaName: String
)