package com.clockworkorange.haohsing.ui.main.pair.user

import com.google.gson.annotations.SerializedName

data class DeviceInfo(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("ver")
    val ver: String,
    @SerializedName("ssid")
    val ssid: String,
    @SerializedName("ip")
    val ip: String,
    @SerializedName("rssi")
    val rssi: String
)