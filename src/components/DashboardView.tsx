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
  Target,
  CheckCircle2,
  ChevronRight
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { Patient } from '../types';
import { WardRadarModal } from './WardRadarModal';

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
  const [showWardModal, setShowWardModal] = useState(false);
  const [syncToast, setSyncToast] = useState<string | null>(null);

  const handleSync = () => {
    setIsSyncing(true);
    setTimeout(() => {
      const res = syncAllRecords();
      setIsSyncing(false);
      setSyncToast(res.message || "All local Room records synchronized to CareSync Cloud!");
      setTimeout(() => setSyncToast(null), 3000);
    }, 700);
  };

  const todayCount = patients.filter(p => p.isTodayVisit).length;
  const pendingCount = patients.filter(p => p.syncStatus === 'PENDING_UPLOAD').length;
  const emergencyCount = patients.filter(p => p.riskLevel === 'CRITICAL' || p.isCritical).length;
  const ancCount = patients.filter(p => p.category === 'PREGNANCY').length;
  const childCount = patients.filter(p => p.category === 'CHILD').length;

  return (
    <div className="space-y-4 pb-4">
      {/* Toast Notification */}
      {syncToast && (
        <div className="fixed top-12 left-1/2 -translate-x-1/2 z-50 px-4 py-2 rounded-2xl bg-indigo-950 border border-indigo-500 text-indigo-200 text-xs font-semibold shadow-2xl flex items-center gap-2 animate-in fade-in-50">
          <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
          <span>{syncToast}</span>
        </div>
      )}

      {/* Ward Radar Modal */}
      {showWardModal && (
        <WardRadarModal 
          onClose={() => setShowWardModal(false)} 
          onNavigateToRisk={() => onNavigateTab('risk-assessment')}
        />
      )}

      {/* Top App Bar */}
      <div className="flex items-center justify-between py-1">
        <button 
          onClick={onOpenDrawer}
          className="p-2 text-slate-300 hover:text-white cursor-pointer"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-2">
          {/* Logo icon (Unified 2-color: Indigo & Emerald) */}
          <div className="w-7 h-7 rounded-xl bg-indigo-600 flex items-center justify-center p-1 text-white shadow-md shadow-indigo-600/30">
            <div className="relative w-4 h-4 flex items-center justify-center">
              <div className="w-3 h-3 bg-white rounded-xs rotate-45"></div>
              <div className="absolute w-1.5 h-1.5 bg-emerald-400 rounded-full"></div>
            </div>
          </div>
          <span className="font-bold text-base text-white tracking-tight">CareSync</span>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2.5 py-1 rounded-full bg-emerald-950/60 text-emerald-400 border border-emerald-500/30 text-[11px] font-semibold flex items-center gap-1">
            <span>☁️</span>
            <span>Sync</span>
          </div>
          <button 
            onClick={() => onNavigateTab('emergency')}
            className="p-1.5 text-slate-300 hover:text-white cursor-pointer"
          >
            <Bell className="w-5 h-5 text-slate-300" />
          </button>
        </div>
      </div>

      {/* Hero Field Hub Card (2-color theme: Deep Clinical Indigo) */}
      <div className="p-4 rounded-3xl bg-gradient-to-r from-indigo-700 via-indigo-600 to-indigo-800 text-white shadow-xl shadow-indigo-950/40 space-y-3 relative overflow-hidden">
        <div className="flex justify-between items-start">
          <div>
            <span className="text-[11px] font-medium text-indigo-200 tracking-wide block">CareSync Field Hub</span>
            <h2 className="text-xl font-bold text-white tracking-tight mt-0.5">Dr. Priya Kulkarni</h2>
            <p className="text-xs text-indigo-200/90">CareSync Community Health Hub</p>
          </div>

          <div className="w-16 h-12 rounded-2xl bg-white/95 p-1 flex items-center justify-center shadow-md">
            <div className="w-full h-full rounded-xl bg-indigo-50 flex flex-col items-center justify-center text-[8px] text-indigo-700 font-bold leading-tight text-center">
              <span>🩺 NHM</span>
              <span className="text-[7px] text-indigo-900 font-mono">Hub</span>
            </div>
          </div>
        </div>

        {/* Sync Status strip & Button */}
        <div className="pt-2 border-t border-white/20 flex items-center justify-between">
          <div>
            <div className="text-xs font-semibold text-white">All local records synchronized</div>
            <p className="text-[10px] text-indigo-200">Last sync: Just now</p>
          </div>

          <button
            onClick={handleSync}
            disabled={isSyncing}
            className="px-3.5 py-1.5 rounded-full bg-white text-indigo-700 text-xs font-bold shadow-md hover:bg-slate-100 cursor-pointer flex items-center gap-1.5 transition-all"
          >
            <RotateCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin' : ''}`} />
            <span>Sync Now</span>
          </button>
        </div>
      </div>

      {/* Quick Actions Grid (2-Color Theme: Clean Indigo) */}
      <div className="space-y-2">
        <h3 className="text-xs font-bold text-slate-300">Quick Actions</h3>

        <div className="grid grid-cols-4 gap-2.5">
          {/* Add Patient */}
          <button
            onClick={onOpenAddModal}
            className="p-3 rounded-2xl bg-[#0f172a] border border-slate-800/90 flex flex-col items-center justify-center space-y-2 cursor-pointer hover:border-indigo-500/50 transition-colors"
          >
            <div className="w-10 h-10 rounded-xl bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 flex items-center justify-center shadow-inner">
              <UserPlus className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-slate-200 text-center leading-tight">Add Patient</span>
          </button>

          {/* Symptoms */}
          <button
            onClick={() => onNavigateTab('risk-assessment')}
            className="p-3 rounded-2xl bg-[#0f172a] border border-slate-800/90 flex flex-col items-center justify-center space-y-2 cursor-pointer hover:border-indigo-500/50 transition-colors"
          >
            <div className="w-10 h-10 rounded-xl bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 flex items-center justify-center shadow-inner">
              <BriefcaseMedical className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-slate-200 text-center leading-tight">Symptoms</span>
          </button>

          {/* AI Chat */}
          <button
            onClick={() => onNavigateTab('ai-assistant')}
            className="p-3 rounded-2xl bg-[#0f172a] border border-slate-800/90 flex flex-col items-center justify-center space-y-2 cursor-pointer hover:border-indigo-500/50 transition-colors"
          >
            <div className="w-10 h-10 rounded-xl bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 flex items-center justify-center shadow-inner">
              <Bot className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-slate-200 text-center leading-tight">AI Chat</span>
          </button>

          {/* Risk */}
          <button
            onClick={() => onNavigateTab('risk-assessment')}
            className="p-3 rounded-2xl bg-[#0f172a] border border-slate-800/90 flex flex-col items-center justify-center space-y-2 cursor-pointer hover:border-indigo-500/50 transition-colors"
          >
            <div className="w-10 h-10 rounded-xl bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 flex items-center justify-center shadow-inner">
              <Stethoscope className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-slate-200 text-center leading-tight">Risk</span>
          </button>
        </div>
      </div>

      {/* Clinical Care Overview Section (2-Color Theme: Indigo & Emerald, Crimson for Emergency) */}
      <div className="space-y-2.5">
        <h3 className="text-xs font-bold text-slate-300">Clinical Care Overview</h3>

        <div className="grid grid-cols-2 gap-3">
          {/* Today's Patients */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800/90 space-y-2 cursor-pointer hover:border-indigo-500/50 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 flex items-center justify-center">
                <Calendar className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">{todayCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-semibold text-white truncate">Today's Patie...</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded-full bg-emerald-950 text-emerald-400 border border-emerald-500/30 flex items-center">
                  ↗ 18.2%
                </span>
              </div>
              <p className="text-[10px] text-slate-400 mt-0.5">Active rounds · 1,234 today</p>
            </div>
          </div>

          {/* Pending Visits */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800/90 space-y-2 cursor-pointer hover:border-indigo-500/50 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 flex items-center justify-center">
                <Clock className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">{pendingCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-semibold text-white">Pending Visits</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded-full bg-slate-800 text-slate-300 border border-slate-700 flex items-center">
                  ↘ 3.2%
                </span>
              </div>
              <p className="text-[10px] text-slate-400 mt-0.5">Awaiting checkup</p>
            </div>
          </div>

          {/* Emergency Cases (Red alert) */}
          <div 
            onClick={onOpenSosModal}
            className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800/90 space-y-2 cursor-pointer hover:border-red-500/50 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-red-950/60 text-red-400 border border-red-500/30 flex items-center justify-center">
                <AlertTriangle className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">{emergencyCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-semibold text-white truncate">Emergency Ca...</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded-full bg-red-950 text-red-400 border border-red-500/30 flex items-center">
                  Critical
                </span>
              </div>
              <p className="text-[10px] text-slate-400 mt-0.5">Critical priority watch</p>
            </div>
          </div>

          {/* Follow Ups */}
          <div 
            onClick={() => onNavigateTab('records')}
            className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800/90 space-y-2 cursor-pointer hover:border-indigo-500/50 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 flex items-center justify-center">
                <History className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">4</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-semibold text-white">Follow Ups</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded-full bg-emerald-950 text-emerald-400 border border-emerald-500/30 flex items-center">
                  ↗ 31.1%
                </span>
              </div>
              <p className="text-[10px] text-slate-400 mt-0.5">Scheduled reviews</p>
            </div>
          </div>

          {/* Pregnancy Tracking */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800/90 space-y-2 cursor-pointer hover:border-indigo-500/50 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 flex items-center justify-center">
                <Baby className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">{ancCount}</span>
            </div>
            <div>
              <span className="text-xs font-semibold text-white">Pregnancy Tracking</span>
              <p className="text-[10px] text-slate-400 mt-0.5">ANC mothers monitored</p>
            </div>
          </div>

          {/* Child Monitoring */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800/90 space-y-2 cursor-pointer hover:border-indigo-500/50 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-indigo-950/60 text-indigo-300 border border-indigo-500/30 flex items-center justify-center">
                <Heart className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">{childCount}</span>
            </div>
            <div>
              <span className="text-xs font-semibold text-white">Child Monitoring</span>
              <p className="text-[10px] text-slate-400 mt-0.5">Growth & pediatric care</p>
            </div>
          </div>
        </div>
      </div>

      {/* Vaccination Status Card */}
      <div 
        onClick={() => onNavigateTab('patients')}
        className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800/90 flex items-center justify-between cursor-pointer hover:border-indigo-500/50 transition-colors"
      >
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-xl bg-emerald-950/60 text-emerald-400 border border-emerald-500/30 flex items-center justify-center shrink-0">
            <span className="text-sm font-bold">💉</span>
          </div>
          <div>
            <h4 className="text-xs font-bold text-white">Vaccination Status</h4>
            <p className="text-[10px] text-slate-400">Cohort immunization coverage across {patients.length} patients</p>
          </div>
        </div>
        <span className="text-xl font-bold text-emerald-400">85%</span>
      </div>

      {/* Ward Outbreak & Trend Radar Card */}
      <div className="p-4 rounded-2xl bg-[#0f172a] border border-slate-800/90 space-y-3">
        <div className="flex items-center space-x-3">
          <div className="w-8 h-8 rounded-xl bg-indigo-950/60 text-indigo-400 border border-indigo-500/30 flex items-center justify-center shrink-0">
            <Target className="w-4 h-4" />
          </div>
          <div>
            <h4 className="text-xs font-bold text-white">Ward Outbreak & Trend Radar</h4>
            <p className="text-[10px] text-slate-400">Offline Cohort Intelligence</p>
          </div>
        </div>

        <button 
          onClick={() => setShowWardModal(true)}
          className="w-full py-2.5 rounded-xl border border-indigo-500/40 bg-indigo-950/30 hover:bg-indigo-900/50 text-indigo-300 font-semibold text-xs flex items-center justify-center gap-2 cursor-pointer transition-colors shadow-xs"
        >
          <span>✨</span>
          <span>Scan Ward Trends</span>
        </button>
      </div>

      {/* Health Analytics Card */}
      <div 
        onClick={() => onNavigateTab('records')}
        className="p-3.5 rounded-2xl bg-[#0f172a] border border-slate-800/90 flex items-center justify-between cursor-pointer hover:border-indigo-500/50 transition-colors"
      >
        <div>
          <h4 className="text-xs font-bold text-white">Health Analytics</h4>
          <p className="text-[10px] text-slate-400">7-day outpatient & home-visit volume</p>
        </div>
        <div className="px-2.5 py-1 rounded-full bg-emerald-950/60 text-emerald-400 border border-emerald-500/30 text-[10px] font-semibold">
          {patients.filter(p => p.syncStatus === 'SYNCED').length}/{patients.length} Synced
        </div>
      </div>

      {/* Recent Activity Card (Clickable to open RecentActivityView) */}
      <div 
        onClick={() => onOpenRecentActivity && onOpenRecentActivity()}
        className="p-3.5 rounded-2xl bg-gradient-to-r from-indigo-950/40 to-[#0f172a] border border-indigo-500/30 flex items-center justify-between cursor-pointer hover:border-indigo-500/60 transition-all shadow-sm"
      >
        <div>
          <h4 className="text-xs font-bold text-white flex items-center gap-1.5">
            <span>Recent Activity</span>
            <ChevronRight className="w-3.5 h-3.5 text-indigo-400" />
          </h4>
          <p className="text-[10px] text-slate-400">Live clinical events, vitals & field ledger</p>
        </div>
        <span className="px-2.5 py-0.5 rounded-full bg-emerald-950/80 text-emerald-400 text-[10px] font-semibold border border-emerald-500/30">
          Active (7)
        </span>
      </div>
    </div>
  );
};
