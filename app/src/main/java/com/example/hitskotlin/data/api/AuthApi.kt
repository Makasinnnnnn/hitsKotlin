package com.example.hitskotlin.data.api

import com.example.hitskotlin.data.api.model.*
import retrofit2.http.*

interface AuthApi {
    @POST("api/account/login")
    suspend fun login(@Body credentials: LoginRequest): TokenResponse

    @POST("api/account/register")
    suspend fun register(@Body form: RegistrationRequest): TokenResponse
}
