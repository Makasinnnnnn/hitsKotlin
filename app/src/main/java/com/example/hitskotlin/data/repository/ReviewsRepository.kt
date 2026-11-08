package com.example.hitskotlin.data.repository

import com.example.hitskotlin.data.api.*
import com.example.hitskotlin.data.api.model.*
import com.example.hitskotlin.data.mapper.*
import com.example.hitskotlin.data.local.MovieCache
import com.example.hitskotlin.data.session.SessionStore
import com.example.hitskotlin.data.model.*
import com.example.hitskotlin.domain.Validation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class ReviewsRepository(private val api: ReviewsApi, private val cache: MovieCache) {
    suspend fun save(movieId: String, reviewId: String?, text: String, rating: Int, anonymous: Boolean) {
        require(text.isNotBlank() && rating in 0..10) { "Заполните отзыв и проверьте оценку" }
        val request = ReviewRequest(text.trim(), rating, anonymous)
        val owner = cache.requireOwner()
        apiCall {
            if (reviewId == null) api.addReview(movieId, request)
            else api.editReview(movieId, reviewId, request)
        }
        cache.invalidateDetails(owner, movieId)
    }

    suspend fun delete(movieId: String, reviewId: String) {
        val owner = cache.requireOwner()
        apiCall { api.deleteReview(movieId, reviewId) }
        cache.invalidateDetails(owner, movieId)
    }
}
