package com.example.hitskotlin.data.local

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.room.withTransaction
import kotlinx.coroutines.CancellationException
import com.example.hitskotlin.data.api.ApiException
import com.example.hitskotlin.data.session.SessionStore
import com.example.hitskotlin.data.model.Movie
import com.example.hitskotlin.data.model.MoviePage
import com.example.hitskotlin.data.model.Profile
import java.io.IOException
import java.security.MessageDigest

class MovieCache(private val database: MovieDatabase, private val session: SessionStore) {
    private val dao = database.cacheDao()

    fun currentOwner(): String? = session.token.value?.let(::ownerFor)

    fun requireOwner(): String = currentOwner() ?: throw ApiException(401, "Войдите в аккаунт")

    // Кэш используется только при сетевом сбое, а не вместо 401 или ошибки сервера.
    suspend fun <T : Any> load(
        read: suspend (String) -> T?,
        save: suspend (String, T) -> Unit,
        request: suspend () -> T
    ): T {
        val owner = requireOwner()
        val value = try {
            request()
        } catch (exception: IOException) {
            if (exception is ApiException || owner != currentOwner()) throw exception
            val cached = read(owner)
            if (cached == null || owner != currentOwner()) throw exception
            return cached
        }
        if (owner != currentOwner()) throw CancellationException("Сессия изменилась")
        save(owner, value)
        return value
    }

    suspend fun page(owner: String, number: Int): MoviePage? = read(owner) {
        val page = dao.page(owner, number) ?: return@read null
        val movies = orderedMovies(owner, page.movieIds) ?: return@read null
        MoviePage(movies, page.pageNumber, page.pageCount, fromCache = true)
    }

    suspend fun details(owner: String, id: String): Movie? = read(owner) { dao.details(owner, id)?.movie }

    suspend fun favorites(owner: String): List<Movie>? = read(owner) {
        val snapshot = dao.favorites(owner) ?: return@read null
        orderedMovies(owner, snapshot.movieIds)
    }

    suspend fun profile(owner: String): Profile? = read(owner) { dao.profile(owner)?.profile }

    suspend fun savePage(owner: String, page: MoviePage) = update(owner) {
        saveMovies(owner, page.movies)
        dao.savePage(CachedPage(owner, page.page, page.pageCount, page.movies.map { it.id }))
    }

    suspend fun saveDetails(owner: String, movie: Movie) = update(owner) {
        dao.saveMovies(listOf(CachedMovie(owner, movie, detailsLoaded = true)))
    }

    suspend fun saveFavorites(owner: String, movies: List<Movie>) = update(owner) {
        saveMovies(owner, movies)
        dao.saveFavorites(CachedFavorites(owner, movies.map { it.id }))
    }

    suspend fun saveProfile(owner: String, profile: Profile) = update(owner) {
        dao.saveProfile(CachedProfile(owner, profile))
    }

    suspend fun setFavorite(owner: String, id: String, favorite: Boolean) = update(owner) {
        val snapshot = dao.favorites(owner) ?: return@update
        val ids = if (favorite) (snapshot.movieIds + id).distinct() else snapshot.movieIds - id
        dao.saveFavorites(snapshot.copy(movieIds = ids))
    }

    suspend fun invalidateDetails(owner: String, id: String) = update(owner) {
        dao.invalidateDetails(owner, id)
    }

    suspend fun clearOwner(owner: String) = edit {
        dao.clearPages(owner)
        dao.clearFavorites(owner)
        dao.clearProfile(owner)
        dao.clearMovies(owner)
    }

    private suspend fun orderedMovies(owner: String, ids: List<String>): List<Movie>? {
        val movies = dao.movies(owner, ids).associateBy { it.movie.id }
        if (ids.any { it !in movies }) return null
        return ids.map { movies.getValue(it).movie }
    }

    private suspend fun saveMovies(owner: String, movies: List<Movie>) {
        val previous = dao.movies(owner, movies.map { it.id }).associateBy { it.movie.id }
        dao.saveMovies(movies.map { movie ->
            val saved = previous[movie.id]
            if (saved?.detailsLoaded == true) {
                val combined = saved.movie.copy(
                    name = movie.name, poster = movie.poster, year = movie.year,
                    country = movie.country, genres = movie.genres, ratings = movie.ratings
                )
                saved.copy(movie = combined)
            } else CachedMovie(owner, movie, detailsLoaded = false)
        })
    }

    private suspend fun <T : Any> read(owner: String, query: suspend () -> T?): T? = try {
        database.withTransaction { if (owner == currentOwner()) query() else null }
    } catch (exception: SQLiteException) {
        Log.w("MovieCache", "Не удалось прочитать кэш", exception)
        null
    }

    private suspend fun update(owner: String, action: suspend () -> Unit) = edit {
        if (owner == currentOwner()) action()
    }

    private suspend fun edit(action: suspend () -> Unit) {
        try {
            database.withTransaction { action() }
        } catch (exception: SQLiteException) {
            Log.w("MovieCache", "Не удалось обновить кэш", exception)
        }
    }

    companion object {
        // Сам токен в базу не записывается.
        fun ownerFor(token: String): String = MessageDigest.getInstance("SHA-256")
            .digest(token.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and 0xff) }
    }
}
