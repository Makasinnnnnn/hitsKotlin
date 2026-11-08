package com.example.hitskotlin.data.api

import com.example.hitskotlin.data.api.model.*
import retrofit2.http.*

interface ProfileApi {
    @GET("api/account/profile")
    suspend fun profile(): ProfileDto

    @PUT("api/account/profile")
    suspend fun updateProfile(@Body profile: ProfileRequest)
}
