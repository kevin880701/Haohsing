package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ApiDevice(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("mac")
    val mac: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("place_name")
    val placeName: String,
    @SerializedName("area_id")
    val areaId: Int,
    @SerializedName("area_name")
    val areaName: String,
    @SerializedName("model_id")
    val modelId: Int,
    @SerializedName("model_name")
    val modelName: String?,
    @SerializedName("model_image_url")
    val modelImageUrl: String,
    @SerializedName("online")
    val online: Int,
    @SerializedName("power")
    val power: Int,
    @SerializedName("error")
    val error: Int,
    @SerializedName("installation_date")
    val installationDate: String?
)