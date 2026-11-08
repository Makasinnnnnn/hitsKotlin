package com.example.hitskotlin.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hitskotlin.data.api.ApiException
import com.example.hitskotlin.data.repository.*
import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.ui.mapper.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(
    private val moviesRepository: MoviesRepository,
    private val profileRepository: ProfileRepository
) : ScreenViewModel() {
    var movies by mutableStateOf<List<MovieUi>>(emptyList())
        private set
    var userRatings by mutableStateOf<Map<String, Int>>(emptyMap())
        private set
    private var nextPage = 1
    var endReached by mutableStateOf(false)
        private set

    init { loadNext() }

    fun loadNext() {
        if (endReached || busy) return
        request {
            val page = moviesRepository.page(nextPage)
            val userId = if (page.fromCache) profileRepository.cached()?.id.orEmpty() else profileRepository.get().id
            val ratings = userRatings.toMutableMap()
            // Краткий список не содержит авторов: свою оценку получаем из подробностей.
            for (movie in page.movies.filter { it.ratings.isNotEmpty() }) {
                val details = if (page.fromCache) moviesRepository.cachedDetails(movie.id) else moviesRepository.details(movie.id)
                val own = details?.reviews?.find { userId.isNotBlank() && it.authorId == userId }
                if (own != null) ratings[movie.id] = own.rating
            }
            userRatings = ratings
            movies = (movies + page.movies.map { it.toUi() }).distinctBy { it.id }
            nextPage = page.page + 1
            endReached = page.movies.isEmpty() || page.page >= page.pageCount
        }
    }

    fun refreshMovie(movie: MovieUi, ownRating: Int?) {
        movies = movies.map { if (it.id == movie.id) movie else it }
        userRatings = if (ownRating == null) userRatings - movie.id else userRatings + (movie.id to ownRating)
    }
}
