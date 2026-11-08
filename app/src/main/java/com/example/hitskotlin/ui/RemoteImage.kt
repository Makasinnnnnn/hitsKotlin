package com.example.hitskotlin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.hitskotlin.data.api.ImageLoader

@Composable
fun RemoteImage(
    address: String,
    description: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val bitmap by produceState<android.graphics.Bitmap?>(null, address) {
        value = null
        if (address.isNotBlank()) value = ImageLoader.load(address)
    }
    Box(modifier.background(Color(0xFF292929)), contentAlignment = Alignment.Center) {
        val image = bitmap
        if (image != null) {
            Image(image.asImageBitmap(), description, Modifier.fillMaxSize(), contentScale = contentScale)
        } else {
            Text("—", color = Color(0xFF909499))
        }
    }
}
