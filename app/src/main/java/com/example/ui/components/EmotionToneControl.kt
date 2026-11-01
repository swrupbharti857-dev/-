package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.SpeakingTone
import com.example.data.model.VoiceEmotion
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
fun EmotionToneControl(
    selectedEmotion: VoiceEmotion,
    speakingTone: SpeakingTone,
    onEmotionChange: (VoiceEmotion) -> Unit,
    onToneChange: (SpeakingTone) -> Unit,
    modifier: Modifier = Modifier,
    isJesaVoice: Boolean = true
) {
    var isToneExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = StudioSurface,
        tonalElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(StudioPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = StudioPrimaryBright,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Voice Emotion & Tone",
                                style = MaterialTheme.typography.titleMedium,
                                color = StudioTextPrimary
                            )
                            if (isJesaVoice) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StudioSecondary.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Jesa Enhanced",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StudioSecondary
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Modulates vocal delivery contour and sentiment resonance",
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = { isToneExpanded = !isToneExpanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isToneExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Tone Parameters",
                        tint = StudioTextSecondary
                    )
                }
            }

            // Emotion Pills Horizontal Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VoiceEmotion.entries.forEach { emotion ->
                    val isSelected = emotion == selectedEmotion
                    val emotionColor = emotion.color

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) emotionColor.copy(alpha = 0.22f)
                                else StudioSurfaceVariant
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) emotionColor else StudioCardBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onEmotionChange(emotion) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("emotion_${emotion.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(emotionColor)
                            )
                            Text(
                                text = emotion.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) StudioTextPrimary else StudioTextSecondary
                            )
                        }
                    }
                }
            }

            // Active Emotion Summary Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(selectedEmotion.color.copy(alpha = 0.12f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${selectedEmotion.displayName}: ${selectedEmotion.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = selectedEmotion.color
                )
            }

            // Expandable Tone Parameters Slider Block
            AnimatedVisibility(
                visible = isToneExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    // Header & Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fine-Tuning Tone Controls",
                            style = MaterialTheme.typography.labelLarge,
                            color = StudioPrimaryBright
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onToneChange(SpeakingTone())
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset Tone",
                                tint = StudioTextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Reset",
                                style = MaterialTheme.typography.labelSmall,
                                color = StudioTextMuted
                            )
                        }
                    }

                    // 1. Emotional Intensity
                    ToneSliderItem(
                        label = "Emotional Intensity",
                        value = speakingTone.emotionalIntensity,
                        valueLabel = "${(speakingTone.emotionalIntensity * 100).toInt()}%",
                        description = "Subtle nuance to dramatic theater",
                        onValueChange = { onToneChange(speakingTone.copy(emotionalIntensity = it)) },
                        accentColor = selectedEmotion.color
                    )

                    // 2. Warmth & Body
                    ToneSliderItem(
                        label = "Warmth & Body",
                        value = speakingTone.warmth,
                        valueLabel = "${(speakingTone.warmth * 100).toInt()}%",
                        description = "Bright & crisp to deep & chesty resonance",
                        onValueChange = { onToneChange(speakingTone.copy(warmth = it)) },
                        accentColor = StudioSecondary
                    )

                    // 3. Pitch Modulation
                    ToneSliderItem(
                        label = "Pitch Modulation",
                        value = speakingTone.pitchModulation,
                        valueLabel = "${(speakingTone.pitchModulation * 100).toInt()}%",
                        description = "Monotone to dynamic melodic inflection",
                        onValueChange = { onToneChange(speakingTone.copy(pitchModulation = it)) },
                        accentColor = StudioPrimaryBright
                    )

                    // 4. Cadence / Speed
                    ToneSliderItem(
                        label = "Cadence & Pacing",
                        value = (speakingTone.cadence - 0.5f) / 1.5f,
                        valueLabel = String.format("%.2fx", speakingTone.cadence),
                        description = "Pace and delivery rhythm",
                        onValueChange = { norm ->
                            val cad = 0.5f + (norm * 1.5f)
                            onToneChange(speakingTone.copy(cadence = cad))
                        },
                        accentColor = StudioPrimary
                    )

                    // 5. Breathiness & Texture
                    ToneSliderItem(
                        label = "Breathiness & Air",
                        value = speakingTone.breathiness,
                        valueLabel = "${(speakingTone.breathiness * 100).toInt()}%",
                        description = "Intimate whisper texture & close-mic air",
                        onValueChange = { onToneChange(speakingTone.copy(breathiness = it)) },
                        accentColor = Color(0xFFF472B6)
                    )
                }
            }
        }
    }
}

@Composable
private fun ToneSliderItem(
    label: String,
    value: Float,
    valueLabel: String,
    description: String,
    onValueChange: (Float) -> Unit,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(StudioSurfaceVariant.copy(alpha = 0.6f))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = StudioTextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = StudioTextMuted
                )
            }
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.labelLarge,
                color = accentColor
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = StudioCardBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        )
    }
}
