/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.android.ui.theme

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.metrolist.android.extensions.findActivity
import com.metrolist.android.utils.isAndroidTv
import com.metrolist.shared.ui.LocalIsTv
import com.metrolist.shared.ui.components.FocusOutlineProvider
import com.metrolist.shared.ui.haptics.MetrolistHapticsProvider
import com.metrolist.shared.ui.theme.MetrolistShapes
import com.metrolist.shared.ui.theme.ThemeColorExtractor
import com.metrolist.shared.ui.theme.pureBlack

// Keep the application chrome neutral; artwork palettes belong to the player.
internal val DarkColorScheme =
    darkColorScheme(
        primary = Color.White,
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF242424),
        onPrimaryContainer = Color.White,
        inversePrimary = Color(0xFF333333),
        secondary = Color(0xFFCCCCCC),
        onSecondary = Color.Black,
        secondaryContainer = Color(0xFF242424),
        onSecondaryContainer = Color.White,
        tertiary = Color(0xFFAAAAAA),
        onTertiary = Color.Black,
        tertiaryContainer = Color(0xFF242424),
        onTertiaryContainer = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        onSurfaceVariant = Color(0xFFB3B3B3),
        surfaceTint = Color.Transparent,
        inverseSurface = Color.White,
        inverseOnSurface = Color.Black,
        outline = Color(0xFF808080),
        outlineVariant = Color(0xFF333333),
        scrim = Color.Black,
        error = Color(0xFFCCCCCC),
        onError = Color.Black,
        errorContainer = Color(0xFF333333),
        onErrorContainer = Color.White,
        primaryFixed = Color(0xFFE6E6E6),
        primaryFixedDim = Color(0xFFCCCCCC),
        onPrimaryFixed = Color.Black,
        onPrimaryFixedVariant = Color(0xFF333333),
        secondaryFixed = Color(0xFFE6E6E6),
        secondaryFixedDim = Color(0xFFCCCCCC),
        onSecondaryFixed = Color.Black,
        onSecondaryFixedVariant = Color(0xFF333333),
        tertiaryFixed = Color(0xFFE6E6E6),
        tertiaryFixedDim = Color(0xFFCCCCCC),
        onTertiaryFixed = Color.Black,
        onTertiaryFixedVariant = Color(0xFF333333),
    ).pureBlack()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MetrolistTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    SideEffect {
        context.findActivity()?.window?.let { window ->
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = SystemBarAppearance.playerLightStatusBars ?: false
                isAppearanceLightNavigationBars = false
            }
        }
    }
    DisposableEffect(context) {
        context.findActivity()?.window?.setBackgroundDrawable(ColorDrawable(Color.Black.toArgb()))
        onDispose { }
    }
    val isTv = remember(context) { context.isAndroidTv() }
    val motionScheme = remember { MotionScheme.expressive() }
    MaterialExpressiveTheme(
        colorScheme = DarkColorScheme,
        typography = getAppTypography(),
        shapes = MetrolistShapes,
        motionScheme = motionScheme,
    ) {
        CompositionLocalProvider(LocalIsTv provides isTv) {
            MetrolistHapticsProvider {
                FocusOutlineProvider(enabled = isTv, content = content)
            }
        }
    }
}

fun Bitmap.extractThemeColor(): Color = ThemeColorExtractor().extractSeedColor(this)

internal object SystemBarAppearance {
    var imageOwners by mutableIntStateOf(0)
    var playerLightStatusBars by mutableStateOf<Boolean?>(null)
}
