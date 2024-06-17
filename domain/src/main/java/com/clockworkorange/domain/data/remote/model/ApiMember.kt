package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiMember(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("account")
    val account: String,
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("status")
    val status: Int
)