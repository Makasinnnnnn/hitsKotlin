package com.example.hitskotlin.di

import android.content.Context

val Context.appContainer: AppContainer
    get() = (applicationContext as MovieApplication).container
