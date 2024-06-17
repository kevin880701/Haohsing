package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class RegisterParam(
    @SerializedName("account")
    val account: String,
    @SerializedName("secret")
    val password: String
)
