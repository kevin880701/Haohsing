package com.clockworkorange.haohsing.ui.main.pair.user

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class SimpleWiFiInfo(
    @SerializedName("ssid")
    val ssid: String,
    @SerializedName("rssi")
    val rssi: Int = 0
) : Parcelable {

    enum class SignalLevel {
        Leve1,
        Leve2,
        Leve3,
        Leve4
    }

    fun getSignalLevel(): SignalLevel {
        return when {
            rssi >= -50 -> {
                SignalLevel.Leve4
            }
            rssi >= -60 -> {
                SignalLevel.Leve3
            }
            rssi >= -70 -> {
                SignalLevel.Leve2
            }
            else -> SignalLevel.Leve1
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ssid)
        parcel.writeInt(rssi)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SimpleWiFiInfo> {
        override fun createFromParcel(parcel: Parcel): SimpleWiFiInfo {
            return SimpleWiFiInfo(parcel)
        }

        override fun newArray(size: Int): Array<SimpleWiFiInfo?> {
            return arrayOfNulls(size)
        }
    }
}
