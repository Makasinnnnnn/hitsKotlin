package com.example.hitskotlin.di

import android.app.Application

class MovieApplication : Application() {
    val container by lazy { AppContainer(this) }
}
