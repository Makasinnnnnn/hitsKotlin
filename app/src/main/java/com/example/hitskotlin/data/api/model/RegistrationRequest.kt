package com.example.hitskotlin.data.api.model

data class RegistrationRequest(
    val userName: String,
    val name: String,
    val password: String,
    val email: String,
    val birthDate: String,
    val gender: Int
)
