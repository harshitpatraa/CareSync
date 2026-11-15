import React, { useState } from 'react';
import { 
  FileText, 
  Search, 
  Plus, 
  Download, 
  Tag, 
  Calendar, 
  User, 
  Building2,
  FileCheck,
  Stethoscope
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { HealthRecord } from '../types';

export const RecordsView: React.FC = () => {
  const { records, addHealthRecord, patients } = useApp();
  const [searchTerm, setSearchTerm] = useState('');
  const [typeFilter, setTypeFilter] = useState<string>('ALL');
  const [showAddModal, setShowAddModal] = useState(false);

  // New Record Form State
  const [selectedPatientId, setSelectedPatientId] = useState(patients[0]?.id || '');
  const [recordType, setRecordType] = useState<HealthRecord['recordType']>('PRESCRIPTION');
  const [title, setTitle] = useState('');
  const [summary, setSummary] = useState('');
  const [doctorName, setDoctorName] = useState('Dr. Priya Kulkarni, CHO');
  const [facilityName, setFacilityName] = useState('CareSync PHC Hub · Ward 4');

  const recordTypes = [
    { label: 'All Records', value: 'ALL' },
    { label: 'Prescriptions', value: 'PRESCRIPTION' },
    { label: 'Referrals', value: 'REFERRAL' },
    { label: 'Lab Reports', value: 'LAB_REPORT' },
    { label: 'Immunization', value: 'IMMUNIZATION' },
  ];

  const filteredRecords = records.filter(r => {
    const matchesSearch = r.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      r.patientName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      r.summary.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesType = typeFilter === 'ALL' || r.recordType === typeFilter;
    return matchesSearch && matchesType;
  });

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !summary.trim()) return;

    const patient = patients.find(p => p.id === selectedPatientId);
    addHealthRecord({
      patientId: selectedPatientId,
      patientName: patient ? patient.fullName : 'Community Walk-in',
      recordType: recordType,
      title: title.trim(),
      summary: summary.trim(),
      dateLabel: 'Today, Just now',
      doctorName: doctorName.trim(),
      facilityName: facilityName.trim(),
      tags: [recordType, 'MoHFW']
    });

    setTitle('');
    setSummary('');
    setShowAddModal(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-xl font-bold text-white flex items-center gap-2">
            <FileText className="w-5 h-5 text-blue-400" />
            Clinical Health Vault
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Encrypted offline health records, referral slips, prescriptions & diagnostic reports
          </p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-semibold text-xs flex items-center gap-2 transition-all shadow-md shadow-blue-500/20 cursor-pointer"
        >
          <Plus className="w-4 h-4" />
          <span>Add Clinical Record</span>
        </button>
      </div>

      {/* Search & Filters */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search records by title, patient, or medication..."
            className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-[#0f172a]/80 border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-blue-500"
          />
        </div>

        <div className="flex gap-1.5 overflow-x-auto pb-1 max-w-full">
          {recordTypes.map((t) => (
            <button
              key={t.value}
              onClick={() => setTypeFilter(t.value)}
              className={`px-3 py-2 rounded-xl text-xs font-semibold whitespace-nowrap transition-colors cursor-pointer ${
                typeFilter === t.value
                  ? 'bg-blue-600 text-white'
                  : 'bg-slate-900 text-slate-400 hover:bg-slate-800 border border-slate-800'
              }`}
            >
              {t.label}
            </button>
          ))}
        </div>
      </div>

      {/* Records Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filteredRecords.map((r) => (
          <div
            key={r.id}
            className="p-5 rounded-2xl bg-[#0f172a]/70 border border-slate-800 hover:border-blue-500/40 transition-all space-y-3"
          >
            <div className="flex items-start justify-between">
              <div>
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-blue-500/15 text-blue-400 border border-blue-500/20 uppercase">
                  {r.recordType}
                </span>
                <h3 className="font-bold text-sm text-white mt-1.5">{r.title}</h3>
                <p className="text-xs text-slate-400 mt-0.5 flex items-center gap-1.5">
                  <User className="w-3.5 h-3.5 text-slate-500" />
                  <span>Patient: <strong>{r.patientName}</strong> ({r.patientId})</span>
                </p>
              </div>

              <span className="text-[11px] text-slate-500 shrink-0">{r.dateLabel}</span>
            </div>

            <p className="text-xs text-slate-300 leading-relaxed bg-slate-900/80 p-3 rounded-xl border border-slate-800/80">
              {r.summary}
            </p>

            <div className="flex items-center justify-between text-[11px] text-slate-400 pt-2 border-t border-slate-800">
              <span className="flex items-center gap-1">
                <Building2 className="w-3 h-3 text-slate-500" />
                {r.facilityName}
              </span>
              <span>By {r.doctorName}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Add Record Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/75 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-in fade-in-50">
          <div className="bg-[#0f172a] border border-slate-800 rounded-3xl p-6 max-w-lg w-full space-y-4 shadow-2xl">
            <h3 className="font-bold text-base text-white">Create New Clinical Health Record</h3>

            <form onSubmit={handleCreate} className="space-y-3.5">
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Target Patient</label>
                <select
                  value={selectedPatientId}
                  onChange={(e) => setSelectedPatientId(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
                >
                  {patients.map(p => (
                    <option key={p.id} value={p.id}>{p.fullName} ({p.villageOrWard})</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300 block mb-1">Record Type</label>
                  <select
                    value={recordType}
                    onChange={(e) => setRecordType(e.target.value as any)}
                    className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
                  >
                    <option value="PRESCRIPTION">Prescription</option>
                    <option value="REFERRAL">Referral Slip</option>
                    <option value="LAB_REPORT">Lab Report</option>
                    <option value="IMMUNIZATION">Immunization</option>
                    <option value="CLINICAL_NOTE">Clinical Note</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300 block mb-1">Doctor / Officer</label>
                  <input
                    type="text"
                    value={doctorName}
                    onChange={(e) => setDoctorName(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
                  />
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Record Title</label>
                <input
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="e.g. Trimester 3 ANC Prescription & Labs"
                  className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
                  required
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Clinical Summary / Dosage</label>
                <textarea
                  value={summary}
                  onChange={(e) => setSummary(e.target.value)}
                  rows={3}
                  placeholder="Enter medical orders, dosages, findings or emergency referral instructions..."
                  className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-700 text-xs text-white"
                  required
                />
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="flex-1 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold cursor-pointer shadow-md shadow-blue-500/20"
                >
                  Save to Vault
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
