package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioPrimaryBright
import com.example.ui.theme.StudioSecondary
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun WaveformVisualizer(
    bars: List<Float>,
    isPlaying: Boolean,
    currentPositionMs: Int,
    durationMs: Int,
    playbackSpeed: Float,
    onTogglePlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (durationMs > 0) {
        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = StudioSurface,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Interactive Waveform Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StudioSurfaceVariant.copy(alpha = 0.5f))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val seekFraction = (offset.x / size.width).coerceIn(0f, 1f)
                            onSeek(seekFraction)
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val barCount = bars.size.coerceAtLeast(1)
                    val gap = 3.dp.toPx()
                    val totalGap = (barCount - 1) * gap
                    val barWidth = ((canvasWidth - totalGap) / barCount).coerceAtLeast(2.dp.toPx())

                    val activeProgressX = canvasWidth * progress

                    for (i in 0 until barCount) {
                        val barHeightFactor = bars.getOrElse(i) { 0.2f }
                        val barHeight = (canvasHeight * barHeightFactor).coerceAtLeast(4.dp.toPx())
                        val x = i * (barWidth + gap)
                        val y = (canvasHeight - barHeight) / 2f

                        val isPlayed = (x + barWidth / 2) <= activeProgressX

                        val barBrush = if (isPlayed) {
                            Brush.verticalGradient(
                                colors = listOf(StudioSecondary, StudioPrimaryBright)
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF334155), Color(0xFF1E293B))
                            )
                        }

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                        )
                    }

                    // Scrubber playhead line
                    if (progress > 0f) {
                        drawCircle(
                            color = StudioSecondary,
                            radius = 4.dp.toPx(),
                            center = Offset(activeProgressX, canvasHeight / 2)
                        )
                    }
                }
            }

            // Time & Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Play / Pause Floating pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(StudioPrimary, StudioSecondary))
                            )
                            .testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column {
                        val curSec = currentPositionMs / 1000
                        val totSec = durationMs / 1000
                        Text(
                            text = String.format("%02d:%02d / %02d:%02d", curSec / 60, curSec % 60, totSec / 60, totSec % 60),
                            style = MaterialTheme.typography.labelLarge,
                            color = StudioTextPrimary
                        )
                        Text(
                            text = if (isPlaying) "Streaming Playback..." else "Ready to play",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isPlaying) StudioSecondary else StudioTextMuted
                        )
                    }
                }

                // Speed and Share Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val speeds = listOf(1.0f, 1.25f, 1.5f)
                    speeds.forEach { spd ->
                        val isSelected = playbackSpeed == spd
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) StudioPrimary.copy(alpha = 0.25f) else StudioSurfaceVariant)
                                .pointerInput(spd) {
                                    detectTapGestures { onSpeedChange(spd) }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${spd}x",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) StudioPrimaryBright else StudioTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(StudioSurfaceVariant)
                            .testTag("share_audio_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Audio",
                            tint = StudioTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
