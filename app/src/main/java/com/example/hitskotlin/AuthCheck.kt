package com.example.hitskotlin

import com.example.hitskotlin.domain.Validation

object AuthCheck {
    fun canSubmitLogin(login: String, password: String): Boolean =
        Validation.login(login, password)

    fun isValidEmail(email: String): Boolean =
        Validation.email(email)

    fun canContinueRegistration(
        name: String,
        login: String,
        email: String,
        hasBirthDate: Boolean
    ): Boolean = Validation.registration(name, login, email, hasBirthDate)

    fun canRegister(password: String, repeatedPassword: String): Boolean =
        Validation.passwords(password, repeatedPassword)
}
