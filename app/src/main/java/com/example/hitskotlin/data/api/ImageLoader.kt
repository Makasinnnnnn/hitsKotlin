package com.example.hitskotlin.data.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.util.concurrent.TimeUnit

object ImageLoader {
    private val cache = object : LruCache<String, Bitmap>(16 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }
    private val slots = Semaphore(4)
    // Публичные изображения загружаются отдельным клиентом без Bearer-токена.
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    fun clear() { cache.evictAll() }

    suspend fun load(address: String): Bitmap? {
        cache.get(address)?.let { return it }
        return slots.withPermit {
            withContext(Dispatchers.IO) {
                try {
                    val uri = runCatching { URI(address) }.getOrNull() ?: return@withContext null
                    require(uri.scheme in listOf("https", "http") && !uri.host.isNullOrBlank())
                    val request = Request.Builder().url(address).build()
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) return@withContext null
                        val body = response.body ?: return@withContext null
                        require(body.contentLength() <= 8 * 1024 * 1024) { "Изображение слишком большое" }
                        val bytes = body.byteStream().use { input ->
                            val output = ByteArrayOutputStream()
                            val buffer = ByteArray(8192)
                            var total = 0
                            while (true) {
                                currentCoroutineContext().ensureActive()
                                val count = input.read(buffer)
                                if (count < 0) break
                                total += count
                                require(total <= 8 * 1024 * 1024) { "Изображение слишком большое" }
                                output.write(buffer, 0, count)
                            }
                            output.toByteArray()
                        }
                        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                        options.inSampleSize = 1
                        while (options.outWidth / options.inSampleSize > 1080 || options.outHeight / options.inSampleSize > 1920) {
                            options.inSampleSize *= 2
                        }
                        options.inJustDecodeBounds = false
                        currentCoroutineContext().ensureActive()
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)?.also { cache.put(address, it) }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: IOException) {
                    null
                } catch (exception: IllegalArgumentException) {
                    null
                }
            }
        }
    }
}
