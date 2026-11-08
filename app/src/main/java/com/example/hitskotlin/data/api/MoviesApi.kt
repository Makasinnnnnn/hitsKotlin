package com.example.hitskotlin.data.api

import com.example.hitskotlin.data.api.model.*
import retrofit2.http.*

interface MoviesApi {
    @GET("api/movies/{page}")
    suspend fun movies(@Path("page") page: Int): MoviesPageResponse

    @GET("api/movies/details/{id}")
    suspend fun movie(@Path("id") id: String): MovieDto
}
