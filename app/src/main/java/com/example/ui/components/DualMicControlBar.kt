package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.SpeakerAColor
import com.example.ui.theme.SpeakerBColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VioletLight
import com.example.ui.theme.VioletPrimary

@Composable
fun DualMicControlBar(
    sourceLanguage: Language,
    targetLanguage: Language,
    isListening: Boolean,
    activeSpeakerId: Int,
    isAutoSpeakEnabled: Boolean,
    onlyStarred: Boolean,
    onMicClick: (speakerId: Int) -> Unit,
    onAutoSpeakToggle: () -> Unit,
    onStarredToggle: () -> Unit,
    onOpenPhrasebook: () -> Unit,
    onOpenTextInput: (speakerId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(MidnightSurface)
            .border(1.dp, MidnightBorder, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(top = 12.dp, bottom = 18.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top tools row (Auto-Speak, Phrasebook, Keyboard, Starred)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Auto-speak translation toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isAutoSpeakEnabled) CyanNeon.copy(alpha = 0.15f) else MidnightSurfaceVariant)
                    .border(
                        1.dp,
                        if (isAutoSpeakEnabled) CyanNeon.copy(alpha = 0.4f) else MidnightBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(onClick = onAutoSpeakToggle)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("auto_speak_toggle"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isAutoSpeakEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                    contentDescription = "Auto-prononciation",
                    tint = if (isAutoSpeakEnabled) CyanNeon else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isAutoSpeakEnabled) "Voix Auto ON" else "Voix Auto OFF",
                    color = if (isAutoSpeakEnabled) TextWhite else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Phrasebook button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(MidnightSurfaceVariant)
                        .border(1.dp, MidnightBorder, RoundedCornerShape(14.dp))
                        .clickable(onClick = onOpenPhrasebook)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("phrasebook_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Phrases types",
                        tint = VioletLight,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Phrases",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Keyboard input button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(MidnightSurfaceVariant)
                        .border(1.dp, MidnightBorder, RoundedCornerShape(14.dp))
                        .clickable { onOpenTextInput(1) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("keyboard_input_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Clavier",
                        tint = TextWhite,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Écrire",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Favorites filter toggle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (onlyStarred) AmberAccent.copy(alpha = 0.2f) else MidnightSurfaceVariant)
                        .border(
                            1.dp,
                            if (onlyStarred) AmberAccent else MidnightBorder,
                            CircleShape
                        )
                        .clickable(onClick = onStarredToggle)
                        .testTag("favorites_filter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favoris",
                        tint = if (onlyStarred) AmberAccent else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Dual Big Microphone Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speaker A Mic Button (Source Language, e.g. Darija)
            val isListeningA = isListening && activeSpeakerId == 1
            SpeakerMicButton(
                speakerId = 1,
                speakerName = "Locuteur A",
                language = sourceLanguage,
                isListening = isListeningA,
                primaryColor = SpeakerAColor,
                onClick = { onMicClick(1) },
                modifier = Modifier.testTag("mic_speaker_a")
            )

            // Speaker B Mic Button (Target Language, e.g. Français)
            val isListeningB = isListening && activeSpeakerId == 2
            SpeakerMicButton(
                speakerId = 2,
                speakerName = "Locuteur B",
                language = targetLanguage,
                isListening = isListeningB,
                primaryColor = SpeakerBColor,
                onClick = { onMicClick(2) },
                modifier = Modifier.testTag("mic_speaker_b")
            )
        }
    }
}

@Composable
private fun SpeakerMicButton(
    speakerId: Int,
    speakerName: String,
    language: Language,
    isListening: Boolean,
    primaryColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "halo_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            // Glowing Halo Ring when listening
            if (isListening) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.3f))
                )
            }

            // Main tactile round button
            Box(
                modifier = modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = if (isListening) {
                                listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
                            } else {
                                listOf(MidnightSurfaceVariant, MidnightBorder)
                            }
                        )
                    )
                    .border(
                        width = if (isListening) 2.5.dp else 1.5.dp,
                        color = if (isListening) CyanNeon else primaryColor.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Parler en ${language.displayName}",
                    tint = if (isListening) MidnightDark else primaryColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Speaker language badge
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(language.flag, fontSize = 14.sp)
                Text(
                    text = language.displayName,
                    color = if (isListening) primaryColor else TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = if (isListening) "Enregistrement..." else "Toucher pour parler",
                color = if (isListening) primaryColor else TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
