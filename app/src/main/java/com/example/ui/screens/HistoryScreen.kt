package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.AudioPlayerController
import com.example.data.repository.VoiceRepository
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    voiceRepository: VoiceRepository,
    playerController: AudioPlayerController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val historyItems by voiceRepository.history.collectAsStateWithLifecycle()
    val isPlaying by playerController.isPlaying.collectAsStateWithLifecycle(false)

    val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Generation History",
                    style = MaterialTheme.typography.titleLarge,
                    color = StudioTextPrimary
                )
                Text(
                    text = "${historyItems.size} synthesized speech items",
                    style = MaterialTheme.typography.bodySmall,
                    color = StudioTextSecondary
                )
            }

            if (historyItems.isNotEmpty()) {
                IconButton(
                    onClick = { voiceRepository.clearAllHistory() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear all history",
                        tint = StudioTextMuted
                    )
                }
            }
        }

        if (historyItems.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = StudioSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = StudioTextMuted,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "No history yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = StudioTextSecondary
                    )
                    Text(
                        text = "Synthesize speech in the Studio tab and your generated audio clips will appear here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = StudioTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historyItems, key = { it.id }) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .testTag("history_item_${item.id}"),
                        color = StudioSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Top Row: Voice & Emotion Badge + Time
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.voiceName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = StudioPrimaryBright
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(item.emotion.color.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.emotion.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = item.emotion.color
                                        )
                                    }
                                    Text(
                                        text = "• ${item.latencyMs}ms",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StudioSecondary
                                    )
                                }

                                Text(
                                    text = dateFormat.format(Date(item.timestamp)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StudioTextMuted
                                )
                            }

                            // Speech Text
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = StudioTextPrimary,
                                maxLines = 3
                            )

                            // Actions Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Play Button
                                    if (item.audioPath != null && File(item.audioPath).exists()) {
                                        Button(
                                            onClick = {
                                                playerController.loadAndPlay(item.audioPath)
                                            },
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            colors = ButtonDefaults.buttonColors(containerColor = StudioPrimary)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play",
                                                modifier = Modifier.size(16.dp),
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Play", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                        }
                                    }

                                    // Copy Text Button
                                    IconButton(
                                        onClick = {
                                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            cm.setPrimaryClip(ClipData.newPlainText("AI Speech", item.text))
                                            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy text",
                                            tint = StudioTextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Share Audio Button
                                    if (item.audioPath != null && File(item.audioPath).exists()) {
                                        IconButton(
                                            onClick = {
                                                try {
                                                    val file = File(item.audioPath)
                                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "audio/*"
                                                        putExtra(Intent.EXTRA_STREAM, uri)
                                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    }
                                                    context.startActivity(Intent.createChooser(shareIntent, "Share Generated Speech"))
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Share audio", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share audio",
                                                tint = StudioTextSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { voiceRepository.deleteHistoryRecord(item.id) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete record",
                                        tint = StudioTextMuted,
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
