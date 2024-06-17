package com.clockworkorange.haohsing.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingCompleteActionUseCase: com.clockworkorange.domain.usecase.prefs.OnboardingCompleteActionUseCase
) : ViewModel() {

    private val _navToLogin = Channel<Unit>(Channel.CONFLATED)
    val navToLogin = _navToLogin.receiveAsFlow()

    fun registerLoginButtonClick() {
        viewModelScope.launch {
            onboardingCompleteActionUseCase(true)
            _navToLogin.send(Unit)
        }
    }

}