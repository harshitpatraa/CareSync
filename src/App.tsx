import React, { useState } from 'react';
import { 
  LayoutGrid, 
  Users, 
  Sparkles, 
  Bell, 
  Asterisk,
  Signal,
  Wifi,
  Battery,
  ShieldCheck,
  CheckCircle2,
  Share2
} from 'lucide-react';
import { AppProvider, useApp } from './context/AppContext';
import { DashboardView } from './components/DashboardView';
import { PatientListView } from './components/PatientListView';
import { AiAssistantView } from './components/AiAssistantView';
import { RiskAssessmentView } from './components/RiskAssessmentView';
import { RecordsView } from './components/RecordsView';
import { AnalyticsView } from './components/AnalyticsView';
import { EmergencyView } from './components/EmergencyView';
import { SettingsView } from './components/SettingsView';
import { NotificationsView } from './components/NotificationsView';
import { PatientProfileView } from './components/PatientProfileView';
import { PatientRegistrationView } from './components/PatientRegistrationView';
import { SosView } from './components/SosView';
import { ClinicianSignInView } from './components/ClinicianSignInView';
import { NavigationDrawer } from './components/NavigationDrawer';
import { PatientDetailModal } from './components/PatientDetailModal';
import { SosBroadcastModal } from './components/SosBroadcastModal';
import { Patient } from './types';

const MobileDeviceApp: React.FC = () => {
  const { patients } = useApp();
  // Navigation & View States
  const [currentTab, setCurrentTab] = useState<string>('dashboard');
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(true);
  const [isDrawerOpen, setIsDrawerOpen] = useState<boolean>(false);
  const [isRegisteringPatient, setIsRegisteringPatient] = useState<boolean>(false);

  // Modals
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [showSosModal, setShowSosModal] = useState<boolean>(false);
  const [sosPatient, setSosPatient] = useState<Patient | null>(null);
  const [phoneHeight, setPhoneHeight] = useState<'long' | 'xl' | 'standard'>('long');

  const handleOpenSosForPatient = (patient: Patient) => {
    setSosPatient(patient);
    setShowSosModal(true);
  };

  const handleOpenGeneralSos = () => {
    setSosPatient(null);
    setShowSosModal(true);
  };

  // Bottom Navigation Items matching Screenshot 1 & 2
  const bottomTabs = [
    { id: 'dashboard', label: 'Home', icon: LayoutGrid },
    { id: 'patients', label: 'Patients', icon: Users },
    { id: 'ai-assistant', label: 'AI Care', icon: Sparkles },
    { id: 'emergency', label: 'Alerts', icon: Bell },
    { id: 'sos', label: 'SOS', icon: Asterisk, isDanger: true },
  ];

  const phoneDimensions = {
    standard: 'w-full max-w-[420px] h-[890px]',
    long: 'w-full max-w-[440px] h-[980px]',
    xl: 'w-full max-w-[460px] h-[1080px]',
  };

  return (
    <div className="min-h-screen w-full bg-[#07090e] flex flex-col items-center justify-start py-6 px-2 sm:px-4 select-none font-sans text-slate-100 overflow-y-auto">
      {/* Top Banner / Studio Control Header */}
      <div className="text-center mb-4 hidden sm:flex flex-col items-center">
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
          <h1 className="text-sm font-bold text-white tracking-wide">
            CareSync Mobile System Preview
          </h1>
          <span className="text-[10px] px-2 py-0.5 rounded-full bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
            Android 14 (Pixel 8)
          </span>
        </div>
        <p className="text-[11px] text-slate-400 mt-0.5">
          100% Interactive Demo Prototype · Live MoHFW Clinical Intelligence & Offline-First Room DB
        </p>

        {/* View Switchers & Phone Size Controls */}
        <div className="flex flex-wrap items-center justify-center gap-2 mt-2.5">
          <button
            onClick={() => {
              setIsAuthenticated(false);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold border transition-all cursor-pointer ${
              !isAuthenticated
                ? 'bg-purple-600 text-white border-purple-500 shadow-xs'
                : 'bg-slate-900 text-slate-400 border-slate-800 hover:text-white'
            }`}
          >
            🔑 Sign-In
          </button>
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('dashboard');
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && !isRegisteringPatient && currentTab === 'dashboard'
                ? 'bg-blue-600 text-white border-blue-500 shadow-xs'
                : 'bg-slate-900 text-slate-400 border-slate-800 hover:text-white'
            }`}
          >
            🏠 Dashboard
          </button>
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setIsRegisteringPatient(false);
              setSelectedPatient(patients[0] || {
                id: 'pt-1003',
                fullName: 'Harish Verma',
                age: 62,
                gender: 'Male',
                phone: '+91 98765 43210',
                villageOrWard: 'Ward 7 · Old Mill Colony',
                bloodGroup: 'A+',
                category: 'CHRONIC',
                riskLevel: 'CRITICAL',
                isCritical: true,
                isTodayVisit: true,
                isPendingVisit: false,
                syncStatus: 'PENDING_UPLOAD',
                lastVisitLabel: 'Today, 10:05 AM',
                nextFollowUpDate: 'Tomorrow, 9:00 AM',
                allergies: 'Penicillin',
                chronicConditions: 'Hypertension, Diabetes Type 2',
                familyHeadName: 'Harish Verma',
                familyMembersCount: 4,
                vaccinationProgress: 80,
                notes: 'Critical SpO2 alert and high systolic BP.',
                vitals: {
                  id: 'vt-1',
                  patientId: 'pt-1003',
                  systolicBp: 164,
                  diastolicBp: 98,
                  heartRate: 112,
                  spo2Percent: 90,
                  temperatureC: 37.3,
                  bloodSugarMgDl: 214,
                  recordedAt: Date.now()
                }
              });
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold border transition-all cursor-pointer ${
              selectedPatient
                ? 'bg-rose-600 text-white border-rose-500 shadow-xs'
                : 'bg-slate-900 text-slate-400 border-slate-800 hover:text-white'
            }`}
          >
            👤 Patient Profile
          </button>
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('emergency');
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && currentTab === 'emergency'
                ? 'bg-amber-600 text-white border-amber-500 shadow-xs'
                : 'bg-slate-900 text-slate-400 border-slate-800 hover:text-white'
            }`}
          >
            🔔 Notifications
          </button>
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('risk-assessment');
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && currentTab === 'risk-assessment'
                ? 'bg-purple-600 text-white border-purple-500 shadow-xs'
                : 'bg-slate-900 text-slate-400 border-slate-800 hover:text-white'
            }`}
          >
            🩺 Medical Assessment
          </button>
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(true);
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold border transition-all cursor-pointer ${
              isAuthenticated && isRegisteringPatient
                ? 'bg-emerald-600 text-white border-emerald-500 shadow-xs'
                : 'bg-slate-900 text-slate-400 border-slate-800 hover:text-white'
            }`}
          >
            📝 Registration
          </button>
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('ai-assistant');
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && !isRegisteringPatient && currentTab === 'ai-assistant'
                ? 'bg-purple-600 text-white border-purple-500 shadow-xs'
                : 'bg-slate-900 text-slate-400 border-slate-800 hover:text-white'
            }`}
          >
            🤖 AI Chat
          </button>
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('sos');
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && currentTab === 'sos'
                ? 'bg-rose-600 text-white border-rose-500 shadow-xs'
                : 'bg-slate-900 text-slate-400 border-slate-800 hover:text-white'
            }`}
          >
            🚨 Emergency SOS
          </button>
          <button
            onClick={() => setIsDrawerOpen(true)}
            className="px-2.5 py-1 rounded-lg text-[11px] font-semibold bg-slate-900 text-slate-300 border border-slate-800 hover:border-slate-700 cursor-pointer"
          >
            ☰ Drawer
          </button>

          {/* Length / Height selector */}
          <div className="flex items-center gap-1 bg-slate-900/90 p-0.5 rounded-lg border border-slate-800 ml-2">
            <span className="text-[10px] text-slate-400 px-1.5 font-medium">Phone Size:</span>
            <button
              onClick={() => setPhoneHeight('standard')}
              className={`px-2 py-0.5 rounded text-[10px] font-semibold cursor-pointer ${
                phoneHeight === 'standard' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'
              }`}
            >
              Standard
            </button>
            <button
              onClick={() => setPhoneHeight('long')}
              className={`px-2 py-0.5 rounded text-[10px] font-semibold cursor-pointer ${
                phoneHeight === 'long' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'
              }`}
            >
              Long (Default)
            </button>
            <button
              onClick={() => setPhoneHeight('xl')}
              className={`px-2 py-0.5 rounded text-[10px] font-semibold cursor-pointer ${
                phoneHeight === 'xl' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'
              }`}
            >
              Extra Long
            </button>
          </div>
        </div>
      </div>

      {/* Android Device Outer Chassis */}
      <div className={`relative ${phoneDimensions[phoneHeight]} bg-[#0c1017] rounded-[52px] p-3 shadow-[0_0_80px_-20px_rgba(99,102,241,0.35)] border-[6px] border-[#1e293b] flex flex-col overflow-hidden ring-1 ring-slate-700/50 transition-all duration-300`}>
        
        {/* Device Camera Punch Hole (Center Top) */}
        <div className="absolute top-4 left-1/2 -translate-x-1/2 z-50 flex items-center justify-center pointer-events-none">
          <div className="w-4 h-4 bg-black rounded-full border border-slate-800 flex items-center justify-center">
            <div className="w-1.5 h-1.5 rounded-full bg-blue-950/80"></div>
          </div>
        </div>

        {/* Screen Viewport */}
        <div className="w-full h-full bg-[#0b0f17] rounded-[42px] overflow-hidden flex flex-col relative border border-slate-800/80">
          
          {/* Android Status Bar (Exact replica of screenshot: 8:00, Snapchat, 5G, 33%) */}
          <div className="h-9 w-full px-6 flex items-center justify-between text-[11px] font-semibold text-slate-300 z-40 bg-[#0b0f17] select-none shrink-0 pt-1">
            <div className="flex items-center space-x-2">
              <span className="font-bold tracking-tight">8:00</span>
              {/* Ghost Snapchat / Notification indicator */}
              <span className="text-[10px] opacity-80">👻</span>
            </div>
            <div className="flex items-center space-x-1.5 text-slate-300">
              <span className="text-[10px] opacity-70">🔕</span>
              <span className="text-[10px] font-bold">5G</span>
              <Signal className="w-3 h-3 text-slate-300 fill-slate-300" />
              <div className="flex items-center gap-0.5 px-1 py-0.2 rounded-full border border-slate-600 text-[9px] font-bold">
                <span>33</span>
              </div>
            </div>
          </div>

          {/* Navigation Drawer Overlay */}
          <NavigationDrawer
            isOpen={isDrawerOpen}
            onClose={() => setIsDrawerOpen(false)}
            onNavigate={(tab) => {
              setIsRegisteringPatient(false);
              setCurrentTab(tab);
            }}
            onSignOut={() => {
              setIsAuthenticated(false);
              setIsDrawerOpen(false);
            }}
          />

          {/* Scrollable Screen Content */}
          <div className="flex-1 overflow-y-auto px-4 py-2 text-xs relative">
            {!isAuthenticated ? (
              <ClinicianSignInView
                onSignInSuccess={() => {
                  setIsAuthenticated(true);
                  setCurrentTab('dashboard');
                }}
              />
            ) : selectedPatient ? (
              <PatientProfileView
                patient={selectedPatient}
                onBack={() => setSelectedPatient(null)}
                onOpenSos={handleOpenSosForPatient}
                onOpenRiskAssessment={() => {
                  setSelectedPatient(null);
                  setCurrentTab('risk-assessment');
                }}
              />
            ) : isRegisteringPatient ? (
              <PatientRegistrationView
                onBack={() => setIsRegisteringPatient(false)}
                onSuccess={() => {
                  setIsRegisteringPatient(false);
                  setCurrentTab('patients');
                }}
              />
            ) : (
              <>
                {currentTab === 'dashboard' && (
                  <DashboardView
                    onNavigateTab={setCurrentTab}
                    onSelectPatient={setSelectedPatient}
                    onOpenAddModal={() => setIsRegisteringPatient(true)}
                    onOpenSosModal={handleOpenGeneralSos}
                    onOpenDrawer={() => setIsDrawerOpen(true)}
                  />
                )}

                {currentTab === 'patients' && (
                  <PatientListView
                    onSelectPatient={setSelectedPatient}
                    onOpenAddModal={() => setIsRegisteringPatient(true)}
                    onOpenSosModal={handleOpenSosForPatient}
                  />
                )}

                {currentTab === 'ai-assistant' && (
                  <AiAssistantView
                    onBack={() => setCurrentTab('dashboard')}
                    onOpenDrawer={() => setIsDrawerOpen(true)}
                  />
                )}

                {currentTab === 'risk-assessment' && (
                  <RiskAssessmentView onBack={() => setCurrentTab('dashboard')} />
                )}

                {currentTab === 'records' && (
                  <RecordsView />
                )}

                {currentTab === 'analytics' && (
                  <AnalyticsView />
                )}

                {currentTab === 'emergency' && (
                  <NotificationsView
                    onOpenDrawer={() => setIsDrawerOpen(true)}
                    onSelectPatient={(name) => {
                      const matched = patients.find(p => p.fullName.toLowerCase().includes(name.toLowerCase())) || patients[0];
                      setSelectedPatient(matched);
                    }}
                  />
                )}

                {currentTab === 'sos' && (
                  <SosView
                    onOpenDrawer={() => setIsDrawerOpen(true)}
                    onSelectPatient={(p) => setSelectedPatient(p)}
                  />
                )}

                {currentTab === 'settings' && (
                  <SettingsView />
                )}
              </>
            )}
          </div>

          {/* Android Bottom Navigation Bar (Hidden on Sign-In screen & Patient Profile) */}
          {isAuthenticated && !isRegisteringPatient && !selectedPatient && (
            <div className="h-16 w-full bg-[#0d131f] border-t border-slate-800/80 flex items-center justify-around px-2 shrink-0 z-40 select-none">
              {bottomTabs.map((btn) => {
                const Icon = btn.icon;
                const isDanger = btn.isDanger;
                const isActive = currentTab === btn.id;

                return (
                  <button
                    key={btn.id}
                    onClick={() => {
                      setCurrentTab(btn.id);
                    }}
                    className="flex flex-col items-center justify-center flex-1 h-full py-1 cursor-pointer transition-all"
                  >
                    {/* Active capsule for all tabs including SOS (as in screenshot) */}
                    <div className={`px-4 py-1 rounded-full transition-all ${
                      isActive && isDanger
                        ? 'bg-[#153424] text-red-500 border border-[#22c55e]/30'
                        : isActive
                        ? 'bg-[#153424] text-[#4ade80] border border-[#22c55e]/30'
                        : isDanger
                        ? 'text-red-500'
                        : 'text-slate-400 hover:text-slate-200'
                    }`}>
                      <Icon className={`w-4 h-4 ${isDanger ? 'stroke-[2.5]' : ''}`} />
                    </div>
                    <span className={`text-[10px] mt-0.5 font-medium ${
                      isDanger
                        ? 'font-bold text-red-500'
                        : isActive
                        ? 'font-bold text-white'
                        : 'text-slate-400'
                    }`}>
                      {btn.label}
                    </span>
                  </button>
                );
              })}
            </div>
          )}

          {/* Android Gesture Bar */}
          <div className="h-4 w-full bg-[#0d131f] flex items-center justify-center shrink-0">
            <div className="w-32 h-1 bg-white/70 rounded-full"></div>
          </div>
        </div>
      </div>

      {/* SOS Broadcast Modal */}
      {showSosModal && (
        <SosBroadcastModal
          initialPatient={sosPatient}
          onClose={() => {
            setShowSosModal(false);
            setSosPatient(null);
          }}
        />
      )}
    </div>
  );
};

export default function App() {
  return (
    <AppProvider>
      <MobileDeviceApp />
    </AppProvider>
  );
}
