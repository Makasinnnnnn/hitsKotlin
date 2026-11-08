package com.example.hitskotlin.ui.viewmodel

import com.example.hitskotlin.data.repository.AuthRepository

class LoginViewModel(private val repository: AuthRepository) : AuthViewModel() {
    fun login(username: String, password: String) = submit(loginRequest = true) {
        repository.login(username, password)
    }
}
