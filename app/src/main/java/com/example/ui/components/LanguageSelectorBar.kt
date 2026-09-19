package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.SpeakerAColor
import com.example.ui.theme.SpeakerBColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun LanguageSelectorBar(
    sourceLanguage: Language,
    targetLanguage: Language,
    onSourceLanguageSelected: (Language) -> Unit,
    onTargetLanguageSelected: (Language) -> Unit,
    onSwapLanguages: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sourceDropdownOpen by remember { mutableStateOf(false) }
    var targetDropdownOpen by remember { mutableStateOf(false) }
    var swapRotation by remember { mutableStateOf(0f) }

    val animatedRotation by animateFloatAsState(
        targetValue = swapRotation,
        label = "swap_rotate"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Offline Status Badge & Brand Row
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
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                )
                Text(
                    text = "100% Hors-Ligne (Offline AI)",
                    color = EmeraldGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MidnightSurfaceVariant)
                    .border(1.dp, MidnightBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Audio Real-Time",
                    color = CyanNeon,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Language Selector Capsule
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MidnightSurface)
                .border(1.dp, MidnightBorder, RoundedCornerShape(18.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Source Language Pill
            Box(modifier = Modifier.weight(1f)) {
                LanguagePill(
                    language = sourceLanguage,
                    accentColor = SpeakerAColor,
                    label = "Locuteur A",
                    onClick = { sourceDropdownOpen = true },
                    modifier = Modifier.testTag("source_lang_pill")
                )

                DropdownMenu(
                    expanded = sourceDropdownOpen,
                    onDismissRequest = { sourceDropdownOpen = false },
                    modifier = Modifier.background(MidnightSurfaceVariant)
                ) {
                    Language.entries.forEach { lang ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(lang.flag, fontSize = 20.sp)
                                    Column {
                                        Text(lang.displayName, color = TextWhite, fontSize = 14.sp)
                                        Text(lang.nativeName, color = TextMuted, fontSize = 11.sp)
                                    }
                                }
                            },
                            trailingIcon = {
                                if (lang == sourceLanguage) {
                                    Icon(Icons.Default.Check, contentDescription = "Sélectionné", tint = SpeakerAColor)
                                }
                            },
                            onClick = {
                                onSourceLanguageSelected(lang)
                                sourceDropdownOpen = false
                            }
                        )
                    }
                }
            }

            // Swap Button
            IconButton(
                onClick = {
                    swapRotation += 180f
                    onSwapLanguages()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MidnightSurfaceVariant)
                    .border(1.dp, MidnightBorder, CircleShape)
                    .rotate(animatedRotation)
                    .testTag("swap_languages_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Intervertir les langues",
                    tint = TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Target Language Pill
            Box(modifier = Modifier.weight(1f)) {
                LanguagePill(
                    language = targetLanguage,
                    accentColor = SpeakerBColor,
                    label = "Locuteur B",
                    onClick = { targetDropdownOpen = true },
                    modifier = Modifier.testTag("target_lang_pill")
                )

                DropdownMenu(
                    expanded = targetDropdownOpen,
                    onDismissRequest = { targetDropdownOpen = false },
                    modifier = Modifier.background(MidnightSurfaceVariant)
                ) {
                    Language.entries.forEach { lang ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(lang.flag, fontSize = 20.sp)
                                    Column {
                                        Text(lang.displayName, color = TextWhite, fontSize = 14.sp)
                                        Text(lang.nativeName, color = TextMuted, fontSize = 11.sp)
                                    }
                                }
                            },
                            trailingIcon = {
                                if (lang == targetLanguage) {
                                    Icon(Icons.Default.Check, contentDescription = "Sélectionné", tint = SpeakerBColor)
                                }
                            },
                            onClick = {
                                onTargetLanguageSelected(lang)
                                targetDropdownOpen = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguagePill(
    language: Language,
    accentColor: Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = language.flag,
            fontSize = 24.sp
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = language.displayName,
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Sélectionner",
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
