package com.example.hitskotlin.data.api

import com.example.hitskotlin.data.api.model.*
import retrofit2.http.*

interface AccountApi {
    @POST("api/account/logout")
    suspend fun logout()
}
