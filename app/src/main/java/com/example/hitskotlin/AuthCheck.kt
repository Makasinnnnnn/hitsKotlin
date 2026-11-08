package com.example.hitskotlin

<<<<<<< HEAD
import android.util.Patterns

object AuthCheck {
    fun canSubmitLogin(login: String, password: String): Boolean =
        login.isNotBlank() && password.isNotBlank()

    fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
=======
import com.example.hitskotlin.domain.Validation

object AuthCheck {
    fun canSubmitLogin(login: String, password: String): Boolean =
        Validation.login(login, password)

    fun isValidEmail(email: String): Boolean =
        Validation.email(email)
>>>>>>> f28fb515fda31e350200026991c8dac75d392080

    fun canContinueRegistration(
        name: String,
        login: String,
        email: String,
        hasBirthDate: Boolean
<<<<<<< HEAD
    ): Boolean = name.isNotBlank() && login.isNotBlank() && isValidEmail(email) && hasBirthDate

    fun canRegister(password: String, repeatedPassword: String): Boolean =
        password.isNotBlank() && password == repeatedPassword
=======
    ): Boolean = Validation.registration(name, login, email, hasBirthDate)

    fun canRegister(password: String, repeatedPassword: String): Boolean =
        Validation.passwords(password, repeatedPassword)
>>>>>>> f28fb515fda31e350200026991c8dac75d392080
}
