package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserPlaceResponse(
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("user_id")
    val userId: Int
)