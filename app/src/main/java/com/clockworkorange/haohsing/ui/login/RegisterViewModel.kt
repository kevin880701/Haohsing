package com.clockworkorange.haohsing.ui.login

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.data
import com.clockworkorange.domain.usecase.login.EmailFormatInValidException
import com.clockworkorange.domain.usecase.register.*
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import com.clockworkorange.haohsing.utils.tryOffer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val checkAccountExistUseCase: CheckAccountExistUseCase,
    private val sendVerifyCodeUseCase: SendVerifyCodeUseCase,
    private val verifyCodeCorrectUseCase: VerifyCodeCorrectUseCase,
    private val registerAccountUseCase: RegisterAccountUseCase
) : BaseViewModel() {

    data class UiState(
        val toCreateAccount: String? = null,
        val isShowAccountExistDialog: Boolean = false,
        val accountFieldError: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _navAction = Channel<RegisterNav>(capacity = Channel.CONFLATED)
    val navAction = _navAction.receiveAsFlow()

    fun createAccount(mail: String) {
        viewModelScope.launch {
            val result = checkAccountExistUseCase.invoke(mail)
            when (result) {
                is Result.Success -> {
                    if (result.data) {
                        //account exist
                        _uiState.update { it.copy(isShowAccountExistDialog = true) }
                    } else {
                        _uiState.update {
                            it.copy(
                                toCreateAccount = mail,
                                accountFieldError = null
                            )
                        }
                        _navAction.tryOffer(RegisterNav.NavToSetupPassword)
                    }
                }
                is Result.Error -> handleError(result.exception)
                Result.Loading -> {}
            }
        }
    }

    private fun handleError(exception: Exception) {
        when (exception) {
            is EmailFormatInValidException -> {
                _uiState.update { it.copy(accountFieldError = "帳號格式不正確") }
            }
        }
    }

    fun onAccountExistDialogShow() {
        _uiState.update { it.copy(isShowAccountExistDialog = false) }
    }

    private suspend fun sendVerifyCode(mail: String) {
        sendVerifyCodeUseCase.invoke(mail)
    }

    fun resentVerifyCode() {
        viewModelScope.launch {
            val mail = uiState.value.toCreateAccount ?: return@launch
            sendVerifyCode(mail)
        }
    }

    fun verifyCode(code: String) {
        viewModelScope.launch {
            val mail = uiState.value.toCreateAccount ?: return@launch
            val param = VerifyCodeCorrectParam(mail, code)
            val result = verifyCodeCorrectUseCase.invoke(param)
            if (result.data == true) {
                _navAction.tryOffer(RegisterNav.NavToSetupPassword)
            } else {
                showToast("驗證碼不正確")
            }
        }
    }

    fun registerWithPassword(password: String) {
        viewModelScope.launch {
            val account = uiState.value.toCreateAccount ?: return@launch
            val param = RegisterAccountParam(account, password)
            val result = registerAccountUseCase.invoke(param)
            if (result.data == true) {
                _navAction.tryOffer(RegisterNav.NavToRegisterSuccess)
            } else {
                showToast("註冊失敗")
            }
        }
    }

}

sealed class RegisterNav {
    object NavToVerifyCode : RegisterNav()
    object NavToSetupPassword : RegisterNav()
    object NavToRegisterSuccess : RegisterNav()
}