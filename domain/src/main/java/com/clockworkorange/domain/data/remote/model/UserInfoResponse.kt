package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("account")
    val account: String,
    @SerializedName("headshot")
    val headshot: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("roles_name")
    val rolesName: String,
    @SerializedName("tel")
    val tel: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("region")
    val region: String?,//鄉鎮,
    @SerializedName("vendor_name")
    val vendorName: String?,
    @SerializedName("vendor_id")
    val vendorId: Int?
)
