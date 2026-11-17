/*
 * DARK, based on the Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.shared.ui.player

import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
internal fun Modifier.darkArtworkAccent(
    color: Color,
    shape: Shape,
): Modifier =
    if (LocalPlayerPlatform.current.useDarkDesign) {
        shadow(10.dp, shape, clip = false, ambientColor = color.copy(alpha = 0.12f), spotColor = color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), shape)
    } else {
        this
    }
