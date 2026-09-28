package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElegantIceBlue,
    onPrimary = ElegantIceBlueOn,
    primaryContainer = ElegantDarkSurfaceHighlight,
    onPrimaryContainer = ElegantIceBlue,

    secondary = ElegantEmerald,
    onSecondary = Color(0xFF003822),
    secondaryContainer = Color(0xFF142B22),
    onSecondaryContainer = ElegantEmerald,

    tertiary = ElegantGold,
    onTertiary = Color(0xFF3E2E04),
    tertiaryContainer = Color(0xFF2D2614),
    onTertiaryContainer = ElegantGold,

    background = ElegantDarkBackground,
    onBackground = ElegantTextPrimary,
    surface = ElegantDarkSurface,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantDarkSurfaceVariant,
    onSurfaceVariant = ElegantTextSecondary,
    outline = ElegantDarkBorder,
    outlineVariant = ElegantDarkBorderSubtle,
    error = ElegantRed,
    onError = Color(0xFF680003),
    errorContainer = ElegantRedContainer,
    onErrorContainer = ElegantRed
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD97706),
    onPrimary = Color.White,
    primaryContainer = TradingGoldContainerLight,
    onPrimaryContainer = Color(0xFF78350F),

    secondary = Color(0xFF059669),
    onSecondary = Color.White,
    secondaryContainer = TradingEmeraldContainerLight,
    onSecondaryContainer = Color(0xFF064E3B),

    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2FE),
    onTertiaryContainer = Color(0xFF075985),

    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightCardBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = TradingRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep tailored fintech colors for best experience
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
