package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class SetDeviceValueParam(
    @SerializedName("id")
    val id: String,
    @SerializedName("value")
    val value: String
)
