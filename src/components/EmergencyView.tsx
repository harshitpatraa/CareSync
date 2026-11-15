import React, { useState } from 'react';
import { 
  AlertTriangle, 
  Phone, 
  MapPin, 
  Clock, 
  Send, 
  CheckCircle2, 
  Radio, 
  ShieldAlert,
  Building2,
  Navigation
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { Patient, NearbyClinician } from '../types';

interface EmergencyViewProps {
  onSelectPatient?: (patient: Patient) => void;
}

export const EmergencyView: React.FC<EmergencyViewProps> = () => {
  const { patients, emergencyContacts, nearbyCenters, nearbyClinicians, triggerSos } = useApp();
  const [selectedPatientId, setSelectedPatientId] = useState<string>('');
  const [sosReason, setSosReason] = useState<string>('Acute Chest Pain / Cardiac Warning (108)');
  const [dispatchResult, setDispatchResult] = useState<{ success: boolean; message: string; notifiedClinicians: NearbyClinician[] } | null>(null);

  const handleBroadcast = () => {
    const res = triggerSos(selectedPatientId || undefined, sosReason);
    setDispatchResult(res);
  };

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      {/* Top Banner Alert */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-red-950/60 via-rose-950/40 to-slate-900 border-2 border-red-500/50 space-y-4 shadow-2xl shadow-red-500/10">
        <div className="flex items-center space-x-3.5">
          <div className="w-12 h-12 rounded-2xl bg-red-600 flex items-center justify-center text-white shadow-lg shadow-red-500/40 animate-pulse">
            <Radio className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-lg font-extrabold text-white tracking-tight flex items-center gap-2">
              National Emergency SOS & Clinician Dispatch
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-red-500/20 text-red-400 border border-red-500/40">
                108 Live Network
              </span>
            </h2>
            <p className="text-xs text-slate-300 mt-0.5">
              Instantly transmits patient GPS coordinates, vitals snapshot, and emergency flags to nearest on-duty doctor & 108 ambulance
            </p>
          </div>
        </div>

        {/* SOS Action Controls */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1">Select Patient in Distress (Optional)</label>
            <select
              value={selectedPatientId}
              onChange={(e) => setSelectedPatientId(e.target.value)}
              className="w-full px-3 py-2.5 rounded-xl bg-slate-900 border border-red-500/40 text-xs text-white"
            >
              <option value="">Emergency Walk-in / Community Victim</option>
              {patients.map(p => (
                <option key={p.id} value={p.id}>{p.fullName} ({p.villageOrWard}) - {p.category}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1">Emergency Danger Triage Flag</label>
            <select
              value={sosReason}
              onChange={(e) => setSosReason(e.target.value)}
              className="w-full px-3 py-2.5 rounded-xl bg-slate-900 border border-red-500/40 text-xs text-white"
            >
              <option value="Acute Chest Pain / Cardiac Warning (108)">Acute Chest Pain / Myocardial Infarction</option>
              <option value="Severe Obstetric Bleeding / Eclampsia Alert">Obstetric Bleeding / Convulsions in Pregnancy</option>
              <option value="Critical Hypoxemia (SpO2 < 90%) / Severe Dyspnea">Hypoxemic Respiratory Failure (SpO2 &lt; 90%)</option>
              <option value="Severe Anaphylaxis / Unconscious / Shock">Severe Anaphylaxis / Unresponsive Shock</option>
              <option value="Category III Animal / Dog Bite Rabies">Category III Dog Bite Rabies Exposure</option>
            </select>
          </div>
        </div>

        <button
          onClick={handleBroadcast}
          className="w-full py-3.5 px-4 rounded-2xl bg-gradient-to-r from-red-600 to-rose-600 hover:from-red-500 hover:to-rose-500 text-white font-extrabold text-sm flex items-center justify-center space-x-2 shadow-xl shadow-red-500/30 transition-all transform active:scale-98 cursor-pointer"
        >
          <AlertTriangle className="w-5 h-5 animate-bounce" />
          <span>BROADCAST IMMEDIATE 108 SOS DISPATCH</span>
        </button>
      </div>

      {/* Confirmation feedback */}
      {dispatchResult && (
        <div className="p-5 rounded-2xl bg-emerald-950/30 border border-emerald-500/50 space-y-3 animate-in fade-in-50">
          <div className="flex items-center gap-2 text-emerald-400 font-bold text-sm">
            <CheckCircle2 className="w-5 h-5" />
            <span>SOS Notification Confirmed & Acknowledged</span>
          </div>
          <p className="text-xs text-slate-200">{dispatchResult.message}</p>
        </div>
      )}

      {/* On-Duty Nearest Clinicians */}
      <div className="space-y-3">
        <h3 className="font-bold text-base text-white flex items-center gap-2">
          <Navigation className="w-4 h-4 text-blue-400" />
          Nearest On-Duty Clinicians (GPS Proximity)
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {nearbyClinicians.map((doc) => (
            <div
              key={doc.id}
              className="p-4 rounded-2xl bg-[#0f172a]/70 border border-slate-800 flex items-center justify-between"
            >
              <div className="flex items-center space-x-3">
                <div className="w-10 h-10 rounded-xl bg-blue-600/20 text-blue-400 border border-blue-500/30 flex items-center justify-center font-bold text-xs">
                  {doc.name.slice(4, 6)}
                </div>
                <div>
                  <h4 className="font-bold text-sm text-white">{doc.name}</h4>
                  <p className="text-xs text-slate-400">{doc.role} · {doc.healthCentre}</p>
                  <div className="flex items-center gap-2 mt-1 text-[11px] text-emerald-400 font-medium">
                    <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                    <span>{doc.distanceKm} km away (ETA: {doc.etaMinutes} mins)</span>
                  </div>
                </div>
              </div>

              <a
                href={`tel:${doc.phone}`}
                className="p-2.5 rounded-xl bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 border border-blue-500/30 cursor-pointer transition-colors"
                title="Call Clinician"
              >
                <Phone className="w-4 h-4" />
              </a>
            </div>
          ))}
        </div>
      </div>

      {/* Emergency Centers & 24/7 Casualty */}
      <div className="space-y-3">
        <h3 className="font-bold text-base text-white flex items-center gap-2">
          <Building2 className="w-4 h-4 text-cyan-400" />
          Government Referral Hospitals & 24/7 Casualty Desks
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {nearbyCenters.map((hc) => (
            <div
              key={hc.id}
              className="p-4 rounded-2xl bg-[#0f172a]/70 border border-slate-800 flex items-center justify-between"
            >
              <div>
                <h4 className="font-bold text-sm text-white">{hc.name}</h4>
                <p className="text-xs text-slate-400 mt-0.5">{hc.type} · {hc.distanceKm}</p>
                <p className="text-[11px] text-emerald-400 font-medium mt-1">{hc.availability}</p>
              </div>

              <a
                href={`tel:${hc.phone}`}
                className="p-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 cursor-pointer transition-colors"
              >
                <Phone className="w-4 h-4" />
              </a>
            </div>
          ))}
        </div>
      </div>

      {/* Toll-Free Helplines */}
      <div className="p-5 rounded-2xl bg-[#0f172a]/60 border border-slate-800">
        <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">
          National Emergency Toll-Free Hotlines
        </h4>
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          {emergencyContacts.map((c) => (
            <a
              key={c.id}
              href={`tel:${c.phone}`}
              className="p-3 rounded-xl bg-slate-900 border border-slate-800 hover:border-blue-500/40 transition-colors flex items-center justify-between text-xs cursor-pointer group"
            >
              <div>
                <span className="font-bold text-white group-hover:text-blue-400 transition-colors">{c.name}</span>
                <span className="text-[11px] text-slate-400 block mt-0.5">{c.role}</span>
              </div>
              <span className="px-2 py-1 rounded bg-red-500/20 text-red-400 font-mono font-bold text-xs">
                {c.phone}
              </span>
            </a>
          ))}
        </div>
      </div>
    </div>
  );
};
