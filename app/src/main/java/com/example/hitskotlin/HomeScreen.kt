package com.example.hitskotlin

import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.ui.mapper.*

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    movies: List<CatalogMovie> = catalogMovies,
    loading: Boolean = false,
    error: String? = null,
    endReached: Boolean = true,
    onLoadNext: () -> Unit = {},
    onMovieClick: (CatalogMovie) -> Unit = {}
) {
    val featured = movies.take(4)
    val pager = rememberPagerState(pageCount = { featured.size })
    val scroll = rememberLazyListState()
    val nearEnd by remember {
        derivedStateOf {
            val last = scroll.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= scroll.layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(nearEnd, movies.size, loading, error, endReached) {
        if (nearEnd && !loading && error == null && !endReached) onLoadNext()
    }

    LazyColumn(
        state = scroll,
        modifier = Modifier.fillMaxSize().background(colorResource(R.color.screen_background)),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (featured.isNotEmpty()) item {
            Box(Modifier.fillMaxWidth().aspectRatio(360f / 500f)) {
                HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) { index ->
                    MoviePoster(
                        featured[index],
                        Modifier.fillMaxSize().clickable { onMovieClick(featured[index]) }
                    )
                }
                Row(
                    Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(featured.size) { index ->
                        Box(Modifier.size(6.dp).border(1.dp, Color.White, CircleShape)
                            .background(if (pager.currentPage == index) Color.White else Color.Transparent, CircleShape))
                    }
                }
            }
        }
        if (movies.isNotEmpty()) item {
            Text(
                "Каталог", Modifier.padding(horizontal = 16.dp),
                color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )
        }
        items(movies.drop(4), key = { it.id }) { movie ->
            CatalogMovieRow(movie) { onMovieClick(movie) }
        }
        item {
            if (loading) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Column(Modifier.padding(16.dp)) {
                    Text(error, color = Color.White)
                    TextButton(onClick = onLoadNext) { Text("Повторить") }
                }
            } else if (movies.isEmpty()) {
                Text("В каталоге пока нет фильмов", Modifier.padding(24.dp), color = Color.White)
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    FilmusTheme { HomeScreen() }
}
