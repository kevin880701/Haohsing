package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiWorkOrderStatus(
    @SerializedName("m_id")
    val workOrderId: Int,
    @SerializedName("added_time")
    val addedTime: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("type_name")
    val typeName: String,
    @SerializedName("status_name")
    val statusName: String
)