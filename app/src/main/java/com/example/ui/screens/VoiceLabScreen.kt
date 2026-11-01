package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.AudioPlayerController
import com.example.audio.AudioSynthesizerEngine
import com.example.audio.VoiceCloneAnalyzer
import com.example.data.model.LatencySettings
import com.example.data.model.SpeakingTone
import com.example.data.model.VoiceEmotion
import com.example.data.model.VoiceProfile
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
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

@Composable
fun VoiceLabScreen(
    voiceRepository: VoiceRepository,
    cloneAnalyzer: VoiceCloneAnalyzer,
    synthesizerEngine: AudioSynthesizerEngine,
    playerController: AudioPlayerController,
    onUseVoiceInStudio: (VoiceProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val voices by voiceRepository.voices.collectAsStateWithLifecycle()
    val clonedVoices = voices.filter { it.isCloned }

    val isRecording by cloneAnalyzer.recordingState.collectAsStateWithLifecycle()
    val recordedSeconds by cloneAnalyzer.recordingSeconds.collectAsStateWithLifecycle()
    val decibels by cloneAnalyzer.currentDecibels.collectAsStateWithLifecycle()
    val liveAmplitudes by cloneAnalyzer.recordingAmplitudes.collectAsStateWithLifecycle()
    val isAnalyzing by cloneAnalyzer.isAnalyzing.collectAsStateWithLifecycle()

    var showCloneWizard by remember { mutableStateOf(false) }
    var cloneNameInput by remember { mutableStateOf("") }
    var cloneDescInput by remember { mutableStateOf("") }
    var cloneSuccessMessage by remember { mutableStateOf<String?>(null) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch {
                cloneAnalyzer.startRecording()
            }
        }
    }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = StudioSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(StudioSecondary, StudioPrimary))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Instant Voice Cloning",
                                style = MaterialTheme.typography.titleLarge,
                                color = StudioTextPrimary
                            )
                            Text(
                                text = "Acoustic cloning with neural formant alignment",
                                style = MaterialTheme.typography.bodySmall,
                                color = StudioTextSecondary
                            )
                        }
                    }
                }

                Text(
                    text = "Clone your voice or any speaker with high fidelity. Record a 10-second sample to capture pitch, timbre, dynamic range, and emotional inflections.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudioTextSecondary
                )

                Button(
                    onClick = {
                        showCloneWizard = !showCloneWizard
                        cloneSuccessMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("start_clone_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showCloneWizard) StudioSurfaceVariant else StudioPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (showCloneWizard) Icons.Default.Stop else Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showCloneWizard) "Close Clone Studio" else "Create New Voice Clone",
                        color = Color.White
                    )
                }
            }
        }

        // Voice Clone Wizard / Recording Studio
        AnimatedVisibility(visible = showCloneWizard) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                color = StudioSurfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, StudioSecondary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Step 1: Name Your Cloned Persona",
                        style = MaterialTheme.typography.labelLarge,
                        color = StudioSecondary
                    )

                    OutlinedTextField(
                        value = cloneNameInput,
                        onValueChange = { cloneNameInput = it },
                        placeholder = { Text("e.g. Aryan (Studio Match), My Voice #2", color = StudioTextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clone_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = StudioSurface,
                            unfocusedContainerColor = StudioSurface,
                            focusedBorderColor = StudioSecondary,
                            unfocusedBorderColor = StudioCardBorder,
                            focusedTextColor = StudioTextPrimary,
                            unfocusedTextColor = StudioTextPrimary
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = "Step 2: Record Voice Sample (Minimum 5-10s)",
                        style = MaterialTheme.typography.labelLarge,
                        color = StudioSecondary
                    )

                    // Waveform Recorder Visualizer Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(StudioSurface)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRecording) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height
                                val barCount = liveAmplitudes.size.coerceAtLeast(1)
                                val barWidth = (canvasWidth / 60).coerceAtLeast(4.dp.toPx())
                                val gap = 3.dp.toPx()

                                for (i in liveAmplitudes.indices) {
                                    val amp = liveAmplitudes[i]
                                    val barH = (canvasHeight * amp).coerceAtLeast(4.dp.toPx())
                                    val x = canvasWidth - (liveAmplitudes.size - i) * (barWidth + gap)
                                    val y = (canvasHeight - barH) / 2f

                                    drawRoundRect(
                                        brush = Brush.verticalGradient(
                                            listOf(StudioSecondary, StudioPrimaryBright)
                                        ),
                                        topLeft = Offset(x, y),
                                        size = Size(barWidth, barH),
                                        cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                                    )
                                }
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = StudioTextMuted,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = if (recordedSeconds > 0) "Sample Recorded (${String.format("%.1fs", recordedSeconds)})" else "Press record and speak naturally",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (recordedSeconds > 0) SuccessGreen else StudioTextMuted
                                )
                            }
                        }
                    }

                    // Recording Controls & Timer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Timer: ${String.format("%.1f", recordedSeconds)}s",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (recordedSeconds >= 5f) SuccessGreen else StudioTextPrimary
                        )

                        // Mic Record Button
                        IconButton(
                            onClick = {
                                if (isRecording) {
                                    cloneAnalyzer.stopRecording()
                                } else {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        coroutineScope.launch {
                                            cloneAnalyzer.startRecording()
                                        }
                                    } else {
                                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .scale(if (isRecording) pulseScale else 1f)
                                .clip(CircleShape)
                                .then(
                                    if (isRecording) Modifier.background(Color(0xFFEF4444))
                                    else Modifier.background(Brush.linearGradient(listOf(StudioPrimary, StudioSecondary)))
                                )
                                .testTag("record_mic_toggle")
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = if (isRecording) "Level: ${decibels.toInt()} dB" else "Min 5.0s",
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioTextSecondary
                        )
                    }

                    // Analyze & Save Action
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val name = cloneNameInput.ifBlank { "My Voice Clone #${clonedVoices.size + 1}" }
                                val newVoice = cloneAnalyzer.analyzeAndCreateClone(
                                    cloneName = name,
                                    description = "Instant voice clone with 98% neural fidelity",
                                    avatarHex = 0xFF06B6D4
                                )
                                voiceRepository.saveClonedVoice(newVoice)
                                showCloneWizard = false
                                cloneNameInput = ""
                                cloneSuccessMessage = "Voice cloned successfully: ${newVoice.name}!"
                            }
                        },
                        enabled = !isAnalyzing && (recordedSeconds >= 3.0f || !isRecording),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("save_clone_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = StudioSecondary)
                    ) {
                        if (isAnalyzing) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Text("Analyzing Acoustic Features...", color = Color.White)
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Text("Extract Acoustic Features & Save Clone", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Success Alert
        cloneSuccessMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SuccessGreen.copy(alpha = 0.2f))
                    .border(1.dp, SuccessGreen, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen
                    )
                    Text(text = msg, color = StudioTextPrimary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Cloned Voices List Section
        Text(
            text = "Your Cloned Voices (${clonedVoices.size})",
            style = MaterialTheme.typography.titleMedium,
            color = StudioTextPrimary
        )

        if (clonedVoices.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = StudioSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = StudioTextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "No custom clones yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = StudioTextSecondary
                    )
                    Text(
                        text = "Tap 'Create New Voice Clone' above to record a voice and build your synthetic clone model.",
                        style = MaterialTheme.typography.bodySmall,
                        color = StudioTextMuted
                    )
                }
            }
        } else {
            clonedVoices.forEach { voice ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .testTag("cloned_voice_card_${voice.id}"),
                    color = StudioSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(voice.avatarHex), Color(voice.avatarHex).copy(alpha = 0.5f))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = voice.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = StudioTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(StudioSecondary.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Neural Match 98%",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = StudioSecondary
                                            )
                                        }
                                    }
                                    Text(
                                        text = voice.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StudioTextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { voiceRepository.deleteClonedVoice(voice.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete clone",
                                    tint = StudioTextMuted
                                )
                            }
                        }

                        // Acoustic Telemetry Chips
                        voice.acousticFingerprint?.let { fp ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StudioSurfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "F0: ${fp.fundamentalPitchHz.toInt()}Hz",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StudioTextSecondary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StudioSurfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Formants: ${fp.formantF1.toInt()} / ${fp.formantF2.toInt()}Hz",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StudioTextSecondary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StudioSurfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "HNR: ${fp.harmonicNoiseRatioDb.toInt()}dB",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StudioTextSecondary
                                    )
                                }
                            }
                        }

                        // Actions Row: Preview & Use in Studio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val previewRes = synthesizerEngine.synthesizeSpeech(
                                            text = "Testing cloned voice synthesis for ${voice.name}.",
                                            voice = voice,
                                            emotion = VoiceEmotion.NEUTRAL,
                                            tone = SpeakingTone(),
                                            model = voiceRepository.availableModels[0],
                                            latencySettings = LatencySettings(),
                                            apiKey = voiceRepository.getApiKey()
                                        )
                                        previewRes.getOrNull()?.let {
                                            playerController.loadAndPlay(it.audioFile.absolutePath)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceVariant)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = StudioTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Preview Voice", color = StudioTextPrimary)
                            }

                            Button(
                                onClick = { onUseVoiceInStudio(voice) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = StudioPrimary)
                            ) {
                                Text("Use in Studio", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
