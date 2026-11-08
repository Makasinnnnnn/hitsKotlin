package com.example.hitskotlin.data.session

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionStore(context: Context, private val onClear: (String?) -> Unit = {}) {
    private val preferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    private val mutableToken = MutableStateFlow(preferences.getString("token", null))
    val token = mutableToken.asStateFlow()

    @Synchronized
    fun save(value: String) {
        require(value.isNotBlank())
        val previous = mutableToken.value
        preferences.edit { putString("token", value) }
        mutableToken.value = value
        if (previous != null && previous != value) onClear(previous)
    }

    @Synchronized
    fun clear() {
        val previous = mutableToken.value
        preferences.edit { clear() }
        mutableToken.value = null
        onClear(previous)
    }

    @Synchronized
    fun clearIfToken(value: String) {
        if (mutableToken.value == value) clear()
    }
}
