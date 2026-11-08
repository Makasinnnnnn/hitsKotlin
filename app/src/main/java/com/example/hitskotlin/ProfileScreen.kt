package com.example.hitskotlin

import com.example.hitskotlin.ui.model.*
import com.example.hitskotlin.ui.mapper.*

import android.app.DatePickerDialog
import com.example.hitskotlin.domain.Validation
import com.example.hitskotlin.ui.RemoteImage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen(
    value: ProfileUi = ProfileUi("preview", "Ivan ivan ivan", "ivan@gmail.com", "https://example.com/avatar.png", "Иван", "2002-01-01T00:00:00", 0),
    loading: Boolean = false,
    error: String? = null,
    onSave: (ProfileUi) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val pink = colorResource(R.color.accent_pink)
    val format = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val saved = listOf(value.email, value.avatarLink, value.name,
        if (value.gender == 1) "Женщина" else "Мужчина",
        runCatching { LocalDate.parse(value.birthDate.take(10)).format(format) }.getOrDefault(""))
    val revision = value.toString()
    var email by rememberSaveable(revision) { mutableStateOf(saved[0]) }
    var avatarLink by rememberSaveable(revision) { mutableStateOf(saved[1]) }
    var name by rememberSaveable(revision) { mutableStateOf(saved[2]) }
    var gender by rememberSaveable(revision) { mutableStateOf(saved[3]) }
    var birthday by rememberSaveable(revision) { mutableStateOf(saved[4]) }
    val date = runCatching { LocalDate.parse(birthday, format) }.getOrNull()
    val draft = value.copy(email = email.trim(), avatarLink = avatarLink.trim(), name = name.trim(),
        gender = if (gender == "Женщина") 1 else 0,
        birthDate = date?.atStartOfDay()?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).orEmpty())
    val changed = listOf(email, avatarLink, name, gender, birthday) != saved
    val valid = Validation.profile(draft.name, draft.email, draft.avatarLink, draft.birthDate)

    Column(
        modifier = Modifier.fillMaxSize()
            .background(colorResource(R.color.screen_background))
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RemoteImage(
            address = value.avatarLink,
            description = "Аватар профиля",
            modifier = Modifier.size(88.dp).clip(CircleShape)
        )
        Text(
            text = value.nickName.ifBlank { value.name },
            modifier = Modifier.padding(top = 8.dp),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onLogout, enabled = !loading) {
            Text("Выйти из аккаунта", color = pink)
        }

        Spacer(Modifier.height(12.dp))
        ProfileField("Электронная почта", email, { email = it }, KeyboardType.Email, !Validation.email(email))
        ProfileField("Ссылка на аватар", avatarLink, { avatarLink = it }, KeyboardType.Uri, !Validation.link(avatarLink))
        ProfileField("Имя", name, { name = it }, invalid = name.isBlank())

        Text("Пол", color = Color.White, modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF292929))
        ) {
            listOf("Мужчина", "Женщина").forEach { option ->
                val selected = gender == option
                Box(
                    modifier = Modifier.weight(1f).height(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selected) Color.White else Color.Transparent)
                        .clickable { gender = option },
                    contentAlignment = Alignment.Center
                ) {
                    Text(option, color = if (selected) Color.Black else colorResource(R.color.hint_gray))
                }
            }
        }

        Text("Дата рождения", color = Color.White, modifier = Modifier.fillMaxWidth())
        OutlinedButton(
            onClick = {
                val date = date ?: LocalDate.now()
                DatePickerDialog(context, { _, year, month, day ->
                    birthday = LocalDate.of(year, month + 1, day).format(format)
                }, date.year, date.monthValue - 1, date.dayOfMonth).apply {
                    datePicker.maxDate = System.currentTimeMillis()
                }.show()
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(48.dp),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Text(birthday, modifier = Modifier.weight(1f), color = Color.White)
            Icon(
                painterResource(R.drawable.ic_calendar),
                contentDescription = "Выбрать дату",
                tint = colorResource(R.color.hint_gray),
                modifier = Modifier.size(20.dp)
            )
        }

        Button(
            onClick = { onSave(draft) },
            enabled = changed && valid && !loading,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp).height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = pink,
                contentColor = Color.White,
                disabledContainerColor = pink.copy(alpha = 0.35f),
                disabledContentColor = Color.White.copy(alpha = 0.4f)
            )
        ) { Text(if (loading) "Сохраняем…" else "Сохранить") }
        if (error != null) Text(error, color = Color(0xFFFF7070), modifier = Modifier.padding(top = 8.dp))

        Button(
            onClick = {
                email = saved[0]
                avatarLink = saved[1]
                name = saved[2]
                gender = saved[3]
                birthday = saved[4]
            },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF292929), contentColor = pink)
        ) { Text("Отмена") }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    invalid: Boolean = false
) {
    Text(label, color = Color.White, modifier = Modifier.fillMaxWidth())
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, color = Color.White),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        cursorBrush = SolidColor(colorResource(R.color.accent_pink)),
        decorationBox = { input ->
            Column {
                Box(
                    Modifier.fillMaxWidth().height(48.dp)
                        .border(1.dp, if (invalid) MaterialTheme.colorScheme.error else colorResource(R.color.input_border), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) { input() }
                if (invalid) Text(
                    "Проверьте поле «$label»",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    )
}

@Preview(widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    FilmusTheme { ProfileScreen() }
}
