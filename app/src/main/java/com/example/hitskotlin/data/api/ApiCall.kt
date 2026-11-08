package com.example.hitskotlin.data.api

import com.example.hitskotlin.data.api.model.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

private val gson = Gson()

internal suspend fun <T> apiCall(action: suspend () -> T): T {
    try {
        return action()
    } catch (exception: HttpException) {
        throw ApiException(exception.code(), errorMessage(exception))
    }
}

private fun errorMessage(exception: HttpException): String {
    val code = exception.code()
    val body = try {
        exception.response()?.errorBody()?.use { it.string() }.orEmpty()
    } catch (exception: IOException) { "" }
    if (code == 401) return "Сессия истекла или неверные данные для входа"
    val response = runCatching { gson.fromJson(body, ErrorResponse::class.java) }.getOrNull()
    val validation = response?.errors?.values?.filterNotNull()?.flatten()?.filterNotNull()?.joinToString("\n").orEmpty()
    return validation.ifBlank {
        response?.message.orEmpty().ifBlank { response?.title.orEmpty() }
    }.ifBlank {
        when (code) {
            400 -> "Проверьте введённые данные"
            403 -> "Недостаточно прав"
            404 -> "Данные не найдены"
            409 -> "Данные уже существуют"
            else -> "Ошибка сервера ($code). Попробуйте ещё раз"
        }
    }
}
