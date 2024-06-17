package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName


data class ApiDeviceFilter(
    @SerializedName("cl")
    val cl: String,//F1~F5
    @SerializedName("value")
    val value: String,//0:關閉 1:開啟
    @SerializedName("cy")
    val cy: Int,//週期(1:月 2:周 3:天)
    @SerializedName("date_of_month")
    val dateOfMonth: Int,//每月幾號
    @SerializedName("weeks")
    val weeks: String,//每周幾
    @SerializedName("times")
    val times: String,//時間
    @SerializedName("notice")
    val notice: Int,//是否通知
    @SerializedName("name")
    val name: String,
    @SerializedName("installation_date")
    val installationDate: String,//安裝日期
    @SerializedName("life_month")
    val lifeMonth: Int,//濾芯壽命(月)
    @SerializedName("life_rate")
    val lifeRate: Double//濾芯壽命(%)
)