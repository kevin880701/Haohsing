package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class UpdateDevicePlaceAreaParam(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("area_id")
    val areaId: Int? = null
)
