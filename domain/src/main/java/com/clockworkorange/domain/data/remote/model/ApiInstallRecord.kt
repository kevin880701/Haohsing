package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName

data class ApiInstallRecord(
    @SerializedName("mac")
    val mac: String,
    @SerializedName("model_name")
    val modelName: String,
    @SerializedName("customer_name")
    val customerName: String,
    @SerializedName("customer_area")
    val customerArea: String,
    @SerializedName("installation_date")
    val installationDate: String
)
