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
        
        {/* Drawer Header with Gradient */}
        <div className="p-5 bg-gradient-to-r from-[#6366f1] via-[#4f46e5] to-[#2563eb] text-white shrink-0 relative">
          <button 
            onClick={onClose}
            className="absolute top-3 right-3 p-1 rounded-full text-white/70 hover:text-white hover:bg-white/10 cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>

          {/* Logo */}
          <div className="flex items-center space-x-2.5 mb-3">
            <div className="w-8 h-8 rounded-xl bg-white/20 p-1 backdrop-blur-xs flex items-center justify-center">
              <div className="relative w-5 h-5 flex items-center justify-center">
                <div className="w-3.5 h-3.5 bg-white rounded-sm rotate-45"></div>
                <div className="absolute top-0 left-1/2 -translate-x-1/2 w-1.5 h-1.5 rounded-full bg-cyan-300"></div>
                <div className="absolute bottom-0 left-1/2 -translate-x-1/2 w-1.5 h-1.5 rounded-full bg-cyan-300"></div>
                <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1.5 h-1.5 rounded-full bg-pink-300"></div>
                <div className="absolute right-0 top-1/2 -translate-y-1/2 w-1.5 h-1.5 rounded-full bg-pink-300"></div>
              </div>
            </div>
            <div>
              <h3 className="font-bold text-base text-white leading-tight">CareSync</h3>
              <p className="text-[10px] text-blue-100/80 leading-none">Offline-First Healthcare Sync</p>
            </div>
          </div>

          {/* Clinician Details */}
          <div className="pt-2 border-t border-white/20">
            <h4 className="font-semibold text-sm text-white">Dr. Priya Kulkarni</h4>
            <p className="text-xs text-blue-100/80">priya.kulkarni@gmail.com</p>
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
