package com.example.hitskotlin.data.model

data class MoviePage(val movies: List<Movie>, val page: Int, val pageCount: Int, val fromCache: Boolean = false)
