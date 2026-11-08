package com.example.hitskotlin.ui.model

data class CatalogMovie(
    val title: String,
    val poster: Int,
    val rating: String,
    val year: String,
    val country: String,
    val genres: List<String>,
    val id: String = title,
    val posterUrl: String = "",
    val userRating: Int? = null,
    val source: MovieUi? = null
)
