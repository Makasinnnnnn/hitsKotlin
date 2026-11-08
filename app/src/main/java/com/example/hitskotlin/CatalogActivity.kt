package com.example.hitskotlin

import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.ui.mapper.*

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hitskotlin.di.appContainer
import com.example.hitskotlin.ui.openWelcome
import com.example.hitskotlin.ui.viewmodel.*
import kotlinx.coroutines.launch

class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.rgb(29, 29, 29)),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.rgb(29, 29, 29))
        )
        val app = appContainer
        if (app.session.token.value == null) {
            openWelcome()
            return
        }
        val main = ViewModelProvider(this, app.homeFactory)[HomeViewModel::class.java]
        val favorites = ViewModelProvider(this, app.favoritesFactory)[FavoritesViewModel::class.java]
        val profile = ViewModelProvider(this, app.profileFactory)[ProfileViewModel::class.java]
        val film = ViewModelProvider(this, app.filmFactory)[FilmViewModel::class.java]
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.session.token.collect { if (it == null) openWelcome() }
            }
        }
        setContent { CatalogScreen(main, favorites, profile, film) }
    }
}

@Composable
fun CatalogScreen(
    main: HomeViewModel,
    favorites: FavoritesViewModel,
    profile: ProfileViewModel,
    film: FilmViewModel
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedMovie by rememberSaveable { mutableStateOf<String?>(null) }
    val tabState = rememberSaveableStateHolder()
    val pink = colorResource(R.color.accent_pink)
    val background = colorResource(R.color.screen_background)
    val titles = listOf("Главное", "Любимое", "Профиль")
    val icons = listOf(R.drawable.ic_home, R.drawable.ic_heart, R.drawable.ic_person)

    BackHandler(enabled = selectedMovie != null) { selectedMovie = null }

    LaunchedEffect(selectedTab, selectedMovie) {
        if (selectedTab == 1 && selectedMovie == null) favorites.load()
    }
    LaunchedEffect(selectedMovie) {
        if (selectedMovie == null) film.dismissError() else film.prepare()
    }
    LaunchedEffect(selectedMovie, film.busy, film.error) {
        selectedMovie?.let { id ->
            if (!film.busy && (film.movie?.id != id || !film.ready) && film.error == null) film.open(id)
        }
    }

    FilmusTheme {
        Scaffold(
            containerColor = background,
            bottomBar = {
                if (selectedMovie == null) NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars).height(56.dp),
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    containerColor = background
                ) {
                    titles.forEachIndexed { index, title ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(painterResource(icons[index]), title) },
                            label = { Text(title, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = pink, selectedTextColor = pink,
                                unselectedIconColor = colorResource(R.color.hint_gray),
                                unselectedTextColor = colorResource(R.color.hint_gray),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                val id = selectedMovie
                if (id != null) {
                    val movie = film.movie
                    if (movie == null || !film.ready || movie.id != id) {
                        Column {
                            TextButton(onClick = { selectedMovie = null }) { Text("Назад") }
                            Box(Modifier.weight(1f)) { RequestStatus(film.busy, film.error) { film.open(id) } }
                        }
                    } else {
                        val own = film.ownReview
                        FilmScreen(
                            movie = movie.toCard(own?.rating),
                            isFavorite = film.favorite,
                            review = own?.toUiReview(),
                            onBack = { selectedMovie = null },
                            onToggleFavorite = { film.toggleFavorite { favorites.load() } },
                            onSaveReview = { value ->
                                film.saveReview(value.text, value.rating, value.anonymous) { updated, rating ->
                                    main.refreshMovie(updated, rating)
                                    favorites.applyMovie(updated)
                                }
                            },
                            onDeleteReview = {
                                film.deleteReview { updated, rating ->
                                    main.refreshMovie(updated, rating)
                                    favorites.applyMovie(updated)
                                }
                            },
                            allReviews = movie.reviews,
                            currentUserId = film.userId,
                            loading = film.busy,
                            error = film.error
                        )
                    }
                } else {
                    tabState.SaveableStateProvider(selectedTab) {
                        when (selectedTab) {
                            0 -> HomeScreen(
                                movies = main.movies.map { it.toCard(main.userRatings[it.id]) },
                                loading = main.busy, error = main.error, endReached = main.endReached,
                                onLoadNext = main::loadNext,
                                onMovieClick = { selectedMovie = it.id }
                            )
                            1 -> if (!favorites.loaded && favorites.busy || favorites.error != null) {
                                RequestStatus(favorites.busy, favorites.error, favorites::load)
                            } else FavoritesScreen(
                                movies = favorites.movies.map { it.toCard() },
                                onMovieClick = { selectedMovie = it.id }
                            )
                            2 -> {
                                val value = profile.profile
                                if (value == null) RequestStatus(profile.busy, profile.error, profile::load)
                                else ProfileScreen(
                                    value = value, loading = profile.busy, error = profile.error,
                                    onSave = profile::save, onLogout = { profile.logout {} }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestStatus(loading: Boolean, error: String?, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (loading) CircularProgressIndicator()
        else {
            Text(error ?: "Не удалось загрузить данные", color = Color.White)
            TextButton(onClick = onRetry) { Text("Повторить") }
        }
    }
}
