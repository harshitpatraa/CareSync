package com.caresync.app.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.caresync.app.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isReady = false
    private var pendingText: String? = null
    private var pendingLanguage: AppLanguage = AppLanguage.ENGLISH

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private fun ensureInitialized() {
        if (tts != null) return
        tts = TextToSpeech(context) { status ->
            isReady = status == TextToSpeech.SUCCESS
            if (isReady) {
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
                pendingText?.let { text ->
                    val lang = pendingLanguage
                    pendingText = null
                    speakInternal(text, lang)
                }
            }
        }
    }

    fun speak(text: String, language: AppLanguage) {
        val clean = text
            .replace(Regex("[*#`•]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (clean.isBlank()) return

        if (tts == null) {
            pendingText = clean
            pendingLanguage = language
            ensureInitialized()
            return
        }
        if (!isReady) {
            pendingText = clean
            pendingLanguage = language
            return
        }
        speakInternal(clean, language)
    }

    private fun speakInternal(text: String, language: AppLanguage) {
        val engine = tts ?: return
        val locale = when (language) {
            AppLanguage.ENGLISH -> Locale.ENGLISH
            AppLanguage.HINDI -> Locale.forLanguageTag("hi-IN")
            AppLanguage.MARATHI -> Locale.forLanguageTag("mr-IN")
            AppLanguage.TAMIL -> Locale.forLanguageTag("ta-IN")
            AppLanguage.BENGALI -> Locale.forLanguageTag("bn-IN")
        }
        val langRes = engine.setLanguage(locale)
        if (langRes == TextToSpeech.LANG_MISSING_DATA || langRes == TextToSpeech.LANG_NOT_SUPPORTED) {
            engine.setLanguage(Locale.ENGLISH)
        }
        _isSpeaking.value = true
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "caresync_tts_${System.currentTimeMillis()}")
    }

    fun stop() {
        pendingText = null
        tts?.stop()
        _isSpeaking.value = false
    }
}
