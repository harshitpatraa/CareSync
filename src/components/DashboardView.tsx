import React, { useState } from 'react';
import { 
  Menu, 
  Bell, 
  CloudCheck, 
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
  TrendingUp,
  TrendingDown
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { Patient } from '../types';

interface DashboardViewProps {
  onNavigateTab: (tab: string) => void;
  onSelectPatient: (patient: Patient) => void;
  onOpenAddModal: () => void;
  onOpenSosModal: () => void;
  onOpenDrawer: () => void;
}

export const DashboardView: React.FC<DashboardViewProps> = ({
  onNavigateTab,
  onOpenAddModal,
  onOpenSosModal,
  onOpenDrawer
}) => {
  const { patients, syncAllRecords } = useApp();
  const [isSyncing, setIsSyncing] = useState(false);

  const handleSync = () => {
    setIsSyncing(true);
    setTimeout(() => {
      syncAllRecords();
      setIsSyncing(false);
    }, 600);
  };

  const todayCount = patients.filter(p => p.isTodayVisit).length;
  const pendingCount = patients.filter(p => p.syncStatus === 'PENDING_UPLOAD').length;
  const emergencyCount = patients.filter(p => p.riskLevel === 'CRITICAL' || p.isCritical).length;
  const ancCount = patients.filter(p => p.category === 'PREGNANCY').length;
  const childCount = patients.filter(p => p.category === 'CHILD').length;

  return (
    <div className="space-y-4">
      {/* Top App Bar */}
      <div className="flex items-center justify-between py-1">
        <button 
          onClick={onOpenDrawer}
          className="p-2 text-slate-300 hover:text-white cursor-pointer"
        >
          <Menu className="w-6 h-6" />
        </button>

        <div className="flex items-center space-x-2">
          {/* Logo icon */}
          <div className="w-7 h-7 rounded-lg bg-gradient-to-tr from-purple-500 via-indigo-500 to-cyan-400 p-[1.5px]">
            <div className="w-full h-full bg-[#0d1017] rounded-[7px] flex items-center justify-center">
              <div className="w-3.5 h-3.5 border-2 border-cyan-400 rounded-sm rotate-45 flex items-center justify-center">
                <div className="w-1.5 h-1.5 bg-purple-400 rounded-full"></div>
              </div>
            </div>
          </div>
          <span className="font-bold text-base text-white tracking-tight">CareSync</span>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2.5 py-1 rounded-full bg-[#1b382b] text-[#22c55e] border border-[#22c55e]/30 text-[11px] font-semibold flex items-center gap-1">
            <span className="text-[10px]">☁️</span>
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

      {/* Hero Field Hub Card (Gradient Blue) */}
      <div className="p-4 rounded-3xl bg-gradient-to-r from-[#6366f1] via-[#3b82f6] to-[#06b6d4] text-white shadow-xl shadow-blue-500/10 space-y-3 relative overflow-hidden">
        <div className="flex justify-between items-start">
          <div>
            <span className="text-[11px] font-medium text-blue-100/90 tracking-wide block">CareSync Field Hub</span>
            <h2 className="text-xl font-bold text-white tracking-tight mt-0.5">Dr. Priya Kulkarni</h2>
            <p className="text-xs text-blue-100/80">CareSync Community Health Hub</p>
          </div>

          {/* Medical Illustration mockup box */}
          <div className="w-20 h-14 rounded-2xl bg-white/95 p-1.5 flex items-center justify-center shadow-md">
            <div className="w-full h-full rounded-xl bg-blue-50 flex flex-col items-center justify-center p-1 text-[8px] text-blue-600 font-bold leading-tight text-center">
              <span>🩺 NHM</span>
              <span className="text-[7px] text-cyan-600">Health Hub</span>
            </div>
          </div>
        </div>

        {/* Sync Status strip & Button */}
        <div className="pt-2 border-t border-white/20 flex items-center justify-between">
          <div>
            <div className="text-xs font-semibold text-white">All local records synchronized</div>
            <p className="text-[10px] text-blue-100/70">Last sync: Just now</p>
          </div>

          <button
            onClick={handleSync}
            disabled={isSyncing}
            className="px-3.5 py-1.5 rounded-full bg-white text-[#4f46e5] text-xs font-bold shadow-md hover:bg-slate-100 cursor-pointer flex items-center gap-1.5 transition-all"
          >
            <RotateCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin' : ''}`} />
            <span>Sync Now</span>
          </button>
        </div>
      </div>

      {/* Quick Actions Grid */}
      <div className="space-y-2">
        <h3 className="text-xs font-bold text-slate-300">Quick Actions</h3>

        <div className="grid grid-cols-4 gap-2.5">
          {/* Add Patient */}
          <button
            onClick={onOpenAddModal}
            className="p-3 rounded-2xl bg-[#131926] border border-slate-800/80 flex flex-col items-center justify-center space-y-2 cursor-pointer hover:border-indigo-500/40 transition-colors"
          >
            <div className="w-10 h-10 rounded-full bg-[#1e2040] text-[#818cf8] flex items-center justify-center shadow-inner">
              <UserPlus className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-slate-300 text-center leading-tight">Add Patient</span>
          </button>

          {/* Symptoms */}
          <button
            onClick={() => onNavigateTab('risk-assessment')}
            className="p-3 rounded-2xl bg-[#131926] border border-slate-800/80 flex flex-col items-center justify-center space-y-2 cursor-pointer hover:border-purple-500/40 transition-colors"
          >
            <div className="w-10 h-10 rounded-full bg-[#271d40] text-[#c084fc] flex items-center justify-center shadow-inner">
              <BriefcaseMedical className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-slate-300 text-center leading-tight">Symptoms</span>
          </button>

          {/* AI Chat */}
          <button
            onClick={() => onNavigateTab('ai-assistant')}
            className="p-3 rounded-2xl bg-[#131926] border border-slate-800/80 flex flex-col items-center justify-center space-y-2 cursor-pointer hover:border-emerald-500/40 transition-colors"
          >
            <div className="w-10 h-10 rounded-full bg-[#152e25] text-[#4ade80] flex items-center justify-center shadow-inner">
              <Bot className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-slate-300 text-center leading-tight">AI Chat</span>
          </button>

          {/* Risk */}
          <button
            onClick={() => onNavigateTab('risk-assessment')}
            className="p-3 rounded-2xl bg-[#131926] border border-slate-800/80 flex flex-col items-center justify-center space-y-2 cursor-pointer hover:border-rose-500/40 transition-colors"
          >
            <div className="w-10 h-10 rounded-full bg-[#351b27] text-[#fb7185] flex items-center justify-center shadow-inner">
              <Stethoscope className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-slate-300 text-center leading-tight">Risk</span>
          </button>
        </div>
      </div>

      {/* Clinical Care Overview Section */}
      <div className="space-y-2.5">
        <h3 className="text-xs font-bold text-slate-300">Clinical Care Overview</h3>

        <div className="grid grid-cols-2 gap-3">
          {/* Today's Patients */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800/90 space-y-2 cursor-pointer hover:border-slate-700 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#201d3b] text-[#a78bfa] flex items-center justify-center">
                <Calendar className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">{todayCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-semibold text-white truncate">Today's Patie...</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded-full bg-[#183327] text-[#4ade80] border border-[#22c55e]/20 flex items-center">
                  ↗ 18.2%
                </span>
              </div>
              <p className="text-[10px] text-slate-400 mt-0.5">Active rounds · 1,234 today</p>
            </div>
          </div>

          {/* Pending Visits */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800/90 space-y-2 cursor-pointer hover:border-slate-700 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#33221b] text-[#fb923c] flex items-center justify-center">
                <Clock className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">{pendingCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-semibold text-white">Pending Visits</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded-full bg-[#352119] text-[#f97316] border border-[#f97316]/20 flex items-center">
                  ↘ 3.2%
                </span>
              </div>
              <p className="text-[10px] text-slate-400 mt-0.5">Awaiting checkup</p>
            </div>
          </div>

          {/* Emergency Cases */}
          <div 
            onClick={onOpenSosModal}
            className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800/90 space-y-2 cursor-pointer hover:border-red-500/40 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#36191f] text-[#f43f5e] flex items-center justify-center">
                <AlertTriangle className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">{emergencyCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-semibold text-white truncate">Emergency Ca...</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded-full bg-[#183327] text-[#4ade80] border border-[#22c55e]/20 flex items-center">
                  ↗ 2.1%
                </span>
              </div>
              <p className="text-[10px] text-slate-400 mt-0.5">Critical priority</p>
            </div>
          </div>

          {/* Follow Ups */}
          <div 
            onClick={() => onNavigateTab('records')}
            className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800/90 space-y-2 cursor-pointer hover:border-slate-700 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#241f3d] text-[#c084fc] flex items-center justify-center">
                <History className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white">4</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-semibold text-white">Follow Ups</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded-full bg-[#183327] text-[#4ade80] border border-[#22c55e]/20 flex items-center">
                  ↗ 31.1%
                </span>
              </div>
              <p className="text-[10px] text-slate-400 mt-0.5">Scheduled reviews</p>
            </div>
          </div>

          {/* Pregnancy Tracking */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800/90 space-y-2 cursor-pointer hover:border-slate-700 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#361e2e] text-[#f472b6] flex items-center justify-center">
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
            className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800/90 space-y-2 cursor-pointer hover:border-slate-700 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#182c23] text-[#4ade80] flex items-center justify-center">
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
        className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800/90 flex items-center justify-between cursor-pointer hover:border-slate-700 transition-colors"
      >
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-xl bg-[#152e22] text-[#4ade80] flex items-center justify-center shrink-0">
            <span className="text-base">💉</span>
          </div>
          <div>
            <h4 className="text-xs font-bold text-white">Vaccination Status</h4>
            <p className="text-[10px] text-slate-400">Cohort immunization coverage across {patients.length} patients</p>
          </div>
        </div>
        <span className="text-xl font-bold text-white">85%</span>
      </div>

      {/* Ward Outbreak & Trend Radar Card */}
      <div className="p-4 rounded-2xl bg-[#131926] border border-slate-800/90 space-y-3">
        <div className="flex items-center space-x-3">
          <div className="w-8 h-8 rounded-xl bg-[#261938] text-[#c084fc] flex items-center justify-center shrink-0">
            <span className="text-base">🎯</span>
          </div>
          <div>
            <h4 className="text-xs font-bold text-white">Ward Outbreak & Trend Radar</h4>
            <p className="text-[10px] text-slate-400">Offline Cohort Intelligence</p>
          </div>
        </div>

        <button 
          onClick={() => onNavigateTab('risk-assessment')}
          className="w-full py-2.5 rounded-xl border border-purple-500/40 bg-purple-950/20 hover:bg-purple-900/30 text-purple-300 font-semibold text-xs flex items-center justify-center gap-2 cursor-pointer transition-colors"
        >
          <span>✨</span>
          <span>Scan Ward Trends</span>
        </button>
      </div>

      {/* Health Analytics Card */}
      <div 
        onClick={() => onNavigateTab('records')}
        className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800/90 flex items-center justify-between cursor-pointer hover:border-slate-700 transition-colors"
      >
        <div>
          <h4 className="text-xs font-bold text-white">Health Analytics</h4>
          <p className="text-[10px] text-slate-400">7-day outpatient & home-visit volume</p>
        </div>
        <div className="px-2.5 py-1 rounded-full bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/30 text-[10px] font-semibold">
          {patients.filter(p => p.syncStatus === 'SYNCED').length}/{patients.length} Synced
        </div>
      </div>

      {/* Recent Activity Card */}
      <div className="p-3.5 rounded-2xl bg-gradient-to-r from-[#171633] to-[#121626] border border-purple-900/30 flex items-center justify-between">
        <div>
          <h4 className="text-xs font-bold text-white">Recent Activity</h4>
          <p className="text-[10px] text-slate-400">Live clinical events, vitals & field ledger</p>
        </div>
        <span className="px-2.5 py-0.5 rounded-full bg-slate-800 text-slate-300 text-[10px] font-semibold border border-slate-700">
          Active
        </span>
      </div>
    </div>
  );
};
