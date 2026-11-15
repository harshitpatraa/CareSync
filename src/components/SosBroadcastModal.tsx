import React, { useState } from 'react';
import { X, AlertTriangle, Send, CheckCircle2 } from 'lucide-react';
import { useApp } from '../context/AppContext';
import { Patient } from '../types';

interface SosBroadcastModalProps {
  initialPatient?: Patient | null;
  onClose: () => void;
}

export const SosBroadcastModal: React.FC<SosBroadcastModalProps> = ({
  initialPatient,
  onClose
}) => {
  const { patients, triggerSos } = useApp();
  const [selectedPatientId, setSelectedPatientId] = useState<string>(initialPatient?.id || '');
  const [reason, setReason] = useState<string>('Acute Chest Pain / Myocardial Infarction Alert');
  const [isDispatched, setIsDispatched] = useState(false);
  const [resultMessage, setResultMessage] = useState('');

  const handleSend = () => {
    const res = triggerSos(selectedPatientId || undefined, reason);
    setResultMessage(res.message);
    setIsDispatched(true);
  };

  return (
    <div className="fixed inset-0 bg-black/85 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-in fade-in-50">
      <div className="bg-[#0f172a] border-2 border-red-500/60 rounded-3xl max-w-lg w-full p-6 space-y-4 shadow-2xl shadow-red-500/20">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <div className="flex items-center space-x-2 text-red-400 font-extrabold text-base">
            <AlertTriangle className="w-5 h-5 animate-bounce" />
            <span>108 Emergency SOS Network</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg text-slate-400 hover:text-white cursor-pointer">
            <X className="w-4 h-4" />
          </button>
        </div>

        {!isDispatched ? (
          <div className="space-y-4">
            <p className="text-xs text-slate-300">
              Broadcasting SOS triggers priority notification to nearest verified Medical Officers and dispatches the National 108 Emergency Ambulance to the patient's coordinates.
            </p>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Patient</label>
              <select
                value={selectedPatientId}
                onChange={(e) => setSelectedPatientId(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
              >
                <option value="">Community Walk-in / Unregistered Victim</option>
                {patients.map(p => (
                  <option key={p.id} value={p.id}>{p.fullName} ({p.villageOrWard})</option>
                ))}
              </select>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Clinical Red Flag</label>
              <select
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
              >
                <option value="Acute Chest Pain / Myocardial Infarction">Acute Chest Pain / Cardiac Distress</option>
                <option value="Severe Maternal Bleeding / PPH Alert">Severe Obstetric Hemorrhage (PPH / Eclampsia)</option>
                <option value="Critical Hypoxemia (SpO2 < 90%)">Critical Hypoxemia / Oxygen Failure</option>
                <option value="Severe Pediatric Pneumonia / Chest Indrawing">Pediatric Severe Pneumonia / Chest Indrawing</option>
                <option value="Animal / Dog Bite Rabies Exposure">Animal Bite (Rabies Category III)</option>
              </select>
            </div>

            <button
              onClick={handleSend}
              className="w-full py-3 rounded-xl bg-gradient-to-r from-red-600 to-rose-600 hover:from-red-500 hover:to-rose-500 text-white font-extrabold text-sm flex items-center justify-center space-x-2 shadow-lg shadow-red-500/40 cursor-pointer transition-all"
            >
              <AlertTriangle className="w-4 h-4" />
              <span>CONFIRM 108 SOS DISPATCH</span>
            </button>
          </div>
        ) : (
          <div className="space-y-4 text-center py-4">
            <div className="w-14 h-14 rounded-full bg-emerald-500/20 text-emerald-400 mx-auto flex items-center justify-center border border-emerald-500/40 animate-pulse">
              <CheckCircle2 className="w-8 h-8" />
            </div>
            <h4 className="font-bold text-white text-base">Alert Successfully Dispatched</h4>
            <p className="text-xs text-slate-300 max-w-sm mx-auto leading-relaxed">{resultMessage}</p>
            <button
              onClick={onClose}
              className="px-6 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold cursor-pointer"
            >
              Close
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
