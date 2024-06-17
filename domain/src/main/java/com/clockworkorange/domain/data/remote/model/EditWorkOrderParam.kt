package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiEditWorkOrderParam(
    @SerializedName("m_id")
    val mId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("tel")
    val tel: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("expected_days_of_week")
    val expectedDaysOfWeek: String,
    @SerializedName("expected_time_of_week")
    val expectedTimeOfWeek: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("codes")
    val errReason: List<ApiFormOptionParam>
)

data class ApiFormOptionParam(
    @SerializedName("code")
    val code: String,
    @SerializedName("value")
    val value: String,//數值(勾選類(0:未選 1:勾選))
    @SerializedName("remark")
    val remark: String
)