package com.example.hitskotlin.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CacheDao {
    @Query("SELECT * FROM cached_pages WHERE owner = :owner AND pageNumber = :number")
    suspend fun page(owner: String, number: Int): CachedPage?

    @Query("SELECT * FROM cached_movies WHERE owner = :owner AND id IN (:ids)")
    suspend fun movies(owner: String, ids: List<String>): List<CachedMovie>

    @Query("SELECT * FROM cached_movies WHERE owner = :owner AND id = :id AND detailsLoaded = 1")
    suspend fun details(owner: String, id: String): CachedMovie?

    @Query("SELECT * FROM cached_favorites WHERE owner = :owner")
    suspend fun favorites(owner: String): CachedFavorites?

    @Query("SELECT * FROM cached_profiles WHERE owner = :owner")
    suspend fun profile(owner: String): CachedProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovies(movies: List<CachedMovie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePage(page: CachedPage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFavorites(favorites: CachedFavorites)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: CachedProfile)

    @Query("UPDATE cached_movies SET detailsLoaded = 0, reviews = '[]' WHERE owner = :owner AND id = :id")
    suspend fun invalidateDetails(owner: String, id: String)

    @Query("DELETE FROM cached_movies WHERE owner = :owner")
    suspend fun clearMovies(owner: String)

    @Query("DELETE FROM cached_pages WHERE owner = :owner")
    suspend fun clearPages(owner: String)

    @Query("DELETE FROM cached_favorites WHERE owner = :owner")
    suspend fun clearFavorites(owner: String)

    @Query("DELETE FROM cached_profiles WHERE owner = :owner")
    suspend fun clearProfile(owner: String)
}
