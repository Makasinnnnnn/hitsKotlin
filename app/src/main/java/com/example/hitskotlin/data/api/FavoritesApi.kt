package com.example.hitskotlin.data.api

import com.example.hitskotlin.data.api.model.*
import retrofit2.http.*

interface FavoritesApi {
    @GET("api/favorites")
    suspend fun favorites(): MoviesResponse

    @POST("api/favorites/{id}/add")
    suspend fun addFavorite(@Path("id") id: String)

    @DELETE("api/favorites/{id}/delete")
    suspend fun deleteFavorite(@Path("id") id: String)
}
