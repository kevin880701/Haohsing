package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class PairDeviceUserParam(
    @SerializedName("sn")
    val sn: String?,
    @SerializedName("mac")
    val mac: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("place_id")
    val placeId: Int?,
    @SerializedName("area_id")
    val areaId: Int?,
    @SerializedName("customer_id")
    val customerId: Int?,
    @SerializedName("customer_address")
    val customerAddress: String?,
)