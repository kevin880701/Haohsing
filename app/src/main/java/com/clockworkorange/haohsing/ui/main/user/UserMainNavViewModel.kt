package com.clockworkorange.haohsing.ui.main.user

import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserMainNavViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val tab: UserMainNavFragment.Tab = UserMainNavFragment.Tab.Home
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

        }

    }

    fun selectTab(tab: UserMainNavFragment.Tab) {
        _uiState.update { it.copy(tab = tab) }
    }


}