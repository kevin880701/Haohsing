package com.clockworkorange.haohsing.ui.login

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.data.AccountNotActiveException
import com.clockworkorange.domain.data.NoNetworkException
import com.clockworkorange.domain.data.SystemMaintainException
import com.clockworkorange.domain.data.WrongAccountPasswordException
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.usecase.login.*
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import com.clockworkorange.haohsing.utils.tryOffer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val resendActiveEmailUseCase: ResendActiveEmailUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val forgetPasswordUseCase: ForgetPasswordUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val appleLoginUseCase: AppleLoginUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val accountFieldError: String? = null,
        val passwordFieldError: String? = null,
        val loginErrorDialog: LoginErrorDialog? = null
    ) {
        enum class LoginErrorDialog {
            AccountNotActive, SystemMaintain, WrongAccountPassword, NoNetwork
        }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _navAction = Channel<LoginNav>(capacity = Channel.CONFLATED)
    val navAction = _navAction.receiveAsFlow()

    fun login(mail: String, password: String, keepLoginStatus: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = loginUseCase(LoginUserCaseParams(mail, password, keepLoginStatus))
            _uiState.update { it.copy(isLoading = false) }
            when (result) {
                is Result.Success -> {
                    getUserInfoUseCase.invoke(Unit).collect {
                        if (it.data?.role == UserInfo.Role.Engineer) {
                            _navAction.tryOffer(LoginNav.NavToEngineerMain)
                        } else {
                            _navAction.tryOffer(LoginNav.NavToMain)
                        }
                    }
                }
                is Result.Error -> handleError(result.exception)
                Result.Loading -> {}
            }
        }
    }

    fun resendActiveEmail(mail: String) {
        viewModelScope.launch {
            when (val result = resendActiveEmailUseCase.invoke(mail)) {
                is Result.Success -> showToast("已發送")
                is Result.Error -> handleError(result.exception)
                Result.Loading -> {}
            }
        }
    }

    fun forgetPassword(mail: String) {
        viewModelScope.launch {
            when (val result = forgetPasswordUseCase.invoke(mail)) {
                is Result.Success -> _navAction.tryOffer(LoginNav.NavToForgetPassword(mail))
                is Result.Error -> handleError(result.exception)
                Result.Loading -> {}
            }
        }
    }

    private fun handleError(exception: Exception) {
        when (exception) {
            is WrongAccountPasswordException -> {
                _uiState.update { it.copy(loginErrorDialog = UiState.LoginErrorDialog.WrongAccountPassword) }
            }
            is AccountNotActiveException -> {
                _uiState.update { it.copy(loginErrorDialog = UiState.LoginErrorDialog.AccountNotActive) }
            }
            is SystemMaintainException -> {
                _uiState.update { it.copy(loginErrorDialog = UiState.LoginErrorDialog.SystemMaintain) }
            }
            is EmailFormatInValidException -> {
                _uiState.update { it.copy(accountFieldError = "帳號格式錯誤") }
            }
            is PasswordFormatInValidException -> {
                _uiState.update { it.copy(passwordFieldError = "密碼需要6-18位數密碼") }
            }
            is NoNetworkException -> {
                _uiState.update { it.copy(loginErrorDialog = UiState.LoginErrorDialog.NoNetwork) }
            }
        }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(loginErrorDialog = null) }
    }

    fun onRegisterClick() {
        _navAction.tryOffer(LoginNav.NavToRegister)
    }

    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = googleLoginUseCase(idToken)
            _uiState.update { it.copy(isLoading = false) }
            when (result) {
                is Result.Success -> {
                    getUserInfoUseCase.invoke(Unit).collect {
                        if (it.data?.role == UserInfo.Role.Engineer) {
                            _navAction.tryOffer(LoginNav.NavToEngineerMain)
                        } else {
                            _navAction.tryOffer(LoginNav.NavToMain)
                        }
                    }
                }
                is Result.Error -> handleError(result.exception)
                Result.Loading -> {}
            }
        }
    }

    fun appleLogin(authCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = appleLoginUseCase(authCode)
            _uiState.update { it.copy(isLoading = false) }
            when (result) {
                is Result.Success -> {
                    getUserInfoUseCase.invoke(Unit).collect {
                        if (it.data?.role == UserInfo.Role.Engineer) {
                            _navAction.tryOffer(LoginNav.NavToEngineerMain)
                        } else {
                            _navAction.tryOffer(LoginNav.NavToMain)
                        }
                    }
                }
                is Result.Error -> handleError(result.exception)
                Result.Loading -> {}
            }
        }
    }
}

sealed class LoginNav {
    object NavToMain : LoginNav()
    object NavToEngineerMain : LoginNav()
    data class NavToForgetPassword(val mail: String) : LoginNav()
    object NavToRegister : LoginNav()
}