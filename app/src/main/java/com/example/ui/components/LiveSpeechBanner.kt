package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.SpeakerAColor
import com.example.ui.theme.SpeakerBColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VioletLight
import com.example.viewmodel.LiveTranslationPreview

@Composable
fun LiveSpeechBanner(
    livePreview: LiveTranslationPreview,
    isListening: Boolean,
    rmsDb: Float,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isListening || livePreview.isActive,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
        modifier = modifier
    ) {
        val isSpeakerA = livePreview.speakerId == 1
        val accentColor = if (isSpeakerA) SpeakerAColor else SpeakerBColor

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MidnightSurface,
                            MidnightDark
                        )
                    )
                )
                .border(
                    1.5.dp,
                    accentColor.copy(alpha = 0.6f),
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Header with glowing mic and live badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "En écoute",
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = if (isSpeakerA) {
                                "Locuteur A (${livePreview.sourceLanguage.displayName} ${livePreview.sourceLanguage.flag})"
                            } else {
                                "Locuteur B (${livePreview.sourceLanguage.displayName} ${livePreview.sourceLanguage.flag})"
                            },
                            color = accentColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "DIRECT AUDIO",
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Dynamic Audio Waveform
                AudioWaveformVisualizer(
                    isListening = isListening,
                    rmsDb = rmsDb,
                    primaryColor = accentColor,
                    secondaryColor = if (isSpeakerA) VioletLight else CyanNeon
                )

                // Live Spoken Input Preview
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (livePreview.rawSpokenText.isNotBlank()) {
                            livePreview.rawSpokenText
                        } else {
                            "Parlez maintenant... l'IA écoute en temps réel"
                        },
                        color = if (livePreview.rawSpokenText.isNotBlank()) TextWhite else TextMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )

                    // Live Translation Preview (Real-time stream)
                    if (livePreview.liveTranslatedText.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(livePreview.targetLanguage.flag, fontSize = 14.sp)
                            Text(
                                text = "➜  ${livePreview.liveTranslatedText}",
                                color = CyanNeon,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
