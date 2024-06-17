package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class AreaResponse(
    @SerializedName("area_id")
    val areaId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("place_id")
    val placeId: Int
)