package com.clockworkorange.haohsing.ui.main.user.devicedetail.warranty

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.Warranty
import com.clockworkorange.domain.usecase.device.GetWarrantyInfoUseCase
import com.clockworkorange.domain.usecase.device.UpdateWarrantyParam
import com.clockworkorange.domain.usecase.device.UpdateWarrantyUseCase
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WarrantyInfoViewModel @Inject constructor(
    private val getWarrantyInfoUseCase: GetWarrantyInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateWarrantyUseCase: UpdateWarrantyUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val ownerName: String? = null,
        val ownerEmail: String? = null,
        val ownerPhone: String? = null,
        val date: LocalDate? = null,
        val shouldShowEditButton: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var deviceId: Int? = null

    fun setDeviceId(deviceId: Int) {
        this.deviceId = deviceId
        viewModelScope.launch {
            when (val result = getWarrantyInfoUseCase.invoke(deviceId)) {
                is Result.Success -> {
                    _uiState.update {
                        val warranty = result.data
                        it.copy(
                            ownerName = warranty?.name,
                            ownerEmail = warranty?.email,
                            ownerPhone = warranty?.phone,
                            date = warranty?.startDate,
                            shouldShowEditButton = result.data == null
                        )
                    }
                }
                is Result.Error -> {
                    showToast("無法取得保固資訊")
                }
            }
        }
    }

    fun updateWarranty() {
        viewModelScope.launch {
            val result = updateWarrantyUseCase(
                UpdateWarrantyParam(
                    deviceId!!,
                    uiState.value.ownerName!!,
                    uiState.value.ownerEmail!!,
                    uiState.value.ownerPhone!!,
                    uiState.value.date!!
                )
            )

            if (result.data == true) {
                _uiState.update { it.copy(shouldShowEditButton = false) }
            }
        }
    }

    fun isDataFilled(): Boolean {
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
        val buyDate = uiState.value.date
        if (buyDate == null) {
            showToast("尚未填寫購買日期")
            return false
        }
        return true
    }

    fun fillWarranty() {
        viewModelScope.launch {
            getUserInfoUseCase.invoke(Unit).first().data?.let { userInfo ->
                _uiState.update {
                    it.copy(
                        ownerName = userInfo.name,
                        ownerEmail = userInfo.email,
                        ownerPhone = userInfo.phone,
                        date = LocalDate.now()
                    )
                }
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
        _uiState.update { it.copy(date = date) }
    }

}