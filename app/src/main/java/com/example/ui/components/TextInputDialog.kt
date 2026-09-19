package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.SpeakerAColor
import com.example.ui.theme.SpeakerBColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VioletLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputDialog(
    isOpen: Boolean,
    initialSpeakerId: Int,
    sourceLanguage: Language,
    targetLanguage: Language,
    onDismiss: () -> Unit,
    onSubmit: (text: String, speakerId: Int) -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var text by remember { mutableStateOf("") }
    var selectedSpeaker by remember { mutableIntStateOf(initialSpeakerId) }

    val currentLang = if (selectedSpeaker == 1) sourceLanguage else targetLanguage

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MidnightDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saisie textuelle directe",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MidnightSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Speaker Switcher Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Speaker A
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selectedSpeaker == 1) SpeakerAColor.copy(alpha = 0.2f) else MidnightSurface)
                        .border(
                            1.dp,
                            if (selectedSpeaker == 1) SpeakerAColor else MidnightBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedSpeaker = 1 }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(sourceLanguage.flag, fontSize = 16.sp)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "Locuteur A (${sourceLanguage.displayName})",
                        color = if (selectedSpeaker == 1) SpeakerAColor else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Speaker B
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selectedSpeaker == 2) SpeakerBColor.copy(alpha = 0.2f) else MidnightSurface)
                        .border(
                            1.dp,
                            if (selectedSpeaker == 2) SpeakerBColor else MidnightBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedSpeaker = 2 }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(targetLanguage.flag, fontSize = 16.sp)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "Locuteur B (${targetLanguage.displayName})",
                        color = if (selectedSpeaker == 2) SpeakerBColor else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Text Input Box
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = {
                    Text(
                        text = "Écrivez ici (ex: salam labas, comment allez-vous, where is the hotel)...",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("text_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MidnightSurface,
                    unfocusedContainerColor = MidnightSurface,
                    focusedBorderColor = CyanNeon,
                    unfocusedBorderColor = MidnightBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(16.dp)
            )

            // Submit Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (text.isNotBlank()) CyanNeon else MidnightSurfaceVariant)
                    .clickable(enabled = text.isNotBlank()) {
                        onSubmit(text, selectedSpeaker)
                        text = ""
                        onDismiss()
                    }
                    .padding(vertical = 14.dp)
                    .testTag("submit_text_translation_button"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Traduire et prononcer",
                    tint = if (text.isNotBlank()) MidnightDark else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Traduire & Prononcer Audio",
                    color = if (text.isNotBlank()) MidnightDark else TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
