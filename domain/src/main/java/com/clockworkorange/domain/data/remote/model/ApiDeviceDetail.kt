package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiDeviceDetail(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("mac")
    val mac: String?,
    @SerializedName("type")
    val type: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("address")
    val installAddress: String?,
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
    val modelName: String,
    @SerializedName("model_image_url")
    val modelImageUrl: String,
    @SerializedName("guide_url")
    val manualUrl: String?,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("vendor_name")
    val vendorName: String?,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("customer_name")
    val customerName: String?,
    @SerializedName("customer_address")
    val customerAddress: String?,
    @SerializedName("online")
    val online: Int,
    @SerializedName("statistics")
    val statistics: ApiDeviceStatus?,
    @SerializedName("queues")
    val deviceSchedules: List<ApiDeviceSchedule>?,
    @SerializedName("area_queues")
    val areaSchedules: List<ApiAreaSchedule>?,
    @SerializedName("maintenance_times")
    val noMaintainTime: Int
)

data class ApiDeviceStatus(
    @SerializedName("h08")
    val h08: Int,
    @SerializedName("h09")
    val h09: Int,
    @SerializedName("h0a")
    val h0a: Int,
    @SerializedName("h12a")
    val h12a: String?,
    @SerializedName("h00")
    val h00: Int,
    @SerializedName("h03")
    val h03: Int,
    @SerializedName("h04")
    val h04: Int,
    @SerializedName("p01")
    val p01: Int,
    @SerializedName("h01")
    val h01: Int?,
    @SerializedName("h05")
    val h05: Int?,
    @SerializedName("h07")
    val h07: Int?,
    @SerializedName("h25")
    val h25: String?,
    @SerializedName("h24")
    val h24: String?,
    @SerializedName("h00a")
    val h00a: Int,
    @SerializedName("h2e")
    val h2e: Int,
    @SerializedName("h2e_n")
    val h2en: String
)