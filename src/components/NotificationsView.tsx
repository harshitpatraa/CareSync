import React, { useState } from 'react';
import { 
  Menu, 
  Bell, 
  AlertTriangle, 
  Pill, 
  Calendar, 
  Syringe, 
  RotateCw,
  Clock,
  ShieldCheck
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
      iconBg: 'bg-red-100',
      iconColor: 'text-red-700',
      cardBg: 'bg-red-50/70',
      borderColor: 'border-red-200',
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
      iconBg: 'bg-slate-100',
      iconColor: 'text-slate-900',
      cardBg: 'bg-white',
      borderColor: 'border-slate-200',
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
      iconBg: 'bg-slate-100',
      iconColor: 'text-slate-900',
      cardBg: 'bg-white',
      borderColor: 'border-slate-200',
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
      iconBg: 'bg-emerald-50',
      iconColor: 'text-[#15803D]',
      cardBg: 'bg-white',
      borderColor: 'border-emerald-200',
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
      iconBg: 'bg-slate-100',
      iconColor: 'text-slate-900',
      cardBg: 'bg-white',
      borderColor: 'border-slate-200'
    },
    {
      id: 'notif-6',
      type: 'followup',
      filter: 'Follow-up',
      title: 'Follow-Up Scheduled: Sunita Rao',
      time: 'Yesterday',
      desc: 'Platelet count re-evaluation & hydration check due tomorrow.',
      icon: Calendar,
      iconBg: 'bg-slate-100',
      iconColor: 'text-slate-900',
      cardBg: 'bg-white',
      borderColor: 'border-slate-200',
      patientName: 'Sunita Rao'
    }
  ];

  const filteredNotifs = notifications.filter(n => {
    if (activeFilter === 'All') return true;
    return n.filter === activeFilter;
  });

  return (
    <div className="space-y-3 pb-4 select-none text-slate-800">
      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white border-y border-slate-200"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Top Header Bar - Clean Light Mode */}
      <div className="flex items-center justify-between py-1 border-b border-slate-200 pb-2">
        <div className="flex items-center space-x-2">
          <button 
            onClick={onOpenDrawer}
            className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs"
          >
            <Menu className="w-5 h-5" />
          </button>

          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-black text-sm text-[#0F172A] tracking-tight">Clinical Alerts & Notices</span>
            </div>
            <p className="text-[9px] font-bold text-slate-600 leading-none">
              National Health Mission · Ward 7 Surveillance
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-black flex items-center gap-1 shadow-xs">
            <ShieldCheck className="w-3 h-3 text-[#15803D]" />
            <span>ABDM</span>
          </div>
          <button className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs">
            <Bell className="w-4 h-4 text-slate-700" />
          </button>
        </div>
      </div>

      {/* Reminders & Alerts header */}
      <div className="flex items-center justify-between pt-0.5">
        <h3 className="text-xs font-black text-[#0F172A] uppercase tracking-wider">
          MoHFW Task Reminders
        </h3>
        <button
          onClick={() => setReadAll(true)}
          className="text-xs font-bold text-slate-900 hover:underline cursor-pointer"
        >
          {readAll ? 'All Marked Read' : 'Mark all read'}
        </button>
      </div>

      {/* Filter Chips - Clean Light Mode */}
      <div className="flex gap-1.5 overflow-x-auto pb-1 select-none scrollbar-none">
        {(['All', 'Medicine', 'Vaccination', 'Follow-up'] as const).map((cat) => {
          const isSelected = activeFilter === cat;
          return (
            <button
              key={cat}
              onClick={() => setActiveFilter(cat)}
              className={`px-3 py-1.5 rounded-lg text-[11px] font-black whitespace-nowrap transition-colors cursor-pointer shadow-xs ${
                isSelected
                  ? 'bg-black text-white border border-black'
                  : 'bg-white text-slate-700 border border-slate-200 hover:bg-slate-50'
              }`}
            >
              {cat} {cat === 'All' ? `(${notifications.length})` : ''}
            </button>
          );
        })}
      </div>

      {/* Notification Cards List */}
      <div className="space-y-2">
        {filteredNotifs.map((n) => {
          const Icon = n.icon;
          return (
            <div
              key={n.id}
              onClick={() => n.patientName && onSelectPatient && onSelectPatient(n.patientName)}
              className={`p-3.5 rounded-xl ${n.cardBg} border ${n.borderColor} flex items-start space-x-3 cursor-pointer hover:shadow-sm transition-all shadow-xs`}
            >
              <div className={`w-8 h-8 rounded-lg ${n.iconBg} ${n.iconColor} flex items-center justify-center shrink-0 mt-0.5 shadow-inner`}>
                <Icon className="w-4 h-4" />
              </div>

              <div className="flex-1 space-y-0.5">
                <div className="flex items-start justify-between gap-1">
                  <h4 className="text-xs font-bold text-[#0F172A] tracking-tight leading-snug">
                    {n.title}
                  </h4>
                  <span className="text-[10px] font-medium text-slate-500 shrink-0 flex items-center gap-1">
                    <Clock className="w-2.5 h-2.5" />
                    {n.time}
                  </span>
                </div>
                <p className="text-[11px] font-medium text-slate-600 leading-snug">
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
