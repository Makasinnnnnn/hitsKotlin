package com.example.hitskotlin.di

import android.content.Context
import androidx.room.Room
import com.example.hitskotlin.data.api.*
import com.example.hitskotlin.data.local.MovieCache
import com.example.hitskotlin.data.local.MovieDatabase
import com.example.hitskotlin.data.repository.*
import com.example.hitskotlin.data.session.SessionStore
import com.example.hitskotlin.ui.viewmodel.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppContainer(
    context: Context,
    private val database: MovieDatabase = Room.databaseBuilder(
        context.applicationContext, MovieDatabase::class.java, "movie_cache.db"
    ).build()
) {
    private val cacheScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val session: SessionStore = SessionStore(context.applicationContext) { previous ->
        ImageLoader.clear()
        if (previous != null) cacheScope.launch { cache.clearOwner(MovieCache.ownerFor(previous)) }
    }
    private val cache: MovieCache by lazy { MovieCache(database, session) }
    private val publicClient = ApiClient.create()
    private val authorizedClient = ApiClient.create(session)

    private val auth = AuthRepository(
        publicClient.create(AuthApi::class.java),
        session,
        authorizedClient.create(AccountApi::class.java)
    )
    private val movies = MoviesRepository(authorizedClient.create(MoviesApi::class.java), cache)
    private val favorites = FavoritesRepository(authorizedClient.create(FavoritesApi::class.java), cache)
    private val profile = ProfileRepository(authorizedClient.create(ProfileApi::class.java), cache)
    private val reviews = ReviewsRepository(authorizedClient.create(ReviewsApi::class.java), cache)

    val loginFactory = ViewModelFactory { LoginViewModel(auth) }
    val registrationFactory = ViewModelFactory { RegistrationViewModel(auth) }
    val homeFactory = ViewModelFactory { HomeViewModel(movies, profile) }
    val favoritesFactory = ViewModelFactory { FavoritesViewModel(favorites) }
    val profileFactory = ViewModelFactory { ProfileViewModel(profile, auth) }
    val filmFactory = ViewModelFactory { FilmViewModel(movies, profile, favorites, reviews) }
}
