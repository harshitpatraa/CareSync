package com.caresync.app.ai

import com.caresync.app.model.AppLanguage

/**
 * Medically reviewed local health knowledge base compiled strictly from official
 * Ministry of Health and Family Welfare (MoHFW) & National Health Mission (NHM)
 * ASHA Modules (Modules 1 to 7, IMNCI, HBNC, and Anemia Mukt Bharat guidelines).
 *
 * Runs 100% offline with zero network connectivity. Supports English, Hindi (Devanagari),
 * and Romanized Hindi (Hinglish).
 */
data class MedicalKnowledgeItem(
    val id: String,
    val category: String,
    val subCategory: String,
    val keywords: List<String>,
    val englishTitle: String,
    val englishContent: String,
    val hindiTitle: String,
    val hindiContent: String,
    val dangerSigns: List<String> = emptyList(),
    val ashaActionPoints: List<String> = emptyList(),
    val isEmergency: Boolean = false
)

object LocalMedicalKnowledgeBase {

    val knowledgeItems: List<MedicalKnowledgeItem> = listOf(
        // ==========================================
        // 1. PREGNANCY & ANTENATAL CARE (ANC)
        // ==========================================
        MedicalKnowledgeItem(
            id = "anc_checkups",
            category = "Pregnancy & Maternal Health",
            subCategory = "Antenatal Checkups",
            keywords = listOf(
                "anc", "pregnancy", "antenatal", "checkup", "garbh", "garbhavastha",
                "jaanch", "trimester", "doctor visit", "mahina", "pregnant", "sonography"
            ),
            englishTitle = "Antenatal Care (ANC) Mandatory Checkups (MoHFW Guidelines)",
            englishContent = "According to National Health Mission guidelines, every pregnant woman must complete at least 4 mandatory ANC checkups:\n\n" +
                "1. 1st Checkup: Within first 12 weeks (early pregnancy registration, confirmation, baseline BP, weight, Hb, and urine test).\n" +
                "2. 2nd Checkup: Between 14th and 26th week (measure fundal height, fetal heart sound, BP, screen for gestational diabetes).\n" +
                "3. 3rd Checkup: Between 28th and 34th week (track fetal growth, BP, check for preeclampsia/edema, ensure IFA and Calcium intake).\n" +
                "4. 4th Checkup: At 36th week to delivery (assess fetal presentation, identify high-risk conditions, plan institutional delivery).\n\n" +
                "Mandatory Vaccinations: 2 doses of Tetanus & adult Diphtheria (Td) toxoid injection (minimum 4 weeks apart).",
            hindiTitle = "प्रसव पूर्व जांच (ANC) - राष्ट्रीय स्वास्थ्य मिशन दिशानिर्देश",
            hindiContent = "राष्ट्रीय स्वास्थ्य मिशन के अनुसार हर गर्भवती महिला को कम से कम 4 प्रसव पूर्व जांचें अवश्य करानी चाहिए:\n\n" +
                "1. पहली जांच: 12 सप्ताह के भीतर (गर्भावस्था का शीघ्र पंजीकरण, वजन, बीपी, खून (हीमोग्लोबिन) व पेशाब की जांच)।\n" +
                "2. दूसरी जांच: 14 से 26 सप्ताह के बीच (शिशु की धड़कन, बीपी तथा पेट का घेरा नापना)।\n" +
                "3. तीसरी जांच: 28 से 34 सप्ताह के बीच (शिशु का विकास, सूजन तथा उच्च रक्तचाप की निगरानी)।\n" +
                "4. चौथी जांच: 36वें सप्ताह से प्रसव तक (शिशु की स्थिति जांचना तथा संस्थागत प्रसव की तैयारी)।\n\n" +
                "टीकाकरण: टिटनेस एवं डिप्थीरिया (Td) के 2 टीके 4 सप्ताह के अंतराल पर लगाएं।",
            dangerSigns = listOf(
                "Severe headache with blurred vision (गंभीर सिरदर्द और धुंधला दिखना)",
                "Swelling on face, hands, and feet (चेहरे और पैरों में अचानक तेज सूजन)",
                "Vaginal bleeding or watery leaking (योनि से रक्तस्राव या पानी बहना)"
            ),
            ashaActionPoints = listOf(
                "Register pregnancy on RCH portal within 12 weeks.",
                "Ensure institutional delivery plan under Pradhan Mantri Matru Vandana Yojana (PMMVY) / JSY.",
                "Provide MCP Card (Mother and Child Protection Card)."
            )
        ),

        MedicalKnowledgeItem(
            id = "anc_ifa_calcium",
            category = "Pregnancy & Maternal Health",
            subCategory = "IFA & Calcium Supplementation",
            keywords = listOf(
                "ifa", "iron", "calcium", "folic acid", "anemia", "khoon ki kami",
                "tablet", "dawa", "lal goli", "chuna", "supplement", "poshan"
            ),
            englishTitle = "IFA & Calcium Supplementation in Pregnancy (Anemia Mukt Bharat)",
            englishContent = "Essential daily micronutrient regimen for pregnant mothers:\n\n" +
                "1. Iron & Folic Acid (IFA - Red Tablet):\n" +
                "   • Dose: 1 tablet daily (100 mg elemental Iron + 500 mcg Folic Acid) starting from 14th week (2nd trimester) for minimum 180 days during pregnancy, and continued for 180 days postpartum.\n" +
                "   • Instructions: Take strictly after food with water or citrus fruit (lemon water/amla). Do not take with milk, tea, or coffee as they block iron absorption.\n" +
                "   • Note: Stools may turn dark black; reassure the mother that this is completely normal.\n\n" +
                "2. Calcium Tablet (500 mg Calcium + 250 IU Vit D3):\n" +
                "   • Dose: 2 tablets daily from 14 weeks onwards until 6 months of breastfeeding.\n" +
                "   • Critical Rule: NEVER take Iron and Calcium tablets together! Take Calcium in morning after breakfast, and IFA after lunch/dinner (minimum 2 hours gap).",
            hindiTitle = "गर्भावस्था में आयरन (IFA) एवं कैल्शियम की खुराक",
            hindiContent = "गर्भवती माताओं के लिए दैनिक पोषण खुराक दिशानिर्देश:\n\n" +
                "1. आयरन एवं फोलिक एसिड (लाल गोली - IFA):\n" +
                "   • खुराक: 1 गोली प्रतिदिन (100 मि.ग्रा. आयरन + 500 माइक्रोग्राम फोलिक एसिड) चौथे महीने (14 सप्ताह) से शुरू कर प्रसव तक कम से कम 180 दिन, और प्रसव के बाद भी 180 दिन लें।\n" +
                "   • निर्देश: भोजन के बाद पानी या नींबू पानी/आंवला के साथ लें। चाय या दूध के साथ कभी न लें। मल का काला होना सामान्य है।\n\n" +
                "2. कैल्शियम की गोली (500 मि.ग्रा.):\n" +
                "   • खुराक: प्रतिदिन 2 गोलियां 14वें सप्ताह से शुरू कर शिशु के 6 माह का होने तक लें।\n" +
                "   • महत्वपूर्ण नियम: आयरन और कैल्शियम की गोली कभी भी एक साथ न खाएं! दोनों के बीच कम से कम 2 घंटे का अंतर रखें।",
            dangerSigns = listOf(
                "Extreme pallor / white palms, conjunctiva (अत्यधिक पीलापन / गंभीर एनीमिया Hb < 7 g/dL)",
                "Breathlessness on mild resting (हल्के काम पर भी सांस फूलना)"
            ),
            ashaActionPoints = listOf(
                "Distribute 180 IFA red tablets and 360 Calcium tablets.",
                "Administer single dose of Tab Albendazole 400 mg in 2nd trimester for deworming.",
                "Encourage iron-rich diet: spinach, jaggery (gur), roasted chana, green leafy vegetables."
            )
        ),

        MedicalKnowledgeItem(
            id = "maternal_danger_signs",
            category = "Pregnancy & Maternal Health",
            subCategory = "Danger Signs in Pregnancy",
            keywords = listOf(
                "danger signs", "emergency", "khatra", "bleeding", "convulsion", "fits",
                "headache", "preeclampsia", "chakkar", "vomiting", "khoon", "dard"
            ),
            englishTitle = "Obstetric Emergency Warning Signs (Immediate Referral)",
            englishContent = "If a pregnant mother exhibits ANY of the following 6 danger signs, it is a medical emergency requiring immediate 108 ambulance dispatch and referral to FRU / District Hospital:\n\n" +
                "1. Vaginal Bleeding (Per Vaginum) at any stage of pregnancy (threat of abortion, placenta previa, abruptio placentae).\n" +
                "2. Convulsions or Fits (Eclampsia) — sudden loss of consciousness, jerky limb movements.\n" +
                "3. Severe persistent headache with blurred vision or spots before eyes (Severe Preeclampsia).\n" +
                "4. High fever (>38.5°C / 101°F) with foul-smelling vaginal discharge (maternal sepsis).\n" +
                "5. Leaking of amniotic fluid without active labor pains (premature rupture of membranes).\n" +
                "6. Absent or significantly reduced fetal movements in 3rd trimester (<10 movements in 2 hours).\n\n" +
                "Immediate First Aid: Place mother on left side, do not insert anything in mouth during convulsions, call 108 ambulance immediately.",
            hindiTitle = "गर्भावस्था के खतरे के लक्षण (तत्काल अस्पताल रेफरल)",
            hindiContent = "गर्भवती महिला में इनमें से कोई भी लक्षण दिखने पर तुरंत 108 एम्बुलेंस बुलाएं और नजदीकी सीएचसी/जिला अस्पताल रेफर करें:\n\n" +
                "1. योनि से थोड़ा भी खून बहना (रक्तस्राव)।\n" +
                "2. दौरे या झटके आना (एक्लेम्पसिया / बेहोशी)।\n" +
                "3. तेज सिरदर्द के साथ आंखों के आगे धुंधलापन या तारे दिखना (अत्यधिक उच्च रक्तचाप)।\n" +
                "4. तेज बुखार के साथ बदबूदार पानी या स्राव आना।\n" +
                "5. प्रसव दर्द शुरू हुए बिना ही योनि से पानी का छूटना।\n" +
                "6. शिशु की हलचल बंद होना या बहुत कम होना (2 घंटे में 10 से कम हलचल)।\n\n" +
                "प्राथमिक सहायता: महिला को बाईं करवट लिटाएं, दौरे के समय मुंह में कुछ न डालें, 108 एम्बुलेंस से तुरंत अस्पताल भेजें।",
            dangerSigns = listOf(
                "Vaginal bleeding (योनि से रक्त बहना)",
                "Convulsions / Fits (दौरे पड़ना)",
                "BP >= 160/110 mmHg (गंभीर उच्च रक्तचाप)"
            ),
            ashaActionPoints = listOf(
                "Call 108 Emergency Ambulance immediately.",
                "Accompany the mother to the First Referral Unit (FRU).",
                "Carry the MCP Card and ensure a blood donor accompanies."
            ),
            isEmergency = true
        ),

        // ==========================================
        // 2. BREASTFEEDING & INFANT FEEDING
        // ==========================================
        MedicalKnowledgeItem(
            id = "breastfeeding_guidelines",
            category = "Infant & Child Health",
            subCategory = "Breastfeeding Guidelines",
            keywords = listOf(
                "breastfeeding", "doodh", "sthanpan", "colostrum", "peela doodh",
                "exclusive breastfeeding", "feed", "baby feed", "ghutti", "honey"
            ),
            englishTitle = "Optimal Breastfeeding Guidelines (National Health Mission)",
            englishContent = "Essential principles of newborn infant feeding:\n\n" +
                "1. Early Initiation within 1 Hour:\n" +
                "   • Put baby to mother's breast within 1 hour of normal delivery (within 2-4 hours of C-section).\n" +
                "   • Colostrum ('First yellow thick milk'): Rich in secretory IgA antibodies, Vitamin A, and protective proteins. It acts as the newborn's first natural vaccine. NEVER discard colostrum!\n\n" +
                "2. Exclusive Breastfeeding for 6 Months (Zero Water):\n" +
                "   • For the first 6 completed months, give ONLY breast milk. No water, no cow/buffalo milk, no honey, no ghutti, no sugar syrup.\n" +
                "   • Breast milk contains 88% water and all required electrolytes, even in hot summer.\n\n" +
                "3. Demand Feeding:\n" +
                "   • Feed at least 8 to 12 times in 24 hours, including day and night.\n" +
                "   • Adequate milk indicators: Baby passes clear urine 6 or more times in 24 hours, sleeps peacefully for 2 hours after feed, and gains weight consistently.",
            hindiTitle = "स्तनपान दिशानिर्देश - राष्ट्रीय स्वास्थ्य मिशन",
            hindiContent = "शिशु के संपूर्ण विकास के लिए स्तनपान के स्वर्णिम नियम:\n\n" +
                "1. जन्म के 1 घंटे के भीतर स्तनपान:\n" +
                "   • सामान्य प्रसव के 1 घंटे के भीतर (सिजेरियन में 2-4 घंटे में) स्तनपान शुरू कराएं।\n" +
                "   • पहला पीला गाढ़ा दूध (कोलोस्ट्रम): यह शिशु का पहला प्राकृतिक टीका है। इसमें रोग प्रतिरोधक क्षमता (एंटीबॉडी) होती है। इसे कभी न फेंकें।\n\n" +
                "2. 6 महीने तक केवल स्तनपान (पानी भी नहीं):\n" +
                "   • पहले 6 माह तक शिशु को केवल मां का दूध दें। घुट्टी, शहद, गाय का दूध या पानी बिल्कुल न दें।\n" +
                "   • मां के दूध में 88% पानी होता है जो गर्मी में भी शिशु की प्यास बुझाने के लिए पर्याप्त है।\n\n" +
                "3. मांग के अनुसार स्तनपान:\n" +
                "   • 24 घंटे में कम से कम 8 से 12 बार (दिन व रात) स्तनपान कराएं।\n" +
                "   • पर्याप्त दूध के लक्षण: शिशु 24 घंटे में 6 या अधिक बार पेशाब करे और वजन लगातार बढ़े।",
            dangerSigns = listOf(
                "Baby unable to suckle or refuses feed (शिशु दूध पीने में असमर्थ)",
                "Dry lips, sunken fontanelle, lethargy (निर्जलीकरण के लक्षण)"
            ),
            ashaActionPoints = listOf(
                "Demonstrate correct latch and positioning (chin touching breast, wide open mouth, lower lip curled outward).",
                "Counsel family against pre-lacteal feeds (honey, janam ghutti).",
                "Ensure burping baby over shoulder after every feed."
            )
        ),

        MedicalKnowledgeItem(
            id = "complementary_feeding",
            category = "Infant & Child Health",
            subCategory = "Complementary Feeding at 6 Months",
            keywords = listOf(
                "complementary feeding", "annaprashan", "poshan", "solid food", "dalia",
                "khichdi", "6 month", "chhah mahina", "upar ka khana", "bachhe ka aahar"
            ),
            englishTitle = "Complementary Feeding at 6 Completed Months (POSHAN Abhiyaan)",
            englishContent = "Initiation of semi-solid food at completed 6 months (Annaprashan):\n\n" +
                "1. Timing: At exactly completed 6 months (180 days). Do not delay beyond 6 months as breast milk alone cannot meet nutritional needs.\n" +
                "2. Consistency: Thick, semi-solid (does not easily run off a tilted spoon). Start with mashed khichdi with ghee, suji kheer with milk/jaggery, mashed banana, boiled mashed potatoes, boiled yellow dal.\n" +
                "3. Frequency & Quantity:\n" +
                "   • 6 to 8 Months: 2–3 tablespoons per feed, 2 to 3 times a day, plus frequent breastfeeds.\n" +
                "   • 9 to 11 Months: 1/2 small katori per feed, 3 to 4 meals a day plus 1 healthy snack (mashed fruit/egg).\n" +
                "   • 12 to 24 Months: 1 full katori per feed, family food chopped/mashed, 3 to 4 meals plus 2 snacks.\n" +
                "4. Hygiene: Wash hands with soap before preparing food and feeding. Use clean, covered utensils. Never use feeding bottles (they cause diarrhea).",
            hindiTitle = "6 माह पूरे होने पर ऊपरी आहार (अन्नप्राशन - पोषण अभियान)",
            hindiContent = "शिशु के 6 माह पूर्ण होने पर ऊपरी आहार शुरू करने के नियम:\n\n" +
                "1. सही समय: 6 माह (180 दिन) पूरे होते ही। केवल मां का दूध अब पर्याप्त नहीं है।\n" +
                "2. भोजन की बनावट: गाढ़ा, मसला हुआ (जो चम्मच से तुरंत न टपके)। मसली हुई दाल-खिचड़ी (घी डालकर), दलिया, सूजी की खीर, मसला केला, उबला आलू।\n" +
                "3. मात्रा और बारम्बारता:\n" +
                "   • 6 से 8 माह: 2-3 चम्मच, दिन में 2 से 3 बार, साथ में स्तनपान जारी रखें।\n" +
                "   • 9 से 11 माह: आधा कटोरी, दिन में 3 से 4 बार, साथ में एक पौष्टिक नाश्ता (फल/अंडा)।\n" +
                "   • 12 से 24 माह: 1 कटोरी, घर का बना खाना, दिन में 3-4 बार भोजन और 2 बार नाश्ता।\n" +
                "4. स्वच्छता: खिलाने से पहले साबुन से हाथ धोएं। बोतल का प्रयोग बिल्कुल न करें (इससे दस्त होते हैं)।",
            dangerSigns = listOf(
                "Persistent vomiting after feeding (बार-बार उल्टी होना)",
                "Weight stagnation or weight loss on growth chart (वजन का घटना या न बढ़ना)"
            ),
            ashaActionPoints = listOf(
                "Celebrate Annaprashan Diwas at Anganwadi on Village Health Sanitation and Nutrition Day (VHSND).",
                "Demonstrate Tiranga Thali (energy food + protein + vitamins).",
                "Emphasize adding 1 teaspoon oil/ghee to energy-dense food."
            )
        ),

        // ==========================================
        // 3. NEWBORN CARE & HBNC
        // ==========================================
        MedicalKnowledgeItem(
            id = "hbnc_home_visits",
            category = "Newborn Care",
            subCategory = "Home-Based Newborn Care (HBNC)",
            keywords = listOf(
                "hbnc", "newborn", "navjat", "shishu", "cord", "umbilical", "temperature",
                "kmc", "kangaroo", "weight", "lbw", "visit", "asha visit"
            ),
            englishTitle = "Home-Based Newborn Care (HBNC) Protocol (7 Visits)",
            englishContent = "ASHA Home Visits schedule for newborn assessment:\n\n" +
                "1. Schedule of Visits:\n" +
                "   • Institutional Deliveries: 6 Visits — Day 3, Day 7, Day 14, Day 21, Day 28, and Day 42.\n" +
                "   • Home Deliveries: 7 Visits — Day 1, Day 3, Day 7, Day 14, Day 21, Day 28, and Day 42.\n" +
                "2. Essential Checks at Every Visit:\n" +
                "   • Check Baby Temperature (Normal 36.5°C to 37.5°C). Avoid hypothermia.\n" +
                "   • Weigh Baby: Normal birth weight is >= 2.5 kg. Low Birth Weight (LBW < 2.5 kg) needs Kangaroo Mother Care.\n" +
                "   • Umbilical Cord: Keep cord stump clean and DRY. No oil, ash, or cow dung.\n" +
                "   • Observe Breastfeeding: Check latch, frequency, and urine output.\n" +
                "   • Immunization status: Verify BCG, OPV-0, and HepB-0.",
            hindiTitle = "गृह आधारित नवजात शिशु देखभाल (HBNC) - आशा गृह भ्रमण",
            hindiContent = "नवजात शिशु की सुरक्षा हेतु आशा के गृह भ्रमण का कार्यक्रम:\n\n" +
                "1. भ्रमण दिवस:\n" +
                "   • अस्पताल में प्रसव: 6 भ्रमण — दिवस 3, 7, 14, 21, 28, और 42.\n" +
                "   • घर पर प्रसव: 7 भ्रमण — दिवस 1, 3, 7, 14, 21, 28, और 42.\n" +
                "2. हर भ्रमण पर मुख्य जांचें:\n" +
                "   • शिशु का तापमान नापें (सामान्य 36.5°C से 37.5°C)। ठंड से बचाएं।\n" +
                "   • वजन तोलें (सामान्य वजन 2.5 कि.ग्रा. या अधिक)। 2.5 कि.ग्रा. से कम होने पर कंगारू मदर केयर (KMC) दें।\n" +
                "   • नाल (नाभि) की देखभाल: नाल को सूखा व साफ रखें। कोई तेल, राख या गोबर न लगाएं।\n" +
                "   • स्तनपान का अवलोकन करें।\n" +
                "   • जन्म के टीके (BCG, OPV-0, Hep-B-0) की पुष्टि करें।",
            dangerSigns = listOf(
                "Temperature < 35.4°C (ठंडा पड़ना / हाइपोथर्मिया)",
                "Fast breathing > 60 breaths/min or chest indrawing (सांस तेज चलना या पसली चलना)",
                "Yellow palms and soles within 24 hours (पीलिया / जांडिस)",
                "Pus or foul smell from umbilical cord (नाभि से पीप आना)"
            ),
            ashaActionPoints = listOf(
                "Teach mother Kangaroo Mother Care (skin-to-skin contact 24 hours).",
                "Ensure baby is weighed using salter scale or digital baby scale.",
                "Refer immediately to SNCU (Special Newborn Care Unit) if danger signs appear."
            )
        ),

        // ==========================================
        // 4. UNIVERSAL IMMUNIZATION PROGRAMME (UIP)
        // ==========================================
        MedicalKnowledgeItem(
            id = "immunization_schedule",
            category = "Immunization",
            subCategory = "National Immunization Schedule (NIS)",
            keywords = listOf(
                "tika", "immunization", "vaccine", "vaccination", "uip", "chart",
                "bcg", "opv", "polio", "pentavalent", "rotavirus", "mr", "measles",
                "dpt", "fipv", "pcv", "schedule", "tikakaran"
            ),
            englishTitle = "National Universal Immunization Schedule (NIS - India)",
            englishContent = "Mandatory vaccination schedule for infants and children:\n\n" +
                "• At Birth: BCG (0.1 mL intradermal left upper arm) + OPV-0 (2 drops oral) + Hepatitis B Birth Dose (within 24 hrs, 0.5 mL IM anterolateral mid-thigh).\n" +
                "• At 6 Weeks: Pentavalent-1 (DPT+HepB+Hib) + OPV-1 + Rotavirus-1 (5 drops) + fIPV-1 (0.1 mL intradermal) + PCV-1 (0.5 mL IM).\n" +
                "• At 10 Weeks: Pentavalent-2 + OPV-2 + Rotavirus-2.\n" +
                "• At 14 Weeks: Pentavalent-3 + OPV-3 + Rotavirus-3 + fIPV-2 + PCV-2.\n" +
                "• At 9–12 Months: MR-1 (Measles-Rubella, 0.5 mL subcutaneous) + JE-1 (in endemic districts) + PCV Booster + Vitamin A 1st Dose (1 lakh IU = 1 mL oral).\n" +
                "• At 16–24 Months: MR-2 + DPT Booster-1 + OPV Booster + JE-2 + Vitamin A 2nd Dose (2 lakh IU).\n" +
                "• At 5–6 Years: DPT Booster-2 (0.5 mL IM).\n" +
                "• At 10 & 16 Years: Td Vaccine (Tetanus & adult Diphtheria, 0.5 mL IM).",
            hindiTitle = "राष्ट्रीय सार्वभौमिक टीकाकरण सारणी (UIP भारत)",
            hindiContent = "शिशुओं और बच्चों के लिए अनिवार्य सरकारी टीकाकरण सारणी:\n\n" +
                "• जन्म के समय: बीसीजी (BCG) + ओपीवी शून्य खुराक (OPV-0) + हेपेटाइटिस-बी जन्म खुराक (24 घंटे में)।\n" +
                "• 6 सप्ताह (डेढ़ माह): पेंटावेलेंट-1 + ओपीवी-1 + रोटावायरस-1 (5 बूंदें) + एफआईपीवी-1 + पीसीवी-1.\n" +
                "• 10 सप्ताह (ढाई माह): पेंटावेलेंट-2 + ओपीवी-2 + रोटावायरस-2.\n" +
                "• 14 सप्ताह (साढ़े तीन माह): पेंटावेलेंट-3 + ओपीवी-3 + रोटावायरस-3 + एफआईपीवी-2 + पीसीवी-2.\n" +
                "• 9 से 12 माह: एमआर-1 (खसरा-रूबेला) + जेई-1 + पीसीवी बूस्टर + विटामिन-ए की पहली खुराक (1 लाख IU).\n" +
                "• 16 से 24 माह: एमआर-2 + डीपीटी बूस्टर-1 + ओपीवी बूस्टर + जेई-2 + विटामिन-ए की दूसरी खुराक (2 लाख IU).\n" +
                "• 5 से 6 वर्ष: डीपीटी बूस्टर-2.\n" +
                "• 10 और 16 वर्ष: टीडी (Td) का टीका।",
            dangerSigns = listOf(
                "Persistent crying > 3 hours or high fever > 40°C post-injection (AEFI - Adverse Event)",
                "Lethargy or seizures post-vaccination (तत्काल पीएचसी सूचित करें)"
            ),
            ashaActionPoints = listOf(
                "Mobilize beneficiaries to the Village Health Sanitation and Nutrition Day (VHSND) session.",
                "Maintain tracking register (Due List) for dropouts and left-outs.",
                "Counsel parents that mild fever post-Pentavalent is normal and treatable with Paracetamol syrup."
            )
        ),

        // ==========================================
        // 5. CHILDHOOD DIARRHEA & DEHYDRATION
        // ==========================================
        MedicalKnowledgeItem(
            id = "child_diarrhea_ors_zinc",
            category = "Child Health & Illnesses",
            subCategory = "Diarrhea Management & ORS-Zinc",
            keywords = listOf(
                "diarrhea", "dast", "loose motion", "ors", "zinc", "dehydration",
                "pani ki kami", "ulti", "vomiting", "kachha dast", "potty"
            ),
            englishTitle = "Management of Acute Diarrhea with ORS & Zinc (MoHFW)",
            englishContent = "Standard 2-step life-saving protocol for childhood diarrhea:\n\n" +
                "1. WHO Oral Rehydration Salts (ORS):\n" +
                "   • Preparation: Dissolve ENTIRE 1 packet in exactly 1 LITER of clean boiled and cooled drinking water. Mix well with a clean spoon.\n" +
                "   • Administration: Give 1/2 to 1 cup (100–200 mL) after every loose watery stool. For babies <2 years: give 50–100 mL using spoon/cup. Do not use feeding bottles.\n" +
                "   • Discard unused prepared ORS solution after 24 hours.\n\n" +
                "2. Zinc Supplementation for 14 Days:\n" +
                "   • Dose for infants 2 to 6 months: 10 mg daily (1/2 tablet dissolved in mother's milk/water).\n" +
                "   • Dose for children 6 months to 5 years: 20 mg daily (1 full dispersible tablet) for a FULL 14 DAYS, even if diarrhea stops after 2 days!\n" +
                "   • Benefit: Repairs damaged intestinal lining, reduces severity, and prevents diarrhea recurrence for the next 3 months.\n\n" +
                "3. Nutrition: Continue breastfeeding and give easily digestible soft foods (khichdi, curd, banana).",
            hindiTitle = "बाल दस्त रोग प्रबंधन - ओआरएस (ORS) एवं जिंक का प्रयोग",
            hindiContent = "बच्चों में दस्त का जीवन रक्षक 2-चरणीय उपचार:\n\n" +
                "1. डब्ल्यूएचओ ओआरएस (ORS) का घोल:\n" +
                "   • बनाने की विधि: 1 पूरा पैकेट ठीक 1 लीटर साफ उबले व ठंडे पानी में घोलें।\n" +
                "   • पिलाने की मात्रा: हर पतले दस्त के बाद आधा से एक कप (100-200 मि.ली.) चम्मच या कटोरी से पिलाएं। 2 वर्ष से छोटे बच्चों को 50-100 मि.ली. पिलाएं।\n" +
                "   • ध्यान दें: बना हुआ घोल 24 घंटे बाद फेंक दें और नया घोल बनाएं।\n\n" +
                "2. जिंक की गोली 14 दिनों तक लगातार:\n" +
                "   • 2 से 6 माह के शिशु: आधी गोली (10 मि.ग्रा.) मां के दूध में घोलकर प्रतिदिन।\n" +
                "   • 6 माह से 5 वर्ष के बच्चे: 1 पूरी गोली (20 मि.ग्रा.) पानी में घोलकर 14 दिनों तक लगातार दें, भले ही दस्त 2 दिन में रुक जाए!\n" +
                "   • लाभ: यह आंतों को ठीक करता है और अगले 3 महीनों तक दोबारा दस्त होने से बचाता है।\n\n" +
                "3. पोषण: स्तनपान व हल्का खाना (खिचड़ी, केला, दही) जारी रखें।",
            dangerSigns = listOf(
                "Blood in stool (पेचिश / लैट्रिन में खून आना)",
                "Sunken eyes, skin pinch retracts very slowly >2 seconds (अत्यधिक पानी की कमी)",
                "Child unable to drink or lethargic (शिशु का सुस्त होना या पीने में असमर्थ होना)"
            ),
            ashaActionPoints = listOf(
                "Demonstrate correct preparation of 1 liter ORS solution to the family.",
                "Ensure child completes the 14-day course of Zinc tablets.",
                "Distribute ORS packets during Intensified Diarrhea Control Fortnight (IDCF)."
            )
        ),

        // ==========================================
        // 6. ACUTE RESPIRATORY INFECTIONS (ARI) & PNEUMONIA
        // ==========================================
        MedicalKnowledgeItem(
            id = "child_pneumonia_ari",
            category = "Child Health & Illnesses",
            subCategory = "ARI & Childhood Pneumonia (IMNCI)",
            keywords = listOf(
                "pneumonia", "cough", "khansi", "breath", "pasli", "chest indrawing",
                "fast breathing", "ari", "saans", "wheeze", "cold", "zukam"
            ),
            englishTitle = "Acute Respiratory Infections & Pneumonia Identification (IMNCI)",
            englishContent = "How to assess a child with cough and fast breathing:\n\n" +
                "1. Count Breaths for 1 Full Minute (Child must be calm/resting):\n" +
                "   • Age < 2 Months: Fast breathing is 60 or more breaths per minute.\n" +
                "   • Age 2 Months to 11 Months: Fast breathing is 50 or more breaths per minute.\n" +
                "   • Age 12 Months to 5 Years: Fast breathing is 40 or more breaths per minute.\n\n" +
                "2. Look for Chest Indrawing:\n" +
                "   • When the child breathes IN, the lower chest wall moves INWARD (sinks in deeply). This is a hallmark of SEVERE PNEUMONIA.\n\n" +
                "3. Stridor or Wheezing:\n" +
                "   • Harsh raspy sound when calm.\n\n" +
                "Triage & Action:\n" +
                "• Cough with No fast breathing: Home care (warm fluids, continue feeding, steam, watch for danger signs).\n" +
                "• Fast breathing: Pneumonia — Refer to Medical Officer for oral antibiotics (Amoxicillin syrup).\n" +
                "• Chest Indrawing or Inability to Drink: SEVERE PNEUMONIA — URGENT hospital referral with pre-referral dose.",
            hindiTitle = "बाल निमोनिया एवं सांस की बीमारी की पहचान (IMNCI)",
            hindiContent = "खांसी व सांस के रोगी बच्चे की जांच कैसे करें:\n\n" +
                "1. पूरे 1 मिनट तक सांस की गति गिनें (जब बच्चा शांत हो):\n" +
                "   • 2 माह से कम उम्र: 60 या अधिक सांस प्रति मिनट = तेज सांस।\n" +
                "   • 2 माह से 11 माह: 50 या अधिक सांस प्रति मिनट = तेज सांस।\n" +
                "   • 12 माह से 5 वर्ष: 40 या अधिक सांस प्रति मिनट = तेज सांस।\n\n" +
                "2. पसली चलना (Chest Indrawing) देखें:\n" +
                "   • सांस अंदर लेते समय छाती का निचला हिस्सा अंदर धंसना। यह गंभीर निमोनिया का प्रमुख लक्षण है।\n\n" +
                "कार्रवाई एवं रेफरल:\n" +
                "• केवल खांसी, सांस सामान्य: घरेलू देखभाल (गुनगुना पानी, स्तनपान जारी रखें, खतरे के लक्षण देखें)।\n" +
                "• तेज सांस: निमोनिया — तुरंत पीएचसी/डॉक्टर के पास ले जाएं (अमोक्सीसिलिन सिरप हेतु)।\n" +
                "• पसली चलना या बच्चा दूध न पी पाना: गंभीर निमोनिया — तत्काल अस्पताल रेफर करें!",
            dangerSigns = listOf(
                "Chest indrawing / stridor in calm child (पसली चलना)",
                "Child unable to drink or breastfeed (बच्चा कुछ भी पीने में असमर्थ)",
                "Cyanosis / blue lips or tongue (होंठों का नीला पड़ना)"
            ),
            ashaActionPoints = listOf(
                "Use ARI timer / stopwatch to accurately count respiratory rate for 60 seconds.",
                "Keep child warm during transport to hospital.",
                "Never give sedative cough syrups to young children."
            ),
            isEmergency = true
        ),

        // ==========================================
        // 7. FEVER, MALARIA & DENGUE
        // ==========================================
        MedicalKnowledgeItem(
            id = "fever_malaria_dengue",
            category = "Common Health Problems",
            subCategory = "Fever, Dengue & Malaria Surveillance",
            keywords = listOf(
                "fever", "bukhar", "dengue", "malaria", "platelet", "chills",
                "shivering", "rdt", "paracetamol", "mosquito", "machhar"
            ),
            englishTitle = "Community Fever Triage, Malaria & Dengue Protocols",
            englishContent = "Primary health guidelines for febrile patients:\n\n" +
                "1. Baseline Care:\n" +
                "   • Measure temperature with digital/axillary thermometer.\n" +
                "   • If temp > 38.5°C (101°F): Perform tepid sponging with normal room-temperature tap water (NEVER ice water or cold water). Apply damp cloth to forehead, armpits, and groin.\n" +
                "   • Antipyretic: Paracetamol tablet (500–650 mg adult, 15 mg/kg child). Do NOT give Ibuprofen or Aspirin in suspected Dengue!\n\n" +
                "2. Malaria Screening:\n" +
                "   • High fever with chills and shivering: Perform Rapid Diagnostic Test (RDT) or prepare blood smear slide (thick & thin) for laboratory examination.\n\n" +
                "3. Dengue Awareness:\n" +
                "   • Sudden high fever with severe headache, retro-orbital eye pain, body ache, joint pain.\n" +
                "   • Critical Danger Signs: Red pinhead spots on skin (petechiae), bleeding from nose/gums, continuous severe abdominal pain, persistent vomiting, black tarry stools. Rush to hospital immediately!",
            hindiTitle = "बुखार, मलेरिया एवं डेंगू प्रबंधन दिशानिर्देश",
            hindiContent = "बुखार के मरीजों के लिए आशा स्वास्थ्य दिशानिर्देश:\n\n" +
                "1. प्राथमिक देखभाल:\n" +
                "   • थर्मामीटर से बुखार नापें। 101°F (38.5°C) से अधिक होने पर सामान्य पानी की पट्टियां माथे और बगलों पर रखें (बर्फ का पानी न लगाएं)।\n" +
                "   • बुखार की सुरक्षित दवा: पैरासिटामोल (Paracetamol 500mg)। डेंगू के संदेह में ब्रूफेन या एस्पिरिन कभी न दें!\n\n" +
                "2. मलेरिया जांच:\n" +
                "   • कंपकंपी और ठंड लगकर बुखार आने पर तुरंत आरडीटी (RDT) किट से जांच करें या खून की स्लाइड बनाएं।\n\n" +
                "3. डेंगू के खतरे के लक्षण:\n" +
                "   • आंखों के पीछे दर्द, जोड़ों में तेज दर्द।\n" +
                "   • खतरे के लक्षण: मसूड़ों या नाक से खून आना, त्वचा पर लाल चकत्ते (पिटेकी), लगातार पेट दर्द या बार-बार उल्टी होना। तुरंत अस्पताल ले जाएं!",
            dangerSigns = listOf(
                "Bleeding from gums or nose (मसूड़ों या नाक से खून बहना)",
                "Severe unyielding abdominal pain with black stools (काला मल / तेज पेट दर्द)",
                "Altered sensorium or extreme drowsiness (बेहोशी या अत्यधिक सुस्ती)"
            ),
            ashaActionPoints = listOf(
                "Promote hydration: ORS, lemon water, thin dal, coconut water.",
                "Educate community to eliminate stagnant water containers every Friday ('Dry Day').",
                "Distribute Long Lasting Insecticidal Nets (LLIN) in malaria-endemic villages."
            )
        ),

        // ==========================================
        // 8. FAMILY PLANNING & CONTRACEPTION
        // ==========================================
        MedicalKnowledgeItem(
            id = "family_planning_methods",
            category = "Family Planning",
            subCategory = "Contraceptive Methods & Counseling",
            keywords = listOf(
                "family planning", "parivar niyojan", "contraceptive", "antara", "chhaya",
                "mala d", "condom", "nirodh", "copper t", "iucd", "spacing", "bachha rokna"
            ),
            englishTitle = "Family Planning & Spacing Methods (National Family Planning Programme)",
            englishContent = "Approved contraceptive options for spacing and limiting:\n\n" +
                "1. Non-Hormonal Pill (Chhaya - Centchroman):\n" +
                "   • Safe, non-hormonal, non-steroidal tablet. Zero impact on breastfeeding.\n" +
                "   • Schedule: 1 tablet twice a week for first 3 months (e.g. Sunday & Wednesday), then 1 tablet once a week on Sunday from 4th month onward.\n\n" +
                "2. Injectable Contraceptive (Antara - DMPA):\n" +
                "   • Subcutaneous/intramuscular injection given once every 3 months (12 weeks).\n" +
                "   • Very high effectiveness. Counsel user that irregular spotting or amenorrhea (absence of periods) is normal.\n\n" +
                "3. Postpartum IUCD (PPIUCD / Copper-T):\n" +
                "   • Inserted by trained provider within 48 hours of institutional delivery or interval IUCD (Cu-T 380A protects for 10 years, Cu-T 375 for 5 years).\n\n" +
                "4. Condoms (Nirodh):\n" +
                "   • Dual protection against unwanted pregnancy and sexually transmitted infections (STIs/HIV).\n\n" +
                "5. Combined Oral Pill (Mala-D / Mala-N):\n" +
                "   • 28-day pack (21 white hormonal pills + 7 brown iron pills). Take 1 pill every night without missing.",
            hindiTitle = "परिवार नियोजन के साधन (गर्भनिरोधक विकल्प)",
            hindiContent = "बच्चों में अंतर रखने और परिवार नियोजन के प्रमुख सरकारी साधन:\n\n" +
                "1. छाया (Chhaya - गैर-हार्मोनल गोली):\n" +
                "   • सुरक्षित, बिना किसी हार्मोन के। स्तनपान कराने वाली माताओं के लिए पूर्णतः सुरक्षित।\n" +
                "   • लेने का तरीका: पहले 3 महीने हफ्ते में 2 बार (जैसे रविवार और बुधवार), चौथे महीने से हफ्ते में सिर्फ 1 बार (रविवार)।\n\n" +
                "2. अंतरा (Antara - इंजेक्शन):\n" +
                "   • हर 3 महीने (12 सप्ताह) में एक बार लगने वाला सुई का टीका। अत्यधिक असरदार।\n" +
                "   • मासिक धर्म में हल्का बदलाव या रुकना सामान्य है, घबराने की जरूरत नहीं।\n\n" +
                "3. कॉपर-टी (PPIUCD / Cu-T):\n" +
                "   • प्रसव के तुरंत बाद (48 घंटे में) अस्पताल में लगवाई जा सकती है। Cu-T 380A दस साल तक गर्भ से सुरक्षा देती है।\n\n" +
                "4. निरोध (कंडोम):\n" +
                "   • अनचाहे गर्भ और एचआईवी/यौन रोगों दोनों से सुरक्षा देता है।\n\n" +
                "5. माला-डी (Mala-D):\n" +
                "   • 28 गोलियों का पत्ता (21 सफेद + 7 भूरी आयरन की गोलियां)। रोज़ रात को बिना भूले एक गोली लें।",
            dangerSigns = listOf(
                "Severe lower abdominal pain with fever after IUCD insertion (संक्रमण का लक्षण)",
                "Missed Antara injection beyond 14 weeks (अनचाहे गर्भ का जोखिम)"
            ),
            ashaActionPoints = listOf(
                "Maintain eligible couple register (ECR).",
                "Counsel newly married couples on Mission Parivar Vikas benefits.",
                "Deliver contraceptives at home under Home Delivery of Contraceptives (HDC) scheme."
            )
        ),

        // ==========================================
        // 9. NUTRITION & ANEMIA MUKT BHARAT
        // ==========================================
        MedicalKnowledgeItem(
            id = "nutrition_anemia_muktbharat",
            category = "Nutrition & Anemia",
            subCategory = "Anemia Mukt Bharat & POSHAN",
            keywords = listOf(
                "anemia", "khoon ki kami", "poshan", "nutrition", "sam", "mam",
                "muac", "kamjori", "chakkar", "albendazole", "deworming", "kide"
            ),
            englishTitle = "Anemia Mukt Bharat & Severe Malnutrition Guidelines",
            englishContent = "Key strategies to combat anemia and child malnutrition:\n\n" +
                "1. Anemia Mukt Bharat IFA Prophylaxis Protocol:\n" +
                "   • 6 to 59 Months Children: 1 mL IFA syrup (20 mg elemental iron + 100 mcg folic acid) bi-weekly.\n" +
                "   • 5 to 9 Years Children: 1 Pink IFA tablet (45 mg iron + 400 mcg folic acid) weekly.\n" +
                "   • 10 to 19 Years Adolescents: 1 Blue IFA tablet (60 mg iron + 500 mcg folic acid) weekly.\n" +
                "   • Pregnant Women: 1 Red IFA tablet (100 mg iron + 500 mcg folic acid) daily for 180 days.\n\n" +
                "2. Bi-Annual Deworming:\n" +
                "   • Albendazole tablet: 400 mg chewable tablet every 6 months for all children >2 years (200 mg for 1–2 years) on National Deworming Day (NDD).\n\n" +
                "3. Identifying SAM (Severe Acute Malnutrition):\n" +
                "   • Measure Mid-Upper Arm Circumference (MUAC) using tricolor tape in children 6–59 months:\n" +
                "     - Green (> 12.5 cm): Normal nutritional status.\n" +
                "     - Yellow (11.5 to 12.5 cm): MAM (Moderate Acute Malnutrition).\n" +
                "     - RED (< 11.5 cm) OR Bilateral Pitting Edema: SAM (Severe Acute Malnutrition) — immediate referral to Nutrition Rehabilitation Center (NRC).",
            hindiTitle = "एनीमिया मुक्त भारत एवं गंभीर कुपोषण की पहचान (POSHAN)",
            hindiContent = "खून की कमी (एनीमिया) और बाल कुपोषण दूर करने की सरकारी योजना:\n\n" +
                "1. एनीमिया मुक्त भारत (IFA खुराक):\n" +
                "   • 6 से 59 माह के बच्चे: 1 मि.ली. नीले रंग का आयरन सिरप हफ्ते में 2 बार।\n" +
                "   • 5 से 9 वर्ष के बच्चे: 1 गुलाबी गोली (IFA Pink) हफ्ते में 1 बार।\n" +
                "   • 10 से 19 वर्ष के किशोर: 1 नीली गोली (IFA Blue) हफ्ते में 1 बार (स्कूल/आंगनवाड़ी में)।\n" +
                "   • गर्भवती महिलाएं: 1 लाल गोली प्रतिदिन 180 दिन तक।\n\n" +
                "2. पेट के कीड़े मारने की दवा (कृमि मुक्ति):\n" +
                "   • एल्बेंडाजोल (Albendazole): 2 वर्ष से बड़े बच्चों को 400 मि.ग्रा. की 1 गोली चबाकर हर 6 माह में (राष्ट्रीय कृमि मुक्ति दिवस पर)।\n\n" +
                "3. अति गंभीर कुपोषण (SAM) की पहचान:\n" +
                "   • MUAC टेप से 6 से 59 माह के बच्चे की बाईं बांह नापें:\n" +
                "     - हरा (> 12.5 सेमी): सामान्य पोषण।\n" +
                "     - पीला (11.5 से 12.5 सेमी): मध्यम कुपोषण (MAM)।\n" +
                "     - लाल (< 11.5 सेमी) या दोनों पैरों में सूजन: अति गंभीर कुपोषण (SAM) — तुरंत पोषण पुनर्वास केंद्र (NRC) भेजें।",
            dangerSigns = listOf(
                "MUAC tape in Red Zone < 11.5 cm (लाल पट्टी में माप)",
                "Bilateral pedal pitting edema (दोनों पैरों में गड्ढा पड़ने वाली सूजन)",
                "Severe lethargy, severe paleness (गंभीर सुस्ती और पीलापन)"
            ),
            ashaActionPoints = listOf(
                "Screen every child under 5 with MUAC tape on VHSND.",
                "Refer SAM children with medical complications to nearest NRC.",
                "Promote green leafy vegetables, jaggery, and sprouted pulses."
            )
        ),

        // ==========================================
        // 10. ACUTE MEDICAL EMERGENCIES (CHEST PAIN, STROKE, SNAKE & DOG BITE)
        // ==========================================
        MedicalKnowledgeItem(
            id = "acute_medical_emergencies",
            category = "Emergency Warning Signs",
            subCategory = "First Aid & 108 Emergency Ambulance",
            keywords = listOf(
                "emergency", "chest pain", "heart attack", "stroke", "paralysis", "dog bite",
                "rabies", "snake bite", "chhati dard", "saanp", "kutta", "108", "fits", "unconscious"
            ),
            englishTitle = "Acute Community Medical Emergencies & Golden Hour First Aid",
            englishContent = "Essential life-saving first-response protocols:\n\n" +
                "1. Heart Attack / Severe Chest Pain:\n" +
                "   • Crushing heavy pain in central chest spreading to left arm, neck, or jaw, with cold sweats and breathlessness.\n" +
                "   • Action: Seat patient comfortably propped up. Call 108 immediately. If instructed by MO and no allergy: chew 300 mg soluble Aspirin tablet.\n\n" +
                "2. Stroke (लकवा / पक्षाघात - FAST):\n" +
                "   • F (Face drooping on one side), A (Arm weakness / cannot lift), S (Speech slurred / cannot speak), T (Time: Call 108 immediately. Golden window is 4.5 hours for clot reversal).\n\n" +
                "3. Animal / Dog Bite (Rabies Prevention):\n" +
                "   • MANDATORY: Wash the wound thoroughly under forceful running tap water with SOAP for FULL 15 MINUTES! This inactivates >90% of virus.\n" +
                "   • Do NOT apply chili, lime, or bandage. Rush to PHC/Hospital for Anti-Rabies Vaccine (ARV) Day 0 dose + Rabies Immunoglobulin (RIG).\n\n" +
                "4. Snake Bite First Aid:\n" +
                "   • Keep victim calm and IMMOBILIZE the bitten limb with a splint at heart level. Movement spreads venom.\n" +
                "   • NEVER cut, suck, or apply tight tourniquet. Rush to hospital stocking Anti-Snake Venom (ASV).",
            hindiTitle = "आपातकालीन प्राथमिक उपचार एवं 108 एम्बुलेंस प्रोटोकॉल",
            hindiContent = "जीवन रक्षक आपातकालीन प्राथमिक चिकित्सा:\n\n" +
                "1. दिल का दौरा / सीने में तेज दर्द:\n" +
                "   • सीने के बीच में भारीपन/दबाव जो बाएं हाथ, जबड़े या पीठ में जाए, साथ में ठंडा पसीना और घबराहट।\n" +
                "   • तुरंत मरीज को बिठाएं, 108 एम्बुलेंस बुलाएं। डॉक्टर की सलाह पर एस्पिरिन (Aspirin 300mg) चबाने को दें।\n\n" +
                "2. स्ट्रोक (लकवा - FAST नियम):\n" +
                "   • चेहरा एक तरफ झुकना, एक हाथ में कमजोरी, बोली लड़खड़ाना। तुरंत 108 बुलाएं (गोल्डन आवर: 4.5 घंटे के भीतर अस्पताल पहुंचना जरूरी)।\n\n" +
                "3. कुत्ते / जानवर का काटना (रेबीज से बचाव):\n" +
                "   • अनिवार्य नियम: घाव को तुरंत बहते नल के पानी और साबुन से पूरे 15 मिनट तक लगातार धोएं! इससे 90% वायरस नष्ट हो जाता है।\n" +
                "   • मिर्च, चूना या पट्टी न बांधें। तुरंत सरकारी अस्पताल से एंटी-रेबीज टीका (ARV) लगवाएं।\n\n" +
                "4. सांप का काटना (सर्पदंश):\n" +
                "   • मरीज को शांत रखें और काटे गए अंग को लकड़ी की खपच्ची से स्थिर रखें (हिलाएं नहीं)।\n" +
                "   • चीरा न लगाएं, मुंह से जहर न चूसें और न ही कसकर रस्सी बांधें। तुरंत एंटी-स्नेक वेनम (ASV) वाले अस्पताल ले जाएं।",
            dangerSigns = listOf(
                "Unresponsiveness / Loss of pulse (बेहोशी / नब्ज न मिलना)",
                "Severe respiratory distress / SpO2 < 90% (अत्यधिक सांस फूलना)",
                "Active profuse arterial bleeding (तेज खून बहना)"
            ),
            ashaActionPoints = listOf(
                "Call 108 Emergency Ambulance immediately.",
                "Provide reassurance and position patient safely.",
                "Accompany patient and alert the emergency medical officer."
            ),
            isEmergency = true
        )
    )

    /**
     * Efficient offline keyword and n-gram retrieval engine.
     * Evaluates question text in English, Hindi (Devanagari), or Roman Hindi/Hinglish
     * and returns the best matching verified medical guidelines with relevance score.
     */
    fun searchKnowledge(query: String): List<Pair<MedicalKnowledgeItem, Int>> {
        val q = query.lowercase().trim()
        val tokens = q.split(Regex("[\\s,?.!;:]+")).filter { it.length > 1 }

        val scored = knowledgeItems.map { item ->
            var score = 0
            for (token in tokens) {
                // Exact keyword match
                if (item.keywords.any { it.equals(token, ignoreCase = true) }) {
                    score += 15
                }
                // Partial keyword match
                else if (item.keywords.any { it.contains(token) || token.contains(it) }) {
                    score += 8
                }
                // Match in titles
                if (item.englishTitle.lowercase().contains(token) || item.hindiTitle.contains(token)) {
                    score += 10
                }
                // Match in content
                if (item.englishContent.lowercase().contains(token) || item.hindiContent.contains(token)) {
                    score += 3
                }
            }
            item to score
        }

        return scored.filter { it.second > 0 }.sortedByDescending { it.second }
    }
}
