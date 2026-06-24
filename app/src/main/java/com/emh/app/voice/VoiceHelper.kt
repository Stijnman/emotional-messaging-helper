package com.emh.app.voice

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.content.ContextCompat
import java.util.Locale
import java.util.UUID

/**
 * Text-to-speech (read replies aloud) and speech-to-text (voice input) helper.
 */
class VoiceHelper(private val context: Context) {

    private var tts: TextToSpeech? = null
    var isTtsReady: Boolean = false
        private set

    private var speechRecognizer: SpeechRecognizer? = null
    var isListening: Boolean = false
        private set

    var isSpeaking: Boolean = false
        private set

    var onListeningChanged: ((Boolean) -> Unit)? = null
    var onSpeakingChanged: ((Boolean) -> Unit)? = null
    var onPartialSpeech: ((String) -> Unit)? = null
    var onFinalSpeech: ((String) -> Unit)? = null
    var onSpeechError: ((String) -> Unit)? = null

    fun initTts(onReady: (Boolean) -> Unit = {}) {
        if (tts != null) {
            onReady(isTtsReady)
            return
        }
        tts = TextToSpeech(context) { status ->
            isTtsReady = status == TextToSpeech.SUCCESS
            if (isTtsReady) {
                tts?.language = Locale.getDefault()
            }
            onReady(isTtsReady)
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
                onSpeakingChanged?.invoke(true)
            }

            override fun onDone(utteranceId: String?) {
                isSpeaking = false
                onSpeakingChanged?.invoke(false)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                isSpeaking = false
                onSpeakingChanged?.invoke(false)
            }
        })
    }

    fun hasMicPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    fun speak(text: String): Boolean {
        if (!isTtsReady || text.isBlank()) return false
        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        return true
    }

    fun stopSpeaking() {
        tts?.stop()
        isSpeaking = false
        onSpeakingChanged?.invoke(false)
    }

    fun startListening(): Boolean {
        if (!hasMicPermission()) {
            onSpeechError?.invoke("Microphone permission required. Grant it in the EMH app.")
            return false
        }
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onSpeechError?.invoke("Speech recognition not available on this device.")
            return false
        }
        stopListening()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(recognitionListener)
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        isListening = true
        onListeningChanged?.invoke(true)
        speechRecognizer?.startListening(intent)
        return true
    }

    fun stopListening() {
        if (!isListening) return
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
        onListeningChanged?.invoke(false)
    }

    fun shutdown() {
        stopListening()
        stopSpeaking()
        tts?.shutdown()
        tts = null
        isTtsReady = false
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            isListening = false
            onListeningChanged?.invoke(false)
        }

        override fun onError(error: Int) {
            isListening = false
            onListeningChanged?.invoke(false)
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Speech client error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission denied"
                SpeechRecognizer.ERROR_NETWORK -> "Network error (offline recognition preferred)"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard"
                else -> "Voice input error ($error)"
            }
            onSpeechError?.invoke(message)
        }

        override fun onResults(results: Bundle?) {
            isListening = false
            onListeningChanged?.invoke(false)
            val text = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.trim()
            if (!text.isNullOrBlank()) onFinalSpeech?.invoke(text)
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val text = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.trim()
            if (!text.isNullOrBlank()) onPartialSpeech?.invoke(text)
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}