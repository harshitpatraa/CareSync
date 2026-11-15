import React, { useState } from 'react';
import { 
  ArrowLeft, 
  Bell, 
  Search, 
  Filter, 
  AlertTriangle, 
  Pill, 
  Baby, 
  HeartPulse, 
  RotateCw, 
  ClipboardList, 
  Clock, 
  CheckCircle2, 
  ChevronRight,
  ShieldAlert
} from 'lucide-react';
import { Patient } from '../types';

interface RecentActivityViewProps {
  onBack: () => void;
  onSelectPatient: (patientName: string) => void;
}

export const RecentActivityView: React.FC<RecentActivityViewProps> = ({ onBack, onSelectPatient }) => {
  const [filter, setFilter] = useState<'ALL' | 'ALERTS' | 'MEDS' | 'SYNC'>('ALL');
  const [search, setSearchTerm] = useState('');

  const activities = [
    {
      id: 'act-1',
      time: '10:05 AM Today',
      type: 'ALERTS',
      icon: AlertTriangle,
      iconColor: 'text-red-400',
      iconBg: 'bg-red-500/15',
      title: 'Critical SpO2 Alert (90%) - Harish Verma',
      desc: 'SpO2 dropped below 92% with BP 164/98. Immediate 108 Emergency PHC transfer advised.',
      patientName: 'Harish Verma',
      tag: 'Critical Alert',
      tagBg: 'bg-red-500/10 text-red-400 border-red-500/20'
    },
    {
      id: 'act-2',
      time: '09:40 AM Today',
      type: 'MEDS',
      icon: Pill,
      iconColor: 'text-white',
      iconBg: 'bg-[#27272a]',
      title: 'Medication Dispensed - Meera Devi',
      desc: 'Ferrous Sulfate + Folic Acid (IFA 100 tablets) dispensed. Ankle edema check scheduled.',
      patientName: 'Meera Devi',
      tag: 'Maternal ANC',
      tagBg: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20'
    },
    {
      id: 'act-3',
      time: '09:15 AM Today',
      type: 'ALERTS',
      icon: Baby,
      iconColor: 'text-emerald-400',
      iconBg: 'bg-emerald-500/15',
      title: 'Child Growth & UIP Immunization - Aarav Sharma',
      desc: 'Weight 12.4 kg (Normal green zone). Pentavalent-3 booster dose administered.',
      patientName: 'Aarav Sharma',
      tag: 'Pediatric UIP',
      tagBg: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20'
    },
    {
      id: 'act-4',
      time: '08:45 AM Today',
      type: 'MEDS',
      icon: HeartPulse,
      iconColor: 'text-white',
      iconBg: 'bg-[#27272a]',
      title: 'Fasting Blood Glucose Recorded - Fatima Begum',
      desc: 'Pre-prandial glucose 142 mg/dL. Metformin 500mg adherence verified by ASHA worker.',
      patientName: 'Fatima Begum',
      tag: 'NCD Screening',
      tagBg: 'bg-indigo-500/10 text-white border-[#3f3f46]/20'
    },
    {
      id: 'act-5',
      time: '08:00 AM Today',
      type: 'SYNC',
      icon: RotateCw,
      iconColor: 'text-emerald-400',
      iconBg: 'bg-emerald-500/15',
      title: 'Field Hub Cloud Synchronization Completed',
      desc: '24 local patient records & vitals batches synchronized with CareSync Cloud Firestore.',
      patientName: '',
      tag: 'Vault Sync',
      tagBg: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20'
    },
    {
      id: 'act-6',
      time: '04:30 PM Yesterday',
      type: 'ALERTS',
      icon: ShieldAlert,
      iconColor: 'text-white',
      iconBg: 'bg-[#27272a]',
      title: 'Category III Dog Bite Protocol - Sunita Rao',
      desc: 'Wound washed with carbolic soap. Anti-Rabies Vaccine (ARV Dose 2) administered at PHC.',
      patientName: 'Sunita Rao',
      tag: 'Urgent Triage',
      tagBg: 'bg-indigo-500/10 text-white border-[#3f3f46]/20'
    },
    {
      id: 'act-7',
      time: '02:15 PM Yesterday',
      type: 'SYNC',
      icon: ClipboardList,
      iconColor: 'text-emerald-400',
      iconBg: 'bg-emerald-500/15',
      title: 'ASHA Household Baseline Survey - Ward 4',
      desc: '18 households enumerated in Rampur East. 3 new pregnancies enrolled for 1st ANC.',
      patientName: '',
      tag: 'Field Survey',
      tagBg: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20'
    }
  ];

  const filtered = activities.filter(a => {
    const matchesFilter = filter === 'ALL' || a.type === filter;
    const matchesSearch = a.title.toLowerCase().includes(search.toLowerCase()) || 
                          a.desc.toLowerCase().includes(search.toLowerCase()) ||
                          a.patientName.toLowerCase().includes(search.toLowerCase());
    return matchesFilter && matchesSearch;
  });

  return (
    <div className="space-y-3.5 pb-2">
      {/* Top Header */}
      <div className="flex items-center justify-between py-1 border-b border-[#27272a]/80 pb-2">
        <button onClick={onBack} className="p-1.5 text-slate-300 hover:text-white cursor-pointer">
          <ArrowLeft className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-2">
          <div className="w-6 h-6 rounded-lg bg-white text-black font-black flex items-center justify-center text-white">
            <ClipboardList className="w-3.5 h-3.5" />
          </div>
          <h2 className="font-bold text-sm text-white tracking-tight">Recent Clinical Activity</h2>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-full bg-emerald-950/60 text-emerald-400 border border-emerald-500/30 text-[10px] font-semibold flex items-center gap-1">
            <span>☁️</span>
            <span>Live Ledger</span>
          </div>
        </div>
      </div>

      {/* Overview Banner */}
      <div className="p-3.5 rounded-2xl bg-[#0f172a] border border-[#27272a] flex items-center justify-between">
        <div>
          <h3 className="text-xs font-bold text-white">ASHA Field Work Ledger</h3>
          <p className="text-[11px] text-slate-400 mt-0.5">Chronological record of patient visits, vitals & emergency events</p>
        </div>
        <span className="px-2.5 py-1 rounded-full bg-white text-black font-black/20 text-white border border-[#3f3f46] text-[10px] font-semibold">
          {activities.length} Events
        </span>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          value={search}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Filter by patient, symptom, or event..."
          className="w-full pl-9 pr-3.5 py-2.5 rounded-xl bg-[#0b101c] border border-[#27272a] text-xs text-white placeholder-slate-500 focus:outline-none focus:border-[#3f3f46]"
        />
      </div>

      {/* Filter Tabs (2-color theme: Indigo & Emerald) */}
      <div className="flex gap-1.5 overflow-x-auto pb-1 select-none">
        {[
          { id: 'ALL', label: 'All Activities' },
          { id: 'ALERTS', label: 'Clinical Alerts' },
          { id: 'MEDS', label: 'Medications & Care' },
          { id: 'SYNC', label: 'Sync & Ledger' }
        ].map((f) => (
          <button
            key={f.id}
            onClick={() => setFilter(f.id as any)}
            className={`px-3 py-1.5 rounded-xl text-[11px] font-semibold whitespace-nowrap cursor-pointer transition-colors ${
              filter === f.id
                ? 'bg-white text-black font-black text-white shadow-xs'
                : 'bg-[#0f172a] text-slate-400 border border-[#27272a] hover:text-white'
            }`}
          >
            {f.label}
          </button>
        ))}
      </div>

      {/* Activity Timeline List */}
      <div className="space-y-2.5">
        {filtered.map((act) => {
          const Icon = act.icon;
          return (
            <div
              key={act.id}
              onClick={() => {
                if (act.patientName) {
                  onSelectPatient(act.patientName);
                }
              }}
              className="p-3.5 rounded-2xl bg-[#0f172a] border border-[#27272a] hover:border-[#52525b] transition-all cursor-pointer space-y-2"
            >
              <div className="flex items-start justify-between gap-2">
                <div className="flex items-start space-x-3">
                  <div className={`w-8 h-8 rounded-xl ${act.iconBg} ${act.iconColor} flex items-center justify-center shrink-0 mt-0.5`}>
                    <Icon className="w-4 h-4" />
                  </div>
                  <div>
                    <h4 className="text-xs font-bold text-white leading-snug">{act.title}</h4>
                    <p className="text-[11px] text-slate-400 mt-0.5 leading-snug">{act.desc}</p>
                  </div>
                </div>

                <span className={`px-2 py-0.5 rounded-md border text-[9px] font-semibold whitespace-nowrap ${act.tagBg}`}>
                  {act.tag}
                </span>
              </div>

              <div className="flex items-center justify-between text-[10px] text-slate-500 pt-1 border-t border-[#27272a]/80">
                <div className="flex items-center gap-1">
                  <Clock className="w-3 h-3 text-slate-500" />
                  <span>{act.time}</span>
                </div>

                {act.patientName && (
                  <span className="text-white font-semibold flex items-center gap-0.5 hover:text-white">
                    <span>View Record</span>
                    <ChevronRight className="w-3 h-3" />
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

