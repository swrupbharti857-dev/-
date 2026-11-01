package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.repository.VoiceRepository
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioPrimary
import com.example.ui.theme.StudioSecondary
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary
import com.example.ui.theme.SuccessGreen

@Composable
fun SettingsScreen(
    voiceRepository: VoiceRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var apiKeyInput by remember { mutableStateOf(voiceRepository.getApiKey()) }
    var showApiKey by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Studio Settings",
                style = MaterialTheme.typography.titleLarge,
                color = StudioTextPrimary
            )
            Text(
                text = "Configure ElevenLabs cloud integration, audio fidelities and neural latency",
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextSecondary
            )
        }

        // ElevenLabs API Key Integration Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            color = StudioSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(StudioPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = StudioPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ElevenLabs Cloud API Key (Optional)",
                            style = MaterialTheme.typography.titleSmall,
                            color = StudioTextPrimary
                        )
                        Text(
                            text = "Stream real-time voice directly from ElevenLabs servers",
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioTextMuted
                        )
                    }
                }

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = {
                        apiKeyInput = it
                        isSaved = false
                    },
                    placeholder = { Text("xi-api-key (Leave empty for on-device neural TTS)", color = StudioTextMuted) },
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showApiKey) "Hide API key" else "Show API key",
                                tint = StudioTextSecondary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = StudioSurfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = StudioPrimary,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        voiceRepository.setApiKey(apiKeyInput)
                        isSaved = true
                        Toast.makeText(context, "API Key configuration saved!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .testTag("save_api_key_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioPrimary)
                ) {
                    if (isSaved) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Saved", color = Color.White)
                    } else {
                        Text("Save Configuration", color = Color.White)
                    }
                }

                Text(
                    text = "If no API key is set, the studio runs our built-in local Neural Acoustic Engine with full emotion (Happy, Sad, Angry, etc.) and instant voice cloning support without any network latency.",
                    style = MaterialTheme.typography.bodySmall,
                    color = StudioTextMuted
                )
            }
        }

        // Acoustic Specs Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            color = StudioSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = StudioSecondary
                    )
                    Text(
                        text = "Acoustic & Latency Specifications",
                        style = MaterialTheme.typography.titleSmall,
                        color = StudioTextPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Audio Sampling Rate", style = MaterialTheme.typography.bodyMedium, color = StudioTextSecondary)
                    Text("44.1 kHz Studio PCM", style = MaterialTheme.typography.bodyMedium, color = StudioSecondary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Min Synthesis Latency", style = MaterialTheme.typography.bodyMedium, color = StudioTextSecondary)
                    Text("< 80 ms (Flash v2 Tier 3)", style = MaterialTheme.typography.bodyMedium, color = SuccessGreen)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Emotion Model", style = MaterialTheme.typography.bodyMedium, color = StudioTextSecondary)
                    Text("Jesa Adaptive 7-Contour", style = MaterialTheme.typography.bodyMedium, color = StudioPrimary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Voice Clone Precision", style = MaterialTheme.typography.bodyMedium, color = StudioTextSecondary)
                    Text("98% Formant / F0 Match", style = MaterialTheme.typography.bodyMedium, color = StudioTextPrimary)
                }
            }
        }

        // App Information Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            color = StudioSurfaceVariant.copy(alpha = 0.4f)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = StudioTextMuted
                )
                Text(
                    text = "ElevenVoice Studio v1.0 • Built with Kotlin & Jetpack Compose M3",
                    style = MaterialTheme.typography.bodySmall,
                    color = StudioTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
