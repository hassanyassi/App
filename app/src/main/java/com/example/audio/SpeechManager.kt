package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface SpeechState {
    data object Idle : SpeechState
    data class Listening(
        val speakerId: Int,
        val language: Language,
        val rmsDb: Float = 0f,
        val partialText: String = ""
    ) : SpeechState
    data class Processing(
        val speakerId: Int,
        val recognizedText: String
    ) : SpeechState
    data class Error(val message: String) : SpeechState
}

class SpeechManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val _speechState = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

    private var currentSpeakerId: Int = 1
    private var currentLanguage: Language = Language.DARIJA

    var onSpeechFinalResult: ((speakerId: Int, language: Language, text: String) -> Unit)? = null
    var onSpeechPartialResult: ((speakerId: Int, language: Language, partialText: String) -> Unit)? = null

    init {
        initRecognizer()
    }

    private fun initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }
        }
    }

    private fun createListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _speechState.value = SpeechState.Listening(
                speakerId = currentSpeakerId,
                language = currentLanguage,
                rmsDb = 0f,
                partialText = ""
            )
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {
            val currentState = _speechState.value
            if (currentState is SpeechState.Listening) {
                _speechState.value = currentState.copy(
                    rmsDb = (rmsdB.coerceAtLeast(0f) / 10f).coerceIn(0f, 1f)
                )
            }
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            val currentState = _speechState.value
            if (currentState is SpeechState.Listening) {
                _speechState.value = SpeechState.Processing(
                    speakerId = currentSpeakerId,
                    recognizedText = currentState.partialText
                )
            }
        }

        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Erreur audio du microphone"
                SpeechRecognizer.ERROR_CLIENT -> "Erreur client"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission microphone requise"
                SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Vérification hors-ligne"
                SpeechRecognizer.ERROR_NO_MATCH -> "Aucun mot détecté. Rapprochez le micro et parlez distinctement."
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconnaissance vocale occupée"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Temps d'attente dépassé (aucun son)"
                else -> "Erreur d'écoute vocale ($error)"
            }
            _speechState.value = SpeechState.Error(errorMessage)
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognizedText = matches?.firstOrNull()?.trim() ?: ""

            if (recognizedText.isNotBlank()) {
                _speechState.value = SpeechState.Processing(currentSpeakerId, recognizedText)
                onSpeechFinalResult?.invoke(currentSpeakerId, currentLanguage, recognizedText)
            }
            _speechState.value = SpeechState.Idle
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val partial = matches?.firstOrNull()?.trim() ?: ""
            if (partial.isNotBlank()) {
                val currentState = _speechState.value
                if (currentState is SpeechState.Listening) {
                    _speechState.value = currentState.copy(partialText = partial)
                }
                onSpeechPartialResult?.invoke(currentSpeakerId, currentLanguage, partial)
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    fun startListening(speakerId: Int, language: Language) {
        currentSpeakerId = speakerId
        currentLanguage = language

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _speechState.value = SpeechState.Error(
                "Le service de reconnaissance vocale n'est pas disponible sur cet appareil. Utilisez le clavier ou installez le pack vocal."
            )
            return
        }

        try {
            if (speechRecognizer == null) {
                initRecognizer()
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.speechLocale)
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            _speechState.value = SpeechState.Listening(
                speakerId = speakerId,
                language = language,
                rmsDb = 0.1f,
                partialText = ""
            )

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _speechState.value = SpeechState.Error("Impossible de démarrer l'écoute : ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
    }

    fun cancel() {
        try {
            speechRecognizer?.cancel()
        } catch (_: Exception) {}
        _speechState.value = SpeechState.Idle
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
    }
}
