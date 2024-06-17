package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class UpdatePlaceDevicePermissionParam(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("share")
    val share: Boolean
)