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
  CheckCircle2
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
  abhaId = '91-8492-3310-9281'
}) => {
  const [showQrModal, setShowQrModal] = useState<boolean>(false);

  return (
    <div className="space-y-3.5 pb-4 select-none text-slate-800">
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
              className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs"
            >
              <Menu className="w-5 h-5" />
            </button>
          )}

          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-slate-100 border border-slate-300 flex items-center justify-center p-1 text-slate-900 shadow-xs">
              <span className="text-base leading-none">🏛️</span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="font-black text-sm text-[#0F172A] tracking-tight">CareSync Citizen</span>
                <span className="text-[9px] font-black px-1.5 py-0.2 rounded-xs bg-slate-200 text-slate-900 border border-slate-300 uppercase">
                  ABDM
                </span>
              </div>
              <p className="text-[9px] font-bold text-slate-600 leading-none">
                National Digital Health Mission · Personal Health Record (PHR)
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
            className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs"
          >
            <Bell className="w-4 h-4 text-slate-700" />
          </button>
        </div>
      </div>

      {/* Official Ayushman Bharat Citizen Digital Card */}
      <div className="p-4 rounded-2xl bg-white border-2 border-slate-900 shadow-sm space-y-3 relative overflow-hidden">
        <div className="flex justify-between items-start">
          <div className="space-y-0.5">
            <div className="flex items-center gap-1.5">
              <span className="text-[10px] font-black text-slate-900 tracking-wider uppercase">
                Ayushman Bharat · Citizen Health ID
              </span>
              <ShieldCheck className="w-3.5 h-3.5 text-[#15803D]" />
            </div>
            <h2 className="text-lg font-black text-[#0F172A] tracking-tight">{citizenName}</h2>
            <p className="text-xs font-mono font-bold text-slate-800">
              ABHA Number: {abhaId}
            </p>
            <p className="text-[10px] font-medium text-slate-600">
              Age: 38y · Male · Blood Group: B+ · Ward 4, Rampur East
            </p>
          </div>

          {/* QR Code Action Pill */}
          <button 
            onClick={() => setShowQrModal(!showQrModal)}
            className="w-14 h-14 rounded-xl bg-slate-100 border border-slate-300 p-1 flex flex-col items-center justify-center shadow-xs shrink-0 cursor-pointer hover:bg-slate-200 transition-colors"
          >
            <QrCode className="w-6 h-6 text-slate-900" />
            <span className="text-[8px] font-black text-slate-800 uppercase mt-0.5">View QR</span>
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
            <p className="text-[10px] font-mono font-bold text-slate-800">
              ABDM Digital Health Token Active
            </p>
          </div>
        )}

        <div className="pt-2 border-t border-slate-200 flex items-center justify-between">
          <div className="flex items-center gap-1.5 text-[11px] font-bold text-slate-800">
            <span className="w-2 h-2 rounded-full bg-[#15803D]"></span>
            <span>Assigned PHC: Rampur Sector 4 (Dr. Arvind Mehta)</span>
          </div>

          <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-emerald-50 text-[#15803D] border border-emerald-300">
            PM-JAY Eligible
          </span>
        </div>
      </div>

      {/* Citizen 4 Core Features Quick Action Grid */}
      <div className="space-y-2">
        <h3 className="text-xs font-black text-[#0F172A] uppercase tracking-wider flex items-center gap-1.5">
          <span>Citizen Digital Health Services</span>
        </h3>

        <div className="grid grid-cols-2 gap-2.5">
          {/* 1. OCR Prescription Scanner */}
          <div
            onClick={() => onNavigateTab('ocr')}
            className="p-3.5 rounded-2xl bg-white border border-slate-200 hover:border-slate-900 cursor-pointer space-y-2 transition-all shadow-xs"
          >
            <div className="w-9 h-9 rounded-xl bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center shadow-inner">
              <Camera className="w-4 h-4 text-slate-900" />
            </div>
            <div>
              <div className="flex items-center gap-1">
                <h4 className="text-xs font-black text-[#0F172A]">Scan Rx (OCR)</h4>
                <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-black text-white">AI</span>
              </div>
              <p className="text-[10px] font-medium text-slate-600 mt-0.5 leading-snug">
                Scan doctor slips to parse vitals & medicines
              </p>
            </div>
          </div>

          {/* 2. Sehat Setu AI Health Assistant */}
          <div
            onClick={() => onNavigateTab('ai-assistant')}
            className="p-3.5 rounded-2xl bg-white border border-slate-200 hover:border-slate-900 cursor-pointer space-y-2 transition-all shadow-xs"
          >
            <div className="w-9 h-9 rounded-xl bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center shadow-inner">
              <Sparkles className="w-4 h-4 text-slate-900" />
            </div>
            <div>
              <div className="flex items-center gap-1">
                <h4 className="text-xs font-black text-[#0F172A]">Sehat Setu AI</h4>
                <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-emerald-50 text-emerald-800 border border-emerald-300">Safe</span>
              </div>
              <p className="text-[10px] font-medium text-slate-600 mt-0.5 leading-snug">
                Direct precautions & supervisor-verified Rx
              </p>
            </div>
          </div>

          {/* 3. 108 Emergency SOS */}
          <div
            onClick={() => onNavigateTab('sos')}
            className="p-3.5 rounded-2xl bg-red-50/70 border border-red-200 hover:border-red-300 cursor-pointer space-y-2 transition-all shadow-xs"
          >
            <div className="w-9 h-9 rounded-xl bg-red-100 text-red-700 border border-red-300 flex items-center justify-center shadow-inner">
              <Asterisk className="w-4 h-4 stroke-[2.5]" />
            </div>
            <div>
              <div className="flex items-center gap-1">
                <h4 className="text-xs font-black text-red-950">108 Emergency</h4>
                <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-red-600 text-white animate-pulse">Live</span>
              </div>
              <p className="text-[10px] font-medium text-red-700 mt-0.5 leading-snug">
                Instant ambulance & nearest doctor call
              </p>
            </div>
          </div>

          {/* 4. My Health Records / Vitals */}
          <div
            onClick={() => onNavigateTab('records')}
            className="p-3.5 rounded-2xl bg-white border border-slate-200 hover:border-slate-900 cursor-pointer space-y-2 transition-all shadow-xs"
          >
            <div className="w-9 h-9 rounded-xl bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center shadow-inner">
              <FileText className="w-4 h-4 text-slate-900" />
            </div>
            <div>
              <div className="flex items-center gap-1">
                <h4 className="text-xs font-black text-[#0F172A]">My Health Vault</h4>
              </div>
              <p className="text-[10px] font-medium text-slate-600 mt-0.5 leading-snug">
                View personal vitals history & vaccines
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Citizen Personal Vitals Snapshot */}
      <div className="p-4 rounded-2xl bg-white border border-slate-200 space-y-3 shadow-xs">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <HeartPulse className="w-4 h-4 text-red-600" />
            <h4 className="text-xs font-black text-[#0F172A]">My Latest Vitals</h4>
          </div>
          <span className="text-[10px] text-slate-500 font-bold">Checked 12 Nov 2026</span>
        </div>

        <div className="grid grid-cols-4 gap-2 text-center">
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-sm font-black text-[#0F172A] block">120/80</span>
            <span className="text-[9px] font-bold text-slate-500 uppercase">BP (Normal)</span>
          </div>
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-sm font-black text-[#0F172A] block">98%</span>
            <span className="text-[9px] font-bold text-slate-500 uppercase">SpO2 (Optimal)</span>
          </div>
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-sm font-black text-[#0F172A] block">76</span>
            <span className="text-[9px] font-bold text-slate-500 uppercase">Pulse (bpm)</span>
          </div>
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200">
            <span className="text-sm font-black text-[#0F172A] block">105</span>
            <span className="text-[9px] font-bold text-slate-500 uppercase">Sugar (Fasting)</span>
          </div>
        </div>
      </div>

      {/* Active Prescribed Medications with Medical Supervisor Verification */}
      <div className="p-4 rounded-2xl bg-white border border-slate-200 space-y-2.5 shadow-xs">
        <div className="flex items-center justify-between">
          <h4 className="text-xs font-black text-[#0F172A] flex items-center gap-1.5">
            <Pill className="w-3.5 h-3.5 text-slate-900" />
            <span>My Prescribed Medications</span>
          </h4>
          <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-emerald-50 text-[#15803D] border border-emerald-300">
            Supervisor Verified
          </span>
        </div>

        <div className="space-y-1.5">
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 flex justify-between items-center text-xs">
            <div>
              <span className="font-bold text-[#0F172A] block">Tab Paracetamol (500 mg)</span>
              <span className="text-[10px] text-slate-500">SOS after food for fever</span>
            </div>
            <span className="text-[9px] font-mono font-bold text-[#15803D] flex items-center gap-1">
              <CheckCircle2 className="w-3 h-3" />
              Verified by Dr. Arvind Mehta
            </span>
          </div>

          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 flex justify-between items-center text-xs">
            <div>
              <span className="font-bold text-[#0F172A] block">Oral Rehydration Salts (ORS)</span>
              <span className="text-[10px] text-slate-500">1 packet daily in 1 Litre water</span>
            </div>
            <span className="text-[9px] font-mono font-bold text-[#15803D] flex items-center gap-1">
              <CheckCircle2 className="w-3 h-3" />
              Verified by Dr. Priya Kulkarni
            </span>
          </div>
        </div>
      </div>

      {/* Quick Emergency Hotline Call Bar */}
      <div className="p-3.5 rounded-2xl bg-slate-900 text-white flex items-center justify-between shadow-xs">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-xl bg-red-600 flex items-center justify-center">
            <Phone className="w-4 h-4 text-white" />
          </div>
          <div>
            <h4 className="text-xs font-black text-white">Need Urgent Medical Help?</h4>
            <p className="text-[10px] text-slate-300">Direct 108 Emergency Medical Response System</p>
          </div>
        </div>

        <button
          onClick={() => onNavigateTab('sos')}
          className="px-3.5 py-1.5 rounded-lg bg-red-600 hover:bg-red-700 text-white font-black text-xs cursor-pointer shadow-xs"
        >
          Call 108
        </button>
      </div>
    </div>
  );
};
