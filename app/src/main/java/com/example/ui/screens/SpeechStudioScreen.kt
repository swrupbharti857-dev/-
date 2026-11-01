package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RecordVoiceOver
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.audio.AudioPlayerController
import com.example.audio.AudioSynthesizerEngine
import com.example.audio.SynthesisResult
import com.example.data.model.GenerationRecord
import com.example.data.model.LatencySettings
import com.example.data.model.SpeakingTone
import com.example.data.model.SynthesisModel
import com.example.data.model.VoiceEmotion
import com.example.data.model.VoiceProfile
import com.example.data.repository.VoiceRepository
import com.example.ui.components.EmotionToneControl
import com.example.ui.components.LatencyFidelityControl
import com.example.ui.components.VoiceSelectorSheet
import com.example.ui.components.WaveformVisualizer
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
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@Composable
fun SpeechStudioScreen(
    voiceRepository: VoiceRepository,
    synthesizerEngine: AudioSynthesizerEngine,
    playerController: AudioPlayerController,
    voices: List<VoiceProfile>,
    currentVoice: VoiceProfile,
    onVoiceSelect: (VoiceProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var inputText by remember {
        mutableStateOf(
            "Hello and welcome to ElevenVoice! You can clone your voice, adjust emotion from happy to dramatic, and toggle ultra-low latency settings."
        )
    }

    var selectedEmotion by remember { mutableStateOf(VoiceEmotion.HAPPY) }
    var speakingTone by remember { mutableStateOf(SpeakingTone()) }

    var selectedModel by remember {
        mutableStateOf(voiceRepository.availableModels.firstOrNull { it.id == voiceRepository.getSelectedModelId() } ?: voiceRepository.availableModels[0])
    }
    var latencySettings by remember { mutableStateOf(voiceRepository.getLatencySettings()) }

    var stability by remember { mutableStateOf(currentVoice.stability) }
    var similarityBoost by remember { mutableStateOf(currentVoice.similarityBoost) }
    var styleExaggeration by remember { mutableStateOf(currentVoice.styleExaggeration) }
    var speakerBoost by remember { mutableStateOf(currentVoice.speakerBoost) }

    var showVoiceSheet by remember { mutableStateOf(false) }
    var isSynthesizing by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<SynthesisResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val samplePrompts = listOf(
        "🇮🇳 हिंदी स्वागत" to "नमस्ते! ElevenVoice में आपका स्वागत है। यहाँ आप 10 भारतीय आवाज़ों में टेक्स्ट को भाषण में बदल सकते हैं।",
        "🎙️ पॉडकास्ट (आरव/कबीर)" to "नमस्ते दोस्तों! आज के इस विशेष एपिसोड में हम आर्टिफिशियल इंटेलिजेंस और वॉयस क्लोनिंग पर चर्चा करेंगे।",
        "📖 कहानी (काव्या/विक्रम)" to "बहुत समय पहले, हिमालय की शांत वादियों में एक ऋषि तपस्या कर रहे थे, और चारों ओर शांति फैली थी।",
        "✨ शायरी (देव)" to "कुछ तो बात है इन ख़ामोश हवाओं में, जो हर धड़कन को एक नई कहानी सुना जाती हैं।",
        "🛍️ विज्ञापन (दिया)" to "हेलो दोस्तों! साल की सबसे बड़ी सेल लाइव हो चुकी है, अपने मनपसंद प्रोडक्ट्स आज ही ऑर्डर करें!",
        "🧘 ध्यान (मीरा)" to "अपनी आँखें धीरे से बंद करें, एक गहरी साँस लें और मन की सारी चिंताओं को विदा कर दें।"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Voice Selector Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { showVoiceSheet = true }
                .testTag("open_voice_selector"),
            color = StudioSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(currentVoice.avatarHex),
                                        Color(currentVoice.avatarHex).copy(alpha = 0.6f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentVoice.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = StudioTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(StudioPrimary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentVoice.isCloned) "Cloned" else currentVoice.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StudioPrimaryBright
                                )
                            }
                        }
                        Text(
                            text = "${currentVoice.gender} • ${currentVoice.accent}",
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioTextSecondary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Change",
                        style = MaterialTheme.typography.labelMedium,
                        color = StudioSecondary
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select voice",
                        tint = StudioSecondary
                    )
                }
            }
        }

        // Text Input Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
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
                    Text(
                        text = "Speech Text",
                        style = MaterialTheme.typography.titleSmall,
                        color = StudioTextSecondary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${inputText.length} characters",
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioTextMuted
                        )
                        if (inputText.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear text",
                                tint = StudioTextMuted,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { inputText = "" }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            "Type or paste text to synthesize with high fidelity...",
                            color = StudioTextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("tts_text_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioSurfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = StudioSurfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = StudioPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Prompt Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(samplePrompts) { (label, text) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioSurfaceVariant)
                                .clickable { inputText = text }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = StudioTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Emotion & Tone Controls (Specifically requested feature!)
        EmotionToneControl(
            selectedEmotion = selectedEmotion,
            speakingTone = speakingTone,
            onEmotionChange = { selectedEmotion = it },
            onToneChange = { speakingTone = it },
            isJesaVoice = currentVoice.id == "voice_jesa"
        )

        // Latency & Fidelity Settings (High fidelity & low latency settings requested!)
        LatencyFidelityControl(
            models = voiceRepository.availableModels,
            selectedModel = selectedModel,
            latencySettings = latencySettings,
            stability = stability,
            similarityBoost = similarityBoost,
            styleExaggeration = styleExaggeration,
            speakerBoost = speakerBoost,
            onModelChange = {
                selectedModel = it
                voiceRepository.setSelectedModelId(it.id)
            },
            onLatencySettingsChange = {
                latencySettings = it
                voiceRepository.saveLatencySettings(it)
            },
            onStabilityChange = { stability = it },
            onSimilarityBoostChange = { similarityBoost = it },
            onStyleExaggerationChange = { styleExaggeration = it },
            onSpeakerBoostChange = { speakerBoost = it }
        )

        // Primary Synthesize Button
        Button(
            onClick = {
                if (inputText.isBlank()) return@Button
                isSynthesizing = true
                errorMessage = null
                coroutineScope.launch {
                    val apiKey = voiceRepository.getApiKey()
                    val result = synthesizerEngine.synthesizeSpeech(
                        text = inputText,
                        voice = currentVoice.copy(
                            stability = stability,
                            similarityBoost = similarityBoost,
                            styleExaggeration = styleExaggeration,
                            speakerBoost = speakerBoost
                        ),
                        emotion = selectedEmotion,
                        tone = speakingTone,
                        model = selectedModel,
                        latencySettings = latencySettings,
                        apiKey = apiKey
                    )

                    isSynthesizing = false
                    result.fold(
                        onSuccess = { res ->
                            lastResult = res
                            playerController.loadAndPlay(res.audioFile.absolutePath)

                            // Save to generation history
                            voiceRepository.addHistoryRecord(
                                GenerationRecord(
                                    id = UUID.randomUUID().toString(),
                                    text = inputText,
                                    voiceId = currentVoice.id,
                                    voiceName = currentVoice.name,
                                    emotion = selectedEmotion,
                                    tone = speakingTone,
                                    latencyMs = res.latencyMs,
                                    durationMs = res.durationMs,
                                    timestamp = System.currentTimeMillis(),
                                    audioPath = res.audioFile.absolutePath,
                                    characterCount = res.characterCount
                                )
                            )
                        },
                        onFailure = { err ->
                            errorMessage = err.message ?: "Speech synthesis error"
                        }
                    )
                }
            },
            enabled = !isSynthesizing && inputText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .testTag("synthesize_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = StudioPrimary,
                disabledContainerColor = StudioSurfaceVariant
            )
        ) {
            if (isSynthesizing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Synthesizing Neural Voice...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
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
                    Text(
                        text = "Generate Speech",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "• ${selectedModel.latencyLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioSecondary
                    )
                }
            }
        }

        // Error banner if any
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF7F1D1D).copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFFCA5A5),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Real-time Audio Waveform & Player Output Card
        lastResult?.let { result ->
            val isPlaying by playerController.isPlaying.collectAsStateWithLifecycle(false)
            val currentPos by playerController.currentPositionMs.collectAsStateWithLifecycle(0)
            val duration by playerController.durationMs.collectAsStateWithLifecycle(result.durationMs.toInt())
            val playbackSpeed by playerController.playbackSpeed.collectAsStateWithLifecycle(1.0f)
            val bars by playerController.waveformBars.collectAsStateWithLifecycle(emptyList())

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Latency & Model Performance Metrics Telemetry Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    color = StudioSurfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = StudioSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Latency: ${result.latencyMs}ms",
                                style = MaterialTheme.typography.labelMedium,
                                color = StudioSecondary
                            )
                        }

                        Text(
                            text = result.modelUsed,
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioTextMuted
                        )

                        Text(
                            text = "${result.characterCount} chars",
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioTextSecondary
                        )
                    }
                }

                WaveformVisualizer(
                    bars = bars,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPos,
                    durationMs = duration,
                    playbackSpeed = playbackSpeed,
                    onTogglePlayPause = { playerController.togglePlayPause() },
                    onSeek = { playerController.seekTo(it) },
                    onSpeedChange = { playerController.setSpeed(it) },
                    onShare = {
                        shareAudio(context, result.audioFile)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showVoiceSheet) {
        VoiceSelectorSheet(
            voices = voices,
            selectedVoice = currentVoice,
            onVoiceSelected = {
                onVoiceSelect(it)
                stability = it.stability
                similarityBoost = it.similarityBoost
                styleExaggeration = it.styleExaggeration
                speakerBoost = it.speakerBoost
            },
            onPreviewVoice = { voice ->
                coroutineScope.launch {
                    val previewRes = synthesizerEngine.synthesizeSpeech(
                        text = voice.sampleText,
                        voice = voice,
                        emotion = selectedEmotion,
                        tone = speakingTone,
                        model = selectedModel,
                        latencySettings = latencySettings,
                        apiKey = voiceRepository.getApiKey()
                    )
                    previewRes.getOrNull()?.let {
                        playerController.loadAndPlay(it.audioFile.absolutePath)
                    }
                }
            },
            onDismiss = { showVoiceSheet = false }
        )
    }
}

private fun shareAudio(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share AI Voice Audio"))
    } catch (e: Exception) {
        // Fallback simple share
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Generated audio using ElevenVoice AI Studio")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Audio Info"))
    }
}
