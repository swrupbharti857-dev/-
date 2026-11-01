package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AcousticFingerprint
import com.example.data.model.GenerationRecord
import com.example.data.model.LatencySettings
import com.example.data.model.SpeakingTone
import com.example.data.model.SynthesisModel
import com.example.data.model.VoiceEmotion
import com.example.data.model.VoiceProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class VoiceRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("eleven_voice_prefs", Context.MODE_PRIVATE)

    val availableModels = listOf(
        SynthesisModel(
            id = "eleven_multilingual_v2",
            name = "Multilingual v2 (Hi-Fi)",
            description = "State of the art high-fidelity emotional delivery in 29+ languages.",
            fidelityScore = "99.4%",
            latencyLabel = "~280ms"
        ),
        SynthesisModel(
            id = "eleven_turbo_v2_5",
            name = "Turbo v2.5 (Low Latency)",
            description = "High quality optimized for fast interactive conversational agents.",
            fidelityScore = "96.8%",
            latencyLabel = "~120ms"
        ),
        SynthesisModel(
            id = "eleven_flash_v2",
            name = "Flash v2 (Ultra Realtime)",
            description = "Sub-100ms latency stream designed for real-time live synthesis.",
            fidelityScore = "94.2%",
            latencyLabel = "~75ms"
        ),
        SynthesisModel(
            id = "eleven_english_v1",
            name = "English v1 (Classic)",
            description = "Original legacy acoustic profile with natural pacing.",
            fidelityScore = "93.0%",
            latencyLabel = "~220ms"
        )
    )

    private val defaultPresetVoices = listOf(
        VoiceProfile(
            id = "voice_jesa",
            name = "Jesa",
            category = "Featured Neural",
            gender = "Female",
            accent = "Natural / Adaptive",
            description = "Hyper-expressive AI voice with advanced emotion & multi-tone vocal delivery.",
            avatarHex = 0xFF6366F1,
            isCloned = false,
            sampleText = "Hello! I am Jesa. You can tune my emotions from joyful to whisper in real time.",
            stability = 0.65f,
            similarityBoost = 0.90f,
            styleExaggeration = 0.35f,
            speakerBoost = true,
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 218f,
                formantF1 = 540f,
                formantF2 = 1750f,
                harmonicNoiseRatioDb = 22.4f,
                dynamicRangeDb = 38.6f,
                durationSeconds = 60f
            )
        ),
        VoiceProfile(
            id = "voice_rachel",
            name = "Rachel",
            category = "Preset",
            gender = "Female",
            accent = "American (Calm)",
            description = "Warm, soothing, clear voice ideal for narration and meditation.",
            avatarHex = 0xFFEC4899,
            isCloned = false,
            stability = 0.80f,
            similarityBoost = 0.85f,
            styleExaggeration = 0.15f
        ),
        VoiceProfile(
            id = "voice_adam",
            name = "Adam",
            category = "Preset",
            gender = "Male",
            accent = "American (Deep)",
            description = "Deep baritone, authoritative and engaging for documentaries.",
            avatarHex = 0xFF0284C7,
            isCloned = false,
            stability = 0.70f,
            similarityBoost = 0.88f,
            styleExaggeration = 0.25f
        ),
        VoiceProfile(
            id = "voice_antoni",
            name = "Antoni",
            category = "Preset",
            gender = "Male",
            accent = "American (Crisp)",
            description = "Energetic, dynamic voiceover specialist for tech and podcasts.",
            avatarHex = 0xFF10B981,
            isCloned = false,
            stability = 0.72f,
            similarityBoost = 0.82f,
            styleExaggeration = 0.20f
        ),
        VoiceProfile(
            id = "voice_bella",
            name = "Bella",
            category = "Preset",
            gender = "Female",
            accent = "American (Emotive)",
            description = "Soft, expressive storytelling voice with gentle warmth.",
            avatarHex = 0xFFA855F7,
            isCloned = false,
            stability = 0.68f,
            similarityBoost = 0.86f,
            styleExaggeration = 0.30f
        ),
        VoiceProfile(
            id = "voice_josh",
            name = "Josh",
            category = "Preset",
            gender = "Male",
            accent = "American (Casual)",
            description = "Young, conversational, relatable and friendly.",
            avatarHex = 0xFFF59E0B,
            isCloned = false,
            stability = 0.75f,
            similarityBoost = 0.80f,
            styleExaggeration = 0.18f
        ),
        VoiceProfile(
            id = "voice_freya",
            name = "Freya",
            category = "Preset",
            gender = "Female",
            accent = "British (Cinematic)",
            description = "Aristocratic, resonant, dramatic narrator with poetic cadence.",
            avatarHex = 0xFF8B5CF6,
            isCloned = false,
            stability = 0.70f,
            similarityBoost = 0.90f,
            styleExaggeration = 0.40f
        ),
        // ===== 5 भारतीय हिंदी पुरुष मॉडल (5 Indian Hindi Male Models) =====
        VoiceProfile(
            id = "voice_aarav",
            name = "Aarav (आरव)",
            nativeName = "आरव",
            category = "Hindi Neural",
            gender = "Male",
            accent = "Hindi (Deep Narrative)",
            description = "शांत और गहरा कथावाचक - ऑडियोबुक्स, कहानियों और गंभीर पॉडकास्ट के लिए।",
            avatarHex = 0xFF0284C7,
            isCloned = false,
            sampleText = "नमस्ते! मैं आरव हूँ। मेरी आवाज़ में आप कहानियों और गंभीर पॉडकास्ट की शानदार रिकॉर्डिंग तैयार कर सकते हैं।",
            stability = 0.78f,
            similarityBoost = 0.92f,
            styleExaggeration = 0.22f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 125f,
                formantF1 = 490f,
                formantF2 = 1520f,
                harmonicNoiseRatioDb = 24.2f,
                dynamicRangeDb = 42.0f,
                durationSeconds = 60f
            )
        ),
        VoiceProfile(
            id = "voice_kabir",
            name = "Kabir (कबीर)",
            nativeName = "कबीर",
            category = "Hindi Neural",
            gender = "Male",
            accent = "Hindi (Energetic Podcast)",
            description = "उत्साही और गतिशील - न्यूज़, पॉडकास्ट, स्पोर्ट्स और विज्ञापनों के लिए।",
            avatarHex = 0xFFD97706,
            isCloned = false,
            sampleText = "नमस्ते दोस्तों! कबीर के इस विशेष पॉडकास्ट में आप सबका स्वागत है, चलिए शुरू करते हैं!",
            stability = 0.70f,
            similarityBoost = 0.88f,
            styleExaggeration = 0.30f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 145f,
                formantF1 = 510f,
                formantF2 = 1600f,
                harmonicNoiseRatioDb = 22.0f,
                dynamicRangeDb = 38.5f,
                durationSeconds = 60f
            )
        ),
        VoiceProfile(
            id = "voice_rohan",
            name = "Rohan (रोहन)",
            nativeName = "रोहन",
            category = "Hindi Neural",
            gender = "Male",
            accent = "Hindi (Conversational Youth)",
            description = "युवा, दोस्ताना और आधुनिक हिंग्लिश आवाज़ - यूट्यूब और कैज़ुअल वार्तालाप।",
            avatarHex = 0xFF10B981,
            isCloned = false,
            sampleText = "अरे भाई सुनो! आज हम एक बहुत ही कमाल की चीज़ देखने वाले हैं, बिल्कुल मिस मत करना।",
            stability = 0.72f,
            similarityBoost = 0.85f,
            styleExaggeration = 0.25f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 155f,
                formantF1 = 525f,
                formantF2 = 1640f,
                harmonicNoiseRatioDb = 21.5f,
                dynamicRangeDb = 36.0f,
                durationSeconds = 60f
            )
        ),
        VoiceProfile(
            id = "voice_vikram",
            name = "Vikram (विक्रम)",
            nativeName = "विक्रम",
            category = "Hindi Neural",
            gender = "Male",
            accent = "Hindi (Authoritative Cinematic)",
            description = "गंभीर, भारी और प्रभावशाली - डॉक्यूमेंट्री, ट्रेलर और शक्तिशाली कथन।",
            avatarHex = 0xFF7C3AED,
            isCloned = false,
            sampleText = "इतिहास गवाह है, जब भी साहस ने कदम आगे बढ़ाया, हर असंभव लक्ष्य संभव बन गया।",
            stability = 0.82f,
            similarityBoost = 0.94f,
            styleExaggeration = 0.35f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 110f,
                formantF1 = 460f,
                formantF2 = 1460f,
                harmonicNoiseRatioDb = 25.8f,
                dynamicRangeDb = 45.0f,
                durationSeconds = 60f
            )
        ),
        VoiceProfile(
            id = "voice_dev",
            name = "Dev (देव)",
            nativeName = "देव",
            category = "Hindi Neural",
            gender = "Male",
            accent = "Hindi (Poetic Shayar)",
            description = "भावुक और शायरी अंदाज़ - कोमल, मद्धम, रोमैंटिक और दिलकश आवाज़।",
            avatarHex = 0xFFE11D48,
            isCloned = false,
            sampleText = "कुछ तो बात है इन ख़ामोश हवाओं में, जो हर धड़कन को एक नई कहानी सुना जाती हैं।",
            stability = 0.68f,
            similarityBoost = 0.90f,
            styleExaggeration = 0.40f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 135f,
                formantF1 = 500f,
                formantF2 = 1560f,
                harmonicNoiseRatioDb = 23.0f,
                dynamicRangeDb = 39.0f,
                durationSeconds = 60f
            )
        ),
        // ===== 5 भारतीय हिंदी महिला मॉडल (5 Indian Hindi Female Models) =====
        VoiceProfile(
            id = "voice_ananya",
            name = "Ananya (अनन्या)",
            nativeName = "अनन्या",
            category = "Hindi Neural",
            gender = "Female",
            accent = "Hindi (Clear Corporate)",
            description = "स्पष्ट, मधुर और पेशेवर आवाज़ - कॉर्पोरेट प्रस्तुति, ई-लर्निंग और समाचार।",
            avatarHex = 0xFFEC4899,
            isCloned = false,
            sampleText = "नमस्कार! मैं अनन्या हूँ। आज के इस डिजिटल सत्र में आपका हार्दिक स्वागत और अभिनंदन है।",
            stability = 0.80f,
            similarityBoost = 0.92f,
            styleExaggeration = 0.18f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 210f,
                formantF1 = 540f,
                formantF2 = 1760f,
                harmonicNoiseRatioDb = 24.0f,
                dynamicRangeDb = 38.0f,
                durationSeconds = 60f
            )
        ),
        VoiceProfile(
            id = "voice_priya",
            name = "Priya (प्रिया)",
            nativeName = "प्रिया",
            category = "Hindi Neural",
            gender = "Female",
            accent = "Hindi (Friendly Assistant)",
            description = "मिलनसार और खुशमिज़ाज - वर्चुअल असिस्टेंट, कस्टमर सपोर्ट और चैटबॉट।",
            avatarHex = 0xFF06B6D4,
            isCloned = false,
            sampleText = "नमस्ते! मैं प्रिया हूँ, आपकी स्मार्ट एआई सहायिका। आज मैं आपकी क्या मदद कर सकती हूँ?",
            stability = 0.74f,
            similarityBoost = 0.88f,
            styleExaggeration = 0.22f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 230f,
                formantF1 = 560f,
                formantF2 = 1820f,
                harmonicNoiseRatioDb = 22.8f,
                dynamicRangeDb = 36.5f,
                durationSeconds = 60f
            )
        ),
        VoiceProfile(
            id = "voice_kavya",
            name = "Kavya (काव्या)",
            nativeName = "काव्या",
            category = "Hindi Neural",
            gender = "Female",
            accent = "Hindi (Expressive Storyteller)",
            description = "भावपूर्ण और भावुक कथावाचिका - उपन्यासों, किस्सों और ऑडियो ड्रामा के लिए।",
            avatarHex = 0xFFA855F7,
            isCloned = false,
            sampleText = "उस शाम गाँव की पगडंडियों पर सन्नाटा था, और दूर मंदिर से शंख की गूंज सुनाई दे रही थी।",
            stability = 0.68f,
            similarityBoost = 0.94f,
            styleExaggeration = 0.35f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 220f,
                formantF1 = 550f,
                formantF2 = 1790f,
                harmonicNoiseRatioDb = 23.5f,
                dynamicRangeDb = 40.0f,
                durationSeconds = 60f
            )
        ),
        VoiceProfile(
            id = "voice_diya",
            name = "Diya (दिया)",
            nativeName = "दिया",
            category = "Hindi Neural",
            gender = "Female",
            accent = "Hindi (Upbeat Commercial)",
            description = "उर्जावान और तेज़ अंदाज़ - सोशल मीडिया रील्स, विज्ञापन और यूथ वॉयसओवर।",
            avatarHex = 0xFFF59E0B,
            isCloned = false,
            sampleText = "हेलो दोस्तों! साल की सबसे बड़ी सेल लाइव हो चुकी है, अपने मनपसंद प्रोडक्ट्स तुरंत ऑर्डर करें!",
            stability = 0.70f,
            similarityBoost = 0.86f,
            styleExaggeration = 0.32f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 245f,
                formantF1 = 580f,
                formantF2 = 1880f,
                harmonicNoiseRatioDb = 21.0f,
                dynamicRangeDb = 37.0f,
                durationSeconds = 60f
            )
        ),
        VoiceProfile(
            id = "voice_meera",
            name = "Meera (मीरा)",
            nativeName = "मीरा",
            category = "Hindi Neural",
            gender = "Female",
            accent = "Hindi (Soothing Meditative)",
            description = "शांत, ध्यानमग्न, कोमल और सुकून देने वाली आध्यात्मिक आवाज़।",
            avatarHex = 0xFF14B8A6,
            isCloned = false,
            sampleText = "अपनी आँखें धीरे से बंद करें, एक गहरी साँस लें और मन की सारी चिंताओं को विदा कर दें।",
            stability = 0.85f,
            similarityBoost = 0.95f,
            styleExaggeration = 0.15f,
            speakerBoost = true,
            languageCode = "hi-IN",
            acousticFingerprint = AcousticFingerprint(
                fundamentalPitchHz = 195f,
                formantF1 = 520f,
                formantF2 = 1710f,
                harmonicNoiseRatioDb = 26.0f,
                dynamicRangeDb = 34.0f,
                durationSeconds = 60f
            )
        )
    )

    private val _voices = MutableStateFlow<List<VoiceProfile>>(emptyList())
    val voices: StateFlow<List<VoiceProfile>> = _voices.asStateFlow()

    private val _history = MutableStateFlow<List<GenerationRecord>>(emptyList())
    val history: StateFlow<List<GenerationRecord>> = _history.asStateFlow()

    init {
        loadVoices()
        loadHistory()
    }

    private fun loadVoices() {
        val clonedJson = prefs.getString("cloned_voices_json", null)
        val customVoices = mutableListOf<VoiceProfile>()
        if (clonedJson != null) {
            try {
                val array = JSONArray(clonedJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    customVoices.add(
                        VoiceProfile(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            category = "Instant Clone",
                            gender = obj.optString("gender", "Custom"),
                            accent = obj.optString("accent", "Custom Clone"),
                            description = obj.optString("description", "Acoustic clone generated from voice sample"),
                            avatarHex = obj.optLong("avatarHex", 0xFF06B6D4),
                            isCloned = true,
                            stability = obj.optDouble("stability", 0.75).toFloat(),
                            similarityBoost = obj.optDouble("similarityBoost", 0.92).toFloat(),
                            styleExaggeration = obj.optDouble("styleExaggeration", 0.25).toFloat(),
                            speakerBoost = obj.optBoolean("speakerBoost", true),
                            acousticFingerprint = AcousticFingerprint(
                                fundamentalPitchHz = obj.optDouble("pitchHz", 185.0).toFloat(),
                                formantF1 = obj.optDouble("formantF1", 520.0).toFloat(),
                                formantF2 = obj.optDouble("formantF2", 1680.0).toFloat(),
                                harmonicNoiseRatioDb = obj.optDouble("hnrDb", 21.0).toFloat(),
                                dynamicRangeDb = obj.optDouble("dynDb", 35.0).toFloat(),
                                durationSeconds = obj.optDouble("duration", 8.5).toFloat()
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Seed one initial realistic cloned voice for immediate testing
            customVoices.add(
                VoiceProfile(
                    id = "cloned_sample_studio",
                    name = "Studio Clone (Demo)",
                    category = "Instant Clone",
                    gender = "Dynamic",
                    accent = "Studio Acoustic Match",
                    description = "Instant voice clone with 98% spectral fidelity and formant matching.",
                    avatarHex = 0xFF06B6D4,
                    isCloned = true,
                    stability = 0.82f,
                    similarityBoost = 0.94f,
                    styleExaggeration = 0.20f,
                    speakerBoost = true,
                    acousticFingerprint = AcousticFingerprint(
                        fundamentalPitchHz = 195f,
                        formantF1 = 510f,
                        formantF2 = 1720f,
                        harmonicNoiseRatioDb = 23.5f,
                        dynamicRangeDb = 41.0f,
                        durationSeconds = 12.0f
                    )
                )
            )
        }

        _voices.value = defaultPresetVoices + customVoices
    }

    fun saveClonedVoice(voice: VoiceProfile) {
        val current = _voices.value.toMutableList()
        current.add(voice)
        _voices.value = current

        // Persist cloned voices
        val clonedList = current.filter { it.isCloned }
        val array = JSONArray()
        for (v in clonedList) {
            val obj = JSONObject()
            obj.put("id", v.id)
            obj.put("name", v.name)
            obj.put("gender", v.gender)
            obj.put("accent", v.accent)
            obj.put("description", v.description)
            obj.put("avatarHex", v.avatarHex)
            obj.put("stability", v.stability.toDouble())
            obj.put("similarityBoost", v.similarityBoost.toDouble())
            obj.put("styleExaggeration", v.styleExaggeration.toDouble())
            obj.put("speakerBoost", v.speakerBoost)
            v.acousticFingerprint?.let {
                obj.put("pitchHz", it.fundamentalPitchHz.toDouble())
                obj.put("formantF1", it.formantF1.toDouble())
                obj.put("formantF2", it.formantF2.toDouble())
                obj.put("hnrDb", it.harmonicNoiseRatioDb.toDouble())
                obj.put("dynDb", it.dynamicRangeDb.toDouble())
                obj.put("duration", it.durationSeconds.toDouble())
            }
            array.put(obj)
        }
        prefs.edit().putString("cloned_voices_json", array.toString()).apply()
    }

    fun deleteClonedVoice(voiceId: String) {
        val current = _voices.value.filterNot { it.id == voiceId && it.isCloned }
        _voices.value = current

        val clonedList = current.filter { it.isCloned }
        val array = JSONArray()
        for (v in clonedList) {
            val obj = JSONObject()
            obj.put("id", v.id)
            obj.put("name", v.name)
            obj.put("gender", v.gender)
            obj.put("accent", v.accent)
            obj.put("description", v.description)
            obj.put("avatarHex", v.avatarHex)
            obj.put("stability", v.stability.toDouble())
            obj.put("similarityBoost", v.similarityBoost.toDouble())
            obj.put("styleExaggeration", v.styleExaggeration.toDouble())
            obj.put("speakerBoost", v.speakerBoost)
            array.put(obj)
        }
        prefs.edit().putString("cloned_voices_json", array.toString()).apply()
    }

    private fun loadHistory() {
        val historyJson = prefs.getString("generation_history_json", null)
        val list = mutableListOf<GenerationRecord>()
        if (historyJson != null) {
            try {
                val array = JSONArray(historyJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        GenerationRecord(
                            id = obj.getString("id"),
                            text = obj.getString("text"),
                            voiceId = obj.getString("voiceId"),
                            voiceName = obj.getString("voiceName"),
                            emotion = try {
                                VoiceEmotion.valueOf(obj.getString("emotion"))
                            } catch (e: Exception) {
                                VoiceEmotion.NEUTRAL
                            },
                            tone = SpeakingTone(
                                emotionalIntensity = obj.optDouble("intensity", 0.7).toFloat(),
                                warmth = obj.optDouble("warmth", 0.5).toFloat(),
                                pitchModulation = obj.optDouble("pitchMod", 0.6).toFloat(),
                                cadence = obj.optDouble("cadence", 1.0).toFloat(),
                                breathiness = obj.optDouble("breath", 0.2).toFloat()
                            ),
                            latencyMs = obj.getLong("latencyMs"),
                            durationMs = obj.getLong("durationMs"),
                            timestamp = obj.getLong("timestamp"),
                            audioPath = obj.optString("audioPath", null),
                            characterCount = obj.getInt("characterCount")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        _history.value = list
    }

    fun addHistoryRecord(record: GenerationRecord) {
        val list = _history.value.toMutableList()
        list.add(0, record)
        if (list.size > 50) {
            list.removeAt(list.size - 1)
        }
        _history.value = list

        val array = JSONArray()
        for (r in list) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("text", r.text)
            obj.put("voiceId", r.voiceId)
            obj.put("voiceName", r.voiceName)
            obj.put("emotion", r.emotion.name)
            obj.put("intensity", r.tone.emotionalIntensity.toDouble())
            obj.put("warmth", r.tone.warmth.toDouble())
            obj.put("pitchMod", r.tone.pitchModulation.toDouble())
            obj.put("cadence", r.tone.cadence.toDouble())
            obj.put("breath", r.tone.breathiness.toDouble())
            obj.put("latencyMs", r.latencyMs)
            obj.put("durationMs", r.durationMs)
            obj.put("timestamp", r.timestamp)
            obj.put("audioPath", r.audioPath ?: "")
            obj.put("characterCount", r.characterCount)
            array.put(obj)
        }
        prefs.edit().putString("generation_history_json", array.toString()).apply()
    }

    fun deleteHistoryRecord(id: String) {
        val list = _history.value.filterNot { it.id == id }
        _history.value = list
        // persist
        val array = JSONArray()
        for (r in list) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("text", r.text)
            obj.put("voiceId", r.voiceId)
            obj.put("voiceName", r.voiceName)
            obj.put("emotion", r.emotion.name)
            obj.put("intensity", r.tone.emotionalIntensity.toDouble())
            obj.put("warmth", r.tone.warmth.toDouble())
            obj.put("pitchMod", r.tone.pitchModulation.toDouble())
            obj.put("cadence", r.tone.cadence.toDouble())
            obj.put("breath", r.tone.breathiness.toDouble())
            obj.put("latencyMs", r.latencyMs)
            obj.put("durationMs", r.durationMs)
            obj.put("timestamp", r.timestamp)
            obj.put("audioPath", r.audioPath ?: "")
            obj.put("characterCount", r.characterCount)
            array.put(obj)
        }
        prefs.edit().putString("generation_history_json", array.toString()).apply()
    }

    fun clearAllHistory() {
        _history.value = emptyList()
        prefs.edit().remove("generation_history_json").apply()
    }

    // ElevenLabs API Key setting
    fun getApiKey(): String = prefs.getString("eleven_api_key", "") ?: ""
    fun setApiKey(key: String) = prefs.edit().putString("eleven_api_key", key.trim()).apply()

    // Model selection
    fun getSelectedModelId(): String = prefs.getString("selected_model_id", "eleven_multilingual_v2") ?: "eleven_multilingual_v2"
    fun setSelectedModelId(id: String) = prefs.edit().putString("selected_model_id", id).apply()

    // Latency defaults
    fun getLatencySettings(): LatencySettings {
        val level = prefs.getInt("latency_level", 1)
        val sampleRate = prefs.getInt("sample_rate", 44)
        return LatencySettings(optimizationLevel = level, sampleRateKhz = sampleRate)
    }

    fun saveLatencySettings(settings: LatencySettings) {
        prefs.edit()
            .putInt("latency_level", settings.optimizationLevel)
            .putInt("sample_rate", settings.sampleRateKhz)
            .apply()
    }
}
