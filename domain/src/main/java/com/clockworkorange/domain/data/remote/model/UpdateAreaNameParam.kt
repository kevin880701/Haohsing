package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class UpdateAreaNameParam(
    @SerializedName("area_id")
    val areaId: Int,
    @SerializedName("name")
    val name: String
)
