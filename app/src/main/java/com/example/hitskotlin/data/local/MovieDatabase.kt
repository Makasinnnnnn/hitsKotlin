package com.example.hitskotlin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CachedMovie::class, CachedPage::class, CachedFavorites::class, CachedProfile::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(CacheConverters::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}
