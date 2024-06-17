package com.clockworkorange.domain.usecase.prefs

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PreferenceStorage
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class OnBoardingCompleteUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<Unit, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Unit): Boolean {
        return preferenceStorage.isOnBoardingCompleted()
    }

}