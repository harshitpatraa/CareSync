package com.caresync.app.ai

import kotlin.math.min

/**
 * Local offline medicine database based on the National List of Essential Medicines (NLEM India)
 * and primary healthcare / Jan Aushadhi formulary.
 *
 * Provides fuzzy matching (Levenshtein distance) to correct OCR misreads
 * from printed prescriptions and extract structured drug items.
 */
data class EssentialMedicine(
    val genericName: String,
    val brandAliases: List<String>,
    val defaultStrength: String,
    val form: String, // Tablet, Capsule, Syrup, Sachet, Injection, Drops
    val standardFrequency: String,
    val standardDuration: String,
    val category: String,
    val precautions: String
)

data class ExtractedMedicineMatch(
    val recognizedToken: String,
    val matchedMedicine: EssentialMedicine,
    val confidencePercent: Int,
    val extractedDosage: String,
    val extractedFrequency: String,
    val extractedDuration: String,
    val isUncertain: Boolean
)

object LocalMedicineDatabase {

    val essentialMedicines: List<EssentialMedicine> = listOf(
        EssentialMedicine(
            genericName = "Paracetamol",
            brandAliases = listOf("Crocin", "Dolo", "Calpol", "PCM", "Pacimol", "Pyrigesic", "Metacin"),
            defaultStrength = "500 mg",
            form = "Tablet",
            standardFrequency = "1 tablet every 6-8 hours PRN fever/pain (Max 2g/day)",
            standardDuration = "3 days",
            category = "Antipyretic / Analgesic",
            precautions = "Avoid combining multiple paracetamol brands. Max 2000mg/day to prevent liver toxicity. Safe in pregnancy."
        ),
        EssentialMedicine(
            genericName = "Amoxicillin + Clavulanic Acid",
            brandAliases = listOf("Augmentin", "Moxikind-CV", "Clavam", "Amoxyclav", "Mega-CV", "Advent"),
            defaultStrength = "625 mg",
            form = "Tablet",
            standardFrequency = "1 tablet twice daily after food",
            standardDuration = "5 days",
            category = "Broad-Spectrum Antibiotic",
            precautions = "Complete full course. Take strictly after meals to prevent gastric upset. Contraindicated in penicillin allergy."
        ),
        EssentialMedicine(
            genericName = "Amoxicillin",
            brandAliases = listOf("Mox", "Novamox", "Amoxil", "Almox"),
            defaultStrength = "500 mg",
            form = "Capsule",
            standardFrequency = "1 capsule three times daily after food",
            standardDuration = "5 days",
            category = "Antibiotic",
            precautions = "Complete full 5-day course even if feeling better."
        ),
        EssentialMedicine(
            genericName = "Azithromycin",
            brandAliases = listOf("Azee", "Azithral", "Zithrox", "Azicip", "Azimax"),
            defaultStrength = "500 mg",
            form = "Tablet",
            standardFrequency = "1 tablet once daily 1 hour before or 2 hours after meals",
            standardDuration = "3 to 5 days",
            category = "Macrolide Antibiotic",
            precautions = "Take at fixed time daily. May cause mild nausea or abdominal cramps."
        ),
        EssentialMedicine(
            genericName = "WHO Oral Rehydration Salts (ORS)",
            brandAliases = listOf("ORS", "Electral", "Walyte", "Prolyte", "Penta-ORS"),
            defaultStrength = "20.5 g sachet in 1L water",
            form = "Oral Solution",
            standardFrequency = "Sip continuously; 100-200 mL after every loose stool",
            standardDuration = "3 days",
            category = "Electrolyte Rehydration",
            precautions = "Mix entire packet in exactly 1 liter clean boiled and cooled water. Discard unused solution after 24 hours."
        ),
        EssentialMedicine(
            genericName = "Zinc Sulphate",
            brandAliases = listOf("Zincovit", "Zinconia", "Zincon", "Zinc-D"),
            defaultStrength = "20 mg",
            form = "Dispersible Tablet",
            standardFrequency = "1 tablet daily dissolved in mother's milk/water for 14 days",
            standardDuration = "14 days",
            category = "Micronutrient for Diarrhea",
            precautions = "Give for a full 14 days continuously to repair intestinal lining and prevent diarrhea recurrence."
        ),
        EssentialMedicine(
            genericName = "Iron and Folic Acid (IFA)",
            brandAliases = listOf("IFA", "Autrin", "Fefol", "Dexorange", "Orofer", "Livogen"),
            defaultStrength = "100 mg Iron + 500 mcg Folic Acid",
            form = "Tablet",
            standardFrequency = "1 tablet daily after lunch or dinner",
            standardDuration = "180 days",
            category = "Hematinic / Anemia",
            precautions = "Take with water or lemon water. NEVER take with milk, tea, or calcium. Stools may appear black."
        ),
        EssentialMedicine(
            genericName = "Calcium Carbonate + Vitamin D3",
            brandAliases = listOf("Shelcal", "Cipcal", "Calcimax", "Gemcal", "Ostocalcium"),
            defaultStrength = "500 mg + 250 IU",
            form = "Tablet",
            standardFrequency = "1 tablet twice daily after meals",
            standardDuration = "180 days",
            category = "Bone Mineral Supplement",
            precautions = "Maintain at least 2 hours gap from Iron (IFA) tablet to allow proper absorption."
        ),
        EssentialMedicine(
            genericName = "Pantoprazole",
            brandAliases = listOf("Pan-40", "Pantocid", "Pantosec", "Pantodac", "Topraz"),
            defaultStrength = "40 mg",
            form = "Tablet",
            standardFrequency = "1 tablet once daily in the morning 30 minutes before breakfast",
            standardDuration = "7 days",
            category = "Proton Pump Inhibitor (Acid Reducer)",
            precautions = "Swallow whole with water; do not crush or chew."
        ),
        EssentialMedicine(
            genericName = "Omeprazole",
            brandAliases = listOf("Omez", "Omee", "Omecip", "Ocid"),
            defaultStrength = "20 mg",
            form = "Capsule",
            standardFrequency = "1 capsule once daily before breakfast",
            standardDuration = "7 days",
            category = "Antacid / PPI",
            precautions = "Take on empty stomach 30 minutes before morning meal."
        ),
        EssentialMedicine(
            genericName = "Metformin HCl",
            brandAliases = listOf("Glycomet", "Glucophage", "Cetapin", "Formin", "Obimet"),
            defaultStrength = "500 mg",
            form = "Tablet",
            standardFrequency = "1 tablet twice daily immediately after meals",
            standardDuration = "30 days",
            category = "Oral Antidiabetic (Biguanide)",
            precautions = "Always take with or after meals to minimize gastrointestinal discomfort. Regular blood sugar tracking required."
        ),
        EssentialMedicine(
            genericName = "Telmisartan",
            brandAliases = listOf("Telma", "Telpres", "Telsar", "Creser", "Telmikind"),
            defaultStrength = "40 mg",
            form = "Tablet",
            standardFrequency = "1 tablet once daily in morning",
            standardDuration = "30 days",
            category = "Antihypertensive (ARB)",
            precautions = "Contraindicated in pregnancy. Monitor blood pressure and serum potassium periodically."
        ),
        EssentialMedicine(
            genericName = "Amlodipine",
            brandAliases = listOf("Amlopres", "Amlong", "Stamlo", "Amlovas"),
            defaultStrength = "5 mg",
            form = "Tablet",
            standardFrequency = "1 tablet once daily in the morning",
            standardDuration = "30 days",
            category = "Antihypertensive (CCB)",
            precautions = "May cause mild ankle swelling; report to physician if swelling becomes significant."
        ),
        EssentialMedicine(
            genericName = "Cetirizine HCl",
            brandAliases = listOf("Cetzine", "Alerid", "Zyrtec", "Okacet", "Incid-L"),
            defaultStrength = "10 mg",
            form = "Tablet",
            standardFrequency = "1 tablet once daily at bedtime",
            standardDuration = "5 days",
            category = "Antihistamine / Antiallergic",
            precautions = "May cause mild drowsiness. Avoid driving or operating machinery after taking."
        ),
        EssentialMedicine(
            genericName = "Levocetirizine + Montelukast",
            brandAliases = listOf("Montek-LC", "Montair-LC", "Telekast-L", "Levocet-M"),
            defaultStrength = "5 mg + 10 mg",
            form = "Tablet",
            standardFrequency = "1 tablet once daily at night",
            standardDuration = "10 days",
            category = "Antiallergic & Bronchial Support",
            precautions = "Helps allergic rhinitis, coughing, and nocturnal bronchospasm. Take at night."
        ),
        EssentialMedicine(
            genericName = "Salbutamol (Albuterol)",
            brandAliases = listOf("Asthalin", "Ventorlin", "Deriphyllin"),
            defaultStrength = "100 mcg per puff / 2 mg tablet",
            form = "Inhaler / Tablet",
            standardFrequency = "2 puffs with spacer PRN breathlessness/wheezing (up to 3 times)",
            standardDuration = "As directed",
            category = "Fast-Acting Bronchodilator",
            precautions = "Rinse mouth with water after inhalation. If breathlessness is unrelieved after 3 doses, seek emergency care."
        ),
        EssentialMedicine(
            genericName = "Albendazole",
            brandAliases = listOf("Zentel", "Bandy", "Noworm", "Albez"),
            defaultStrength = "400 mg",
            form = "Chewable Tablet",
            standardFrequency = "1 chewable tablet single dose at bedtime (Repeat after 6 months)",
            standardDuration = "1 day",
            category = "Anthelmintic (Deworming)",
            precautions = "Chew tablet thoroughly before swallowing. Safe in children >2 years. Contraindicated in 1st trimester of pregnancy."
        ),
        EssentialMedicine(
            genericName = "Ondansetron",
            brandAliases = listOf("Emeset", "Vomitro", "Ondem", "Zofran"),
            defaultStrength = "4 mg",
            form = "Tablet",
            standardFrequency = "1 tablet 30 minutes before food PRN severe vomiting",
            standardDuration = "3 days",
            category = "Antiemetic",
            precautions = "Use under clinician guidance for acute vomiting."
        ),
        EssentialMedicine(
            genericName = "Ciprofloxacin",
            brandAliases = listOf("Ciplox", "Ciprobid", "Cifran"),
            defaultStrength = "500 mg",
            form = "Tablet",
            standardFrequency = "1 tablet twice daily after meals",
            standardDuration = "5 days",
            category = "Fluoroquinolone Antibiotic",
            precautions = "Take with plenty of water. Contraindicated in pregnancy and growing children unless specifically ordered."
        ),
        EssentialMedicine(
            genericName = "Silver Sulfadiazine 1%",
            brandAliases = listOf("Silverex", "Burnol", "Silvazine"),
            defaultStrength = "1% w/w cream",
            form = "Topical Ointment",
            standardFrequency = "Apply 1-2 mm layer over clean burn wound twice daily",
            standardDuration = "7 days",
            category = "Topical Burn Antimicrobial",
            precautions = "Cool burn with running water first. Do NOT break intact blisters."
        ),
        EssentialMedicine(
            genericName = "Povidone Iodine 5%",
            brandAliases = listOf("Betadine", "Cipladine", "Wokadine"),
            defaultStrength = "5% w/w ointment / solution",
            form = "Topical Antiseptic",
            standardFrequency = "Apply gently on clean wound bed and dress with sterile gauze",
            standardDuration = "5 days",
            category = "Topical Antiseptic",
            precautions = "Clean wound with sterile normal saline before applying. Do not use if known iodine allergy."
        )
    )

    /**
     * Finds matches for words or tokens extracted from an OCR prescription.
     * Uses Levenshtein distance and alias matching against the Essential Medicines list.
     */
    fun findMedicineMatches(recognizedText: String): List<ExtractedMedicineMatch> {
        val lines = recognizedText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val matches = mutableListOf<ExtractedMedicineMatch>()

        for (line in lines) {
            val words = line.split(Regex("[\\s,;:-]+")).filter { it.length >= 3 }
            for (word in words) {
                val cleanWord = word.lowercase()
                var bestMed: EssentialMedicine? = null
                var bestDistance = 999
                var isAliasMatch = false

                for (med in essentialMedicines) {
                    // Check generic name words
                    for (medWord in med.genericName.lowercase().split(" ")) {
                        val dist = computeLevenshteinDistance(cleanWord, medWord)
                        if (dist < bestDistance) {
                            bestDistance = dist
                            bestMed = med
                        }
                    }
                    // Check brand aliases
                    for (alias in med.brandAliases) {
                        val dist = computeLevenshteinDistance(cleanWord, alias.lowercase())
                        if (dist < bestDistance) {
                            bestDistance = dist
                            bestMed = med
                            isAliasMatch = true
                        }
                    }
                }

                // If match is sufficiently close (distance <= 2 for word length >= 4, or distance <= 1 for word length 3)
                val maxAllowedDistance = if (cleanWord.length <= 4) 1 else 2
                if (bestMed != null && bestDistance <= maxAllowedDistance) {
                    val maxLen = bestMed.genericName.length.coerceAtLeast(cleanWord.length)
                    val confidence = ((1.0 - (bestDistance.toDouble() / maxLen)) * 100).toInt().coerceIn(40, 99)

                    // Extract adjacent dosage, frequency, and duration from the same line
                    val dosage = extractDosage(line).ifBlank { bestMed.defaultStrength }
                    val frequency = extractFrequency(line).ifBlank { bestMed.standardFrequency }
                    val duration = extractDuration(line).ifBlank { bestMed.standardDuration }

                    val isUncertain = bestDistance > 0 || confidence < 85

                    // Avoid duplicate matches for same generic medicine in list
                    if (matches.none { it.matchedMedicine.genericName == bestMed.genericName }) {
                        matches.add(
                            ExtractedMedicineMatch(
                                recognizedToken = word,
                                matchedMedicine = bestMed,
                                confidencePercent = confidence,
                                extractedDosage = dosage,
                                extractedFrequency = frequency,
                                extractedDuration = duration,
                                isUncertain = isUncertain
                            )
                        )
                    }
                }
            }
        }

        return matches
    }

    private fun extractDosage(text: String): String {
        val pattern = Regex("(?i)(\\d+\\s*(?:mg|g|mcg|ml|iu|sachet|puff|tablet|tab|cap))")
        return pattern.find(text)?.value?.trim().orEmpty()
    }

    private fun extractFrequency(text: String): String {
        val upper = text.uppercase()
        return when {
            "1-0-1" in upper || "1-0-1" in text -> "Twice daily after food (1-0-1)"
            "1-1-1" in upper || "1-1-1" in text -> "Three times daily after food (1-1-1)"
            "1-0-0" in upper -> "Once daily in the morning (1-0-0)"
            "0-0-1" in upper -> "Once daily at night (0-0-1)"
            "TDS" in upper || "TID" in upper -> "Three times daily (TDS)"
            "BD" in upper || "BID" in upper -> "Twice daily (BD)"
            "OD" in upper -> "Once daily (OD)"
            "QID" in upper -> "Four times daily (QID)"
            "SOS" in upper || "PRN" in upper -> "As needed (SOS)"
            else -> ""
        }
    }

    private fun extractDuration(text: String): String {
        val pattern = Regex("(?i)(\\d+\\s*(?:days?|weeks?|months?|din|hafte|mahina))")
        return pattern.find(text)?.value?.trim().orEmpty()
    }

    /**
     * Classic dynamic programming Levenshtein distance implementation.
     */
    fun computeLevenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    dp[i - 1][j] + 1, // deletion
                    min(
                        dp[i][j - 1] + 1, // insertion
                        dp[i - 1][j - 1] + cost // substitution
                    )
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
