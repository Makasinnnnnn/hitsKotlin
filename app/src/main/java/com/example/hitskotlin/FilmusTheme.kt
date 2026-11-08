package com.example.hitskotlin

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

private fun interStyle(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = inter,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = height.sp,
    letterSpacing = 0.sp
)

private val filmusTypography = Typography(
    displayLarge = interStyle(57, 64),
    displayMedium = interStyle(45, 52),
    displaySmall = interStyle(36, 44),
    headlineLarge = interStyle(32, 38, FontWeight.Bold),
    headlineMedium = interStyle(28, 34, FontWeight.Bold),
    headlineSmall = interStyle(24, 29, FontWeight.Bold),
    titleLarge = interStyle(24, 29, FontWeight.Bold),
    titleMedium = interStyle(20, 24, FontWeight.Bold),
    titleSmall = interStyle(16, 19, FontWeight.Bold),
    bodyLarge = interStyle(15, 18),
    bodyMedium = interStyle(15, 18),
    bodySmall = interStyle(12, 15),
    labelLarge = interStyle(15, 18, FontWeight.Medium),
    labelMedium = interStyle(14, 17, FontWeight.Medium),
    labelSmall = interStyle(11, 13)
)

@Composable
fun FilmusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = colorResource(R.color.accent_pink),
            onPrimary = Color.White,
            background = colorResource(R.color.screen_background),
            surface = colorResource(R.color.screen_background),
            onSurface = Color.White,
            outline = colorResource(R.color.input_border)
        ),
        typography = filmusTypography,
        content = content
    )
}
