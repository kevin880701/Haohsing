package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class SharePlaceDeviceParam(
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("account")
    val account: String
)
