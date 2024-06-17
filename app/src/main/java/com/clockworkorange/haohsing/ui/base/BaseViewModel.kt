package com.clockworkorange.haohsing.ui.base

import androidx.lifecycle.ViewModel
import com.clockworkorange.haohsing.utils.tryOffer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

open class BaseViewModel : ViewModel() {

    private val _toastChannel = Channel<String>(capacity = Channel.CONFLATED)
    val toastFlow = _toastChannel.receiveAsFlow()

    fun showToast(msg: String) {
        _toastChannel.tryOffer(msg)
    }

}

sealed class NavAction
object NavUp : NavAction()