package com.example.appcimuc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcimuc.R

// Paleta confirmada por el usuario. El contenedor .make no está disponible localmente.
private val Azul = Color(0xFF1A3C5E)
private val Naranja = Color(0xFFB5621A)
private val Fondo = Color(0xFFF4F1EC)
private val Texto = Color(0xFF1A1A1A)
private val TextoSecundario = Color(0xFF7A7267)
private val Borde = Color(0xFFDDD8CE)
private val FondoSecundario = Color(0xFFEAE5DC)

// Roles explícitos para evitar superficies o acentos predeterminados ajenos al diseño.
// Los colores de error son semánticos locales, no valores atribuidos al prototipo.
private val CimColors = lightColorScheme(
    primary = Azul, onPrimary = Color.White,
    primaryContainer = FondoSecundario, onPrimaryContainer = Azul,
    inversePrimary = Color.White,
    secondary = Naranja, onSecondary = Color.White,
    secondaryContainer = FondoSecundario, onSecondaryContainer = Azul,
    tertiary = Naranja, onTertiary = Color.White,
    tertiaryContainer = FondoSecundario, onTertiaryContainer = Texto,
    background = Fondo, onBackground = Texto,
    surface = Color.White, onSurface = Texto,
    surfaceVariant = FondoSecundario, onSurfaceVariant = TextoSecundario,
    surfaceTint = Azul,
    inverseSurface = Azul, inverseOnSurface = Color.White,
    error = Color(0xFFBA1A1A), onError = Color.White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002),
    outline = Borde, outlineVariant = Borde, scrim = Color.Black,
    surfaceBright = Color.White, surfaceDim = FondoSecundario,
    surfaceContainer = Fondo, surfaceContainerLow = Fondo,
    surfaceContainerLowest = Color.White,
    surfaceContainerHigh = FondoSecundario, surfaceContainerHighest = FondoSecundario
)

// Fuentes estáticas locales de sus autores; licencias SIL OFL en assets/licenses.
// Tamaños conservados de Android: no se atribuyen al .make ausente.
private val Titulos = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold)
)
private val Cuerpo = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium)
)
private val BaseTypography = Typography()
private val CimTypography = Typography(
    displayLarge = BaseTypography.displayLarge.copy(fontFamily = Titulos),
    displayMedium = BaseTypography.displayMedium.copy(fontFamily = Titulos),
    displaySmall = BaseTypography.displaySmall.copy(fontFamily = Titulos),
    headlineLarge = BaseTypography.headlineLarge.copy(fontFamily = Titulos),
    headlineSmall = BaseTypography.headlineSmall.copy(fontFamily = Titulos),
    headlineMedium = TextStyle(fontFamily = Titulos, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 36.sp),
    titleLarge = TextStyle(fontFamily = Titulos, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontFamily = Titulos, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 26.sp),
    titleSmall = BaseTypography.titleSmall.copy(fontFamily = Titulos),
    bodyLarge = TextStyle(fontFamily = Cuerpo, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = BaseTypography.bodyMedium.copy(fontFamily = Cuerpo),
    bodySmall = TextStyle(fontFamily = Cuerpo, fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = Titulos, fontWeight = FontWeight.Bold,
        fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = BaseTypography.labelMedium.copy(fontFamily = Cuerpo),
    labelSmall = BaseTypography.labelSmall.copy(fontFamily = Cuerpo)
)

private val CimShapes = Shapes(
    small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun AppNombreGrupoXTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CimColors, typography = CimTypography, shapes = CimShapes, content = content)
}

// Estilos de las hojas de participación, integrados al tema existente.
// Valores confirmados en el mensaje; pendientes de contraste con el exportado ausente.
internal object ParticipacionEstilo {
    val avisoPostulacionFondo = Color(0xFFFEF3E2)
    val avisoPostulacionBorde = Color(0xFFF5C78A)
    val avisoPostulacionTexto = Color(0xFF7D4E1F)
    val avisoAporteFondo = Color(0xFFEAF2FF)
    val avisoAporteBorde = Color(0xFFA8C8F0)
    val placeholder = Color(0xFFA09890)
    val formaModal = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val formaBoton = RoundedCornerShape(16.dp)
    val padding = 24.dp
}
