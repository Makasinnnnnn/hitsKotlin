package com.example.hitskotlin

import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.ui.mapper.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hitskotlin.domain.Validation
import com.example.hitskotlin.ui.RemoteImage

fun ratingColor(value: String): Color {
    val rating = value.toDoubleOrNull() ?: 0.0
    return when {
        rating >= 7 -> Color(0xFF4BB34B)
        rating >= 5 -> Color(0xFFFFD54F)
        else -> Color(0xFFF05C44)
    }
}

@Composable
fun MoviePoster(movie: CatalogMovie, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Crop) {
    if (movie.posterUrl.isNotBlank()) {
        RemoteImage(movie.posterUrl, movie.title, modifier, contentScale)
    } else {
        Image(painterResource(movie.poster), movie.title, modifier, contentScale = contentScale)
    }
}

// Примеры используются только в Preview.
val catalogMovies = listOf(
    CatalogMovie(
        title = "Волк с Уолл-стрит",
        poster = R.drawable.poster_wolf,
        rating = "9.0",
        year = "2013",
        country = "США",
        genres = listOf("Драма", "Комедия", "Криминал")
    ),
    CatalogMovie(
        title = "Поймай меня, если сможешь",
        poster = R.drawable.poster_catch_me,
        rating = "9.0",
        year = "2002",
        country = "США",
        genres = listOf("Драма", "Комедия", "Криминал")
    ),
    CatalogMovie(
        title = "Матрица",
        poster = R.drawable.poster_matrix,
        rating = "6.0",
        year = "1999",
        country = "США",
        genres = listOf("Боевик", "Фантастика")
    ),
    CatalogMovie(
        title = "Люди в чёрном",
        poster = R.drawable.poster_men,
        rating = "8.0",
        year = "1997",
        country = "США",
        genres = listOf("Комедия", "Фантастика")
    )
)

@Composable
fun CatalogMovieRow(
    movie: CatalogMovie,
    onMovieClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onMovieClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(95.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(3.dp))
        ) {
            MoviePoster(movie, Modifier.fillMaxSize())


            if (movie.rating.isNotBlank()) Text(
                text = movie.rating,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 2.dp, top = 2.dp)
                    .background(ratingColor(movie.rating), RoundedCornerShape(5.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = Color.Black,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp, lineHeight = 16.sp),
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = movie.title,
                modifier = Modifier.weight(1f),
                color = colorResource(R.color.white),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
                movie.userRating?.let { rating ->
                    Text(
                        text = "★ $rating",
                        color = Color.White,
                        modifier = Modifier
                            .background(ratingColor(rating.toString()), RoundedCornerShape(16.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Text(
                text = "${movie.year} · ${movie.country}",
                color = colorResource(R.color.hint_gray),
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                movie.genres.forEach { genre ->
                    Text(
                        text = genre,
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF303030))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        color = colorResource(R.color.white),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, lineHeight = 15.sp),
                        maxLines = 1
                    )
                }
            }
        }

    }
}
