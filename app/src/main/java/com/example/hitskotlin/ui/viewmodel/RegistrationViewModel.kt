package com.example.hitskotlin.ui.viewmodel

import com.example.hitskotlin.data.repository.AuthRepository
import com.example.hitskotlin.ui.model.RegistrationForm
import com.example.hitskotlin.ui.mapper.toData

class RegistrationViewModel(private val repository: AuthRepository) : AuthViewModel() {
    fun register(form: RegistrationForm) = submit { repository.register(form.toData()) }
}
