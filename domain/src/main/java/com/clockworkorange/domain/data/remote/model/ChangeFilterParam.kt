package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ChangeFilterParam(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("cl")
    val cl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("installation_date")
    val installationDate: String,
    @SerializedName("life_month")
    val lifeMonth: Int
)