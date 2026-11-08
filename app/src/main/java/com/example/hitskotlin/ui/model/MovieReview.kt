package com.example.hitskotlin.ui.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MovieReview(
    val text: String,
    val rating: Int,
    val anonymous: Boolean,
    val date: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
    val id: String = "",
    val avatarUrl: String = ""
)
