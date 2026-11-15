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
  ChevronRight
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

  const handleSync = () => {
    setIsSyncing(true);
    setTimeout(() => {
      const res = syncAllRecords();
      setIsSyncing(false);
      setSyncToast(res.message || "All local records synchronized to CareSync Cloud!");
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
        <div className="fixed top-12 left-1/2 -translate-x-1/2 z-50 px-4 py-2 rounded-2xl bg-[#262626] border border-[#52525b] text-white text-xs font-bold shadow-2xl flex items-center gap-2 animate-in fade-in-50">
          <CheckCircle2 className="w-4 h-4 text-[#22c55e] shrink-0" />
          <span className="font-bold">{syncToast}</span>
        </div>
      )}

      {/* Top App Bar */}
      <div className="flex items-center justify-between py-1">
        <button 
          onClick={onOpenDrawer}
          className="p-2 text-white hover:text-[#d4d4d8] cursor-pointer"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-2">
          {/* Logo icon - Solid Grey / Black */}
          <div className="w-7 h-7 rounded-xl bg-[#27272a] border border-[#3f3f46] flex items-center justify-center p-1 text-white shadow-md">
            <div className="relative w-4 h-4 flex items-center justify-center">
              <div className="w-3 h-3 bg-white rounded-xs rotate-45"></div>
              <div className="absolute w-1.5 h-1.5 bg-[#22c55e] rounded-full"></div>
            </div>
          </div>
          <span className="font-black text-base text-white tracking-tight">CareSync</span>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2.5 py-1 rounded-full bg-[#1c2e1f] text-[#4ade80] border border-[#22c55e]/40 text-[11px] font-bold flex items-center gap-1">
            <span>☁️</span>
            <span className="font-extrabold">Sync</span>
          </div>
          <button 
            onClick={() => onNavigateTab('emergency')}
            className="p-1.5 text-white hover:text-[#d4d4d8] cursor-pointer"
          >
            <Bell className="w-5 h-5 text-white" />
          </button>
        </div>
      </div>

      {/* Hero Field Hub Card - Solid Grey / Solid Black */}
      <div className="p-4 rounded-3xl bg-[#27272a] border-2 border-[#3f3f46] text-white shadow-xl space-y-3 relative overflow-hidden">
        <div className="flex justify-between items-start">
          <div>
            <span className="text-[11px] font-extrabold text-[#d4d4d8] tracking-wider block uppercase">CareSync Field Hub</span>
            <h2 className="text-xl font-black text-white tracking-tight mt-0.5">Dr. Priya Kulkarni</h2>
            <p className="text-xs font-bold text-[#a1a1aa]">CareSync Community Health Hub</p>
          </div>

          <div className="w-16 h-12 rounded-2xl bg-[#18181b] border border-[#3f3f46] p-1 flex items-center justify-center shadow-md">
            <div className="w-full h-full rounded-xl bg-[#27272a] flex flex-col items-center justify-center text-[8px] text-white font-black leading-tight text-center">
              <span className="font-black">🩺 NHM</span>
              <span className="text-[7px] text-[#a1a1aa] font-mono font-bold">Hub</span>
            </div>
          </div>
        </div>

        {/* Sync Status strip & Button */}
        <div className="pt-2 border-t border-[#3f3f46] flex items-center justify-between">
          <div>
            <div className="text-xs font-extrabold text-white">All local records synchronized</div>
            <p className="text-[10px] font-bold text-[#a1a1aa]">Last sync: Just now</p>
          </div>

          <button
            onClick={handleSync}
            disabled={isSyncing}
            className="px-3.5 py-1.5 rounded-full bg-white text-black text-xs font-black shadow-md hover:bg-[#e4e4e7] cursor-pointer flex items-center gap-1.5 transition-all"
          >
            <RotateCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin' : ''}`} />
            <span>Sync Now</span>
          </button>
        </div>
      </div>

      {/* Quick Actions Grid - Solid Grey Surfaces */}
      <div className="space-y-2">
        <h3 className="text-xs font-black text-white uppercase tracking-wider">Quick Actions</h3>

        <div className="grid grid-cols-4 gap-2.5">
          {/* Add Patient */}
          <button
            onClick={onOpenAddModal}
            className="p-3 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] flex flex-col items-center justify-center space-y-2 cursor-pointer transition-colors"
          >
            <div className="w-10 h-10 rounded-xl bg-[#27272a] text-white border border-[#3f3f46] flex items-center justify-center shadow-inner">
              <UserPlus className="w-5 h-5 text-white" />
            </div>
            <span className="text-[11px] font-extrabold text-white text-center leading-tight">Add Patient</span>
          </button>

          {/* Symptoms */}
          <button
            onClick={() => onNavigateTab('risk-assessment')}
            className="p-3 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] flex flex-col items-center justify-center space-y-2 cursor-pointer transition-colors"
          >
            <div className="w-10 h-10 rounded-xl bg-[#27272a] text-white border border-[#3f3f46] flex items-center justify-center shadow-inner">
              <BriefcaseMedical className="w-5 h-5 text-white" />
            </div>
            <span className="text-[11px] font-extrabold text-white text-center leading-tight">Symptoms</span>
          </button>

          {/* AI Chat */}
          <button
            onClick={() => onNavigateTab('ai-assistant')}
            className="p-3 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] flex flex-col items-center justify-center space-y-2 cursor-pointer transition-colors"
          >
            <div className="w-10 h-10 rounded-xl bg-[#27272a] text-white border border-[#3f3f46] flex items-center justify-center shadow-inner">
              <Bot className="w-5 h-5 text-white" />
            </div>
            <span className="text-[11px] font-extrabold text-white text-center leading-tight">AI Chat</span>
          </button>

          {/* Risk */}
          <button
            onClick={() => onNavigateTab('risk-assessment')}
            className="p-3 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] flex flex-col items-center justify-center space-y-2 cursor-pointer transition-colors"
          >
            <div className="w-10 h-10 rounded-xl bg-[#27272a] text-white border border-[#3f3f46] flex items-center justify-center shadow-inner">
              <Stethoscope className="w-5 h-5 text-white" />
            </div>
            <span className="text-[11px] font-extrabold text-white text-center leading-tight">Risk</span>
          </button>
        </div>
      </div>

      {/* Clinical Care Overview Section - Solid Grey & Solid Black */}
      <div className="space-y-2.5">
        <h3 className="text-xs font-black text-white uppercase tracking-wider">Clinical Care Overview</h3>

        <div className="grid grid-cols-2 gap-3">
          {/* Today's Patients */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] space-y-2 cursor-pointer transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#27272a] text-white border border-[#3f3f46] flex items-center justify-center">
                <Calendar className="w-4 h-4 text-white" />
              </div>
              <span className="text-2xl font-black text-white">{todayCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-black text-white truncate">Today's Patients</span>
                <span className="text-[9px] font-black px-1.5 py-0.5 rounded-full bg-[#1c2e1f] text-[#4ade80] border border-[#22c55e]/30 flex items-center">
                  ↗ 18.2%
                </span>
              </div>
              <p className="text-[10px] font-bold text-[#a1a1aa] mt-0.5">Active rounds · 1,234 today</p>
            </div>
          </div>

          {/* Pending Visits */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] space-y-2 cursor-pointer transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#27272a] text-white border border-[#3f3f46] flex items-center justify-center">
                <Clock className="w-4 h-4 text-white" />
              </div>
              <span className="text-2xl font-black text-white">{pendingCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-black text-white">Pending Visits</span>
                <span className="text-[9px] font-black px-1.5 py-0.5 rounded-full bg-[#27272a] text-white border border-[#3f3f46] flex items-center">
                  ↘ 3.2%
                </span>
              </div>
              <p className="text-[10px] font-bold text-[#a1a1aa] mt-0.5">Awaiting checkup</p>
            </div>
          </div>

          {/* Emergency Cases (Solid Red alert for medical urgency) */}
          <div 
            onClick={onOpenSosModal}
            className="p-3.5 rounded-2xl bg-[#18181b] border-2 border-red-900/60 hover:border-red-600 space-y-2 cursor-pointer transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-red-950 text-red-400 border border-red-600 flex items-center justify-center">
                <AlertTriangle className="w-4 h-4 text-red-400" />
              </div>
              <span className="text-2xl font-black text-white">{emergencyCount}</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-black text-white truncate">Emergency Cases</span>
                <span className="text-[9px] font-black px-1.5 py-0.5 rounded-full bg-red-950 text-red-400 border border-red-600 flex items-center">
                  Critical
                </span>
              </div>
              <p className="text-[10px] font-bold text-[#a1a1aa] mt-0.5">Critical priority watch</p>
            </div>
          </div>

          {/* Follow Ups */}
          <div 
            onClick={() => onNavigateTab('records')}
            className="p-3.5 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] space-y-2 cursor-pointer transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#27272a] text-white border border-[#3f3f46] flex items-center justify-center">
                <History className="w-4 h-4 text-white" />
              </div>
              <span className="text-2xl font-black text-white">4</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-black text-white">Follow Ups</span>
                <span className="text-[9px] font-black px-1.5 py-0.5 rounded-full bg-[#1c2e1f] text-[#4ade80] border border-[#22c55e]/30 flex items-center">
                  ↗ 31.1%
                </span>
              </div>
              <p className="text-[10px] font-bold text-[#a1a1aa] mt-0.5">Scheduled reviews</p>
            </div>
          </div>

          {/* Pregnancy Tracking */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] space-y-2 cursor-pointer transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#27272a] text-white border border-[#3f3f46] flex items-center justify-center">
                <Baby className="w-4 h-4 text-white" />
              </div>
              <span className="text-2xl font-black text-white">{ancCount}</span>
            </div>
            <div>
              <span className="text-xs font-black text-white">Pregnancy Tracking</span>
              <p className="text-[10px] font-bold text-[#a1a1aa] mt-0.5">ANC mothers monitored</p>
            </div>
          </div>

          {/* Child Monitoring */}
          <div 
            onClick={() => onNavigateTab('patients')}
            className="p-3.5 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] space-y-2 cursor-pointer transition-colors"
          >
            <div className="flex items-center justify-between">
              <div className="w-8 h-8 rounded-xl bg-[#27272a] text-white border border-[#3f3f46] flex items-center justify-center">
                <Heart className="w-4 h-4 text-white" />
              </div>
              <span className="text-2xl font-black text-white">{childCount}</span>
            </div>
            <div>
              <span className="text-xs font-black text-white">Child Monitoring</span>
              <p className="text-[10px] font-bold text-[#a1a1aa] mt-0.5">Growth & pediatric care</p>
            </div>
          </div>
        </div>
      </div>

      {/* Vaccination Status Card - Solid Grey */}
      <div 
        onClick={() => onNavigateTab('patients')}
        className="p-3.5 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] flex items-center justify-between cursor-pointer transition-colors"
      >
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-xl bg-[#1c2e1f] text-[#4ade80] border border-[#22c55e]/40 flex items-center justify-center shrink-0">
            <span className="text-sm font-black">💉</span>
          </div>
          <div>
            <h4 className="text-xs font-black text-white">Vaccination Status</h4>
            <p className="text-[10px] font-bold text-[#a1a1aa]">Cohort immunization coverage across {patients.length} patients</p>
          </div>
        </div>
        <span className="text-2xl font-black text-[#4ade80]">85%</span>
      </div>

      {/* Health Analytics Card - Solid Grey */}
      <div 
        onClick={() => onNavigateTab('records')}
        className="p-3.5 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] flex items-center justify-between cursor-pointer transition-colors"
      >
        <div>
          <h4 className="text-xs font-black text-white">Health Analytics</h4>
          <p className="text-[10px] font-bold text-[#a1a1aa]">7-day outpatient & home-visit volume</p>
        </div>
        <div className="px-2.5 py-1 rounded-full bg-[#1c2e1f] text-[#4ade80] border border-[#22c55e]/40 text-[10px] font-black">
          {patients.filter(p => p.syncStatus === 'SYNCED').length}/{patients.length} Synced
        </div>
      </div>

      {/* Recent Activity Card - Solid Grey */}
      <div 
        onClick={() => onOpenRecentActivity && onOpenRecentActivity()}
        className="p-3.5 rounded-2xl bg-[#18181b] border-2 border-[#27272a] hover:border-[#52525b] flex items-center justify-between cursor-pointer transition-all shadow-sm"
      >
        <div>
          <h4 className="text-xs font-black text-white flex items-center gap-1.5">
            <span>Recent Activity</span>
            <ChevronRight className="w-3.5 h-3.5 text-white" />
          </h4>
          <p className="text-[10px] font-bold text-[#a1a1aa]">Live clinical events, vitals & field ledger</p>
        </div>
        <span className="px-2.5 py-0.5 rounded-full bg-[#1c2e1f] text-[#4ade80] text-[10px] font-black border border-[#22c55e]/40">
          Active (7)
        </span>
      </div>
    </div>
  );
};
