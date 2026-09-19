package com.example.model

data class PhraseCategory(
    val id: String,
    val titleFr: String,
    val titleAr: String,
    val icon: String,
    val phrases: List<PhraseItem>
)

data class PhraseItem(
    val id: String,
    val darija: String,
    val arabic: String,
    val french: String,
    val english: String
) {
    fun getTextForLanguage(lang: Language): String = when (lang) {
        Language.DARIJA -> darija
        Language.ARABIC -> arabic
        Language.FRENCH -> french
        Language.ENGLISH -> english
        Language.SPANISH -> french // Fallback or translated
        Language.GERMAN -> english // Fallback or translated
    }
}

object PhrasebookRepository {
    val categories: List<PhraseCategory> = listOf(
        PhraseCategory(
            id = "greetings",
            titleFr = "Salutations",
            titleAr = "التحيات",
            icon = "👋",
            phrases = listOf(
                PhraseItem("g1", "السلام عليكم", "السلام عليكم", "Bonjour / Bonjour à tous", "Hello / Peace be upon you"),
                PhraseItem("g2", "لاباس عليك؟ كلشي مزيان؟", "كيف حالك؟ هل أنت بخير؟", "Comment allez-vous ? Tout va bien ?", "How are you? Is everything good?"),
                PhraseItem("g3", "الحمد لله، كولشي بخير", "الحمد لله، أنا بأفضل حال", "Je vais très bien, merci", "I am fine, thank you"),
                PhraseItem("g4", "متشرفين بمعرفتك", "تشرفنا بلقائك", "Enchanté de faire votre connaissance", "Nice to meet you"),
                PhraseItem("g5", "بسلامة، الله يعاونك", "مع السلامة وفي أمان الله", "Au revoir, bonne journée", "Goodbye, take care"),
                PhraseItem("g6", "شكراً بزاف، ربي يخليك", "شكراً جزيلاً لك", "Merci beaucoup, c'est très aimable", "Thank you very much")
            )
        ),
        PhraseCategory(
            id = "travel",
            titleFr = "Voyage & Transport",
            titleAr = "السفر والمواصلات",
            icon = "✈️",
            phrases = listOf(
                PhraseItem("t1", "فين كاين المطار عافاك؟", "أين يقع المطار من فضلك؟", "Où se trouve l'aéroport s'il vous plaît ?", "Where is the airport, please?"),
                PhraseItem("t2", "بغيت نمشي لمحطة القطار", "أريد الذهاب إلى محطة القطار", "Je veux aller à la gare ferroviaire", "I want to go to the train station"),
                PhraseItem("t3", "شحال ثمن الطاكسي لهاد البلاصة؟", "كم تكلفة سيارة الأجرة إلى هنا؟", "Combien coûte le taxi pour cet endroit ?", "How much is the taxi to this place?"),
                PhraseItem("t4", "خدم الكونطور عافاك", "شغّل العداد من فضلك", "Mettez le compteur s'il vous plaît", "Please turn on the taximeter"),
                PhraseItem("t5", "فين كاين أقرب فندق زوين؟", "أين أجد أقرب فندق جيد؟", "Où se trouve le meilleur hôtel proche ?", "Where is the nearest good hotel?"),
                PhraseItem("t6", "سير نيشان ومن بعد دور لليمن", "اتجه للأمام ثم انعطف يميناً", "Allez tout droit puis tournez à droite", "Go straight then turn right")
            )
        ),
        PhraseCategory(
            id = "shopping",
            titleFr = "Marché & Achats",
            titleAr = "التسوق والشراء",
            icon = "🛍️",
            phrases = listOf(
                PhraseItem("s1", "بشحال هادا عافاك؟", "بكم هذا من فضلك؟", "Combien coûte cet article ?", "How much does this cost?"),
                PhraseItem("s2", "غالي بزاف، نقص ليا شوية", "السعر مرتفع جداً، خفّضه قليلاً", "C'est un peu trop cher, baissez le prix svp", "It's too expensive, give a discount"),
                PhraseItem("s3", "واش كتقبلو الكارطة البنكية؟", "هل تقبلون الدفع بالبطاقة؟", "Acceptez-vous la carte bancaire ?", "Do you accept credit card payment?"),
                PhraseItem("s4", "بغيت هادا باللون لخر", "أريد هذا بلون آخر", "Je voudrais celui-ci dans une autre couleur", "I want this in another color"),
                PhraseItem("s5", "عطيني شي حاجة زوينة وأصلية", "أريد شيئاً أصلياً وجميلاً", "Donnez-moi quelque chose d'authentique", "Give me something authentic and good")
            )
        ),
        PhraseCategory(
            id = "restaurant",
            titleFr = "Café & Restaurant",
            titleAr = "المطعم والمقهى",
            icon = "🍽️",
            phrases = listOf(
                PhraseItem("r1", "بغيت طبلة لجوج د الناس", "أريد طاولة لشخصين من فضلك", "Une table pour deux personnes s'il vous plaît", "A table for two please"),
                PhraseItem("r2", "عطيني أتاي بالنعناع يكون زوين", "أحضر لي شاي بالنعناع من فضلك", "Un bon thé à la menthe s'il vous plaît", "A Moroccan mint tea, please"),
                PhraseItem("r3", "شنو الماكلة لي مشهورة عندكم؟", "ما هو الطبق الموصى به هنا؟", "Quelle est la spécialité de la maison ?", "What is the house specialty?"),
                PhraseItem("r4", "عطيني قرعة د الما باردة", "أعطني زجاجة ماء بارد", "Une bouteille d'eau fraîche s'il vous plaît", "A bottle of cold water, please"),
                PhraseItem("r5", "الحساب عافاك", "الفاتورة من فضلك", "L'addition s'il vous plaît", "The bill, please")
            )
        ),
        PhraseCategory(
            id = "emergency",
            titleFr = "Urgence & Santé",
            titleAr = "الطوارئ والصحة",
            icon = "🚨",
            phrases = listOf(
                PhraseItem("e1", "عتقوني عافاكم، محتاج مساعدة", "أنقذوني أرجوكم، أحتاج للمساعدة", "Aidez-moi s'il vous plaît, urgence !", "Help me please, emergency!"),
                PhraseItem("e2", "عيطو على الإسعاف دغيا", "اتصلوا بالإسعاف فوراً", "Appelez une ambulance immédiatement", "Call an ambulance immediately"),
                PhraseItem("e3", "فين كاينة الصيدلية لي حالة دابا؟", "أين أجد صيدلية الحراسة الآن؟", "Où est la pharmacie de garde ?", "Where is the on-call pharmacy?"),
                PhraseItem("e4", "راني مريض بزاف ومحتاج طبيب", "أنا مريض جداً وأحتاج لطبيب", "Je suis très malade, j'ai besoin d'un médecin", "I am very sick, I need a doctor"),
                PhraseItem("e5", "تجلات ليا الصاك والپاسبور", "فقدت حقيبتي وجواز سفري", "J'ai perdu mon sac et mon passeport", "I lost my bag and passport")
            )
        )
    )
}
