import React, { useState } from 'react';
import { 
  Menu, 
  Bell, 
  Search, 
  ChevronRight, 
  AlertTriangle, 
  HeartPulse, 
  Calendar, 
  Phone, 
  MapPin, 
  Clock, 
  UserPlus,
  ShieldCheck,
  Building2,
  FileCheck
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { Patient } from '../types';

interface PatientListViewProps {
  onSelectPatient: (patient: Patient) => void;
  onOpenAddModal: () => void;
  onOpenSosModal: (patient: Patient) => void;
  onOpenDrawer?: () => void;
}

export const PatientListView: React.FC<PatientListViewProps> = ({
  onSelectPatient,
  onOpenAddModal,
  onOpenSosModal,
  onOpenDrawer
}) => {
  const { patients } = useApp();
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');

  const categories: { label: string; value: string }[] = [
    { label: 'All Beneficiaries', value: 'ALL' },
    { label: 'Maternal ANC', value: 'PREGNANCY' },
    { label: 'Child Health (UIP)', value: 'CHILD' },
    { label: 'NCD Chronic Care', value: 'CHRONIC' },
    { label: '108 Emergency', value: 'EMERGENCY' },
    { label: 'General OPD', value: 'GENERAL' },
  ];

  const filteredPatients = patients.filter(p => {
    const matchesSearch = p.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.villageOrWard.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.phone.includes(searchTerm) ||
      p.id.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = categoryFilter === 'ALL' || p.category === categoryFilter;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="space-y-3 pb-2 select-none">
      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Mobile Top Header Bar */}
      <div className="flex items-center justify-between py-0.5 border-b border-[#1e293b] pb-2">
        <div className="flex items-center space-x-2">
          <button 
            onClick={onOpenDrawer}
            className="p-1.5 rounded-lg bg-[#0f172a] border border-[#334155] text-slate-200 hover:text-white cursor-pointer"
          >
            <Menu className="w-5 h-5" />
          </button>

          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-black text-sm text-white tracking-tight">ABHA Beneficiary Register</span>
            </div>
            <p className="text-[9px] font-bold text-slate-400 leading-none">
              राष्ट्रीय स्वास्थ्य मिशन (NHM) · Ward 7 Sub-Centre
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-1.5">
          <div className="px-2 py-0.5 rounded-md bg-[#064e3b] text-[#34d399] border border-[#059669]/50 text-[10px] font-black flex items-center gap-1">
            <ShieldCheck className="w-3 h-3 text-[#10b981]" />
            <span>ABDM</span>
          </div>
          <button className="p-1.5 rounded-lg bg-[#0f172a] border border-[#334155] text-slate-200 hover:text-white cursor-pointer">
            <Bell className="w-4 h-4 text-slate-300" />
          </button>
        </div>
      </div>

      {/* Roster Title & Add ABHA Patient Action */}
      <div className="flex items-center justify-between pt-0.5">
        <div>
          <h2 className="text-xs font-black text-white tracking-wide uppercase flex items-center gap-1.5">
            <span>Enrolled Citizens</span>
            <span className="px-1.5 py-0.2 rounded-xs bg-[#1e3a8a] text-blue-200 text-[9px] font-black">
              {filteredPatients.length} Active
            </span>
          </h2>
          <p className="text-[10px] font-medium text-slate-400">
            Encrypted offline health ledger (NIC Vault compliant)
          </p>
        </div>

        <button
          onClick={onOpenAddModal}
          className="px-3 py-1.5 rounded-lg bg-[#1d4ed8] hover:bg-[#1e40af] text-white font-black text-[11px] flex items-center gap-1.5 shadow-md border border-[#3b82f6]/40 cursor-pointer transition-all"
        >
          <UserPlus className="w-3.5 h-3.5" />
          <span>+ Enroll ABHA</span>
        </button>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search by ABHA ID, name, ward, phone..."
          className="w-full pl-9 pr-3.5 py-2.5 rounded-xl bg-[#0f172a] border border-[#334155] text-xs text-white placeholder-slate-400 font-medium focus:outline-none focus:border-[#38bdf8] transition-colors"
        />
      </div>

      {/* Category Filter Pills (Govt Program Badges) */}
      <div className="flex gap-1.5 overflow-x-auto pb-1 select-none scrollbar-none">
        {categories.map((c) => {
          const isSelected = categoryFilter === c.value;
          return (
            <button
              key={c.value}
              onClick={() => setCategoryFilter(c.value)}
              className={`px-3 py-1.5 rounded-lg text-[11px] font-black whitespace-nowrap transition-colors cursor-pointer ${
                isSelected
                  ? 'bg-[#1d4ed8] text-white border border-[#3b82f6] shadow-xs'
                  : 'bg-[#0f172a] text-slate-300 border border-[#334155] hover:text-white'
              }`}
            >
              {c.label}
            </button>
          );
        })}
      </div>

      {/* Single Column Patient List */}
      <div className="flex flex-col gap-2.5">
        {filteredPatients.map((p) => {
          const isCrit = p.riskLevel === 'CRITICAL' || p.isCritical;
          const initials = p.fullName.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
          const pseudoAbha = `91-${p.id.replace('pt-', '2026-')}-00${p.age}`;

          return (
            <div
              key={p.id}
              className={`p-3.5 rounded-2xl border transition-all space-y-2.5 ${
                isCrit
                  ? 'bg-[#181014] border-red-800 hover:border-red-500'
                  : 'bg-[#0f172a] border-[#334155] hover:border-[#3b82f6]'
              }`}
            >
              <div>
                {/* Header row: Avatar, Name, ABHA ID, Risk Pill */}
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-center space-x-3">
                    <div className={`w-10 h-10 rounded-xl flex items-center justify-center font-black text-xs shrink-0 shadow-inner ${
                      isCrit 
                        ? 'bg-red-950 text-red-400 border border-red-700' 
                        : 'bg-[#1e293b] text-[#60a5fa] border border-[#334155]'
                    }`}>
                      {initials}
                    </div>
                    <div>
                      <div className="flex items-center gap-1.5 flex-wrap">
                        <h3 className="font-black text-xs text-white tracking-tight">{p.fullName}</h3>
                        <span className="text-[9px] font-black px-1.5 py-0.2 rounded-xs bg-[#1e293b] text-slate-300 border border-[#334155] font-mono">
                          ABHA: {pseudoAbha}
                        </span>
                      </div>
                      <p className="text-[11px] font-bold text-slate-300 mt-0.5">
                        {p.age}y · {p.gender} · Blood {p.bloodGroup} · PM-JAY Eligible
                      </p>
                    </div>
                  </div>

                  <span className={`text-[9px] font-black px-2 py-0.5 rounded-full shrink-0 ${
                    isCrit 
                      ? 'bg-red-950 text-red-400 border border-red-600 animate-pulse'
                      : 'bg-[#064e3b] text-[#34d399] border border-[#059669]/40'
                  }`}>
                    {p.riskLevel} PRIORITY
                  </span>
                </div>

                {/* Patient metadata details */}
                <div className="grid grid-cols-2 gap-1.5 mt-2.5 text-[10px] text-slate-300">
                  <div className="flex items-center gap-1.5 font-bold truncate">
                    <MapPin className="w-3 h-3 text-[#38bdf8] shrink-0" />
                    <span className="truncate">{p.villageOrWard}</span>
                  </div>
                  <div className="flex items-center gap-1.5 font-bold">
                    <Phone className="w-3 h-3 text-[#38bdf8] shrink-0" />
                    <span className="truncate">{p.phone}</span>
                  </div>
                  <div className="flex items-center gap-1.5 font-bold">
                    <Calendar className="w-3 h-3 text-[#38bdf8] shrink-0" />
                    <span className="truncate">Next Field Visit: <strong className="text-white">{p.nextFollowUpDate}</strong></span>
                  </div>
                  <div className="flex items-center gap-1.5 font-bold">
                    <Clock className="w-3 h-3 text-[#38bdf8] shrink-0" />
                    <span className="truncate">{p.lastVisitLabel}</span>
                  </div>
                </div>

                {/* Vitals pill */}
                {p.vitals && (
                  <div className="mt-2 p-2 rounded-lg bg-[#0b1329] border border-[#1e293b] flex items-center justify-between text-[11px]">
                    <div className="flex items-center gap-1.5 text-white">
                      <HeartPulse className="w-3.5 h-3.5 text-red-400 shrink-0" />
                      <span>BP: <strong className="text-white font-black">{p.vitals.systolicBp}/{p.vitals.diastolicBp}</strong></span>
                      <span className="text-[#334155]">·</span>
                      <span>SpO2: <strong className="text-white font-black">{p.vitals.spo2Percent}%</strong></span>
                    </div>
                    <div className="text-[10px] font-black text-slate-300">
                      HR: {p.vitals.heartRate} · Sugar: {p.vitals.bloodSugarMgDl}
                    </div>
                  </div>
                )}

                {/* Clinical note */}
                {p.chronicConditions && (
                  <p className="mt-1.5 text-[10px] text-amber-300 font-black flex items-center gap-1">
                    <span>⚠️ NCD Watch:</span>
                    <span className="text-white">{p.chronicConditions}</span>
                  </p>
                )}
              </div>

              {/* Action buttons */}
              <div className="pt-2 border-t border-[#1e293b] flex items-center justify-between gap-2">
                <button
                  onClick={() => onOpenSosModal(p)}
                  className="flex-1 py-1.5 px-3 rounded-lg bg-red-950 hover:bg-red-900 text-red-400 border border-red-700 text-xs font-black flex items-center justify-center gap-1.5 cursor-pointer transition-colors"
                >
                  <AlertTriangle className="w-3.5 h-3.5" />
                  <span>108 Referral</span>
                </button>

                <button
                  onClick={() => onSelectPatient(p)}
                  className="flex-1 py-1.5 px-3 rounded-lg bg-[#1e293b] hover:bg-[#334155] text-white border border-[#334155] text-xs font-black flex items-center justify-center gap-1.5 cursor-pointer transition-colors"
                >
                  <FileCheck className="w-3.5 h-3.5 text-[#38bdf8]" />
                  <span>Clinical File</span>
                  <ChevronRight className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
