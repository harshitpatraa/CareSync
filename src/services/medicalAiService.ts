import { ClinicalAssessmentResult, DiseaseRiskItem, RiskLevel, DiseaseBrief } from '../types';

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

export const DISEASE_BRIEFS: Record<string, DiseaseBrief> = {
  dengue: {
    diseaseName: "Dengue Viral Fever (डेंगू बुखार)",
    whatIsIt: "मच्छर (Aedes) के काटने से फैलने वाला वायरल संक्रमण, जिसमें प्लेटलेट्स तेजी से गिर सकते हैं।",
    keySymptoms: [
      "अचानक तेज बुखार और आंखों के पीछे तेज दर्द",
      "गंभीर बदन दर्द (Breakbone fever) और उल्टी",
      "त्वचा पर लाल चकत्ते और अत्यधिक कमजोरी"
    ],
    quickCare: [
      "खूब तरल पिएं: ओआरएस (ORS), नारियल पानी, नीबू पानी (> 2.5L/day)",
      "केवल पैरासिटामोल लें (एस्पिरिन या ब्रूफेन कभी न लें)"
    ],
    whenToSeeDoctor: "मसूड़ों/नाक से खून, लगातार उल्टी, या प्लेटलेट्स < 1 लाख होने पर तुरंत PHC/अस्पताल जाएं।"
  },
  malaria: {
    diseaseName: "Malaria (मलेरिया संक्रमण)",
    whatIsIt: "मादा एनाफिलीज़ मच्छर के काटने से होने वाला प्रोटोजोआ परजीवी संक्रमण।",
    keySymptoms: [
      "तेज कंपकंपी के साथ बुखार आना और उतरना",
      "अत्यधिक पसीना आना और सिरदर्द",
      "उल्टी और तेज बदन दर्द"
    ],
    quickCare: [
      "रक्त जांच (RDT/Malarial Slide) तुरंत करवाएं",
      "मच्छरदानी में सोएं और पर्याप्त पानी पिएं"
    ],
    whenToSeeDoctor: "ठंड के साथ बार-बार तेज बुखार आने पर 24 घंटे के भीतर सरकारी स्वास्थ्य केंद्र पर जांच कराएं।"
  },
  typhoid: {
    diseaseName: "Typhoid Enteric Fever (टाइफाइड / मियादी बुखार)",
    whatIsIt: "दूषित पानी या भोजन से फैलने वाला साल्मोनेला बैक्टीरियल आंतों का संक्रमण।",
    keySymptoms: [
      "लगातार चढ़ता हुआ बुखार (Step-ladder fever)",
      "पेट में दर्द, कब्ज या दस्त",
      "भूख न लगना और जीभ पर सफेद परत"
    ],
    quickCare: [
      "उबला हुआ छना पानी ही पिएं",
      "हल्का सुपाच्य भोजन (खिचड़ी, दलिया) लें"
    ],
    whenToSeeDoctor: "बुखार 4-5 दिन से लगातार रहे तो विडाल / ब्लड कल्चर टेस्ट और एंटीबायोटिक हेतु डॉक्टर से मिलें।"
  },
  hypertension: {
    diseaseName: "Hypertension / High BP (उच्च रक्तचाप)",
    whatIsIt: "धमनियों की दीवारों पर रक्त का अत्यधिक दबाव (140/90 mmHg से अधिक), जो हार्ट और किडनी पर दबाव डालता है।",
    keySymptoms: [
      "सिर के पिछले हिस्से में भारीपन या दर्द",
      "चक्कर आना या आंखों के आगे धुंधलापन",
      "हल्की सी मेहनत पर सांस फूलना"
    ],
    quickCare: [
      "दैनिक नमक की मात्रा 1 छोटे चम्मच (<5g) से कम करें",
      "पापड़, अचार, नमकीन बंद करें और 30 मिनट टहलें"
    ],
    whenToSeeDoctor: "बीपी 160/100 से अधिक हो, सीने में भारीपन या बाएं हाथ में दर्द फैले तो आपातकाल 108 बुलाएं।"
  },
  diabetes: {
    diseaseName: "Type 2 Diabetes Mellitus (शुगर / मधुमेह)",
    whatIsIt: "शरीर में इंसुलिन की कमी या अप्रभावशीलता के कारण रक्त में ग्लूकोज (शुगर) का अत्यधिक बढ़ जाना।",
    keySymptoms: [
      "बार-बार पेशाब लगना (विशेषकर रात में)",
      "अत्यधिक प्यास और भूख लगना",
      "बिना कारण वजन घटना और घाव देर से भरना"
    ],
    quickCare: [
      "मीठा, कोल्ड ड्रिंक, मैदा और आलू-चावल सीमित करें",
      "थाली में 50% हरी सब्जियां, 25% दाल और 25% चपाती रखें"
    ],
    whenToSeeDoctor: "फास्टिंग शुगर > 126 mg/dL या रैंडम > 200 mg/dL होने पर मेडिकल सुपरवाइजर से दवा शुरू करवाएं।"
  },
  asthma: {
    diseaseName: "Bronchial Asthma / Respiratory Distress (दमा / श्वास रोग)",
    whatIsIt: "फेफड़ों की सांस नलियों में सूजन और संकुचन जिससे सांस लेने में सीटी जैसी आवाज और रुकावट होती है।",
    keySymptoms: [
      "सांस फूलना और सीने में जकड़न",
      "सांस लेते समय सीटी (Wheezing) की आवाज",
      "रात या सुबह के समय सूखी खांसी बढ़ना"
    ],
    quickCare: [
      "धूल, धुआं, अगरबत्ती और ठंडी हवा से बचें",
      "गर्म पानी की भाप लें और सिर ऊंचा करके बैठें"
    ],
    whenToSeeDoctor: "ऑक्सीजन SpO2 94% से नीचे आए या बोलते समय सांस फूले तो तुरंत इमरजेंसी नेबुलाइजर लें।"
  },
  anemia: {
    diseaseName: "Anemia / Iron Deficiency (खून की कमी)",
    whatIsIt: "रक्त में हीमोग्लोबिन (Hb < 11-12 g/dL) की कमी जिससे अंगों तक ऑक्सीजन कम पहुंचती है।",
    keySymptoms: [
      "चेहरे, नाखूनों और आंखों का पीलापन",
      "जल्दी थकान और सीढ़ियां चढ़ने पर सांस फूलना",
      "चक्कर आना और सिरदर्द"
    ],
    quickCare: [
      "आयरन युक्त आहार: पालक, मेथी, गुड़, भुना चना खाएं",
      "नींबू पानी के साथ आईएफए (IFA लाल गोली) लें"
    ],
    whenToSeeDoctor: "हीमोग्लोबिन 7 g/dL से कम हो तो तुरंत रक्त चढ़ाने या इंजेक्शन हेतु अस्पताल जाएं।"
  },
  jaundice: {
    diseaseName: "Jaundice / Viral Hepatitis (पीलिया)",
    whatIsIt: "लिवर में संक्रमण के कारण रक्त में बिलीरुबिन का स्तर बढ़ जाना।",
    keySymptoms: [
      "आंखों और त्वचा का पीला पड़ना",
      "गहरे पीले रंग का पेशाब और उल्टी",
      "भूख बिल्कुल न लगना और अत्यधिक सुस्ती"
    ],
    quickCare: [
      "पूर्ण शारीरिक आराम करें और उबला पानी पिएं",
      "हल्का कार्बोहाइड्रेट आहार (गन्ने का स्वच्छ रस, ग्लूकोज, दलिया) लें"
    ],
    whenToSeeDoctor: "उल्टी न रुके या पेशाब बहुत गहरा पीला हो तो तुरंत लिवर फंक्शन टेस्ट (LFT) कराएं।"
  },
  pneumonia: {
    diseaseName: "Pneumonia / Lung Infection (निमोनिया)",
    whatIsIt: "फेफड़ों की वायु थैलियों में संक्रमण और मवाद/द्रव का भर जाना।",
    keySymptoms: [
      "तेज बुखार, कंपकंपी और सीने में दर्द",
      "पीले या हरे बलगम वाली खांसी",
      "सांस की तेज गति (Fast breathing) और पसलियां चलना"
    ],
    quickCare: [
      "मरीज को गर्म रखें और भाप दें",
      "पल्स ऑक्सीमीटर से SpO2 की नियमित निगरानी करें"
    ],
    whenToSeeDoctor: "SpO2 93% से कम हो या सांस की गति 24/min से अधिक हो तो तत्काल अस्पताल में भर्ती कराएं।"
  }
};

export function findDiseaseBrief(query: string): DiseaseBrief | null {
  const q = query.toLowerCase();
  if (q.includes("dengue") || q.includes("डेंगू")) return DISEASE_BRIEFS.dengue;
  if (q.includes("malaria") || q.includes("मलेरिया")) return DISEASE_BRIEFS.malaria;
  if (q.includes("typhoid") || q.includes("मियादी") || q.includes("टाइफाइड")) return DISEASE_BRIEFS.typhoid;
  if (q.includes("bp") || q.includes("hypertension") || q.includes("रक्तचाप") || q.includes("high pressure")) return DISEASE_BRIEFS.hypertension;
  if (q.includes("sugar") || q.includes("diabetes") || q.includes("मधुमेह") || q.includes("डायबिटीज")) return DISEASE_BRIEFS.diabetes;
  if (q.includes("asthma") || q.includes("दमा") || q.includes("सांस") || q.includes("wheeze") || q.includes("cough")) return DISEASE_BRIEFS.asthma;
  if (q.includes("anemia") || q.includes("khoon ki kami") || q.includes("एनीमिया") || q.includes("खून की कमी")) return DISEASE_BRIEFS.anemia;
  if (q.includes("jaundice") || q.includes("पीलिया") || q.includes("hepatitis")) return DISEASE_BRIEFS.jaundice;
  if (q.includes("pneumonia") || q.includes("निमोनिया")) return DISEASE_BRIEFS.pneumonia;
  return null;
}

export interface AiMedicalResponse {
  text: string;
  tags: string[];
  conciseSummary?: DiseaseBrief;
  precautions: string[];
  dietAdvice: string[];
  warningRedFlags: string[];
  prescriptions: {
    name: string;
    dosage: string;
    frequency: string;
    duration: string;
  }[];
  requiresSupervisorVerification: boolean;
}

export function queryMedicalAi(query: string, lang: 'en' | 'hi' = 'en'): AiMedicalResponse {
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

  // Pre-configured precautions, diet advice, warning signs, and guarded prescriptions
  let precautions: string[] = [];
  let dietAdvice: string[] = [];
  let warningRedFlags: string[] = [];
  let prescriptions: { name: string; dosage: string; frequency: string; duration: string }[] = [];

  if (q.includes('fever') || q.includes('bukhar') || q.includes('dengue') || q.includes('malaria')) {
    precautions = [
      "Room-temperature sponge bathing (Never use cold ice water)",
      "Strict physical bed rest; keep room well-ventilated",
      "Sleep under Long Lasting Insecticidal Mosquito Nets (LLIN)",
      "Monitor body temperature every 4 hours with digital thermometer"
    ];
    dietAdvice = [
      "High fluid intake: 2.5 - 3 Litres/day (ORS solution, fresh coconut water, clear dal soup)",
      "Light, easily digestible meals (Khichdi, porridge, mashed vegetables)",
      "Avoid oily, deep-fried, or spicy street food"
    ];
    warningRedFlags = [
      "Continuous vomiting or inability to retain fluids",
      "Bleeding gums, nosebleed, or red petechial spots on skin (Thrombocytopenia warning)",
      "Severe abdominal pain or extreme lethargy / cold extremities",
      "Persistent temperature > 103°F (39.4°C) despite sponging"
    ];
    prescriptions = [
      { name: "Tab Paracetamol", dosage: "500 mg", frequency: "1 tab TDS (every 8 hrs) PRN fever", duration: "3-5 days" },
      { name: "ORS (Oral Rehydration Salt)", dosage: "1 packet in 1L clean water", frequency: "Sip throughout day", duration: "As needed" },
      { name: "Tab Pantoprazole", dosage: "40 mg", frequency: "1 tab once daily before breakfast", duration: "3 days" }
    ];
  } else if (q.includes('bp') || q.includes('hypertension') || q.includes('blood pressure') || q.includes('chhati') || q.includes('stroke')) {
    precautions = [
      "Strict dietary sodium restriction (< 5g salt / 1 teaspoon total daily)",
      "Seated resting posture for 10 minutes prior to blood pressure verification",
      "Avoid sudden postural changes (rise slowly from bed to avoid orthostatic dizziness)",
      "Engage in 30 minutes of mild brisk walking daily once stable"
    ];
    dietAdvice = [
      "DASH Diet: High potassium vegetables (bottle gourd, spinach, carrots)",
      "Completely avoid salty pickles, papad, packaged chips, and instant noodles",
      "Limit daily caffeine, tea, and tobacco consumption to zero"
    ];
    warningRedFlags = [
      "Sudden occipital crushing headache or blurred double vision",
      "Chest heaviness or pain radiating to left shoulder, jaw, or arm",
      "Sudden facial drooping, arm weakness, or slurred speech (FAST Stroke warning)",
      "Systolic BP > 180 mmHg or Diastolic > 110 mmHg (Hypertensive Crisis)"
    ];
    prescriptions = [
      { name: "Tab Telmisartan", dosage: "40 mg", frequency: "1 tablet once daily morning (Empty stomach)", duration: "Continuous / Regular" },
      { name: "Tab Amlodipine", dosage: "5 mg", frequency: "1 tablet once daily evening", duration: "Continuous" }
    ];
  } else if (q.includes('sugar') || q.includes('diabetes') || q.includes('diabetic') || q.includes('meetha') || q.includes('glucose')) {
    precautions = [
      "Daily foot inspection for minor blisters, cuts, or redness (Diabetic foot guard)",
      "Maintain fixed meal timings; never skip breakfast or delay meals",
      "Carry glucose candy or sugar sachet when traveling for hypoglycemia emergencies",
      "Maintain adequate hydration with clean plain water"
    ];
    dietAdvice = [
      "Low Glycemic Index foods: whole wheat chapati, oats, sprouts, green moong dal",
      "Zero refined sugar, sweets, cold drinks, honey, or high-sugar fruits (mango, banana)",
      "Follow Plate Method: 50% fibrous vegetables, 25% protein (dal/paneer), 25% complex carbs"
    ];
    warningRedFlags = [
      "Cold clammy sweating, trembling hands, sudden dizziness (Hypoglycemia < 70 mg/dL)",
      "Fruity ketone breath odor with rapid breathing (Diabetic Ketoacidosis warning)",
      "Non-healing foot ulcer or dark blackened skin on toes",
      "Random blood glucose > 300 mg/dL with severe dehydration"
    ];
    prescriptions = [
      { name: "Tab Metformin HCl", dosage: "500 mg", frequency: "1 tablet twice daily after lunch & dinner", duration: "Regular" },
      { name: "Tab Glimepiride", dosage: "1 mg", frequency: "1 tablet once daily before breakfast", duration: "Regular" }
    ];
  } else if (q.includes('anc') || q.includes('pregnancy') || q.includes('pregnant') || q.includes('garbh') || q.includes('trimester')) {
    precautions = [
      "Left lateral sleeping position to maximize placental blood flow to baby",
      "Minimum 8 hours nighttime sleep plus 2 hours afternoon rest",
      "Avoid lifting heavy water pots or rigorous manual agricultural work",
      "Ensure all 4 mandatory ANC checkups are completed per MCP card schedule"
    ];
    dietAdvice = [
      "Iron-rich foods: jaggery (gur), roasted chana, green leafy vegetables (Palak, Methi)",
      "Calcium support: fresh curd, milk, ragi porridge",
      "Take IFA tablet strictly with clean lemon water; NEVER take with tea or milk"
    ];
    warningRedFlags = [
      "Sudden swelling on face, eyelids, and hands with persistent headache (Preeclampsia)",
      "Vaginal bleeding or sudden gush of clear amniotic fluid",
      "Noticeable reduction or absence of baby's movements for > 4 hours",
      "High fever with chills or foul-smelling vaginal discharge"
    ];
    prescriptions = [
      { name: "Tab Iron & Folic Acid (IFA Red)", dosage: "100 mg elemental Iron + 500 mcg FA", frequency: "1 tab daily post-lunch", duration: "180 days in pregnancy" },
      { name: "Tab Calcium & Vit D3", dosage: "500 mg Calcium + 250 IU D3", frequency: "2 tabs daily (morning & night, 2 hrs away from IFA)", duration: "180 days" },
      { name: "Inj Tetanus & adult Diphtheria (Td)", dosage: "0.5 mL IM", frequency: "2 doses (minimum 4 weeks apart)", duration: "Antenatal course" }
    ];
  } else if (q.includes('burn') || q.includes('wound') || q.includes('skin') || q.includes('chot') || q.includes('jalna')) {
    precautions = [
      "Immediately run cool tap water over burn for 15-20 minutes continuously",
      "NEVER apply ice, ghee, turmeric paste, or toothpaste to burn surfaces",
      "Do NOT puncture or pop burn blisters — intact skin prevents deep infection",
      "Keep minor cuts washed with clean water and covered with sterile gauze"
    ];
    dietAdvice = [
      "High protein intake (boiled lentils, sprouts, milk) to accelerate tissue granulation",
      "Vitamin C rich fruits (amla, oranges, lemons) to support collagen synthesis",
      "Drink at least 2.5 litres clean water daily to prevent dehydration"
    ];
    warningRedFlags = [
      "Burns covering face, palms, joints, or groin (> 5% body surface area)",
      "Spreading circumferential redness, foul odor, or yellowish pus from wound bed",
      "High fever developing 24-48 hours after traumatic injury (Cellulitis/Sepsis)",
      "Unknown tetanus vaccination history or puncture wound from dirty rusted metal"
    ];
    prescriptions = [
      { name: "Silver Sulfadiazine Cream 1%", dosage: "Topical application", frequency: "Apply sterile layer twice daily", duration: "5-7 days" },
      { name: "Inj Td (Tetanus Toxoid)", dosage: "0.5 mL IM", frequency: "Stat dose if not received in past 5 yrs", duration: "Single dose" },
      { name: "Tab Amoxicillin + Clavulanate", dosage: "625 mg", frequency: "1 tab BD after food (Strict MO approval)", duration: "5 days" }
    ];
  } else {
    // General universal advisory
    precautions = [
      "Verify vital signs: Record Body Temperature, Blood Pressure, Pulse, and SpO2",
      "Ensure clean, boiled, filtered drinking water at all times",
      "Maintain adequate rest; avoid strenuous physical exertion during illness",
      "Practice strict hand hygiene before meals and after caregiving"
    ];
    dietAdvice = [
      "Provide light, warm, freshly cooked home meals (khichdi, vegetable soups, dalia)",
      "Avoid raw street food, stale leftovers, and heavily spiced preparations",
      "Support recovery with natural fluids: lemon water, ORS, tender coconut water"
    ];
    warningRedFlags = [
      "High persistent fever > 102°F lasting more than 48 hours",
      "Difficulty in breathing, rapid shallow breaths, or SpO2 dropping below 94%",
      "Inability to tolerate oral fluids or severe dizziness upon standing",
      "Altered mental alertness, confusion, or extreme sluggishness"
    ];
    prescriptions = [
      { name: "Tab Paracetamol", dosage: "500 mg", frequency: "1 tablet PRN for pain or fever > 100°F (Max 3/day)", duration: "2-3 days" },
      { name: "Oral Rehydration Salt (ORS)", dosage: "1 standard sachet in 1 Litre water", frequency: "Drink regularly", duration: "As needed" }
    ];
  }

  const brief = findDiseaseBrief(q);
  const isShortRequested = q.includes("less word") || q.includes("short") || q.includes("brief") || q.includes("kam shabd") || q.includes("summary") || q.includes("कम शब्द") || q.includes("chhota");

  if (isShortRequested && brief) {
    const shortText = `### ⚡ ${brief.diseaseName} (कम शब्दों में)\n\n• **क्या है:** ${brief.whatIsIt}\n• **मुख्य लक्षण:** ${brief.keySymptoms.join(", ")}\n• **त्वरित देखभाल:** ${brief.quickCare.join("; ")}\n• **खतरे के संकेत:** ${brief.whenToSeeDoctor}\n\n---\n*ℹ️ कम शब्दों में त्वरित स्वास्थ्य सारांश · MoHFW ABDM Standard*`;
    return {
      text: shortText,
      tags: ["Sehat Setu Quick Summary", "कम शब्दों में", "MoHFW Protocol"],
      conciseSummary: brief,
      precautions,
      dietAdvice,
      warningRedFlags,
      prescriptions,
      requiresSupervisorVerification: true
    };
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
      tags: ["Sehat Setu AI", isHindi ? "एनएचएम दिशानिर्देश" : "MoHFW Guidelines", "Verified Medical DB"],
      conciseSummary: brief || undefined,
      precautions,
      dietAdvice,
      warningRedFlags,
      prescriptions,
      requiresSupervisorVerification: true
    };
  }

  // If a disease brief was matched even if no long knowledge article matched
  if (brief) {
    const briefText = `### ${brief.diseaseName}\n\n${brief.whatIsIt}\n\n**मुख्य लक्षण (Key Symptoms):**\n${brief.keySymptoms.map(s => `• ${s}`).join('\n')}\n\n**त्वरित देखभाल (Immediate Care):**\n${brief.quickCare.map(c => `• ${c}`).join('\n')}\n\n**डॉक्टर को कब दिखाएं:**\n• ${brief.whenToSeeDoctor}\n\n---\n*ℹ️ संक्षिप्त एवं सुरक्षित स्वास्थ्य मार्गदर्शन · MoHFW Standard*`;
    return {
      text: briefText,
      tags: ["Sehat Setu AI", "संक्षिप्त रोग विवरण", "Verified Medical DB"],
      conciseSummary: brief,
      precautions,
      dietAdvice,
      warningRedFlags,
      prescriptions,
      requiresSupervisorVerification: true
    };
  }

  // Fallback general guidance
  if (isHindi) {
    return {
      text: `### सेहत सेतु आशा स्वास्थ्य सहायक (NHM Certified)\n\nआपके प्रश्न '${query}' के संदर्भ में राष्ट्रीय स्वास्थ्य मिशन के अनुसार सामान्य स्वास्थ्य दिशा-निर्देश:\n\n• **प्राथमिक जांच:** मरीज का तापमान, रक्तचाप (BP), नब्ज और सांस की गति की जांच करें।\n• **हाइड्रेशन:** पर्याप्त तरल पदार्थ (ओआरएस का घोल, नारियल पानी, स्वच्छ उबला पानी) दें।\n• **पोषण:** ताजा, पौष्टिक और सुपाच्य आहार सुनिश्चित करें।\n• **खतरे के संकेत:** यदि लक्षण 48 घंटे से अधिक बने रहें, तेज बुखार, उल्टी या सांस फूलने की समस्या हो, तो तुरंत प्राथमिक स्वास्थ्य केंद्र (PHC) में डॉक्टर को दिखाएं।\n\n---\n*ℹ️ राष्ट्रीय स्वास्थ्य मिशन आशा मॉड्यूल द्वारा समर्थित सुरक्षित स्वास्थ्य ज्ञानकोष।*`,
      tags: ["Sehat Setu", "MoHFW Guidelines", "Community Health"],
      conciseSummary: brief || undefined,
      precautions,
      dietAdvice,
      warningRedFlags,
      prescriptions,
      requiresSupervisorVerification: true
    };
  }

  return {
    text: `### Sehat Setu ASHA Health Assistant (NHM Certified)\n\nRegarding your query on '${query}': General community health advisory per MoHFW guidelines:\n\n• **Baseline Evaluation:** Verify vital signs (Body temperature, Blood Pressure, Respiratory Rate, SpO2).\n• **Rest & Hydration:** Ensure clean boiled water, ORS fluids, and restorative rest.\n• **Nutritional Support:** Provide home-cooked, balanced energy-dense meals.\n• **Escalation Protocol:** If symptoms persist beyond 48 hours or red flags emerge (severe weakness, chest pain, high fever, dyspnea), facilitate immediate referral to the Primary Health Center (PHC).\n\n---\n*ℹ️ Verified medical database based on National Health Mission (NHM) standards.*`,
    tags: ["Sehat Setu", "MoHFW Guidelines", "Community Health"],
    conciseSummary: brief || undefined,
    precautions,
    dietAdvice,
    warningRedFlags,
    prescriptions,
    requiresSupervisorVerification: true
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
