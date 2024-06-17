package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiPlaceMember(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("account")
    val account: String,
    @SerializedName("device_num")
    val deviceNum: Int,
    @SerializedName("invite")
    val invite: Boolean
)