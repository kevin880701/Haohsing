package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class UploadFcmTokenParam(
    @SerializedName("token")
    val token: String
)
