package be.ugent.gigacharge.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = Blue400,
    primaryVariant = Blue700,

    secondary = Amber400,
    secondaryVariant = Amber700,
    onSecondary = Color.White,

    surface = LightGray,

    background = LightGray,

    onBackground = Color.White,

    onSurface = Color.Black
)

private val LightColorPalette = lightColors(
    primary = Blue700,
    primaryVariant = Blue900,

    secondary = Amber400,
    secondaryVariant = Amber700,
    onSecondary = Color.White,

    surface = LightGray,

    background = Color.White,

    onBackground = Color.Black,

    onSurface = VeryLightGray

    /* Other default colors to override
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun GigaChargeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}