import React, { useState } from 'react';
import { 
  ArrowLeft, 
  Bell, 
  CheckCircle2, 
  CloudCheck,
  User,
  HeartPulse
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { PatientCategory, RiskLevel } from '../types';

interface PatientRegistrationViewProps {
  onBack: () => void;
  onSuccess: () => void;
}

export const PatientRegistrationView: React.FC<PatientRegistrationViewProps> = ({
  onBack,
  onSuccess
}) => {
  const { addPatient } = useApp();

  const [fullName, setFullName] = useState('');
  const [age, setAge] = useState<number>(28);
  const [bloodGroup, setBloodGroup] = useState('O+');
  const [gender, setGender] = useState('Female');
  const [phone, setPhone] = useState('');
  const [villageOrWard, setVillageOrWard] = useState('Ward 4 · Rampur East');
  const [category, setCategory] = useState<PatientCategory>('GENERAL');
  const [isCritical, setIsCritical] = useState(false);
  const [systolic, setSystolic] = useState<number>(120);
  const [diastolic, setDiastolic] = useState<number>(80);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim()) return;

    addPatient({
      fullName: fullName.trim(),
      age: Number(age),
      gender,
      phone: phone.trim() || '+91 98765 00000',
      villageOrWard: villageOrWard.trim(),
      bloodGroup,
      category,
      riskLevel: isCritical ? 'CRITICAL' : 'LOW',
      isCritical,
      isTodayVisit: true,
      isPendingVisit: false,
      syncStatus: 'PENDING_UPLOAD',
      lastVisitLabel: 'Today, Just now',
      nextFollowUpDate: 'In 7 Days',
      allergies: 'None',
      chronicConditions: isCritical ? 'Critical emergency priority watch' : 'None',
      familyHeadName: fullName.trim(),
      familyMembersCount: 4,
      vaccinationProgress: 90,
      notes: 'Registered via CareSync Patient Registration Form.',
      vitals: {
        id: `VT-${Date.now()}`,
        patientId: '',
        systolicBp: systolic,
        diastolicBp: diastolic,
        heartRate: 76,
        spo2Percent: 98,
        temperatureC: 36.8,
        bloodSugarMgDl: 105,
        recordedAt: Date.now()
      }
    });

    onSuccess();
  };

  return (
    <div className="space-y-4">
      {/* Top Header Bar */}
      <div className="flex items-center justify-between py-1 border-b border-slate-800/60 pb-2">
        <button onClick={onBack} className="p-1.5 text-slate-300 hover:text-white cursor-pointer">
          <ArrowLeft className="w-5 h-5" />
        </button>

        <h2 className="font-bold text-base text-white tracking-tight">Patient Registration</h2>

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

      {/* Offline-First Green Banner */}
      <div className="p-3 rounded-2xl bg-[#0f241a] border border-[#22c55e]/30 flex items-start gap-2.5">
        <span className="text-base text-[#4ade80]">☁️</span>
        <p className="text-[11px] text-[#4ade80] leading-relaxed">
          Offline-First Registration: Saves immediately to local Room storage and queues for CareSync upload.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Demographics & Category Card */}
        <div className="p-4 rounded-3xl bg-[#131926] border border-slate-800 space-y-3.5">
          <h3 className="text-xs font-bold text-white">Demographics & Category</h3>

          {/* Full Name */}
          <div className="relative">
            <input
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="Patient Full Name *"
              required
              className="w-full px-3.5 py-3 rounded-2xl bg-[#101421] border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-blue-500"
            />
          </div>

          {/* Age & Blood Group */}
          <div className="grid grid-cols-2 gap-3">
            <div className="p-2.5 rounded-2xl bg-[#101421] border border-slate-800">
              <label className="text-[10px] text-slate-400 block mb-0.5">Age</label>
              <input
                type="number"
                value={age}
                onChange={(e) => setAge(Number(e.target.value))}
                className="w-full bg-transparent text-xs text-white font-semibold focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-2xl bg-[#101421] border border-slate-800">
              <label className="text-[10px] text-slate-400 block mb-0.5">Blood Group</label>
              <select
                value={bloodGroup}
                onChange={(e) => setBloodGroup(e.target.value)}
                className="w-full bg-transparent text-xs text-white font-semibold focus:outline-none cursor-pointer"
              >
                <option value="O+" className="bg-slate-900">O+</option>
                <option value="A+" className="bg-slate-900">A+</option>
                <option value="B+" className="bg-slate-900">B+</option>
                <option value="AB+" className="bg-slate-900">AB+</option>
                <option value="O-" className="bg-slate-900">O-</option>
              </select>
            </div>
          </div>

          {/* Gender Pills */}
          <div className="flex gap-2">
            {['Female', 'Male', 'Other'].map((g) => {
              const isSelected = gender === g;
              return (
                <button
                  key={g}
                  type="button"
                  onClick={() => setGender(g)}
                  className={`flex-1 py-2 rounded-xl text-xs font-semibold cursor-pointer transition-colors ${
                    isSelected
                      ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                      : 'bg-[#101421] text-slate-400 border border-slate-800'
                  }`}
                >
                  {g}
                </button>
              );
            })}
          </div>

          {/* Phone Number */}
          <input
            type="text"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="Phone Number"
            className="w-full px-3.5 py-3 rounded-2xl bg-[#101421] border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-blue-500"
          />

          {/* Village / Ward */}
          <div className="p-2.5 rounded-2xl bg-[#101421] border border-slate-800">
            <label className="text-[10px] text-slate-400 block mb-0.5">Village / Ward</label>
            <input
              type="text"
              value={villageOrWard}
              onChange={(e) => setVillageOrWard(e.target.value)}
              className="w-full bg-transparent text-xs text-white font-semibold focus:outline-none"
            />
          </div>

          {/* Care Program Category */}
          <div className="space-y-1.5">
            <span className="text-xs text-slate-300 font-semibold block">Care Program Category</span>
            <div className="flex gap-2 overflow-x-auto pb-1">
              {[
                { label: 'General', val: 'GENERAL' },
                { label: 'Pregnancy', val: 'PREGNANCY' },
                { label: 'Child Care', val: 'CHILD' },
                { label: 'Chronic', val: 'CHRONIC' },
              ].map((c) => {
                const isSelected = category === c.val;
                return (
                  <button
                    key={c.val}
                    type="button"
                    onClick={() => setCategory(c.val as any)}
                    className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap cursor-pointer transition-colors ${
                      isSelected
                        ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                        : 'bg-[#101421] text-slate-400 border border-slate-800'
                    }`}
                  >
                    {c.label}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Tag as Critical Priority Switch */}
          <div className="pt-2 border-t border-slate-800 flex items-center justify-between">
            <div>
              <span className="text-xs font-semibold text-white block">Tag as Critical Priority</span>
              <p className="text-[10px] text-slate-400">Flags patient in Emergency SOS list</p>
            </div>

            <label className="relative inline-flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={isCritical}
                onChange={(e) => setIsCritical(e.target.checked)}
                className="sr-only peer"
              />
              <div className="w-11 h-6 bg-slate-800 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-purple-600"></div>
            </label>
          </div>
        </div>

        {/* Baseline Vitals Card */}
        <div className="p-4 rounded-3xl bg-[#131926] border border-slate-800 space-y-3">
          <h3 className="text-xs font-bold text-white">Baseline Vitals</h3>

          <div className="grid grid-cols-2 gap-3">
            <div className="p-2.5 rounded-2xl bg-[#101421] border border-slate-800">
              <label className="text-[10px] text-slate-400 block mb-0.5">Systolic BP</label>
              <input
                type="number"
                value={systolic}
                onChange={(e) => setSystolic(Number(e.target.value))}
                className="w-full bg-transparent text-xs text-white font-semibold focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-2xl bg-[#101421] border border-slate-800">
              <label className="text-[10px] text-slate-400 block mb-0.5">Diastolic BP</label>
              <input
                type="number"
                value={diastolic}
                onChange={(e) => setDiastolic(Number(e.target.value))}
                className="w-full bg-transparent text-xs text-white font-semibold focus:outline-none"
              />
            </div>
          </div>
        </div>

        {/* Submit Button */}
        <button
          type="submit"
          className="w-full py-3.5 rounded-2xl bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white font-bold text-sm shadow-xl shadow-purple-500/20 cursor-pointer transition-all"
        >
          Save & Register Patient
        </button>
      </form>
    </div>
  );
};
