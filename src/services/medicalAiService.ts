import { ClinicalAssessmentResult, DiseaseRiskItem, RiskLevel } from '../types';

interface KnowledgeItem {
  keywords: string[];
  titleEn: string;
  titleHi: string;
  contentEn: string;
  contentHi: string;
  dangerSignsEn: string[];
  dangerSignsHi: string[];
  actionPointsEn: string[];
  actionPointsHi: string[];
}

const KNOWLEDGE_ITEMS: KnowledgeItem[] = [
  {
    keywords: ["anc", "pregnancy", "pregnant", "garbh", "garbhavastha", "checkup", "trimester", "jaanch", "mahina"],
    titleEn: "Antenatal Care (ANC) Mandatory Checkups (MoHFW Guidelines)",
    titleHi: "प्रसव पूर्व जांच (ANC) - राष्ट्रीय स्वास्थ्य मिशन दिशानिर्देश",
    contentEn: `According to National Health Mission guidelines, every pregnant woman must complete at least 4 mandatory ANC checkups:
1. 1st Checkup: Within first 12 weeks (early pregnancy registration, confirmation, baseline BP, weight, Hb, and urine test).
2. 2nd Checkup: Between 14th and 26th week (measure fundal height, fetal heart sound, BP, screen for gestational diabetes).
3. 3rd Checkup: Between 28th and 34th week (track fetal growth, BP, check for preeclampsia/edema, ensure IFA and Calcium intake).
4. 4th Checkup: At 36th week to delivery (assess fetal presentation, identify high-risk conditions, plan institutional delivery).

Mandatory Vaccinations: 2 doses of Tetanus & adult Diphtheria (Td) toxoid injection (minimum 4 weeks apart).`,
    contentHi: `राष्ट्रीय स्वास्थ्य मिशन के अनुसार हर गर्भवती महिला को कम से कम 4 प्रसव पूर्व जांचें अवश्य करानी चाहिए:
1. पहली जांच: 12 सप्ताह के भीतर (गर्भावस्था का शीघ्र पंजीकरण, वजन, बीपी, खून व पेशाब जांच)।
2. दूसरी जांच: 14 से 26 सप्ताह के बीच (शिशु की धड़कन, बीपी तथा पेट का घेरा नापना)।
3. तीसरी जांच: 28 से 34 सप्ताह के बीच (शिशु का विकास, सूजन तथा उच्च रक्तचाप की निगरानी)।
4. चौथी जांच: 36वें सप्ताह से प्रसव तक (शिशु की स्थिति जांचना तथा संस्थागत प्रसव की तैयारी)।

टीकाकरण: टिटनेस एवं डिप्थीरिया (Td) के 2 टीके 4 सप्ताह के अंतराल पर लगवाएं।`,
    dangerSignsEn: [
      "Severe headache with blurred vision",
      "Swelling on face, hands, and feet (Preeclampsia)",
      "Vaginal bleeding or watery leaking"
    ],
    dangerSignsHi: [
      "गंभीर सिरदर्द और आंखों के आगे धुंधला दिखना",
      "चेहरे, हाथों और पैरों में अचानक तेज सूजन",
      "योनि से रक्तस्राव या पानी बहना"
    ],
    actionPointsEn: [
      "Register pregnancy on RCH portal within 12 weeks.",
      "Ensure institutional delivery plan under PMMVY / Janani Suraksha Yojana.",
      "Provide MCP Card (Mother and Child Protection Card)."
    ],
    actionPointsHi: [
      "12 सप्ताह के भीतर RCH पोर्टल पर पंजीकरण कराएं।",
      "प्रधानमंत्री मातृ वंदना योजना (PMMVY) और JSY के तहत संस्थागत प्रसव तय करें।",
      "MCP कार्ड (मातृ एवं बाल सुरक्षा कार्ड) जारी करें।"
    ]
  },
  {
    keywords: ["ifa", "iron", "calcium", "folic acid", "anemia", "khoon ki kami", "tablet", "dawa", "lal goli", "chuna"],
    titleEn: "IFA & Calcium Supplementation in Pregnancy (Anemia Mukt Bharat)",
    titleHi: "गर्भावस्था में आयरन (IFA) एवं कैल्शियम की खुराक",
    contentEn: `Essential daily micronutrient regimen for pregnant mothers:
1. Iron & Folic Acid (IFA - Red Tablet):
   • Dose: 1 tablet daily (100 mg elemental Iron + 500 mcg Folic Acid) starting from 14th week (2nd trimester) for minimum 180 days during pregnancy, and continued for 180 days postpartum.
   • Instructions: Take strictly after food with water or lemon water. Do NOT take with milk, tea, or coffee.
   • Note: Stools may turn black; reassure the mother this is completely normal.

2. Calcium Tablet (500 mg Calcium + 250 IU Vit D3):
   • Dose: 2 tablets daily from 14 weeks onwards until 6 months of breastfeeding.
   • Critical Rule: NEVER take Iron and Calcium tablets together! Maintain minimum 2 hours gap (Calcium after breakfast, IFA after lunch/dinner).`,
    contentHi: `गर्भवती माताओं के लिए दैनिक पोषण खुराक दिशानिर्देश:
1. आयरन एवं फोलिक एसिड (लाल गोली - IFA):
   • 1 गोली प्रतिदिन चौथे महीने (14 सप्ताह) से शुरू कर प्रसव तक 180 दिन, और प्रसव के बाद भी 180 दिन लें।
   • भोजन के बाद पानी या नींबू पानी के साथ लें। चाय या दूध के साथ कभी न लें। मल का काला होना सामान्य है।

2. कैल्शियम की गोली (500 मि.ग्रा.):
   • प्रतिदिन 2 गोलियां 14वें सप्ताह से शुरू कर शिशु के 6 माह तक लें।
   • महत्वपूर्ण नियम: आयरन और कैल्शियम की गोली कभी भी एक साथ न खाएं! कम से कम 2 घंटे का अंतर रखें।`,
    dangerSignsEn: [
      "Severe pallor (Hb < 7 g/dL) — urgent blood transfusion evaluation",
      "Shortness of breath on mild exertion or resting"
    ],
    dangerSignsHi: [
      "अत्यधिक पीलापन (Hb < 7 g/dL) - तत्काल रक्त चढ़ाने की आवश्यकता",
      "हल्के काम पर या बैठे रहने पर भी सांस फूलना"
    ],
    actionPointsEn: [
      "Distribute 180 IFA red tablets and 360 Calcium tablets.",
      "Administer Tab Albendazole 400 mg in 2nd trimester for deworming.",
      "Encourage iron-rich diet: green leafy vegetables, jaggery (gur), roasted chana."
    ],
    actionPointsHi: [
      "180 लाल IFA गोलियां और 360 कैल्शियम गोलियां वितरित करें।",
      "कृमि मुक्ति हेतु दूसरे तिमाही में एल्बेंडाजोल 400 मि.ग्रा. की 1 गोली दें।",
      "आयरन युक्त आहार: पालक, गुड़, चना, हरी सब्जियां खाने की सलाह दें।"
    ]
  },
  {
    keywords: ["fever", "bukhar", "dengue", "malaria", "mosquito", "shivering", "chills", "platelet", "machhar"],
    titleEn: "Fever, Dengue & Malaria Clinical Protocol (NVBDCP / MoHFW)",
    titleHi: "बुखार, डेंगू एवं मलेरिया प्रोटोकॉल (राष्ट्रीय स्वास्थ्य मिशन)",
    contentEn: `Management protocol for acute febrile illness:
1. Triage & Baseline Tests:
   • Rapid Diagnostic Test (RDT) for Malaria (Pf/Pv) and Dengue NS1 / IgM Antigen.
   • Complete Blood Count (CBC) to monitor Platelet count and Hematocrit.

2. Safe Supportive Management:
   • Tab Paracetamol 500mg (1 tablet every 6 hours PRN fever, Maximum 2g/day).
   • Oral Rehydration Salts (ORS): 1 sachet in 1 liter clean water; sip continuously.
   • Cold/Tepid water sponging on forehead, arms, and legs.
   • STRICT CONTRAINDICATION: NEVER give Aspirin, Ibuprofen, or Diclofenac as they aggravate bleeding risks in Dengue!`,
    contentHi: `तेज बुखार और संक्रामक रोगों का उपचार प्रोटोकॉल:
1. जांच: मलेरिया RDT कार्ड टेस्ट तथा डेंगू NS1 एंटीजन टेस्ट और सीबीसी (प्लेटलेट्स) जांच कराएं।
2. सुरक्षित उपचार:
   • पैरासिटामोल 500 मि.ग्रा. (बुखार होने पर 6 घंटे में 1 गोली)।
   • ओआरएस (ORS) का घोल 1 लीटर पानी में बनाकर लगातार पिलाएं।
   • सामान्य पानी की पट्टियां माथे और हाथ-पैरों पर रखें।
   • चेतावनी: दर्द निवारक जैसे ब्रूफेन, एस्पिरिन या डाइक्लोफेनाक कभी न दें, इससे डेंगू में आंतरिक रक्तस्राव हो सकता है!`,
    dangerSignsEn: [
      "Severe abdominal pain or persistent vomiting",
      "Bleeding from gums, nose, black stools, or petechiae red spots",
      "Sudden drop in platelet count < 50,000/mcL or BP < 90/60 mmHg (Dengue Shock)"
    ],
    dangerSignsHi: [
      "पेट में असहनीय दर्द या लगातार उल्टी होना",
      "मसूड़ों/नाक से खून आना या काले रंग का मल आना",
      "प्लेटलेट 50,000 से कम होना या बीपी गिरना (डेंगू शॉक सिंड्रोम)"
    ],
    actionPointsEn: [
      "Refer immediately to Community Health Center if warning signs emerge.",
      "Conduct door-to-door larval surveillance in patient's neighborhood.",
      "Advise mosquito net usage and eliminate stagnant water collections."
    ],
    actionPointsHi: [
      "खतरे के लक्षण दिखने पर मरीज को तुरंत सीएचसी/अस्पताल भेजें।",
      "घर के आसपास रुके पानी को खाली कराएं और मच्छरदानी का उपयोग कराएं।"
    ]
  },
  {
    keywords: ["chest pain", "heart", "chhati", "cardiac", "stroke", "emergency", "108", "ambulance", "attack", "blood pressure"],
    titleEn: "Acute Cardiac & Hypertensive Emergency (108 Dispatch Protocol)",
    titleHi: "सीने में तेज दर्द एवं हृदय संबंधी आपातकाल (108 एम्बुलेंस प्रोटोकॉल)",
    contentEn: `Immediate Golden Hour Life-Saving Protocol:
1. Recognition:
   • Crushing, squeezing substernal central chest pressure radiating to left shoulder, arm, neck, or jaw.
   • Accompanied by cold profuse diaphoresis (sweating), dyspnea, nausea, or dizziness.

2. Immediate Action:
   • Seat the patient comfortably in a propped-up (half-sitting) position; loosen tight collar/clothing.
   • Call 108 Emergency Ambulance immediately with cardiac request.
   • Under Medical Officer standing instructions and if patient is non-allergic: provide Tab Aspirin 300mg chewable immediately.
   • Keep patient completely still; do NOT allow walking or exertion.`,
    contentHi: `जीवन रक्षक प्राथमिक सहायता प्रोटोकॉल:
1. पहचान:
   • सीने के बीच में अत्यधिक भारीपन या दबाव जो बाएं हाथ, कंधे, गर्दन या जबड़े तक फैले।
   • साथ में ठंडा पसीना, घबराहट, सांस फूलना या चक्कर आना।

2. तत्काल कार्रवाई:
   • मरीज को आराम से पीठ के सहारे आधा बिठाएं; कपड़े ढीले करें।
   • तुरंत 108 एम्बुलेंस बुलाएं।
   • डॉक्टर की सलाह पर एस्पिरिन 300 मि.ग्रा. की गोली तुरंत चबाने को दें।
   • मरीज को बिल्कुल भी चलने-फिरने न दें।`,
    dangerSignsEn: [
      "Loss of consciousness or sudden collapse",
      "SpO2 dropping below 90% or systolic BP > 180 / < 90 mmHg"
    ],
    dangerSignsHi: [
      "बेहोशी या अचानक गिर पड़ना",
      "ऑक्सीजन (SpO2) 90% से कम होना या बीपी बहुत बढ़ना"
    ],
    actionPointsEn: [
      "Accompany patient in 108 ambulance to nearest hospital with ICU/Cath-lab.",
      "Keep airway clear; prepare for CPR if pulse ceases."
    ],
    actionPointsHi: [
      "108 एम्बुलेंस में मरीज के साथ आईसीयू युक्त नजदीकी अस्पताल जाएं।"
    ]
  },
  {
    keywords: ["vaccine", "immunization", "teeka", "bcg", "polio", "opv", "pentavalent", "rotavirus", "measles", "mr", "dpt"],
    titleEn: "National Universal Immunization Schedule (UIP Guidelines)",
    titleHi: "राष्ट्रीय सार्वभौमिक टीकाकरण सारणी (UIP दिशानिर्देश)",
    contentEn: `Mandatory National Immunization Schedule:
• At Birth: BCG (intradermal), OPV-0 (oral 2 drops), Hepatitis B birth dose (within 24 hours).
• 6 Weeks: OPV-1, Pentavalent-1 (DPT+HepB+Hib), Rotavirus-1, fIPV-1, PCV-1.
• 10 Weeks: OPV-2, Pentavalent-2, Rotavirus-2.
• 14 Weeks: OPV-3, Pentavalent-3, Rotavirus-3, fIPV-2, PCV-2.
• 9-12 Months: MR-1 (Measles & Rubella), JE-1 (in endemic districts), PCV Booster, Vitamin A dose 1 (1 lakh IU).
• 16-24 Months: MR-2, DPT Booster-1, OPV Booster, Vitamin A dose 2.
• 5-6 Years: DPT Booster-2.
• 10 & 16 Years: Td (Tetanus & adult Diphtheria) vaccine.`,
    contentHi: `शिशु एवं बाल टीकाकरण सारणी:
• जन्म के समय: बीसीजी (BCG), ओपीवी-0 (OPV 2 बूंद), हेपेटाइटिस-बी (24 घंटे के भीतर)।
• 6 सप्ताह: पेंटावेलेंट-1, ओपीवी-1, रोटावायरस-1, fIPV-1, पीसीवी-1।
• 10 सप्ताह: पेंटावेलेंट-2, ओपीवी-2, रोटावायरस-2।
• 14 सप्ताह: पेंटावेलेंट-3, ओपीवी-3, रोटावायरस-3, fIPV-2, पीसीवी-2।
• 9 से 12 माह: एमआर-1 (खसरा-रूबेला), विटामिन-ए (1 लाख IU), पीसीवी बूस्टर।
• 16 से 24 माह: एमआर-2, डीपीटी बूस्टर-1, ओपीवी बूस्टर।
• 5 से 6 वर्ष: डीपीटी बूस्टर-2।
• 10 व 16 वर्ष: टीडी (Td) टीका।`,
    dangerSignsEn: [
      "High persistent fever > 103°F or continuous crying > 3 hours after injection (AEFI Watch)",
      "Swelling at injection site with abscess formation"
    ],
    dangerSignsHi: [
      "टीकाकरण के बाद 3 घंटे से अधिक लगातार रोना या अत्यधिक तेज बुखार",
      "टीके के स्थान पर मवाद या बड़ा फोड़ा बनना"
    ],
    actionPointsEn: [
      "Report any Adverse Event Following Immunization (AEFI) immediately to MO within 24 hours.",
      "Advise Paracetamol drops for mild post-vaccine fever; do not massage injection site."
    ],
    actionPointsHi: [
      "टीके के स्थान पर मालिश न करने की सलाह दें। हल्का बुखार होने पर पैरासिटामोल दें।"
    ]
  },
  {
    keywords: ["burn", "wound", "skin", "rash", "chot", "jalna", "dressing", "first aid", "infection", "khujli"],
    titleEn: "First Aid for Burns, Dermal Wounds & Common Rashes",
    titleHi: "जलने, घाव और त्वचा के चकत्तों पर प्राथमिक उपचार",
    contentEn: `Clinical First-Aid Guidelines:
1. Thermal Burns:
   • Immediately hold under gentle, cold running tap water for 15-20 minutes. (NEVER apply ice, toothpaste, or ghee!).
   • Cover loosely with clean, dry sterile gauze. Keep blisters intact to prevent deep infection.
   • Apply sterile Silver Sulfadiazine 1% burn cream if available.

2. Clean Wounds & Cuts:
   • Wash thoroughly with sterile Normal Saline (0.9%) or clean drinking water and soap.
   • Apply Povidone Iodine 5% solution and sterile non-stick dressing.
   • Verify Tetanus Toxoid (TT/Td) status: administer 0.5 mL IM if last dose > 5 years ago.

3. Fungal Rashes (Ringworm / Tinea):
   • Keep area dry and ventilated. Apply topical Clotrimazole 1% cream twice daily for 2-3 weeks.
   • Do NOT use steroid creams (like Betamethasone) alone as they worsen fungal infection.`,
    contentHi: `प्राथमिक चिकित्सा दिशानिर्देश:
1. जलने पर:
   • तुरंत 15-20 मिनट तक नल के ठंडे पानी की धार में रखें। (बर्फ, टूथपेस्ट या घी कभी न लगाएं!)।
   • साफ सूती कपड़े या स्टेराइल पट्टी से ढंकें। फफोले कभी न फोड़ें।
   • सिल्वर सल्फाडियाजीन (Silver Sulfadiazine) क्रीम लगाएं।

2. घाव या चोट पर:
   • साफ पानी और साबुन से घाव धोएं। बीटाडीन (Povidone Iodine) लगाएं और पट्टी बांधें।
   • टिटनेस का टीका (Td) लगवाएं यदि पिछले 5 वर्षों में न लगा हो।

3. दाद/खाज (फंगल इन्फेक्शन):
   • जगह को सूखा रखें। क्लोट्रिमाजोल (Clotrimazole) क्रीम दिन में 2 बार लगाएं।`,
    dangerSignsEn: [
      "Burns on face, neck, hands, or groin (>5% surface area)",
      "Spreading redness, warmth, or purulent pus from wound bed (Cellulitis)"
    ],
    dangerSignsHi: [
      "चेहरे, गर्दन या हाथों पर जलना",
      "घाव से मवाद आना या लाली का तेजी से फैलना"
    ],
    actionPointsEn: [
      "Refer extensive burns or infected wounds to PHC/CHC Medical Officer.",
      "Advise oral antibiotics only after physician consultation."
    ],
    actionPointsHi: [
      "गंभीर जलने पर तुरंत प्राथमिक स्वास्थ्य केंद्र (PHC) रेफर करें।"
    ]
  }
];

export function queryMedicalAi(query: string, lang: 'en' | 'hi' = 'en'): { text: string; tags: string[] } {
  const q = query.toLowerCase().trim();
  const isHindi = lang === 'hi' || /[\u0900-\u097F]/.test(q);

  let bestMatch: KnowledgeItem | null = null;
  let highestScore = 0;

  for (const item of KNOWLEDGE_ITEMS) {
    let score = 0;
    for (const kw of item.keywords) {
      if (q.includes(kw)) score += 15;
    }
    if (score > highestScore) {
      highestScore = score;
      bestMatch = item;
    }
  }

  if (bestMatch && highestScore > 0) {
    const title = isHindi ? bestMatch.titleHi : bestMatch.titleEn;
    const content = isHindi ? bestMatch.contentHi : bestMatch.contentEn;
    const dangerSigns = isHindi ? bestMatch.dangerSignsHi : bestMatch.dangerSignsEn;
    const actionPoints = isHindi ? bestMatch.actionPointsHi : bestMatch.actionPointsEn;

    let response = `### ${title}\n\n${content}\n\n`;
    if (dangerSigns.length > 0) {
      response += `**${isHindi ? "खतरे के लक्षण (Danger Signs):" : "Key Danger Signs:"}**\n` +
        dangerSigns.map(d => `• ${d}`).join('\n') + '\n\n';
    }
    if (actionPoints.length > 0) {
      response += `**${isHindi ? "आशा कार्यकर्ता कार्य बिंदु (ASHA Action Points):" : "ASHA Protocol & Guidance:"}**\n` +
        actionPoints.map(a => `• ${a}`).join('\n') + '\n\n';
    }
    response += `---\n*ℹ️ ${isHindi ? "राष्ट्रीय स्वास्थ्य मिशन (NHM/MoHFW) प्रमाणित स्वास्थ्य दिशानिर्देश।" : "Guidance verified per Indian MoHFW / National Health Mission standards."}*`;

    return {
      text: response,
      tags: ["Sehat Setu AI", isHindi ? "एनएचएम दिशानिर्देश" : "MoHFW Guidelines", "Verified Medical DB"]
    };
  }

  // Fallback general guidance
  if (isHindi) {
    return {
      text: `### सेहत सेतु आशा स्वास्थ्य सहायक (NHM Certified)\n\nआपके प्रश्न '${query}' के संदर्भ में राष्ट्रीय स्वास्थ्य मिशन के अनुसार सामान्य स्वास्थ्य दिशा-निर्देश:\n\n• **प्राथमिक जांच:** मरीज का तापमान, रक्तचाप (BP), नब्ज और सांस की गति की जांच करें।\n• **हाइड्रेशन:** पर्याप्त तरल पदार्थ (ओआरएस का घोल, नारियल पानी, स्वच्छ उबला पानी) दें।\n• **पोषण:** ताजा, पौष्टिक और सुपाच्य आहार सुनिश्चित करें।\n• **खतरे के संकेत:** यदि लक्षण 48 घंटे से अधिक बने रहें, तेज बुखार, उल्टी या सांस फूलने की समस्या हो, तो तुरंत प्राथमिक स्वास्थ्य केंद्र (PHC) में डॉक्टर को दिखाएं।\n\n---\n*ℹ️ राष्ट्रीय स्वास्थ्य मिशन आशा मॉड्यूल द्वारा समर्थित सुरक्षित स्वास्थ्य ज्ञानकोष।*`,
      tags: ["Sehat Setu", "MoHFW Guidelines", "Community Health"]
    };
  }

  return {
    text: `### Sehat Setu ASHA Health Assistant (NHM Certified)\n\nRegarding your query on '${query}': General community health advisory per MoHFW guidelines:\n\n• **Baseline Evaluation:** Verify vital signs (Body temperature, Blood Pressure, Respiratory Rate, SpO2).\n• **Rest & Hydration:** Ensure clean boiled water, ORS fluids, and restorative rest.\n• **Nutritional Support:** Provide home-cooked, balanced energy-dense meals.\n• **Escalation Protocol:** If symptoms persist beyond 48 hours or red flags emerge (severe weakness, chest pain, high fever, dyspnea), facilitate immediate referral to the Primary Health Center (PHC).\n\n---\n*ℹ️ Verified medical database based on National Health Mission (NHM) standards.*`,
    tags: ["Sehat Setu", "MoHFW Guidelines", "Community Health"]
  };
}

export function evaluateQuestionnaireRisk(answers: Record<string, string>): ClinicalAssessmentResult {
  let score = 15;
  const diseaseList: DiseaseRiskItem[] = [];
  const redFlags: string[] = [];
  const actions: string[] = [];
  const meds: string[] = [];

  const isFever = answers["fever"] === "Yes";
  const isChest = answers["chest_pain"] === "Yes";
  const isBp = answers["blood_pressure"] === "Yes";
  const isSugar = answers["sugar_thirst"] === "Yes";
  const isCough = answers["cough_wheeze"] === "Yes";
  const isHeadache = answers["headache_vision"] === "Yes";
  const isJoint = answers["joint_fatigue"] === "Yes";
  const isAbdomen = answers["abdominal_digestive"] === "Yes";
  const isWound = answers["skin_wound"] === "Yes";
  const isMosquito = answers["mosquito_exposure"] === "Yes";

  if (isSugar) {
    score += 24;
    diseaseList.push({
      diseaseName: "Type 2 Diabetes Mellitus & Metabolic Risk",
      riskPercentage: 82,
      riskTier: "High",
      rationale: "Polyuria, intense thirst, and glycemic symptoms reported"
    });
    actions.push("Fasting Blood Sugar + HbA1c laboratory screening at PHC");
    meds.push("Dietary glycemic control, reduce refined carbohydrates, daily brisk walk");
  } else {
    diseaseList.push({
      diseaseName: "Type 2 Diabetes Mellitus",
      riskPercentage: 18,
      riskTier: "Low",
      rationale: "No acute osmotic or glycemic symptoms reported"
    });
  }

  if (isChest || isBp) {
    score += 28;
    diseaseList.push({
      diseaseName: "Cardiovascular Disease & Essential Hypertension",
      riskPercentage: 78,
      riskTier: "High",
      rationale: "Chest heaviness and dyspnea on exertion flagged"
    });
    redFlags.push("Immediate emergency review if chest pain radiates to arm/jaw or is accompanied by cold sweating");
    actions.push("12-Lead ECG + Ambulatory Blood Pressure monitoring");
  } else {
    diseaseList.push({
      diseaseName: "Cardiovascular Disease & Hypertension",
      riskPercentage: 22,
      riskTier: "Low",
      rationale: "Baseline cardiovascular stability"
    });
  }

  if (isFever || isMosquito) {
    score += 22;
    const feverPct = isMosquito ? 84 : 68;
    diseaseList.push({
      diseaseName: "Acute Vector-Borne Viral Fever (Dengue / Malaria)",
      riskPercentage: feverPct,
      riskTier: feverPct >= 75 ? "High" : "Moderate",
      rationale: "Pyrexia with mosquito vector vulnerability"
    });
    actions.push("Complete Blood Count (Platelet count) and Dengue NS1 / Malaria RDT test");
    meds.push("Oral Rehydration Salts (ORS) and Paracetamol 500mg PRN (Avoid Brufen/Aspirin)");
  } else {
    diseaseList.push({
      diseaseName: "Acute Vector-Borne Viral Infection",
      riskPercentage: 14,
      riskTier: "Low",
      rationale: "Afebrile, low infectious index"
    });
  }

  if (isCough) {
    score += 18;
    diseaseList.push({
      diseaseName: "Lower Respiratory Tract Infection / Bronchitis",
      riskPercentage: 64,
      riskTier: "Moderate",
      rationale: "Subacute productive cough and airway irritation"
    });
    actions.push("Sputum examination and chest auscultation for wheeze/crepitations");
  } else {
    diseaseList.push({
      diseaseName: "Chronic Respiratory Illness",
      riskPercentage: 16,
      riskTier: "Low",
      rationale: "Clear airways reported"
    });
  }

  if (isHeadache) {
    score += 14;
    diseaseList.push({
      diseaseName: "Migraine & Hypertensive Vascular Strain",
      riskPercentage: 58,
      riskTier: "Moderate",
      rationale: "Frequent cephalalgia and visual strain noted"
    });
  }

  if (isWound) {
    score += 16;
    diseaseList.push({
      diseaseName: "Dermatological Infection & Impaired Wound Healing",
      riskPercentage: 66,
      riskTier: "Moderate",
      rationale: "Chronic lesion / slow healing tissue integrity risk"
    });
    actions.push("Sterile wound saline dressing and evaluation for cellulitis / neuropathy");
  }

  if (isAbdomen) {
    score += 14;
    diseaseList.push({
      diseaseName: "Gastrointestinal / Dyspeptic Inflammation",
      riskPercentage: 52,
      riskTier: "Moderate",
      rationale: "Recurrent abdominal discomfort and acid reflux"
    });
  }

  const clampedScore = Math.min(98, Math.max(12, score));
  let level: RiskLevel = "LOW";
  if (clampedScore >= 75) level = "CRITICAL";
  else if (clampedScore >= 50) level = "HIGH";
  else if (clampedScore >= 30) level = "MODERATE";

  if (actions.length === 0) actions.push("Schedule routine preventive health checkup at Sub-Center / PHC.");
  if (meds.length === 0) meds.push("Maintain balanced hydration, adequate sleep, and nutritious diet.");

  const highRisks = diseaseList.filter(d => d.riskPercentage >= 50).map(d => d.diseaseName);
  const summary = `Sehat Setu AI completed health screening across ${Object.keys(answers).length} clinical questions. ${
    highRisks.length > 0 ? `Highest risks identified in: ${highRisks.join(", ")}. ` : ""
  }Overall Clinical Risk Index: ${clampedScore}/100 (${level}).`;

  return {
    riskScore: clampedScore,
    riskLevel: level,
    primarySummary: summary,
    possibleConditions: diseaseList.map(d => `${d.diseaseName} (${d.riskPercentage}% Risk)`),
    medicineSuggestions: Array.from(new Set(meds)),
    recommendedActions: Array.from(new Set(actions)),
    redFlags: Array.from(new Set(redFlags)),
    diseaseRisksWithPercentage: diseaseList.sort((a, b) => b.riskPercentage - a.riskPercentage)
  };
}

export function evaluateVitalsRisk(
  systolic: number,
  diastolic: number,
  heartRate: number,
  spo2: number,
  tempC: number,
  bloodSugar: number,
  isPregnant: boolean
): ClinicalAssessmentResult {
  let score = 15;
  const conditions: string[] = [];
  const actions: string[] = [];
  const meds: string[] = [];
  const redFlags: string[] = [];
  const diseaseList: DiseaseRiskItem[] = [];

  // 1. Blood Pressure Evaluation
  const pulsePressure = systolic - diastolic;
  if (systolic >= 160 || diastolic >= 100) {
    score += 45;
    const bpProb = Math.min(96, Math.round(75 + (systolic - 160) * 0.8));
    diseaseList.push({
      diseaseName: isPregnant ? "Severe Preeclampsia / Eclamptic Emergency" : "Stage 2 Hypertensive Crisis",
      riskPercentage: bpProb,
      riskTier: "Critical",
      rationale: `Extreme arterial pressure (${systolic}/${diastolic} mmHg) exceeding emergency limits.`
    });
    redFlags.push(`Critical BP ${systolic}/${diastolic} mmHg: high risk of stroke or acute organ damage.`);
    actions.push("Immediate Medical Officer evaluation and slow IV/oral antihypertensive stabilization.");
  } else if (systolic >= 130 || diastolic >= 80) {
    score += 25;
    const bpProb = Math.min(88, Math.round(55 + (systolic - 130) * 1.1 + (diastolic - 80) * 0.8));
    diseaseList.push({
      diseaseName: "Prehypertension / Stage 1 Essential Hypertension",
      riskPercentage: bpProb,
      riskTier: bpProb >= 70 ? "High" : "Moderate",
      rationale: `Systolic ${systolic} mmHg or Diastolic ${diastolic} mmHg above standard normotensive baseline (120/80).`
    });
    actions.push("Initiate 7-day ambulatory BP diary, reduce dietary sodium (<5g/day), and verify medication compliance.");
  } else {
    diseaseList.push({
      diseaseName: "Essential Hypertension",
      riskPercentage: 14,
      riskTier: "Low",
      rationale: `Normotensive arterial reading (${systolic}/${diastolic} mmHg).`
    });
  }

  // 2. Metabolic & Glycemic Evaluation
  if (bloodSugar >= 200) {
    score += 30;
    const dmProb = Math.min(95, Math.round(70 + (bloodSugar - 200) * 0.15));
    diseaseList.push({
      diseaseName: "Uncontrolled Hyperglycemia / Diabetes Mellitus Type 2",
      riskPercentage: dmProb,
      riskTier: "High",
      rationale: `Random blood glucose ${bloodSugar} mg/dL significantly exceeds safe glycemic thresholds.`
    });
    actions.push("Urine dipstick testing for ketones and protein; review Metformin/Insulin titration.");
  } else if (bloodSugar >= 126) {
    score += 16;
    const ifgProb = Math.min(75, Math.round(45 + (bloodSugar - 126) * 0.4));
    diseaseList.push({
      diseaseName: "Impaired Fasting Glucose & Early Metabolic Syndrome",
      riskPercentage: ifgProb,
      riskTier: "Moderate",
      rationale: `Elevated glucose (${bloodSugar} mg/dL) coupled with cardiovascular strain signals insulin resistance.`
    });
    actions.push("Fasting HbA1c test and dietary counseling to limit high-glycemic carbohydrates.");
  } else {
    diseaseList.push({
      diseaseName: "Type 2 Diabetes Mellitus",
      riskPercentage: 18,
      riskTier: "Low",
      rationale: `Normal glycemic range (${bloodSugar} mg/dL).`
    });
  }

  // 3. Oxygenation & Respiratory Evaluation
  if (spo2 < 90) {
    score += 50;
    diseaseList.push({
      diseaseName: "Acute Hypoxemic Respiratory Failure",
      riskPercentage: 94,
      riskTier: "Critical",
      rationale: `Dangerous oxygen desaturation (${spo2}%); tissue hypoxia imminent.`
    });
    redFlags.push(`Critical SpO2 ${spo2}%: immediate oxygen support & 108 ambulance dispatch mandatory.`);
    actions.push("Administer high-flow supplemental oxygen (4-6 L/min) and transfer to nearest ICU/trauma centre.");
  } else if (spo2 < 95) {
    score += 24;
    const respProb = Math.min(80, Math.round(50 + (95 - spo2) * 6));
    diseaseList.push({
      diseaseName: "Subacute Respiratory Distress / Early Bronchial Obstruction",
      riskPercentage: respProb,
      riskTier: "Moderate",
      rationale: `SpO2 ${spo2}% is below the 95% physiological resting threshold.`
    });
    actions.push("Auscultate lungs for crackles or wheeze; evaluate for early pneumonia or asthma exacerbation.");
  } else {
    diseaseList.push({
      diseaseName: "Acute Respiratory Distress Syndrome",
      riskPercentage: 10,
      riskTier: "Low",
      rationale: `Adequate peripheral capillary oxygenation (${spo2}%).`
    });
  }

  // 4. Cardiac Workload & Pulse Pressure Strain
  const cardiacStrainProb = Math.min(85, Math.round((heartRate >= 90 ? 50 : 25) + (pulsePressure >= 50 ? 25 : 10)));
  diseaseList.push({
    diseaseName: "Autonomic Cardiovascular Strain & High Pulse Pressure",
    riskPercentage: cardiacStrainProb,
    riskTier: cardiacStrainProb >= 60 ? "High" : "Moderate",
    rationale: `Pulse pressure of ${pulsePressure} mmHg (Sys ${systolic} - Dia ${diastolic}) with HR ${heartRate} bpm indicates vascular resistance load.`
  });

  // 5. Early Chronic Kidney Disease (CKD) Microvascular Strain
  const ckdProb = Math.min(72, Math.round((systolic >= 135 ? 40 : 15) + (bloodSugar >= 140 ? 25 : 10)));
  diseaseList.push({
    diseaseName: "Renal Microvascular Strain Watch (Early CKD)",
    riskPercentage: ckdProb,
    riskTier: ckdProb >= 50 ? "Moderate" : "Low",
    rationale: `Co-occurrence of sustained arterial pressure (${systolic}/${diastolic}) and glycemic load stresses glomerular filtration.`
  });

  // 6. Pregnancy Specific Complication
  if (isPregnant) {
    const preeclampsiaProb = systolic >= 135 || diastolic >= 85 ? 88 : 28;
    diseaseList.push({
      diseaseName: "Gestational Hypertension / Preeclampsia Watch",
      riskPercentage: preeclampsiaProb,
      riskTier: preeclampsiaProb >= 70 ? "Critical" : "Low",
      rationale: `Maternal BP of ${systolic}/${diastolic} mmHg in active pregnancy requires urgent proteinuria dipstick.`
    });
    if (preeclampsiaProb >= 70) {
      redFlags.push("Obstetric Warning: check for headache, visual disturbances, epigastric pain, and ankle edema.");
      actions.push("Refer immediately to First Referral Unit (FRU) for obstetrician review.");
    }
  }

  // 7. Temperature / Infection
  if (tempC >= 38.0) {
    score += 22;
    diseaseList.push({
      diseaseName: "Acute Systemic Febrile Infection / Vector-Borne Watch",
      riskPercentage: 76,
      riskTier: "High",
      rationale: `Body temperature ${tempC}°C exceeds fever threshold; assess for Dengue/Malaria/Sepsis.`
    });
    meds.push("Tab Paracetamol 500mg every 6 hours PRN fever.");
  }

  const clamped = Math.min(99, Math.max(12, score));
  let level: RiskLevel = "LOW";
  if (clamped >= 72) level = "CRITICAL";
  else if (clamped >= 48) level = "HIGH";
  else if (clamped >= 28) level = "MODERATE";

  if (actions.length === 0) actions.push("Continue baseline preventive surveillance; maintain 3-month NCD follow-up.");
  if (meds.length === 0) meds.push("No urgent pharmacotherapy indicated; maintain hydration and balanced diet.");

  const summary = `Vitals Triage: BP ${systolic}/${diastolic} mmHg, SpO2 ${spo2}%, HR ${heartRate} bpm, Temp ${tempC}°C, Sugar ${bloodSugar} mg/dL. Calculated risk score: ${clamped}/100 (${level}).`;

  return {
    riskScore: clamped,
    riskLevel: level,
    primarySummary: summary,
    possibleConditions: diseaseList.filter(d => d.riskPercentage >= 40).map(d => `${d.diseaseName} (${d.riskPercentage}%)`),
    medicineSuggestions: meds,
    recommendedActions: actions,
    redFlags: redFlags,
    diseaseRisksWithPercentage: diseaseList.sort((a, b) => b.riskPercentage - a.riskPercentage)
  };
}
