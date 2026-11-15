import { Patient, HealthRecord, ActivityItem, EmergencyContact, NearbyHealthCenter, NearbyClinician, WardOutbreakInsight } from '../types';

export const INITIAL_PATIENTS: Patient[] = [
  {
    id: "PT-1001",
    fullName: "Meera Devi",
    age: 26,
    gender: "Female",
    phone: "+91 98765 41001",
    villageOrWard: "Ward 4 · Rampur East",
    bloodGroup: "O+",
    category: "PREGNANCY",
    riskLevel: "HIGH",
    isCritical: false,
    isTodayVisit: true,
    isPendingVisit: true,
    syncStatus: "PENDING_UPLOAD",
    lastVisitLabel: "Today, 09:15 AM",
    nextFollowUpDate: "18 Nov 2026",
    allergies: "Penicillin",
    chronicConditions: "Mild Gestational Anemia (Hb 9.8 g/dL)",
    familyHeadName: "Rajesh Kumar",
    familyMembersCount: 4,
    pregnancyWeeks: 28,
    expectedDueDate: "06 Feb 2027",
    vaccinationProgress: 100,
    notes: "Third trimester ANC visit completed offline. Iron + Folic Acid supplemented; BP borderline elevated (138/88 mmHg).",
    updatedAt: Date.now() - 600000,
    vitals: {
      id: "VT-1001",
      patientId: "PT-1001",
      systolicBp: 138,
      diastolicBp: 88,
      heartRate: 86,
      spo2Percent: 98,
      temperatureC: 36.8,
      bloodSugarMgDl: 112,
      recordedAt: Date.now() - 600000
    }
  },
  {
    id: "PT-1002",
    fullName: "Aarav Sharma",
    age: 3,
    gender: "Male",
    phone: "+91 98112 41002",
    villageOrWard: "Sector 2 · Green Park",
    bloodGroup: "B+",
    category: "CHILD",
    riskLevel: "LOW",
    isCritical: false,
    isTodayVisit: true,
    isPendingVisit: false,
    syncStatus: "SYNCED",
    lastVisitLabel: "Today, 08:30 AM",
    nextFollowUpDate: "24 Nov 2026",
    allergies: "None reported",
    chronicConditions: "None",
    familyHeadName: "Vikram Sharma",
    familyMembersCount: 5,
    childWeightKg: 14.2,
    vaccinationProgress: 85,
    notes: "Growth percentile steady (62nd). DPT Booster scheduled next week.",
    updatedAt: Date.now() - 1800000,
    vitals: {
      id: "VT-1002",
      patientId: "PT-1002",
      systolicBp: 95,
      diastolicBp: 62,
      heartRate: 102,
      spo2Percent: 99,
      temperatureC: 37.0,
      bloodSugarMgDl: 88,
      recordedAt: Date.now() - 1800000
    }
  },
  {
    id: "PT-1003",
    fullName: "Harish Verma",
    age: 62,
    gender: "Male",
    phone: "+91 98204 41003",
    villageOrWard: "Ward 7 · Old Mill Colony",
    bloodGroup: "A+",
    category: "EMERGENCY",
    riskLevel: "CRITICAL",
    isCritical: true,
    isTodayVisit: true,
    isPendingVisit: true,
    syncStatus: "PENDING_UPLOAD",
    lastVisitLabel: "Today, 10:05 AM",
    nextFollowUpDate: "Immediate Referral",
    allergies: "Sulfa drugs",
    chronicConditions: "Hypertension, Type 2 Diabetes",
    familyHeadName: "Harish Verma",
    familyMembersCount: 3,
    vaccinationProgress: 100,
    notes: "Patient reported acute crushing retrosternal chest pain radiating to left arm. 108 Emergency Ambulance requested.",
    updatedAt: Date.now() - 300000,
    vitals: {
      id: "VT-1003",
      patientId: "PT-1003",
      systolicBp: 168,
      diastolicBp: 104,
      heartRate: 114,
      spo2Percent: 91,
      temperatureC: 37.4,
      bloodSugarMgDl: 242,
      recordedAt: Date.now() - 300000
    }
  },
  {
    id: "PT-1004",
    fullName: "Sunita Bai",
    age: 48,
    gender: "Female",
    phone: "+91 98334 41004",
    villageOrWard: "Ward 6 · Shivaji Chowk",
    bloodGroup: "AB+",
    category: "CHRONIC",
    riskLevel: "MODERATE",
    isCritical: false,
    isTodayVisit: false,
    isPendingVisit: false,
    syncStatus: "SYNCED",
    lastVisitLabel: "Yesterday, 04:30 PM",
    nextFollowUpDate: "29 Nov 2026",
    allergies: "Aspirin",
    chronicConditions: "Type 2 Diabetes Mellitus, Neuropathy",
    familyHeadName: "Mohan Bai",
    familyMembersCount: 6,
    vaccinationProgress: 90,
    notes: "Fasting blood sugar 154 mg/dL. Metformin 500mg compliance confirmed. Foot examination clear.",
    updatedAt: Date.now() - 86400000,
    vitals: {
      id: "VT-1004",
      patientId: "PT-1004",
      systolicBp: 130,
      diastolicBp: 84,
      heartRate: 78,
      spo2Percent: 97,
      temperatureC: 36.6,
      bloodSugarMgDl: 154,
      recordedAt: Date.now() - 86400000
    }
  },
  {
    id: "PT-1005",
    fullName: "Raju Gond",
    age: 29,
    gender: "Male",
    phone: "+91 98451 41005",
    villageOrWard: "Ward 6 · Shivaji Chowk",
    bloodGroup: "B+",
    category: "GENERAL",
    riskLevel: "HIGH",
    isCritical: false,
    isTodayVisit: true,
    isPendingVisit: true,
    syncStatus: "PENDING_UPLOAD",
    lastVisitLabel: "Today, 11:20 AM",
    nextFollowUpDate: "16 Nov 2026",
    allergies: "None",
    chronicConditions: "Suspected Vector-Borne Febrile Illness (Dengue watch)",
    familyHeadName: "Raju Gond",
    familyMembersCount: 4,
    vaccinationProgress: 80,
    notes: "High fever 102.4°F with retro-orbital pain and severe arthralgia for 3 days. Dengue NS1 rapid antigen test advised at PHC.",
    updatedAt: Date.now() - 1200000,
    vitals: {
      id: "VT-1005",
      patientId: "PT-1005",
      systolicBp: 110,
      diastolicBp: 74,
      heartRate: 98,
      spo2Percent: 96,
      temperatureC: 39.1,
      bloodSugarMgDl: 104,
      recordedAt: Date.now() - 1200000
    }
  },
  {
    id: "PT-1006",
    fullName: "Pooja Kumari",
    age: 21,
    gender: "Female",
    phone: "+91 98562 41006",
    villageOrWard: "Ward 3 · Anand Nagar",
    bloodGroup: "O+",
    category: "PREGNANCY",
    riskLevel: "LOW",
    isCritical: false,
    isTodayVisit: false,
    isPendingVisit: false,
    syncStatus: "SYNCED",
    lastVisitLabel: "12 Nov 2026",
    nextFollowUpDate: "26 Nov 2026",
    allergies: "None",
    chronicConditions: "Primigravida",
    familyHeadName: "Sanjay Kumar",
    familyMembersCount: 3,
    pregnancyWeeks: 16,
    expectedDueDate: "02 May 2027",
    vaccinationProgress: 100,
    notes: "Second trimester registration completed. IFA Red tablets dispensed (180 count).",
    updatedAt: Date.now() - 172800000,
    vitals: {
      id: "VT-1006",
      patientId: "PT-1006",
      systolicBp: 116,
      diastolicBp: 76,
      heartRate: 74,
      spo2Percent: 99,
      temperatureC: 36.7,
      bloodSugarMgDl: 92,
      recordedAt: Date.now() - 172800000
    }
  }
];

export const INITIAL_RECORDS: HealthRecord[] = [
  {
    id: "REC-2001",
    patientId: "PT-1001",
    patientName: "Meera Devi",
    recordType: "PRESCRIPTION",
    title: "ANC Trimester 3 Clinical Prescription",
    summary: "Iron & Folic Acid 100mg (1 OD pc), Calcium Carbonate 500mg + Vit D3 (1 OD post-bf), Td booster toxoid confirmed.",
    dateLabel: "Today, 09:20 AM",
    doctorName: "Dr. Priya Kulkarni, MO",
    facilityName: "CareSync PHC Hub · Ward 4",
    tags: ["ANC", "MoHFW", "Maternal"]
  },
  {
    id: "REC-2002",
    patientId: "PT-1003",
    patientName: "Harish Verma",
    recordType: "REFERRAL",
    title: "Emergency Cardiac Referral Slip · 108 Dispatch",
    summary: "Severe chest pain radiating to left arm. Immediate 12-lead ECG and Troponin I ordered at District Hospital.",
    dateLabel: "Today, 10:10 AM",
    doctorName: "Dr. Priya Kulkarni, CHO",
    facilityName: "First Referral Unit (FRU) / District Hospital",
    tags: ["Emergency", "Cardiac", "108 Ambulance"]
  },
  {
    id: "REC-2003",
    patientId: "PT-1005",
    patientName: "Raju Gond",
    recordType: "LAB_REPORT",
    title: "Vector-Borne Disease Screening Requisition",
    summary: "Complete Blood Count (CBC) with Platelet count, Dengue NS1 & IgM Antigen, Malaria Peripheral Smear (MP).",
    dateLabel: "Today, 11:30 AM",
    doctorName: "Dr. Arvind Mehta",
    facilityName: "Community Health Center (CHC) Lab",
    tags: ["Dengue", "Platelets", "CBC"]
  },
  {
    id: "REC-2004",
    patientId: "PT-1002",
    patientName: "Aarav Sharma",
    recordType: "IMMUNIZATION",
    title: "Universal Immunization Card Update",
    summary: "Pentavalent-3, OPV-3, and Rotavirus completed. Scheduled DPT Booster 1 for next week.",
    dateLabel: "Today, 08:35 AM",
    doctorName: "Sister Anjali, ANM",
    facilityName: "Sub-Center Anganwadi Hub 2",
    tags: ["Immunization", "Child", "UIP"]
  }
];

export const INITIAL_ACTIVITIES: ActivityItem[] = [
  {
    id: "ACT-3001",
    timestamp: "10 mins ago",
    title: "ANC Visit Documented",
    description: "Meera Devi (28w ANC) vitals recorded: BP 138/88 mmHg, SpO2 98%. Saved in local Room database.",
    type: "visit",
    patientId: "PT-1001",
    patientName: "Meera Devi"
  },
  {
    id: "ACT-3002",
    timestamp: "35 mins ago",
    title: "🚨 SOS Emergency Dispatched",
    description: "Cardiac Alert triggered for Harish Verma (62y). Nearest clinician Dr. Arvind Mehta notified via 108 network.",
    type: "sos",
    patientId: "PT-1003",
    patientName: "Harish Verma"
  },
  {
    id: "ACT-3003",
    timestamp: "1 hour ago",
    title: "Prescription OCR Scanned",
    description: "Scanned handwritten OPD slip: 3 medications matched against local essential medicine database.",
    type: "rx",
    patientId: "PT-1004",
    patientName: "Sunita Bai"
  },
  {
    id: "ACT-3004",
    timestamp: "2 hours ago",
    title: "Ward 6 Outbreak Surveillance",
    description: "Sehat Setu active radar detected 2 acute febrile cases in Shivaji Chowk. Dengue watch alert initiated.",
    type: "alert"
  },
  {
    id: "ACT-3005",
    timestamp: "3 hours ago",
    title: "Offline Vault Auto-Sync",
    description: "6 queued records synchronized with CareSync Cloud Firestore backend successfully.",
    type: "sync"
  }
];

export const EMERGENCY_CONTACTS: EmergencyContact[] = [
  { id: "C-1", name: "108 National Emergency Ambulance", role: "Emergency Medical Transport", phone: "108", isPrimary: true },
  { id: "C-2", name: "102 Janani Shishu Express", role: "Pregnant Mother & Infant Transport", phone: "102", isPrimary: true },
  { id: "C-3", name: "Primary Health Center (PHC) Rampur", role: "24x7 Casualty & Emergency Desk", phone: "+91 11 2789 4501", isPrimary: false },
  { id: "C-4", name: "District Hospital Central ICU", role: "Tertiary Referral & Trauma Care", phone: "+91 11 2789 9999", isPrimary: false },
  { id: "C-5", name: "104 Health Helpline (National)", role: "Tele-Consultation & ASHA Support", phone: "104", isPrimary: false },
  { id: "C-6", name: "112 National Emergency Response", role: "Police & Disaster Management", phone: "112", isPrimary: false }
];

export const NEARBY_CENTERS: NearbyHealthCenter[] = [
  { id: "HC-1", name: "Rampur East Primary Health Center (PHC)", type: "Government PHC", distanceKm: "1.4 km", availability: "Open 24/7 · Casualty Ready", phone: "+91 11 2789 4501" },
  { id: "HC-2", name: "Shivaji Chowk Community Health Center (CHC)", type: "Government CHC", distanceKm: "3.2 km", availability: "Open 24/7 · Blood Storage Unit", phone: "+91 11 2789 6702" },
  { id: "HC-3", name: "District Civil Hospital & Trauma Centre", type: "District Hospital / FRU", distanceKm: "6.8 km", availability: "Open 24/7 · ICU & NICU Facility", phone: "+91 11 2789 9999" },
  { id: "HC-4", name: "Anand Nagar Sub-Center Anganwadi Hub", type: "Sub-Center / VHSND Site", distanceKm: "0.8 km", availability: "Mon-Sat 9 AM - 4 PM", phone: "+91 98111 00234" }
];

export const NEARBY_CLINICIANS: NearbyClinician[] = [
  { id: "DOC-1", name: "Dr. Arvind Mehta, MBBS, MD", role: "Medical Officer In-Charge", healthCentre: "PHC Rampur East", distanceKm: 0.9, phone: "+91 98111 23456", etaMinutes: 4, isAvailable: true },
  { id: "DOC-2", name: "Dr. Priya Kulkarni, CHO", role: "Community Health Officer", healthCentre: "CareSync Hub Ward 4", distanceKm: 1.2, phone: "+91 98111 70409", etaMinutes: 6, isAvailable: true },
  { id: "DOC-3", name: "Sister Anjali Verma, GNM", role: "Senior Staff Nurse & Midwife", healthCentre: "CHC Shivaji Chowk", distanceKm: 2.1, phone: "+91 98222 34567", etaMinutes: 9, isAvailable: true },
  { id: "DOC-4", name: "Dr. Rajeshwar Rao, MS", role: "Emergency Medicine Specialist", healthCentre: "District Hospital", distanceKm: 5.4, phone: "+91 98333 45678", etaMinutes: 18, isAvailable: true }
];

export const WARD_SURVEILLANCE: WardOutbreakInsight = {
  alertLevel: "HIGH",
  headline: "Ward 4 & 6 Dengue & Febrile Cluster Alert",
  summary: "Active surveillance scanned 6 community ward sectors. 2 acute febrile illness cases flagged in Ward 6 (Shivaji Chowk) and 1 gestational high BP case in Ward 4.",
  affectedWards: ["Ward 6 · Shivaji Chowk", "Ward 4 · Rampur East", "Sector 2 · Green Park"],
  recommendations: [
    "Prioritize doorstep follow-up for febrile patients with platelet tracking (CBC).",
    "Administer catch-up immunization for pending infant records in Ward 3.",
    "Deploy municipal vector-control larvicidal spray and community water storage hygiene advisories."
  ]
};
