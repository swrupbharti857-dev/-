package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.AudioPlayerController
import com.example.audio.AudioSynthesizerEngine
import com.example.audio.VoiceCloneAnalyzer
import com.example.data.model.VoiceProfile
import com.example.data.repository.VoiceRepository
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SpeechStudioScreen
import com.example.ui.screens.VoiceLabScreen
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

enum class StudioTab(val label: String) {
    STUDIO("Studio"),
    VOICE_LAB("Voice Lab"),
    HISTORY("History"),
    SETTINGS("Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    voiceRepository: VoiceRepository,
    synthesizerEngine: AudioSynthesizerEngine,
    cloneAnalyzer: VoiceCloneAnalyzer,
    playerController: AudioPlayerController
) {
    var currentTab by remember { mutableStateOf(StudioTab.STUDIO) }
    val voices by voiceRepository.voices.collectAsStateWithLifecycle()
    var selectedVoice by remember(voices) {
        mutableStateOf(voices.firstOrNull { it.id == "voice_jesa" } ?: voices.firstOrNull() ?: VoiceProfile(
            id = "voice_jesa",
            name = "Jesa",
            category = "Featured Neural",
            gender = "Female",
            accent = "Natural / Adaptive",
            description = "Hyper-expressive AI voice with advanced emotion & multi-tone vocal delivery.",
            avatarHex = 0xFF6366F1
        ))
    }

    // BackHandler to navigate back to STUDIO tab when inside other sub-screens
    BackHandler(enabled = currentTab != StudioTab.STUDIO) {
        currentTab = StudioTab.STUDIO
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = StudioBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(listOf(StudioPrimary, StudioSecondary))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ElevenVoice",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = StudioTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(StudioSecondary)
                                )
                            }
                            Text(
                                text = "AI TTS • Instant Clone Studio",
                                style = MaterialTheme.typography.labelSmall,
                                color = StudioTextMuted
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StudioSurfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = StudioSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Low Latency",
                                style = MaterialTheme.typography.labelSmall,
                                color = StudioSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StudioSurface,
                    scrolledContainerColor = StudioSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = StudioSurface,
                tonalElevation = 8.dp
            ) {
                StudioTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    StudioTab.STUDIO -> if (isSelected) Icons.Filled.GraphicEq else Icons.Outlined.GraphicEq
                                    StudioTab.VOICE_LAB -> if (isSelected) Icons.Filled.Fingerprint else Icons.Outlined.Fingerprint
                                    StudioTab.HISTORY -> if (isSelected) Icons.Filled.History else Icons.Outlined.History
                                    StudioTab.SETTINGS -> if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings
                                },
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = StudioPrimaryBright,
                            unselectedIconColor = StudioTextMuted,
                            unselectedTextColor = StudioTextMuted,
                            indicatorColor = StudioPrimary
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            label = "tab_fade",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tab ->
            when (tab) {
                StudioTab.STUDIO -> SpeechStudioScreen(
                    voiceRepository = voiceRepository,
                    synthesizerEngine = synthesizerEngine,
                    playerController = playerController,
                    voices = voices,
                    currentVoice = selectedVoice,
                    onVoiceSelect = { selectedVoice = it }
                )
                StudioTab.VOICE_LAB -> VoiceLabScreen(
                    voiceRepository = voiceRepository,
                    cloneAnalyzer = cloneAnalyzer,
                    synthesizerEngine = synthesizerEngine,
                    playerController = playerController,
                    onUseVoiceInStudio = { voice ->
                        selectedVoice = voice
                        currentTab = StudioTab.STUDIO
                    }
                )
                StudioTab.HISTORY -> HistoryScreen(
                    voiceRepository = voiceRepository,
                    playerController = playerController
                )
                StudioTab.SETTINGS -> SettingsScreen(
                    voiceRepository = voiceRepository
                )
            }
        }
    }
}
