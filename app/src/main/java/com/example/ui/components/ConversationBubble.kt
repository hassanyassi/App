package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.TranslationEntity
import com.example.model.Language
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.SpeakerAColor
import com.example.ui.theme.SpeakerBColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VioletLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationBubble(
    item: TranslationEntity,
    isSpeaking: Boolean,
    onSpeakClick: () -> Unit,
    onStarClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isSpeakerA = item.speakerId == 1
    val speakerAccent = if (isSpeakerA) SpeakerAColor else SpeakerBColor
    val sourceLang = Language.fromCode(item.sourceLangCode)
    val targetLang = Language.fromCode(item.targetLangCode)

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(item.timestamp))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = if (isSpeakerA) Alignment.Start else Alignment.End
    ) {
        // Bubble container
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isSpeakerA) 4.dp else 18.dp,
                        bottomEnd = if (isSpeakerA) 18.dp else 4.dp
                    )
                )
                .background(MidnightSurface)
                .border(
                    width = 1.dp,
                    color = speakerAccent.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Header: Speaker tag & timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(speakerAccent)
                        )
                        Text(
                            text = if (isSpeakerA) "Locuteur A (${sourceLang.flag})" else "Locuteur B (${sourceLang.flag})",
                            color = speakerAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = formattedTime,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                // Original Spoken Text
                Text(
                    text = item.sourceText,
                    color = TextWhite.copy(alpha = 0.85f),
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )

                // Translated Card Container (Glowing & Distinct)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MidnightSurfaceVariant,
                                    MidnightSurfaceVariant.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            speakerAccent.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(targetLang.flag, fontSize = 14.sp)
                            Text(
                                text = "Traduction (${targetLang.displayName})",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = item.translatedText,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Action Bar (Speak Audio, Copy, Star, Delete)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Audio Speak Button with active glow
                    IconButton(
                        onClick = onSpeakClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSpeaking) CyanNeon.copy(alpha = 0.25f) else MidnightSurfaceVariant)
                            .testTag("speak_audio_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Écouter la traduction audio",
                            tint = if (isSpeaking) CyanNeon else TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Copy Button
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Traduction", item.translatedText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Traduction copiée !", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copier",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Star Button
                        IconButton(
                            onClick = onStarClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favori",
                                tint = if (item.isStarred) AmberAccent else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Delete Button
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Supprimer",
                                tint = TextMuted.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
