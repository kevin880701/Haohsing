package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class SimpleApiResult(
    @SerializedName("result")
    override val result: Int
):ApiResult

interface ApiResult{
    val result: Int
    fun isSuccess(): Boolean{
        return result == RESULT_SUCCESS
    }

    companion object{
        private const val RESULT_SUCCESS = 0
        private const val RESULT_FAIL = 1
        const val Login__AccountPasswordError = 10
    }
}

