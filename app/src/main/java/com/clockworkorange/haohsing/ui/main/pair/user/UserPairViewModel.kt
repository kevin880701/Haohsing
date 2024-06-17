package com.clockworkorange.haohsing.ui.main.pair.user

import android.os.Build
import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.AreaInfo
import com.clockworkorange.domain.entity.PlaceInfo
import com.clockworkorange.domain.usecase.device.FinishUserPairParam
import com.clockworkorange.domain.usecase.device.FinishUserPairUseCase
import com.clockworkorange.domain.usecase.device.SearchDeviceByMACUseCase
import com.clockworkorange.domain.usecase.device.SearchedDevice
import com.clockworkorange.domain.usecase.palcearea.*
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.domain.usecase.user.GetUserTokenUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import com.clockworkorange.haohsing.ui.dialog.AreaDialogType
import com.clockworkorange.haohsing.ui.dialog.PlaceDialogType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UserPairViewModel @Inject constructor(
    private val gpsController: GPSController,
    private val bleController: BluetoothController,
    private val searchDeviceByMACUseCase: SearchDeviceByMACUseCase,
    private val getUserTokenUseCase: GetUserTokenUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPlaceListUseCase: GetPlaceListUseCase,
    private val getAreaListUseCase: GetAreaListUseCase,
    private val addPlaceUseCase: AddPlaceUseCase,
    private val addAreaUseCase: AddAreaUseCase,
    private val finishUserPairUseCase: FinishUserPairUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val isRequestBluetoothPermission: Boolean = false,
        val isRequestEnableBluetooth: Boolean = false,
        val isRequestGPSPermission: Boolean = false,
        val isRequestEnableGPS: Boolean = false,
        val searchedDevice: SearchedDevice? = null,
        val showConfirmTransferPermissionDialog: Boolean = false,
        val isBlePairFail: Boolean = false,
        val scanWiFiList: List<SimpleWiFiInfo>? = null,
        val selectedWifi: SimpleWiFiInfo? = null,
        val isPairWiFiFail: Boolean = false,
        val isActiveSuccess: Boolean = false,
        val deviceName: String? = null,
        val place: PlaceInfo? = null,
        val area: AreaInfo? = null,
        val ownerName: String? = null,
        val ownerEmail: String? = null,
        val ownerPhone: String? = null,
        val buyDate: LocalDate? = null,
        val placeDialogType: PlaceDialogType? = null,
        val areaDialogType: AreaDialogType? = null,
        val isPairSuccess: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var pendingWork: (() -> Unit)? = null

    private var device: GatewayDevice? = null

    fun searchDevice(mac: String) {
        viewModelScope.launch {
            when (val result = searchDeviceByMACUseCase.invoke(mac)) {
                is Result.Success -> {
                    if (result.data.userId != null) {
                        pendingWork = {
                            _uiState.update {
                                it.copy(
                                    showConfirmTransferPermissionDialog = false,
                                    searchedDevice = result.data
                                )
                            }
                        }
                        _uiState.update { it.copy(showConfirmTransferPermissionDialog = true) }
                    } else {
                        _uiState.update { it.copy(searchedDevice = result.data) }
                    }
                }
                is Result.Error -> {
                    showToast("搜尋不到裝置")
                }
                Result.Loading -> {}
            }
        }
    }


    fun continueWork() {
        pendingWork?.invoke()
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

    fun startPair(mac: String? = _uiState.value.searchedDevice?.mac) {
        viewModelScope.launch {
            mac ?: return@launch
            _uiState.update { it.copy(isLoading = true, isBlePairFail = false) }
            try {
                this@UserPairViewModel.device = null
                this@UserPairViewModel.device = bleController.searchDevice(mac)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("搜尋不到裝置，請確認裝置在配對模式下")
                _uiState.update { it.copy(isLoading = false, isBlePairFail = true) }
                return@launch
            }

            val device = this@UserPairViewModel.device ?: return@launch

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
                this@UserPairViewModel.device = null
                _uiState.update { it.copy(isLoading = false, isBlePairFail = true) }
            }
        }
    }

    fun setWiFi(ssid: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isPairWiFiFail = false) }
            val device = this@UserPairViewModel.device!!

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

    override fun onCleared() {
        super.onCleared()
        device?.disconnect()
        device = null
    }

    fun setDeviceName(name: String) {
        _uiState.update { it.copy(deviceName = name) }
    }

    fun fillWarranty() {
        viewModelScope.launch {
            getUserInfoUseCase.invoke(Unit).first().data?.let { userInfo ->
                _uiState.update {
                    it.copy(
                        ownerName = userInfo.name,
                        ownerEmail = userInfo.email,
                        ownerPhone = userInfo.phone,
                        buyDate = LocalDate.now()
                    )
                }
            }
        }
    }

    fun clickEditPlace() {
        viewModelScope.launch {
            val placeList = getPlaceListUseCase.invoke(Unit).first().data
            if (placeList?.isEmpty() == true) {
                _uiState.update { it.copy(placeDialogType = PlaceDialogType.Add) }
            } else {
                _uiState.update { it.copy(placeDialogType = PlaceDialogType.Select(placeList!!)) }
            }
        }
    }

    fun clickEditArea() {
        viewModelScope.launch {
            val selectedPlace = _uiState.value.place
            if (selectedPlace == null) {
                showToast("尚未選擇單位")
                return@launch
            }
            val placeId = selectedPlace.id
            val areaList = getAreaListUseCase.invoke(placeId).data
            if (areaList?.isEmpty() == true) {
                _uiState.update { it.copy(areaDialogType = AreaDialogType.Add) }
            } else {
                _uiState.update { it.copy(areaDialogType = AreaDialogType.Select(areaList!!)) }
            }
        }
    }

    fun selectWifi(wifi: SimpleWiFiInfo) {
        _uiState.update { it.copy(selectedWifi = wifi) }
    }

    fun selectPlace(selectedPlace: PlaceInfo) {
        _uiState.update { it.copy(placeDialogType = null, place = selectedPlace) }
    }

    fun addPlace(name: String) {
        viewModelScope.launch {
            when (val result = addPlaceUseCase.invoke(name)) {
                is Result.Success -> {
                    val id = result.data
                    val newPlaceInfo =
                        getPlaceListUseCase.invoke(Unit).first().data?.firstOrNull { it.id == id }
                    _uiState.update { it.copy(placeDialogType = null, place = newPlaceInfo) }
                }
                is Result.Error -> {
                    showToast("新增單位失敗")
                    _uiState.update { it.copy(placeDialogType = null) }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun selectArea(selectedArea: AreaInfo) {
        _uiState.update { it.copy(areaDialogType = null, area = selectedArea) }
    }

    fun addArea(name: String) {
        viewModelScope.launch {
            val selectedPlace = _uiState.value.place
            if (selectedPlace == null) {
                showToast("尚未選擇單位")
                return@launch
            }
            val placeId = selectedPlace.id

            when (val result = addAreaUseCase.invoke(AddAreaParam(placeId, name))) {
                is Result.Success -> {
                    val id = result.data
                    val newAreaInfo =
                        getAreaListUseCase.invoke(placeId).data?.firstOrNull { it.id == id }
                    _uiState.update { it.copy(areaDialogType = null, area = newAreaInfo) }
                }
                is Result.Error -> {
                    showToast("新增區域失敗")
                    _uiState.update { it.copy(areaDialogType = null) }
                }
                is Result.Loading -> {}
            }

        }
    }

    fun editOwnerName(name: String) {
        _uiState.update { it.copy(ownerName = name) }
    }

    fun editOwnerEmail(email: String) {
        _uiState.update { it.copy(ownerEmail = email) }
    }

    fun editOwnerPhone(phone: String) {
        _uiState.update { it.copy(ownerPhone = phone) }
    }

    fun setBuyDate(date: LocalDate) {
        _uiState.update { it.copy(buyDate = date) }
    }

    fun addDevice() {
        viewModelScope.launch {
            if (!isFillEnough()) return@launch
            _uiState.update { it.copy(isLoading = true) }

            val deviceId = uiState.value.searchedDevice!!.deviceId
            val sn = uiState.value.searchedDevice!!.sn
            val mac = uiState.value.searchedDevice!!.mac
            val deviceName = uiState.value.deviceName!!
            val place = uiState.value.place!!
            val area = uiState.value.area!!
            val ownerName = uiState.value.ownerName
            val ownerEmail = uiState.value.ownerEmail
            val ownerPhone = uiState.value.ownerPhone
            val buyDate = uiState.value.buyDate

            val param = FinishUserPairParam(
                deviceId,
                sn,
                mac,
                deviceName,
                place.id,
                area.id,
                ownerName,
                ownerEmail,
                ownerPhone,
                buyDate
            )
            when (val result = finishUserPairUseCase.invoke(param)) {
                is Result.Success -> {
                    if (!result.data) {
                        showToast("配對失敗，請再試一次")
                        _uiState.update { it.copy(isLoading = false) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, isPairSuccess = result.data) }
                    }
                }
                is Result.Error -> {
                    showToast("配對失敗，請再試一次")
                    _uiState.update { it.copy(isLoading = false) }
                }
                Result.Loading -> {}
            }

        }
    }

    private fun isFillEnough(): Boolean {
        val deviceName = uiState.value.deviceName
        if (deviceName == null) {
            showToast("尚未填寫名稱")
            return false
        }
        val place = uiState.value.place
        if (place == null) {
            showToast("尚未選擇單位")
            return false
        }
        val area = uiState.value.area
        if (area == null) {
            showToast("尚未選擇區域")
            return false
        }
        if (uiState.value.searchedDevice?.isRegisterWarranty == false) {
            val ownerName = uiState.value.ownerName
            if (ownerName == null) {
                showToast("尚未填寫所屬人")
                return false
            }
            val ownerEmail = uiState.value.ownerEmail
            if (ownerEmail == null) {
                showToast("尚未填寫Email")
                return false
            }
            val ownerPhone = uiState.value.ownerPhone
            if (ownerPhone == null) {
                showToast("尚未填寫聯絡電話")
                return false
            }
            val buyDate = uiState.value.buyDate
            if (buyDate == null) {
                showToast("尚未填寫購買日期")
                return false
            }
        }

        return true
    }

}