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

class FavoritesViewModel(private val repository: FavoritesRepository) : ScreenViewModel() {
    var movies by mutableStateOf<List<MovieUi>>(emptyList())
        private set
    var loaded by mutableStateOf(false)
        private set

    fun load() = request {
        movies = repository.all().map { it.toUi() }
        loaded = true
    }

    fun applyMovie(movie: MovieUi) {
        movies = movies.map { if (it.id == movie.id) movie else it }
    }

    fun contains(id: String) = movies.any { it.id == id }
}
