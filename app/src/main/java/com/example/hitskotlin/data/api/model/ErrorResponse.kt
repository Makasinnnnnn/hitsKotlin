package com.example.hitskotlin.data.api.model

internal data class ErrorResponse(
    val message: String? = null,
    val title: String? = null,
    val errors: Map<String, List<String?>?>? = null
)
