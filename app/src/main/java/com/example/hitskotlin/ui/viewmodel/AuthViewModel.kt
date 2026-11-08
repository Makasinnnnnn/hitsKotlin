package com.example.hitskotlin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hitskotlin.data.api.ApiException
import com.example.hitskotlin.ui.model.AuthState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class AuthViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(AuthState())
    val state = mutableState.asStateFlow()


    fun dismissError() { mutableState.value = mutableState.value.copy(error = null) }

    protected fun submit(loginRequest: Boolean = false, action: suspend () -> Unit) {
        if (mutableState.value.loading || mutableState.value.success) return
        viewModelScope.launch {
            mutableState.value = AuthState(loading = true)
            try {
                action()
                mutableState.value = AuthState(success = true)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                val message = when {
                    loginRequest && exception is ApiException && exception.code in listOf(400, 401) ->
                        "Неверный логин или пароль"
                    exception is ApiException -> exception.message
                    else -> "Не удалось выполнить запрос. Проверьте соединение"
                }
                mutableState.value = AuthState(error = message)
            }
        }
    }
}
