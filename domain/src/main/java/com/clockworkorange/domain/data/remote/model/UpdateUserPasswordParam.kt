package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class UpdateUserPasswordParam(
    @SerializedName("password")
    val password: String,
    @SerializedName("newPassword")
    val newPassword: String
)