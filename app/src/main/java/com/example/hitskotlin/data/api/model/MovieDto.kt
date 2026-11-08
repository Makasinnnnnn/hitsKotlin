package com.example.hitskotlin.data.api.model

data class MovieDto(
    val id: String? = null,
    val name: String? = null,
    val poster: String? = null,
    val year: Int? = null,
    val country: String? = null,
    val genres: List<GenreDto>? = null,
    val reviews: List<ReviewDto>? = null,
    val description: String? = null,
    val director: String? = null,
    val tagline: String? = null,
    val budget: Long? = null,
    val fees: Long? = null,
    val ageLimit: Int? = null,
    val time: Int? = null
)
