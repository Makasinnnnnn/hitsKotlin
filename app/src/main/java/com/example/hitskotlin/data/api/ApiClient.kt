package com.example.hitskotlin.data.api

import com.example.hitskotlin.data.session.SessionStore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    fun create(session: SessionStore? = null): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .callTimeout(45, TimeUnit.SECONDS)
        if (session != null) client.addInterceptor(AuthInterceptor(session))

        return Retrofit.Builder()
            .baseUrl("https://react-midterm.kreosoft.space/")
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
