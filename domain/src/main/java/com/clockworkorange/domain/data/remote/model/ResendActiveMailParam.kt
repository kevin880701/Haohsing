package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class ResendActiveMailParam(
    @SerializedName("account")
    val account: String
)
