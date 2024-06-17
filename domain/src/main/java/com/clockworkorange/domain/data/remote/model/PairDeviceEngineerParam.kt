package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class PairDeviceEngineerParam(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("customer_address")
    val customerAddress: String
)