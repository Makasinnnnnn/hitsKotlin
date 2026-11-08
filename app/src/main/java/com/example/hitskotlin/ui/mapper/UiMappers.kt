package com.example.hitskotlin.ui.mapper

import com.example.hitskotlin.R
import com.example.hitskotlin.data.model.*
import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.domain.Validation
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Review.toUi() = ReviewUi(
    id = id,
    text = text,
    rating = rating,
    anonymous = anonymous,
    date = date,
    authorId = authorId,
    authorName = authorName,
    avatar = avatar
)

fun Movie.toUi() = MovieUi(
    id = id,
    name = name,
    poster = poster,
    year = year,
    country = country,
    genres = genres,
    ratings = ratings,
    reviews = reviews.map { it.toUi() },
    description = description,
    director = director,
    tagline = tagline,
    budget = budget,
    fees = fees,
    ageLimit = ageLimit,
    time = time
)

fun Profile.toUi() = ProfileUi(
    id = id, nickName = nickName, email = email, avatarLink = avatarLink,
    name = name, birthDate = birthDate, gender = gender
)

fun ProfileUi.toData() = Profile(
    id = id, nickName = nickName, email = email, avatarLink = avatarLink,
    name = name, birthDate = birthDate, gender = gender
)

fun RegistrationForm.toData() = Registration(
    userName = userName, name = name, password = password,
    email = email, birthDate = birthDate, gender = gender
)

fun MovieUi.toCard(userRating: Int? = null): CatalogMovie = CatalogMovie(
    title = name,
    poster = R.drawable.ic_splash_logo,
    rating = Validation.average(ratings)?.let { String.format(Locale.US, "%.1f", it) }.orEmpty(),
    year = year.toString(),
    country = country,
    genres = genres,
    id = id,
    posterUrl = poster,
    userRating = userRating,
    source = this
)

fun ReviewUi.toUiReview() = MovieReview(
    text = text,
    rating = rating,
    anonymous = anonymous,
    date = runCatching {
        LocalDate.parse(date.take(10)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }.getOrDefault(date),
    id = id,
    avatarUrl = avatar
)
