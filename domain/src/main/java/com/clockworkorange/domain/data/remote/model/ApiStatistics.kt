package com.clockworkorange.domain.data.remote.model
import com.google.gson.annotations.SerializedName
import kotlin.random.Random

data class ApiPlaceStatistics(
    @SerializedName("filter_life")
    val filterLife: Int?,
    @SerializedName("filter_life_compare")
    val filterLifeAverage: Int?,
    @SerializedName("power_area_hours")
    val powerAreaHours: List<ApiAreaHourUsage>,
    @SerializedName("power_areas")
    val powerAreas: List<ApiAreaValue>,
    @SerializedName("power_month")
    val powerMonth: Int?,
    @SerializedName("power_month_compare")
    val powerMonthCompare: Int?,
    @SerializedName("power_today")
    val powerToday: Int?,
    @SerializedName("power_today_compare")
    val powerTodayCompare: Int?,
    @SerializedName("volume_area_hours")
    val waterAreaHours: List<ApiAreaHourUsage>,
    @SerializedName("volume_areas")
    val waterAreas: List<ApiAreaValue>?,
    @SerializedName("volume_month")
    val waterMonth: Int?,
    @SerializedName("volume_month_compare")
    val waterMonthCompare: Int?,
    @SerializedName("volume_today")
    val waterToday: Int?,
    @SerializedName("volume_today_compare")
    val waterTodayCompare: Int?,
    @SerializedName("device_total")
    val deviceCount: Int?
) {
    fun isPowerDayMonthNotExist() = (powerToday == null || powerToday == 0) &&
                (powerTodayCompare == null || powerTodayCompare == 0) &&
                (powerMonth == null || powerMonth == 0) &&
                (powerMonthCompare == null || powerMonthCompare == 0)
}

data class ApiDeviceStatistics(
    @SerializedName("power_today")
    val powerToday: Int,
    @SerializedName("power_today_compare")
    val powerTodayCompare: Int?,
    @SerializedName("power_month")
    val powerMonth: Int,
    @SerializedName("power_month_compare")
    val powerMonthCompare: Int?,
    @SerializedName("volume_today")
    val waterToday: Int,
    @SerializedName("volume_today_compare")
    val waterTodayCompare: Int?,
    @SerializedName("volume_month")
    val waterMonth: Int,
    @SerializedName("volume_month_compare")
    val waterMonthCompare: Int?,
    @SerializedName("volume_area_hours")
    val waterAreaHours: List<ApiAreaHourUsage>,
    @SerializedName("power_area_hours")
    val powerAreaHours: List<ApiAreaHourUsage>,
) {
    fun isPowerDayMonthNotExist() = (powerToday == 0) &&
            (powerTodayCompare == null || powerTodayCompare == 0) &&
            (powerMonth == 0) &&
            (powerMonthCompare == null || powerMonthCompare == 0)

}

data class ApiAreaValue(
    @SerializedName("area_id")
    val areaId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("value_int")
    val value: Int
)

data class ApiAreaHourUsage(
    @SerializedName("area_id")
    val areaId: Int,
    @SerializedName("hours")
    val hours: List<ApiHourValue>,
    @SerializedName("name")
    val name: String,
    @SerializedName("suggests")
    val suggests: List<String>,
)

data class ApiHourValue(
    @SerializedName("hour")
    val hour: Int,
    @SerializedName("value_int")
    val value: Int
)
