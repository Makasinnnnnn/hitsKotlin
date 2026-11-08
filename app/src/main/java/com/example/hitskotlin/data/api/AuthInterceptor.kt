package com.example.hitskotlin.data.api

import com.example.hitskotlin.data.session.SessionStore
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val session: SessionStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = session.token.value ?: throw ApiException(401, "Войдите в аккаунт")
        val request = original.newBuilder()
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .build()

        val response = chain.proceed(request)
        if (response.code == 401) session.clearIfToken(token)
        return response
    }
}
