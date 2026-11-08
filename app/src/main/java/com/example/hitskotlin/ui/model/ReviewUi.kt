package com.example.hitskotlin.ui.model

data class ReviewUi(
    val id: String,
    val text: String,
    val rating: Int,
    val anonymous: Boolean,
    val date: String,
    val authorId: String,
    val authorName: String,
    val avatar: String
)
