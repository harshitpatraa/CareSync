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
  Radio,
  ShieldAlert
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
              <span className="font-black text-sm text-[#0F172A] tracking-tight">108 Emergency Dispatch</span>
              <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-red-100 text-red-800 border border-red-300 animate-pulse">
                LIVE
              </span>
            </div>
            <p className="text-[9px] font-bold text-slate-600 leading-none">
              National Health Mission · Emergency Medical Response System (EMRS)
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-black flex items-center gap-1 shadow-xs">
            <span>☁️</span>
            <span>ABDM</span>
          </div>
          <button className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs">
            <Bell className="w-4 h-4 text-slate-700" />
          </button>
        </div>
      </div>

      {/* Main SOS Dispatch Card */}
      <div className="p-5 rounded-2xl bg-white border-2 border-red-200 space-y-4 shadow-sm text-center relative overflow-hidden">
        <div>
          <span className="text-[10px] font-black text-red-700 uppercase tracking-widest block">
            National Health Mission · Disaster & Trauma Desk
          </span>
          <h3 className="text-base font-black text-[#0F172A] mt-0.5">
            Emergency 108 Ambulance Hotline
          </h3>
          <p className="text-[11px] font-medium text-slate-600 mt-0.5">
            Instant PHC Rampur Trauma Desk Alert & Automatic Offline Telemetry Dispatch
          </p>
        </div>

        {/* GPS Coordinates Pill */}
        <div className="flex justify-center">
          <div className="px-3 py-1 rounded-full bg-slate-100 border border-slate-300 text-slate-800 text-[11px] font-black flex items-center gap-1.5 shadow-xs">
            <Crosshair className="w-3.5 h-3.5 text-slate-900" />
            <span>GPS: 28.7387, 77.1213</span>
          </div>
        </div>

        {/* Big Red Circular SOS Button */}
        <div className="flex justify-center py-2">
          <button
            onClick={handleTriggerBigSos}
            className={`w-32 h-32 rounded-full bg-red-600 hover:bg-red-700 text-white shadow-lg flex flex-col items-center justify-center cursor-pointer transition-all active:scale-95 duration-200 ${
              sosDispatched ? 'scale-105 ring-4 ring-red-400 animate-pulse' : ''
            }`}
          >
            <AlertTriangle className="w-8 h-8 text-white fill-white" />
            <span className="text-base font-black tracking-widest mt-1">SOS</span>
          </button>
        </div>

        {/* Active Emergency Dispatch & Nearest Doctor Card */}
        {sosDispatched && (
          <div className="p-4 rounded-2xl bg-red-50 border-2 border-red-300 text-left space-y-3 shadow-sm animate-in fade-in-50">
            <div className="flex items-center justify-between border-b border-red-200 pb-2">
              <div className="flex items-center gap-2 text-red-900 font-extrabold text-xs">
                <Radio className="w-4 h-4 text-red-600 animate-pulse" />
                <span>108 DISPATCH BROADCASTED · LIVE NETWORK</span>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-red-600 text-white text-[10px] font-extrabold animate-pulse">
                ETA 4 MINS
              </span>
            </div>

            {/* Nearest Doctor Profile */}
            <div className="p-3 rounded-xl bg-white border border-red-200 space-y-2 shadow-xs">
              <div className="flex justify-between items-start">
                <div>
                  <span className="text-[10px] uppercase font-bold text-red-700 tracking-wider">Nearest On-Duty Doctor</span>
                  <h4 className="text-sm font-extrabold text-[#0F172A] mt-0.5">Dr. Arvind Mehta (MBBS, MD)</h4>
                  <p className="text-[11px] font-medium text-slate-600">Senior Medical Officer · PHC Rampur</p>
                </div>
                <div className="text-right">
                  <span className="text-xs font-mono font-black text-[#15803D]">1.8 km away</span>
                  <span className="text-[10px] font-bold text-slate-500 block">On Active Duty</span>
                </div>
              </div>

              <div className="text-[11px] text-slate-700 space-y-0.5 border-t border-slate-100 pt-1.5 font-medium">
                <p><strong className="text-[#0F172A]">Facility Address:</strong> Primary Health Centre (PHC) Rampur, Block Health Complex, Main Hospital Road, Ward 2, Rampur East (Pin: 110041)</p>
                <p><strong className="text-[#0F172A]">Direct Phone:</strong> <span className="text-black font-mono font-black">+91 98765 11000</span></p>
                <p><strong className="text-[#0F172A]">Secondary Backup:</strong> Dr. Sunita Deshmukh (Obstetrics / Trauma) · +91 98765 11002 (3.2 km)</p>
              </div>

              <div className="grid grid-cols-2 gap-2 pt-1">
                <a
                  href="tel:+919876511000"
                  className="py-2.5 px-3 rounded-lg bg-[#15803D] hover:bg-[#166534] text-white font-extrabold text-xs flex items-center justify-center gap-1.5 shadow-xs cursor-pointer"
                >
                  <Phone className="w-3.5 h-3.5" />
                  <span>Call Dr. Mehta</span>
                </a>

                <button
                  onClick={() => alert("Transmitting patient telemetry and vitals to Dr. Arvind Mehta's tablet...")}
                  className="py-2.5 px-3 rounded-lg bg-black hover:bg-neutral-800 text-white font-extrabold text-xs flex items-center justify-center gap-1.5 shadow-xs cursor-pointer"
                >
                  <Radio className="w-3.5 h-3.5" />
                  <span>Send Telemetry</span>
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Bottom Actions Row: SMS SOS & Share Brief */}
        <div className="grid grid-cols-2 gap-3 pt-1">
          <button
            onClick={handleSmsSos}
            className="py-2.5 px-3 rounded-xl bg-red-50 border border-red-200 hover:bg-red-100 text-red-800 text-xs font-black flex items-center justify-center gap-2 cursor-pointer transition-colors shadow-xs"
          >
            <MessageSquare className="w-3.5 h-3.5 text-red-600" />
            <span>{smsSent ? 'SMS Queued!' : 'SMS SOS (No Data)'}</span>
          </button>

          <button
            onClick={handleShareBrief}
            className="py-2.5 px-3 rounded-xl bg-slate-100 border border-slate-300 hover:bg-slate-200 text-slate-900 text-xs font-black flex items-center justify-center gap-2 cursor-pointer transition-colors shadow-xs"
          >
            <Share2 className="w-3.5 h-3.5 text-slate-800" />
            <span>{copiedBrief ? 'Copied to Clipboard!' : 'Share Brief'}</span>
          </button>
        </div>
      </div>

      {/* Critical Patient Tagging Section */}
      <div className="space-y-2">
        <h3 className="text-xs font-black text-[#0F172A] uppercase tracking-wider">
          Critical Patient Tagging ({criticalPatients.length})
        </h3>

        <div className="space-y-2">
          {criticalPatients.map((patient) => (
            <div
              key={patient.id}
              className="p-3.5 rounded-xl bg-white border border-slate-200 flex items-center justify-between gap-3 hover:border-slate-300 transition-colors shadow-xs"
            >
              <div 
                className="flex-1 cursor-pointer"
                onClick={() => patient.patientObj && onSelectPatient && onSelectPatient(patient.patientObj)}
              >
                <h4 className="text-xs font-extrabold text-[#0F172A] tracking-tight">{patient.name}</h4>
                <p className="text-[11px] font-medium text-slate-600 mt-0.5 leading-snug">{patient.subtext}</p>
              </div>

              {/* Action Icons Matrix (Message, Warning, Flag) */}
              <div className="flex items-center space-x-1.5 shrink-0">
                <button 
                  onClick={() => alert(`Opening CareSync direct SMS/Notes for ${patient.name}`)}
                  className="p-2 rounded-lg bg-slate-100 text-slate-800 border border-slate-300 hover:bg-slate-200 cursor-pointer shadow-xs"
                  title="SMS Note"
                >
                  <MessageSquare className="w-3.5 h-3.5" />
                </button>
                <div className="p-2 rounded-lg bg-red-50 text-red-700 border border-red-200 shadow-xs">
                  <AlertTriangle className="w-3.5 h-3.5" />
                </div>
                <div className="p-2 rounded-lg bg-red-50 text-red-700 border border-red-200 shadow-xs">
                  <Flag className="w-3.5 h-3.5 fill-red-600" />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Emergency Contacts Section */}
      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <h3 className="text-xs font-black text-[#0F172A] uppercase tracking-wider">
            Emergency Contacts
          </h3>
          <button
            onClick={() => setShowAddContact(!showAddContact)}
            className="text-xs font-bold text-slate-900 hover:underline flex items-center gap-1 cursor-pointer"
          >
            <Plus className="w-3 h-3" />
            <span>Add Contact</span>
          </button>
        </div>

        {/* Add Contact inline form */}
        {showAddContact && (
          <form onSubmit={handleAddContactSubmit} className="p-3.5 rounded-xl bg-white border-2 border-slate-900 space-y-2.5 shadow-sm">
            <h4 className="text-xs font-extrabold text-[#0F172A]">Add Emergency Contact</h4>
            <div className="grid grid-cols-2 gap-2">
              <input
                type="text"
                placeholder="Full Name / Facility"
                value={newContactName}
                onChange={(e) => setNewContactName(e.target.value)}
                className="px-3 py-1.5 rounded-lg bg-slate-50 border border-slate-300 text-xs text-[#0F172A] placeholder-slate-400 font-medium focus:outline-none focus:border-black"
              />
              <input
                type="text"
                placeholder="Phone Number"
                value={newContactPhone}
                onChange={(e) => setNewContactPhone(e.target.value)}
                className="px-3 py-1.5 rounded-lg bg-slate-50 border border-slate-300 text-xs text-[#0F172A] placeholder-slate-400 font-medium focus:outline-none focus:border-black"
              />
            </div>
            <div className="flex justify-end gap-2">
              <button
                type="button"
                onClick={() => setShowAddContact(false)}
                className="px-3 py-1 rounded-lg text-xs font-bold text-slate-500 hover:text-slate-800"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-3 py-1 rounded-lg bg-black text-white text-xs font-extrabold hover:bg-neutral-800"
              >
                Save Contact
              </button>
            </div>
          </form>
        )}

        <div className="space-y-2">
          {contacts.map((contact) => (
            <div
              key={contact.id}
              className="p-3 rounded-xl bg-white border border-slate-200 flex items-center justify-between shadow-xs"
            >
              <div>
                <h4 className="text-xs font-extrabold text-[#0F172A]">{contact.name}</h4>
                <p className="text-[10px] font-medium text-slate-500">{contact.role} · {contact.phone}</p>
              </div>

              <a
                href={`tel:${contact.phone}`}
                className="p-2 rounded-lg bg-emerald-50 text-[#15803D] border border-emerald-200 hover:bg-emerald-100 cursor-pointer shadow-xs"
                title={`Call ${contact.name}`}
              >
                <Phone className="w-3.5 h-3.5" />
              </a>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
