import React, { useState } from 'react';
import { 
  Menu, 
  Bell, 
  AlertTriangle, 
  MessageSquare, 
  Share2, 
  Crosshair, 
  Flag, 
  Phone, 
  Plus, 
  CheckCircle2, 
  Radio
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { Patient } from '../types';

interface SosViewProps {
  onOpenDrawer?: () => void;
  onSelectPatient?: (patient: Patient) => void;
}

export const SosView: React.FC<SosViewProps> = ({ onOpenDrawer, onSelectPatient }) => {
  const { patients, triggerSos } = useApp();
  const [sosDispatched, setSosDispatched] = useState(false);
  const [copiedBrief, setCopiedBrief] = useState(false);
  const [smsSent, setSmsSent] = useState(false);
  const [showAddContact, setShowAddContact] = useState(false);

  const [contacts, setContacts] = useState([
    { id: 'c1', name: '108 National Ambulance', role: '24x7 Emergency Line', phone: '108' },
    { id: 'c2', name: 'Dr. Arvind Mehta (PHC Rampur)', role: 'On-Duty Medical Officer', phone: '+91 98765 11000' },
    { id: 'c3', name: 'Rampur Sub-District Hospital', role: 'Emergency Trauma Ward', phone: '+91 98765 22000' },
  ]);

  const [newContactName, setNewContactName] = useState('');
  const [newContactPhone, setNewContactPhone] = useState('');

  // Critical Patients list matching screenshot
  const criticalPatients = [
    {
      id: 'crit-1',
      name: 'Harish Verma · 62y',
      subtext: 'Reported chest tightness & SpO2 drop to 90%. Flagged Critical for immediate PHC transfer.',
      patientObj: patients.find(p => p.fullName.toLowerCase().includes('harish')) || patients[0]
    },
    {
      id: 'crit-2',
      name: 'Sunita Rao · 44y',
      subtext: 'Platelet count re-check scheduled; hydration ORS advised.',
      patientObj: patients.find(p => p.fullName.toLowerCase().includes('sunita')) || patients[1] || patients[0]
    }
  ];

  const handleTriggerBigSos = () => {
    setSosDispatched(true);
    triggerSos(undefined, 'Field Worker Panic / Emergency 108 Dispatch Triggered');
    if ('vibrate' in navigator) {
      navigator.vibrate([200, 100, 200]);
    }
    setTimeout(() => {
      setSosDispatched(false);
    }, 4000);
  };

  const handleShareBrief = () => {
    const brief = `[CareSync Emergency SOS]\nField Hub: Rampur Sector 4\nGPS: 28.7387, 77.1213\nActive Critical Cases: 2\nImmediate 108 Ambulance Dispatch requested.`;
    navigator.clipboard?.writeText(brief);
    setCopiedBrief(true);
    setTimeout(() => setCopiedBrief(false), 2500);
  };

  const handleSmsSos = () => {
    setSmsSent(true);
    setTimeout(() => setSmsSent(false), 2500);
  };

  const handleAddContactSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newContactName.trim() || !newContactPhone.trim()) return;
    setContacts(prev => [
      ...prev,
      {
        id: `c-${Date.now()}`,
        name: newContactName.trim(),
        role: 'Community Health Liaison',
        phone: newContactPhone.trim()
      }
    ]);
    setNewContactName('');
    setNewContactPhone('');
    setShowAddContact(false);
  };

  return (
    <div className="space-y-4">
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
          <span className="font-bold text-sm text-white tracking-tight">Emergency SOS</span>
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

      {/* Main SOS Dispatch Card */}
      <div className="p-5 rounded-3xl bg-[#14121d] border border-red-950/40 space-y-4 shadow-xl shadow-red-950/20 text-center relative overflow-hidden">
        <div>
          <h3 className="text-sm font-bold text-[#f87171] tracking-wide">
            CareSync Emergency Dispatch
          </h3>
          <p className="text-[11px] text-slate-400 mt-0.5">
            Tap SOS to alert the PHC emergency desk & log offline dispatch
          </p>
        </div>

        {/* GPS Coordinates Pill */}
        <div className="flex justify-center">
          <div className="px-3 py-1 rounded-full bg-[#1d1733] border border-purple-500/30 text-purple-300 text-[11px] font-medium flex items-center gap-1.5">
            <Crosshair className="w-3.5 h-3.5 text-purple-400" />
            <span>GPS: 28.7387, 77.1213</span>
          </div>
        </div>

        {/* Big Red Circular SOS Button */}
        <div className="flex justify-center py-2">
          <button
            onClick={handleTriggerBigSos}
            className={`w-32 h-32 rounded-full bg-[#f43f5e] hover:bg-[#e11d48] text-white shadow-[0_0_40px_-5px_rgba(244,63,94,0.6)] flex flex-col items-center justify-center cursor-pointer transition-all active:scale-95 duration-200 ${
              sosDispatched ? 'scale-105 ring-4 ring-white animate-pulse' : ''
            }`}
          >
            <AlertTriangle className="w-8 h-8 text-white fill-white" />
            <span className="text-base font-extrabold tracking-wider mt-1">SOS</span>
          </button>
        </div>

        {sosDispatched && (
          <div className="p-3 rounded-2xl bg-red-950/80 border border-red-500 text-white text-xs font-bold animate-in fade-in-50">
            🚨 SOS DISPATCH BROADCASTED! Alert sent to 108 Emergency Ambulance & On-Duty PHC Doctor!
          </div>
        )}

        {/* Bottom Actions Row: SMS SOS & Share Brief */}
        <div className="grid grid-cols-2 gap-3 pt-1">
          <button
            onClick={handleSmsSos}
            className="py-2.5 px-3 rounded-2xl bg-[#171424] border border-red-900/30 hover:border-red-500/50 text-[#f87171] text-xs font-semibold flex items-center justify-center gap-2 cursor-pointer transition-colors"
          >
            <MessageSquare className="w-3.5 h-3.5 text-red-400" />
            <span>{smsSent ? 'SMS Queued!' : 'SMS SOS (No Data)'}</span>
          </button>

          <button
            onClick={handleShareBrief}
            className="py-2.5 px-3 rounded-2xl bg-[#171424] border border-purple-900/30 hover:border-purple-500/50 text-purple-300 text-xs font-semibold flex items-center justify-center gap-2 cursor-pointer transition-colors"
          >
            <Share2 className="w-3.5 h-3.5 text-purple-400" />
            <span>{copiedBrief ? 'Copied to Clipboard!' : 'Share Brief'}</span>
          </button>
        </div>
      </div>

      {/* Critical Patient Tagging Section */}
      <div className="space-y-2.5">
        <h3 className="text-xs font-bold text-white tracking-wide">
          Critical Patient Tagging ({criticalPatients.length})
        </h3>

        <div className="space-y-2">
          {criticalPatients.map((patient) => (
            <div
              key={patient.id}
              className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800 flex items-center justify-between gap-3 hover:border-slate-700 transition-colors"
            >
              <div 
                className="flex-1 cursor-pointer"
                onClick={() => patient.patientObj && onSelectPatient && onSelectPatient(patient.patientObj)}
              >
                <h4 className="text-xs font-bold text-white tracking-tight">{patient.name}</h4>
                <p className="text-[11px] text-slate-400 mt-0.5 leading-snug">{patient.subtext}</p>
              </div>

              {/* Action Icons Matrix (Chat, Warning, Flag) */}
              <div className="flex items-center space-x-2 shrink-0">
                <button 
                  onClick={() => alert(`Opening CareSync direct SMS/Notes for ${patient.name}`)}
                  className="p-2 rounded-xl bg-purple-950/40 text-purple-400 hover:bg-purple-900/60 cursor-pointer"
                  title="SMS Note"
                >
                  <MessageSquare className="w-3.5 h-3.5" />
                </button>

                <button 
                  onClick={() => handleTriggerBigSos()}
                  className="p-2 rounded-xl bg-red-950/40 text-red-400 hover:bg-red-900/60 cursor-pointer"
                  title="Trigger SOS"
                >
                  <AlertTriangle className="w-3.5 h-3.5" />
                </button>

                <button 
                  className="p-2 rounded-xl bg-red-950/40 text-red-400 hover:bg-red-900/60 cursor-pointer"
                  title="Critical Flag Active"
                >
                  <Flag className="w-3.5 h-3.5 fill-red-400" />
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Emergency Contacts Section */}
      <div className="space-y-2.5">
        <div className="flex items-center justify-between">
          <h3 className="text-xs font-bold text-white tracking-wide">Emergency Contacts</h3>
          <button 
            onClick={() => setShowAddContact(!showAddContact)}
            className="text-xs font-semibold text-purple-400 hover:text-purple-300 cursor-pointer flex items-center gap-1"
          >
            <Plus className="w-3 h-3" />
            <span>Add Contact</span>
          </button>
        </div>

        {showAddContact && (
          <form onSubmit={handleAddContactSubmit} className="p-3.5 rounded-2xl bg-[#131926] border border-purple-500/40 space-y-2.5">
            <h4 className="text-xs font-bold text-white">Add Emergency Contact</h4>
            <input
              type="text"
              placeholder="Name or Facility (e.g. Rampur Trauma Center)"
              value={newContactName}
              onChange={(e) => setNewContactName(e.target.value)}
              className="w-full px-3 py-2 rounded-xl bg-[#0f1420] border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500"
              required
            />
            <input
              type="tel"
              placeholder="Phone Number (e.g. +91 98765 00000)"
              value={newContactPhone}
              onChange={(e) => setNewContactPhone(e.target.value)}
              className="w-full px-3 py-2 rounded-xl bg-[#0f1420] border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500"
              required
            />
            <button
              type="submit"
              className="w-full py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-semibold text-xs cursor-pointer"
            >
              Save Contact
            </button>
          </form>
        )}

        <div className="space-y-2">
          {contacts.map((contact) => (
            <div
              key={contact.id}
              className="p-3 rounded-2xl bg-[#131926] border border-slate-800 flex items-center justify-between"
            >
              <div>
                <h4 className="text-xs font-bold text-white">{contact.name}</h4>
                <p className="text-[10px] text-slate-400">{contact.role}</p>
                <p className="text-[11px] text-purple-300 font-mono mt-0.5">{contact.phone}</p>
              </div>

              <a
                href={`tel:${contact.phone}`}
                className="p-2.5 rounded-xl bg-emerald-950/40 text-emerald-400 border border-emerald-500/30 hover:bg-emerald-900/60 cursor-pointer flex items-center justify-center"
              >
                <Phone className="w-4 h-4" />
              </a>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
