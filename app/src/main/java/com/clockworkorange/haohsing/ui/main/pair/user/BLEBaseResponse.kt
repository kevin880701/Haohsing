package com.clockworkorange.haohsing.ui.main.pair.user

import com.google.gson.annotations.SerializedName

open class BLEBaseResponse {
    @SerializedName("cmd")
    val cmd: String = ""
}

open class BLEGenericResponse : BLEBaseResponse() {
    @SerializedName("result")
    val result: Int = 0

    fun isSuccess(): Boolean = result == 0
}

class BLEScanWiFiResponse : BLEGenericResponse() {
    @SerializedName("data")
    val wifiList: List<SimpleWiFiInfo> = emptyList()
}

