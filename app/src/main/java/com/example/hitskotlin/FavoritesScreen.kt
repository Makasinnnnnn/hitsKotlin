package com.example.hitskotlin

import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.ui.mapper.*

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FavoritesScreen(
    movies: List<CatalogMovie>,
    onMovieClick: (CatalogMovie) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.screen_background))
    ) {
        Text(
            text = "Любимое",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            color = colorResource(R.color.white),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        if (movies.isEmpty()) {
            EmptyFavorites()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Каждая третья карточка занимает обе колонки.
                itemsIndexed(
                    items = movies,
                    key = { _, movie -> movie.id },
                    span = { index, _ -> GridItemSpan(if (index % 3 == 2) 2 else 1) }
                ) { _, movie ->
                    FavoriteMovieCard(movie, onClick = { onMovieClick(movie) })
                }
            }
        }
    }
}

@Composable
private fun FavoriteMovieCard(movie: CatalogMovie, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(204.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            MoviePoster(movie, Modifier.fillMaxSize())

            if (movie.rating.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(ratingColor(movie.rating), RoundedCornerShape(16.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "★", color = Color.White, fontSize = 14.sp)
                    Text(
                        text = movie.rating.removeSuffix(".0"),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Text(
            text = movie.title,
            color = colorResource(R.color.white),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyFavorites() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 88.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "У вас пока нет любимых\nфильмов",
            color = colorResource(R.color.white),
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Выберите понравившийся фильм в каталоге\nи добавьте его в любимые",
            color = colorResource(R.color.white),
            fontSize = 15.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Данные только для предпросмотра макета.
private val favoritePreviewMovies = listOf(
    CatalogMovie("По соображениям совести", R.drawable.poster_hacksaw_ridge, "9", "2016", "США", emptyList()),
    CatalogMovie("Джентльмены", R.drawable.poster_gentlemen, "", "2019", "Великобритания", emptyList()),
    CatalogMovie("Человек-паук: Нет пути домой", R.drawable.poster_spider_man, "9", "2021", "США", emptyList()),
    CatalogMovie("Собачья жизнь 2", R.drawable.poster_dogs_journey, "", "2019", "США", emptyList()),
    CatalogMovie("Марсианин", R.drawable.poster_martian, "8", "2015", "США", emptyList())
)

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun FavoritesScreenPreview() {
    FilmusTheme {
        FavoritesScreen(favoritePreviewMovies)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun EmptyFavoritesScreenPreview() {
    FilmusTheme {
        FavoritesScreen(emptyList())
    }
}
