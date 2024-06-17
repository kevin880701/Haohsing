package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.data.toDeviceStatistics
import com.clockworkorange.domain.data.toPlaceStatistics
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.time.YearMonth
import javax.inject.Inject
import kotlin.random.Random

class GetPlaceDataAnalysisUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<GetPlaceDataAnalysisParam, PlaceStatistics>(dispatcher) {

    override suspend fun execute(parameters: GetPlaceDataAnalysisParam): PlaceStatistics {
        return deviceRepository.getPlaceDataAnalysis(parameters.placeId, parameters.yearMonth).toPlaceStatistics()
    }
}

data class GetPlaceDataAnalysisParam(
    val placeId: Int,
    val yearMonth: YearMonth,
)

data class PlaceStatistics(
    val deviceCount: Int,
    val avgFilterLifeInDays: Int,
    val filterLifeSuggest: String,
    val waterDayMonthUsage: DayMonthUsage,
    val waterAreas: List<AreaValue>?,
    val powerDayMonthUsage: DayMonthUsage?,
    val powerAreas: List<AreaValue>?,
    val waterAreaHours: List<AreaHourUsage>,
    val powerAreaHours: List<AreaHourUsage>,
)

class GetDeviceStatisticsUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, DeviceStatistics>(dispatcher) {

    override suspend fun execute(parameters: Int): DeviceStatistics {
        return deviceRepository.getDeviceStatistics(parameters).toDeviceStatistics()
    }
}

data class DeviceStatistics(
    val powerDayMonthUsage: DayMonthUsage?,
    val waterDayMonthUsage: DayMonthUsage,
    val powerHourValues: List<HourValue>?,
    val waterHourValues: List<HourValue>?,
    val suggest: String?
)

data class DayMonthUsage(
    val todayValue: Int,
    val todayUpDown: Int,
    val monthValue: Int,
    val monthUpDown: Int
) {
    companion object{
        fun createRandom(): DayMonthUsage{
            return DayMonthUsage(
                (Random.nextFloat()*50).toInt(),
                (Random.nextFloat()*10).toInt(),
                (Random.nextFloat()*2500).toInt(),
                (Random.nextFloat()*10).toInt()
            )
        }
    }
}

data class AreaValue(
    val areaId: Int,
    val name: String,
    val value: Int
)

data class AreaHourUsage(
    val areaId: Int,
    val name: String,
    val hourValues: List<HourValue>,
    val suggest: List<String>
) {
    companion object {
        fun createTestList(): List<AreaHourUsage> {
            val list = mutableListOf<AreaHourUsage>()
            list.add(
                AreaHourUsage(
                    areaId = 46,
                    name = "test",
                    HourValue.createTestList(3),
                    suggest = listOf("suggest")
                )
            )
            list.add(
                AreaHourUsage(
                    areaId = 50,
                    name = "test",
                    HourValue.createTestList(3),
                    suggest = listOf("suggest")
                )
            )
            list.add(
                AreaHourUsage(
                    areaId = 68,
                    name = "test",
                    HourValue.createTestList(3),
                    suggest = listOf("suggest")
                )
            )
            list.add(
                AreaHourUsage(
                    areaId = 91,
                    name = "test",
                    HourValue.createTestList(3),
                    suggest = listOf("suggest")
                )
            )
            list.add(
                AreaHourUsage(
                    areaId = 96,
                    name = "test",
                    HourValue.createTestList(3),
                    suggest = listOf("suggest")
                )
            )
            return list
        }
    }
}

data class HourValue(
    val hour: Int,
    val value: Int
) {
    companion object {
        fun createRandomList(): List<HourValue> {
            val list = mutableListOf<HourValue>()
            for(i in 0 until 24) {
                list.add(HourValue(i, Random.nextInt(1,5)))
            }
            return list
        }
        fun createTestList(value: Int): List<HourValue> {
            val list = mutableListOf<HourValue>()
            for(i in 0 until 3) {
                list.add(HourValue(i, value))
            }
            for(i in 3 until 24) {
                list.add(HourValue(i, Random.nextInt(1,2)))
            }
            return list
        }
    }
}


