package com.clockworkorange.haohsing.ui.main.pair.engineer

import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.usecase.device.DrainStatus
import com.clockworkorange.domain.usecase.task.ChangedFilter
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class PairDeviceInspectionViewModel @Inject constructor(

) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val tds: String? = null,
        val psi: String? = null,
        val checkReliefValve: Boolean = false,
        val voltage: String? = null,
        val checkGround: Boolean = false,
        val drainStatus: DrainStatus? = null,
        val installROFilter: Boolean = false,
        val checkROWork: Boolean = false,
        val extraFilterCount: Int = 0,
        val changedFilter1: ChangedFilter? = null,
        val changedFilter2: ChangedFilter? = null,
        val changedFilter3: ChangedFilter? = null,
        val changedFilter4: ChangedFilter? = null,
        val changedFilter5: ChangedFilter? = null,
        val checkIntakeWaterWork: Boolean = false,
        val checkOutWaterWork: Boolean = false,
        val checkHeatWork: Boolean = false,
        val checkCoolWork: Boolean = false,
        val checkFunctionWork: Boolean = false,
        val checkPowerPlug: Boolean = false,
        val checkPowerSwitch: Boolean = false,
        val checkWaterSwitch: Boolean = false,
        val checkBasicFunction: Boolean = false,
        val checkBasicMaintain: Boolean = false,
        val checkConnectService: Boolean = false,
        val checkGuide: Boolean = false,
        val productImageUri: Uri? = null,
        val faucetImageUri: Uri? = null,
        val breakerImageUri: Uri? = null,
        val waterPressureGaugeImageUri: Uri? = null,
        val pairDeviceInspection: PairDeviceInspection? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

        }
    }

    fun setTDS(tds: String) {
        _uiState.update { it.copy(tds = tds) }
    }

    fun setPsi(psi: String) {
        _uiState.update { it.copy(psi = psi) }
    }

    fun setVoltage(voltage: String) {
        _uiState.update { it.copy(voltage = voltage) }
    }


    fun setDrainStatus(status: DrainStatus) {
        _uiState.update { it.copy(drainStatus = status) }
    }

    fun clickCheckReliefValve() {
        _uiState.update { it.copy(checkReliefValve = !it.checkReliefValve) }
    }

    fun clickCheckGround() {
        _uiState.update { it.copy(checkGround = !it.checkGround) }
    }

    fun clickInstallROFilter() {
        _uiState.update { it.copy(installROFilter = !it.installROFilter) }
    }

    fun clickRoWork() {
        _uiState.update { it.copy(checkROWork = !it.checkROWork) }
    }

    fun addExtraFilter() {
        val newCount = uiState.value.extraFilterCount + 1
        if (newCount >= 5) return
        _uiState.update { it.copy(extraFilterCount = newCount) }
    }

    fun setChangedFilter1(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter1 = filter) }
    }

    fun setChangedFilter2(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter2 = filter) }
    }

    fun setChangedFilter3(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter3 = filter) }
    }

    fun setChangedFilter4(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter4 = filter) }
    }

    fun setChangedFilter5(filter: ChangedFilter) {
        _uiState.update { it.copy(changedFilter5 = filter) }
    }

    fun clickIntakeWaterWork() {
        _uiState.update { it.copy(checkIntakeWaterWork = !it.checkIntakeWaterWork) }
    }

    fun clickOutWaterWork() {
        _uiState.update { it.copy(checkOutWaterWork = !it.checkOutWaterWork) }
    }

    fun clickHeatWork() {
        _uiState.update { it.copy(checkHeatWork = !it.checkHeatWork) }
    }

    fun clickCoolWork() {
        _uiState.update { it.copy(checkCoolWork = !it.checkCoolWork) }
    }

    fun clickFunctionWork() {
        _uiState.update { it.copy(checkFunctionWork = !it.checkFunctionWork) }
    }

    fun clickPowerPlug() {
        _uiState.update { it.copy(checkPowerPlug = !it.checkPowerPlug) }
    }

    fun clickPowerSwitch() {
        _uiState.update { it.copy(checkPowerSwitch = !it.checkPowerSwitch) }
    }

    fun clickWaterSwitch() {
        _uiState.update { it.copy(checkWaterSwitch = !it.checkWaterSwitch) }
    }

    fun clickBasicFunction() {
        _uiState.update { it.copy(checkBasicFunction = !it.checkBasicFunction) }
    }

    fun clickBasicMaintain() {
        _uiState.update { it.copy(checkBasicMaintain = !it.checkBasicMaintain) }
    }

    fun clickConnectService() {
        _uiState.update { it.copy(checkConnectService = !it.checkConnectService) }
    }

    fun clickGuide() {
        _uiState.update { it.copy(checkGuide = !it.checkGuide) }
    }

    fun setProductImage(uri: Uri) {
        _uiState.update { it.copy(productImageUri = uri) }
    }

    fun removeProductImage() {
        _uiState.update { it.copy(productImageUri = null) }
    }

    fun setFaucetImage(uri: Uri) {
        _uiState.update { it.copy(faucetImageUri = uri) }
    }

    fun removeFaucetImage() {
        _uiState.update { it.copy(faucetImageUri = null) }
    }

    fun setBreakerImage(uri: Uri) {
        _uiState.update { it.copy(breakerImageUri = uri) }
    }

    fun removeBreakerImage() {
        _uiState.update { it.copy(breakerImageUri = null) }
    }

    fun setWaterPressureGaugeImage(uri: Uri) {
        _uiState.update { it.copy(waterPressureGaugeImageUri = uri) }
    }

    fun removeWaterPressureGaugeImage() {
        _uiState.update { it.copy(waterPressureGaugeImageUri = null) }
    }

    fun clickNext() {

        val pairDeviceInspection = PairDeviceInspection(
            uiState.value.tds,
            uiState.value.psi,
            uiState.value.checkReliefValve,
            uiState.value.voltage,
            uiState.value.checkGround,
            uiState.value.drainStatus,
            uiState.value.installROFilter,
            uiState.value.checkROWork,
            uiState.value.changedFilter1,
            uiState.value.changedFilter2,
            uiState.value.changedFilter3,
            uiState.value.changedFilter4,
            uiState.value.changedFilter5,
            uiState.value.checkIntakeWaterWork,
            uiState.value.checkOutWaterWork,
            uiState.value.checkHeatWork,
            uiState.value.checkCoolWork,
            uiState.value.checkFunctionWork,
            uiState.value.checkPowerPlug,
            uiState.value.checkPowerSwitch,
            uiState.value.checkWaterSwitch,
            uiState.value.checkBasicFunction,
            uiState.value.checkBasicMaintain,
            uiState.value.checkConnectService,
            uiState.value.checkGuide,
            uiState.value.productImageUri,
            uiState.value.faucetImageUri,
            uiState.value.breakerImageUri,
            uiState.value.waterPressureGaugeImageUri
        )
        _uiState.update { it.copy(pairDeviceInspection = null) }
        _uiState.update { it.copy(pairDeviceInspection = pairDeviceInspection) }
    }

}

@Parcelize
data class PairDeviceInspection(
    val tds: String? = null,
    val psi: String? = null,
    val checkReliefValve: Boolean = false,
    val voltage: String? = null,
    val checkGround: Boolean = false,
    val drainStatus: DrainStatus? = null,
    val installROFilter: Boolean = false,
    val checkROWork: Boolean = false,
    val changedFilter1: ChangedFilter? = null,
    val changedFilter2: ChangedFilter? = null,
    val changedFilter3: ChangedFilter? = null,
    val changedFilter4: ChangedFilter? = null,
    val changedFilter5: ChangedFilter? = null,
    val checkIntakeWaterWork: Boolean = false,
    val checkOutWaterWork: Boolean = false,
    val checkHeatWork: Boolean = false,
    val checkCoolWork: Boolean = false,
    val checkFunctionWork: Boolean = false,
    val checkPowerPlug: Boolean = false,
    val checkPowerSwitch: Boolean = false,
    val checkWaterSwitch: Boolean = false,
    val checkBasicFunction: Boolean = false,
    val checkBasicMaintain: Boolean = false,
    val checkConnectService: Boolean = false,
    val checkGuide: Boolean = false,
    val productImageUri: Uri? = null,
    val faucetImageUri: Uri? = null,
    val breakerImageUri: Uri? = null,
    val waterPressureGaugeImageUri: Uri? = null
) : Parcelable