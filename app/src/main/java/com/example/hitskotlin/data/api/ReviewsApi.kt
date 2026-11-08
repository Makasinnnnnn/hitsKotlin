package com.example.hitskotlin.data.api

import com.example.hitskotlin.data.api.model.*
import retrofit2.http.*

interface ReviewsApi {
    @POST("api/movie/{movieId}/review/add")
    suspend fun addReview(@Path("movieId") movieId: String, @Body review: ReviewRequest)

    @PUT("api/movie/{movieId}/review/{id}/edit")
    suspend fun editReview(
        @Path("movieId") movieId: String,
        @Path("id") reviewId: String,
        @Body review: ReviewRequest
    )

    @DELETE("api/movie/{movieId}/review/{id}/delete")
    suspend fun deleteReview(@Path("movieId") movieId: String, @Path("id") reviewId: String)
}
