import React, { useState } from 'react';
import { 
  Menu,
  Bell,
  Search, 
  Plus, 
  ChevronRight, 
  AlertTriangle, 
  HeartPulse, 
  Calendar, 
  Phone, 
  MapPin,
  Clock,
  UserPlus
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
    { label: 'All Patients', value: 'ALL' },
    { label: 'Pregnancy (ANC)', value: 'PREGNANCY' },
    { label: 'Child Health', value: 'CHILD' },
    { label: 'Chronic Care', value: 'CHRONIC' },
    { label: 'Emergency', value: 'EMERGENCY' },
    { label: 'General', value: 'GENERAL' },
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
    <div className="space-y-3.5 pb-2">
      {/* Mobile Top Header Bar */}
      <div className="flex items-center justify-between py-1 border-b border-slate-800/60 pb-2">
        <button 
          onClick={onOpenDrawer}
          className="p-1.5 text-slate-300 hover:text-white cursor-pointer"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-2">
          {/* Logo icon */}
          <div className="w-6 h-6 rounded-lg bg-gradient-to-tr from-purple-500 via-indigo-500 to-cyan-400 p-[1.5px]">
            <div className="w-full h-full bg-[#0d1017] rounded-[5px] flex items-center justify-center">
              <div className="w-3 h-3 border border-cyan-400 rounded-xs rotate-45 flex items-center justify-center">
                <div className="w-1 h-1 bg-purple-400 rounded-full"></div>
              </div>
            </div>
          </div>
          <span className="font-bold text-sm text-white tracking-tight">Patients Roster</span>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-full bg-[#1b382b] text-[#22c55e] border border-[#22c55e]/30 text-[10px] font-semibold flex items-center gap-1">
            <span className="text-[9px]">☁️</span>
            <span>Sync</span>
          </div>
          <button className="p-1.5 text-slate-300 hover:text-white cursor-pointer">
            <Bell className="w-4 h-4 text-slate-300" />
          </button>
        </div>
      </div>

      {/* Roster Title & Add Patient Action */}
      <div className="flex items-center justify-between pt-0.5">
        <div>
          <h2 className="text-xs font-bold text-white tracking-wide">Community Patient Roster</h2>
          <p className="text-[10px] text-slate-400">
            {filteredPatients.length} record(s) isolated in local Room vault
          </p>
        </div>

        <button
          onClick={onOpenAddModal}
          className="px-3 py-1.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-semibold text-[11px] flex items-center gap-1.5 transition-all shadow-md shadow-purple-600/30 cursor-pointer"
        >
          <UserPlus className="w-3.5 h-3.5" />
          <span>Add Patient</span>
        </button>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search by name, ward, phone, or ID..."
          className="w-full pl-9 pr-3.5 py-2.5 rounded-2xl bg-[#101421] border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500 transition-colors"
        />
      </div>

      {/* Category Filter Pills (Horizontal Scroll) */}
      <div className="flex gap-1.5 overflow-x-auto pb-1 select-none scrollbar-none">
        {categories.map((c) => {
          const isSelected = categoryFilter === c.value;
          return (
            <button
              key={c.value}
              onClick={() => setCategoryFilter(c.value)}
              className={`px-3 py-1.5 rounded-xl text-[11px] font-semibold whitespace-nowrap transition-colors cursor-pointer ${
                isSelected
                  ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                  : 'bg-[#101421] text-slate-400 border border-slate-800 hover:text-white'
              }`}
            >
              {c.label}
            </button>
          );
        })}
      </div>

      {/* Mobile-Friendly Single Column Patient List (Never grid-cols-2 inside mobile viewport) */}
      <div className="flex flex-col gap-3">
        {filteredPatients.map((p) => {
          const isCrit = p.riskLevel === 'CRITICAL' || p.isCritical;
          const isHigh = p.riskLevel === 'HIGH';
          const initials = p.fullName.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();

          return (
            <div
              key={p.id}
              className={`p-4 rounded-3xl border transition-all space-y-3 ${
                isCrit
                  ? 'bg-[#1c1218] border-red-900/40 hover:border-red-500/60'
                  : isHigh
                  ? 'bg-[#1c1814] border-amber-900/40 hover:border-amber-500/60'
                  : 'bg-[#131926] border-slate-800/90 hover:border-purple-500/40'
              }`}
            >
              <div>
                {/* Header row: Avatar, Name, ID, Risk Pill */}
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-center space-x-3">
                    <div className={`w-10 h-10 rounded-2xl flex items-center justify-center font-bold text-xs shrink-0 shadow-inner ${
                      isCrit 
                        ? 'bg-[#3b151e] text-[#f43f5e] border border-red-500/30' 
                        : isHigh 
                        ? 'bg-[#332015] text-[#fb923c] border border-amber-500/30'
                        : 'bg-[#1d1b38] text-[#c084fc] border border-purple-500/30'
                    }`}>
                      {initials}
                    </div>
                    <div>
                      <div className="flex items-center gap-1.5 flex-wrap">
                        <h3 className="font-bold text-xs text-white tracking-tight">{p.fullName}</h3>
                        <span className="text-[10px] text-slate-400 font-mono">({p.id})</span>
                      </div>
                      <p className="text-[11px] text-slate-400">
                        {p.age}y · {p.gender} · Blood {p.bloodGroup}
                      </p>
                    </div>
                  </div>

                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full shrink-0 ${
                    isCrit 
                      ? 'bg-red-500/20 text-red-400 border border-red-500/30 animate-pulse'
                      : isHigh
                      ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                      : 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                  }`}>
                    {p.riskLevel} RISK
                  </span>
                </div>

                {/* Patient metadata details */}
                <div className="grid grid-cols-2 gap-2 mt-3 text-[10px] text-slate-300">
                  <div className="flex items-center gap-1.5 text-slate-400 truncate">
                    <MapPin className="w-3 h-3 text-slate-500 shrink-0" />
                    <span className="truncate">{p.villageOrWard}</span>
                  </div>
                  <div className="flex items-center gap-1.5 text-slate-400">
                    <Phone className="w-3 h-3 text-slate-500 shrink-0" />
                    <span className="truncate">{p.phone}</span>
                  </div>
                  <div className="flex items-center gap-1.5 text-slate-400">
                    <Calendar className="w-3 h-3 text-slate-500 shrink-0" />
                    <span className="truncate">Follow-up: {p.nextFollowUpDate}</span>
                  </div>
                  <div className="flex items-center gap-1.5 text-slate-400">
                    <Clock className="w-3 h-3 text-slate-500 shrink-0" />
                    <span className="truncate">{p.lastVisitLabel}</span>
                  </div>
                </div>

                {/* Vitals pill */}
                {p.vitals && (
                  <div className="mt-2.5 p-2 rounded-xl bg-[#0f131f] border border-slate-800 flex items-center justify-between text-[11px]">
                    <div className="flex items-center gap-1.5 text-slate-300">
                      <HeartPulse className="w-3.5 h-3.5 text-rose-400 shrink-0" />
                      <span>BP: <strong>{p.vitals.systolicBp}/{p.vitals.diastolicBp}</strong></span>
                      <span className="text-slate-600">·</span>
                      <span>SpO2: <strong>{p.vitals.spo2Percent}%</strong></span>
                    </div>
                    <div className="text-[10px] text-slate-400">
                      HR: {p.vitals.heartRate} · Sugar: {p.vitals.bloodSugarMgDl}
                    </div>
                  </div>
                )}

                {/* Chronic conditions note */}
                {p.chronicConditions && (
                  <p className="mt-2 text-[10px] text-amber-400/90 font-medium">
                    ⚠️ {p.chronicConditions}
                  </p>
                )}
              </div>

              {/* Action buttons */}
              <div className="pt-2 border-t border-slate-800/80 flex items-center justify-between gap-2">
                <button
                  onClick={() => onOpenSosModal(p)}
                  className="flex-1 py-2 px-3 rounded-xl bg-red-600/20 hover:bg-red-600/30 text-red-400 border border-red-500/30 text-xs font-semibold flex items-center justify-center gap-1.5 cursor-pointer transition-colors"
                >
                  <AlertTriangle className="w-3.5 h-3.5" />
                  <span>Dispatch 108</span>
                </button>

                <button
                  onClick={() => onSelectPatient(p)}
                  className="flex-1 py-2 px-3 rounded-xl bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 border border-blue-500/30 text-xs font-semibold flex items-center justify-center gap-1.5 cursor-pointer transition-colors"
                >
                  <span>View Details</span>
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
