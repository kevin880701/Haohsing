package com.clockworkorange.domain.data.remote

import com.clockworkorange.domain.data.remote.model.ErrorResponse

interface GlobalErrorHandler {

    fun onNetworkUnavailable()

    fun onLoginSessionExpired()

    fun onErrorMessage(msg: ErrorResponse)
}