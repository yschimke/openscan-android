package ee.schimke.openscan.components.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// OpenScan Material 3 palette + shapes (seed indigo #3F51B5, the OpenScan brand
// blue, with a warm amber accent for capture/scan actions). Single source of
// truth, shared by the app theme and the CMP desktop parity render.

private val IndigoPrimary = Color(0xFF4355B9)
private val IndigoOnPrimary = Color(0xFFFFFFFF)
private val IndigoPrimaryContainer = Color(0xFFDDE1FF)
private val IndigoOnPrimaryContainer = Color(0xFF00105C)

private val BlueSecondary = Color(0xFF5A5D72)
private val BlueOnSecondary = Color(0xFFFFFFFF)
private val BlueSecondaryContainer = Color(0xFFDFE1F9)
private val BlueOnSecondaryContainer = Color(0xFF171A2C)

private val AmberTertiary = Color(0xFF77574E)
private val AmberOnTertiary = Color(0xFFFFFFFF)
private val AmberTertiaryContainer = Color(0xFFFFDAD1)
private val AmberOnTertiaryContainer = Color(0xFF2C150F)

private val ErrorRed = Color(0xFFBA1A1A)
private val OnErrorRed = Color(0xFFFFFFFF)
private val ErrorRedContainer = Color(0xFFFFDAD6)
private val OnErrorRedContainer = Color(0xFF410002)

private val SurfaceLight = Color(0xFFFBF8FF)
private val OnSurfaceLight = Color(0xFF1B1B1F)
private val SurfaceVariantLight = Color(0xFFE3E1EC)
private val OnSurfaceVariantLight = Color(0xFF46464F)
private val OutlineLight = Color(0xFF767680)
private val OutlineVariantLight = Color(0xFFC7C5D0)
private val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
private val SurfaceContainerLowLight = Color(0xFFF5F2FA)
private val SurfaceContainerLight = Color(0xFFEFEDF4)
private val SurfaceContainerHighLight = Color(0xFFE9E7EF)
private val SurfaceContainerHighestLight = Color(0xFFE3E1E9)

private val IndigoPrimaryDark = Color(0xFFB9C3FF)
private val IndigoOnPrimaryDark = Color(0xFF0B2284)
private val IndigoPrimaryContainerDark = Color(0xFF2A3B9F)
private val IndigoOnPrimaryContainerDark = Color(0xFFDDE1FF)

private val BlueSecondaryDark = Color(0xFFC3C5DD)
private val BlueOnSecondaryDark = Color(0xFF2C2F42)
private val BlueSecondaryContainerDark = Color(0xFF424659)
private val BlueOnSecondaryContainerDark = Color(0xFFDFE1F9)

private val AmberTertiaryDark = Color(0xFFE7BDB2)
private val AmberOnTertiaryDark = Color(0xFF442A23)
private val AmberTertiaryContainerDark = Color(0xFF5D4038)
private val AmberOnTertiaryContainerDark = Color(0xFFFFDAD1)

private val ErrorRedDark = Color(0xFFFFB4AB)
private val OnErrorRedDark = Color(0xFF690005)
private val ErrorRedContainerDark = Color(0xFF93000A)
private val OnErrorRedContainerDark = Color(0xFFFFDAD6)

private val SurfaceDark = Color(0xFF121316)
private val OnSurfaceDark = Color(0xFFE4E1E9)
private val SurfaceVariantDark = Color(0xFF46464F)
private val OnSurfaceVariantDark = Color(0xFFC7C5D0)
private val OutlineDark = Color(0xFF90909A)
private val OutlineVariantDark = Color(0xFF46464F)
private val SurfaceContainerLowestDark = Color(0xFF0D0E11)
private val SurfaceContainerLowDark = Color(0xFF1B1B1F)
private val SurfaceContainerDark = Color(0xFF1F1F23)
private val SurfaceContainerHighDark = Color(0xFF2A2A2D)
private val SurfaceContainerHighestDark = Color(0xFF353438)

val OpenScanLightColors =
  lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = IndigoOnPrimary,
    primaryContainer = IndigoPrimaryContainer,
    onPrimaryContainer = IndigoOnPrimaryContainer,
    secondary = BlueSecondary,
    onSecondary = BlueOnSecondary,
    secondaryContainer = BlueSecondaryContainer,
    onSecondaryContainer = BlueOnSecondaryContainer,
    tertiary = AmberTertiary,
    onTertiary = AmberOnTertiary,
    tertiaryContainer = AmberTertiaryContainer,
    onTertiaryContainer = AmberOnTertiaryContainer,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorRedContainer,
    onErrorContainer = OnErrorRedContainer,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
  )

val OpenScanDarkColors =
  darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = IndigoOnPrimaryDark,
    primaryContainer = IndigoPrimaryContainerDark,
    onPrimaryContainer = IndigoOnPrimaryContainerDark,
    secondary = BlueSecondaryDark,
    onSecondary = BlueOnSecondaryDark,
    secondaryContainer = BlueSecondaryContainerDark,
    onSecondaryContainer = BlueOnSecondaryContainerDark,
    tertiary = AmberTertiaryDark,
    onTertiary = AmberOnTertiaryDark,
    tertiaryContainer = AmberTertiaryContainerDark,
    onTertiaryContainer = AmberOnTertiaryContainerDark,
    error = ErrorRedDark,
    onError = OnErrorRedDark,
    errorContainer = ErrorRedContainerDark,
    onErrorContainer = OnErrorRedContainerDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
  )

val OpenScanShapes =
  Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
  )
