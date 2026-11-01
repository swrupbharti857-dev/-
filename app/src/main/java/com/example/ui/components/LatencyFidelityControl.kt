package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.LatencySettings
import com.example.data.model.SynthesisModel
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
fun LatencyFidelityControl(
    models: List<SynthesisModel>,
    selectedModel: SynthesisModel,
    latencySettings: LatencySettings,
    stability: Float,
    similarityBoost: Float,
    styleExaggeration: Float,
    speakerBoost: Boolean,
    onModelChange: (SynthesisModel) -> Unit,
    onLatencySettingsChange: (LatencySettings) -> Unit,
    onStabilityChange: (Float) -> Unit,
    onSimilarityBoostChange: (Float) -> Unit,
    onStyleExaggerationChange: (Float) -> Unit,
    onSpeakerBoostChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

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
                            .background(StudioSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = StudioSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Fidelity & Latency",
                                style = MaterialTheme.typography.titleMedium,
                                color = StudioTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudioPrimary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = selectedModel.latencyLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StudioPrimaryBright
                                )
                            }
                        }
                        Text(
                            text = "${selectedModel.name} • ${latencySettings.levelLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Fidelity Settings",
                        tint = StudioTextSecondary
                    )
                }
            }

            // Quick Models Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                models.take(2).forEach { model ->
                    val isSelected = model.id == selectedModel.id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) StudioPrimary.copy(alpha = 0.2f) else StudioSurfaceVariant)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) StudioPrimary else StudioCardBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onModelChange(model) }
                            .padding(10.dp)
                            .testTag("model_${model.id}")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = model.name.split(" ")[0],
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) StudioPrimaryBright else StudioTextPrimary
                                )
                                Text(
                                    text = model.fidelityScore,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StudioSecondary
                                )
                            }
                            Text(
                                text = model.latencyLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = StudioTextMuted
                            )
                        }
                    }
                }
            }

            // Expanded Detailed Parameters
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Remaining models if more than 2
                    if (models.size > 2) {
                        Text(
                            text = "All Synthesis Engines",
                            style = MaterialTheme.typography.labelMedium,
                            color = StudioTextSecondary
                        )
                        models.drop(2).forEach { model ->
                            val isSelected = model.id == selectedModel.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) StudioPrimary.copy(alpha = 0.15f) else StudioSurfaceVariant)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) StudioPrimary else StudioCardBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onModelChange(model) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = model.name,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = StudioTextPrimary
                                        )
                                        Text(
                                            text = model.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = StudioTextMuted
                                        )
                                    }
                                    Text(
                                        text = model.latencyLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StudioSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Latency Optimization Level Selector
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(StudioSurfaceVariant.copy(alpha = 0.6f))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Latency Optimization Tier",
                                style = MaterialTheme.typography.labelMedium,
                                color = StudioTextPrimary
                            )
                            Text(
                                text = "Level ${latencySettings.optimizationLevel}",
                                style = MaterialTheme.typography.labelMedium,
                                color = StudioSecondary
                            )
                        }
                        Text(
                            text = latencySettings.levelLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioTextMuted
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(0, 1, 2, 3).forEach { lvl ->
                                val active = latencySettings.optimizationLevel == lvl
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) StudioSecondary.copy(alpha = 0.25f) else StudioSurface)
                                        .border(
                                            width = 1.dp,
                                            color = if (active) StudioSecondary else StudioCardBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            onLatencySettingsChange(latencySettings.copy(optimizationLevel = lvl))
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (lvl == 0) "Quality" else "Lvl $lvl",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (active) StudioSecondary else StudioTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Stability
                    KnobSlider(
                        label = "Stability",
                        value = stability,
                        valueLabel = "${(stability * 100).toInt()}%",
                        leftTag = "Variable / Emotional",
                        rightTag = "Monotone / Robuste",
                        onValueChange = onStabilityChange,
                        accentColor = StudioPrimaryBright
                    )

                    // Similarity Boost
                    KnobSlider(
                        label = "Clarity + Similarity Boost",
                        value = similarityBoost,
                        valueLabel = "${(similarityBoost * 100).toInt()}%",
                        leftTag = "Low Artifact",
                        rightTag = "High Voice Match",
                        onValueChange = onSimilarityBoostChange,
                        accentColor = StudioSecondary
                    )

                    // Style Exaggeration
                    KnobSlider(
                        label = "Style Exaggeration",
                        value = styleExaggeration,
                        valueLabel = "${(styleExaggeration * 100).toInt()}%",
                        leftTag = "Natural",
                        rightTag = "Dramatic",
                        onValueChange = onStyleExaggerationChange,
                        accentColor = Color(0xFFA855F7)
                    )

                    // Speaker Boost Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(StudioSurfaceVariant.copy(alpha = 0.6f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Speaker Boost",
                                style = MaterialTheme.typography.labelMedium,
                                color = StudioTextPrimary
                            )
                            Text(
                                text = "Amplifies speaker fingerprint similarity & spectral clarity",
                                style = MaterialTheme.typography.bodySmall,
                                color = StudioTextMuted
                            )
                        }
                        Switch(
                            checked = speakerBoost,
                            onCheckedChange = onSpeakerBoostChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = StudioPrimary,
                                uncheckedTrackColor = StudioCardBorder
                            ),
                            modifier = Modifier.testTag("speaker_boost_switch")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KnobSlider(
    label: String,
    value: Float,
    valueLabel: String,
    leftTag: String,
    rightTag: String,
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
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = StudioTextPrimary
            )
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.labelMedium,
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
                .height(28.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = leftTag, style = MaterialTheme.typography.bodySmall, color = StudioTextMuted)
            Text(text = rightTag, style = MaterialTheme.typography.bodySmall, color = StudioTextMuted)
        }
    }
}
