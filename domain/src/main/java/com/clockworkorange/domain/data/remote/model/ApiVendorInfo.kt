package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiVendorInfo(
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("tel")
    val tel: String,
    @SerializedName("tax_id")
    val taxId: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("address")
    val address: String
)