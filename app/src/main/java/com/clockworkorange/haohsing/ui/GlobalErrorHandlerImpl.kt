package com.clockworkorange.haohsing.ui

import com.clockworkorange.domain.data.remote.GlobalErrorHandler
import com.clockworkorange.domain.data.remote.model.ErrorResponse
import timber.log.Timber

object GlobalErrorHandlerImpl : GlobalErrorHandler {

    private var handler: GlobalErrorHandler? = null

    override fun onNetworkUnavailable() {
        Timber.e("onNetworkUnavailable $handler")
        handler?.onNetworkUnavailable()
    }

    override fun onLoginSessionExpired() {
        Timber.e("onLoginSessionExpired $handler")
        handler?.onLoginSessionExpired()
    }

    override fun onErrorMessage(msg: ErrorResponse) {
        Timber.e("onErrorMessage $handler")
        Timber.e("error:${msg.errorMsg}")
        handler?.onErrorMessage(msg)
    }

    fun registerErrorHandler(handler: GlobalErrorHandler?) {
        this.handler = handler
    }

}