package com.example.hitskotlin.data.api.model

data class ProfileRequest(
    val id: String,
    val nickName: String,
    val email: String,
    val avatarLink: String,
    val name: String,
    val birthDate: String,
    val gender: Int
)
