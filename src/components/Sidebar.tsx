import React from 'react';
import { 
  LayoutDashboard, 
  Users, 
  Bot, 
  Stethoscope, 
  AlertTriangle, 
  FileText, 
  Activity, 
  Settings, 
  PhoneCall, 
  Wifi, 
  WifiOff, 
  CloudCheck,
  ShieldCheck,
  HeartPulse
} from 'lucide-react';
import { useApp } from '../context/AppContext';

interface SidebarProps {
  currentTab: string;
  setCurrentTab: (tab: string) => void;
  onOpenSosModal: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ currentTab, setCurrentTab, onOpenSosModal }) => {
  const { isOfflineOnly, setIsOfflineOnly, patients, syncAllRecords } = useApp();

  const pendingSync = patients.filter(p => p.syncStatus === 'PENDING_UPLOAD').length;

  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, badge: null },
    { id: 'patients', label: 'Patient Roster', icon: Users, badge: patients.length },
    { id: 'ai-assistant', label: 'Sehat Setu AI Bot', icon: Bot, badge: 'AI' },
    { id: 'risk-assessment', label: 'Disease Risk & Triage', icon: Stethoscope, badge: 'New' },
    { id: 'records', label: 'Health Vault', icon: FileText, badge: null },
    { id: 'analytics', label: 'Outbreak & Analytics', icon: Activity, badge: 'Radar' },
    { id: 'emergency', label: '108 SOS & Clinicians', icon: PhoneCall, badge: 'SOS' },
    { id: 'settings', label: 'Settings', icon: Settings, badge: null },
  ];

  return (
    <aside className="w-64 bg-[#0d131f] border-r border-slate-800/80 flex flex-col h-screen select-none sticky top-0 shrink-0 z-30">
      {/* Brand Header */}
      <div className="p-4 border-b border-slate-800/60 flex items-center justify-between">
        <div className="flex items-center space-x-3 cursor-pointer" onClick={() => setCurrentTab('dashboard')}>
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-blue-600 to-cyan-400 p-0.5 shadow-lg shadow-blue-500/20">
            <div className="w-full h-full bg-[#0b0f17] rounded-[10px] flex items-center justify-center">
              <HeartPulse className="w-5 h-5 text-cyan-400 animate-pulse" />
            </div>
          </div>
          <div>
            <h1 className="font-bold text-lg text-white tracking-tight flex items-center gap-1.5">
              CareSync
              <span className="text-[10px] px-1.5 py-0.5 rounded bg-blue-500/20 text-blue-400 border border-blue-500/30 font-semibold">Web</span>
            </h1>
            <p className="text-[11px] text-slate-400 font-medium">ASHA Clinical Hub</p>
          </div>
        </div>
      </div>

      {/* SOS Button */}
      <div className="p-3">
        <button
          onClick={onOpenSosModal}
          className="w-full py-2.5 px-3 rounded-xl bg-gradient-to-r from-red-600 to-rose-500 hover:from-red-500 hover:to-rose-600 text-white font-semibold text-xs flex items-center justify-center space-x-2 shadow-lg shadow-red-500/25 transition-all transform active:scale-98 cursor-pointer"
        >
          <AlertTriangle className="w-4 h-4 animate-bounce" />
          <span>BROADCAST 108 SOS</span>
        </button>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-2 space-y-1 overflow-y-auto">
        <p className="px-3 text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-2">Clinical Portal</p>
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setCurrentTab(item.id)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-xs font-medium transition-all cursor-pointer ${
                isActive
                  ? 'bg-blue-600/15 text-blue-400 border border-blue-500/30 shadow-sm shadow-blue-500/10'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/40'
              }`}
            >
              <div className="flex items-center space-x-3">
                <Icon className={`w-4 h-4 ${isActive ? 'text-blue-400' : 'text-slate-400'}`} />
                <span>{item.label}</span>
              </div>
              {item.badge && (
                <span className={`text-[10px] px-1.5 py-0.5 rounded-full font-bold ${
                  item.badge === 'SOS' 
                    ? 'bg-red-500/20 text-red-400 border border-red-500/30' 
                    : item.badge === 'AI' 
                    ? 'bg-purple-500/20 text-purple-400 border border-purple-500/30'
                    : 'bg-slate-800 text-slate-300'
                }`}>
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      {/* Sync & Vault Status Box */}
      <div className="p-3 m-3 rounded-xl bg-slate-900/80 border border-slate-800">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center space-x-2">
            {isOfflineOnly ? (
              <WifiOff className="w-3.5 h-3.5 text-amber-400" />
            ) : (
              <Wifi className="w-3.5 h-3.5 text-emerald-400" />
            )}
            <span className="text-[11px] font-semibold text-slate-300">
              {isOfflineOnly ? 'Offline Vault' : 'Cloud Connected'}
            </span>
          </div>
          <button 
            onClick={() => setIsOfflineOnly(!isOfflineOnly)}
            className="text-[10px] text-blue-400 hover:underline cursor-pointer"
          >
            {isOfflineOnly ? 'Go Online' : 'Go Offline'}
          </button>
        </div>

        <div className="flex items-center justify-between text-[11px] text-slate-400 mb-2">
          <span>Pending queue:</span>
          <span className={`font-bold ${pendingSync > 0 ? 'text-amber-400' : 'text-emerald-400'}`}>
            {pendingSync} records
          </span>
        </div>

        <button
          onClick={() => {
            const res = syncAllRecords();
            alert(res.message);
          }}
          className="w-full py-1.5 px-2 rounded-lg bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 border border-blue-500/30 text-[11px] font-medium flex items-center justify-center space-x-1.5 cursor-pointer transition-colors"
        >
          <CloudCheck className="w-3.5 h-3.5" />
          <span>Sync All Queue</span>
        </button>
      </div>

      {/* Worker Badge */}
      <div className="p-3 border-t border-slate-800/80 flex items-center space-x-3">
        <div className="w-8 h-8 rounded-full bg-emerald-500/20 border border-emerald-500/40 flex items-center justify-center text-emerald-400 font-bold text-xs">
          PK
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-xs font-semibold text-white truncate">Dr. Priya Kulkarni</p>
          <p className="text-[10px] text-slate-400 truncate flex items-center gap-1">
            <ShieldCheck className="w-2.5 h-2.5 text-emerald-400" />
            CHO · Ward 4 Hub
          </p>
        </div>
      </div>
    </aside>
  );
};
