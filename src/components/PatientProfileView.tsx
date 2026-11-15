import React, { useState } from 'react';
import { 
  ArrowLeft, 
  Bell, 
  Volume2, 
  Phone, 
  Flag, 
  Activity, 
  QrCode, 
  FileText, 
  Edit3, 
  Trash2, 
  Plus, 
  CheckCircle2,
  TrendingUp,
  AlertTriangle
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
  const { toggleCriticalStatus, deletePatient } = useApp();
  const [activeTab, setActiveTab] = useState<'vitals' | 'timeline' | 'meds'>('vitals');
  const [trendMetric, setTrendMetric] = useState<'bp' | 'spo2' | 'hr'>('bp');
  const [showLogSuccess, setShowLogSuccess] = useState(false);

  const getInitials = (name: string) => {
    return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
  };

  const handleSpeech = () => {
    const speechText = `Patient ${patient.fullName}, age ${patient.age}, blood group ${patient.bloodGroup}. Critical status is active with high blood pressure alert.`;
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(speechText);
      utterance.rate = 0.95;
      window.speechSynthesis.speak(utterance);
    }
  };

  return (
    <div className="space-y-4">
      {/* Top Header Bar */}
      <div className="flex items-center justify-between py-1 border-b border-slate-800/60 pb-2">
        <button onClick={onBack} className="p-1.5 text-slate-300 hover:text-white cursor-pointer">
          <ArrowLeft className="w-5 h-5" />
        </button>

        <h2 className="font-bold text-base text-white tracking-tight">Patient Profile</h2>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-full bg-[#1b382b] text-[#22c55e] border border-[#22c55e]/30 text-[10px] font-semibold flex items-center gap-1">
            <span className="text-[9px]">☁️</span>
            <span>Sync</span>
          </div>
          <button className="p-1.5 text-slate-300 hover:text-white cursor-pointer">
            <Bell className="w-4 h-4 text-slate-300" />
          </button>
        </div>
      </div>

      {/* Patient Hero Card */}
      <div className="p-4 rounded-3xl bg-[#131926] border border-slate-800 space-y-3.5">
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-3.5">
            {/* Avatar Circle */}
            <div className="w-14 h-14 rounded-full bg-[#3b1722] text-[#fb7185] border border-red-500/30 flex items-center justify-center font-bold text-base shadow-inner">
              {getInitials(patient.fullName || 'Harish Verma')}
            </div>

            <div>
              <h3 className="text-base font-bold text-white tracking-tight">{patient.fullName || 'Harish Verma'}</h3>
              <p className="text-[11px] text-slate-400">
                PT-1003 · {patient.age || 62}y {patient.gender || 'Male'} · Blood {patient.bloodGroup || 'A+'}
              </p>
              <p className="text-[11px] text-slate-400">
                {patient.villageOrWard || 'Ward 7 · Old Mill Colony'}
              </p>
            </div>
          </div>

          {/* Audio voice reader button */}
          <button 
            onClick={handleSpeech}
            className="p-2 rounded-xl bg-purple-950/40 text-purple-400 hover:bg-purple-900/50 cursor-pointer"
            title="Read out loud"
          >
            <Volume2 className="w-5 h-5" />
          </button>
        </div>

        {/* Status badges */}
        <div className="flex items-center gap-2">
          <div className="px-2.5 py-1 rounded-full bg-[#291b15] text-[#fb923c] border border-amber-500/30 text-[10px] font-semibold flex items-center gap-1">
            <span>☁️</span>
            <span>Pending Sync</span>
          </div>

          {(patient.isCritical || true) && (
            <div className="px-2.5 py-1 rounded-full bg-[#3b151e] text-[#f43f5e] border border-red-500/30 text-[10px] font-semibold flex items-center gap-1">
              <span>⚠️</span>
              <span>Critical</span>
            </div>
          )}
        </div>

        {/* Action Buttons Matrix */}
        <div className="space-y-2 pt-1">
          {/* Row 1 */}
          <div className="grid grid-cols-3 gap-2">
            <button className="py-2.5 px-3 rounded-xl bg-[#101421] border border-slate-800 text-slate-200 text-xs font-semibold flex items-center justify-center gap-1.5 hover:bg-slate-800 cursor-pointer">
              <Phone className="w-3.5 h-3.5 text-blue-400" />
              <span>Call</span>
            </button>

            <button 
              onClick={() => toggleCriticalStatus(patient.id)}
              className="py-2.5 px-3 rounded-xl bg-[#101421] border border-slate-800 text-slate-200 text-xs font-semibold flex items-center justify-center gap-1.5 hover:bg-slate-800 cursor-pointer"
            >
              <Flag className="w-3.5 h-3.5 text-red-400 fill-red-400" />
              <span>Unflag</span>
            </button>

            <button 
              onClick={onOpenRiskAssessment}
              className="py-2.5 px-3 rounded-xl bg-[#8b5cf6] hover:bg-[#7c3aed] text-white text-xs font-semibold flex items-center justify-center gap-1.5 shadow-md shadow-purple-600/30 cursor-pointer"
            >
              <Activity className="w-3.5 h-3.5" />
              <span>Risk AI</span>
            </button>
          </div>

          {/* Row 2 */}
          <div className="grid grid-cols-4 gap-2">
            <button className="py-2 px-2 rounded-xl bg-[#163a29] text-[#4ade80] border border-[#22c55e]/30 text-[11px] font-semibold flex items-center justify-center gap-1 hover:bg-[#1a4530] cursor-pointer">
              <QrCode className="w-3.5 h-3.5" />
              <span>QR Card</span>
            </button>

            <button className="py-2 px-2 rounded-xl bg-[#163a29] text-[#4ade80] border border-[#22c55e]/30 text-[11px] font-semibold flex items-center justify-center gap-1 hover:bg-[#1a4530] cursor-pointer">
              <FileText className="w-3.5 h-3.5" />
              <span>Referral PDF</span>
            </button>

            <button className="py-2 rounded-xl bg-[#101421] border border-slate-800 text-purple-400 hover:text-white flex items-center justify-center cursor-pointer">
              <Edit3 className="w-3.5 h-3.5" />
            </button>

            <button 
              onClick={() => {
                deletePatient(patient.id);
                onBack();
              }}
              className="py-2 rounded-xl bg-[#101421] border border-slate-800 text-red-400 hover:bg-red-500/20 flex items-center justify-center cursor-pointer"
            >
              <Trash2 className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>

      {/* Tabs Row */}
      <div className="border-b border-slate-800 flex text-xs font-semibold">
        <button
          onClick={() => setActiveTab('vitals')}
          className={`pb-2.5 px-3 cursor-pointer transition-colors relative ${
            activeTab === 'vitals'
              ? 'text-purple-400 font-bold'
              : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          Vitals & Overview
          {activeTab === 'vitals' && (
            <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-purple-500 rounded-full"></div>
          )}
        </button>

        <button
          onClick={() => setActiveTab('timeline')}
          className={`pb-2.5 px-3 cursor-pointer transition-colors relative ${
            activeTab === 'timeline'
              ? 'text-purple-400 font-bold'
              : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          Timeline & History
          {activeTab === 'timeline' && (
            <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-purple-500 rounded-full"></div>
          )}
        </button>

        <button
          onClick={() => setActiveTab('meds')}
          className={`pb-2.5 px-3 cursor-pointer transition-colors relative ${
            activeTab === 'meds'
              ? 'text-purple-400 font-bold'
              : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          Meds & Vacc...
          {activeTab === 'meds' && (
            <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-purple-500 rounded-full"></div>
          )}
        </button>
      </div>

      {/* Vitals Section */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <h3 className="text-xs font-bold text-white">Latest Vitals</h3>
          <button
            onClick={() => {
              setShowLogSuccess(true);
              setTimeout(() => setShowLogSuccess(false), 2000);
            }}
            className="px-3 py-1 rounded-full bg-[#8b5cf6] hover:bg-[#7c3aed] text-white text-[11px] font-semibold flex items-center gap-1 cursor-pointer shadow-xs"
          >
            <Plus className="w-3 h-3" />
            <span>Log Vitals</span>
          </button>
        </div>

        {showLogSuccess && (
          <div className="p-2.5 rounded-xl bg-emerald-950/60 border border-emerald-500/40 text-[11px] text-emerald-300 flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400" />
            <span>New vitals recorded to local vault successfully!</span>
          </div>
        )}

        {/* Vitals Trend Card */}
        <div className="p-4 rounded-3xl bg-[#131926] border border-slate-800 space-y-3">
          <h4 className="text-xs font-bold text-white flex items-center gap-1.5">
            <TrendingUp className="w-3.5 h-3.5 text-purple-400" />
            <span>Vitals Trend (2 readings)</span>
          </h4>

          {/* Subtabs */}
          <div className="flex gap-2">
            <button
              onClick={() => setTrendMetric('bp')}
              className={`px-3 py-1 rounded-xl text-[11px] font-semibold cursor-pointer ${
                trendMetric === 'bp'
                  ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                  : 'bg-[#101421] text-slate-400 border border-slate-800'
              }`}
            >
              BP (Sys/Dia)
            </button>
            <button
              onClick={() => setTrendMetric('spo2')}
              className={`px-3 py-1 rounded-xl text-[11px] font-semibold cursor-pointer ${
                trendMetric === 'spo2'
                  ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                  : 'bg-[#101421] text-slate-400 border border-slate-800'
              }`}
            >
              SpO2 %
            </button>
            <button
              onClick={() => setTrendMetric('hr')}
              className={`px-3 py-1 rounded-xl text-[11px] font-semibold cursor-pointer ${
                trendMetric === 'hr'
                  ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                  : 'bg-[#101421] text-slate-400 border border-slate-800'
              }`}
            >
              Heart Rate
            </button>
          </div>

          {/* Chart Display */}
          <div className="h-32 w-full bg-[#0d1017] rounded-2xl p-3 border border-slate-800/80 flex flex-col justify-between relative overflow-hidden">
            {/* Top Line (Systolic 164) */}
            <div className="relative w-full flex items-center">
              <div className="w-2.5 h-2.5 rounded-full bg-purple-400 ring-2 ring-purple-600/50"></div>
              <div className="flex-1 h-0.5 bg-gradient-to-r from-purple-400 via-purple-500 to-purple-400"></div>
              <div className="w-2.5 h-2.5 rounded-full bg-purple-400 ring-2 ring-purple-600/50"></div>
            </div>

            {/* Red Alert Line 140 */}
            <div className="w-full flex items-center border-b border-dashed border-red-500/60 my-auto"></div>

            {/* Bottom Line (Diastolic 98) */}
            <div className="relative w-full flex items-center">
              <div className="w-2.5 h-2.5 rounded-full bg-purple-400 ring-2 ring-purple-600/50"></div>
              <div className="flex-1 h-0.5 bg-gradient-to-r from-purple-400 via-purple-500 to-purple-400"></div>
              <div className="w-2.5 h-2.5 rounded-full bg-purple-400 ring-2 ring-purple-600/50"></div>
            </div>

            {/* Chart Legend */}
            <div className="flex items-center justify-between text-[10px] text-slate-400 pt-1">
              <div className="flex items-center gap-3">
                <span className="flex items-center gap-1 text-purple-300">
                  <span className="w-2 h-2 rounded-full bg-purple-400"></span>
                  Systolic
                </span>
                <span className="flex items-center gap-1 text-purple-300">
                  <span className="w-2 h-2 rounded-full bg-purple-400"></span>
                  Diastolic
                </span>
              </div>
              <span className="text-[10px] text-red-400 font-medium">Alert line: 140 mmHg High Sys</span>
            </div>
          </div>
        </div>

        {/* Snapshot Vitals Box */}
        <div className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800 space-y-2.5">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-300">Today, 10:05 AM</span>
            <div className="px-2 py-0.5 rounded-full bg-[#291b15] text-[#fb923c] border border-amber-500/30 text-[9px] font-semibold flex items-center gap-1">
              <span>☁️</span>
              <span>Pending Sync</span>
            </div>
          </div>

          <div className="grid grid-cols-5 gap-2 text-center pt-1 border-t border-slate-800/80">
            <div>
              <span className="text-sm font-bold text-white block">164/98</span>
              <span className="text-[9px] text-slate-400 uppercase">BP</span>
            </div>
            <div>
              <span className="text-sm font-bold text-white block">90%</span>
              <span className="text-[9px] text-slate-400 uppercase">SpO2</span>
            </div>
            <div>
              <span className="text-sm font-bold text-white block">112 bpm</span>
              <span className="text-[9px] text-slate-400 uppercase">HR</span>
            </div>
            <div>
              <span className="text-sm font-bold text-white block">37.3°C</span>
              <span className="text-[9px] text-slate-400 uppercase">Temp</span>
            </div>
            <div>
              <span className="text-sm font-bold text-white block">214</span>
              <span className="text-[9px] text-slate-400 uppercase">Sugar</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
