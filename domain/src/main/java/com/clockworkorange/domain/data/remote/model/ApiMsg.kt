package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiMsg(
    @SerializedName("msg_id")
    val msgId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("type")
    val type: Int,//1:裝置通知 2:保修單通知 3:保修單逾期通知 4:能源管理通知
    @SerializedName("subject")
    val subject: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("added_time")
    val addedTime: String,
    @SerializedName("device_id")
    val deviceId: Int?,
    @SerializedName("m_id")
    val mId: Int?
)