package com.example.hitskotlin.data.api.model

data class ReviewDto(
    val id: String? = null,
    val rating: Int? = null,
    val reviewText: String? = null,
    val isAnonymous: Boolean? = null,
    val createDateTime: String? = null,
    val author: AuthorDto? = null
)

// Список содержит краткие отзывы без автора, а подробности — полные.
