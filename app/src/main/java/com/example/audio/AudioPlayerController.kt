package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AudioPlayerController(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1)
    val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _waveformBars = MutableStateFlow<List<Float>>(List(48) { 0.15f })
    val waveformBars: StateFlow<List<Float>> = _waveformBars.asStateFlow()

    private var currentAudioPath: String? = null

    fun loadAndPlay(audioPath: String) {
        val file = File(audioPath)
        if (!file.exists()) return

        stop()
        currentAudioPath = audioPath

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.fromFile(file))
                prepare()
                val dur = duration.coerceAtLeast(1000)
                _durationMs.value = dur
                generateWaveformBars(file, dur)

                playbackParams = playbackParams.setSpeed(_playbackSpeed.value)

                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPositionMs.value = 0
                    stopProgressTracking()
                }

                start()
            }
            _isPlaying.value = true
            startProgressTracking()
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) {
            mp.pause()
            _isPlaying.value = false
            stopProgressTracking()
        } else {
            mp.start()
            _isPlaying.value = true
            startProgressTracking()
        }
    }

    fun seekTo(fraction: Float) {
        val mp = mediaPlayer ?: return
        val targetMs = (fraction * _durationMs.value).toInt()
        mp.seekTo(targetMs)
        _currentPositionMs.value = targetMs
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        try {
            mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(speed) ?: return
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        stopProgressTracking()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentPositionMs.value = 0
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = scope.launch {
            while (isActive && mediaPlayer != null && _isPlaying.value) {
                try {
                    _currentPositionMs.value = mediaPlayer?.currentPosition ?: 0
                } catch (e: Exception) {
                    break
                }
                delay(40)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun generateWaveformBars(file: File, durationMs: Int) {
        // Derive smooth natural waveform envelope
        val count = 48
        val randomSeed = file.name.hashCode().toLong()
        val random = java.util.Random(randomSeed)
        val bars = ArrayList<Float>(count)
        for (i in 0 until count) {
            val progress = i.toFloat() / count
            val envelope = kotlin.math.sin(progress * Math.PI).toFloat()
            val noise = (random.nextFloat() * 0.5f + 0.5f)
            val height = (envelope * 0.7f + noise * 0.3f).coerceIn(0.12f, 0.98f)
            bars.add(height)
        }
        _waveformBars.value = bars
    }

    fun release() {
        stop()
    }
}
