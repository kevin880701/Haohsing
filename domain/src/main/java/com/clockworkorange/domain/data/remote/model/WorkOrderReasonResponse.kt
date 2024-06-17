package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class WorkOrderReasonResponse(
    @SerializedName("code")
    val code: String,
    @SerializedName("types")
    val types: String,
    @SerializedName("group1")
    val group1: String,
    @SerializedName("group2")
    val group2: String,
    @SerializedName("item")
    val item: String
)