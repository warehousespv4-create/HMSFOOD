package com.example.foodku.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Orange = Color(0xFFEE4D2D)
val OrangeLight = Color(0xFFFF7337)
val OrangeSoft = Color(0xFFFFF0EB)
val Surface = Color(0xFFF5F5F5)
val TextGray = Color(0xFF757575)
val StarYellow = Color(0xFFFFB400)

private val Colors = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    secondary = OrangeLight,
    background = Surface,
    surface = Color.White,
    primaryContainer = OrangeSoft,
    onPrimaryContainer = Orange
)

@Composable
fun FoodKuTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, content = content)
}
