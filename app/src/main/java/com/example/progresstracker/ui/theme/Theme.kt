package com.example.progresstracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.platform.LocalContext

// ─────────────────────────────────────────────────────────────────────────────
// App Theme Enum  (stored in DataStore as ordinal Int)
// ─────────────────────────────────────────────────────────────────────────────

enum class AppThemeMode { SYSTEM, LIGHT, DARK }

// ─────────────────────────────────────────────────────────────────────────────
// Color Scheme Enum
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
enum class AppColorScheme(
    val displayName: String,
    val previewColor: Color   // shown as a dot/circle in the picker UI
) {
    TERRACOTTA("Terracotta", Color(0xFF8F4C38)),
    OCEAN("Ocean",           Color(0xFF1B6091)),
    FOREST("Forest",         Color(0xFF3A6B35)),
    VIOLET("Violet",         Color(0xFF6750A4)),
    SLATE("Slate",           Color(0xFF3D5F6F)),
}

// ─────────────────────────────────────────────────────────────────────────────
// Color Scheme Builders
// ─────────────────────────────────────────────────────────────────────────────

private fun terracottaLight() = lightColorScheme(
    primary = terracotta_primary_light,
    onPrimary = terracotta_onPrimary_light,
    primaryContainer = terracotta_primaryContainer_light,
    onPrimaryContainer = terracotta_onPrimaryContainer_light,
    secondary = terracotta_secondary_light,
    onSecondary = terracotta_onSecondary_light,
    secondaryContainer = terracotta_secondaryContainer_light,
    onSecondaryContainer = terracotta_onSecondaryContainer_light,
    tertiary = terracotta_tertiary_light,
    onTertiary = terracotta_onTertiary_light,
    tertiaryContainer = terracotta_tertiaryContainer_light,
    onTertiaryContainer = terracotta_onTertiaryContainer_light,
    error = terracotta_error_light,
    onError = terracotta_onError_light,
    errorContainer = terracotta_errorContainer_light,
    onErrorContainer = terracotta_onErrorContainer_light,
    background = terracotta_background_light,
    onBackground = terracotta_onBackground_light,
    surface = terracotta_surface_light,
    onSurface = terracotta_onSurface_light,
    surfaceVariant = terracotta_surfaceVariant_light,
    onSurfaceVariant = terracotta_onSurfaceVariant_light,
    outline = terracotta_outline_light,
    outlineVariant = terracotta_outlineVariant_light,
    inverseSurface = terracotta_inverseSurface_light,
    inverseOnSurface = terracotta_inverseOnSurface_light,
    inversePrimary = terracotta_inversePrimary_light,
    surfaceContainerLowest = terracotta_surfaceContainerLowest_light,
    surfaceContainerLow = terracotta_surfaceContainerLow_light,
    surfaceContainer = terracotta_surfaceContainer_light,
    surfaceContainerHigh = terracotta_surfaceContainerHigh_light,
    surfaceContainerHighest = terracotta_surfaceContainerHighest_light,
)

private fun terracottaDark() = darkColorScheme(
    primary = terracotta_primary_dark,
    onPrimary = terracotta_onPrimary_dark,
    primaryContainer = terracotta_primaryContainer_dark,
    onPrimaryContainer = terracotta_onPrimaryContainer_dark,
    secondary = terracotta_secondary_dark,
    onSecondary = terracotta_onSecondary_dark,
    secondaryContainer = terracotta_secondaryContainer_dark,
    onSecondaryContainer = terracotta_onSecondaryContainer_dark,
    tertiary = terracotta_tertiary_dark,
    onTertiary = terracotta_onTertiary_dark,
    tertiaryContainer = terracotta_tertiaryContainer_dark,
    onTertiaryContainer = terracotta_onTertiaryContainer_dark,
    error = terracotta_error_dark,
    onError = terracotta_onError_dark,
    errorContainer = terracotta_errorContainer_dark,
    onErrorContainer = terracotta_onErrorContainer_dark,
    background = terracotta_background_dark,
    onBackground = terracotta_onBackground_dark,
    surface = terracotta_surface_dark,
    onSurface = terracotta_onSurface_dark,
    surfaceVariant = terracotta_surfaceVariant_dark,
    onSurfaceVariant = terracotta_onSurfaceVariant_dark,
    outline = terracotta_outline_dark,
    outlineVariant = terracotta_outlineVariant_dark,
    inverseSurface = terracotta_inverseSurface_dark,
    inverseOnSurface = terracotta_inverseOnSurface_dark,
    inversePrimary = terracotta_inversePrimary_dark,
    surfaceContainerLowest = terracotta_surfaceContainerLowest_dark,
    surfaceContainerLow = terracotta_surfaceContainerLow_dark,
    surfaceContainer = terracotta_surfaceContainer_dark,
    surfaceContainerHigh = terracotta_surfaceContainerHigh_dark,
    surfaceContainerHighest = terracotta_surfaceContainerHighest_dark,
)

private fun oceanLight() = lightColorScheme(
    primary = ocean_primary_light,
    onPrimary = ocean_onPrimary_light,
    primaryContainer = ocean_primaryContainer_light,
    onPrimaryContainer = ocean_onPrimaryContainer_light,
    secondary = ocean_secondary_light,
    onSecondary = ocean_onSecondary_light,
    secondaryContainer = ocean_secondaryContainer_light,
    onSecondaryContainer = ocean_onSecondaryContainer_light,
    tertiary = ocean_tertiary_light,
    onTertiary = ocean_onTertiary_light,
    tertiaryContainer = ocean_tertiaryContainer_light,
    onTertiaryContainer = ocean_onTertiaryContainer_light,
    error = ocean_error_light,
    onError = ocean_onError_light,
    errorContainer = ocean_errorContainer_light,
    onErrorContainer = ocean_onErrorContainer_light,
    background = ocean_background_light,
    onBackground = ocean_onBackground_light,
    surface = ocean_surface_light,
    onSurface = ocean_onSurface_light,
    surfaceVariant = ocean_surfaceVariant_light,
    onSurfaceVariant = ocean_onSurfaceVariant_light,
    outline = ocean_outline_light,
    outlineVariant = ocean_outlineVariant_light,
    inverseSurface = ocean_inverseSurface_light,
    inverseOnSurface = ocean_inverseOnSurface_light,
    inversePrimary = ocean_inversePrimary_light,
    surfaceContainerLowest = ocean_surfaceContainerLowest_light,
    surfaceContainerLow = ocean_surfaceContainerLow_light,
    surfaceContainer = ocean_surfaceContainer_light,
    surfaceContainerHigh = ocean_surfaceContainerHigh_light,
    surfaceContainerHighest = ocean_surfaceContainerHighest_light,
)

private fun oceanDark() = darkColorScheme(
    primary = ocean_primary_dark,
    onPrimary = ocean_onPrimary_dark,
    primaryContainer = ocean_primaryContainer_dark,
    onPrimaryContainer = ocean_onPrimaryContainer_dark,
    secondary = ocean_secondary_dark,
    onSecondary = ocean_onSecondary_dark,
    secondaryContainer = ocean_secondaryContainer_dark,
    onSecondaryContainer = ocean_onSecondaryContainer_dark,
    tertiary = ocean_tertiary_dark,
    onTertiary = ocean_onTertiary_dark,
    tertiaryContainer = ocean_tertiaryContainer_dark,
    onTertiaryContainer = ocean_onTertiaryContainer_dark,
    error = ocean_error_dark,
    onError = ocean_onError_dark,
    errorContainer = ocean_errorContainer_dark,
    onErrorContainer = ocean_onErrorContainer_dark,
    background = ocean_background_dark,
    onBackground = ocean_onBackground_dark,
    surface = ocean_surface_dark,
    onSurface = ocean_onSurface_dark,
    surfaceVariant = ocean_surfaceVariant_dark,
    onSurfaceVariant = ocean_onSurfaceVariant_dark,
    outline = ocean_outline_dark,
    outlineVariant = ocean_outlineVariant_dark,
    inverseSurface = ocean_inverseSurface_dark,
    inverseOnSurface = ocean_inverseOnSurface_dark,
    inversePrimary = ocean_inversePrimary_dark,
    surfaceContainerLowest = ocean_surfaceContainerLowest_dark,
    surfaceContainerLow = ocean_surfaceContainerLow_dark,
    surfaceContainer = ocean_surfaceContainer_dark,
    surfaceContainerHigh = ocean_surfaceContainerHigh_dark,
    surfaceContainerHighest = ocean_surfaceContainerHighest_dark,
)

private fun forestLight() = lightColorScheme(
    primary = forest_primary_light,
    onPrimary = forest_onPrimary_light,
    primaryContainer = forest_primaryContainer_light,
    onPrimaryContainer = forest_onPrimaryContainer_light,
    secondary = forest_secondary_light,
    onSecondary = forest_onSecondary_light,
    secondaryContainer = forest_secondaryContainer_light,
    onSecondaryContainer = forest_onSecondaryContainer_light,
    tertiary = forest_tertiary_light,
    onTertiary = forest_onTertiary_light,
    tertiaryContainer = forest_tertiaryContainer_light,
    onTertiaryContainer = forest_onTertiaryContainer_light,
    error = forest_error_light,
    onError = forest_onError_light,
    errorContainer = forest_errorContainer_light,
    onErrorContainer = forest_onErrorContainer_light,
    background = forest_background_light,
    onBackground = forest_onBackground_light,
    surface = forest_surface_light,
    onSurface = forest_onSurface_light,
    surfaceVariant = forest_surfaceVariant_light,
    onSurfaceVariant = forest_onSurfaceVariant_light,
    outline = forest_outline_light,
    outlineVariant = forest_outlineVariant_light,
    inverseSurface = forest_inverseSurface_light,
    inverseOnSurface = forest_inverseOnSurface_light,
    inversePrimary = forest_inversePrimary_light,
    surfaceContainerLowest = forest_surfaceContainerLowest_light,
    surfaceContainerLow = forest_surfaceContainerLow_light,
    surfaceContainer = forest_surfaceContainer_light,
    surfaceContainerHigh = forest_surfaceContainerHigh_light,
    surfaceContainerHighest = forest_surfaceContainerHighest_light,
)

private fun forestDark() = darkColorScheme(
    primary = forest_primary_dark,
    onPrimary = forest_onPrimary_dark,
    primaryContainer = forest_primaryContainer_dark,
    onPrimaryContainer = forest_onPrimaryContainer_dark,
    secondary = forest_secondary_dark,
    onSecondary = forest_onSecondary_dark,
    secondaryContainer = forest_secondaryContainer_dark,
    onSecondaryContainer = forest_onSecondaryContainer_dark,
    tertiary = forest_tertiary_dark,
    onTertiary = forest_onTertiary_dark,
    tertiaryContainer = forest_tertiaryContainer_dark,
    onTertiaryContainer = forest_onTertiaryContainer_dark,
    error = forest_error_dark,
    onError = forest_onError_dark,
    errorContainer = forest_errorContainer_dark,
    onErrorContainer = forest_onErrorContainer_dark,
    background = forest_background_dark,
    onBackground = forest_onBackground_dark,
    surface = forest_surface_dark,
    onSurface = forest_onSurface_dark,
    surfaceVariant = forest_surfaceVariant_dark,
    onSurfaceVariant = forest_onSurfaceVariant_dark,
    outline = forest_outline_dark,
    outlineVariant = forest_outlineVariant_dark,
    inverseSurface = forest_inverseSurface_dark,
    inverseOnSurface = forest_inverseOnSurface_dark,
    inversePrimary = forest_inversePrimary_dark,
    surfaceContainerLowest = forest_surfaceContainerLowest_dark,
    surfaceContainerLow = forest_surfaceContainerLow_dark,
    surfaceContainer = forest_surfaceContainer_dark,
    surfaceContainerHigh = forest_surfaceContainerHigh_dark,
    surfaceContainerHighest = forest_surfaceContainerHighest_dark,
)

private fun violetLight() = lightColorScheme(
    primary = violet_primary_light,
    onPrimary = violet_onPrimary_light,
    primaryContainer = violet_primaryContainer_light,
    onPrimaryContainer = violet_onPrimaryContainer_light,
    secondary = violet_secondary_light,
    onSecondary = violet_onSecondary_light,
    secondaryContainer = violet_secondaryContainer_light,
    onSecondaryContainer = violet_onSecondaryContainer_light,
    tertiary = violet_tertiary_light,
    onTertiary = violet_onTertiary_light,
    tertiaryContainer = violet_tertiaryContainer_light,
    onTertiaryContainer = violet_onTertiaryContainer_light,
    error = violet_error_light,
    onError = violet_onError_light,
    errorContainer = violet_errorContainer_light,
    onErrorContainer = violet_onErrorContainer_light,
    background = violet_background_light,
    onBackground = violet_onBackground_light,
    surface = violet_surface_light,
    onSurface = violet_onSurface_light,
    surfaceVariant = violet_surfaceVariant_light,
    onSurfaceVariant = violet_onSurfaceVariant_light,
    outline = violet_outline_light,
    outlineVariant = violet_outlineVariant_light,
    inverseSurface = violet_inverseSurface_light,
    inverseOnSurface = violet_inverseOnSurface_light,
    inversePrimary = violet_inversePrimary_light,
    surfaceContainerLowest = violet_surfaceContainerLowest_light,
    surfaceContainerLow = violet_surfaceContainerLow_light,
    surfaceContainer = violet_surfaceContainer_light,
    surfaceContainerHigh = violet_surfaceContainerHigh_light,
    surfaceContainerHighest = violet_surfaceContainerHighest_light,
)

private fun violetDark() = darkColorScheme(
    primary = violet_primary_dark,
    onPrimary = violet_onPrimary_dark,
    primaryContainer = violet_primaryContainer_dark,
    onPrimaryContainer = violet_onPrimaryContainer_dark,
    secondary = violet_secondary_dark,
    onSecondary = violet_onSecondary_dark,
    secondaryContainer = violet_secondaryContainer_dark,
    onSecondaryContainer = violet_onSecondaryContainer_dark,
    tertiary = violet_tertiary_dark,
    onTertiary = violet_onTertiary_dark,
    tertiaryContainer = violet_tertiaryContainer_dark,
    onTertiaryContainer = violet_onTertiaryContainer_dark,
    error = violet_error_dark,
    onError = violet_onError_dark,
    errorContainer = violet_errorContainer_dark,
    onErrorContainer = violet_onErrorContainer_dark,
    background = violet_background_dark,
    onBackground = violet_onBackground_dark,
    surface = violet_surface_dark,
    onSurface = violet_onSurface_dark,
    surfaceVariant = violet_surfaceVariant_dark,
    onSurfaceVariant = violet_onSurfaceVariant_dark,
    outline = violet_outline_dark,
    outlineVariant = violet_outlineVariant_dark,
    inverseSurface = violet_inverseSurface_dark,
    inverseOnSurface = violet_inverseOnSurface_dark,
    inversePrimary = violet_inversePrimary_dark,
    surfaceContainerLowest = violet_surfaceContainerLowest_dark,
    surfaceContainerLow = violet_surfaceContainerLow_dark,
    surfaceContainer = violet_surfaceContainer_dark,
    surfaceContainerHigh = violet_surfaceContainerHigh_dark,
    surfaceContainerHighest = violet_surfaceContainerHighest_dark,
)

private fun slateLight() = lightColorScheme(
    primary = slate_primary_light,
    onPrimary = slate_onPrimary_light,
    primaryContainer = slate_primaryContainer_light,
    onPrimaryContainer = slate_onPrimaryContainer_light,
    secondary = slate_secondary_light,
    onSecondary = slate_onSecondary_light,
    secondaryContainer = slate_secondaryContainer_light,
    onSecondaryContainer = slate_onSecondaryContainer_light,
    tertiary = slate_tertiary_light,
    onTertiary = slate_onTertiary_light,
    tertiaryContainer = slate_tertiaryContainer_light,
    onTertiaryContainer = slate_onTertiaryContainer_light,
    error = slate_error_light,
    onError = slate_onError_light,
    errorContainer = slate_errorContainer_light,
    onErrorContainer = slate_onErrorContainer_light,
    background = slate_background_light,
    onBackground = slate_onBackground_light,
    surface = slate_surface_light,
    onSurface = slate_onSurface_light,
    surfaceVariant = slate_surfaceVariant_light,
    onSurfaceVariant = slate_onSurfaceVariant_light,
    outline = slate_outline_light,
    outlineVariant = slate_outlineVariant_light,
    inverseSurface = slate_inverseSurface_light,
    inverseOnSurface = slate_inverseOnSurface_light,
    inversePrimary = slate_inversePrimary_light,
    surfaceContainerLowest = slate_surfaceContainerLowest_light,
    surfaceContainerLow = slate_surfaceContainerLow_light,
    surfaceContainer = slate_surfaceContainer_light,
    surfaceContainerHigh = slate_surfaceContainerHigh_light,
    surfaceContainerHighest = slate_surfaceContainerHighest_light,
)

private fun slateDark() = darkColorScheme(
    primary = slate_primary_dark,
    onPrimary = slate_onPrimary_dark,
    primaryContainer = slate_primaryContainer_dark,
    onPrimaryContainer = slate_onPrimaryContainer_dark,
    secondary = slate_secondary_dark,
    onSecondary = slate_onSecondary_dark,
    secondaryContainer = slate_secondaryContainer_dark,
    onSecondaryContainer = slate_onSecondaryContainer_dark,
    tertiary = slate_tertiary_dark,
    onTertiary = slate_onTertiary_dark,
    tertiaryContainer = slate_tertiaryContainer_dark,
    onTertiaryContainer = slate_onTertiaryContainer_dark,
    error = slate_error_dark,
    onError = slate_onError_dark,
    errorContainer = slate_errorContainer_dark,
    onErrorContainer = slate_onErrorContainer_dark,
    background = slate_background_dark,
    onBackground = slate_onBackground_dark,
    surface = slate_surface_dark,
    onSurface = slate_onSurface_dark,
    surfaceVariant = slate_surfaceVariant_dark,
    onSurfaceVariant = slate_onSurfaceVariant_dark,
    outline = slate_outline_dark,
    outlineVariant = slate_outlineVariant_dark,
    inverseSurface = slate_inverseSurface_dark,
    inverseOnSurface = slate_inverseOnSurface_dark,
    inversePrimary = slate_inversePrimary_dark,
    surfaceContainerLowest = slate_surfaceContainerLowest_dark,
    surfaceContainerLow = slate_surfaceContainerLow_dark,
    surfaceContainer = slate_surfaceContainer_dark,
    surfaceContainerHigh = slate_surfaceContainerHigh_dark,
    surfaceContainerHighest = slate_surfaceContainerHighest_dark,
)

// ─────────────────────────────────────────────────────────────────────────────
// AppTheme composable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AppTheme(
    colorScheme: AppColorScheme = AppColorScheme.TERRACOTTA,
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }



    val materialColorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> when (colorScheme) {
            AppColorScheme.TERRACOTTA -> if (isDark) terracottaDark() else terracottaLight()
            AppColorScheme.OCEAN      -> if (isDark) oceanDark()      else oceanLight()
            AppColorScheme.FOREST     -> if (isDark) forestDark()     else forestLight()
            AppColorScheme.VIOLET     -> if (isDark) violetDark()     else violetLight()
            AppColorScheme.SLATE      -> if (isDark) slateDark()      else slateLight()
        }
    }

    MaterialTheme(
        colorScheme = materialColorScheme,
        typography = Typography,
        content = content
    )
}
