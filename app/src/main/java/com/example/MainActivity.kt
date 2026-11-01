package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.audio.AudioPlayerController
import com.example.audio.AudioSynthesizerEngine
import com.example.audio.VoiceCloneAnalyzer
import com.example.data.repository.VoiceRepository
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioBackground

class MainActivity : ComponentActivity() {

    private lateinit var voiceRepository: VoiceRepository
    private lateinit var synthesizerEngine: AudioSynthesizerEngine
    private lateinit var cloneAnalyzer: VoiceCloneAnalyzer
    private lateinit var playerController: AudioPlayerController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        voiceRepository = VoiceRepository(applicationContext)
        synthesizerEngine = AudioSynthesizerEngine(applicationContext)
        cloneAnalyzer = VoiceCloneAnalyzer(applicationContext)
        playerController = AudioPlayerController(applicationContext)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = StudioBackground
                ) {
                    MainScreen(
                        voiceRepository = voiceRepository,
                        synthesizerEngine = synthesizerEngine,
                        cloneAnalyzer = cloneAnalyzer,
                        playerController = playerController
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        synthesizerEngine.release()
        playerController.release()
    }
}
