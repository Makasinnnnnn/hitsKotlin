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

open class ScreenViewModel : ViewModel() {
    var busy by mutableStateOf(false)
        protected set
    var error by mutableStateOf<String?>(null)
        protected set

    protected fun request(action: suspend () -> Unit) {
        if (busy) return
        viewModelScope.launch {
            busy = true
            error = null
            try { action() }
            catch (exception: CancellationException) { throw exception }
            catch (exception: Exception) {
                error = when (exception) {
                    is ApiException -> exception.message
                    is IOException -> "Не удалось соединиться с сервером. Проверьте интернет"
                    else -> exception.message ?: "Не удалось обработать данные"
                }
            } finally { busy = false }
        }
    }

    fun dismissError() { error = null }
}
