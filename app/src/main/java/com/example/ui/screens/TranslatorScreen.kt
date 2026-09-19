package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audio.SpeechState
import com.example.model.Language
import com.example.ui.components.ConversationBubble
import com.example.ui.components.DualMicControlBar
import com.example.ui.components.LanguageSelectorBar
import com.example.ui.components.LiveSpeechBanner
import com.example.ui.components.PhrasebookSheet
import com.example.ui.components.TextInputDialog
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.SpeakerAColor
import com.example.ui.theme.SpeakerBColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VioletLight
import com.example.viewmodel.TranslatorViewModel

@Composable
fun TranslatorScreen(
    viewModel: TranslatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val sourceLanguage by viewModel.sourceLanguage.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val isAutoSpeakEnabled by viewModel.isAutoSpeakEnabled.collectAsState()
    val livePreview by viewModel.livePreview.collectAsState()
    val conversationHistory by viewModel.conversationHistory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val onlyStarred by viewModel.onlyStarred.collectAsState()
    val isPhrasebookOpen by viewModel.isPhrasebookOpen.collectAsState()
    val isTextInputOpen by viewModel.isTextInputOpen.collectAsState()
    val textInputSpeaker by viewModel.textInputSpeaker.collectAsState()

    val speechState by viewModel.speechManager.speechState.collectAsState()
    val isSpeakingAudio by viewModel.ttsManager.isSpeaking.collectAsState()
    val currentlySpeakingText by viewModel.ttsManager.currentlySpeakingText.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var pendingSpeakerIdToRecord by remember { mutableIntStateOf(1) }

    val listState = rememberLazyListState()

    // Auto-scroll on new message
    LaunchedEffect(conversationHistory.size) {
        if (conversationHistory.isNotEmpty()) {
            listState.animateScrollToItem(conversationHistory.size - 1)
        }
    }

    // Audio permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening(pendingSpeakerIdToRecord)
        } else {
            Toast.makeText(
                context,
                "Permission microphone nécessaire pour la traduction vocale",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun handleMicClick(speakerId: Int) {
        val currentState = speechState
        if (currentState is SpeechState.Listening && currentState.speakerId == speakerId) {
            viewModel.stopListening()
            return
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.startListening(speakerId)
        } else {
            pendingSpeakerIdToRecord = speakerId
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val isListening = speechState is SpeechState.Listening
    val activeSpeakerId = (speechState as? SpeechState.Listening)?.speakerId ?: 0
    val rmsDb = (speechState as? SpeechState.Listening)?.rmsDb ?: 0f

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightDark),
        containerColor = MidnightDark,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .background(MidnightDark)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(CyanPrimary, VioletLight)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Logo",
                                tint = MidnightDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Tarjama AI",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Audio Real-Time • Hors-Ligne",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Search Toggle
                        IconButton(
                            onClick = { isSearchOpen = !isSearchOpen },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Rechercher",
                                tint = if (isSearchOpen) CyanNeon else TextMuted
                            )
                        }

                        // Clear history
                        if (conversationHistory.isNotEmpty()) {
                            IconButton(
                                onClick = { showClearDialog = true },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Effacer l'historique",
                                    tint = TextMuted
                                )
                            }
                        }
                    }
                }

                // Search Bar Expandable
                AnimatedVisibility(visible = isSearchOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Rechercher dans la conversation...", color = TextMuted, fontSize = 13.sp) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = TextMuted)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MidnightSurface,
                                unfocusedContainerColor = MidnightSurface,
                                focusedBorderColor = CyanNeon,
                                unfocusedBorderColor = MidnightBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            singleLine = true
                        )
                    }
                }

                // Language Selector Top Deck
                LanguageSelectorBar(
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    onSourceLanguageSelected = { viewModel.setSourceLanguage(it) },
                    onTargetLanguageSelected = { viewModel.setTargetLanguage(it) },
                    onSwapLanguages = { viewModel.swapLanguages() }
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Live Speech Banner shown when someone is speaking
                LiveSpeechBanner(
                    livePreview = livePreview,
                    isListening = isListening,
                    rmsDb = rmsDb
                )

                // Dual Big Mic Control Deck
                DualMicControlBar(
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    isListening = isListening,
                    activeSpeakerId = activeSpeakerId,
                    isAutoSpeakEnabled = isAutoSpeakEnabled,
                    onlyStarred = onlyStarred,
                    onMicClick = { handleMicClick(it) },
                    onAutoSpeakToggle = { viewModel.toggleAutoSpeak() },
                    onStarredToggle = { viewModel.toggleOnlyStarred() },
                    onOpenPhrasebook = { viewModel.setPhrasebookOpen(true) },
                    onOpenTextInput = { viewModel.openTextInput(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (conversationHistory.isEmpty()) {
                // Empty state greeting & guide
                EmptyConversationState(
                    sourceLang = sourceLanguage,
                    targetLang = targetLanguage,
                    onQuickPhrase = { phrase, speakerId ->
                        viewModel.processTranslation(phrase, speakerId)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Active Conversation List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("conversation_history_list"),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(conversationHistory, key = { it.id }) { item ->
                        val isThisItemSpeaking = isSpeakingAudio && currentlySpeakingText == item.translatedText
                        ConversationBubble(
                            item = item,
                            isSpeaking = isThisItemSpeaking,
                            onSpeakClick = {
                                viewModel.speakText(item.translatedText, item.targetLangCode)
                            },
                            onStarClick = { viewModel.toggleStar(item) },
                            onDeleteClick = { viewModel.deleteTranslation(item) }
                        )
                    }
                }
            }
        }
    }

    // Clear History Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = MidnightSurface,
            title = { Text("Effacer la conversation ?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text("Toutes les traductions audio enregistrées seront supprimées.", color = TextMuted) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    }
                ) {
                    Text("Effacer", color = RoseAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Annuler", color = TextWhite)
                }
            }
        )
    }

    // Offline Phrasebook Bottom Sheet
    PhrasebookSheet(
        isOpen = isPhrasebookOpen,
        sourceLanguage = sourceLanguage,
        targetLanguage = targetLanguage,
        onDismiss = { viewModel.setPhrasebookOpen(false) },
        onPhraseSelected = { phrase ->
            viewModel.processTranslation(phrase, 1)
        },
        onSpeakPhrase = { text, lang ->
            viewModel.ttsManager.speak(text, lang)
        }
    )

    // Direct Text Input Dialog
    TextInputDialog(
        isOpen = isTextInputOpen,
        initialSpeakerId = textInputSpeaker,
        sourceLanguage = sourceLanguage,
        targetLanguage = targetLanguage,
        onDismiss = { viewModel.closeTextInput() },
        onSubmit = { text, speakerId ->
            viewModel.processTranslation(text, speakerId)
        }
    )
}

@Composable
private fun EmptyConversationState(
    sourceLang: Language,
    targetLang: Language,
    onQuickPhrase: (phrase: String, speakerId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MidnightSurfaceVariant)
                .border(1.dp, MidnightBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🎙️", fontSize = 36.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Traduction Audio en Temps Réel",
            color = TextWhite,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Parlez naturellement sans connexion internet. L'IA traduit instantanément vos paroles et les prononce à haute voix.",
            color = TextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Suggestion Chips
        Text(
            text = "ESSAYER UNE PHRASE INSTANTANÉE :",
            color = CyanNeon,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuickSuggestionChip(
                flag = sourceLang.flag,
                speakerText = "Salam, labas 3lik? (السلام عليكم)",
                onClick = { onQuickPhrase("السلام عليكم، لاباس عليك؟", 1) }
            )

            QuickSuggestionChip(
                flag = targetLang.flag,
                speakerText = "Où se trouve l'hôtel s'il vous plaît ?",
                onClick = { onQuickPhrase("Où se trouve l'hôtel s'il vous plaît ?", 2) }
            )

            QuickSuggestionChip(
                flag = sourceLang.flag,
                speakerText = "Chhal hada? Ghali bzf (بشحال هادا)",
                onClick = { onQuickPhrase("بشحال هادا عافاك؟", 1) }
            )

            QuickSuggestionChip(
                flag = targetLang.flag,
                speakerText = "Combien ça coûte ?",
                onClick = { onQuickPhrase("Combien ça coûte ?", 2) }
            )
        }
    }
}

@Composable
private fun QuickSuggestionChip(
    flag: String,
    speakerText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(14.dp))
            .background(MidnightSurface)
            .border(1.dp, MidnightBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(flag, fontSize = 18.sp)
        Text(
            text = speakerText,
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "➜",
            color = CyanNeon,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
