package com.clockworkorange.haohsing.ui.main.pair.engineer

import android.os.Build
import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.usecase.customer.CustomerAgency
import com.clockworkorange.domain.usecase.device.*
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.domain.usecase.user.GetUserTokenUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import com.clockworkorange.haohsing.ui.main.pair.user.BluetoothController
import com.clockworkorange.haohsing.ui.main.pair.user.GPSController
import com.clockworkorange.haohsing.ui.main.pair.user.GatewayDevice
import com.clockworkorange.haohsing.ui.main.pair.user.SimpleWiFiInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class EngineerPairViewModel @Inject constructor(
    private val gpsController: GPSController,
    private val bleController: BluetoothController,
    private val getUserTokenUseCase: GetUserTokenUseCase,
    private val searchDeviceByMACUseCase: SearchDeviceByMACUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val finishEngineerPairUseCase: FinishEngineerPairUseCase,
    private val addDeviceManuallyUseCase: AddDeviceManuallyUseCase,
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val qrcodeSearchedDevice: SearchedDevice? = null,
        val userInfo: UserInfo? = null,
        val customerAgency: CustomerAgency? = null,
        val name: String = "新飲水機",
        val address: String = "",
        val isPairSuccess: Boolean = false,
        val manualPairDeviceId: Int? = null,
        val brand: String? = null,
        val model: String? = null,
        val showConfirmTransferPermissionDialog: Boolean = false,
        val isRequestBluetoothPermission: Boolean = false,
        val isRequestEnableBluetooth: Boolean = false,
        val isRequestGPSPermission: Boolean = false,
        val isRequestEnableGPS: Boolean = false,
        val scanWiFiList: List<SimpleWiFiInfo>? = null,
        val selectedWifi: SimpleWiFiInfo? = null,
        val isBlePairFail: Boolean = false,
        val isPairWiFiFail: Boolean = false,
        val isActiveSuccess: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var pendingWork: (() -> Unit)? = null

    private var device: GatewayDevice? = null

    init {
        viewModelScope.launch {
            getUserInfoUseCase.invoke(Unit).first().data?.let { user ->
                _uiState.update { it.copy(userInfo = user) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        device?.disconnect()
        device = null
    }

    fun searchDevice(mac: String) {
        viewModelScope.launch {
            when (val result = searchDeviceByMACUseCase.invoke(mac)) {
                is Result.Success -> {
                    if (result.data.userId != null) {
                        pendingWork = {
                            _uiState.update {
                                it.copy(
                                    showConfirmTransferPermissionDialog = false,
                                    qrcodeSearchedDevice = result.data
                                )
                            }
                        }
                        _uiState.update { it.copy(showConfirmTransferPermissionDialog = true) }
                    } else {
                        _uiState.update { it.copy(qrcodeSearchedDevice = result.data) }
                    }
                }
                is Result.Error -> {
                    showToast("搜尋不到裝置")
                }
                Result.Loading -> {}
            }
        }
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!bleController.isPermissionAllow()) {
                _uiState.update { it.copy(isRequestBluetoothPermission = true) }
                pendingWork = { checkPermission() }
                return
            }
        }

        if (bleController.isNotEnable()) {
            _uiState.update {
                it.copy(
                    isRequestBluetoothPermission = false,
                    isRequestEnableBluetooth = true
                )
            }
            pendingWork = { checkPermission() }
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (!gpsController.isPermissionAllow()) {
                _uiState.update {
                    it.copy(
                        isRequestBluetoothPermission = false,
                        isRequestEnableBluetooth = false,
                        isRequestGPSPermission = true
                    )
                }
                pendingWork = { checkPermission() }
                return
            }

            if (!gpsController.isLocationEnable()) {
                _uiState.update {
                    it.copy(
                        isRequestBluetoothPermission = false,
                        isRequestEnableBluetooth = false,
                        isRequestGPSPermission = false,
                        isRequestEnableGPS = true
                    )
                }
                pendingWork = { checkPermission() }
                return
            }
        }


        _uiState.update {
            it.copy(
                isRequestBluetoothPermission = false,
                isRequestEnableBluetooth = false,
                isRequestGPSPermission = false,
                isRequestEnableGPS = false
            )
        }
        pendingWork = null
    }

    fun startPair() {
        viewModelScope.launch {
            val mac = _uiState.value.qrcodeSearchedDevice?.mac ?: return@launch
            _uiState.update { it.copy(isLoading = true, isBlePairFail = false) }
            try {
                this@EngineerPairViewModel.device = null
                this@EngineerPairViewModel.device = bleController.searchDevice(mac)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("搜尋不到裝置，請確認裝置在配對模式下")
                _uiState.update { it.copy(isLoading = false, isBlePairFail = true) }
                return@launch
            }

            val device = this@EngineerPairViewModel.device ?: return@launch

            showToast("連線裝置中...")
            val connectSuccess = device.connect()
            if (!connectSuccess) {
                showToast("連線失敗，請再試一次")
                _uiState.update { it.copy(isLoading = false, isBlePairFail = true) }
                return@launch
            }

            try {
                showToast("取得WiFi列表中")
                val wifiList = device.scanWiFi()
                _uiState.update { it.copy(isLoading = false, scanWiFiList = wifiList, selectedWifi = wifiList.first()) }
            } catch (e: Exception) {
                showToast("搜尋不到WiFi，請再試一次")
                device.disconnect()
                this@EngineerPairViewModel.device = null
                _uiState.update { it.copy(isLoading = false, isBlePairFail = true) }
            }
        }
    }

    fun setWiFi(ssid: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isPairWiFiFail = false) }
            val device = this@EngineerPairViewModel.device!!

            showToast("設定WiFi中")
            val pairSuccess = device.pairWiFi(ssid, password)
            if (!pairSuccess) {
                showToast("配對失敗")
                _uiState.update { it.copy(isLoading = false, isPairWiFiFail = true) }
                return@launch
            }

            delay(4000)

            val token = getUserTokenUseCase.invoke(Unit).data!!
            showToast("啟用中...")
            var activeSuccess = false
            var retryCount = 0

            while (!activeSuccess && retryCount < 6) {
                delay(5_000)

                activeSuccess = try {
                    withTimeout(20_000) { device.activeDevice(token) }
                } catch (e: TimeoutCancellationException) {
                    false
                }
                showToast("啟用中...")
                retryCount++
            }


            if (!activeSuccess) {
                showToast("啟用失敗，請再試一次")
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            showToast("啟用成功")

            device.disconnect()

            _uiState.update { it.copy(isLoading = false, isActiveSuccess = true) }
        }
    }

    fun finishDeviceInspection(pairDeviceInspection: PairDeviceInspection) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val pairDeviceId = _uiState.value.qrcodeSearchedDevice?.deviceId ?: _uiState.value.manualPairDeviceId!!
            val sn = _uiState.value.qrcodeSearchedDevice?.sn
            val mac = _uiState.value.qrcodeSearchedDevice?.mac
            val name = _uiState.value.name
            val customerId = _uiState.value.customerAgency?.id
            val customerAddress = _uiState.value.address

            val param = FinishEngineerPairUseCaseParam(
                pairDeviceId,
                sn,
                mac,
                name,
                pairDeviceInspection.toDeviceInspection(),
                customerId,
                customerAddress
            )
            when (finishEngineerPairUseCase.invoke(param)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isPairSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
                Result.Loading -> {}
            }
        }
    }

    fun addDeviceManually() {
        if(!isFillEnough()) return
        _uiState.update { it.copy(isLoading = true) }


        val param = with(uiState.value) {
            AddDeviceManualUseCaseParam(
                brand = brand!!,
                model = model!!,
                name = name,
                vendorId = userInfo?.vendorId,
                customerAddress = address,
                customerAgencyId = customerAgency?.id
            )
        }

        viewModelScope.launch {
            when (val result = addDeviceManuallyUseCase.invoke(param)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, manualPairDeviceId = result.data) }
                }
                is Result.Error -> {
                    showToast("新增失敗，請再試一次")
                    _uiState.update { it.copy(isLoading = false) }
                }
                Result.Loading -> {}
            }
        }
    }

    fun continueWork() {
        pendingWork?.invoke()
    }

    fun selectCustomer(customerAgency: CustomerAgency) {
        _uiState.update { it.copy(customerAgency = customerAgency) }
    }

    fun selectWifi(wifi: SimpleWiFiInfo) {
        _uiState.update { it.copy(selectedWifi = wifi) }
    }

    fun setDeviceName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun setAddress(fullAddress: String) {
        _uiState.update { it.copy(address = fullAddress) }
    }

    fun setBrand(name: String) {
        _uiState.update { it.copy(brand = name) }
    }

    fun setModel(name: String) {
        _uiState.update { it.copy(model = name) }
    }

    private fun isFillEnough(): Boolean {
        if (uiState.value.brand == null) {
            showToast("尚未填寫品牌")
            return false
        }

        if (uiState.value.model == null) {
            showToast("尚未填寫型號")
            return false
        }

        if (uiState.value.address.isEmpty()) {
            showToast("尚未填寫地址")
            return false
        }

        if (uiState.value.customerAgency == null) {
            showToast("尚未填寫機構")
            return false
        }

        return true
    }
}

fun PairDeviceInspection.toDeviceInspection(): DeviceInspection {
    return DeviceInspection(
        tds,
        psi,
        checkReliefValve,
        voltage,
        checkGround,
        drainStatus,
        installROFilter,
        checkROWork,
        changedFilter1,
        changedFilter2,
        changedFilter3,
        changedFilter4,
        changedFilter5,
        checkIntakeWaterWork,
        checkOutWaterWork,
        checkHeatWork,
        checkCoolWork,
        checkFunctionWork,
        checkPowerPlug,
        checkPowerSwitch,
        checkWaterSwitch,
        checkBasicFunction,
        checkBasicMaintain,
        checkConnectService,
        checkGuide,
        productImageUri,
        faucetImageUri,
        breakerImageUri,
        waterPressureGaugeImageUri
    )
}
