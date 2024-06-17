package com.clockworkorange.domain.usecase.login

object FormatChecker {

    fun isEmailValid(mail: String): Boolean{
        return mail.isNotEmpty() && mail.contains("@")
    }

    //密碼需要6-18碼
    fun isPasswordValid(password: String): Boolean{
        return password.length in 6..18
    }

    fun isPhoneNumberValid(phone: String): Boolean{
        return phone.startsWith("09") && phone.length == 10
    }
}