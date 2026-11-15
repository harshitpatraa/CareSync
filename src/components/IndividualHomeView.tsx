import React, { useState } from 'react';
import { 
  Menu, 
  Bell, 
  Camera, 
  Asterisk, 
  Sparkles, 
  FileText, 
  ShieldCheck, 
  QrCode, 
  HeartPulse, 
  Pill, 
  Calendar, 
  ChevronRight, 
  Phone, 
  UserCheck, 
  AlertTriangle,
  Clock,
  Plus,
  CheckCircle2,
  Activity,
  MapPin,
  CalendarCheck,
  Ambulance,
  IdCard,
  User,
  ShieldAlert,
  Download
} from 'lucide-react';
import { useApp } from '../context/AppContext';

interface IndividualHomeViewProps {
  onNavigateTab: (tab: string) => void;
  onOpenDrawer?: () => void;
  onOpenSosModal?: () => void;
  citizenName?: string;
  abhaId?: string;
}

export const IndividualHomeView: React.FC<IndividualHomeViewProps> = ({
  onNavigateTab,
  onOpenDrawer,
  onOpenSosModal,
  citizenName = 'Ramesh Sharma',
  abhaId = '91-8842-1092-4412'
}) => {
  const [showQrModal, setShowQrModal] = useState<boolean>(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

  const recentBookings = [
    {
      id: 'bk-101',
      title: 'OPD Doctor Consultation (General Medicine)',
      doctor: 'Dr. Arvind Mehta (MBBS, MD)',
      facility: 'Primary Health Centre (PHC) Rampur Sector 4',
      dateTime: 'Tomorrow, 10:30 AM',
      token: 'Token #14',
      status: 'CONFIRMED',
      statusColor: 'bg-emerald-100 text-emerald-950 border-emerald-300'
    },
    {
      id: 'bk-102',
      title: 'Diagnostic Lab: Fasting Blood Sugar & Lipid Profile',
      doctor: 'Central Pathology Team',
      facility: 'ABDM Diagnostic Centre · Ward 4, Rampur',
      dateTime: '12 Nov 2026, 08:00 AM',
      token: 'Report Synced',
      status: 'COMPLETED',
      statusColor: 'bg-slate-100 text-slate-800 border-slate-300'
    },
    {
      id: 'bk-103',
      title: 'Mission Indradhanush Family Vaccine Camp',
      doctor: 'ANM Sunita Devi',
      facility: 'Health Sub-Centre, Shanti Nagar',
      dateTime: '18 Nov 2026, 11:00 AM',
      token: 'Token #08',
      status: 'SCHEDULED',
      statusColor: 'bg-amber-100 text-amber-950 border-amber-300'
    }
  ];

  const recentSosDispatches = [
    {
      id: 'sos-991',
      title: '108 Emergency Ambulance Dispatch #EM-8821',
      dateTime: '04 Nov 2026, 02:15 PM',
      reason: 'Acute Febrile Illness & SpO2 Drop (92%)',
      attending: 'Dr. Arvind Mehta (PHC Rampur)',
      response: 'Resolved · 8 mins response time',
      status: 'RESOLVED',
      statusBadge: 'Resolved & Safe'
    }
  ];

  return (
    <div className="space-y-3.5 pb-6 select-none text-slate-800">
      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white border-y border-slate-200"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Top App Bar - Individual Citizen Portal */}
      <div className="flex items-center justify-between py-1 border-b border-slate-200 pb-2">
        <div className="flex items-center space-x-2">
          {onOpenDrawer && (
            <button 
              onClick={onOpenDrawer}
              className="p-1.5 rounded-lg bg-white border border-slate-300 text-black hover:bg-slate-50 cursor-pointer shadow-xs"
            >
              <Menu className="w-5 h-5 text-black" />
            </button>
          )}

          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-black text-white flex items-center justify-center p-1 shadow-xs">
              <span className="text-base leading-none">🏛️</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="font-black text-sm text-black tracking-tight">CareSync Citizen</span>
                <span className="text-[9px] font-black px-1.5 py-0.2 rounded-xs bg-black text-white border border-black uppercase">
                  ABDM
                </span>
              </div>
              <p className="text-[9px] font-bold text-slate-600 leading-none">
                National Digital Health Mission · Personal Health Vault
              </p>
            </div>
          </div>
        </div>

        <div className="flex items-center space-x-1.5">
          <div className="px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-black flex items-center gap-1 shadow-xs">
            <ShieldCheck className="w-3 h-3 text-[#15803D]" />
            <span>Verified</span>
          </div>
          <button 
            onClick={() => onNavigateTab('emergency')}
            className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-800 hover:bg-slate-50 cursor-pointer shadow-xs"
          >
            <Bell className="w-4 h-4 text-slate-800" />
          </button>
        </div>
      </div>

      {/* Toast Alert */}
      {toastMessage && (
        <div className="p-2.5 rounded-xl bg-black text-white border border-black text-xs font-bold flex items-center justify-between shadow-lg animate-in fade-in slide-in-from-top-2">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>{toastMessage}</span>
          </div>
          <button onClick={() => setToastMessage(null)} className="text-slate-400 hover:text-white text-xs px-1">✕</button>
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* 1. OFFICIAL AYUSHMAN BHARAT CITIZEN DIGITAL HEALTH CARD   */}
      {/* ────────────────────────────────────────────────────────── */}
      <div className="p-4 rounded-2xl bg-white border-2 border-slate-900 shadow-sm space-y-3 relative overflow-hidden">
        <div className="flex justify-between items-start">
          <div className="space-y-0.5">
            <div className="flex items-center gap-1.5">
              <span className="text-[10px] font-black text-black tracking-wider uppercase">
                Ayushman Bharat · Citizen Health ID
              </span>
              <ShieldCheck className="w-3.5 h-3.5 text-[#15803D]" />
            </div>
            <h2 className="text-lg font-black text-black tracking-tight">{citizenName}</h2>
            <p className="text-xs font-mono font-bold text-black">
              ABHA Number: {abhaId}
            </p>
            <p className="text-[10px] font-bold text-slate-600">
              Age: 38y · Male · Blood Group: O+ · Ward 4, Rampur
            </p>
          </div>

          {/* QR Code Action Pill */}
          <button 
            onClick={() => setShowQrModal(!showQrModal)}
            className="w-14 h-14 rounded-xl bg-slate-100 border border-slate-300 p-1 flex flex-col items-center justify-center shadow-xs shrink-0 cursor-pointer hover:bg-slate-200 transition-colors"
          >
            <QrCode className="w-6 h-6 text-black" />
            <span className="text-[8px] font-black text-black uppercase mt-0.5">View QR</span>
          </button>
        </div>

        {/* QR Modal expand preview */}
        {showQrModal && (
          <div className="p-3 rounded-xl bg-slate-50 border border-slate-300 text-center space-y-2 animate-in fade-in-50">
            <div className="w-24 h-24 mx-auto bg-white p-1 rounded-xl border border-slate-300 flex items-center justify-center">
              <div className="w-full h-full border-2 border-black grid grid-cols-3 gap-0.5 p-1 bg-white">
                <div className="bg-black"></div>
                <div className="bg-white"></div>
                <div className="bg-black"></div>
                <div className="bg-white"></div>
                <div className="bg-black"></div>
                <div className="bg-white"></div>
                <div className="bg-black"></div>
                <div className="bg-black"></div>
                <div className="bg-black"></div>
              </div>
            </div>
            <p className="text-[10px] font-mono font-bold text-black">
              ABDM Digital Token #ABHA-DEL-918842
            </p>
          </div>
        )}

        <div className="pt-2 border-t border-slate-200 flex items-center justify-between">
          <div className="flex items-center gap-1.5 text-[11px] font-bold text-black">
            <span className="w-2 h-2 rounded-full bg-[#15803D]"></span>
            <span>Assigned PHC: Rampur Sector 4 (Dr. Arvind Mehta)</span>
          </div>

          <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-300">
            PM-JAY Active
          </span>
        </div>
      </div>

      {/* ────────────────────────────────────────────────────────── */}
      {/* 2. PERSONAL DATA & DEMOGRAPHICS (Requested by User)       */}
      {/* ────────────────────────────────────────────────────────── */}
      <div className="p-4 rounded-2xl bg-white border border-slate-300 space-y-2.5 shadow-xs">
        <div className="flex items-center justify-between border-b border-slate-100 pb-2">
          <div className="flex items-center gap-2">
            <User className="w-4 h-4 text-black" />
            <h3 className="text-xs font-black text-black">Personal Health & Identity Data</h3>
          </div>
          <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-slate-100 text-slate-800 border border-slate-300">
            ABDM Verified
          </span>
        </div>

        <div className="grid grid-cols-2 gap-2 text-xs">
          <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-[10px] text-slate-500 font-bold block">Mobile Number</span>
            <span className="text-xs font-black text-black">+91 98231 44102</span>
          </div>

          <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-[10px] text-slate-500 font-bold block">Blood Group</span>
            <span className="text-xs font-black text-black">O Positive (O+)</span>
          </div>

          <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-[10px] text-slate-500 font-bold block">PM-JAY Ayushman Card</span>
            <span className="text-xs font-black text-black font-mono">PMJAY-DEL-99104821</span>
            <span className="text-[9px] text-[#15803D] font-bold block mt-0.5">₹5,00,000 Active Cover</span>
          </div>

          <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-[10px] text-slate-500 font-bold block">Primary Emergency Contact</span>
            <span className="text-xs font-black text-black">Sunita Sharma (Wife)</span>
            <span className="text-[9px] text-slate-600 font-bold block mt-0.5">+91 98231 99011</span>
          </div>
        </div>

        <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-[11px] space-y-0.5">
          <span className="text-[10px] text-slate-500 font-bold block">Residential Address & Ward</span>
          <p className="text-xs font-black text-black">Ward 4 · Shanti Nagar, Rampur Sub-District, Delhi NCR - 110085</p>
        </div>
      </div>

      {/* ────────────────────────────────────────────────────────── */}
      {/* 3. RECENT BOOKINGS & APPOINTMENTS (Requested by User)      */}
      {/* ────────────────────────────────────────────────────────── */}
      <div className="p-4 rounded-2xl bg-white border border-slate-300 space-y-3 shadow-xs">
        <div className="flex items-center justify-between border-b border-slate-100 pb-2">
          <div className="flex items-center gap-2">
            <CalendarCheck className="w-4 h-4 text-black" />
            <h3 className="text-xs font-black text-black">My Recent Bookings & Appointments</h3>
          </div>
          <button
            type="button"
            onClick={() => showToast('Opening Ayushman Bharat PHC Appointment Scheduler...')}
            className="text-[10px] font-black px-2 py-0.5 rounded-lg bg-black text-white hover:bg-neutral-800 cursor-pointer flex items-center gap-1 shadow-xs"
          >
            <Plus className="w-3 h-3" />
            <span>Book New</span>
          </button>
        </div>

        <div className="space-y-2">
          {recentBookings.map((bk) => (
            <div 
              key={bk.id}
              className="p-3 rounded-xl bg-slate-50 border border-slate-200 hover:border-black transition-colors space-y-1"
            >
              <div className="flex items-start justify-between gap-2">
                <div>
                  <h4 className="text-xs font-black text-black">{bk.title}</h4>
                  <p className="text-[10px] font-bold text-slate-600 mt-0.5">{bk.doctor}</p>
                </div>
                <span className={`text-[9px] font-black px-2 py-0.5 rounded-md border ${bk.statusColor}`}>
                  {bk.status}
                </span>
              </div>

              <div className="flex items-center justify-between text-[10px] text-slate-600 pt-1 border-t border-slate-200">
                <span className="flex items-center gap-1 font-bold">
                  <Clock className="w-3 h-3 text-slate-500" />
                  <span>{bk.dateTime}</span>
                </span>
                <span className="font-mono font-black text-black">{bk.token}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* ────────────────────────────────────────────────────────── */}
      {/* 4. RECENT SOS & EMERGENCY DISPATCH RECORDS (Requested by User) */}
      {/* ────────────────────────────────────────────────────────── */}
      <div className="p-4 rounded-2xl bg-white border border-slate-300 space-y-3 shadow-xs">
        <div className="flex items-center justify-between border-b border-slate-100 pb-2">
          <div className="flex items-center gap-2">
            <Ambulance className="w-4 h-4 text-red-600" />
            <h3 className="text-xs font-black text-black">108 SOS & Emergency Records</h3>
          </div>
          <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-red-100 text-red-900 border border-red-300">
            Emergency Network
          </span>
        </div>

        {/* SOS History Item */}
        <div className="space-y-2">
          {recentSosDispatches.map((sos) => (
            <div key={sos.id} className="p-3 rounded-xl bg-red-50/60 border border-red-200 space-y-1">
              <div className="flex items-start justify-between">
                <div>
                  <h4 className="text-xs font-black text-red-950">{sos.title}</h4>
                  <p className="text-[10px] font-bold text-red-800 mt-0.5">{sos.reason}</p>
                </div>
                <span className="text-[9px] font-black px-2 py-0.5 rounded bg-emerald-100 text-emerald-950 border border-emerald-300">
                  {sos.statusBadge}
                </span>
              </div>

              <div className="text-[10px] text-red-900 pt-1 border-t border-red-200 flex items-center justify-between">
                <span>{sos.dateTime} · Attending: {sos.attending}</span>
                <span className="font-bold">{sos.response}</span>
              </div>
            </div>
          ))}
        </div>

        {/* Quick Direct 108 Emergency Trigger */}
        <div className="grid grid-cols-2 gap-2 pt-1">
          <button
            onClick={() => onNavigateTab('sos')}
            className="p-2.5 rounded-xl bg-red-600 hover:bg-red-700 text-white font-black text-xs flex items-center justify-center gap-1.5 cursor-pointer shadow-xs"
          >
            <Asterisk className="w-4 h-4 stroke-[2.5]" />
            <span>Trigger 108 SOS</span>
          </button>

          <a
            href="tel:+919876511002"
            onClick={(e) => {
              e.preventDefault();
              showToast('Calling Dr. Arvind Mehta (+91 98765 11002) - ETA: 7 mins');
            }}
            className="p-2.5 rounded-xl bg-black hover:bg-neutral-800 text-white font-black text-xs flex items-center justify-center gap-1.5 cursor-pointer shadow-xs"
          >
            <Phone className="w-3.5 h-3.5" />
            <span>Call Nearest Doctor</span>
          </a>
        </div>
      </div>

      {/* ────────────────────────────────────────────────────────── */}
      {/* 5. BRING BACK RISK ASSESSMENT TILE (Requested by User)     */}
      {/* ────────────────────────────────────────────────────────── */}
      <div 
        onClick={() => onNavigateTab('risk-assessment')}
        className="p-4 rounded-2xl bg-white border-2 border-black hover:bg-slate-50 cursor-pointer space-y-2 transition-all shadow-xs"
      >
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl bg-black text-white flex items-center justify-center">
              <Activity className="w-4 h-4" />
            </div>
            <div>
              <h4 className="text-xs font-black text-black">12-Question Symptom & Risk Assessment</h4>
              <p className="text-[10px] font-medium text-slate-600">
                Run AI Multi-Disease Screening & Numeric Vitals Triage
              </p>
            </div>
          </div>
          <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-black text-white uppercase">
            Start AI Triage
          </span>
        </div>
      </div>

      {/* ────────────────────────────────────────────────────────── */}
      {/* 6. CITIZEN CORE DIGITAL HEALTH ACTION TILES                */}
      {/* ────────────────────────────────────────────────────────── */}
      <div className="space-y-2">
        <h3 className="text-xs font-black text-black uppercase tracking-wider">
          Citizen Digital Services
        </h3>

        <div className="grid grid-cols-2 gap-2.5">
          {/* Scan Rx OCR */}
          <div
            onClick={() => onNavigateTab('ocr')}
            className="p-3.5 rounded-2xl bg-white border border-slate-300 hover:border-black cursor-pointer space-y-2 transition-all shadow-xs"
          >
            <div className="w-9 h-9 rounded-xl bg-black text-white flex items-center justify-center">
              <Camera className="w-4 h-4 text-white" />
            </div>
            <div>
              <div className="flex items-center gap-1">
                <h4 className="text-xs font-black text-black">Scan Rx (OCR)</h4>
                <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-black text-white">AI</span>
              </div>
              <p className="text-[10px] font-medium text-slate-600 mt-0.5 leading-snug">
                Scan doctor slips to parse vitals & medicines
              </p>
            </div>
          </div>

          {/* Sehat Setu AI Health Assistant */}
          <div
            onClick={() => onNavigateTab('ai-assistant')}
            className="p-3.5 rounded-2xl bg-white border border-slate-300 hover:border-black cursor-pointer space-y-2 transition-all shadow-xs"
          >
            <div className="w-9 h-9 rounded-xl bg-black text-white flex items-center justify-center">
              <Sparkles className="w-4 h-4 text-white" />
            </div>
            <div>
              <div className="flex items-center gap-1">
                <h4 className="text-xs font-black text-black">Sehat Setu AI</h4>
                <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-emerald-100 text-emerald-950 border border-emerald-300">Safe</span>
              </div>
              <p className="text-[10px] font-medium text-slate-600 mt-0.5 leading-snug">
                Direct precautions & supervisor-verified Rx
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* ────────────────────────────────────────────────────────── */}
      {/* 7. CITIZEN PERSONAL VITALS SNAPSHOT                       */}
      {/* ────────────────────────────────────────────────────────── */}
      <div className="p-4 rounded-2xl bg-white border border-slate-300 space-y-3 shadow-xs">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <HeartPulse className="w-4 h-4 text-red-600" />
            <h4 className="text-xs font-black text-black">My Latest Vitals</h4>
          </div>
          <span className="text-[10px] text-slate-600 font-bold">Checked 12 Nov 2026</span>
        </div>

        <div className="grid grid-cols-4 gap-2 text-center">
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-sm font-black text-black block">120/80</span>
            <span className="text-[9px] font-bold text-slate-500 uppercase">BP (Normal)</span>
          </div>
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-sm font-black text-black block">98%</span>
            <span className="text-[9px] font-bold text-slate-500 uppercase">SpO2 (Optimal)</span>
          </div>
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-sm font-black text-black block">76</span>
            <span className="text-[9px] font-bold text-slate-500 uppercase">Pulse (bpm)</span>
          </div>
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-sm font-black text-black block">105</span>
            <span className="text-[9px] font-bold text-slate-500 uppercase">Sugar (Fasting)</span>
          </div>
        </div>
      </div>

      {/* ────────────────────────────────────────────────────────── */}
      {/* 8. ACTIVE PRESCRIBED MEDICATIONS WITH DOCTOR SIGN-OFF      */}
      {/* ────────────────────────────────────────────────────────── */}
      <div className="p-4 rounded-2xl bg-white border border-slate-300 space-y-2.5 shadow-xs">
        <div className="flex items-center justify-between">
          <h4 className="text-xs font-black text-black flex items-center gap-1.5">
            <Pill className="w-3.5 h-3.5 text-black" />
            <span>My Prescribed Medications</span>
          </h4>
          <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-300">
            Supervisor Verified
          </span>
        </div>

        <div className="space-y-1.5">
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 flex justify-between items-center text-xs">
            <div>
              <span className="font-black text-black block">Tab Paracetamol (500 mg)</span>
              <span className="text-[10px] text-slate-600 font-medium">SOS after food for fever</span>
            </div>
            <span className="text-[9px] font-mono font-bold text-[#15803D] flex items-center gap-1">
              <CheckCircle2 className="w-3 h-3" />
              Verified by Dr. Arvind Mehta
            </span>
          </div>

          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 flex justify-between items-center text-xs">
            <div>
              <span className="font-black text-black block">Oral Rehydration Salts (ORS)</span>
              <span className="text-[10px] text-slate-600 font-medium">1 packet daily in 1 Litre water</span>
            </div>
            <span className="text-[9px] font-mono font-bold text-[#15803D] flex items-center gap-1">
              <CheckCircle2 className="w-3 h-3" />
              Verified by Dr. Priya Kulkarni
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
