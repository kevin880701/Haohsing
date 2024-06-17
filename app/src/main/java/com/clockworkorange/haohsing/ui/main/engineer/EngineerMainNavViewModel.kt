package com.clockworkorange.haohsing.ui.main.engineer

import com.clockworkorange.haohsing.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EngineerMainNavViewModel @Inject constructor(

) : BaseViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val tab: EngineerMainNavFragment.Tab = EngineerMainNavFragment.Tab.Home
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    fun selectTab(tab: EngineerMainNavFragment.Tab) {
        _uiState.update { it.copy(tab = tab) }
    }

}