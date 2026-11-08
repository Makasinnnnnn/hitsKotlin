package com.example.hitskotlin

import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.ui.mapper.*

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hitskotlin.ui.RemoteImage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilmScreen(
    movie: CatalogMovie,
    isFavorite: Boolean,
    review: MovieReview?,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSaveReview: (MovieReview) -> Unit,
    onDeleteReview: () -> Unit,
    allReviews: List<ReviewUi> = emptyList(),
    currentUserId: String = "",
    loading: Boolean = false,
    error: String? = null
) {
    BackHandler(onBack = onBack)
    val background = colorResource(R.color.screen_background)
    val pink = colorResource(R.color.accent_pink)
    val scroll = rememberLazyListState()
    var expanded by rememberSaveable(movie.title) { mutableStateOf(false) }
    var showReviewDialog by rememberSaveable(movie.title) { mutableStateOf(false) }
    val threshold = with(LocalDensity.current) { 400.dp.toPx() }
    val fadeDistance = with(LocalDensity.current) { 100.dp.toPx() }
    val toolbarAlpha by remember(threshold, fadeDistance) {
        derivedStateOf {
            if (scroll.firstVisibleItemIndex > 0) 1f
            else ((scroll.firstVisibleItemScrollOffset - threshold) / fadeDistance).coerceIn(0f, 1f)
        }
    }
    val bannerDarkness by remember(threshold) {
        derivedStateOf {
            if (scroll.firstVisibleItemIndex > 0) 0.7f
            else (scroll.firstVisibleItemScrollOffset / threshold).coerceIn(0f, 1f) * 0.7f
        }
    }
    val source = movie.source
    LaunchedEffect(loading, error) {
        if (!loading && error == null) showReviewDialog = false
    }

    BoxWithConstraints(
        Modifier.fillMaxSize().background(background)
            .then(if (showReviewDialog) Modifier.blur(8.dp) else Modifier)
    ) {
        val posterHeight = minOf(maxWidth * (520f / 360f), (maxHeight - 56.dp) * 0.6f)
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(painterResource(R.drawable.ic_back), "Назад", tint = Color.White)
                }
                Text(
                    movie.title,
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = toolbarAlpha),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (toolbarAlpha > 0f) {
                    FavoriteButton(isFavorite, onToggleFavorite)
                }
            }

            LazyColumn(
                state = scroll,
                modifier = Modifier.weight(1f).clipToBounds(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Box(
                        Modifier.fillMaxWidth()
                            .height(posterHeight)
                            .clipToBounds()
                    ) {
                        MoviePoster(
                            movie,
                            Modifier.fillMaxSize(),
                            ContentScale.Crop
                        )
                        Box(Modifier.fillMaxSize().background(background.copy(alpha = bannerDarkness)))
                        Box(
                            Modifier.fillMaxSize().background(
                                Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    0.6f to Color.Transparent,
                                    0.9f to background,
                                    1f to background
                                )
                            )
                        )
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        if (movie.rating.isNotBlank()) {
                            Text(
                                movie.rating,
                                color = Color.Black,
                                modifier = Modifier.padding(top = 6.dp)
                                    .background(ratingColor(movie.rating), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            movie.title,
                            color = Color.White,
                            fontSize = 24.sp,
                            lineHeight = 29.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        FavoriteButton(isFavorite, onToggleFavorite)
                    }
                }
                item {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = source?.description.orEmpty().ifBlank { "Описание фильма пока недоступно." },
                            color = Color.White,
                            fontSize = 15.sp,
                            maxLines = if (expanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        TextButton(onClick = { expanded = !expanded }, contentPadding = PaddingValues(0.dp)) {
                            Text(if (expanded) "Свернуть  ⌃" else "Подробнее  ⌄", color = pink)
                        }
                        FilmHeading("Жанры")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            movie.genres.forEach { genre ->
                                Text(
                                    genre.lowercase(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.background(pink, RoundedCornerShape(5.dp))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                        FilmHeading("О фильме")
                        FilmInfoRow("Год", movie.year)
                        FilmInfoRow("Страна", movie.country)
                        FilmInfoRow("Слоган", source?.tagline.orEmpty())
                        FilmInfoRow("Режиссёр", source?.director.orEmpty())
                        FilmInfoRow("Бюджет", money(source?.budget.orEmpty()))
                        FilmInfoRow("Сборы в мире", money(source?.fees.orEmpty()))
                        FilmInfoRow("Возраст", source?.ageLimit?.let { "$it+" }.orEmpty())
                        FilmInfoRow("Время", source?.time?.let { "$it мин." }.orEmpty())
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Отзывы",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            if (review == null) {
                                TextButton(onClick = { showReviewDialog = true }) { Text("Добавить", color = pink) }
                            }
                        }
                        if (review != null) {
                            FilmReview(
                                author = if (review.anonymous) "Анонимный пользователь" else allReviews.find { it.authorId == currentUserId }?.authorName.orEmpty(),
                                review = review,
                                own = true,
                                onEdit = { showReviewDialog = true },
                                onDelete = onDeleteReview
                            )
                        }
                        allReviews.filter { it.authorId != currentUserId }.forEach { item ->
                            FilmReview(
                                author = if (item.anonymous) "Анонимный пользователь" else item.authorName,
                                review = item.toUiReview()
                            )
                        }
                        if (allReviews.isEmpty()) {
                            Text("Отзывов пока нет", color = colorResource(R.color.hint_gray))
                        }
                        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
                        if (error != null) Text(error, color = Color(0xFFFF7070))

                    }
                }
            }
        }
    }

    if (showReviewDialog) {
        ReviewDialog(
            initialReview = review,
            onDismiss = { showReviewDialog = false },
            onSave = {
                onSaveReview(it)
            },
            loading = loading,
            error = error
        )
    }
}

@Composable
private fun FavoriteButton(selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Box(
            modifier = Modifier.size(40.dp).background(Color(0xFF363636), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(if (selected) R.drawable.ic_heart_filled else R.drawable.ic_heart),
                contentDescription = if (selected) "Убрать из любимого" else "В любимое",
                tint = if (selected) colorResource(R.color.accent_pink) else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun FilmHeading(text: String) {
    Text(
        text,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
    )
}

@Composable
private fun FilmInfoRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Text(label, modifier = Modifier.weight(0.35f).padding(end = 12.dp), color = colorResource(R.color.hint_gray), fontSize = 15.sp)
        Text(value, modifier = Modifier.weight(0.65f), color = Color.White, fontSize = 15.sp)
    }
}

private fun money(value: String): String {
    val amount = value.toLongOrNull() ?: return value
    return "$" + java.text.NumberFormat.getIntegerInstance(java.util.Locale.US).format(amount)
}

@Composable
private fun FilmReview(
    author: String,
    review: MovieReview,
    own: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var menu by remember { mutableStateOf(false) }
    Column(Modifier.padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (review.anonymous) {
                Box(
                    Modifier.size(40.dp).background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(R.drawable.ic_person), contentDescription = null, tint = Color.Black)
                }
            } else {
                RemoteImage(
                    review.avatarUrl,
                    description = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
            }
            Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(author, color = Color.White, fontSize = 15.sp)
                if (own) Text("Мой отзыв", color = colorResource(R.color.hint_gray), fontSize = 12.sp)
            }
            Text(
                "★ ${review.rating}",
                color = Color.White,
                modifier = Modifier.background(ratingColor(review.rating.toString()), CircleShape).padding(horizontal = 7.dp, vertical = 3.dp)
            )
            if (own) {
                Box {
                    IconButton(onClick = { menu = true }) { Text("⋯", color = Color.White, fontSize = 24.sp) }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(text = { Text("Редактировать") }, onClick = { menu = false; onEdit() })
                        DropdownMenuItem(text = { Text("Удалить") }, onClick = { menu = false; onDelete() })
                    }
                }
            }
        }
        Text(review.text, modifier = Modifier.padding(top = 8.dp), color = Color.White, fontSize = 15.sp)
        Text(
            review.date,
            modifier = Modifier.padding(top = 4.dp),
            color = colorResource(R.color.hint_gray),
            fontSize = 12.sp
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun FilmScreenPreview() {
    FilmusTheme {
        FilmScreen(catalogMovies[2], false, null, {}, {}, {}, {})
    }
}
