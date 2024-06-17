package com.clockworkorange.haohsing.ui.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockworkorange.domain.data
import com.clockworkorange.domain.entity.UserInfo
import com.clockworkorange.domain.usecase.login.CheckLoginStatusUseCase
import com.clockworkorange.domain.usecase.prefs.OnBoardingCompleteUseCase
import com.clockworkorange.domain.usecase.user.GetUserInfoUseCase
import com.clockworkorange.haohsing.utils.tryOffer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    onboardingCompletedUseCase: OnBoardingCompleteUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _navAction = Channel<LaunchNavAction>(capacity = Channel.CONFLATED)
    val navAction = _navAction.receiveAsFlow()

    init {
        viewModelScope.launch {
            val isOnBoardComplete = onboardingCompletedUseCase.invoke(Unit)
            if (isOnBoardComplete.data == false) {
                _navAction.tryOffer(LaunchNavAction.NavToOnboarding)
                return@launch
            }

            val loginStatus = checkLoginStatusUseCase.invoke(Unit)
            if (loginStatus.data == false) {
                _navAction.tryOffer(LaunchNavAction.NavToLogin)
                return@launch
            }

            val userInfo = getUserInfoUseCase.invoke(Unit).first()
            if (userInfo.data?.role == UserInfo.Role.Engineer) {
                _navAction.tryOffer(LaunchNavAction.NavToEngineerMain)
                return@launch
            } else {
                _navAction.tryOffer(LaunchNavAction.NavToUserMain)
                return@launch
            }
        }

    }

}

sealed class LaunchNavAction {
    object NavToOnboarding : LaunchNavAction()
    object NavToLogin : LaunchNavAction()
    object NavToUserMain : LaunchNavAction()
    object NavToEngineerMain : LaunchNavAction()
}
