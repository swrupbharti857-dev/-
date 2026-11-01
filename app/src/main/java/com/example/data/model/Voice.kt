package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EmotionAngry
import com.example.ui.theme.EmotionDramatic
import com.example.ui.theme.EmotionExcited
import com.example.ui.theme.EmotionHappy
import com.example.ui.theme.EmotionNeutral
import com.example.ui.theme.EmotionSad
import com.example.ui.theme.EmotionWhisper

enum class VoiceEmotion(
    val displayName: String,
    val description: String,
    val pitchMultiplier: Float,
    val rateMultiplier: Float,
    val color: Color
) {
    NEUTRAL("Neutral", "Balanced, clear, natural conversational tone", 1.0f, 1.0f, EmotionNeutral),
    HAPPY("Happy", "Upbeat, energetic, warm and cheerful delivery", 1.18f, 1.12f, EmotionHappy),
    SAD("Sad", "Gentle, somber, slower pace and softer delivery", 0.88f, 0.86f, EmotionSad),
    ANGRY("Angry", "Sharp, intense, commanding and assertive", 1.08f, 1.18f, EmotionAngry),
    EXCITED("Excited", "High energy, enthusiastic, dynamic inflections", 1.25f, 1.20f, EmotionExcited),
    WHISPER("Whisper", "Soft, intimate, breathy and close-mic texture", 0.95f, 0.92f, EmotionWhisper),
    DRAMATIC("Dramatic", "Expressive, theatrical, deliberate dramatic pauses", 0.92f, 0.88f, EmotionDramatic)
}

data class SpeakingTone(
    val emotionalIntensity: Float = 0.70f, // 0.0 to 1.0 (subtle to extreme)
    val warmth: Float = 0.50f,             // 0.0 (crisp/bright) to 1.0 (deep/warm)
    val pitchModulation: Float = 0.60f,     // 0.0 (flat/robotic) to 1.0 (melodic contour)
    val cadence: Float = 1.0f,              // 0.5x to 2.0x speaking speed
    val breathiness: Float = 0.20f          // 0.0 to 1.0 air & intimacy
)

data class LatencySettings(
    val optimizationLevel: Int = 1, // 0: Highest Quality, 1: 50% Reduction, 2: 75% Reduction, 3: Ultra Realtime
    val sampleRateKhz: Int = 44,    // 44.1kHz, 24kHz, 16kHz
    val bufferStreaming: Boolean = true
) {
    val levelLabel: String
        get() = when (optimizationLevel) {
            0 -> "None (Highest Quality)"
            1 -> "Tier 1 (~150ms reduction)"
            2 -> "Tier 2 (~300ms reduction)"
            3 -> "Ultra Low Latency (<100ms)"
            else -> "Tier 1"
        }
}

data class SynthesisModel(
    val id: String,
    val name: String,
    val description: String,
    val fidelityScore: String,
    val latencyLabel: String,
    val supportsMultilingual: Boolean = true
)

data class VoiceProfile(
    val id: String,
    val name: String,
    val category: String, // "Jesa Special", "Hindi Neural", "Preset", "Instant Cloned"
    val gender: String,
    val accent: String,
    val description: String,
    val avatarHex: Long,
    val isCloned: Boolean = false,
    val sampleText: String = "Hello! I am your AI voice clone, ready with neural clarity.",
    val stability: Float = 0.75f,
    val similarityBoost: Float = 0.85f,
    val styleExaggeration: Float = 0.20f,
    val speakerBoost: Boolean = true,
    val languageCode: String = "en-US",
    val nativeName: String = "",
    val acousticFingerprint: AcousticFingerprint? = null
)

data class AcousticFingerprint(
    val fundamentalPitchHz: Float,
    val formantF1: Float,
    val formantF2: Float,
    val harmonicNoiseRatioDb: Float,
    val dynamicRangeDb: Float,
    val durationSeconds: Float,
    val confidenceScore: Float = 0.96f
)

data class GenerationRecord(
    val id: String,
    val text: String,
    val voiceId: String,
    val voiceName: String,
    val emotion: VoiceEmotion,
    val tone: SpeakingTone,
    val latencyMs: Long,
    val durationMs: Long,
    val timestamp: Long,
    val audioPath: String? = null,
    val characterCount: Int
)
