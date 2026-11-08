package com.example.hitskotlin.ui.model

data class MovieUi(
    val id: String,
    val name: String,
    val poster: String,
    val year: Int,
    val country: String,
    val genres: List<String>,
    val ratings: List<Int>,
    val reviews: List<ReviewUi> = emptyList(),
    val description: String = "",
    val director: String = "",
    val tagline: String = "",
    val budget: String = "",
    val fees: String = "",
    val ageLimit: String = "",
    val time: String = ""
)
