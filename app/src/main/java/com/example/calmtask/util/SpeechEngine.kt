package com.example.calmtask.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class SpeechEngine(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var initialized = false

    suspend fun init(languageCode: String, speed: Float, pitch: Float): Boolean = suspendCancellableCoroutine { cont ->
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                initialized = true
                cont.resume(true)
            } else {
                cont.resume(false)
            }
        }
    }

    suspend fun speak(text: String, languageCode: String = "en", speed: Float = 1.0f, pitch: Float = 1.0f, utteranceId: String = "calmtask") {
        if (tts == null) return
        val locale = Locale.forLanguageTag(languageCode.take(2))
        if (tts?.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
            tts?.language = locale
        } else {
            tts?.language = Locale.ENGLISH
        }
        tts?.setSpeechRate(speed)
        tts?.setPitch(pitch)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
