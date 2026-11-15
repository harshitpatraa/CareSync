export type RiskLevel = 'LOW' | 'MODERATE' | 'HIGH' | 'CRITICAL';

export type PatientCategory = 'PREGNANCY' | 'CHILD' | 'EMERGENCY' | 'GENERAL' | 'CHRONIC' | 'GERIATRIC';

export type SyncStatus = 'SYNCED' | 'PENDING_UPLOAD' | 'LOCAL_ONLY';

export interface Vitals {
  id: string;
  patientId: string;
  systolicBp: number;
  diastolicBp: number;
  heartRate: number;
  spo2Percent: number;
  temperatureC: number;
  bloodSugarMgDl: number;
  recordedAt: number;
}

export interface Patient {
  id: string;
  fullName: string;
  age: number;
  gender: string;
  phone: string;
  villageOrWard: string;
  bloodGroup: string;
  category: PatientCategory;
  riskLevel: RiskLevel;
  isCritical: boolean;
  isTodayVisit: boolean;
  isPendingVisit: boolean;
  syncStatus: SyncStatus;
  lastVisitLabel: string;
  nextFollowUpDate: string;
  allergies: string;
  chronicConditions: string;
  familyHeadName: string;
  familyMembersCount: number;
  pregnancyWeeks?: number;
  expectedDueDate?: string;
  childWeightKg?: number;
  vaccinationProgress: number;
  notes: string;
  updatedAt: number;
  vitals?: Vitals;
}

export interface HealthRecord {
  id: string;
  patientId: string;
  patientName: string;
  recordType: 'PRESCRIPTION' | 'LAB_REPORT' | 'CLINICAL_NOTE' | 'IMMUNIZATION' | 'REFERRAL';
  title: string;
  summary: string;
  dateLabel: string;
  doctorName: string;
  facilityName: string;
  tags: string[];
}

export interface ActivityItem {
  id: string;
  timestamp: string;
  title: string;
  description: string;
  type: 'visit' | 'vitals' | 'rx' | 'sos' | 'sync' | 'alert';
  patientId?: string;
  patientName?: string;
}

export interface EmergencyContact {
  id: string;
  name: string;
  role: string;
  phone: string;
  isPrimary: boolean;
}

export interface NearbyHealthCenter {
  id: string;
  name: string;
  type: string;
  distanceKm: string;
  availability: string;
  phone: string;
}

export interface NearbyClinician {
  id: string;
  name: string;
  role: string;
  healthCentre: string;
  distanceKm: number;
  phone: string;
  etaMinutes: number;
  isAvailable: boolean;
}

export interface DiseaseBrief {
  diseaseName: string;
  whatIsIt: string;
  keySymptoms: string[];
  quickCare: string[];
  whenToSeeDoctor: string;
}

export interface ChatMessage {
  id: string;
  isUser: boolean;
  text: string;
  timestamp: string;
  tags?: string[];
  conciseSummary?: DiseaseBrief;
  precautions?: string[];
  dietAdvice?: string[];
  warningRedFlags?: string[];
  prescriptions?: {
    name: string;
    dosage: string;
    frequency: string;
    duration: string;
  }[];
  requiresSupervisorVerification?: boolean;
  supervisorVerified?: boolean;
  verifiedBy?: string;
  verifiedAt?: string;
}

export interface DiseaseRiskItem {
  diseaseName: string;
  riskPercentage: number;
  riskTier: string;
  rationale: string;
}

export interface ClinicalAssessmentResult {
  riskScore: number;
  riskLevel: RiskLevel;
  primarySummary: string;
  possibleConditions: string[];
  medicineSuggestions: string[];
  recommendedActions: string[];
  redFlags: string[];
  diseaseRisksWithPercentage: DiseaseRiskItem[];
}

export interface RxMedicationItem {
  name: string;
  dosage: string;
  frequency: string;
  duration: string;
}

export interface RxAnalysisResult {
  summary: string;
  medications: RxMedicationItem[];
  warnings: string[];
  isOffline: boolean;
}

export interface WardOutbreakInsight {
  alertLevel: RiskLevel;
  headline: string;
  summary: string;
  affectedWards: string[];
  recommendations: string[];
}
