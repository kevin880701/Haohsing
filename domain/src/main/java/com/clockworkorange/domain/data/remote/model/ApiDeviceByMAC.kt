package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiDeviceByMAC(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("sn")
    val sn: String?,
    @SerializedName("mac")
    val mac: String,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("model_id")
    val modelId: Int,
    @SerializedName("model_name")
    val modelName: String,
    @SerializedName("model_image_url")
    val modelImageUrl: String,
    @SerializedName("vendor_id")
    val vendorId: Int?,
    @SerializedName("vendor_name")
    val vendorName: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("customer_id")
    val customerId: Int?,
    @SerializedName("customer_name")
    val customerName: String?,
    @SerializedName("warranty")
    val warranty: Int
)