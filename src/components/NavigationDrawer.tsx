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
  X,
  Camera,
  HeartPulse,
  LogOut,
  ShieldCheck
} from 'lucide-react';
import { UserRole } from './ClinicianSignInView';
import { CareSyncLogo } from './CareSyncLogo';

interface NavigationDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  onNavigate: (tab: string) => void;
  onSignOut: () => void;
  userRole?: UserRole;
  userProfile?: any;
}

export const NavigationDrawer: React.FC<NavigationDrawerProps> = ({
  isOpen,
  onClose,
  onNavigate,
  onSignOut,
  userRole = 'asha_worker',
  userProfile
}) => {
  if (!isOpen) return null;

  const isIndividual = userRole === 'individual';

  const individualMenuItems = [
    { id: 'dashboard', label: 'My Health Vault (ABHA)', icon: HeartPulse },
    { id: 'risk-assessment', label: '12-Q Symptom & Risk Assessment', icon: PlusSquare },
    { id: 'ocr', label: 'Scan Rx & Lab OCR', icon: Camera },
    { id: 'ai-assistant', label: 'Sehat Setu AI Assistant', icon: Bot },
    { id: 'sos', label: '108 Emergency & Nearest Doctor', icon: Asterisk, isDanger: true },
  ];

  const ashaMenuItems = [
    { id: 'dashboard', label: 'ASHA Home Dashboard', icon: LayoutGrid },
    { id: 'patients', label: 'Beneficiary Register (Patients)', icon: Users },
    { id: 'records', label: 'Clinical Health Records', icon: FolderMinus },
    { id: 'risk-assessment', label: '12-Q Symptom & Risk Assessment', icon: PlusSquare },
    { id: 'ai-assistant', label: 'Sehat Setu AI CDSS', icon: Bot },
    { id: 'ocr', label: 'Rx & Lab OCR Scanner', icon: Camera },
    { 
      id: 'sos', 
      label: '108 CareSync Emergency Dispatch', 
      icon: Asterisk, 
      isDanger: true 
    },
    { id: 'emergency', label: 'Ward Alerts & Notifications', icon: Bell },
  ];

  const menuItems = isIndividual ? individualMenuItems : ashaMenuItems;

  const secondaryItems = [
    { id: 'settings', label: 'Settings & ABDM Vault', icon: Settings, action: () => onNavigate('settings') },
    { id: 'signout', label: 'Sign Out / Switch Mode', icon: LogOut, action: onSignOut },
  ];

  return (
    <div className="absolute inset-0 z-50 flex">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-black/60 backdrop-blur-xs transition-opacity"
        onClick={onClose}
      />

      {/* Drawer Body */}
      <div className="relative w-[82%] max-w-[320px] h-full bg-white border-r border-slate-300 shadow-2xl flex flex-col z-10 animate-in slide-in-from-left duration-200">
        
        {/* Official Government Drawer Header */}
        <div className="p-4 bg-slate-50 border-b-2 border-slate-900 text-slate-900 shrink-0 relative">
          {/* Tricolor Stripe */}
          <div className="h-1 w-full flex overflow-hidden mb-3 rounded-full">
            <div className="w-1/3 bg-[#FF9933]"></div>
            <div className="w-1/3 bg-white border-y border-slate-200"></div>
            <div className="w-1/3 bg-[#138808]"></div>
          </div>

          <button 
            onClick={onClose}
            className="absolute top-4 right-3 p-1 rounded-full text-slate-500 hover:text-black hover:bg-slate-200 cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>

          {/* Official CareSync Logo */}
          <div className="flex items-center space-x-2.5 mb-2.5">
            <CareSyncLogo size={36} className="rounded-xl shadow-xs" />
            <div>
              <div className="flex items-center gap-1.5">
                <h3 className="font-black text-base text-[#0F172A] leading-tight">CareSync</h3>
                <span className="text-[9px] font-black px-1.5 py-0.2 rounded-xs bg-black text-white border border-black uppercase">
                  {isIndividual ? 'Citizen' : 'NHM Govt'}
                </span>
              </div>
              <p className="text-[9px] font-bold text-slate-600 leading-none">
                Ministry of Health & Family Welfare · ABDM
              </p>
            </div>
          </div>

          {/* User Details */}
          <div className="pt-2 border-t border-slate-200">
            <div className="flex items-center justify-between">
              <h4 className="font-black text-xs text-[#0F172A]">
                {userProfile?.name || (isIndividual ? 'Ramesh Sharma' : 'Dr. Priya Kulkarni')}
              </h4>
              <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-emerald-50 text-emerald-800 border border-emerald-300">
                ABDM Verified
              </span>
            </div>
            <p className="text-[10px] text-slate-600 font-medium">
              {isIndividual 
                ? (userProfile?.ward || 'Ward 4 · Shanti Nagar, Rampur') 
                : (userProfile?.designation || 'Medical Officer · PHC Rampur')}
            </p>
            <p className="text-[9px] text-slate-900 font-mono font-bold mt-0.5">
              {isIndividual 
                ? `ABHA: ${userProfile?.abhaId || '91-8842-1092-4412'}` 
                : 'Govt Reg: NHM-DEL-2026-88102'}
            </p>
          </div>
        </div>

        {/* Drawer Menu Items */}
        <div className="flex-1 overflow-y-auto py-3 px-2 space-y-1 text-slate-700 text-xs">
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
                    ? 'text-red-700 hover:bg-red-50' 
                    : 'text-slate-700 hover:bg-slate-100 hover:text-black'
                }`}
              >
                <Icon className={`w-4 h-4 ${isDanger ? 'text-red-600 stroke-[2.5]' : 'text-slate-600'}`} />
                <span className={`text-[12px] font-bold ${isDanger ? 'text-red-700' : 'text-slate-800'}`}>
                  {item.label}
                </span>
              </button>
            );
          })}

          <div className="my-2 border-t border-slate-200"></div>

          {secondaryItems.map((item) => {
            const Icon = item.icon;
            return (
              <button
                key={item.id}
                onClick={() => {
                  item.action();
                  onClose();
                }}
                className="w-full flex items-center space-x-3.5 px-3 py-2.5 rounded-xl text-left text-slate-600 hover:bg-slate-100 hover:text-black cursor-pointer transition-colors font-bold"
              >
                <Icon className="w-4 h-4 text-slate-500" />
                <span className="text-[12px]">{item.label}</span>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
};
