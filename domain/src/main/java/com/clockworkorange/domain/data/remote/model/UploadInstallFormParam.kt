package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class UploadInstallFormParam(
    @SerializedName("device_id")
    val deviceId: Int,
    @SerializedName("values")
    val values: List<ApiFormOptionField>
)

