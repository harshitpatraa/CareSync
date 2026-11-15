import React, { useState } from 'react';
import { 
  ArrowLeft, 
  Bell, 
  Volume2, 
  Phone, 
  PhoneOff,
  Mic,
  Flag, 
  Activity, 
  QrCode, 
  FileText, 
  Edit3, 
  Trash2, 
  Plus, 
  CheckCircle2, 
  TrendingUp, 
  AlertTriangle,
  X,
  Download,
  Share2,
  Printer,
  Calendar,
  Pill,
  ShieldCheck,
  Building2,
  UserCheck
} from 'lucide-react';
import { Patient } from '../types';
import { useApp } from '../context/AppContext';

interface PatientProfileViewProps {
  patient: Patient;
  onBack: () => void;
  onOpenSos: (patient: Patient) => void;
  onOpenRiskAssessment: () => void;
}

export const PatientProfileView: React.FC<PatientProfileViewProps> = ({
  patient,
  onBack,
  onOpenSos,
  onOpenRiskAssessment
}) => {
  const { toggleCriticalStatus, deletePatient, updatePatient } = useApp();
  
  // Tabs
  const [activeTab, setActiveTab] = useState<'vitals' | 'timeline' | 'meds'>('vitals');
  const [trendMetric, setTrendMetric] = useState<'bp' | 'spo2' | 'hr'>('bp');

  // Modals
  const [showCallModal, setShowCallModal] = useState(false);
  const [showQrModal, setShowQrModal] = useState(false);
  const [showReferralModal, setShowReferralModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showLogVitalsModal, setShowLogVitalsModal] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Edit form state
  const [editName, setEditName] = useState(patient.fullName);
  const [editAge, setEditAge] = useState(patient.age);
  const [editPhone, setEditPhone] = useState(patient.phone);
  const [editWard, setEditWard] = useState(patient.villageOrWard);

  // Vitals form state
  const [sysInput, setSysInput] = useState(patient.vitals?.systolicBp || 164);
  const [diaInput, setDiaInput] = useState(patient.vitals?.diastolicBp || 98);
  const [spo2Input, setSpo2Input] = useState(patient.vitals?.spo2Percent || 90);
  const [hrInput, setHrInput] = useState(patient.vitals?.heartRate || 112);
  const [sugarInput, setSugarInput] = useState(patient.vitals?.bloodSugarMgDl || 214);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const getInitials = (name: string) => {
    return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
  };

  const handleSpeech = () => {
    const speechText = `Patient ${patient.fullName}, age ${patient.age}, blood group ${patient.bloodGroup}. Ward: ${patient.villageOrWard}. Systolic BP is ${sysInput}, diastolic ${diaInput}, SpO2 ${spo2Input} percent.`;
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(speechText);
      utterance.rate = 0.95;
      window.speechSynthesis.speak(utterance);
    }
    showToast("Voice reading clinical snapshot...");
  };

  const handleSaveEdit = (e: React.FormEvent) => {
    e.preventDefault();
    updatePatient({
      ...patient,
      fullName: editName,
      age: Number(editAge),
      phone: editPhone,
      villageOrWard: editWard
    });
    setShowEditModal(false);
    showToast("Patient record updated in Room vault!");
  };

  const handleSaveVitals = (e: React.FormEvent) => {
    e.preventDefault();
    updatePatient({
      ...patient,
      vitals: {
        id: `vt-${Date.now()}`,
        patientId: patient.id,
        systolicBp: Number(sysInput),
        diastolicBp: Number(diaInput),
        heartRate: Number(hrInput),
        spo2Percent: Number(spo2Input),
        temperatureC: 37.3,
        bloodSugarMgDl: Number(sugarInput),
        recordedAt: Date.now()
      }
    });
    setShowLogVitalsModal(false);
    showToast("New clinical vitals saved & trend updated!");
  };

  return (
    <div className="space-y-3.5 pb-4">
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed top-12 left-1/2 -translate-x-1/2 z-50 px-4 py-2 rounded-2xl bg-indigo-950 border border-indigo-500 text-indigo-200 text-xs font-semibold shadow-2xl flex items-center gap-2 animate-in fade-in-50">
          <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Top Header Bar */}
      <div className="flex items-center justify-between py-1 border-b border-slate-800/80 pb-2">
        <button onClick={onBack} className="p-1.5 text-slate-300 hover:text-white cursor-pointer">
          <ArrowLeft className="w-5 h-5" />
        </button>

        <h2 className="font-bold text-sm text-white tracking-tight">Patient Profile</h2>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-full bg-emerald-950/60 text-emerald-400 border border-emerald-500/30 text-[10px] font-semibold flex items-center gap-1">
            <span>☁️</span>
            <span>Room DB</span>
          </div>
          <button className="p-1.5 text-slate-300 hover:text-white cursor-pointer">
            <Bell className="w-4 h-4 text-slate-300" />
          </button>
        </div>
      </div>

      {/* Patient Hero Card (2-color theme: Deep Indigo & Emerald, Red only for critical) */}
      <div className="p-4 rounded-3xl bg-[#0f172a] border border-slate-800 space-y-3.5">
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-3.5">
            {/* Avatar Circle */}
            <div className={`w-13 h-13 rounded-2xl flex items-center justify-center font-bold text-sm shadow-inner ${
              patient.isCritical
                ? 'bg-red-950/50 text-red-400 border border-red-500/40'
                : 'bg-indigo-950/50 text-indigo-300 border border-indigo-500/40'
            }`}>
              {getInitials(patient.fullName)}
            </div>

            <div>
              <h3 className="text-sm font-bold text-white tracking-tight">{patient.fullName}</h3>
              <p className="text-[11px] text-slate-400 mt-0.5">
                {patient.id} · {patient.age}y {patient.gender} · Blood {patient.bloodGroup}
              </p>
              <p className="text-[11px] text-slate-400">
                {patient.villageOrWard}
              </p>
            </div>
          </div>

          <button 
            onClick={handleSpeech}
            className="p-2 rounded-xl bg-indigo-950/60 text-indigo-300 hover:bg-indigo-900/80 cursor-pointer border border-indigo-500/30"
            title="Read clinical snapshot aloud"
          >
            <Volume2 className="w-4 h-4" />
          </button>
        </div>

        {/* Status badges */}
        <div className="flex items-center gap-2">
          <div className="px-2.5 py-1 rounded-full bg-emerald-950/40 text-emerald-400 border border-emerald-500/30 text-[10px] font-semibold flex items-center gap-1">
            <span>☁️</span>
            <span>Local Vault Ready</span>
          </div>

          {patient.isCritical && (
            <div className="px-2.5 py-1 rounded-full bg-red-950/50 text-red-400 border border-red-500/40 text-[10px] font-semibold flex items-center gap-1 animate-pulse">
              <span>⚠️</span>
              <span>Critical Priority</span>
            </div>
          )}
        </div>

        {/* Interactive Action Buttons Grid (All functional & clickable) */}
        <div className="space-y-2 pt-1">
          {/* Row 1: Call, Flag/Unflag, Risk AI */}
          <div className="grid grid-cols-3 gap-2">
            <button 
              onClick={() => setShowCallModal(true)}
              className="py-2.5 px-3 rounded-xl bg-[#0b101c] border border-slate-800 text-slate-200 text-xs font-semibold flex items-center justify-center gap-1.5 hover:border-indigo-500/50 hover:text-white cursor-pointer transition-colors"
            >
              <Phone className="w-3.5 h-3.5 text-indigo-400" />
              <span>Call</span>
            </button>

            <button 
              onClick={() => {
                toggleCriticalStatus(patient.id);
                showToast(patient.isCritical ? "Patient unflagged from critical list" : "Patient flagged as Critical Priority!");
              }}
              className="py-2.5 px-3 rounded-xl bg-[#0b101c] border border-slate-800 text-slate-200 text-xs font-semibold flex items-center justify-center gap-1.5 hover:border-indigo-500/50 hover:text-white cursor-pointer transition-colors"
            >
              <Flag className={`w-3.5 h-3.5 ${patient.isCritical ? 'text-red-400 fill-red-400' : 'text-slate-400'}`} />
              <span>{patient.isCritical ? 'Unflag' : 'Flag'}</span>
            </button>

            <button 
              onClick={onOpenRiskAssessment}
              className="py-2.5 px-3 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold flex items-center justify-center gap-1.5 shadow-md shadow-indigo-600/30 cursor-pointer transition-all"
            >
              <Activity className="w-3.5 h-3.5" />
              <span>Risk AI</span>
            </button>
          </div>

          {/* Row 2: QR Card, Referral PDF, Edit, Delete */}
          <div className="grid grid-cols-4 gap-2">
            <button 
              onClick={() => setShowQrModal(true)}
              className="py-2 px-2 rounded-xl bg-emerald-950/40 text-emerald-400 border border-emerald-500/30 text-[11px] font-semibold flex items-center justify-center gap-1 hover:bg-emerald-900/50 cursor-pointer transition-colors"
            >
              <QrCode className="w-3.5 h-3.5" />
              <span>QR Card</span>
            </button>

            <button 
              onClick={() => setShowReferralModal(true)}
              className="py-2 px-2 rounded-xl bg-emerald-950/40 text-emerald-400 border border-emerald-500/30 text-[11px] font-semibold flex items-center justify-center gap-1 hover:bg-emerald-900/50 cursor-pointer transition-colors"
            >
              <FileText className="w-3.5 h-3.5" />
              <span>Referral</span>
            </button>

            <button 
              onClick={() => setShowEditModal(true)}
              className="py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-indigo-400 hover:text-white flex items-center justify-center cursor-pointer hover:border-indigo-500/50 transition-colors"
              title="Edit Demographics"
            >
              <Edit3 className="w-3.5 h-3.5" />
            </button>

            <button 
              onClick={() => setShowDeleteConfirm(true)}
              className="py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-red-400 hover:bg-red-500/20 flex items-center justify-center cursor-pointer transition-colors"
              title="Archive Patient"
            >
              <Trash2 className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>

      {/* Tabs Row (Unified 2-color theme: Indigo indicator) */}
      <div className="border-b border-slate-800 flex text-xs font-semibold">
        {[
          { id: 'vitals', label: 'Vitals & Overview' },
          { id: 'timeline', label: 'Timeline & History' },
          { id: 'meds', label: 'Meds & Vaccines' },
        ].map((t) => (
          <button
            key={t.id}
            onClick={() => setActiveTab(t.id as any)}
            className={`pb-2.5 px-3 cursor-pointer transition-colors relative ${
              activeTab === t.id
                ? 'text-indigo-400 font-bold'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            {t.label}
            {activeTab === t.id && (
              <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-indigo-500 rounded-full"></div>
            )}
          </button>
        ))}
      </div>

      {/* ────────────────────────────────────────────────────────── */}
      {/* 1. Tab: Vitals & Overview */}
      {/* ────────────────────────────────────────────────────────── */}
      {activeTab === 'vitals' && (
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-bold text-white">Latest Vitals</h3>
            <button
              onClick={() => setShowLogVitalsModal(true)}
              className="px-3 py-1 rounded-full bg-indigo-600 hover:bg-indigo-500 text-white text-[11px] font-semibold flex items-center gap-1 cursor-pointer shadow-xs"
            >
              <Plus className="w-3 h-3" />
              <span>Log Vitals</span>
            </button>
          </div>

          {/* Vitals Trend Card */}
          <div className="p-4 rounded-3xl bg-[#0f172a] border border-slate-800 space-y-3">
            <div className="flex items-center justify-between">
              <h4 className="text-xs font-bold text-white flex items-center gap-1.5">
                <TrendingUp className="w-3.5 h-3.5 text-indigo-400" />
                <span>Vitals Trend (2 readings)</span>
              </h4>
              <span className="text-[10px] text-slate-400 font-mono">Today 10:05 AM</span>
            </div>

            {/* Metric Switcher */}
            <div className="flex gap-2">
              {[
                { id: 'bp', label: 'BP (Sys/Dia)' },
                { id: 'spo2', label: 'SpO2 %' },
                { id: 'hr', label: 'Heart Rate' }
              ].map((m) => (
                <button
                  key={m.id}
                  onClick={() => setTrendMetric(m.id as any)}
                  className={`px-3 py-1 rounded-xl text-[11px] font-semibold cursor-pointer ${
                    trendMetric === m.id
                      ? 'bg-indigo-600 text-white shadow-xs'
                      : 'bg-[#0b101c] text-slate-400 border border-slate-800'
                  }`}
                >
                  {m.label}
                </button>
              ))}
            </div>

            {/* Clinical Trend Graphic */}
            <div className="h-32 w-full bg-[#0b101c] rounded-2xl p-3 border border-slate-800/80 flex flex-col justify-between relative overflow-hidden">
              {/* Top Systolic Line */}
              <div className="relative w-full flex items-center">
                <div className="w-2.5 h-2.5 rounded-full bg-indigo-400 ring-2 ring-indigo-600/50"></div>
                <div className="flex-1 h-0.5 bg-gradient-to-r from-indigo-400 via-indigo-500 to-indigo-400"></div>
                <div className="w-2.5 h-2.5 rounded-full bg-indigo-400 ring-2 ring-indigo-600/50"></div>
              </div>

              {/* Red Dotted Alert Threshold (140 mmHg) */}
              <div className="w-full flex items-center border-b border-dashed border-red-500/60 my-auto"></div>

              {/* Bottom Diastolic Line */}
              <div className="relative w-full flex items-center">
                <div className="w-2.5 h-2.5 rounded-full bg-emerald-400 ring-2 ring-emerald-600/50"></div>
                <div className="flex-1 h-0.5 bg-gradient-to-r from-emerald-400 via-emerald-500 to-emerald-400"></div>
                <div className="w-2.5 h-2.5 rounded-full bg-emerald-400 ring-2 ring-emerald-600/50"></div>
              </div>

              {/* Trend Legend */}
              <div className="flex items-center justify-between text-[10px] text-slate-400 pt-1">
                <div className="flex items-center gap-3">
                  <span className="flex items-center gap-1 text-indigo-300">
                    <span className="w-2 h-2 rounded-full bg-indigo-400"></span>
                    Systolic ({sysInput})
                  </span>
                  <span className="flex items-center gap-1 text-emerald-300">
                    <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
                    Diastolic ({diaInput})
                  </span>
                </div>
                <span className="text-[10px] text-red-400 font-medium">Alert: &gt; 140 mmHg</span>
              </div>
            </div>
          </div>

          {/* Vitals Snapshot 5-Column Grid */}
          <div className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800 space-y-2.5">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-300">Recorded Today, 10:05 AM</span>
              <div className="px-2 py-0.5 rounded-full bg-emerald-950/50 text-emerald-400 border border-emerald-500/30 text-[9px] font-semibold flex items-center gap-1">
                <span>✓</span>
                <span>Verified</span>
              </div>
            </div>

            <div className="grid grid-cols-5 gap-2 text-center pt-1 border-t border-slate-800/80">
              <div>
                <span className="text-xs font-bold text-white block">{sysInput}/{diaInput}</span>
                <span className="text-[9px] text-slate-400 uppercase">BP</span>
              </div>
              <div>
                <span className={`text-xs font-bold block ${spo2Input < 92 ? 'text-red-400 font-black' : 'text-white'}`}>
                  {spo2Input}%
                </span>
                <span className="text-[9px] text-slate-400 uppercase">SpO2</span>
              </div>
              <div>
                <span className="text-xs font-bold text-white block">{hrInput} bpm</span>
                <span className="text-[9px] text-slate-400 uppercase">HR</span>
              </div>
              <div>
                <span className="text-xs font-bold text-white block">37.3°C</span>
                <span className="text-[9px] text-slate-400 uppercase">Temp</span>
              </div>
              <div>
                <span className="text-xs font-bold text-white block">{sugarInput}</span>
                <span className="text-[9px] text-slate-400 uppercase">Sugar</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* 2. Tab: Timeline & History */}
      {/* ────────────────────────────────────────────────────────── */}
      {activeTab === 'timeline' && (
        <div className="space-y-3">
          <div className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800 space-y-3">
            <h4 className="text-xs font-bold text-white flex items-center gap-1.5">
              <Calendar className="w-3.5 h-3.5 text-indigo-400" />
              <span>Clinical Visit History</span>
            </h4>

            <div className="space-y-3 relative pl-4 border-l border-indigo-500/30 ml-2">
              <div className="relative space-y-1">
                <div className="absolute -left-[21px] top-1 w-2.5 h-2.5 rounded-full bg-red-500"></div>
                <div className="flex justify-between items-center text-[10px]">
                  <span className="font-bold text-white">Emergency Triage Visit</span>
                  <span className="text-slate-400">Today, 10:05 AM</span>
                </div>
                <p className="text-[11px] text-slate-300">
                  Reported chest heaviness & acute breathlessness. SpO2 recorded at 90%. Flagged for 108 PHC referral.
                </p>
              </div>

              <div className="relative space-y-1 pt-2">
                <div className="absolute -left-[21px] top-3 w-2.5 h-2.5 rounded-full bg-indigo-400"></div>
                <div className="flex justify-between items-center text-[10px]">
                  <span className="font-bold text-white">Routine Hypertension Review</span>
                  <span className="text-slate-400">12 Nov 2026</span>
                </div>
                <p className="text-[11px] text-slate-300">
                  BP checked at 152/92 mmHg. Amlodipine 5mg refill issued by ASHA worker. Salt reduction advised.
                </p>
              </div>

              <div className="relative space-y-1 pt-2">
                <div className="absolute -left-[21px] top-3 w-2.5 h-2.5 rounded-full bg-emerald-400"></div>
                <div className="flex justify-between items-center text-[10px]">
                  <span className="font-bold text-white">Quarterly NCD Screening</span>
                  <span className="text-slate-400">28 Oct 2026</span>
                </div>
                <p className="text-[11px] text-slate-300">
                  Random blood glucose 214 mg/dL. Foot examination completed: no diabetic neuropathy sores detected.
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* 3. Tab: Meds & Vaccines */}
      {/* ────────────────────────────────────────────────────────── */}
      {activeTab === 'meds' && (
        <div className="space-y-3">
          <div className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800 space-y-2.5">
            <h4 className="text-xs font-bold text-white flex items-center gap-1.5">
              <Pill className="w-3.5 h-3.5 text-indigo-400" />
              <span>Active Prescribed Medications</span>
            </h4>

            <div className="space-y-2">
              {[
                { name: 'Amlodipine Besylate', dose: '5 mg', timing: 'Once daily morning after food', duration: '30 Days Supply' },
                { name: 'Metformin Hydrochloride', dose: '500 mg', timing: 'Twice daily after meals', duration: '30 Days Supply' },
                { name: 'Paracetamol', dose: '500 mg', timing: 'SOS for fever / headache', duration: 'As Needed' }
              ].map((m, idx) => (
                <div key={idx} className="p-2.5 rounded-xl bg-[#0b101c] border border-slate-800 flex justify-between items-center">
                  <div>
                    <h5 className="text-xs font-bold text-white">{m.name} ({m.dose})</h5>
                    <p className="text-[10px] text-slate-400 mt-0.5">{m.timing}</p>
                  </div>
                  <span className="px-2 py-0.5 rounded-md bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 text-[10px] font-semibold">
                    {m.duration}
                  </span>
                </div>
              ))}
            </div>
          </div>

          <div className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800 space-y-2.5">
            <h4 className="text-xs font-bold text-white flex items-center gap-1.5">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
              <span>Immunization & Vaccine Register</span>
            </h4>

            <div className="space-y-1.5">
              <div className="p-2 rounded-xl bg-[#0b101c] border border-slate-800 flex justify-between items-center text-xs">
                <span className="text-slate-200">COVID-19 Precautionary Dose</span>
                <span className="text-emerald-400 font-semibold flex items-center gap-1">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                  Administered
                </span>
              </div>
              <div className="p-2 rounded-xl bg-[#0b101c] border border-slate-800 flex justify-between items-center text-xs">
                <span className="text-slate-200">Tetanus Toxoid (TT Booster)</span>
                <span className="text-emerald-400 font-semibold flex items-center gap-1">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                  Valid (2025)
                </span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* MODAL 1: Interactive Call Simulation */}
      {/* ────────────────────────────────────────────────────────── */}
      {showCallModal && (
        <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="w-full max-w-sm rounded-3xl bg-[#0f172a] border border-slate-800 p-5 space-y-4 text-center">
            <div className="w-16 h-16 rounded-full bg-indigo-600/20 text-indigo-400 border border-indigo-500/40 flex items-center justify-center mx-auto animate-pulse">
              <Phone className="w-7 h-7" />
            </div>

            <div>
              <h3 className="text-sm font-bold text-white">Calling {patient.fullName}</h3>
              <p className="text-xs text-indigo-300 font-mono mt-0.5">{patient.phone}</p>
              <p className="text-[10px] text-slate-400 mt-1">CareSync Field VoIP Bridge · Encrypted Channel</p>
            </div>

            <div className="py-2">
              <span className="px-3 py-1 rounded-full bg-emerald-950/60 text-emerald-400 border border-emerald-500/30 text-xs font-mono font-bold">
                00:14 · Connected
              </span>
            </div>

            <div className="grid grid-cols-2 gap-3 pt-2">
              <button
                onClick={() => {
                  setShowCallModal(false);
                  showToast("Call ended. Summary logged to Field Ledger.");
                }}
                className="py-2.5 rounded-xl bg-red-600 hover:bg-red-500 text-white font-semibold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <PhoneOff className="w-4 h-4" />
                <span>End Call</span>
              </button>

              <button
                onClick={() => {
                  setShowCallModal(false);
                  showToast("Follow-up note logged for call!");
                }}
                className="py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <FileText className="w-4 h-4" />
                <span>Log Note</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* MODAL 2: ABHA / ABDM QR Health Card */}
      {/* ────────────────────────────────────────────────────────── */}
      {showQrModal && (
        <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="w-full max-w-sm rounded-3xl bg-[#0f172a] border border-slate-800 p-5 space-y-4">
            <div className="flex justify-between items-center border-b border-slate-800 pb-2">
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-white">Ayushman Bharat Health Card (ABHA)</span>
              </div>
              <button onClick={() => setShowQrModal(false)} className="text-slate-400 hover:text-white cursor-pointer">
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* QR Card Graphic */}
            <div className="p-4 rounded-2xl bg-gradient-to-br from-indigo-900/60 to-slate-900 border border-indigo-500/40 text-center space-y-3">
              <div className="flex items-center justify-between text-[10px] text-indigo-300 font-semibold border-b border-white/10 pb-1.5">
                <span>National Health Authority (NHA)</span>
                <span>MoHFW · India</span>
              </div>

              {/* QR Code Matrix preview */}
              <div className="w-32 h-32 mx-auto bg-white p-2 rounded-2xl flex items-center justify-center shadow-lg">
                <div className="w-full h-full border-4 border-black grid grid-cols-4 gap-1 p-1 bg-white">
                  <div className="bg-black"></div>
                  <div className="bg-black"></div>
                  <div className="bg-white"></div>
                  <div className="bg-black"></div>
                  <div className="bg-white"></div>
                  <div className="bg-black"></div>
                  <div className="bg-black"></div>
                  <div className="bg-white"></div>
                  <div className="bg-black"></div>
                  <div className="bg-white"></div>
                  <div className="bg-black"></div>
                  <div className="bg-black"></div>
                  <div className="bg-black"></div>
                  <div className="bg-black"></div>
                  <div className="bg-white"></div>
                  <div className="bg-black"></div>
                </div>
              </div>

              <div>
                <h4 className="text-sm font-bold text-white">{patient.fullName}</h4>
                <p className="text-xs font-mono font-bold text-indigo-300 mt-0.5">ABHA: 91-8492-3310-9281</p>
                <p className="text-[10px] text-slate-300">harish.verma@abdm · Blood {patient.bloodGroup}</p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2 pt-1">
              <button 
                onClick={() => {
                  setShowQrModal(false);
                  showToast("QR Card downloaded to device storage!");
                }}
                className="py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <Download className="w-3.5 h-3.5" />
                <span>Download</span>
              </button>

              <button 
                onClick={() => {
                  setShowQrModal(false);
                  showToast("ABHA link shared to patient phone!");
                }}
                className="py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <Share2 className="w-3.5 h-3.5" />
                <span>Share</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* MODAL 3: Official PHC Referral Slip PDF Preview */}
      {/* ────────────────────────────────────────────────────────── */}
      {showReferralModal && (
        <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="w-full max-w-sm rounded-3xl bg-[#0f172a] border border-slate-800 p-5 space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center border-b border-slate-800 pb-2">
              <span className="text-xs font-bold text-white">Official NHM Referral Slip</span>
              <button onClick={() => setShowReferralModal(false)} className="text-slate-400 hover:text-white cursor-pointer">
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Document preview */}
            <div className="p-3.5 rounded-2xl bg-white text-slate-900 text-[10px] space-y-2.5 shadow-md">
              <div className="text-center border-b border-slate-200 pb-2">
                <h4 className="font-extrabold text-xs uppercase tracking-tight">Government of India · NHM</h4>
                <p className="text-[9px] text-slate-600">Primary Health Centre (PHC) Rampur · Emergency Referral</p>
              </div>

              <div className="grid grid-cols-2 gap-1.5 border-b border-slate-200 pb-2">
                <div><strong>Patient:</strong> {patient.fullName}</div>
                <div><strong>Age/Sex:</strong> {patient.age}y / {patient.gender}</div>
                <div><strong>ID:</strong> {patient.id}</div>
                <div><strong>Blood:</strong> {patient.bloodGroup}</div>
                <div className="col-span-2"><strong>Ward:</strong> {patient.villageOrWard}</div>
              </div>

              <div className="space-y-1 border-b border-slate-200 pb-2">
                <div><strong>Clinical Diagnosis:</strong> Acute Hypoxemic Respiratory Distress & Hypertensive Crisis</div>
                <div><strong>Triage Vitals:</strong> BP {sysInput}/{diaInput} mmHg · SpO2 {spo2Input}% · HR {hrInput} bpm</div>
                <div><strong>Referred To:</strong> District Civil Hospital / Trauma Ward</div>
              </div>

              <div className="flex justify-between items-end pt-1">
                <div>
                  <p className="text-[9px] text-slate-500">Dispatch Code: REF-2026-9041</p>
                  <p className="text-[9px] text-slate-500">Date: Today, 10:05 AM</p>
                </div>
                <div className="text-right">
                  <p className="font-bold text-[9px] text-indigo-800">Dr. Priya Kulkarni</p>
                  <p className="text-[8px] text-slate-500">Medical Officer In-Charge</p>
                </div>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2 pt-1">
              <button 
                onClick={() => {
                  setShowReferralModal(false);
                  showToast("Sending referral slip to district printer...");
                }}
                className="py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <Printer className="w-3.5 h-3.5" />
                <span>Print Slip</span>
              </button>

              <button 
                onClick={() => {
                  setShowReferralModal(false);
                  showToast("PDF referral slip saved to downloads!");
                }}
                className="py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <Download className="w-3.5 h-3.5" />
                <span>Export PDF</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* MODAL 4: Edit Patient Demographics Form */}
      {/* ────────────────────────────────────────────────────────── */}
      {showEditModal && (
        <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-4">
          <form onSubmit={handleSaveEdit} className="w-full max-w-sm rounded-3xl bg-[#0f172a] border border-slate-800 p-5 space-y-3.5">
            <div className="flex justify-between items-center border-b border-slate-800 pb-2">
              <span className="text-xs font-bold text-white">Edit Patient Record</span>
              <button type="button" onClick={() => setShowEditModal(false)} className="text-slate-400 hover:text-white cursor-pointer">
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="space-y-2">
              <div>
                <label className="text-[10px] text-slate-400 block mb-0.5">Full Name</label>
                <input
                  type="text"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-xs text-white"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="text-[10px] text-slate-400 block mb-0.5">Age</label>
                  <input
                    type="number"
                    value={editAge}
                    onChange={(e) => setEditAge(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-xs text-white"
                    required
                  />
                </div>
                <div>
                  <label className="text-[10px] text-slate-400 block mb-0.5">Phone Number</label>
                  <input
                    type="text"
                    value={editPhone}
                    onChange={(e) => setEditPhone(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-xs text-white"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="text-[10px] text-slate-400 block mb-0.5">Village / Ward</label>
                <input
                  type="text"
                  value={editWard}
                  onChange={(e) => setEditWard(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-xs text-white"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-xs cursor-pointer shadow-md"
            >
              Save Changes to Room DB
            </button>
          </form>
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* MODAL 5: Log Vitals Form */}
      {/* ────────────────────────────────────────────────────────── */}
      {showLogVitalsModal && (
        <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-4">
          <form onSubmit={handleSaveVitals} className="w-full max-w-sm rounded-3xl bg-[#0f172a] border border-slate-800 p-5 space-y-3.5">
            <div className="flex justify-between items-center border-b border-slate-800 pb-2">
              <span className="text-xs font-bold text-white">Record New Vitals Snapshot</span>
              <button type="button" onClick={() => setShowLogVitalsModal(false)} className="text-slate-400 hover:text-white cursor-pointer">
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="grid grid-cols-2 gap-2.5">
              <div>
                <label className="text-[10px] text-slate-400 block mb-0.5">Systolic BP (mmHg)</label>
                <input
                  type="number"
                  value={sysInput}
                  onChange={(e) => setSysInput(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-xs text-white font-semibold"
                  required
                />
              </div>

              <div>
                <label className="text-[10px] text-slate-400 block mb-0.5">Diastolic BP (mmHg)</label>
                <input
                  type="number"
                  value={diaInput}
                  onChange={(e) => setDiaInput(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-xs text-white font-semibold"
                  required
                />
              </div>

              <div>
                <label className="text-[10px] text-slate-400 block mb-0.5">SpO2 Percentage (%)</label>
                <input
                  type="number"
                  value={spo2Input}
                  onChange={(e) => setSpo2Input(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-xs text-white font-semibold"
                  required
                />
              </div>

              <div>
                <label className="text-[10px] text-slate-400 block mb-0.5">Heart Rate (bpm)</label>
                <input
                  type="number"
                  value={hrInput}
                  onChange={(e) => setHrInput(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-xs text-white font-semibold"
                  required
                />
              </div>

              <div className="col-span-2">
                <label className="text-[10px] text-slate-400 block mb-0.5">Blood Glucose (mg/dL)</label>
                <input
                  type="number"
                  value={sugarInput}
                  onChange={(e) => setSugarInput(Number(e.target.value))}
                  className="w-full px-3 py-2 rounded-xl bg-[#0b101c] border border-slate-800 text-xs text-white font-semibold"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-xs cursor-pointer shadow-md"
            >
              Save Vitals & Update Trend
            </button>
          </form>
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* MODAL 6: Archive Patient Confirmation */}
      {/* ────────────────────────────────────────────────────────── */}
      {showDeleteConfirm && (
        <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="w-full max-w-sm rounded-3xl bg-[#0f172a] border border-slate-800 p-5 space-y-4 text-center">
            <div className="w-12 h-12 rounded-full bg-red-950/60 text-red-400 border border-red-500/40 flex items-center justify-center mx-auto">
              <Trash2 className="w-6 h-6" />
            </div>

            <div>
              <h3 className="text-sm font-bold text-white">Archive Patient Record?</h3>
              <p className="text-xs text-slate-400 mt-1">
                Are you sure you want to remove <strong>{patient.fullName}</strong> from active community roster? All encrypted clinical logs will be archived.
              </p>
            </div>

            <div className="grid grid-cols-2 gap-3 pt-2">
              <button
                onClick={() => setShowDeleteConfirm(false)}
                className="py-2.5 rounded-xl bg-[#0b101c] border border-slate-800 text-slate-300 font-semibold text-xs cursor-pointer hover:bg-slate-800"
              >
                Cancel
              </button>

              <button
                onClick={() => {
                  deletePatient(patient.id);
                  setShowDeleteConfirm(false);
                  onBack();
                }}
                className="py-2.5 rounded-xl bg-red-600 hover:bg-red-500 text-white font-semibold text-xs cursor-pointer shadow-md shadow-red-600/30"
              >
                Confirm Archive
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
