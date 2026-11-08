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

class MoviesRepository(private val api: MoviesApi, private val cache: MovieCache) {
    suspend fun page(number: Int): MoviePage {
        require(number > 0) { "Номер страницы должен быть положительным" }
        return cache.load(read = { cache.page(it, number) }, save = cache::savePage) {
            val response = apiCall { api.movies(number) }
            MoviePage(
                response.movies.orEmpty().map { it.toData() },
                response.pageInfo?.currentPage ?: number,
                response.pageInfo?.pageCount ?: number
            )
        }
    }

    suspend fun details(id: String) = cache.load(read = { cache.details(it, id) }, save = cache::saveDetails) {
        apiCall { api.movie(id) }.toData(details = true)
    }

    suspend fun cachedDetails(id: String): Movie? {
        val owner = cache.currentOwner() ?: return null
        return cache.details(owner, id)
    }
}
