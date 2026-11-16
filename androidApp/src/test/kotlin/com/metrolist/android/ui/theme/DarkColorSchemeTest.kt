package com.metrolist.android.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DarkColorSchemeTest {
    @Test
    fun applicationSurfacesStayOpaqueBlack() {
        with(DarkColorScheme) {
            listOf(
                background,
                surface,
                surfaceDim,
                surfaceBright,
                surfaceContainerLowest,
                surfaceContainerLow,
                surfaceContainer,
                surfaceContainerHigh,
                surfaceContainerHighest,
            ).forEach { assertEquals(Color.Black, it) }
        }
    }

    @Test
    fun neutralControlsAndTextRemainLegible() {
        with(DarkColorScheme) {
            val pairs =
                listOf(
                    background to onBackground,
                    surface to onSurface,
                    surface to onSurfaceVariant,
                    primary to onPrimary,
                    primaryContainer to onPrimaryContainer,
                    secondary to onSecondary,
                    secondaryContainer to onSecondaryContainer,
                    tertiary to onTertiary,
                    tertiaryContainer to onTertiaryContainer,
                    error to onError,
                    errorContainer to onErrorContainer,
                    inverseSurface to inverseOnSurface,
                    primaryFixedDim to onPrimaryFixedVariant,
                    secondaryFixedDim to onSecondaryFixedVariant,
                    tertiaryFixedDim to onTertiaryFixedVariant,
                )
            pairs.forEach { (background, foreground) ->
                listOf(background, foreground).forEach { color ->
                    assertEquals(color.red, color.green, 0.001f)
                    assertEquals(color.green, color.blue, 0.001f)
                }
                val first = background.luminance()
                val second = foreground.luminance()
                val contrast = (maxOf(first, second) + 0.05f) / (minOf(first, second) + 0.05f)
                assertTrue("Text contrast must meet WCAG AA: $contrast", contrast >= 4.5f)
            }
        }
    }
}
