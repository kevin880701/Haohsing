package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName

data class TransferOwnerParam(
    @SerializedName("account")
    val account: String,
    @SerializedName("device_id")
    val deviceId: List<Int>
)