package com.example

import com.example.data.model.LatencySettings
import com.example.data.model.SpeakingTone
import com.example.data.model.VoiceEmotion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleVoiceUnitTest {

    @Test
    fun `verify emotion set and multipliers`() {
        assertEquals(7, VoiceEmotion.entries.size)
        assertTrue(VoiceEmotion.entries.contains(VoiceEmotion.HAPPY))
        assertTrue(VoiceEmotion.entries.contains(VoiceEmotion.SAD))
        assertTrue(VoiceEmotion.entries.contains(VoiceEmotion.ANGRY))
        assertTrue(VoiceEmotion.entries.contains(VoiceEmotion.NEUTRAL))
        assertTrue(VoiceEmotion.entries.contains(VoiceEmotion.EXCITED))
        assertTrue(VoiceEmotion.entries.contains(VoiceEmotion.WHISPER))
        assertTrue(VoiceEmotion.entries.contains(VoiceEmotion.DRAMATIC))

        // Verify Happy emotion has elevated pitch and rate
        assertTrue(VoiceEmotion.HAPPY.pitchMultiplier > 1.0f)
        assertTrue(VoiceEmotion.HAPPY.rateMultiplier > 1.0f)

        // Verify Sad emotion has subdued pitch and rate
        assertTrue(VoiceEmotion.SAD.pitchMultiplier < 1.0f)
        assertTrue(VoiceEmotion.SAD.rateMultiplier < 1.0f)
    }

    @Test
    fun `verify speaking tone parameter ranges`() {
        val defaultTone = SpeakingTone()
        assertEquals(0.70f, defaultTone.emotionalIntensity, 0.01f)
        assertEquals(0.50f, defaultTone.warmth, 0.01f)
        assertEquals(0.60f, defaultTone.pitchModulation, 0.01f)
        assertEquals(1.0f, defaultTone.cadence, 0.01f)
        assertEquals(0.20f, defaultTone.breathiness, 0.01f)

        // Modified tone
        val customTone = defaultTone.copy(emotionalIntensity = 0.95f, cadence = 1.4f)
        assertEquals(0.95f, customTone.emotionalIntensity, 0.01f)
        assertEquals(1.4f, customTone.cadence, 0.01f)
    }

    @Test
    fun `verify latency optimization settings`() {
        val defaultLatency = LatencySettings()
        assertEquals(1, defaultLatency.optimizationLevel)
        assertTrue(defaultLatency.levelLabel.contains("Tier 1"))

        val maxLatency = LatencySettings(optimizationLevel = 3)
        assertTrue(maxLatency.levelLabel.contains("Ultra Low Latency"))
    }
}
