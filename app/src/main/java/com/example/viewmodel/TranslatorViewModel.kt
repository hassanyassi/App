package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechManager
import com.example.audio.SpeechState
import com.example.audio.TextToSpeechManager
import com.example.data.db.AppDatabase
import com.example.data.db.TranslationEntity
import com.example.engine.OfflineTranslationEngine
import com.example.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LiveTranslationPreview(
    val speakerId: Int = 1,
    val sourceLanguage: Language = Language.DARIJA,
    val targetLanguage: Language = Language.FRENCH,
    val rawSpokenText: String = "",
    val liveTranslatedText: String = "",
    val isActive: Boolean = false
)

class TranslatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.translationDao()

    val speechManager = SpeechManager(application)
    val ttsManager = TextToSpeechManager(application)

    // Language selection
    private val _sourceLanguage = MutableStateFlow(Language.DARIJA)
    val sourceLanguage: StateFlow<Language> = _sourceLanguage.asStateFlow()

    private val _targetLanguage = MutableStateFlow(Language.FRENCH)
    val targetLanguage: StateFlow<Language> = _targetLanguage.asStateFlow()

    // Auto-Speak feature ("audio traduction real time")
    private val _isAutoSpeakEnabled = MutableStateFlow(true)
    val isAutoSpeakEnabled: StateFlow<Boolean> = _isAutoSpeakEnabled.asStateFlow()

    // Real-time live speech preview
    private val _livePreview = MutableStateFlow(LiveTranslationPreview())
    val livePreview: StateFlow<LiveTranslationPreview> = _livePreview.asStateFlow()

    // Search and filter in history
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _onlyStarred = MutableStateFlow(false)
    val onlyStarred: StateFlow<Boolean> = _onlyStarred.asStateFlow()

    // Bottom sheet dialogs
    private val _isPhrasebookOpen = MutableStateFlow(false)
    val isPhrasebookOpen: StateFlow<Boolean> = _isPhrasebookOpen.asStateFlow()

    private val _isTextInputOpen = MutableStateFlow(false)
    val isTextInputOpen: StateFlow<Boolean> = _isTextInputOpen.asStateFlow()

    private val _textInputSpeaker = MutableStateFlow(1)
    val textInputSpeaker: StateFlow<Int> = _textInputSpeaker.asStateFlow()

    // Conversation history stream
    val conversationHistory: StateFlow<List<TranslationEntity>> = combine(
        dao.getAllTranslations(),
        _searchQuery,
        _onlyStarred
    ) { list, query, starredOnly ->
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.sourceText.contains(query, ignoreCase = true) ||
                item.translatedText.contains(query, ignoreCase = true)
            val matchesStar = !starredOnly || item.isStarred
            matchesQuery && matchesStar
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Wire SpeechManager callbacks
        speechManager.onSpeechPartialResult = { speakerId, lang, partialText ->
            val fromLang = if (speakerId == 1) _sourceLanguage.value else _targetLanguage.value
            val toLang = if (speakerId == 1) _targetLanguage.value else _sourceLanguage.value

            val liveTranslated = OfflineTranslationEngine.translate(partialText, fromLang, toLang)
            _livePreview.value = LiveTranslationPreview(
                speakerId = speakerId,
                sourceLanguage = fromLang,
                targetLanguage = toLang,
                rawSpokenText = partialText,
                liveTranslatedText = liveTranslated,
                isActive = true
            )
        }

        speechManager.onSpeechFinalResult = { speakerId, lang, finalText ->
            processTranslation(finalText, speakerId)
            _livePreview.value = LiveTranslationPreview(isActive = false)
        }
    }

    fun swapLanguages() {
        val temp = _sourceLanguage.value
        _sourceLanguage.value = _targetLanguage.value
        _targetLanguage.value = temp
    }

    fun setSourceLanguage(language: Language) {
        if (language == _targetLanguage.value) {
            _targetLanguage.value = _sourceLanguage.value
        }
        _sourceLanguage.value = language
    }

    fun setTargetLanguage(language: Language) {
        if (language == _sourceLanguage.value) {
            _sourceLanguage.value = _targetLanguage.value
        }
        _targetLanguage.value = language
    }

    fun toggleAutoSpeak() {
        _isAutoSpeakEnabled.value = !_isAutoSpeakEnabled.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleOnlyStarred() {
        _onlyStarred.value = !_onlyStarred.value
    }

    fun setPhrasebookOpen(open: Boolean) {
        _isPhrasebookOpen.value = open
    }

    fun openTextInput(speakerId: Int) {
        _textInputSpeaker.value = speakerId
        _isTextInputOpen.value = true
    }

    fun closeTextInput() {
        _isTextInputOpen.value = false
    }

    fun startListening(speakerId: Int) {
        ttsManager.stop()
        val lang = if (speakerId == 1) _sourceLanguage.value else _targetLanguage.value
        _livePreview.value = LiveTranslationPreview(
            speakerId = speakerId,
            sourceLanguage = lang,
            targetLanguage = if (speakerId == 1) _targetLanguage.value else _sourceLanguage.value,
            isActive = true
        )
        speechManager.startListening(speakerId, lang)
    }

    fun stopListening() {
        speechManager.stopListening()
    }

    fun processTranslation(text: String, speakerId: Int) {
        if (text.isBlank()) return

        val fromLang = if (speakerId == 1) _sourceLanguage.value else _targetLanguage.value
        val toLang = if (speakerId == 1) _targetLanguage.value else _sourceLanguage.value

        viewModelScope.launch {
            val translated = OfflineTranslationEngine.translate(text, fromLang, toLang)

            val entity = TranslationEntity(
                sourceLangCode = fromLang.code,
                targetLangCode = toLang.code,
                sourceText = text,
                translatedText = translated,
                speakerId = speakerId
            )
            dao.insertTranslation(entity)

            // Real-time audio speak translation
            if (_isAutoSpeakEnabled.value) {
                ttsManager.speak(translated, toLang)
            }
        }
    }

    fun speakText(text: String, langCode: String) {
        val language = Language.fromCode(langCode)
        ttsManager.speak(text, language)
    }

    fun toggleStar(entity: TranslationEntity) {
        viewModelScope.launch {
            dao.toggleStar(entity.id)
        }
    }

    fun deleteTranslation(entity: TranslationEntity) {
        viewModelScope.launch {
            dao.deleteTranslation(entity)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
        ttsManager.shutdown()
    }
}
