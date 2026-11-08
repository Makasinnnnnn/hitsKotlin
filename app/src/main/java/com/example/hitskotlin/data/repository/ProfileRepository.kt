package com.example.hitskotlin.data.repository

import com.example.hitskotlin.data.api.*
import com.example.hitskotlin.data.api.model.*
import com.example.hitskotlin.data.mapper.*
import com.example.hitskotlin.data.local.MovieCache
import com.example.hitskotlin.data.session.SessionStore
import com.example.hitskotlin.data.model.*
import com.example.hitskotlin.domain.Validation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class ProfileRepository(private val api: ProfileApi, private val cache: MovieCache) {
    suspend fun get() = cache.load(read = cache::profile, save = cache::saveProfile) {
        apiCall { api.profile() }.toData()
    }

    suspend fun cached(): Profile? {
        val owner = cache.currentOwner() ?: return null
        return cache.profile(owner)
    }

    suspend fun save(profile: Profile): Profile {
        require(Validation.profile(profile.name, profile.email, profile.avatarLink, profile.birthDate)) { "Проверьте имя, email, ссылку и дату рождения" }
        val owner = cache.requireOwner()
        apiCall { api.updateProfile(profile.toRequest()) }
        cache.saveProfile(owner, profile)
        return get()
    }
}
