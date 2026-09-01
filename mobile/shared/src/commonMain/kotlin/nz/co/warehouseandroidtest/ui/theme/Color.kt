package nz.co.warehouseandroidtest.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val warehouseRed = Color(0xFFE31837)

val warehouseLightColorScheme = lightColorScheme(
    primary = warehouseRed,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDAD9),
    onPrimaryContainer = Color(0xFF410006),
    secondary = Color(0xFF5F5F5F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE5E5E5),
    onSecondaryContainer = Color(0xFF1A1A1A),
    tertiary = Color(0xFF555555),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE0E0E0),
    onTertiaryContainer = Color(0xFF1A1A1A),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE7E0E0),
    onSurfaceVariant = Color(0xFF494444),
    outline = Color(0xFF797474),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

val warehouseDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB3B5),
    onPrimary = Color(0xFF68000D),
    primaryContainer = Color(0xFF930018),
    onPrimaryContainer = Color(0xFFFFDAD9),
    secondary = Color(0xFFC9C6C6),
    onSecondary = Color(0xFF303030),
    secondaryContainer = Color(0xFF464646),
    onSecondaryContainer = Color(0xFFE5E5E5),
    tertiary = Color(0xFFC6C6C6),
    onTertiary = Color(0xFF303030),
    tertiaryContainer = Color(0xFF454545),
    onTertiaryContainer = Color(0xFFE0E0E0),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E1),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE6E1E1),
    surfaceVariant = Color(0xFF494444),
    onSurfaceVariant = Color(0xFFCAC4C4),
    outline = Color(0xFF938F8F),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)
