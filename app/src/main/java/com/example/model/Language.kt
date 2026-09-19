package com.example.model

import java.util.Locale

enum class Language(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flag: String,
    val speechLocale: String,
    val ttsLocale: Locale
) {
    DARIJA(
        code = "ary",
        displayName = "Darija (Maroc)",
        nativeName = "الدارجة المغربية",
        flag = "🇲🇦",
        speechLocale = "ar-MA",
        ttsLocale = Locale("ar", "MA")
    ),
    ARABIC(
        code = "ar",
        displayName = "Arabe classique",
        nativeName = "العربية الفصحى",
        flag = "🇸🇦",
        speechLocale = "ar-SA",
        ttsLocale = Locale("ar")
    ),
    FRENCH(
        code = "fr",
        displayName = "Français",
        nativeName = "Français",
        flag = "🇫🇷",
        speechLocale = "fr-FR",
        ttsLocale = Locale.FRENCH
    ),
    ENGLISH(
        code = "en",
        displayName = "Anglais",
        nativeName = "English",
        flag = "🇬🇧",
        speechLocale = "en-US",
        ttsLocale = Locale.ENGLISH
    ),
    SPANISH(
        code = "es",
        displayName = "Espagnol",
        nativeName = "Español",
        flag = "🇪🇸",
        speechLocale = "es-ES",
        ttsLocale = Locale("es", "ES")
    ),
    GERMAN(
        code = "de",
        displayName = "Allemand",
        nativeName = "Deutsch",
        flag = "🇩🇪",
        speechLocale = "de-DE",
        ttsLocale = Locale.GERMAN
    );

    companion object {
        fun fromCode(code: String): Language =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: FRENCH
    }
}
