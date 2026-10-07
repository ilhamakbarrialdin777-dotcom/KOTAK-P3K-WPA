package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = SafetyGreenDarkPrimary,
    onPrimary = SafetyGreenDarkOnPrimary,
    primaryContainer = SafetyGreenDarkContainer,
    onPrimaryContainer = SafetyGreenDarkOnContainer,
    secondary = SafetyGreenDarkPrimary,
    background = SafetyDarkBackground,
    surface = SafetyDarkSurface,
    surfaceVariant = SafetyDarkSurfaceVariant
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SafetyGreenPrimary,
    onPrimary = SafetyGreenOnPrimary,
    primaryContainer = SafetyGreenContainer,
    onPrimaryContainer = SafetyGreenOnContainer,
    secondary = SafetySecondary,
    secondaryContainer = SafetySecondaryContainer,
    onSecondaryContainer = SafetyOnSecondaryContainer,
    tertiary = SafetyTealTertiary,
    tertiaryContainer = SafetyTealContainer,
    onTertiaryContainer = SafetyOnTertiaryContainer,
    background = SafetyBackground,
    surface = SafetySurface,
    surfaceVariant = SafetySurfaceVariant,
    outline = SafetyOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our branded HSE Safety Green by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
