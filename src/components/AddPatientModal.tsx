import React, { useState } from 'react';
import { X, Plus, User, HeartPulse, MapPin } from 'lucide-react';
import { useApp } from '../context/AppContext';
import { PatientCategory, RiskLevel } from '../types';

interface AddPatientModalProps {
  onClose: () => void;
}

export const AddPatientModal: React.FC<AddPatientModalProps> = ({ onClose }) => {
  const { addPatient } = useApp();

  const [fullName, setFullName] = useState('');
  const [age, setAge] = useState(25);
  const [gender, setGender] = useState('Female');
  const [phone, setPhone] = useState('+91 ');
  const [villageOrWard, setVillageOrWard] = useState('Ward 4 · Rampur East');
  const [bloodGroup, setBloodGroup] = useState('B+');
  const [category, setCategory] = useState<PatientCategory>('PREGNANCY');
  const [riskLevel, setRiskLevel] = useState<RiskLevel>('LOW');
  const [chronicConditions, setChronicConditions] = useState('');
  const [allergies, setAllergies] = useState('None');
  const [notes, setNotes] = useState('');

  // Vitals
  const [systolic, setSystolic] = useState(120);
  const [diastolic, setDiastolic] = useState(80);
  const [spo2, setSpo2] = useState(98);
  const [sugar, setSugar] = useState(105);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim()) return;

    addPatient({
      fullName: fullName.trim(),
      age: Number(age),
      gender,
      phone: phone.trim(),
      villageOrWard,
      bloodGroup,
      category,
      riskLevel,
      isCritical: riskLevel === 'CRITICAL',
      isTodayVisit: true,
      isPendingVisit: false,
      syncStatus: 'PENDING_UPLOAD',
      lastVisitLabel: 'Today, Just now',
      nextFollowUpDate: 'In 7 Days',
      allergies,
      chronicConditions,
      familyHeadName: fullName.trim(),
      familyMembersCount: 4,
      vaccinationProgress: 90,
      notes: notes.trim() || 'New registration recorded via CareSync Portal.',
      vitals: {
        id: `VT-${Date.now()}`,
        patientId: '',
        systolicBp: systolic,
        diastolicBp: diastolic,
        heartRate: 76,
        spo2Percent: spo2,
        temperatureC: 36.8,
        bloodSugarMgDl: sugar,
        recordedAt: Date.now()
      }
    });

    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/80 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-in fade-in-50">
      <div className="bg-[#0f172a] border border-slate-800 rounded-3xl max-w-xl w-full max-h-[90vh] overflow-y-auto space-y-4 p-6 shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h3 className="font-bold text-base text-white flex items-center gap-2">
            <User className="w-4 h-4 text-blue-400" />
            Register Community Patient
          </h3>
          <button onClick={onClose} className="p-1.5 rounded-lg text-slate-400 hover:text-white cursor-pointer">
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Full Name</label>
              <input
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="e.g. Meera Devi"
                className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
                required
              />
            </div>
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Age (Years)</label>
              <input
                type="number"
                value={age}
                onChange={(e) => setAge(Number(e.target.value))}
                className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Gender</label>
              <select
                value={gender}
                onChange={(e) => setGender(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
              >
                <option value="Female">Female</option>
                <option value="Male">Male</option>
                <option value="Other">Other</option>
              </select>
            </div>
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Category</label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value as any)}
                className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
              >
                <option value="PREGNANCY">Pregnancy (ANC)</option>
                <option value="CHILD">Child Health</option>
                <option value="CHRONIC">Chronic Care</option>
                <option value="GENERAL">General</option>
                <option value="EMERGENCY">Emergency</option>
              </select>
            </div>
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Blood Group</label>
              <select
                value={bloodGroup}
                onChange={(e) => setBloodGroup(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
              >
                <option value="O+">O+</option>
                <option value="A+">A+</option>
                <option value="B+">B+</option>
                <option value="AB+">AB+</option>
                <option value="O-">O-</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Phone Number</label>
              <input
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
              />
            </div>
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Village / Ward</label>
              <input
                type="text"
                value={villageOrWard}
                onChange={(e) => setVillageOrWard(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
              />
            </div>
          </div>

          {/* Vitals inputs */}
          <div className="p-3.5 rounded-2xl bg-slate-900 border border-slate-800 space-y-2">
            <span className="text-xs font-bold text-slate-300 block">Baseline Vitals Measurement</span>
            <div className="grid grid-cols-4 gap-2 text-xs">
              <div>
                <label className="text-[10px] text-slate-400 block">Systolic BP</label>
                <input
                  type="number"
                  value={systolic}
                  onChange={(e) => setSystolic(Number(e.target.value))}
                  className="w-full px-2 py-1.5 rounded-lg bg-slate-950 border border-slate-700 text-white"
                />
              </div>
              <div>
                <label className="text-[10px] text-slate-400 block">Diastolic BP</label>
                <input
                  type="number"
                  value={diastolic}
                  onChange={(e) => setDiastolic(Number(e.target.value))}
                  className="w-full px-2 py-1.5 rounded-lg bg-slate-950 border border-slate-700 text-white"
                />
              </div>
              <div>
                <label className="text-[10px] text-slate-400 block">SpO2 (%)</label>
                <input
                  type="number"
                  value={spo2}
                  onChange={(e) => setSpo2(Number(e.target.value))}
                  className="w-full px-2 py-1.5 rounded-lg bg-slate-950 border border-slate-700 text-white"
                />
              </div>
              <div>
                <label className="text-[10px] text-slate-400 block">Sugar (mg/dL)</label>
                <input
                  type="number"
                  value={sugar}
                  onChange={(e) => setSugar(Number(e.target.value))}
                  className="w-full px-2 py-1.5 rounded-lg bg-slate-950 border border-slate-700 text-white"
                />
              </div>
            </div>
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1">Clinical Notes & Conditions</label>
            <input
              type="text"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="e.g. Mild gestational anemia, BP borderline"
              className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
            />
          </div>

          <div className="flex gap-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold cursor-pointer"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="flex-1 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold shadow-md shadow-blue-500/20 cursor-pointer"
            >
              Register & Save
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
