package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiTaskSummary(
    @SerializedName("m_id")
    val mId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String?,
    @SerializedName("continuance")
    val continuance: Int,
    @SerializedName("appointed_datetime")
    val appointedDatetime: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("type_name")
    val typeName: String?,
    @SerializedName("status_name")
    val statusName: String,
    @SerializedName("added_time")
    val addedTime: String
)