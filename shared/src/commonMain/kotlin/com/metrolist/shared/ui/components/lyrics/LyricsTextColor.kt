/*
 * DARK, based on the Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.shared.ui.components.lyrics

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val LocalDarkLyricsColors = staticCompositionLocalOf { false }

internal fun lyricsTextColorForPlayer(
    textColor: Color,
    neutralColor: Boolean,
    darkDesign: Boolean,
    scheme: ColorScheme,
): Color =
    when {
        darkDesign -> Color.White
        neutralColor -> if (textColor.luminance() > 0.5f) Color.White else Color.Black
        else -> lyricsPaletteTextColor(scheme, textColor.luminance() > 0.5f)
    }
