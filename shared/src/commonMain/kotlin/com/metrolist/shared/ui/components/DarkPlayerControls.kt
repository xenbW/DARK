/*
 * DARK, based on the Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.metrolist.shared.ui.icons.MetrolistIcons

@Composable
internal fun DarkPlayerControls(
    isPlaying: Boolean,
    isLoading: Boolean,
    playText: String,
    pauseText: String,
    previousText: String,
    nextText: String,
    onPrevious: (() -> Unit)?,
    onPlayPause: () -> Unit,
    onNext: (() -> Unit)?,
    focusRequester: FocusRequester?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { onPrevious?.invoke() }, enabled = onPrevious != null, modifier = Modifier.size(48.dp)) {
                Icon(MetrolistIcons.Rounded.SkipPrevious, previousText, modifier = Modifier.size(28.dp))
            }
            FilledIconButton(
                onClick = onPlayPause,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF202020), contentColor = Color.White),
                modifier = Modifier.size(64.dp).then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(26.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(
                        if (isPlaying) MetrolistIcons.Rounded.Pause else MetrolistIcons.Rounded.PlayArrow,
                        if (isPlaying) pauseText else playText,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
            IconButton(onClick = { onNext?.invoke() }, enabled = onNext != null, modifier = Modifier.size(48.dp)) {
                Icon(MetrolistIcons.Rounded.SkipNext, nextText, modifier = Modifier.size(28.dp))
            }
        }
    }
}
