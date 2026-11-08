package com.example.hitskotlin.data.model

data class Review(
    val id: String,
    val text: String,
    val rating: Int,
    val anonymous: Boolean,
    val date: String,
    val authorId: String,
    val authorName: String,
    val avatar: String
)
