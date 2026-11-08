package com.example.hitskotlin.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hitskotlin.data.model.Movie
import com.example.hitskotlin.data.model.Profile

@Entity(tableName = "cached_pages", primaryKeys = ["owner", "pageNumber"])
data class CachedPage(
    val owner: String,
    val pageNumber: Int,
    val pageCount: Int,
    val movieIds: List<String>
)
