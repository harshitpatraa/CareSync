import React, { useState } from 'react';
import { 
  X, 
  User, 
  MapPin, 
  Phone, 
  HeartPulse, 
  Calendar, 
  FileText, 
  AlertTriangle,
  CheckCircle2,
  Stethoscope,
  ChevronRight
} from 'lucide-react';
import { Patient } from '../types';
import { useApp } from '../context/AppContext';

interface PatientDetailModalProps {
  patient: Patient | null;
  onClose: () => void;
  onOpenSos: (patient: Patient) => void;
}

export const PatientDetailModal: React.FC<PatientDetailModalProps> = ({
  patient,
  onClose,
  onOpenSos
}) => {
  if (!patient) return null;
  const { records } = useApp();

  const patientRecords = records.filter(r => r.patientId === patient.id);

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-in fade-in-50">
      <div className="bg-white border-2 border-slate-900 rounded-3xl max-w-2xl w-full max-h-[90vh] overflow-y-auto space-y-5 p-6 shadow-2xl text-slate-800">
        {/* Header */}
        <div className="flex items-start justify-between border-b border-slate-200 pb-4">
          <div className="flex items-center space-x-3.5">
            <div className="w-12 h-12 rounded-2xl bg-slate-100 text-slate-900 border border-slate-300 flex items-center justify-center font-extrabold text-base">
              {patient.fullName.slice(0, 2).toUpperCase()}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="font-black text-lg text-[#0F172A]">{patient.fullName}</h3>
                <span className="text-xs text-slate-500 font-mono font-bold">({patient.id})</span>
              </div>
              <p className="text-xs text-slate-600 mt-0.5">
                {patient.age}y · {patient.gender} · Blood {patient.bloodGroup} · Category: <strong className="text-slate-900">{patient.category}</strong>
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-600 hover:text-black transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-2">
          <button
            onClick={() => {
              onClose();
              onOpenSos(patient);
            }}
            className="flex-1 py-2.5 rounded-xl bg-red-600 hover:bg-red-500 text-white font-bold text-xs flex items-center justify-center gap-2 shadow-md shadow-red-500/20 cursor-pointer"
          >
            <AlertTriangle className="w-4 h-4" />
            <span>Trigger 108 Emergency SOS</span>
          </button>
        </div>

        {/* Vitals Snapshot */}
        {patient.vitals && (
          <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 space-y-2">
            <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider flex items-center gap-1.5">
              <HeartPulse className="w-4 h-4 text-rose-400" />
              Latest Vitals Assessment
            </h4>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
              <div className="p-2.5 rounded-xl bg-slate-950/60 border border-slate-800">
                <span className="text-[11px] text-slate-400 block">Blood Pressure</span>
                <span className={`font-bold text-sm ${patient.vitals.systolicBp >= 140 ? 'text-red-400' : 'text-emerald-400'}`}>
                  {patient.vitals.systolicBp}/{patient.vitals.diastolicBp} mmHg
                </span>
              </div>
              <div className="p-2.5 rounded-xl bg-slate-950/60 border border-slate-800">
                <span className="text-[11px] text-slate-400 block">Oxygen SpO2</span>
                <span className={`font-bold text-sm ${patient.vitals.spo2Percent < 95 ? 'text-amber-400' : 'text-emerald-400'}`}>
                  {patient.vitals.spo2Percent}%
                </span>
              </div>
              <div className="p-2.5 rounded-xl bg-slate-950/60 border border-slate-800">
                <span className="text-[11px] text-slate-400 block">Heart Rate</span>
                <span className="font-bold text-sm text-white">
                  {patient.vitals.heartRate} bpm
                </span>
              </div>
              <div className="p-2.5 rounded-xl bg-slate-950/60 border border-slate-800">
                <span className="text-[11px] text-slate-400 block">Blood Sugar</span>
                <span className={`font-bold text-sm ${patient.vitals.bloodSugarMgDl >= 180 ? 'text-red-400' : 'text-white'}`}>
                  {patient.vitals.bloodSugarMgDl} mg/dL
                </span>
              </div>
            </div>
          </div>
        )}

        {/* Clinical Profile details */}
        <div className="p-4 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-2 text-xs">
          <div className="grid grid-cols-2 gap-3 text-slate-300">
            <div>
              <span className="text-slate-500 block">Location Ward:</span>
              <span className="font-semibold text-white">{patient.villageOrWard}</span>
            </div>
            <div>
              <span className="text-slate-500 block">Contact Phone:</span>
              <span className="font-semibold text-white">{patient.phone}</span>
            </div>
            <div>
              <span className="text-slate-500 block">Allergies:</span>
              <span className="font-semibold text-amber-300">{patient.allergies || 'None reported'}</span>
            </div>
            <div>
              <span className="text-slate-500 block">Next Scheduled Visit:</span>
              <span className="font-semibold text-white">{patient.nextFollowUpDate}</span>
            </div>
          </div>

          {patient.notes && (
            <div className="pt-2 border-t border-slate-800 mt-2">
              <span className="text-slate-500 block mb-0.5">Clinical Case Notes:</span>
              <p className="text-slate-200 leading-relaxed">{patient.notes}</p>
            </div>
          )}
        </div>

        {/* Associated Records */}
        <div className="space-y-2">
          <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center gap-1.5">
            <FileText className="w-4 h-4 text-slate-800" />
            Patient Clinical Records ({patientRecords.length})
          </h4>

          {patientRecords.length === 0 ? (
            <p className="text-xs text-slate-500 italic p-3 bg-slate-900/40 rounded-xl">No previous documents logged.</p>
          ) : (
            <div className="space-y-2">
              {patientRecords.map(r => (
                <div key={r.id} className="p-3 rounded-xl bg-slate-900 border border-slate-800 text-xs">
                  <div className="flex justify-between items-center mb-1">
                    <span className="font-bold text-white">{r.title}</span>
                    <span className="text-[10px] text-slate-500">{r.dateLabel}</span>
                  </div>
                  <p className="text-slate-400 text-[11px]">{r.summary}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
