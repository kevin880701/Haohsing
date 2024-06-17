package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class ShareDeviceParam(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("account")
    val account: String
)
