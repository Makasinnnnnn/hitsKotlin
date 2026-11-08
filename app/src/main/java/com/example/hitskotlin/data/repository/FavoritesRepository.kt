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

class FavoritesRepository(private val api: FavoritesApi, private val cache: MovieCache) {
    suspend fun all(): List<Movie> = cache.load(read = cache::favorites, save = cache::saveFavorites) {
        apiCall { api.favorites() }.movies.orEmpty().map { it.toData() }
    }

    suspend fun set(id: String, favorite: Boolean) {
        val owner = cache.requireOwner()
        apiCall {
            if (favorite) api.addFavorite(id)
            else api.deleteFavorite(id)
        }
        cache.setFavorite(owner, id, favorite)
    }
}
