package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiCustomer(
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("tel")
    val tel: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("contact_name")
    val contactName: String
)