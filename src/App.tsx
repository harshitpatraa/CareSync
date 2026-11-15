import React, { useState } from 'react';
import { 
  LayoutGrid, 
  Users, 
  Sparkles, 
  Bell, 
  Asterisk,
  Signal,
  ShieldCheck,
  Camera,
  HeartPulse,
  IdCard,
  Activity
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
import { ClinicianSignInView, UserRole } from './components/ClinicianSignInView';
import { IndividualHomeView } from './components/IndividualHomeView';
import { OcrScannerView } from './components/OcrScannerView';
import { NavigationDrawer } from './components/NavigationDrawer';
import { SosBroadcastModal } from './components/SosBroadcastModal';
import { Patient } from './types';

const MobileDeviceApp: React.FC = () => {
  const { patients } = useApp();

  // Role & Authentication States
  // Starts with false so the Login/Register menu is seen on start of app as requested
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [userRole, setUserRole] = useState<UserRole>('individual');
  const [userProfile, setUserProfile] = useState<any>({
    name: 'Ramesh Sharma',
    abhaId: '91-8842-1092-4412',
    ward: 'Ward 4 · Shanti Nagar, Rampur',
    phone: '+91 98231 44102'
  });

  // Navigation & View States
  const [currentTab, setCurrentTab] = useState<string>('dashboard');
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

  const handleSignInSuccess = (role: UserRole, profile?: any) => {
    setUserRole(role);
    if (profile) setUserProfile(profile);
    setIsAuthenticated(true);
    setCurrentTab('dashboard');
    setSelectedPatient(null);
    setIsRegisteringPatient(false);
  };

  // Role-Separated Bottom Tabs
  const individualBottomTabs = [
    { id: 'dashboard', label: 'My Health', icon: HeartPulse },
    { id: 'risk-assessment', label: 'Risk Check', icon: Activity },
    { id: 'ocr', label: 'Scan Rx', icon: Camera },
    { id: 'ai-assistant', label: 'AI Health', icon: Sparkles },
    { id: 'sos', label: '108 SOS', icon: Asterisk, isDanger: true },
  ];

  const ashaBottomTabs = [
    { id: 'dashboard', label: 'Home', icon: LayoutGrid },
    { id: 'patients', label: 'Patients', icon: Users },
    { id: 'ai-assistant', label: 'AI Care', icon: Sparkles },
    { id: 'emergency', label: 'Alerts', icon: Bell },
    { id: 'sos', label: 'SOS', icon: Asterisk, isDanger: true },
  ];

  const bottomTabs = userRole === 'individual' ? individualBottomTabs : ashaBottomTabs;

  const phoneDimensions = {
    standard: 'w-full max-w-[420px] h-[890px]',
    long: 'w-full max-w-[440px] h-[980px]',
    xl: 'w-full max-w-[460px] h-[1080px]',
  };

  return (
    <div className="min-h-screen w-full bg-[#F1F5F9] flex flex-col items-center justify-start py-6 px-2 sm:px-4 select-none font-sans text-slate-800 overflow-y-auto">
      {/* Team Innovix - Fixed Corner Watermark Outside Mobile Screen */}
      <div className="fixed top-4 right-4 z-50 flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-black text-white border-2 border-neutral-800 shadow-2xl select-none">
        <span className="w-2 h-2 rounded-full bg-[#15803D] animate-pulse"></span>
        <div className="flex items-center gap-1.5">
          <span className="text-[10px] text-neutral-400 font-bold uppercase tracking-wider">By</span>
          <span className="text-xs font-black tracking-wide text-white">Team Innovix</span>
        </div>
      </div>
      {/* Top Banner / Studio Control Header */}
      <div className="text-center mb-4 hidden sm:flex flex-col items-center">
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-[#15803D] animate-pulse"></span>
          <h1 className="text-sm font-black text-[#0F172A] tracking-wide">
            CareSync Mobile System (Role-Separated: Citizen vs ASHA Worker)
          </h1>
          <span className="text-[10px] px-2 py-0.5 rounded-full bg-slate-200 text-slate-900 border border-slate-300 font-bold">
            Android 14 · ABDM Standard
          </span>
        </div>
        <p className="text-[11px] font-bold text-slate-600 mt-0.5">
          Role-Gated Security · Direct AI Precautions · Medical Supervisor Verification Protocol
        </p>

        {/* View Switchers & Phone Size Controls */}
        <div className="flex flex-wrap items-center justify-center gap-2 mt-2.5">
          {/* Sign-In / Register Switcher */}
          <button
            onClick={() => {
              setIsAuthenticated(false);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
            }}
            className={`px-3 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              !isAuthenticated
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
            }`}
          >
            🔑 Login / Register Menu
          </button>

          {/* Citizen Mode */}
          <button
            onClick={() => {
              setUserRole('individual');
              setUserProfile({
                name: 'Ramesh Sharma',
                abhaId: '91-8842-1092-4412',
                ward: 'Ward 4 · Shanti Nagar, Rampur',
                phone: '+91 98231 44102'
              });
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('dashboard');
            }}
            className={`px-3 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && userRole === 'individual' && currentTab === 'dashboard'
                ? 'bg-[#FF9933] text-white border-[#FF9933] shadow-xs'
                : 'bg-white text-amber-900 border-amber-300 hover:bg-amber-50'
            }`}
          >
            👤 Citizen Mode (Ramesh)
          </button>

          {/* ASHA Worker Mode */}
          <button
            onClick={() => {
              setUserRole('asha_worker');
              setUserProfile({
                name: 'Dr. Priya Kulkarni',
                email: 'priya.kulkarni@nhm.gov.in',
                designation: 'Medical Officer / ASHA Supervisor',
                phc: 'Rampur Primary Health Centre'
              });
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('dashboard');
            }}
            className={`px-3 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && userRole === 'asha_worker' && currentTab === 'dashboard'
                ? 'bg-[#15803D] text-white border-[#15803D] shadow-xs'
                : 'bg-white text-emerald-900 border-emerald-300 hover:bg-emerald-50'
            }`}
          >
            👩‍⚕️ ASHA Mode (Dr. Priya)
          </button>

          {/* OCR Scanner */}
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('ocr');
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && currentTab === 'ocr'
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
            }`}
          >
            📸 Scan Rx (OCR)
          </button>

          {/* Risk Assessment Button */}
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('risk-assessment');
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && currentTab === 'risk-assessment'
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
            }`}
          >
            🩺 Risk Assessment
          </button>

          {/* AI Health Assistant */}
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('ai-assistant');
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && currentTab === 'ai-assistant'
                ? 'bg-black text-white border-black shadow-xs'
                : 'bg-white text-slate-700 border-slate-300 hover:bg-slate-50'
            }`}
          >
            🤖 AI Precautions & Rx
          </button>

          {/* 108 SOS */}
          <button
            onClick={() => {
              setIsAuthenticated(true);
              setSelectedPatient(null);
              setIsRegisteringPatient(false);
              setCurrentTab('sos');
            }}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-black border transition-all cursor-pointer ${
              isAuthenticated && currentTab === 'sos'
                ? 'bg-red-600 text-white border-red-600 shadow-xs'
                : 'bg-white text-red-700 border-red-300 hover:bg-red-50'
            }`}
          >
            🚨 108 SOS
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
              Std
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
              XL
            </button>
          </div>
        </div>
      </div>

      {/* Phone Chassis Wrapper with Outside Corner Branding */}
      <div className="relative">
        {/* Team Innovix Floating Badge - At Corner Outside Mobile Screen */}
        <div className="absolute -bottom-3 -right-3 z-40 flex items-center gap-1.5 px-3 py-1 rounded-full bg-black text-white border-2 border-neutral-700 shadow-xl font-black text-[11px] select-none tracking-wide">
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
          <span>Team Innovix</span>
        </div>

        {/* Android Device Outer Chassis - Solid Black Phone Frame & Border */}
        <div className={`relative ${phoneDimensions[phoneHeight]} bg-black rounded-[54px] p-3 shadow-2xl border-[10px] border-black flex flex-col overflow-hidden ring-2 ring-neutral-900 transition-all duration-300`}>
        
        {/* Device Camera Punch Hole (Center Top) */}
        <div className="absolute top-4 left-1/2 -translate-x-1/2 z-50 flex items-center justify-center pointer-events-none">
          <div className="w-4 h-4 bg-black rounded-full border border-neutral-800 flex items-center justify-center">
            <div className="w-1.5 h-1.5 rounded-full bg-neutral-950"></div>
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
            userRole={userRole}
            userProfile={userProfile}
            onNavigate={(tab) => {
              setIsRegisteringPatient(false);
              setShowRecentActivity(false);
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
                onSignInSuccess={handleSignInSuccess}
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
                {/* 1. CITIZEN / INDIVIDUAL SPECIFIC VIEWS */}
                {userRole === 'individual' ? (
                  <>
                    {currentTab === 'dashboard' && (
                      <IndividualHomeView
                        onNavigateTab={(tab) => {
                          setShowRecentActivity(false);
                          setCurrentTab(tab);
                        }}
                        onOpenDrawer={() => setIsDrawerOpen(true)}
                        onOpenSosModal={handleOpenGeneralSos}
                        citizenName={userProfile?.name || 'Ramesh Sharma'}
                        abhaId={userProfile?.abhaId || '91-8842-1092-4412'}
                      />
                    )}

                    {currentTab === 'ocr' && (
                      <OcrScannerView
                        onBack={() => setCurrentTab('dashboard')}
                        onOpenDrawer={() => setIsDrawerOpen(true)}
                        onNavigateToAi={(query) => {
                          setCurrentTab('ai-assistant');
                        }}
                        userRole="individual"
                      />
                    )}

                    {currentTab === 'risk-assessment' && (
                      <RiskAssessmentView onBack={() => setCurrentTab('dashboard')} />
                    )}

                    {currentTab === 'ai-assistant' && (
                      <AiAssistantView
                        onBack={() => setCurrentTab('dashboard')}
                        onOpenDrawer={() => setIsDrawerOpen(true)}
                        userRole="individual"
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
                ) : (
                  /* 2. ASHA WORKER CLINICAL SUITE VIEWS */
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
                        userRole="asha_worker"
                      />
                    )}

                    {currentTab === 'ocr' && (
                      <OcrScannerView
                        onBack={() => setCurrentTab('dashboard')}
                        onOpenDrawer={() => setIsDrawerOpen(true)}
                        onNavigateToAi={(query) => {
                          setCurrentTab('ai-assistant');
                        }}
                        userRole="asha_worker"
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
              </>
            )}
          </div>

          {/* Android Bottom Navigation Bar (Hidden on Sign-In screen & Patient Profile) */}
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
