package com.example.hitskotlin.data.local

import androidx.room.TypeConverter
import com.example.hitskotlin.data.model.Review
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CacheConverters {
    private val gson = Gson()

    @TypeConverter
    fun stringsToJson(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun stringsFromJson(value: String): List<String> =
        gson.fromJson<List<String>>(value, object : TypeToken<List<String>>() {}.type).orEmpty()

    @TypeConverter
    fun ratingsToJson(value: List<Int>): String = gson.toJson(value)

    @TypeConverter
    fun ratingsFromJson(value: String): List<Int> =
        gson.fromJson<List<Int>>(value, object : TypeToken<List<Int>>() {}.type).orEmpty()

    @TypeConverter
    fun reviewsToJson(value: List<Review>): String = gson.toJson(value)

    @TypeConverter
    fun reviewsFromJson(value: String): List<Review> =
        gson.fromJson<List<Review>>(value, object : TypeToken<List<Review>>() {}.type).orEmpty()
}
