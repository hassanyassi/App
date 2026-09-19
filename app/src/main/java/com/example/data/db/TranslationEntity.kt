package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceLangCode: String,
    val targetLangCode: String,
    val sourceText: String,
    val translatedText: String,
    val speakerId: Int = 1, // 1 for Speaker A, 2 for Speaker B
    val isStarred: Boolean = false
)
