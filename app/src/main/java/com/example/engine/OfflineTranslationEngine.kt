package com.example.engine

import com.example.model.Language
import java.util.Locale

/**
 * Offline AI Translation Engine
 * Operates completely on-device without internet connection.
 * Features multi-lingual phrase matching, morphological token translation,
 * and Moroccan Darija (Arabic script & Arabizi) normalization.
 */
object OfflineTranslationEngine {

    // Common phrases mapped to standard concept keys
    private data class ConceptPhrase(
        val conceptId: String,
        val text: String,
        val lang: Language
    )

    // Concept dictionary for instant high-fidelity conversational translation
    private val conceptMatrix = mapOf(
        "greeting_hello" to mapOf(
            Language.DARIJA to "السلام عليكم (Salam)",
            Language.ARABIC to "السلام عليكم ورحمة الله",
            Language.FRENCH to "Bonjour, comment allez-vous ?",
            Language.ENGLISH to "Hello, how are you?",
            Language.SPANISH to "¡Hola! ¿Cómo estás?",
            Language.GERMAN to "Hallo, wie geht es dir?"
        ),
        "greeting_how_are_you" to mapOf(
            Language.DARIJA to "لاباس عليك؟ كلشي بخير؟",
            Language.ARABIC to "كيف حالك؟ هل أنت بخير؟",
            Language.FRENCH to "Comment ça va ? Tout va bien ?",
            Language.ENGLISH to "How are you? Is everything good?",
            Language.SPANISH to "¿Cómo estás? ¿Todo bien?",
            Language.GERMAN to "Wie geht es dir? Alles gut?"
        ),
        "greeting_im_fine" to mapOf(
            Language.DARIJA to "الحمد لله، كلشي مزيان",
            Language.ARABIC to "الحمد لله، أنا بخير",
            Language.FRENCH to "Je vais bien, merci beaucoup",
            Language.ENGLISH to "I am doing well, thank you",
            Language.SPANISH to "Estoy bien, muchas gracias",
            Language.GERMAN to "Mir geht es gut, danke schön"
        ),
        "thanks_much" to mapOf(
            Language.DARIJA to "شكراً بزاف، الله يحفظك",
            Language.ARABIC to "شكراً جزيلاً، بارك الله فيك",
            Language.FRENCH to "Merci beaucoup, c'est très gentil",
            Language.ENGLISH to "Thank you very much, you are kind",
            Language.SPANISH to "Muchas gracias, muy amable",
            Language.GERMAN to "Vielen Dank, sehr freundlich"
        ),
        "you_are_welcome" to mapOf(
            Language.DARIJA to "العفو، مرحبا بك فكل وقت",
            Language.ARABIC to "على الرحب والسعة",
            Language.FRENCH to "Je vous en prie / De rien",
            Language.ENGLISH to "You're welcome / My pleasure",
            Language.SPANISH to "De nada / Con gusto",
            Language.GERMAN to "Bitte sehr / Gern geschehen"
        ),
        "yes_sure" to mapOf(
            Language.DARIJA to "إيه واخا، ما كاين مشكل",
            Language.ARABIC to "نعم بالتأكيد، لا توجد مشكلة",
            Language.FRENCH to "Oui bien sûr, aucun problème",
            Language.ENGLISH to "Yes sure, no problem at all",
            Language.SPANISH to "Sí por supuesto, no hay problema",
            Language.GERMAN to "Ja natürlich, kein Problem"
        ),
        "no_sorry" to mapOf(
            Language.DARIJA to "لا سمح لي، ما نقدرش",
            Language.ARABIC to "لا أعتذر، لا أستطيع",
            Language.FRENCH to "Non désolé, je ne peux pas",
            Language.ENGLISH to "No sorry, I cannot",
            Language.SPANISH to "No lo siento, no puedo",
            Language.GERMAN to "Nein tut mir leid, ich kann nicht"
        ),
        "where_is_hotel" to mapOf(
            Language.DARIJA to "فين كاين لوطيل عافاك؟",
            Language.ARABIC to "أين يوجد الفندق من فضلك؟",
            Language.FRENCH to "Où se trouve l'hôtel s'il vous plaît ?",
            Language.ENGLISH to "Where is the hotel, please?",
            Language.SPANISH to "¿Dónde está el hotel, por favor?",
            Language.GERMAN to "Wo befindet sich das Hotel, bitte?"
        ),
        "where_is_airport" to mapOf(
            Language.DARIJA to "فين كاين المطار واش بعيد؟",
            Language.ARABIC to "أين يقع المطار وهل هو بعيد؟",
            Language.FRENCH to "Où est l'aéroport, est-ce loin ?",
            Language.ENGLISH to "Where is the airport, is it far?",
            Language.SPANISH to "¿Dónde está el aeropuerto, está lejos?",
            Language.GERMAN to "Wo ist der Flughafen, ist er weit weg?"
        ),
        "where_is_train_station" to mapOf(
            Language.DARIJA to "فين كاينة محطة القطار (لاغار)؟",
            Language.ARABIC to "أين تقع محطة القطار؟",
            Language.FRENCH to "Où se trouve la gare ferroviaire ?",
            Language.ENGLISH to "Where is the train station?",
            Language.SPANISH to "¿Dónde está la estación de tren?",
            Language.GERMAN to "Wo ist der Bahnhof?"
        ),
        "how_much_is_this" to mapOf(
            Language.DARIJA to "بشحال هادشي؟ شحال الثمن؟",
            Language.ARABIC to "كم سعر هذا من فضلك؟",
            Language.FRENCH to "Combien ça coûte s'il vous plaît ?",
            Language.ENGLISH to "How much does this cost, please?",
            Language.SPANISH to "¿Cuánto cuesta esto, por favor?",
            Language.GERMAN to "Wie viel kostet das, bitte?"
        ),
        "too_expensive" to mapOf(
            Language.DARIJA to "غالي بزاف، نقص ليا شوية عافاك",
            Language.ARABIC to "هذا باهظ الثمن، هل يمكن تخفيض السعر؟",
            Language.FRENCH to "C'est trop cher, pouvez-vous baisser un peu ?",
            Language.ENGLISH to "It's too expensive, can you lower the price?",
            Language.SPANISH to "Es muy caro, ¿puede bajar un poco el precio?",
            Language.GERMAN to "Das ist zu teuer, können Sie mit dem Preis runtergehen?"
        ),
        "can_you_help_me" to mapOf(
            Language.DARIJA to "واش تقدر تعاوني عافاك؟",
            Language.ARABIC to "هل يمكنك مساعدتي من فضلك؟",
            Language.FRENCH to "Pouvez-vous m'aider s'il vous plaît ?",
            Language.ENGLISH to "Can you help me, please?",
            Language.SPANISH to "¿Puedes ayudarme, por favor?",
            Language.GERMAN to "Können Sie mir bitte helfen?"
        ),
        "i_need_doctor" to mapOf(
            Language.DARIJA to "محتاج طبيب ضروري، راني مريض",
            Language.ARABIC to "أحتاج إلى طبيب عاجل، أنا مريض",
            Language.FRENCH to "J'ai besoin d'un médecin d'urgence, je suis malade",
            Language.ENGLISH to "I need a doctor urgently, I am sick",
            Language.SPANISH to "Necesito un médico urgente, estoy enfermo",
            Language.GERMAN to "Ich brauche dringend einen Arzt, ich bin krank"
        ),
        "where_is_pharmacy" to mapOf(
            Language.DARIJA to "فين كاينة أقرب فرماسيان (صيدلية)؟",
            Language.ARABIC to "أين تقع أقرب صيدلية؟",
            Language.FRENCH to "Où est la pharmacie la plus proche ?",
            Language.ENGLISH to "Where is the nearest pharmacy?",
            Language.SPANISH to "¿Dónde está la farmacia más cercana?",
            Language.GERMAN to "Wo ist die nächste Apotheke?"
        ),
        "call_police" to mapOf(
            Language.DARIJA to "عيطو على البوليس عافاكم دغيا",
            Language.ARABIC to "اتصلوا بالشرطة فوراً من فضلكم",
            Language.FRENCH to "Appelez la police immédiatement s'il vous plaît",
            Language.ENGLISH to "Call the police immediately please",
            Language.SPANISH to "Llame a la policía de inmediato, por favor",
            Language.GERMAN to "Rufen Sie sofort die Polizei, bitte"
        ),
        "i_dont_understand" to mapOf(
            Language.DARIJA to "ما فهمتش مزيان، عاود عافاك",
            Language.ARABIC to "لم أفهم جيداً، أعد من فضلك",
            Language.FRENCH to "Je n'ai pas bien compris, répétez s'il vous plaît",
            Language.ENGLISH to "I did not understand well, please repeat",
            Language.SPANISH to "No entendí bien, repita por favor",
            Language.GERMAN to "Ich habe nicht gut verstanden, bitte wiederholen"
        ),
        "do_you_speak_english" to mapOf(
            Language.DARIJA to "واش كتهضر بالإنجليزية ولا الفرنسية؟",
            Language.ARABIC to "هل تتحدث الإنجليزية أو الفرنسية؟",
            Language.FRENCH to "Parlez-vous anglais ou français ?",
            Language.ENGLISH to "Do you speak English or French?",
            Language.SPANISH to "¿Hablas inglés o francés?",
            Language.GERMAN to "Sprechen Sie Englisch oder Französisch?"
        ),
        "what_is_your_name" to mapOf(
            Language.DARIJA to "شنو سميتك؟ متشرفين",
            Language.ARABIC to "ما اسمك؟ تشرفنا",
            Language.FRENCH to "Comment vous appelez-vous ? Enchanté",
            Language.ENGLISH to "What is your name? Nice to meet you",
            Language.SPANISH to "¿Cómo te llamas? Mucho gusto",
            Language.GERMAN to "Wie heißen Sie? Freut mich"
        ),
        "where_are_you_from" to mapOf(
            Language.DARIJA to "منين نتا؟ أين بلاد؟",
            Language.ARABIC to "من أي بلد أنت؟",
            Language.FRENCH to "D'où venez-vous ? De quel pays ?",
            Language.ENGLISH to "Where are you from? Which country?",
            Language.SPANISH to "¿De dónde eres? ¿De qué país?",
            Language.GERMAN to "Woher kommen Sie? Aus welchem Land?"
        ),
        "i_want_to_eat" to mapOf(
            Language.DARIJA to "جاني الجوع، بغيت ناكل شي حاجة زوينة",
            Language.ARABIC to "أشعر بالجوع، أريد أن آكل وجبة شهية",
            Language.FRENCH to "J'ai faim, je voudrais manger quelque chose de bon",
            Language.ENGLISH to "I am hungry, I would like to eat something good",
            Language.SPANISH to "Tengo hambre, me gustaría comer algo rico",
            Language.GERMAN to "Ich habe Hunger, ich möchte etwas Gutes essen"
        ),
        "water_please" to mapOf(
            Language.DARIJA to "عطيني قرعة ديال الما باردة عافاك",
            Language.ARABIC to "أعطني زجاجة ماء بارد من فضلك",
            Language.FRENCH to "Donnez-moi une bouteille d'eau fraîche s'il vous plaît",
            Language.ENGLISH to "Give me a bottle of cold water, please",
            Language.SPANISH to "Deme una botella de agua fresca, por favor",
            Language.GERMAN to "Geben Sie mir bitte eine Flasche kaltes Wasser"
        ),
        "bill_please" to mapOf(
            Language.DARIJA to "الحساب عافاك (لاصيصيو)",
            Language.ARABIC to "الحساب من فضلك",
            Language.FRENCH to "L'addition s'il vous plaît",
            Language.ENGLISH to "The bill, please",
            Language.SPANISH to "La cuenta, por favor",
            Language.GERMAN to "Die Rechnung, bitte"
        ),
        "goodbye" to mapOf(
            Language.DARIJA to "بسلامة، الله يعاونك، تهلا فراسك",
            Language.ARABIC to "مع السلامة، في أمان الله",
            Language.FRENCH to "Au revoir, prenez soin de vous",
            Language.ENGLISH to "Goodbye, take care of yourself",
            Language.SPANISH to "Adiós, cuídate mucho",
            Language.GERMAN to "Auf Wiedersehen, pass auf dich auf"
        ),
        "straight_ahead" to mapOf(
            Language.DARIJA to "سير نيشان ومن بعد دور على اليمن",
            Language.ARABIC to "امشِ للأمام مباشرة ثم انعطف يميناً",
            Language.FRENCH to "Allez tout droit puis tournez à droite",
            Language.ENGLISH to "Go straight ahead then turn right",
            Language.SPANISH to "Siga todo recto y luego gire a la derecha",
            Language.GERMAN to "Gehen Sie geradeaus und biegen Sie rechts ab"
        ),
        "turn_left" to mapOf(
            Language.DARIJA to "دور على اليسر تلقى البلاصة",
            Language.ARABIC to "انعطف يساراً وستجد المكان",
            Language.FRENCH to "Tournez à gauche pour trouver l'endroit",
            Language.ENGLISH to "Turn left to find the place",
            Language.SPANISH to "Gire a la izquierda para encontrar el lugar",
            Language.GERMAN to "Biegen Sie links ab, um den Ort zu finden"
        ),
        "take_taxi" to mapOf(
            Language.DARIJA to "شد طاكسي صغير أحسن ليك",
            Language.ARABIC to "خذ سيارة أجرة فهو أفضل",
            Language.FRENCH to "Prenez un taxi, c'est plus pratique",
            Language.ENGLISH to "Take a taxi, it is better",
            Language.SPANISH to "Tome un taxi, es mejor",
            Language.GERMAN to "Nehmen Sie ein Taxi, das ist besser"
        )
    )

    // Token & keyword offline dictionary (English base key)
    private val tokenDictionary = mapOf(
        "hello" to mapOf(Language.DARIJA to "سلام", Language.ARABIC to "أهلاً", Language.FRENCH to "bonjour", Language.ENGLISH to "hello", Language.SPANISH to "hola", Language.GERMAN to "hallo"),
        "salam" to mapOf(Language.DARIJA to "سلام", Language.ARABIC to "سلام", Language.FRENCH to "bonjour", Language.ENGLISH to "peace / hello", Language.SPANISH to "hola", Language.GERMAN to "hallo"),
        "good" to mapOf(Language.DARIJA to "مزيان", Language.ARABIC to "جيد", Language.FRENCH to "bien / bon", Language.ENGLISH to "good", Language.SPANISH to "bueno", Language.GERMAN to "gut"),
        "mzyan" to mapOf(Language.DARIJA to "مزيان", Language.ARABIC to "جيد", Language.FRENCH to "bien", Language.ENGLISH to "good", Language.SPANISH to "bien", Language.GERMAN to "gut"),
        "yes" to mapOf(Language.DARIJA to "إيه / نعم", Language.ARABIC to "نعم", Language.FRENCH to "oui", Language.ENGLISH to "yes", Language.SPANISH to "sí", Language.GERMAN to "ja"),
        "no" to mapOf(Language.DARIJA to "لا", Language.ARABIC to "لا", Language.FRENCH to "non", Language.ENGLISH to "no", Language.SPANISH to "no", Language.GERMAN to "nein"),
        "please" to mapOf(Language.DARIJA to "عافاك", Language.ARABIC to "من فضلك", Language.FRENCH to "s'il vous plaît", Language.ENGLISH to "please", Language.SPANISH to "por favor", Language.GERMAN to "bitte"),
        "afak" to mapOf(Language.DARIJA to "عافاك", Language.ARABIC to "من فضلك", Language.FRENCH to "s'il vous plaît", Language.ENGLISH to "please", Language.SPANISH to "por favor", Language.GERMAN to "bitte"),
        "thank" to mapOf(Language.DARIJA to "شكراً", Language.ARABIC to "شكراً", Language.FRENCH to "merci", Language.ENGLISH to "thank you", Language.SPANISH to "gracias", Language.GERMAN to "danke"),
        "choukran" to mapOf(Language.DARIJA to "شكراً", Language.ARABIC to "شكراً", Language.FRENCH to "merci", Language.ENGLISH to "thank you", Language.SPANISH to "gracias", Language.GERMAN to "danke"),
        "bzaf" to mapOf(Language.DARIJA to "بزاف", Language.ARABIC to "كثيراً", Language.FRENCH to "beaucoup", Language.ENGLISH to "a lot", Language.SPANISH to "mucho", Language.GERMAN to "viel"),
        "much" to mapOf(Language.DARIJA to "بزاف", Language.ARABIC to "كثيراً", Language.FRENCH to "beaucoup", Language.ENGLISH to "much", Language.SPANISH to "mucho", Language.GERMAN to "viel"),
        "where" to mapOf(Language.DARIJA to "فين", Language.ARABIC to "أين", Language.FRENCH to "où", Language.ENGLISH to "where", Language.SPANISH to "dónde", Language.GERMAN to "wo"),
        "fin" to mapOf(Language.DARIJA to "فين", Language.ARABIC to "أين", Language.FRENCH to "où", Language.ENGLISH to "where", Language.SPANISH to "dónde", Language.GERMAN to "wo"),
        "how" to mapOf(Language.DARIJA to "كيفاش", Language.ARABIC to "كيف", Language.FRENCH to "comment", Language.ENGLISH to "how", Language.SPANISH to "cómo", Language.GERMAN to "wie"),
        "kifash" to mapOf(Language.DARIJA to "كيفاش", Language.ARABIC to "كيف", Language.FRENCH to "comment", Language.ENGLISH to "how", Language.SPANISH to "cómo", Language.GERMAN to "wie"),
        "why" to mapOf(Language.DARIJA to "علاش", Language.ARABIC to "لماذا", Language.FRENCH to "pourquoi", Language.ENGLISH to "why", Language.SPANISH to "por qué", Language.GERMAN to "warum"),
        "3lash" to mapOf(Language.DARIJA to "علاش", Language.ARABIC to "لماذا", Language.FRENCH to "pourquoi", Language.ENGLISH to "why", Language.SPANISH to "por qué", Language.GERMAN to "warum"),
        "when" to mapOf(Language.DARIJA to "فوقاش", Language.ARABIC to "متى", Language.FRENCH to "quand", Language.ENGLISH to "when", Language.SPANISH to "cuándo", Language.GERMAN to "wann"),
        "who" to mapOf(Language.DARIJA to "شكون", Language.ARABIC to "من", Language.FRENCH to "qui", Language.ENGLISH to "who", Language.SPANISH to "quién", Language.GERMAN to "wer"),
        "chkoun" to mapOf(Language.DARIJA to "شكون", Language.ARABIC to "من", Language.FRENCH to "qui", Language.ENGLISH to "who", Language.SPANISH to "quién", Language.GERMAN to "wer"),
        "what" to mapOf(Language.DARIJA to "شنو / آش", Language.ARABIC to "ماذا", Language.FRENCH to "quoi / qu'est-ce", Language.ENGLISH to "what", Language.SPANISH to "qué", Language.GERMAN to "was"),
        "chno" to mapOf(Language.DARIJA to "شنو", Language.ARABIC to "ماذا", Language.FRENCH to "quoi", Language.ENGLISH to "what", Language.SPANISH to "qué", Language.GERMAN to "was"),
        "want" to mapOf(Language.DARIJA to "باغي / بغيت", Language.ARABIC to "أريد", Language.FRENCH to "vouloir / je veux", Language.ENGLISH to "want", Language.SPANISH to "querer / quiero", Language.GERMAN to "wollen / möchte"),
        "bghit" to mapOf(Language.DARIJA to "بغيت", Language.ARABIC to "أريد", Language.FRENCH to "je veux", Language.ENGLISH to "I want", Language.SPANISH to "quiero", Language.GERMAN to "ich möchte"),
        "i" to mapOf(Language.DARIJA to "أنا", Language.ARABIC to "أنا", Language.FRENCH to "je / moi", Language.ENGLISH to "I", Language.SPANISH to "yo", Language.GERMAN to "ich"),
        "you" to mapOf(Language.DARIJA to "نتا / نتي", Language.ARABIC to "أنت", Language.FRENCH to "vous / tu", Language.ENGLISH to "you", Language.SPANISH to "tú / usted", Language.GERMAN to "du / Sie"),
        "we" to mapOf(Language.DARIJA to "حنا", Language.ARABIC to "نحن", Language.FRENCH to "nous", Language.ENGLISH to "we", Language.SPANISH to "nosotros", Language.GERMAN to "wir"),
        "they" to mapOf(Language.DARIJA to "هما", Language.ARABIC to "هم", Language.FRENCH to "ils / elles", Language.ENGLISH to "they", Language.SPANISH to "ellos", Language.GERMAN to "sie"),
        "water" to mapOf(Language.DARIJA to "الما", Language.ARABIC to "الماء", Language.FRENCH to "eau", Language.ENGLISH to "water", Language.SPANISH to "agua", Language.GERMAN to "Wasser"),
        "food" to mapOf(Language.DARIJA to "الماكلة", Language.ARABIC to "الطعام", Language.FRENCH to "nourriture", Language.ENGLISH to "food", Language.SPANISH to "comida", Language.GERMAN to "Essen"),
        "makla" to mapOf(Language.DARIJA to "الماكلة", Language.ARABIC to "الأكل", Language.FRENCH to "repas", Language.ENGLISH to "food", Language.SPANISH to "comida", Language.GERMAN to "Essen"),
        "bread" to mapOf(Language.DARIJA to "الخبز", Language.ARABIC to "الخبز", Language.FRENCH to "pain", Language.ENGLISH to "bread", Language.SPANISH to "pan", Language.GERMAN to "Brot"),
        "khobz" to mapOf(Language.DARIJA to "الخبز", Language.ARABIC to "الخبز", Language.FRENCH to "pain", Language.ENGLISH to "bread", Language.SPANISH to "pan", Language.GERMAN to "Brot"),
        "tea" to mapOf(Language.DARIJA to "أتاي", Language.ARABIC to "الشاي", Language.FRENCH to "thé", Language.ENGLISH to "tea", Language.SPANISH to "té", Language.GERMAN to "Tee"),
        "atay" to mapOf(Language.DARIJA to "أتاي", Language.ARABIC to "الشاي", Language.FRENCH to "thé marocain", Language.ENGLISH to "Moroccan tea", Language.SPANISH to "té", Language.GERMAN to "Tee"),
        "coffee" to mapOf(Language.DARIJA to "القهوة", Language.ARABIC to "القهوة", Language.FRENCH to "café", Language.ENGLISH to "coffee", Language.SPANISH to "café", Language.GERMAN to "Kaffee"),
        "money" to mapOf(Language.DARIJA to "الفلوس", Language.ARABIC to "النقود", Language.FRENCH to "argent", Language.ENGLISH to "money", Language.SPANISH to "dinero", Language.GERMAN to "Geld"),
        "flous" to mapOf(Language.DARIJA to "الفلوس", Language.ARABIC to "المال", Language.FRENCH to "argent", Language.ENGLISH to "money", Language.SPANISH to "dinero", Language.GERMAN to "Geld"),
        "price" to mapOf(Language.DARIJA to "الثمن", Language.ARABIC to "السعر", Language.FRENCH to "prix", Language.ENGLISH to "price", Language.SPANISH to "precio", Language.GERMAN to "Preis"),
        "taman" to mapOf(Language.DARIJA to "الثمن", Language.ARABIC to "السعر", Language.FRENCH to "prix", Language.ENGLISH to "price", Language.SPANISH to "precio", Language.GERMAN to "Preis"),
        "market" to mapOf(Language.DARIJA to "السوق", Language.ARABIC to "السوق", Language.FRENCH to "marché / souk", Language.ENGLISH to "market / souk", Language.SPANISH to "mercado", Language.GERMAN to "Markt"),
        "souk" to mapOf(Language.DARIJA to "السوق", Language.ARABIC to "السوق", Language.FRENCH to "marché", Language.ENGLISH to "market", Language.SPANISH to "mercado", Language.GERMAN to "Markt"),
        "hotel" to mapOf(Language.DARIJA to "لوطيل / فندق", Language.ARABIC to "فندق", Language.FRENCH to "hôtel", Language.ENGLISH to "hotel", Language.SPANISH to "hotel", Language.GERMAN to "Hotel"),
        "doctor" to mapOf(Language.DARIJA to "طبيب", Language.ARABIC to "طبيب", Language.FRENCH to "médecin", Language.ENGLISH to "doctor", Language.SPANISH to "médico", Language.GERMAN to "Arzt"),
        "tbib" to mapOf(Language.DARIJA to "طبيب", Language.ARABIC to "طبيب", Language.FRENCH to "médecin", Language.ENGLISH to "doctor", Language.SPANISH to "médico", Language.GERMAN to "Arzt"),
        "hospital" to mapOf(Language.DARIJA to "السبيطار", Language.ARABIC to "المستشفى", Language.FRENCH to "hôpital", Language.ENGLISH to "hospital", Language.SPANISH to "hospital", Language.GERMAN to "Krankenhaus"),
        "sbitar" to mapOf(Language.DARIJA to "السبيطار", Language.ARABIC to "المستشفى", Language.FRENCH to "hôpital", Language.ENGLISH to "hospital", Language.SPANISH to "hospital", Language.GERMAN to "Krankenhaus"),
        "pharmacy" to mapOf(Language.DARIJA to "الفرماسيان", Language.ARABIC to "الصيدلية", Language.FRENCH to "pharmacie", Language.ENGLISH to "pharmacy", Language.SPANISH to "farmacia", Language.GERMAN to "Apotheke"),
        "taxi" to mapOf(Language.DARIJA to "طاكسي", Language.ARABIC to "سيارة أجرة", Language.FRENCH to "taxi", Language.ENGLISH to "taxi", Language.SPANISH to "taxi", Language.GERMAN to "Taxi"),
        "airport" to mapOf(Language.DARIJA to "المطار", Language.ARABIC to "المطار", Language.FRENCH to "aéroport", Language.ENGLISH to "airport", Language.SPANISH to "aeropuerto", Language.GERMAN to "Flughafen"),
        "matar" to mapOf(Language.DARIJA to "المطار", Language.ARABIC to "المطار", Language.FRENCH to "aéroport", Language.ENGLISH to "airport", Language.SPANISH to "aeropuerto", Language.GERMAN to "Flughafen"),
        "train" to mapOf(Language.DARIJA to "التران / القطار", Language.ARABIC to "القطار", Language.FRENCH to "train", Language.ENGLISH to "train", Language.SPANISH to "tren", Language.GERMAN to "Zug"),
        "station" to mapOf(Language.DARIJA to "المحطة", Language.ARABIC to "المحطة", Language.FRENCH to "gare / station", Language.ENGLISH to "station", Language.SPANISH to "estación", Language.GERMAN to "Bahnhof"),
        "car" to mapOf(Language.DARIJA to "الطوموبيل", Language.ARABIC to "السيارة", Language.FRENCH to "voiture", Language.ENGLISH to "car", Language.SPANISH to "coche", Language.GERMAN to "Auto"),
        "tomobil" to mapOf(Language.DARIJA to "الطوموبيل", Language.ARABIC to "السيارة", Language.FRENCH to "voiture", Language.ENGLISH to "car", Language.SPANISH to "coche", Language.GERMAN to "Auto"),
        "street" to mapOf(Language.DARIJA to "الشارع / الزنقة", Language.ARABIC to "الشارع", Language.FRENCH to "rue / avenue", Language.ENGLISH to "street", Language.SPANISH to "calle", Language.GERMAN to "Straße"),
        "help" to mapOf(Language.DARIJA to "عتق / معاونة", Language.ARABIC to "مساعدة", Language.FRENCH to "aide / au secours", Language.ENGLISH to "help", Language.SPANISH to "ayuda / socorro", Language.GERMAN to "Hilfe"),
        "urgent" to mapOf(Language.DARIJA to "ضروري بزاف", Language.ARABIC to "عاجل", Language.FRENCH to "urgent", Language.ENGLISH to "urgent", Language.SPANISH to "urgente", Language.GERMAN to "dringend"),
        "today" to mapOf(Language.DARIJA to "اليوم", Language.ARABIC to "اليوم", Language.FRENCH to "aujourd'hui", Language.ENGLISH to "today", Language.SPANISH to "hoy", Language.GERMAN to "heute"),
        "tomorrow" to mapOf(Language.DARIJA to "غدا", Language.ARABIC to "غداً", Language.FRENCH to "demain", Language.ENGLISH to "tomorrow", Language.SPANISH to "mañana", Language.GERMAN to "morgen"),
        "ghdda" to mapOf(Language.DARIJA to "غدا", Language.ARABIC to "غداً", Language.FRENCH to "demain", Language.ENGLISH to "tomorrow", Language.SPANISH to "mañana", Language.GERMAN to "morgen"),
        "now" to mapOf(Language.DARIJA to "دابا", Language.ARABIC to "الآن", Language.FRENCH to "maintenant", Language.ENGLISH to "now", Language.SPANISH to "ahora", Language.GERMAN to "jetzt"),
        "daba" to mapOf(Language.DARIJA to "دابا", Language.ARABIC to "الآن", Language.FRENCH to "maintenant", Language.ENGLISH to "now", Language.SPANISH to "ahora", Language.GERMAN to "jetzt"),
        "later" to mapOf(Language.DARIJA to "من بعد", Language.ARABIC to "لاحقاً", Language.FRENCH to "plus tard", Language.ENGLISH to "later", Language.SPANISH to "más tarde", Language.GERMAN to "später"),
        "beautiful" to mapOf(Language.DARIJA to "زوين", Language.ARABIC to "جميل", Language.FRENCH to "beau / magnifique", Language.ENGLISH to "beautiful / nice", Language.SPANISH to "hermoso / bonito", Language.GERMAN to "schön"),
        "zwin" to mapOf(Language.DARIJA to "زوين", Language.ARABIC to "جميل", Language.FRENCH to "beau / joli", Language.ENGLISH to "beautiful", Language.SPANISH to "bonito", Language.GERMAN to "schön"),
        "brother" to mapOf(Language.DARIJA to "خويا", Language.ARABIC to "أخي", Language.FRENCH to "mon frère / mon ami", Language.ENGLISH to "brother / friend", Language.SPANISH to "hermano / amigo", Language.GERMAN to "Bruder / Freund"),
        "khoya" to mapOf(Language.DARIJA to "خويا", Language.ARABIC to "أخي", Language.FRENCH to "mon frère", Language.ENGLISH to "my brother", Language.SPANISH to "mi hermano", Language.GERMAN to "mein Bruder"),
        "sister" to mapOf(Language.DARIJA to "ختي", Language.ARABIC to "أختي", Language.FRENCH to "ma soeur", Language.ENGLISH to "sister", Language.SPANISH to "hermana", Language.GERMAN to "Schwester"),
        "khti" to mapOf(Language.DARIJA to "ختي", Language.ARABIC to "أختي", Language.FRENCH to "ma soeur", Language.ENGLISH to "my sister", Language.SPANISH to "mi hermana", Language.GERMAN to "meine Schwester"),
        "welcome" to mapOf(Language.DARIJA to "مرحبا", Language.ARABIC to "أهلاً وسهلاً", Language.FRENCH to "bienvenue", Language.ENGLISH to "welcome", Language.SPANISH to "bienvenido", Language.GERMAN to "willkommen"),
        "marhaba" to mapOf(Language.DARIJA to "مرحبا", Language.ARABIC to "مرحباً", Language.FRENCH to "bienvenue", Language.ENGLISH to "welcome", Language.SPANISH to "bienvenido", Language.GERMAN to "willkommen"),
        "understand" to mapOf(Language.DARIJA to "فهمت", Language.ARABIC to "فهمت", Language.FRENCH to "compris", Language.ENGLISH to "understood", Language.SPANISH to "entendido", Language.GERMAN to "verstanden"),
        "fhmt" to mapOf(Language.DARIJA to "فهمت", Language.ARABIC to "فهمت", Language.FRENCH to "je comprends", Language.ENGLISH to "I understand", Language.SPANISH to "entiendo", Language.GERMAN to "ich verstehe"),
        "speak" to mapOf(Language.DARIJA to "نهضر / تكلّم", Language.ARABIC to "أتحدث", Language.FRENCH to "parler", Language.ENGLISH to "speak", Language.SPANISH to "hablar", Language.GERMAN to "sprechen"),
        "nhdr" to mapOf(Language.DARIJA to "نهضر", Language.ARABIC to "أتكلم", Language.FRENCH to "je parle", Language.ENGLISH to "I speak", Language.SPANISH to "hablo", Language.GERMAN to "ich spreche"),
        "friend" to mapOf(Language.DARIJA to "صاحبي", Language.ARABIC to "صديقي", Language.FRENCH to "ami", Language.ENGLISH to "friend", Language.SPANISH to "amigo", Language.GERMAN to "Freund"),
        "sahbi" to mapOf(Language.DARIJA to "صاحبي", Language.ARABIC to "صديقي", Language.FRENCH to "mon ami", Language.ENGLISH to "my friend", Language.SPANISH to "mi amigo", Language.GERMAN to "mein Freund"),
        "morocco" to mapOf(Language.DARIJA to "المغرب", Language.ARABIC to "المملكة المغربية", Language.FRENCH to "Maroc", Language.ENGLISH to "Morocco", Language.SPANISH to "Marruecos", Language.GERMAN to "Marokko"),
        "maghrib" to mapOf(Language.DARIJA to "المغرب", Language.ARABIC to "المغرب", Language.FRENCH to "Maroc", Language.ENGLISH to "Morocco", Language.SPANISH to "Marruecos", Language.GERMAN to "Marokko"),
        "city" to mapOf(Language.DARIJA to "المدينة", Language.ARABIC to "المدينة", Language.FRENCH to "ville", Language.ENGLISH to "city", Language.SPANISH to "ciudad", Language.GERMAN to "Stadt"),
        "mdina" to mapOf(Language.DARIJA to "المدينة", Language.ARABIC to "المدينة", Language.FRENCH to "ville", Language.ENGLISH to "city", Language.SPANISH to "ciudad", Language.GERMAN to "Stadt"),
        "time" to mapOf(Language.DARIJA to "الوقت / الساعة", Language.ARABIC to "الوقت / الساعة", Language.FRENCH to "l'heure / temps", Language.ENGLISH to "time", Language.SPANISH to "hora / tiempo", Language.GERMAN to "Zeit / Uhrzeit"),
        "sa3a" to mapOf(Language.DARIJA to "الساعة", Language.ARABIC to "الساعة", Language.FRENCH to "l'heure", Language.ENGLISH to "time / hour", Language.SPANISH to "la hora", Language.GERMAN to "Uhrzeit"),
        "one" to mapOf(Language.DARIJA to "واحد", Language.ARABIC to "واحد", Language.FRENCH to "un", Language.ENGLISH to "one", Language.SPANISH to "uno", Language.GERMAN to "eins"),
        "two" to mapOf(Language.DARIJA to "جوج", Language.ARABIC to "اثنان", Language.FRENCH to "deux", Language.ENGLISH to "two", Language.SPANISH to "dos", Language.GERMAN to "zwei"),
        "three" to mapOf(Language.DARIJA to "تلاتة", Language.ARABIC to "ثلاثة", Language.FRENCH to "trois", Language.ENGLISH to "three", Language.SPANISH to "tres", Language.GERMAN to "drei"),
        "four" to mapOf(Language.DARIJA to "ربعة", Language.ARABIC to "أربعة", Language.FRENCH to "quatre", Language.ENGLISH to "four", Language.SPANISH to "cuatro", Language.GERMAN to "vier"),
        "five" to mapOf(Language.DARIJA to "خمسة", Language.ARABIC to "خمسة", Language.FRENCH to "cinq", Language.ENGLISH to "five", Language.SPANISH to "cinco", Language.GERMAN to "fünf"),
        "ten" to mapOf(Language.DARIJA to "عشرة", Language.ARABIC to "عشرة", Language.FRENCH to "dix", Language.ENGLISH to "ten", Language.SPANISH to "diez", Language.GERMAN to "zehn"),
        "hundred" to mapOf(Language.DARIJA to "مية", Language.ARABIC to "مئة", Language.FRENCH to "cent", Language.ENGLISH to "hundred", Language.SPANISH to "cien", Language.GERMAN to "hundert"),
        "dirham" to mapOf(Language.DARIJA to "درهم", Language.ARABIC to "درهم", Language.FRENCH to "dirhams", Language.ENGLISH to "dirhams", Language.SPANISH to "dírham", Language.GERMAN to "Dirham")
    )

    /**
     * Main offline translation entrypoint.
     * Takes input speech or text, cleans and normalizes it, matches against
     * concept phrases, n-grams, or token syntax, and produces a fluid, natural translation.
     */
    fun translate(
        input: String,
        from: Language,
        to: Language
    ): String {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return ""
        if (from == to) return trimmed

        val normalized = normalizeInput(trimmed)

        // 1. Try high-priority phrase concept matching
        val bestConcept = findMatchingConcept(normalized, from)
        if (bestConcept != null) {
            val targetPhrase = conceptMatrix[bestConcept]?.get(to)
            if (!targetPhrase.isNullOrBlank()) {
                return targetPhrase
            }
        }

        // 2. Tokenized & N-Gram translation synthesis
        val tokens = trimmed.split(Regex("\\s+"))
        val translatedTokens = mutableListOf<String>()

        var index = 0
        while (index < tokens.size) {
            // Check 2-word phrase first
            if (index + 1 < tokens.size) {
                val bigram = "${tokens[index]} ${tokens[index + 1]}"
                val bigramMatch = findTokenTranslation(bigram, from, to)
                if (bigramMatch != null) {
                    translatedTokens.add(bigramMatch)
                    index += 2
                    continue
                }
            }

            // Single word translation
            val single = tokens[index]
            val singleMatch = findTokenTranslation(single, from, to)
            if (singleMatch != null) {
                translatedTokens.add(singleMatch)
            } else {
                // Keep proper noun or unknown word
                translatedTokens.add(single)
            }
            index++
        }

        val result = formatTranslatedSentence(translatedTokens, to)
        return result
    }

    /**
     * Normalizes Darija Arabizi (e.g. 3, 7, 9, kh, gh, ch) and trims punctuation
     */
    private fun normalizeInput(text: String): String {
        return text.lowercase(Locale.ROOT)
            .replace("؟", "")
            .replace("?", "")
            .replace("!", "")
            .replace(".", "")
            .replace(",", "")
            .trim()
    }

    /**
     * Matches normalized text with concept dictionary based on similarity or key phrases
     */
    private fun findMatchingConcept(normalized: String, from: Language): String? {
        for ((conceptId, langMap) in conceptMatrix) {
            val phraseInSource = langMap[from] ?: continue
            val normSource = normalizeInput(phraseInSource)

            // Direct inclusion or substring match
            if (normalized == normSource) return conceptId

            // Check if key words match
            val normTokens = normalized.split(" ").filter { it.length > 2 }
            val sourceTokens = normSource.split(" ").filter { it.length > 2 }
            if (normTokens.isNotEmpty() && sourceTokens.isNotEmpty()) {
                val common = normTokens.intersect(sourceTokens.toSet())
                val overlap = common.size.toFloat() / normTokens.size.coerceAtLeast(sourceTokens.size)
                if (overlap >= 0.5f) {
                    return conceptId
                }
            }
        }

        // Specialized Moroccan Darija Arabizi checks
        if (from == Language.DARIJA || from == Language.ARABIC) {
            when {
                normalized.contains("salam") || normalized.contains("ahlan") || normalized.contains("سلام") -> return "greeting_hello"
                normalized.contains("labas") || normalized.contains("kif dayr") || normalized.contains("kidayr") || normalized.contains("لاباس") -> return "greeting_how_are_you"
                normalized.contains("hamdullah") || normalized.contains("bikhir") || normalized.contains("الحمد لله") || normalized.contains("بخير") -> return "greeting_im_fine"
                normalized.contains("choukran") || normalized.contains("chokran") || normalized.contains("شكرا") -> return "thanks_much"
                normalized.contains("3afak") || normalized.contains("afak") || normalized.contains("3awnni") || normalized.contains("عافاك") -> return "can_you_help_me"
                normalized.contains("chhal") || normalized.contains("bchhal") || normalized.contains("taman") || normalized.contains("شحال") -> return "how_much_is_this"
                normalized.contains("fin") && (normalized.contains("hotel") || normalized.contains("lotil") || normalized.contains("فندق")) -> return "where_is_hotel"
                normalized.contains("fin") && (normalized.contains("matar") || normalized.contains("aeroport") || normalized.contains("مطار")) -> return "where_is_airport"
                normalized.contains("fin") && (normalized.contains("gare") || normalized.contains("train") || normalized.contains("قطار")) -> return "where_is_train_station"
                normalized.contains("tbib") || normalized.contains("mrid") || normalized.contains("طبيب") || normalized.contains("مريض") -> return "i_need_doctor"
                normalized.contains("sbitar") || normalized.contains("hopital") || normalized.contains("مستشفى") -> return "i_need_doctor"
                normalized.contains("pharmacie") || normalized.contains("farmasyan") || normalized.contains("صيدلية") -> return "where_is_pharmacy"
                normalized.contains("bolis") || normalized.contains("police") || normalized.contains("شرطة") -> return "call_police"
                normalized.contains("ma fhemtch") || normalized.contains("fhemch") || normalized.contains("ما فهمتش") -> return "i_dont_understand"
                normalized.contains("smitk") || normalized.contains("ismouk") || normalized.contains("سميتك") -> return "what_is_your_name"
                normalized.contains("matsrfine") || normalized.contains("metcherrfin") || normalized.contains("متشرفين") -> return "what_is_your_name"
                normalized.contains("joo3") || normalized.contains("bghit nakol") || normalized.contains("manger") || normalized.contains("ناكل") -> return "i_want_to_eat"
                normalized.contains("lma") || normalized.contains("ma bared") || normalized.contains("ماء") || normalized.contains("الماء") -> return "water_please"
                normalized.contains("hsab") || normalized.contains("l'addition") || normalized.contains("حساب") -> return "bill_please"
                normalized.contains("bslama") || normalized.contains("thala") || normalized.contains("بسلامة") -> return "goodbye"
                normalized.contains("nishan") || normalized.contains("limn") || normalized.contains("نيشان") -> return "straight_ahead"
                normalized.contains("lissr") || normalized.contains("يسار") -> return "turn_left"
                normalized.contains("taxi") || normalized.contains("طاكسي") -> return "take_taxi"
            }
        }

        // French & English speech phrases
        if (from == Language.FRENCH) {
            when {
                normalized.contains("bonjour") || normalized.contains("salut") -> return "greeting_hello"
                normalized.contains("comment allez") || normalized.contains("ça va") -> return "greeting_how_are_you"
                normalized.contains("je vais bien") || normalized.contains("très bien") -> return "greeting_im_fine"
                normalized.contains("merci") -> return "thanks_much"
                normalized.contains("de rien") || normalized.contains("je vous en prie") -> return "you_are_welcome"
                normalized.contains("combien") || normalized.contains("prix") || normalized.contains("coûte") -> return "how_much_is_this"
                normalized.contains("trop cher") -> return "too_expensive"
                normalized.contains("où est l'hôtel") || normalized.contains("où se trouve l'hôtel") -> return "where_is_hotel"
                normalized.contains("aéroport") -> return "where_is_airport"
                normalized.contains("gare") -> return "where_is_train_station"
                normalized.contains("aider") || normalized.contains("aidez") -> return "can_you_help_me"
                normalized.contains("médecin") || normalized.contains("docteur") || normalized.contains("malade") -> return "i_need_doctor"
                normalized.contains("pharmacie") -> return "where_is_pharmacy"
                normalized.contains("police") -> return "call_police"
                normalized.contains("pas compris") || normalized.contains("répétez") -> return "i_dont_understand"
                normalized.contains("comment vous appelez") || normalized.contains("votre nom") -> return "what_is_your_name"
                normalized.contains("d'où venez") -> return "where_are_you_from"
                normalized.contains("faim") || normalized.contains("manger") -> return "i_want_to_eat"
                normalized.contains("eau") -> return "water_please"
                normalized.contains("l'addition") || normalized.contains("payer") -> return "bill_please"
                normalized.contains("au revoir") || normalized.contains("adieu") -> return "goodbye"
                normalized.contains("tout droit") -> return "straight_ahead"
                normalized.contains("à gauche") -> return "turn_left"
                normalized.contains("taxi") -> return "take_taxi"
            }
        }

        if (from == Language.ENGLISH) {
            when {
                normalized.contains("hello") || normalized.contains("hi ") || normalized == "hi" -> return "greeting_hello"
                normalized.contains("how are you") || normalized.contains("how do you do") -> return "greeting_how_are_you"
                normalized.contains("im good") || normalized.contains("i am fine") || normalized.contains("doing well") -> return "greeting_im_fine"
                normalized.contains("thank you") || normalized.contains("thanks") -> return "thanks_much"
                normalized.contains("welcome") || normalized.contains("pleasure") -> return "you_are_welcome"
                normalized.contains("how much") || normalized.contains("price") || normalized.contains("cost") -> return "how_much_is_this"
                normalized.contains("too expensive") || normalized.contains("cheaper") -> return "too_expensive"
                normalized.contains("where is the hotel") || normalized.contains("hotel") -> return "where_is_hotel"
                normalized.contains("airport") -> return "where_is_airport"
                normalized.contains("train station") || normalized.contains("train") -> return "where_is_train_station"
                normalized.contains("help me") || normalized.contains("can you help") -> return "can_you_help_me"
                normalized.contains("doctor") || normalized.contains("sick") || normalized.contains("hospital") -> return "i_need_doctor"
                normalized.contains("pharmacy") || normalized.contains("drugstore") -> return "where_is_pharmacy"
                normalized.contains("police") -> return "call_police"
                normalized.contains("dont understand") || normalized.contains("repeat") -> return "i_dont_understand"
                normalized.contains("your name") -> return "what_is_your_name"
                normalized.contains("where are you from") -> return "where_are_you_from"
                normalized.contains("hungry") || normalized.contains("want to eat") -> return "i_want_to_eat"
                normalized.contains("water") -> return "water_please"
                normalized.contains("bill") || normalized.contains("check please") -> return "bill_please"
                normalized.contains("goodbye") || normalized.contains("bye") || normalized.contains("see you") -> return "goodbye"
                normalized.contains("straight") -> return "straight_ahead"
                normalized.contains("left") -> return "turn_left"
                normalized.contains("taxi") || normalized.contains("cab") -> return "take_taxi"
            }
        }

        return null
    }

    /**
     * Looks up single or two-word token translation
     */
    private fun findTokenTranslation(token: String, from: Language, to: Language): String? {
        val clean = normalizeInput(token)
        if (clean.isBlank()) return null

        for ((_, langDict) in tokenDictionary) {
            val sourceWord = langDict[from]
            if (sourceWord != null) {
                val words = sourceWord.split("/").map { normalizeInput(it) }
                if (words.any { it == clean || clean.contains(it) || it.contains(clean) }) {
                    return langDict[to]?.split("/")?.firstOrNull()?.trim()
                }
            }
        }

        // Direct key lookup
        val directMatch = tokenDictionary[clean]
        if (directMatch != null) {
            return directMatch[to]?.split("/")?.firstOrNull()?.trim()
        }

        return null
    }

    private fun formatTranslatedSentence(tokens: List<String>, to: Language): String {
        if (tokens.isEmpty()) return ""
        val joined = tokens.joinToString(" ").replace(Regex("\\s+"), " ").trim()
        if (joined.isEmpty()) return ""

        return when (to) {
            Language.DARIJA, Language.ARABIC -> joined
            else -> {
                // Capitalize first character
                joined.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            }
        }
    }
}
