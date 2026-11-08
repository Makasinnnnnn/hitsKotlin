package com.example.hitskotlin.domain

import java.net.URI

object Validation {
    fun login(username: String, password: String): Boolean = username.isNotBlank() && password.isNotBlank()

    fun registration(name: String, username: String, email: String, hasDate: Boolean): Boolean =
        name.isNotBlank() && username.isNotBlank() && email(email) && hasDate

    fun passwords(password: String, repeated: String): Boolean = password.isNotBlank() && password == repeated

    fun email(value: String): Boolean =
        Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(value.trim())

    fun link(value: String): Boolean = runCatching {
        val uri = URI(value.trim())
        uri.scheme in listOf("https", "http") && !uri.host.isNullOrBlank()
    }.getOrDefault(false)

    fun profile(name: String, email: String, avatarLink: String, birthDate: String): Boolean =
        name.isNotBlank() && email(email) && link(avatarLink) && birthDate.isNotBlank()

    fun average(ratings: List<Int>): Double? =
        if (ratings.isEmpty()) null else ratings.average()
}
