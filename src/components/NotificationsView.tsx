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
      iconBg: 'bg-[#3b151e]',
      iconColor: 'text-[#f43f5e]',
      cardBg: 'bg-[#22121a]',
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
      iconBg: 'bg-[#29173b]',
      iconColor: 'text-[#c084fc]',
      cardBg: 'bg-[#19152b]',
      borderColor: 'border-purple-900/40',
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
      iconBg: 'bg-[#251b3d]',
      iconColor: 'text-[#a78bfa]',
      cardBg: 'bg-[#18182d]',
      borderColor: 'border-indigo-900/40',
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
      iconBg: 'bg-[#142e22]',
      iconColor: 'text-[#4ade80]',
      cardBg: 'bg-[#12241d]',
      borderColor: 'border-emerald-900/40',
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
      iconBg: 'bg-[#332015]',
      iconColor: 'text-[#fb923c]',
      cardBg: 'bg-[#241712]',
      borderColor: 'border-amber-900/40'
    },
    {
      id: 'notif-6',
      type: 'followup',
      filter: 'Follow-up',
      title: 'Follow-Up Scheduled: Sunita Rao',
      time: 'Yesterday',
      desc: 'Platelet count re-evaluation & hydration check due tomorrow.',
      icon: Calendar,
      iconBg: 'bg-[#251b3d]',
      iconColor: 'text-[#a78bfa]',
      cardBg: 'bg-[#18182d]',
      borderColor: 'border-indigo-900/40',
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
          <span className="font-bold text-sm text-white tracking-tight">Notifications</span>
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

      {/* Subheader: Reminders & Alerts + Mark all read */}
      <div className="flex items-center justify-between pt-1">
        <h3 className="text-xs font-bold text-white tracking-wide">Reminders & Alerts</h3>
        <button 
          onClick={() => setReadAll(true)}
          className="text-xs font-semibold text-purple-400 hover:text-purple-300 cursor-pointer"
        >
          {readAll ? 'All marked read' : 'Mark all read'}
        </button>
      </div>

      {/* Filter Tabs */}
      <div className="flex gap-2 overflow-x-auto pb-1 select-none">
        {(['All', 'Medicine', 'Vaccination', 'Follow-up'] as const).map((filter) => {
          const isSelected = activeFilter === filter;
          const count = filter === 'All' ? notifications.length : notifications.filter(n => n.filter === filter).length;
          return (
            <button
              key={filter}
              onClick={() => setActiveFilter(filter)}
              className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap cursor-pointer transition-colors ${
                isSelected
                  ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                  : 'bg-[#131926] text-slate-400 border border-slate-800 hover:text-slate-200'
              }`}
            >
              {filter} {filter === 'All' ? `(${count})` : ''}
            </button>
          );
        })}
      </div>

      {/* Alert Cards List */}
      <div className="space-y-2.5">
        {filteredNotifs.map((item) => {
          const Icon = item.icon;
          return (
            <div
              key={item.id}
              onClick={() => item.patientName && onSelectPatient && onSelectPatient(item.patientName)}
              className={`p-3.5 rounded-2xl ${item.cardBg} border ${item.borderColor} space-y-2 transition-transform hover:scale-[1.01] cursor-pointer`}
            >
              <div className="flex items-start justify-between gap-2.5">
                <div className="flex items-start space-x-3">
                  <div className={`w-8 h-8 rounded-xl ${item.iconBg} ${item.iconColor} flex items-center justify-center shrink-0 mt-0.5`}>
                    <Icon className="w-4 h-4" />
                  </div>
                  <div>
                    <h4 className="text-xs font-bold text-white tracking-tight">{item.title}</h4>
                    <p className="text-[11px] text-slate-300 mt-1 leading-snug">{item.desc}</p>
                  </div>
                </div>

                <span className="text-[10px] text-slate-400 whitespace-nowrap shrink-0">{item.time}</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
