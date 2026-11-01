package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.VoiceProfile
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioPrimaryBright
import com.example.ui.theme.StudioSecondary
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSelectorSheet(
    voices: List<VoiceProfile>,
    selectedVoice: VoiceProfile,
    onVoiceSelected: (VoiceProfile) -> Unit,
    onPreviewVoice: (VoiceProfile) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StudioSurface,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Voice Library",
                        style = MaterialTheme.typography.titleLarge,
                        color = StudioTextPrimary
                    )
                    Text(
                        text = "Select an AI persona or your custom cloned voice",
                        style = MaterialTheme.typography.bodyMedium,
                        color = StudioTextSecondary
                    )
                }
            }

            var activeFilter by remember { mutableStateOf("Hindi") } // Default to Hindi to highlight the new models!
            val filterOptions = listOf(
                "Hindi" to "🇮🇳 हिंदी (10)",
                "All" to "सभी (All)",
                "Male" to "पुरुष (Male)",
                "Female" to "महिला (Female)",
                "Cloned" to "Cloned"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { (key, label) ->
                    val isSelected = activeFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) StudioPrimary.copy(alpha = 0.25f) else StudioSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) StudioPrimary else StudioCardBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { activeFilter = key }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) StudioPrimaryBright else StudioTextSecondary
                        )
                    }
                }
            }

            val filteredVoices = voices.filter { voice ->
                when (activeFilter) {
                    "Hindi" -> voice.languageCode == "hi-IN" || voice.accent.contains("Hindi", ignoreCase = true)
                    "Male" -> voice.gender.equals("Male", ignoreCase = true)
                    "Female" -> voice.gender.equals("Female", ignoreCase = true)
                    "Cloned" -> voice.isCloned
                    else -> true
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredVoices) { voice ->
                    val isSelected = voice.id == selectedVoice.id
                    val isJesa = voice.id == "voice_jesa"
                    val avatarColor = Color(voice.avatarHex)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onVoiceSelected(voice)
                                onDismiss()
                            }
                            .testTag("voice_item_${voice.id}"),
                        color = if (isSelected) StudioSurfaceVariant else StudioSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) (if (isJesa) StudioSecondary else StudioPrimary) else StudioCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                // Avatar circle
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(avatarColor, avatarColor.copy(alpha = 0.6f))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (voice.isCloned) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else if (isJesa) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Text(
                                            text = voice.name.take(1),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = voice.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = StudioTextPrimary
                                        )
                                        if (isJesa) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(StudioSecondary.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Emotion Ready",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = StudioSecondary
                                                )
                                            }
                                        } else if (voice.isCloned) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(StudioPrimary.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Instant Clone",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = StudioPrimaryBright
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "${voice.gender} • ${voice.accent}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StudioTextSecondary
                                    )
                                    Text(
                                        text = voice.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StudioTextMuted,
                                        maxLines = 1
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { onPreviewVoice(voice) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(StudioSurfaceVariant)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Preview voice",
                                        tint = StudioTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(StudioPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
