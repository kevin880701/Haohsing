package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiEditScheduleParam(
    @SerializedName("q_id")
    val qId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("weeks")
    val weeks: String,
    @SerializedName("open_times")
    val openTimes: String,
    @SerializedName("sleep_times")
    val sleepTimes: String
)