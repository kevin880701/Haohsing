package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class UpdateWarrantyParam(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("warranty_name")
    val warrantyName: String,
    @SerializedName("warranty_email")
    val warrantyEmail: String,
    @SerializedName("warranty_tel")
    val warrantyTel: String,
    @SerializedName("inv_date")
    val invDate: String
)