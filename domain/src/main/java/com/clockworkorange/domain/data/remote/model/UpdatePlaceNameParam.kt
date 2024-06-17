package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class UpdatePlaceNameParam(
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("name")
    val name: String
)
