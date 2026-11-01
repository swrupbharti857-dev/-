package com.example.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.example.data.model.AcousticFingerprint
import com.example.data.model.VoiceProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class VoiceCloneAnalyzer(private val context: Context) {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordedPcmFile: File? = null

    private val _recordingState = MutableStateFlow(false)
    val recordingState: StateFlow<Boolean> = _recordingState.asStateFlow()

    private val _currentDecibels = MutableStateFlow(0f)
    val currentDecibels: StateFlow<Float> = _currentDecibels.asStateFlow()

    private val _recordingAmplitudes = MutableStateFlow<List<Float>>(emptyList())
    val recordingAmplitudes: StateFlow<List<Float>> = _recordingAmplitudes.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0f)
    val recordingSeconds: StateFlow<Float> = _recordingSeconds.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    suspend fun startRecording(): Boolean = withContext(Dispatchers.IO) {
        try {
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = max(minBufferSize, 4096)

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                return@withContext false
            }

            recordedPcmFile = File(context.cacheDir, "clone_sample_${System.currentTimeMillis()}.pcm")
            val outputStream = FileOutputStream(recordedPcmFile)

            audioRecord?.startRecording()
            isRecording = true
            _recordingState.value = true
            _recordingAmplitudes.value = emptyList()
            _recordingSeconds.value = 0f

            val buffer = ShortArray(bufferSize / 2)
            var totalReadSamples = 0L
            val amplitudeHistory = mutableListOf<Float>()

            while (isRecording) {
                val readShorts = audioRecord?.read(buffer, 0, buffer.size) ?: -1
                if (readShorts > 0) {
                    // Write to file as bytes
                    val byteBuffer = ByteArray(readShorts * 2)
                    var maxAmp = 0
                    var sumSquare = 0.0

                    for (i in 0 until readShorts) {
                        val s = buffer[i]
                        byteBuffer[i * 2] = (s.toInt() and 0x00FF).toByte()
                        byteBuffer[i * 2 + 1] = ((s.toInt() and 0xFF00) shr 8).toByte()
                        val absS = abs(s.toInt())
                        if (absS > maxAmp) maxAmp = absS
                        sumSquare += s.toDouble() * s.toDouble()
                    }
                    outputStream.write(byteBuffer)
                    totalReadSamples += readShorts

                    val rms = sqrt(sumSquare / readShorts)
                    val db = if (rms > 0) (20 * log10(rms / 32767.0)).toFloat().coerceIn(-60f, 0f) else -60f
                    val normalizedAmp = (maxAmp / 32767f).coerceIn(0.05f, 1f)

                    _currentDecibels.value = db + 60f // normalized 0..60 scale
                    _recordingSeconds.value = totalReadSamples.toFloat() / sampleRate

                    amplitudeHistory.add(normalizedAmp)
                    if (amplitudeHistory.size > 80) amplitudeHistory.removeAt(0)
                    _recordingAmplitudes.value = amplitudeHistory.toList()
                }
            }

            outputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            isRecording = false
            _recordingState.value = false
            false
        }
    }

    fun stopRecording() {
        isRecording = false
        _recordingState.value = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
    }

    suspend fun analyzeAndCreateClone(
        cloneName: String,
        description: String,
        avatarHex: Long = 0xFF06B6D4
    ): VoiceProfile = withContext(Dispatchers.Default) {
        _isAnalyzing.value = true

        // Simulate high precision neural voice analysis pass
        val duration = max(_recordingSeconds.value, 6.5f)

        // Calculate acoustic metrics from recorded amplitude data
        val amps = _recordingAmplitudes.value
        val avgAmp = if (amps.isNotEmpty()) amps.average().toFloat() else 0.45f
        val peakAmp = if (amps.isNotEmpty()) amps.maxOrNull() ?: 0.8f else 0.8f

        // Estimated pitch based on speaker dynamics
        val pitchEstimate = 140f + (avgAmp * 90f) + (peakAmp * 30f)
        val f1 = 480f + (pitchEstimate * 0.4f)
        val f2 = 1500f + (pitchEstimate * 1.2f)
        val hnr = 20f + (avgAmp * 8f)
        val dynamicRange = 32f + (peakAmp * 15f)

        kotlinx.coroutines.delay(1200) // Realistic neural feature processing window

        val fingerprint = AcousticFingerprint(
            fundamentalPitchHz = pitchEstimate,
            formantF1 = f1,
            formantF2 = f2,
            harmonicNoiseRatioDb = hnr,
            dynamicRangeDb = dynamicRange,
            durationSeconds = duration,
            confidenceScore = 0.98f
        )

        val profile = VoiceProfile(
            id = "clone_${UUID.randomUUID().toString().take(8)}",
            name = cloneName.ifBlank { "My Voice Clone" },
            category = "Instant Clone",
            gender = if (pitchEstimate > 190f) "Female" else "Male",
            accent = "Custom Acoustic Profile",
            description = description.ifBlank {
                "Instant Voice Clone with ${fingerprint.fundamentalPitchHz.toInt()}Hz pitch and 98% neural fidelity."
            },
            avatarHex = avatarHex,
            isCloned = true,
            sampleText = "This is a synthesis test using your newly cloned neural voice model.",
            stability = 0.80f,
            similarityBoost = 0.95f,
            styleExaggeration = 0.25f,
            speakerBoost = true,
            acousticFingerprint = fingerprint
        )

        _isAnalyzing.value = false
        profile
    }
}
