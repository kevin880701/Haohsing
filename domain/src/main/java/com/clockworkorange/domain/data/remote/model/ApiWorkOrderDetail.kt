package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiWorkOrderDetail(
    @SerializedName("m_id")
    val mId: Int,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("description")
    val description: String?,
    @SerializedName("vendor_description")
    val vendorDescription: String?,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("status")
    val status: Int,
    @SerializedName("continuance")
    val continuance: Int,
    @SerializedName("engineer")
    val engineerId: Int,
    @SerializedName("engineer_name")
    val engineerName: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("type_name")
    val typeName: String,
    @SerializedName("expected_days_of_week")
    val expectedDaysOfWeek: String?,
    @SerializedName("sign_image")
    val signImage: String?,
    @SerializedName("expected_time_of_week")
    val expectedTimeOfWeek: String,
    @SerializedName("added_time")
    val addedTime: String,
    @SerializedName("appointed_datetime")
    val appointedDatetime: String,
    @SerializedName("work_start_datetime")
    val workStartDatetime: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("tel")
    val tel: String,
    @SerializedName("fee")
    val fee: Int,
    @SerializedName("work")
    val work: Int,
    @SerializedName("error_reason")
    val errorReason: String?,
    @SerializedName("attachment_url")
    val attachmentUrl: String?,
    @SerializedName("vendor_name")
    val vendorName: String,
    @SerializedName("vendor_tel")
    val vendorTel: String,
    @SerializedName("customer_name")
    val customerName: String?,
    @SerializedName("customer_tel")
    private val _customerTel: String?,
    @SerializedName("customer_address")
    private val _customerAddress: String?,
    @SerializedName("values")
    val values: List<ApiFormOption>,
    @SerializedName("user_images")
    val userImages: List<UserImage>,
    @SerializedName("engineer_images")
    val engineerImages: List<EngineerImage>,
    @SerializedName("added_type")
    val addedType: String
) {
    val customerTel get() = _customerTel ?: ""
    val customerAddress get() = _customerAddress ?: ""
}

data class ApiFormOption(
    @SerializedName("code")
    val code: String,
    @SerializedName("types")
    val types: String,
    @SerializedName("group1")
    val group1: String,
    @SerializedName("group2")
    val group2: String,
    @SerializedName("item")
    val item: String,
    @SerializedName("value")
    val value: String,
    @SerializedName("remark")
    val remark: String
)

data class UserImage(
    @SerializedName("m_id")
    val mId: Int,
    @SerializedName("m_u_image_id")
    val mUImageId: Int,
    @SerializedName("image_url")
    val imageUrl: String
)

data class EngineerImage(
    @SerializedName("m_id")
    val mId: Int,
    @SerializedName("m_e_image_id")
    val mEImageId: Int,
    @SerializedName("kind")
    val kind: Int,
    @SerializedName("image_url")
    val imageUrl: String
)