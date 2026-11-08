package com.example.hitskotlin.data.mapper

import com.example.hitskotlin.data.api.model.*
import com.example.hitskotlin.data.model.*

internal fun ReviewDto.toData() = Review(
    id.orEmpty(), reviewText.orEmpty(), rating ?: 0, isAnonymous ?: false,
    createDateTime.orEmpty(), author?.userId.orEmpty(), author?.nickName.orEmpty(), author?.avatar.orEmpty()
)

internal fun MovieDto.toData(details: Boolean = false) = Movie(
    id = id.orEmpty(),
    name = name.orEmpty(),
    poster = poster.orEmpty(),
    year = year ?: 0,
    country = country.orEmpty(),
    genres = genres.orEmpty().mapNotNull { it.name },
    ratings = reviews.orEmpty().mapNotNull { it.rating },
    reviews = if (details) reviews.orEmpty().map { it.toData() } else emptyList(),
    description = description.orEmpty(),
    director = director.orEmpty(),
    tagline = tagline.orEmpty(),
    budget = budget?.toString().orEmpty(),
    fees = fees?.toString().orEmpty(),
    ageLimit = ageLimit?.toString().orEmpty(),
    time = time?.toString().orEmpty()
)

internal fun ProfileDto.toData() = Profile(
    id.orEmpty(), nickName.orEmpty(), email.orEmpty(), avatarLink.orEmpty(), name.orEmpty(),
    birthDate.orEmpty(), gender ?: 0
)

internal fun Profile.toRequest() = ProfileRequest(
    id, nickName, email.trim(), avatarLink.trim(), name.trim(), birthDate, gender
)

internal fun Registration.toRequest() = RegistrationRequest(
    userName.trim(), name.trim(), password, email.trim(), birthDate, gender
)
