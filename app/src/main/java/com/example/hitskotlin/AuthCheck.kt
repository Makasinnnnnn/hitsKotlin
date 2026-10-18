package com.example.hitskotlin

import android.util.Patterns

object AuthCheck {
    fun canSubmitLogin(login: String, password: String): Boolean =
        login.isNotBlank() && password.isNotBlank()

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun canContinueRegistration(
        name: String,
        login: String,
        email: String,
        hasBirthDate: Boolean
    ): Boolean = name.isNotBlank() && login.isNotBlank() && isValidEmail(email) && hasBirthDate

    fun canRegister(password: String, repeatedPassword: String): Boolean =
        password.isNotBlank() && password == repeatedPassword
}
