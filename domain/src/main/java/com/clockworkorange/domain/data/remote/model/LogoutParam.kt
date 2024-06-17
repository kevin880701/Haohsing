package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class LogoutParam(
    @SerializedName("token")
    val fcmToken: String
)
