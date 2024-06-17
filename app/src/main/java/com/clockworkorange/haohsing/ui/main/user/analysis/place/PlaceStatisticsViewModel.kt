package com.clockworkorange.haohsing.ui.main.user.analysis.place

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.domain.usecase.device.GetPlaceDataAnalysisParam
import com.clockworkorange.domain.usecase.device.GetPlaceDataAnalysisUseCase
import com.clockworkorange.domain.usecase.device.HourValue
import com.clockworkorange.domain.usecase.device.PlaceStatistics
import com.clockworkorange.domain.usecase.palcearea.GetAreaListUseCase
import com.clockworkorange.domain.usecase.palcearea.GetPlaceListUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class PlaceStatisticsViewModel @Inject constructor(
    private val getPlaceListUseCase: GetPlaceListUseCase,
    private val getAreaListUseCase: GetAreaListUseCase,
    private val getPlaceDataAnalysisUseCase: GetPlaceDataAnalysisUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val placeList: List<PlaceInfo> = emptyList(),
        val areaList: List<AreaInfo> = emptyList(),
        val yearMonth: YearMonth = YearMonth.now(),
        val placeStatistics: PlaceStatistics? = null,
        val waterHourValues: List<HourValue>? = null,
        val powerHourValues: List<HourValue>? = null,
        val suggestMsg: List<String>? = null,
    )

    private val selectPlaceFlow = MutableStateFlow<PlaceInfo?>(null)
    private val selectAreaFlow = MutableStateFlow<AreaInfo?>(null)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                getPlaceListUseCase.invoke(Unit)
                    .filter { it is Result.Success }
                    .collect {
                        it.data?.let { placeInfoList ->
                            _uiState.update { it.copy(placeList = placeInfoList) }
                        }
                    }
            }

            launch {
                selectPlaceFlow
                    .filterNotNull()
                    .combine(uiState.map { it.yearMonth }) { place, yearMonth -> place to yearMonth }
                    .distinctUntilChanged()
                    .map { (placeInfo, yearMonth) ->
                        _uiState.update { it.copy(isLoading = true) }
                        val result = getPlaceDataAnalysisUseCase.invoke(GetPlaceDataAnalysisParam(placeInfo.id, yearMonth))
                        _uiState.update { it.copy(isLoading = false) }
                        result
                    }
                    .filter { it is Result.Success }
                    .map { it.data!! }
                    .onEach { placeStatistics->
                        _uiState.update { it.copy(placeStatistics = placeStatistics)}
                    }.combine( selectAreaFlow.filterNotNull() ) { a, b -> a to b }
                    .onEach { (placeStatistics, selectedArea) ->
                        setAreaHourUsage(placeStatistics, selectedArea)
                    }.collect()
            }

            launch {
                selectPlaceFlow
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collect { updateAreaList(it.id) }
            }

        }
    }

    private fun setAreaHourUsage(
        placeStatistics: PlaceStatistics,
        selectedArea: AreaInfo
    ) {
        if (selectedArea == AreaInfo.ALL) {
            val sumOfWaterHourValues = placeStatistics.waterAreaHours.flatMap { it.hourValues }
                .groupBy({ it.hour }, { it.value }).map { HourValue(it.key, it.value.sum()) }
                .takeIf { it.isNotEmpty() }
            val sumOfPowerHourValues = placeStatistics.powerAreaHours.flatMap { it.hourValues }
                .groupBy({ it.hour }, { it.value }).map { HourValue(it.key, it.value.sum()) }
                .takeIf { it.isNotEmpty() }
            _uiState.update {
                it.copy(
                    waterHourValues = sumOfWaterHourValues,
                    powerHourValues = sumOfPowerHourValues,
                    suggestMsg = null
                )
            }
        } else {
            val areaWaterHourUsage = placeStatistics.waterAreaHours.find { it.areaId == selectedArea.id }
            val waterHourValues = areaWaterHourUsage?.hourValues
            val waterSuggest = areaWaterHourUsage?.suggest

            val areaPowerHourUsage = placeStatistics.powerAreaHours.find { it.areaId == selectedArea.id }
            val powerHourValues = areaPowerHourUsage?.hourValues
            val powerSuggest = areaPowerHourUsage?.suggest

            val suggestMsg = mutableListOf<String>().apply {
                waterSuggest?.let { addAll(it) }
                powerSuggest?.let { addAll(it) }
            }.takeIf { it.isNotEmpty() }

            _uiState.update {
                it.copy(
                    waterHourValues = waterHourValues,
                    powerHourValues = powerHourValues,
                    suggestMsg = suggestMsg
                )
            }
        }
    }

    fun selectPlace(place: PlaceInfo?) {
        place ?: return
        selectPlaceFlow.tryEmit(place)
    }

    fun selectArea(area: AreaInfo) {
        selectAreaFlow.tryEmit(area)
    }

    fun getArea(): AreaInfo? {
        return selectAreaFlow.value
    }

    private fun updateAreaList(placeId: Int) {
        viewModelScope.launch {
            val result = getAreaListUseCase.invoke(placeId)
            result.data?.let { areaList ->
                _uiState.update { it.copy(areaList = listOf(AreaInfo.ALL) + areaList) }
            }
        }
    }

    fun previousMonth() {
        val newYearMonth = uiState.value.yearMonth.minusMonths(1)
        _uiState.update { it.copy(yearMonth = newYearMonth) }
    }

    fun nextMonth() {
        val newYearMonth = uiState.value.yearMonth.plusMonths(1)
        _uiState.update { it.copy(yearMonth = newYearMonth) }
    }

    fun setCurrentYearMonth(yearMonth: YearMonth) {
        _uiState.update { it.copy(yearMonth = yearMonth) }
    }

}