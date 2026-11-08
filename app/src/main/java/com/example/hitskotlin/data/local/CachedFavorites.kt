package com.example.hitskotlin.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hitskotlin.data.model.Movie
import com.example.hitskotlin.data.model.Profile

@Entity(tableName = "cached_favorites")
data class CachedFavorites(
    @PrimaryKey val owner: String,
    val movieIds: List<String>
)
