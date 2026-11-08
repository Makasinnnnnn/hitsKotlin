package com.example.hitskotlin.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hitskotlin.data.api.ApiException
import com.example.hitskotlin.data.repository.*
import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.ui.mapper.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val authRepository: AuthRepository
) : ScreenViewModel() {
    var profile by mutableStateOf<ProfileUi?>(null)
        private set

    init { load() }
    fun load() = request { profile = repository.get().toUi() }
    fun save(value: ProfileUi) = request { profile = repository.save(value.toData()).toUi() }
    fun logout(onComplete: () -> Unit) = request {
        try { authRepository.logout() } finally { onComplete() }
    }
}
