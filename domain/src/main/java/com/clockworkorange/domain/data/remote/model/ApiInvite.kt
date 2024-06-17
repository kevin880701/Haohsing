package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiInvite(
    @SerializedName("owner")
    val owner: String,
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("place_name")
    val placeName: String,
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("names")
    val names: String
)