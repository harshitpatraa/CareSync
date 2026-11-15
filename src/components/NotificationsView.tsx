import React, { useState } from 'react';
import { 
  Menu, 
  Bell, 
  AlertTriangle, 
  Pill, 
  Calendar, 
  Syringe, 
  RotateCw,
  Clock
} from 'lucide-react';
import { useApp } from '../context/AppContext';

interface NotificationsViewProps {
  onOpenDrawer?: () => void;
  onSelectPatient?: (patientName: string) => void;
}

export const NotificationsView: React.FC<NotificationsViewProps> = ({ onOpenDrawer, onSelectPatient }) => {
  const [activeFilter, setActiveFilter] = useState<'All' | 'Medicine' | 'Vaccination' | 'Follow-up'>('All');
  const [readAll, setReadAll] = useState(false);

  const notifications = [
    {
      id: 'notif-1',
      type: 'critical',
      filter: 'Follow-up',
      title: 'Critical SpO2 Alert: Harish Verma',
      time: '10 mins ago',
      desc: 'SpO2 dropped to 90% with BP 164/98. Immediate PHC transfer advised.',
      icon: AlertTriangle,
      iconBg: 'bg-red-950/60',
      iconColor: 'text-red-400',
      cardBg: 'bg-[#180f14]',
      borderColor: 'border-red-900/40',
      patientName: 'Harish Verma'
    },
    {
      id: 'notif-2',
      type: 'medicine',
      filter: 'Medicine',
      title: 'Medicine Reminder: Meera Devi',
      time: '35 mins ago',
      desc: 'Verify Ferrous Sulfate + Folic Acid evening dose & monitor ankle edema.',
      icon: Pill,
      iconBg: 'bg-[#27272a]',
      iconColor: 'text-white',
      cardBg: 'bg-[#18181b]',
      borderColor: 'border-[#3f3f46]',
      patientName: 'Meera Devi'
    },
    {
      id: 'notif-3',
      type: 'review',
      filter: 'Follow-up',
      title: 'Diabetic Ulcer Review: Fatima Begum',
      time: '1 hr ago',
      desc: 'Inspect dressing and check fasting blood glucose tomorrow morning.',
      icon: Calendar,
      iconBg: 'bg-[#27272a]',
      iconColor: 'text-white',
      cardBg: 'bg-[#18181b]',
      borderColor: 'border-[#3f3f46]',
      patientName: 'Fatima Begum'
    },
    {
      id: 'notif-4',
      type: 'vaccine',
      filter: 'Vaccination',
      title: 'Vaccination Due: Ananya Nair',
      time: '2 hrs ago',
      desc: 'MMR Dose 1 immunization scheduled for 19 Nov 2026 at Ward 3 Clinic.',
      icon: Syringe,
      iconBg: 'bg-emerald-950/60',
      iconColor: 'text-emerald-400',
      cardBg: 'bg-[#18181b]',
      borderColor: 'border-emerald-500/30',
      patientName: 'Ananya Nair'
    },
    {
      id: 'notif-5',
      type: 'sync',
      filter: 'Follow-up',
      title: '5 Offline Records Pending Sync',
      time: '3 hrs ago',
      desc: 'Connect to network or tap Sync Now to upload queued patient records.',
      icon: RotateCw,
      iconBg: 'bg-[#27272a]',
      iconColor: 'text-white',
      cardBg: 'bg-[#18181b]',
      borderColor: 'border-[#3f3f46]'
    },
    {
      id: 'notif-6',
      type: 'followup',
      filter: 'Follow-up',
      title: 'Follow-Up Scheduled: Sunita Rao',
      time: 'Yesterday',
      desc: 'Platelet count re-evaluation & hydration check due tomorrow.',
      icon: Calendar,
      iconBg: 'bg-[#27272a]',
      iconColor: 'text-white',
      cardBg: 'bg-[#18181b]',
      borderColor: 'border-[#3f3f46]',
      patientName: 'Sunita Rao'
    }
  ];

  const filteredNotifs = notifications.filter(n => {
    if (activeFilter === 'All') return true;
    return n.filter === activeFilter;
  });

  return (
    <div className="space-y-3.5">
      {/* Top Header Bar */}
      <div className="flex items-center justify-between py-1 border-b border-[#27272a]/80 pb-2">
        <button 
          onClick={onOpenDrawer}
          className="p-1.5 text-slate-300 hover:text-white cursor-pointer"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-2">
          {/* Logo icon (Unified 2-color: Clinical Indigo & Emerald) */}
          <div className="w-7 h-7 rounded-xl bg-indigo-600 flex items-center justify-center p-1 text-white shadow-md shadow-indigo-600/30">
            <div className="relative w-4 h-4 flex items-center justify-center">
              <div className="w-3 h-3 bg-white rounded-xs rotate-45"></div>
              <div className="absolute w-1.5 h-1.5 bg-emerald-400 rounded-full"></div>
            </div>
          </div>
          <span className="font-extrabold text-sm text-white tracking-tight">Notifications</span>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-full bg-emerald-950/60 text-emerald-400 border border-emerald-500/30 text-[10px] font-bold flex items-center gap-1">
            <span className="text-[9px]">☁️</span>
            <span>Sync</span>
          </div>
          <button className="p-1.5 text-slate-300 hover:text-white cursor-pointer">
            <Bell className="w-4 h-4 text-slate-300" />
          </button>
        </div>
      </div>

      {/* Reminders & Alerts header */}
      <div className="flex items-center justify-between pt-0.5">
        <h3 className="text-xs font-extrabold text-white uppercase tracking-wider">
          Reminders & Alerts
        </h3>
        <button
          onClick={() => setReadAll(true)}
          className="text-xs font-bold text-white hover:text-white cursor-pointer"
        >
          {readAll ? 'All Marked Read' : 'Mark all read'}
        </button>
      </div>

      {/* Filter Chips (2-Color Theme: Indigo & Emerald) */}
      <div className="flex gap-1.5 overflow-x-auto pb-1 select-none">
        {(['All', 'Medicine', 'Vaccination', 'Follow-up'] as const).map((cat) => {
          const isSelected = activeFilter === cat;
          return (
            <button
              key={cat}
              onClick={() => setActiveFilter(cat)}
              className={`px-3 py-1.5 rounded-xl text-[11px] font-bold whitespace-nowrap transition-colors cursor-pointer ${
                isSelected
                  ? 'bg-white text-black border-2 border-white shadow-xs'
                  : 'bg-[#0f172a] text-slate-300 border border-[#27272a] hover:text-white'
              }`}
            >
              {cat} {cat === 'All' ? `(${notifications.length})` : ''}
            </button>
          );
        })}
      </div>

      {/* Notification Cards List */}
      <div className="space-y-2.5">
        {filteredNotifs.map((n) => {
          const Icon = n.icon;
          return (
            <div
              key={n.id}
              onClick={() => n.patientName && onSelectPatient && onSelectPatient(n.patientName)}
              className={`p-3.5 rounded-2xl ${n.cardBg} border ${n.borderColor} flex items-start space-x-3 cursor-pointer hover:border-[#52525b] transition-colors shadow-xs`}
            >
              <div className={`w-8 h-8 rounded-xl ${n.iconBg} ${n.iconColor} flex items-center justify-center shrink-0 mt-0.5 shadow-inner`}>
                <Icon className="w-4 h-4" />
              </div>

              <div className="flex-1 space-y-1">
                <div className="flex items-start justify-between gap-1">
                  <h4 className="text-xs font-bold text-white tracking-tight leading-snug">
                    {n.title}
                  </h4>
                  <span className="text-[10px] font-medium text-slate-400 shrink-0 flex items-center gap-1">
                    <Clock className="w-2.5 h-2.5" />
                    {n.time}
                  </span>
                </div>
                <p className="text-[11px] font-medium text-slate-300 leading-snug">
                  {n.desc}
                </p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

