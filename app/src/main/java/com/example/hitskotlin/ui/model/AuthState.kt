package com.example.hitskotlin.ui.model

data class AuthState(val loading: Boolean = false, val success: Boolean = false, val error: String? = null)
