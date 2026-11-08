package com.example.hitskotlin.ui.model

data class RegistrationForm(
    val userName: String,
    val name: String,
    val password: String,
    val email: String,
    val birthDate: String,
    val gender: Int
)
