package com.tictactoemaster.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** All colours used by the app, defined once per theme. Text colours keep at least WCAG AA contrast. */
@Immutable
data class AppColors(
    val isDark: Boolean,
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val card: Color,
    val cardBorder: Color,
    val dialog: Color,
    val cell: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentStart: Color,
    val accentEnd: Color,
    val accentText: Color,
    val xColor: Color,
    val oColor: Color,
    val win: Color
)

private val DarkColors = AppColors(
    isDark = true,
    backgroundTop = Color(0xFF0A0F2C),     // dark navy
    backgroundBottom = Color(0xFF2A1670),  // deep purple
    card = Color(0x1AFFFFFF),
    cardBorder = Color(0x33FFFFFF),
    dialog = Color(0xFF1B1D52),
    cell = Color(0x1FFFFFFF),
    textPrimary = Color(0xFFF4F5FF),
    textSecondary = Color(0xFFB9BEE6),
    accentStart = Color(0xFF6D3FE0),       // purple
    accentEnd = Color(0xFF2563EB),         // blue
    accentText = Color(0xFFB39DFF),
    xColor = Color(0xFF4DB5FF),
    oColor = Color(0xFFFF6FB5),
    win = Color(0xFFFFD166)
)

private val LightColors = AppColors(
    isDark = false,
    backgroundTop = Color(0xFFF5F3FF),
    backgroundBottom = Color(0xFFDCE6FF),
    card = Color(0xB3FFFFFF),
    cardBorder = Color(0x331B1F6B),
    dialog = Color(0xFFFFFFFF),
    cell = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF14163A),
    textSecondary = Color(0xFF454A7A),
    accentStart = Color(0xFF6D3FE0),
    accentEnd = Color(0xFF2563EB),
    accentText = Color(0xFF5B34D6),
    xColor = Color(0xFF1D63D8),
    oColor = Color(0xFFC2185B),
    win = Color(0xFFF59E0B)
)

val LocalAppColors = staticCompositionLocalOf { DarkColors }

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

@Composable
fun TicTacToeTheme(dark: Boolean, content: @Composable () -> Unit) {
    val colors = if (dark) DarkColors else LightColors
    val scheme = if (dark) {
        darkColorScheme(
            primary = colors.accentStart,
            background = colors.backgroundTop,
            surface = colors.dialog,
            onSurface = colors.textPrimary,
            onBackground = colors.textPrimary
        )
    } else {
        lightColorScheme(
            primary = colors.accentStart,
            background = colors.backgroundTop,
            surface = colors.dialog,
            onSurface = colors.textPrimary,
            onBackground = colors.textPrimary
        )
    }
    MaterialTheme(colorScheme = scheme) {
        CompositionLocalProvider(
            LocalAppColors provides colors,
            LocalContentColor provides colors.textPrimary,
            content = content
        )
    }
}
