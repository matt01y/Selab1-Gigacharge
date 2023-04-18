package be.ugent.gigacharge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Blue400,
    primaryVariant = Blue700,

    secondary = Color.White,
    secondaryVariant = Color.White,
    onSecondary = Color.White,

    surface = LightGray
)

private val LightColorPalette = lightColors(
    primary = Blue700,
    primaryVariant = Blue900,

    secondary = Color.White,/*Amber400,*/
    secondaryVariant = Color.White,/*Amber700,*/
    onSecondary = Color.White,

    surface = LightGray

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