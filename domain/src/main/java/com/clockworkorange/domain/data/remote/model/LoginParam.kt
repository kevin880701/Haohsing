package com.clockworkorange.domain.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginParam(
    @SerializedName("account")
    val account: String,
    @SerializedName("psw")
    val password: String
)

data class LoginResponse(
    @SerializedName("result")
    override val result: Int,
    @SerializedName("token")
    val token: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("info")
    val info: Info?
):ApiResult{

    data class Info(
        @SerializedName("name")
        val name: String?,
        @SerializedName("account")
        val account: String
    )

    fun isErrorWrongAccountPassword(): Boolean{
        return result == RESULT_WRONG_ACCOUNT_PASSWORD
    }

    fun isErrorAccountNotActive(): Boolean{
        return result == RESULT_ACCOUNT_NOT_ACTIVE
    }

    companion object{
        private const val RESULT_WRONG_ACCOUNT_PASSWORD = 10
        private const val RESULT_ACCOUNT_NOT_ACTIVE = 15
    }
}