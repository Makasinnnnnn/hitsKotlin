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

class FilmViewModel(
    private val moviesRepository: MoviesRepository,
    private val profileRepository: ProfileRepository,
    private val favoritesRepository: FavoritesRepository,
    private val reviewsRepository: ReviewsRepository
) : ScreenViewModel() {
    var movie by mutableStateOf<MovieUi?>(null)
        private set
    var favorite by mutableStateOf(false)
        private set
    var userId by mutableStateOf("")
        private set
    var ready by mutableStateOf(false)
        private set

    val ownReview: ReviewUi? get() = movie?.reviews?.find { it.authorId == userId }

    fun prepare() {
        ready = false
        error = null
    }

    fun open(id: String) = request {
        ready = false
        movie = null
        userId = profileRepository.get().id
        movie = moviesRepository.details(id).toUi()
        favorite = favoritesRepository.all().any { it.id == id }
        ready = true
    }

    fun toggleFavorite(onChanged: () -> Unit) = request {
        val current = movie ?: return@request
        favoritesRepository.set(current.id, !favorite)
        favorite = !favorite
        onChanged()
    }

    fun saveReview(text: String, rating: Int, anonymous: Boolean, onChanged: (MovieUi, Int?) -> Unit) = request {
        val current = movie ?: return@request
        reviewsRepository.save(current.id, ownReview?.id, text, rating, anonymous)
        val updated = moviesRepository.details(current.id).toUi()
        movie = updated
        onChanged(updated, ownReview?.rating)
    }

    fun deleteReview(onChanged: (MovieUi, Int?) -> Unit) = request {
        val current = movie ?: return@request
        val own = ownReview ?: return@request
        reviewsRepository.delete(current.id, own.id)
        val updated = moviesRepository.details(current.id).toUi()
        movie = updated
        onChanged(updated, null)
    }
}
