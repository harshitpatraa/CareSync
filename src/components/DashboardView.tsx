import React, { useState } from 'react';
import { 
  Menu, 
  Bell, 
  RotateCw, 
  UserPlus, 
  BriefcaseMedical, 
  Bot, 
  Stethoscope, 
  Calendar, 
  Clock, 
  AlertTriangle, 
  History, 
  Baby, 
  Heart,
  CheckCircle2,
  ChevronRight,
  ShieldCheck,
  Building2,
  Globe2
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { Patient } from '../types';

interface DashboardViewProps {
  onNavigateTab: (tab: string) => void;
  onSelectPatient: (patient: Patient) => void;
  onOpenAddModal: () => void;
  onOpenSosModal: () => void;
  onOpenDrawer: () => void;
  onOpenRecentActivity?: () => void;
}

export const DashboardView: React.FC<DashboardViewProps> = ({
  onNavigateTab,
  onOpenAddModal,
  onOpenSosModal,
  onOpenDrawer,
  onOpenRecentActivity
}) => {
  const { patients, syncAllRecords } = useApp();
  const [isSyncing, setIsSyncing] = useState(false);
  const [syncToast, setSyncToast] = useState<string | null>(null);
  const [activeLang, setActiveLang] = useState<'EN' | 'हिन्दी'>('EN');

  const handleSync = () => {
    setIsSyncing(true);
    setTimeout(() => {
      const res = syncAllRecords();
      setIsSyncing(false);
      setSyncToast(res.message || "ABDM / NIC Cloud Vault Synchronized Successfully!");
      setTimeout(() => setSyncToast(null), 3000);
    }, 700);
  };

  const todayCount = patients.filter(p => p.isTodayVisit).length;
  const pendingCount = patients.filter(p => p.syncStatus === 'PENDING_UPLOAD').length;
  const emergencyCount = patients.filter(p => p.riskLevel === 'CRITICAL' || p.isCritical).length;
  const ancCount = patients.filter(p => p.category === 'PREGNANCY').length;
  const childCount = patients.filter(p => p.category === 'CHILD').length;

  return (
    <div className="space-y-3 pb-4 select-none text-slate-800">
      {/* Toast Notification */}
      {syncToast && (
        <div className="fixed top-12 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-xl bg-white border-2 border-slate-900 text-[#0F172A] text-xs font-black shadow-2xl flex items-center gap-2 animate-in fade-in-50">
          <CheckCircle2 className="w-4 h-4 text-[#15803D] shrink-0" />
          <span>{syncToast}</span>
        </div>
      )}

      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white border-y border-slate-200"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Official Top App Bar - Clean Light Mode */}
      <div className="flex items-center justify-between py-1 border-b border-slate-200 pb-2">
        <div className="flex items-center space-x-2">
          <button 
            onClick={onOpenDrawer}
            className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs"
          >
            <Menu className="w-5 h-5" />
          </button>

          {/* National Health Mission Emblem Badge */}
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-slate-100 border border-slate-300 flex items-center justify-center p-1 text-slate-900 shadow-xs">
              <span className="text-base leading-none">🏛️</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="font-black text-sm text-[#0F172A] tracking-tight">CareSync</span>
                <span className="text-[9px] font-black px-1.5 py-0.2 rounded-xs bg-slate-200 text-slate-900 border border-slate-300 uppercase">
                  NHM Govt
                </span>
              </div>
              <p className="text-[9px] font-bold text-slate-600 leading-none">
                राष्ट्रीय स्वास्थ्य मिशन · MoHFW
              </p>
            </div>
          </div>
        </div>

        {/* Right Actions: Lang Switcher & Official ABDM Badge */}
        <div className="flex items-center space-x-1.5">
          <button
            onClick={() => setActiveLang(activeLang === 'EN' ? 'हिन्दी' : 'EN')}
            className="px-2 py-1 rounded-md bg-white border border-slate-300 text-[10px] font-black text-slate-700 hover:bg-slate-50 cursor-pointer flex items-center gap-1 shadow-xs"
          >
            <Globe2 className="w-2.5 h-2.5 text-slate-900" />
            <span>{activeLang}</span>
          </button>

          <div className="px-2 py-1 rounded-md bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-black flex items-center gap-1 shadow-xs">
            <span className="w-1.5 h-1.5 rounded-full bg-[#15803D] animate-pulse"></span>
            <span>ABDM</span>
          </div>

          <button 
            onClick={() => onNavigateTab('emergency')}
            className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs"
          >
            <Bell className="w-4 h-4 text-slate-700" />
          </button>
        </div>
      </div>

      {/* Official Government Officer Hero Banner (Authentic Ayushman Bharat Card Design) */}
      <div className="p-4 rounded-2xl bg-white border-2 border-slate-900 shadow-sm space-y-3 relative overflow-hidden">
        {/* Top Header Strip */}
        <div className="flex justify-between items-start">
          <div>
            <div className="flex items-center gap-1.5">
              <span className="text-[10px] font-black text-slate-900 tracking-wider uppercase">
                Ayushman Bharat · Health & Wellness Centre
              </span>
              <ShieldCheck className="w-3.5 h-3.5 text-[#15803D]" />
            </div>
            <h2 className="text-lg font-black text-[#0F172A] tracking-tight mt-0.5">Dr. Priya Kulkarni</h2>
            <p className="text-xs font-bold text-slate-700">
              Medical Officer Incharge · CHC / PHC Rampur Sector 4
            </p>
            <p className="text-[10px] font-mono font-bold text-amber-800 mt-0.5">
              Govt Reg. No: NHM-DEL-2026-88102 · ASHA Hub
            </p>
          </div>

          {/* Official ABHA Verified Seal */}
          <div className="w-14 h-12 rounded-xl bg-slate-100 border border-slate-300 p-1 flex flex-col items-center justify-center shadow-xs shrink-0 text-center">
            <span className="text-xs font-black text-slate-900">ABHA</span>
            <span className="text-[8px] font-black text-[#15803D] uppercase tracking-tight">Verified</span>
          </div>
        </div>

        {/* Sync Status strip & Official Button */}
        <div className="pt-2 border-t border-slate-200 flex items-center justify-between">
          <div>
            <div className="text-[11px] font-black text-slate-900 flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-[#15803D]"></span>
              <span>NIC Cloud Database Synchronized</span>
            </div>
            <p className="text-[10px] font-medium text-slate-500">Offline-first local records ready for upload</p>
          </div>

          <button
            onClick={handleSync}
            disabled={isSyncing}
            className="px-3.5 py-1.5 rounded-lg bg-black hover:bg-neutral-800 text-white text-xs font-black shadow-xs cursor-pointer flex items-center gap-1.5 transition-all"
          >
            <RotateCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin' : ''}`} />
            <span>Sync ABDM</span>
          </button>
        </div>
      </div>

      {/* Official Government Quick Actions */}
      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <h3 className="text-xs font-black text-[#0F172A] uppercase tracking-wider flex items-center gap-1.5">
            <Building2 className="w-3.5 h-3.5 text-slate-900" />
            <span>Field Health Directives</span>
          </h3>
          <span className="text-[10px] font-bold text-slate-500">MoHFW Standard 2026</span>
        </div>

        <div className="grid grid-cols-4 gap-2">
          {/* Add Patient / ABHA Registration */}
          <button
            onClick={onOpenAddModal}
            className="p-2.5 rounded-xl bg-white border border-slate-200 hover:border-slate-900 flex flex-col items-center justify-center space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="w-9 h-9 rounded-lg bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center shadow-inner">
              <UserPlus className="w-4 h-4" />
            </div>
            <span className="text-[10px] font-black text-[#0F172A] text-center leading-tight">
              + ABHA Entry
            </span>
          </button>

          {/* NCD & Symptoms Checker */}
          <button
            onClick={() => onNavigateTab('risk-assessment')}
            className="p-2.5 rounded-xl bg-white border border-slate-200 hover:border-slate-900 flex flex-col items-center justify-center space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="w-9 h-9 rounded-lg bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center shadow-inner">
              <BriefcaseMedical className="w-4 h-4" />
            </div>
            <span className="text-[10px] font-black text-[#0F172A] text-center leading-tight">
              NCD Screen
            </span>
          </button>

          {/* AI Clinical Assistant (Sehat Setu) */}
          <button
            onClick={() => onNavigateTab('ai-assistant')}
            className="p-2.5 rounded-xl bg-white border border-slate-200 hover:border-slate-900 flex flex-col items-center justify-center space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="w-9 h-9 rounded-lg bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center shadow-inner">
              <Bot className="w-4 h-4" />
            </div>
            <span className="text-[10px] font-black text-[#0F172A] text-center leading-tight">
              Sehat Setu
            </span>
          </button>

          {/* Clinical Risk Triage */}
          <button
            onClick={() => onNavigateTab('risk-assessment')}
            className="p-2.5 rounded-xl bg-white border border-slate-200 hover:border-slate-900 flex flex-col items-center justify-center space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="w-9 h-9 rounded-lg bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center shadow-inner">
              <Stethoscope className="w-4 h-4" />
            </div>
            <span className="text-[10px] font-black text-[#0F172A] text-center leading-tight">
              Triage AI
            </span>
          </button>
        </div>
      </div>

      {/* Clinical Care Overview Section - National Health Mission Performance */}
      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <h3 className="text-xs font-black text-[#0F172A] uppercase tracking-wider">
            NHM Sector Indicators
          </h3>
          <span className="text-[10px] font-bold text-[#15803D]">Ward 7 Health Sub-Centre</span>
        </div>

        <div className="grid grid-cols-2 gap-2.5">
          {/* Today's Patients */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3 rounded-xl bg-white border border-slate-200 hover:border-slate-300 space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="flex items-center justify-between">
              <div className="w-7 h-7 rounded-lg bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center">
                <Calendar className="w-3.5 h-3.5" />
              </div>
              <span className="text-2xl font-black text-[#0F172A]">{todayCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-black text-[#0F172A] truncate">OPD Field Visits</span>
                <span className="text-[9px] font-black px-1.5 py-0.2 rounded-full bg-emerald-50 text-[#15803D] border border-emerald-200 flex items-center">
                  ↗ 18.2%
                </span>
              </div>
              <p className="text-[10px] font-medium text-slate-500 mt-0.5">Active home rounds today</p>
            </div>
          </div>

          {/* Pending PHC Reviews */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3 rounded-xl bg-white border border-slate-200 hover:border-slate-300 space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="flex items-center justify-between">
              <div className="w-7 h-7 rounded-lg bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center">
                <Clock className="w-3.5 h-3.5" />
              </div>
              <span className="text-2xl font-black text-[#0F172A]">{pendingCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-black text-[#0F172A]">Pending Sync</span>
                <span className="text-[9px] font-black px-1.5 py-0.2 rounded-full bg-slate-100 text-slate-700 border border-slate-300 flex items-center">
                  Offline
                </span>
              </div>
              <p className="text-[10px] font-medium text-slate-500 mt-0.5">Awaiting cloud upload</p>
            </div>
          </div>

          {/* 108 Emergency Referrals (Red alert for national emergency protocol) */}
          <div 
            onClick={onOpenSosModal}
            className="p-3 rounded-xl bg-red-50/70 border border-red-200 hover:border-red-300 space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="flex items-center justify-between">
              <div className="w-7 h-7 rounded-lg bg-red-100 text-red-700 border border-red-300 flex items-center justify-center">
                <AlertTriangle className="w-3.5 h-3.5 text-red-700" />
              </div>
              <span className="text-2xl font-black text-red-900">{emergencyCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-black text-red-950 truncate">108 Emergency</span>
                <span className="text-[9px] font-black px-1.5 py-0.2 rounded-full bg-red-100 text-red-800 border border-red-300 flex items-center">
                  Priority
                </span>
              </div>
              <p className="text-[10px] font-medium text-red-700 mt-0.5">Critical ambulance dispatch</p>
            </div>
          </div>

          {/* Follow Ups */}
          <div 
            onClick={() => onNavigateTab('records')}
            className="p-3 rounded-xl bg-white border border-slate-200 hover:border-slate-300 space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="flex items-center justify-between">
              <div className="w-7 h-7 rounded-lg bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center">
                <History className="w-3.5 h-3.5" />
              </div>
              <span className="text-2xl font-black text-[#0F172A]">4</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-black text-[#0F172A]">NCD Reviews</span>
                <span className="text-[9px] font-black px-1.5 py-0.2 rounded-full bg-emerald-50 text-[#15803D] border border-emerald-200 flex items-center">
                  ↗ 31.1%
                </span>
              </div>
              <p className="text-[10px] font-medium text-slate-500 mt-0.5">BP & Sugar follow-ups</p>
            </div>
          </div>

          {/* Maternal ANC Tracking */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3 rounded-xl bg-white border border-slate-200 hover:border-slate-300 space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="flex items-center justify-between">
              <div className="w-7 h-7 rounded-lg bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center">
                <Baby className="w-3.5 h-3.5" />
              </div>
              <span className="text-2xl font-black text-[#0F172A]">{ancCount}</span>
            </div>
            <div>
              <span className="text-xs font-black text-[#0F172A]">Maternal ANC Care</span>
              <p className="text-[10px] font-medium text-slate-500 mt-0.5">PMSMA high-risk mothers</p>
            </div>
          </div>

          {/* Child Immunization UIP */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3 rounded-xl bg-white border border-slate-200 hover:border-slate-300 space-y-1.5 cursor-pointer transition-colors shadow-xs"
          >
            <div className="flex items-center justify-between">
              <div className="w-7 h-7 rounded-lg bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center">
                <Heart className="w-3.5 h-3.5" />
              </div>
              <span className="text-2xl font-black text-[#0F172A]">{childCount}</span>
            </div>
            <div>
              <span className="text-xs font-black text-[#0F172A]">UIP Immunization</span>
              <p className="text-[10px] font-medium text-slate-500 mt-0.5">Mission Indradhanush</p>
            </div>
          </div>
        </div>
      </div>

      {/* Universal Immunization UIP Target Card */}
      <div 
        onClick={() => onNavigateTab('patients')}
        className="p-3.5 rounded-xl bg-white border border-slate-200 flex items-center justify-between cursor-pointer hover:border-slate-300 transition-colors shadow-xs"
      >
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-lg bg-emerald-50 text-[#15803D] border border-emerald-200 flex items-center justify-center shrink-0">
            <span className="text-sm font-black">💉</span>
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <h4 className="text-xs font-black text-[#0F172A]">Mission Indradhanush (UIP)</h4>
              <span className="px-1.5 py-0.2 rounded-xs bg-slate-200 text-slate-900 text-[8px] font-black">Govt Target</span>
            </div>
            <p className="text-[10px] font-medium text-slate-500">Ward 7 cohort vaccination coverage verified</p>
          </div>
        </div>
        <span className="text-xl font-black text-[#15803D]">85%</span>
      </div>

      {/* Recent Activity Ledger (Clickable to open RecentActivityView) */}
      <div 
        onClick={() => onOpenRecentActivity && onOpenRecentActivity()}
        className="p-3.5 rounded-xl bg-white border border-slate-200 hover:border-slate-300 flex items-center justify-between cursor-pointer transition-all shadow-xs"
      >
        <div>
          <h4 className="text-xs font-black text-[#0F172A] flex items-center gap-1.5">
            <span>ASHA Field Work Ledger</span>
            <ChevronRight className="w-3.5 h-3.5 text-slate-900" />
          </h4>
          <p className="text-[10px] font-medium text-slate-500">Live clinical field records & household surveys</p>
        </div>
        <span className="px-2.5 py-0.5 rounded-full bg-emerald-50 text-[#15803D] text-[10px] font-black border border-emerald-200">
          7 Recorded
        </span>
      </div>

      {/* Official Government Compliance Footer */}
      <div className="pt-1 text-center">
        <p className="text-[9px] font-bold text-slate-500 uppercase tracking-widest">
          ABDM COMPLIANT · MoHFW GOVT. OF INDIA · NIC CLOUD SECURE
        </p>
      </div>
    </div>
  );
};

