package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class CancelWorkOrderParam(
    @SerializedName("m_id")
    val id: Int,
    @SerializedName("status")
    val status: Int = 3
)
