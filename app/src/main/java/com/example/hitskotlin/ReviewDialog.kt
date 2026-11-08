package com.example.hitskotlin

import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.ui.mapper.*

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReviewDialog(
    initialReview: MovieReview?,
    onDismiss: () -> Unit,
    onSave: (MovieReview) -> Unit,
    loading: Boolean = false,
    error: String? = null
) {
    var text by rememberSaveable { mutableStateOf(initialReview?.text ?: "") }
    var rating by rememberSaveable { mutableIntStateOf(initialReview?.rating ?: 0) }
    var anonymous by rememberSaveable { mutableStateOf(initialReview?.anonymous ?: false) }
    val pink = colorResource(R.color.accent_pink)

    Dialog(onDismissRequest = { if (!loading) onDismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
                .imePadding()
                .background(colorResource(R.color.screen_background), RoundedCornerShape(5.dp))
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text("Оставить отзыв", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                (1..10).forEach { value ->
                    Box(
                        modifier = Modifier.weight(1f).height(24.dp)
                            .semantics { contentDescription = "Оценка $value из 10" }
                            .clickable { rating = value },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (value <= rating) "★" else "☆",
                            color = if (value <= rating) Color(0xFFFFD54F) else Color(0xFF909499),
                            fontSize = 25.sp
                        )
                    }
                }
            }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().height(98.dp)
                    .border(1.dp, colorResource(R.color.input_border), RoundedCornerShape(2.dp))
                    .padding(10.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 15.sp),
                cursorBrush = SolidColor(pink),
                decorationBox = { input ->
                    Box {
                        if (text.isEmpty()) Text("Напишите отзыв", color = colorResource(R.color.hint_gray))
                        input()
                    }
                }
            )
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { anonymous = !anonymous },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        modifier = Modifier.size(18.dp)
                            .background(if (anonymous) pink else Color.White, RoundedCornerShape(2.dp)),
                        checked = anonymous,
                        onCheckedChange = { anonymous = it },
                        colors = CheckboxDefaults.colors(checkedColor = pink, uncheckedColor = Color.White)
                    )
                    Text("Анонимный отзыв", color = Color.White)
                }
            }
            Button(
                onClick = {
                    val updated = MovieReview(text.trim(), rating, anonymous)
                    onSave(if (initialReview == null) updated else updated.copy(date = initialReview.date))
                },
                enabled = !loading && text.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = pink,
                    contentColor = Color.White,
                    disabledContainerColor = pink.copy(alpha = 0.35f),
                    disabledContentColor = Color.White.copy(alpha = 0.4f)
                )
            ) { Text(if (loading) "Сохраняем…" else "Сохранить") }
            if (error != null) Text(error, color = Color(0xFFFF7070))
            Button(
                onClick = onDismiss,
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF292929), contentColor = pink)
            ) { Text("Отмена") }
        }
    }
}
