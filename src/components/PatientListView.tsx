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
      <div className="flex items-center justify-between py-1 border-b border-[#27272a] pb-2">
        <button 
          onClick={onOpenDrawer}
          className="p-1.5 text-white hover:text-[#d4d4d8] cursor-pointer"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-2">
          {/* Logo icon - Solid Grey / Black */}
          <div className="w-7 h-7 rounded-xl bg-[#27272a] border border-[#3f3f46] flex items-center justify-center p-1 text-white shadow-md">
            <div className="relative w-4 h-4 flex items-center justify-center">
              <div className="w-3 h-3 bg-white rounded-xs rotate-45"></div>
              <div className="absolute w-1.5 h-1.5 bg-[#22c55e] rounded-full"></div>
            </div>
          </div>
          <span className="font-black text-sm text-white tracking-tight">Patients Roster</span>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-full bg-[#1c2e1f] text-[#4ade80] border border-[#22c55e]/40 text-[10px] font-black flex items-center gap-1">
            <span className="text-[9px]">☁️</span>
            <span>Sync</span>
          </div>
          <button className="p-1.5 text-white hover:text-[#d4d4d8] cursor-pointer">
            <Bell className="w-4 h-4 text-white" />
          </button>
        </div>
      </div>

      {/* Roster Title & Add Patient Action */}
      <div className="flex items-center justify-between pt-0.5">
        <div>
          <h2 className="text-xs font-black text-white tracking-wider uppercase">Community Patient Roster</h2>
          <p className="text-[10px] font-bold text-[#a1a1aa]">
            {filteredPatients.length} record(s) isolated in local Room vault
          </p>
        </div>

        <button
          onClick={onOpenAddModal}
          className="px-3 py-1.5 rounded-xl bg-white hover:bg-[#e4e4e7] text-black font-black text-[11px] flex items-center gap-1.5 transition-all shadow-md cursor-pointer"
        >
          <UserPlus className="w-3.5 h-3.5 text-black" />
          <span>Add Patient</span>
        </button>
      </div>

      {/* Search Input - Solid Black / Grey */}
      <div className="relative">
        <Search className="w-3.5 h-3.5 text-[#a1a1aa] absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search by name, ward, phone, or ID..."
          className="w-full pl-9 pr-3.5 py-2.5 rounded-2xl bg-[#18181b] border-2 border-[#27272a] text-xs text-white placeholder-[#71717a] font-bold focus:outline-none focus:border-[#52525b] transition-colors"
        />
      </div>

      {/* Category Filter Pills - Solid Grey / Solid Black */}
      <div className="flex gap-1.5 overflow-x-auto pb-1 select-none scrollbar-none">
        {categories.map((c) => {
          const isSelected = categoryFilter === c.value;
          return (
            <button
              key={c.value}
              onClick={() => setCategoryFilter(c.value)}
              className={`px-3 py-1.5 rounded-xl text-[11px] font-black whitespace-nowrap transition-colors cursor-pointer ${
                isSelected
                  ? 'bg-white text-black border-2 border-white shadow-xs'
                  : 'bg-[#18181b] text-[#d4d4d8] border-2 border-[#27272a] hover:border-[#3f3f46]'
              }`}
            >
              {c.label}
            </button>
          );
        })}
      </div>

      {/* Single Column Patient List */}
      <div className="flex flex-col gap-3">
        {filteredPatients.map((p) => {
          const isCrit = p.riskLevel === 'CRITICAL' || p.isCritical;
          const initials = p.fullName.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();

          return (
            <div
              key={p.id}
              className={`p-4 rounded-3xl border-2 transition-all space-y-3 ${
                isCrit
                  ? 'bg-[#18181b] border-red-800'
                  : 'bg-[#18181b] border-[#27272a] hover:border-[#52525b]'
              }`}
            >
              <div>
                {/* Header row: Avatar, Name, ID, Risk Pill */}
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-center space-x-3">
                    <div className={`w-10 h-10 rounded-2xl flex items-center justify-center font-black text-xs shrink-0 shadow-inner ${
                      isCrit 
                        ? 'bg-red-950 text-red-400 border border-red-600' 
                        : 'bg-[#27272a] text-white border border-[#3f3f46]'
                    }`}>
                      {initials}
                    </div>
                    <div>
                      <div className="flex items-center gap-1.5 flex-wrap">
                        <h3 className="font-black text-xs text-white tracking-tight">{p.fullName}</h3>
                        <span className="text-[10px] text-[#a1a1aa] font-mono font-bold">({p.id})</span>
                      </div>
                      <p className="text-[11px] font-bold text-[#a1a1aa]">
                        {p.age}y · {p.gender} · Blood {p.bloodGroup}
                      </p>
                    </div>
                  </div>

                  <span className={`text-[10px] font-black px-2 py-0.5 rounded-full shrink-0 ${
                    isCrit 
                      ? 'bg-red-950 text-red-400 border border-red-600 animate-pulse'
                      : 'bg-[#1c2e1f] text-[#4ade80] border border-[#22c55e]/40'
                  }`}>
                    {p.riskLevel} RISK
                  </span>
                </div>

                {/* Patient metadata details */}
                <div className="grid grid-cols-2 gap-2 mt-3 text-[10px] text-[#d4d4d8]">
                  <div className="flex items-center gap-1.5 font-bold truncate">
                    <MapPin className="w-3 h-3 text-[#a1a1aa] shrink-0" />
                    <span className="truncate">{p.villageOrWard}</span>
                  </div>
                  <div className="flex items-center gap-1.5 font-bold">
                    <Phone className="w-3 h-3 text-[#a1a1aa] shrink-0" />
                    <span className="truncate">{p.phone}</span>
                  </div>
                  <div className="flex items-center gap-1.5 font-bold">
                    <Calendar className="w-3 h-3 text-[#a1a1aa] shrink-0" />
                    <span className="truncate">Follow-up: <strong className="text-white">{p.nextFollowUpDate}</strong></span>
                  </div>
                  <div className="flex items-center gap-1.5 font-bold">
                    <Clock className="w-3 h-3 text-[#a1a1aa] shrink-0" />
                    <span className="truncate">{p.lastVisitLabel}</span>
                  </div>
                </div>

                {/* Vitals pill */}
                {p.vitals && (
                  <div className="mt-2.5 p-2 rounded-xl bg-[#0f0f10] border border-[#27272a] flex items-center justify-between text-[11px]">
                    <div className="flex items-center gap-1.5 text-white">
                      <HeartPulse className="w-3.5 h-3.5 text-red-400 shrink-0" />
                      <span>BP: <strong className="text-white font-black">{p.vitals.systolicBp}/{p.vitals.diastolicBp}</strong></span>
                      <span className="text-[#52525b]">·</span>
                      <span>SpO2: <strong className="text-white font-black">{p.vitals.spo2Percent}%</strong></span>
                    </div>
                    <div className="text-[10px] font-black text-[#d4d4d8]">
                      HR: {p.vitals.heartRate} · Sugar: {p.vitals.bloodSugarMgDl}
                    </div>
                  </div>
                )}

                {/* Chronic conditions note */}
                {p.chronicConditions && (
                  <p className="mt-2 text-[10px] text-white font-black">
                    ⚠️ {p.chronicConditions}
                  </p>
                )}
              </div>

              {/* Action buttons - Solid Red & Solid Grey */}
              <div className="pt-2 border-t border-[#27272a] flex items-center justify-between gap-2">
                <button
                  onClick={() => onOpenSosModal(p)}
                  className="flex-1 py-2 px-3 rounded-xl bg-red-950 hover:bg-red-900 text-red-400 border border-red-700 text-xs font-black flex items-center justify-center gap-1.5 cursor-pointer transition-colors"
                >
                  <AlertTriangle className="w-3.5 h-3.5" />
                  <span>Dispatch 108</span>
                </button>

                <button
                  onClick={() => onSelectPatient(p)}
                  className="flex-1 py-2 px-3 rounded-xl bg-[#27272a] hover:bg-[#3f3f46] text-white border border-[#3f3f46] text-xs font-black flex items-center justify-center gap-1.5 cursor-pointer transition-colors"
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
