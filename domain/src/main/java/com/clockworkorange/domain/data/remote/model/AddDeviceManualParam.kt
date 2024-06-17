package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class AddDeviceManualParam(
    @SerializedName("area_id")
    val areaId: Int? = null,
    @SerializedName("filters")
    val filters: List<FilterParam>? = null,
    @SerializedName("manual_brand")
    val brand: String? = null,
    @SerializedName("manual_model")
    val model: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("place_id")
    val placeId: Int? = null,
    @SerializedName("vendor_id")
    val vendorId: Int? = null
)

data class FilterParam(
    @SerializedName("cl")
    val cl: String,
    @SerializedName("installation_date")
    val installationDate: String,
    @SerializedName("life_month")
    val lifeMonth: Int,
    @SerializedName("name")
    val name: String
)