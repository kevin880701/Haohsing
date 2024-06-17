package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class ForgetPasswordParam(
    @SerializedName("account")
    val account: String
)
