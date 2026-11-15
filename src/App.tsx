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
import { RecentActivityView } from './components/RecentActivityView';
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
  const [showRecentActivity, setShowRecentActivity] = useState<boolean>(false);

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
    <div className="min-h-screen w-full bg-[#F1F5F9] flex flex-col items-center justify-start py-6 px-2 sm:px-4 select-none font-sans text-slate-800 overflow-y-auto">
      {/* Top Banner / Studio Control Header */}
      <div className="text-center mb-4 hidden sm:flex flex-col items-center">
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-[#15803D] animate-pulse"></span>
          <h1 className="text-sm font-black text-[#0F172A] tracking-wide">
            CareSync Mobile System Preview (Official Government Light Mode)
          </h1>
          <span className="text-[10px] px-2 py-0.5 rounded-full bg-slate-200 text-slate-900 border border-slate-300 font-bold">
            Android 14 · ABDM Standard
          </span>
        </div>
        <p className="text-[11px] font-bold text-slate-600 mt-0.5">
          100% Interactive Demo Prototype · National Health Mission & MoHFW CDSS Engine
        </p>

        {/* View Switchers & Phone Size Controls */}
        <div className="flex flex-wrap items-center justify-center gap-2 mt-2.5">
          <button
            onClick={() => {
              setIsAuthenticated(false);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              !isAuthenticated
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
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
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && !isRegisteringPatient && currentTab === 'dashboard'
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
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
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              selectedPatient
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
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
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && currentTab === 'emergency'
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
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
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && currentTab === 'risk-assessment'
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
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
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && isRegisteringPatient
                ? 'bg-[#15803D] text-white border-[#15803D] shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
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
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && !isRegisteringPatient && currentTab === 'ai-assistant'
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
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
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && !selectedPatient && currentTab === 'sos'
                ? 'bg-red-600 text-white border-red-600 shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
            }`}
          >
            🚨 108 Emergency
          </button>
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setShowRecentActivity(true);
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              showRecentActivity
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
            }`}
          >
            📋 Recent Activity
          </button>
          <button
            onClick={() => setIsDrawerOpen(true)}
            className="px-2.5 py-1 rounded-lg text-[11px] font-black bg-white text-slate-700 border border-slate-300 hover:bg-slate-50 cursor-pointer shadow-xs"
          >
            ☰ Drawer
          </button>

          {/* Length / Height selector */}
          <div className="flex items-center gap-1 bg-white p-0.5 rounded-lg border border-slate-300 ml-2 shadow-xs">
            <span className="text-[10px] text-slate-600 px-1.5 font-bold">Size:</span>
            <button
              onClick={() => setPhoneHeight('standard')}
              className={`px-2 py-0.5 rounded text-[10px] font-black cursor-pointer ${
                phoneHeight === 'standard' ? 'bg-black text-white' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Standard
            </button>
            <button
              onClick={() => setPhoneHeight('long')}
              className={`px-2 py-0.5 rounded text-[10px] font-black cursor-pointer ${
                phoneHeight === 'long' ? 'bg-black text-white' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Long
            </button>
            <button
              onClick={() => setPhoneHeight('xl')}
              className={`px-2 py-0.5 rounded text-[10px] font-black cursor-pointer ${
                phoneHeight === 'xl' ? 'bg-black text-white' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Extra Long
            </button>
          </div>
        </div>
      </div>

      {/* Android Device Outer Chassis - Clean Premium Device Frame */}
      <div className={`relative ${phoneDimensions[phoneHeight]} bg-[#E2E8F0] rounded-[52px] p-3 shadow-2xl border-[6px] border-[#CBD5E1] flex flex-col overflow-hidden ring-1 ring-slate-300 transition-all duration-300`}>
        
        {/* Device Camera Punch Hole (Center Top) */}
        <div className="absolute top-4 left-1/2 -translate-x-1/2 z-50 flex items-center justify-center pointer-events-none">
          <div className="w-4 h-4 bg-slate-800 rounded-full border border-slate-400 flex items-center justify-center">
            <div className="w-1.5 h-1.5 rounded-full bg-slate-900"></div>
          </div>
        </div>

        {/* Screen Viewport - Pure Clean Light Mode Background */}
        <div className="w-full h-full bg-[#F8FAFC] rounded-[42px] overflow-hidden flex flex-col relative border border-slate-300">
          
          {/* Android Status Bar */}
          <div className="h-9 w-full px-6 flex items-center justify-between text-[11px] font-black text-slate-800 z-40 bg-[#F8FAFC] select-none shrink-0 pt-1">
            <div className="flex items-center space-x-2">
              <span className="font-black tracking-tight">8:00</span>
              <span className="text-[10px] opacity-70">🔔</span>
            </div>
            <div className="flex items-center space-x-1.5 text-slate-800">
              <span className="text-[10px] font-black">5G</span>
              <Signal className="w-3 h-3 text-slate-800 fill-slate-800" />
              <div className="flex items-center gap-0.5 px-1 py-0.2 rounded-full border border-slate-400 text-[9px] font-black">
                <span>94%</span>
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
            ) : showRecentActivity ? (
              <RecentActivityView
                onBack={() => setShowRecentActivity(false)}
                onSelectPatient={(name) => {
                  const matched = patients.find(p => p.fullName.toLowerCase().includes(name.toLowerCase())) || patients[0];
                  setShowRecentActivity(false);
                  setSelectedPatient(matched);
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
                    onNavigateTab={(tab) => {
                      setShowRecentActivity(false);
                      setCurrentTab(tab);
                    }}
                    onSelectPatient={setSelectedPatient}
                    onOpenAddModal={() => setIsRegisteringPatient(true)}
                    onOpenSosModal={handleOpenGeneralSos}
                    onOpenDrawer={() => setIsDrawerOpen(true)}
                    onOpenRecentActivity={() => setShowRecentActivity(true)}
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

          {/* Android Bottom Navigation Bar (Hidden on Sign-In screen & Patient Profile) - Clean Light Mode */}
          {isAuthenticated && !isRegisteringPatient && !selectedPatient && (
            <div className="h-16 w-full bg-white border-t border-slate-200 flex items-center justify-around px-2 shrink-0 z-40 select-none shadow-xs">
              {bottomTabs.map((btn) => {
                const Icon = btn.icon;
                const isDanger = btn.isDanger;
                const isActive = currentTab === btn.id;

                return (
                  <button
                    key={btn.id}
                    onClick={() => {
                      setShowRecentActivity(false);
                      setCurrentTab(btn.id);
                    }}
                    className="flex flex-col items-center justify-center flex-1 h-full py-1 cursor-pointer transition-all"
                  >
                    {/* Active capsule - Clean Govt Light Theme with Solid Black */}
                    <div className={`px-4 py-1 rounded-full transition-all ${
                      isActive && isDanger
                        ? 'bg-red-50 text-red-700 border border-red-300'
                        : isActive
                        ? 'bg-black text-white border-black shadow-xs'
                        : isDanger
                        ? 'text-red-600 hover:text-red-700'
                        : 'text-slate-500 hover:text-slate-800'
                    }`}>
                      <Icon className={`w-4 h-4 ${isDanger ? 'stroke-[2.5]' : ''}`} />
                    </div>
                    <span className={`text-[10px] mt-0.5 font-bold ${
                      isDanger
                        ? 'text-red-600'
                        : isActive
                        ? 'text-black font-black'
                        : 'text-slate-500'
                    }`}>
                      {btn.label}
                    </span>
                  </button>
                );
              })}
            </div>
          )}

          {/* Android Gesture Bar */}
          <div className="h-4 w-full bg-white flex items-center justify-center shrink-0 border-t border-slate-100">
            <div className="w-32 h-1 bg-slate-300 rounded-full"></div>
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
