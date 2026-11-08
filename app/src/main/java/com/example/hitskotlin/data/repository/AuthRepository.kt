package com.example.hitskotlin.data.repository

import com.example.hitskotlin.data.api.*
import com.example.hitskotlin.data.api.model.*
import com.example.hitskotlin.data.mapper.*
import com.example.hitskotlin.data.local.MovieCache
import com.example.hitskotlin.data.session.SessionStore
import com.example.hitskotlin.data.model.*
import com.example.hitskotlin.domain.Validation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class AuthRepository(private val api: AuthApi, private val session: SessionStore, private val accountApi: AccountApi) {
    suspend fun login(username: String, password: String) {
        require(Validation.login(username, password)) { "Введите логин и пароль" }
        val response = apiCall { api.login(LoginRequest(username.trim(), password)) }
        saveToken(response)
    }

    suspend fun register(form: Registration) {
        require(Validation.registration(form.name, form.userName, form.email, form.birthDate.isNotBlank()) && form.password.isNotBlank()) {
            "Проверьте данные регистрации"
        }
        val response = apiCall { api.register(form.toRequest()) }
        saveToken(response)
    }

    private suspend fun saveToken(response: TokenResponse) {
        val token = response.token?.takeIf { it.isNotBlank() }
            ?: throw ApiException(500, "Сервер не вернул токен авторизации")
        currentCoroutineContext().ensureActive()
        session.save(token)
    }

    suspend fun logout() {
        try { apiCall { accountApi.logout() } }
        finally { session.clear() }
    }
}
