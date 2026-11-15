import React from 'react';
import { 
  LayoutGrid, 
  Users, 
  FolderMinus, 
  PlusSquare, 
  Bot, 
  Activity, 
  Asterisk, 
  Bell, 
  User, 
  Settings, 
  Info,
  X
} from 'lucide-react';

interface NavigationDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  onNavigate: (tab: string) => void;
  onSignOut: () => void;
}

export const NavigationDrawer: React.FC<NavigationDrawerProps> = ({
  isOpen,
  onClose,
  onNavigate,
  onSignOut
}) => {
  if (!isOpen) return null;

  const menuItems = [
    { id: 'dashboard', label: 'Home', icon: LayoutGrid },
    { id: 'patients', label: 'Patients', icon: Users },
    { id: 'records', label: 'Health Records', icon: FolderMinus },
    { id: 'risk-assessment', label: 'Symptom Checker', icon: PlusSquare },
    { id: 'ai-assistant', label: 'AI Chat Assistant', icon: Bot },
    { id: 'risk-assessment', label: 'Medical Assessment', icon: Activity },
    { 
      id: 'sos', 
      label: 'CareSync Emergency Dispatch', 
      icon: Asterisk, 
      isDanger: true 
    },
    { id: 'emergency', label: 'Alerts', icon: Bell },
  ];

  const secondaryItems = [
    { id: 'profile', label: 'Profile', icon: User, action: () => onNavigate('settings') },
    { id: 'settings', label: 'Settings & Sync', icon: Settings, action: () => onNavigate('settings') },
    { id: 'about', label: 'About CareSync', icon: Info, action: onSignOut },
  ];

  return (
    <div className="absolute inset-0 z-50 flex">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-black/60 backdrop-blur-xs transition-opacity"
        onClick={onClose}
      />

      {/* Drawer Body */}
      <div className="relative w-[82%] max-w-[320px] h-full bg-[#0d131f] border-r border-slate-800 shadow-2xl flex flex-col z-10 animate-in slide-in-from-left duration-200">
        
        {/* Official Government Drawer Header */}
        <div className="p-4 bg-[#0f172a] border-b-2 border-[#1e3a8a] text-white shrink-0 relative">
          {/* Tricolor Stripe */}
          <div className="h-1 w-full flex overflow-hidden mb-3 rounded-full">
            <div className="w-1/3 bg-[#FF9933]"></div>
            <div className="w-1/3 bg-white"></div>
            <div className="w-1/3 bg-[#138808]"></div>
          </div>

          <button 
            onClick={onClose}
            className="absolute top-4 right-3 p-1 rounded-full text-white/70 hover:text-white hover:bg-white/10 cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>

          {/* Logo with Govt Emblem */}
          <div className="flex items-center space-x-2.5 mb-2.5">
            <div className="w-9 h-9 rounded-xl bg-[#1e293b] border border-[#334155] p-1 flex items-center justify-center text-lg shadow-sm">
              🏛️
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <h3 className="font-black text-base text-white leading-tight">CareSync</h3>
                <span className="text-[9px] font-black px-1.5 py-0.2 rounded-xs bg-[#1e3a8a] text-blue-200 border border-[#3b82f6]/40 uppercase">
                  NHM Govt
                </span>
              </div>
              <p className="text-[9px] font-bold text-slate-400 leading-none">
                Ministry of Health & Family Welfare · Govt of India
              </p>
            </div>
          </div>

          {/* Clinician Details */}
          <div className="pt-2 border-t border-[#1e293b]">
            <div className="flex items-center justify-between">
              <h4 className="font-black text-xs text-white">Dr. Priya Kulkarni</h4>
              <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-[#064e3b] text-[#34d399] border border-[#059669]/40">
                ABHA Verified
              </span>
            </div>
            <p className="text-[10px] text-slate-300 font-medium">Medical Officer Incharge · PHC Rampur</p>
            <p className="text-[9px] text-amber-400 font-mono mt-0.5">Govt Reg: NHM-DEL-2026-88102</p>
          </div>
        </div>

        {/* Drawer Menu Items */}
        <div className="flex-1 overflow-y-auto py-3 px-2 space-y-1 text-slate-300 text-xs">
          {menuItems.map((item, idx) => {
            const Icon = item.icon;
            const isDanger = item.isDanger;
            return (
              <button
                key={`${item.id}-${idx}`}
                onClick={() => {
                  onNavigate(item.id);
                  onClose();
                }}
                className={`w-full flex items-center space-x-3.5 px-3 py-2.5 rounded-xl text-left cursor-pointer transition-colors ${
                  isDanger 
                    ? 'text-red-400 hover:bg-red-500/10' 
                    : 'text-slate-300 hover:bg-slate-800/60 hover:text-white'
                }`}
              >
                <Icon className={`w-4 h-4 ${isDanger ? 'text-red-500 stroke-[2.5]' : 'text-slate-400'}`} />
                <span className={`text-[12px] ${isDanger ? 'font-semibold text-red-400' : 'font-medium'}`}>
                  {item.label}
                </span>
              </button>
            );
          })}

          <div className="my-2 border-t border-slate-800/80"></div>

          {secondaryItems.map((item) => {
            const Icon = item.icon;
            return (
              <button
                key={item.id}
                onClick={() => {
                  item.action();
                  onClose();
                }}
                className="w-full flex items-center space-x-3.5 px-3 py-2.5 rounded-xl text-left text-slate-400 hover:bg-slate-800/60 hover:text-white cursor-pointer transition-colors"
              >
                <Icon className="w-4 h-4 text-slate-400" />
                <span className="text-[12px] font-medium">{item.label}</span>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
};
