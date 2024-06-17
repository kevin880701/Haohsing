package com.clockworkorange.haohsing.ui.main.user.account

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.usecase.login.PasswordFormatInValidException
import com.clockworkorange.domain.usecase.user.*
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateUserImageUseCase: UpdateUserImageUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val updateUserPhoneUseCase: UpdateUserPhoneUseCase,
    private val updateUserCityRegionUseCase: UpdateUserCityRegionUseCase,
    private val updateUserPasswordUseCase: UpdateUserPasswordUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel() {

    data class UiState(
        val name: String = "",
        val image: String = "",
        val mail: String = "",
        val phone: String = "",
        val region: String = "",
        val isEngineer: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserInfoUseCase.invoke(Unit).collect { result ->
                if (result is Result.Success) {
                    val userInfo = result.data
                    _uiState.update {
                        it.copy(
                            name = userInfo.name.ifEmpty { "尚未設定名稱" },
                            image = userInfo.image,
                            mail = userInfo.email,
                            phone = userInfo.phone.ifEmpty { "尚未設定電話" },
                            region = "${userInfo.city}${userInfo.region}".ifEmpty { "尚未設定地區" },
                            isEngineer = userInfo.role == UserInfo.Role.Engineer
                        )
                    }
                }
            }
        }
    }

    fun updateUserPhone(phone: String) {
        if (phone.isEmpty()) {
            showToast("電話不能為空")
            return
        }

        viewModelScope.launch {
            when (val result = updateUserPhoneUseCase.invoke(phone)) {
                is Result.Success -> showToast("更新成功")
                is Result.Error -> showToast("更新失敗:${result.exception.message}")
            }
        }

    }

    fun updateUserName(name: String) {
        if (name.isEmpty()) {
            showToast("名稱不能為空")
            return
        }

        viewModelScope.launch {
            when (val result = updateUserNameUseCase.invoke(name)) {
                is Result.Success -> showToast("更新成功")
                is Result.Error -> showToast("更新失敗:${result.exception.message}")
            }
        }
    }

    fun updateUserCityRegion(city: String, region: String) {
        viewModelScope.launch {
            when (val result =
                updateUserCityRegionUseCase.invoke(UpdateUserCityRegionParam(city, region))) {
                is Result.Success -> showToast("更新成功")
                is Result.Error -> showToast("更新失敗:${result.exception.message}")
            }
        }
    }

    private val _navToLogin = Channel<Unit>(Channel.CONFLATED)
    val navFlow = _navToLogin.receiveAsFlow()

    fun logout(fcmToken: String) {
        viewModelScope.launch {
            logoutUseCase.invoke(fcmToken)
            _navToLogin.send(Unit)
        }
    }

    fun updateUserImage(imageFile: File) {
        viewModelScope.launch {
            when (val result = updateUserImageUseCase.invoke(imageFile)) {
                is Result.Success -> showToast("更新成功")
                is Result.Error -> showToast("更新失敗:${result.exception.message}")
            }
        }
    }

    fun updateUserPassword(oldPwd: String, newPwd: String) {
        viewModelScope.launch {
            when (val result =
                updateUserPasswordUseCase.invoke(UpdateUserPasswordParam(oldPwd, newPwd))) {
                is Result.Success -> showToast("更新成功")
                is Result.Error -> {
                    when (result.exception) {
                        is PasswordFormatInValidException -> {
                            showToast("密碼格式錯誤，需介於6-18碼")
                        }
                        is AccountPasswordErrorException -> {
                            showToast("密碼不正確")
                        }
                        else -> {
                            showToast("更新失敗:${result.exception.message}")
                        }
                    }
                }
            }
        }
    }
}
