package com.example.hitskotlin.data.api.model

data class MoviesPageResponse(
    val movies: List<MovieDto>? = null,
    val pageInfo: PageInfoDto? = null
)
