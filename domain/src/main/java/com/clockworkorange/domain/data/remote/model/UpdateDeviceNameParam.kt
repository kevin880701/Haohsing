package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class UpdateDeviceNameParam(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("name")
    val name: String
)
