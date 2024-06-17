package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenParam(
    @SerializedName("refresh_token")
    val refreshToken: String
)
