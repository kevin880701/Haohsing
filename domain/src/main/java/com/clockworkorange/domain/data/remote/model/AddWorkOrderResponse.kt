package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class AddWorkOrderResponse(
    @SerializedName("m_id")
    val mId: Int,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("status")
    val status: Int,
    @SerializedName("engineer")
    val engineer: Int,
    @SerializedName("type")
    val type: Int,
    @SerializedName("expected_days_of_week")
    val expectedDaysOfWeek: String,
    @SerializedName("sign_image")
    val signImage: String,
    @SerializedName("expected_time_of_week")
    val expectedTimeOfWeek: String,
    @SerializedName("added_time")
    val addedTime: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tel")
    val tel: String,
    @SerializedName("vendor_name")
    val vendorName: String,
    @SerializedName("vendor_tel")
    val vendorTel: String
)