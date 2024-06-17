package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiDeviceSchedule(
    @SerializedName("q_id")
    val qId: Int,
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("weeks")
    val weeks: String,
    @SerializedName("open_times")
    val openTimes: String,
    @SerializedName("sleep_times")
    val sleepTimes: String
)

data class ApiAreaSchedule(
    @SerializedName("q_id")
    val qId: Int,
    @SerializedName("area_id")
    val areaId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("weeks")
    val weeks: String,
    @SerializedName("open_times")
    val openTimes: String,
    @SerializedName("sleep_times")
    val sleepTimes: String
)