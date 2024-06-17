package com.clockworkorange.haohsing.ui.main.user.account

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.haohsing.BuildConfig
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : BaseViewModel() {

    data class UiState(
        val userName: String? = null,
        val userImage: String? = null,
        val version: String? = null
    )

    private val _uiState = MutableStateFlow(UiState(version = BuildConfig.VERSION_NAME))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserInfoUseCase.invoke(Unit).collect { result ->
                if (result is Result.Success) {
                    _uiState.update {
                        var name = result.data.name.ifEmpty { result.data.email }

                        if (result.data.role == UserInfo.Role.Engineer) {
                            name = "技術員 ${result.data.name}"
                        }

                        it.copy(
                            userName = name,
                            userImage = result.data.image
                        )
                    }
                }
            }
        }
    }

}