import React from 'react';
import { 
  Activity, 
  AlertTriangle, 
  MapPin, 
  TrendingUp, 
  ShieldAlert, 
  CheckCircle2,
  Users,
  Compass
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export const AnalyticsView: React.FC = () => {
  const { wardOutbreak, patients } = useApp();

  const totalPatients = patients.length;
  const criticalWards = ["Ward 6 · Shivaji Chowk", "Ward 4 · Rampur East"];

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      {/* Header */}
      <div>
        <h2 className="text-xl font-bold text-white flex items-center gap-2">
          <Activity className="w-5 h-5 text-purple-400" />
          Epidemiological Outbreak Radar & Community Surveillance
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Real-time cluster detection across local community wards according to Integrated Disease Surveillance Programme (IDSP)
        </p>
      </div>

      {/* Outbreak Status Card */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-purple-950/40 via-blue-950/40 to-slate-900 border border-purple-500/40 space-y-4 shadow-xl">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="p-3 rounded-2xl bg-purple-500/20 text-purple-400">
              <Compass className="w-6 h-6 animate-spin [animation-duration:12s]" />
            </div>
            <div>
              <h3 className="font-bold text-base text-white">{wardOutbreak.headline}</h3>
              <p className="text-xs text-slate-300 mt-0.5">{wardOutbreak.summary}</p>
            </div>
          </div>

          <span className="text-xs font-bold px-3 py-1 rounded-full bg-rose-500/20 text-rose-400 border border-rose-500/40 shrink-0">
            {wardOutbreak.alertLevel} ALERT
          </span>
        </div>

        {/* Affected Ward Badges */}
        <div className="flex flex-wrap gap-2 pt-2 border-t border-slate-800">
          <span className="text-xs font-semibold text-slate-400">Cluster Epicenters:</span>
          {wardOutbreak.affectedWards.map((w, idx) => (
            <span key={idx} className="text-xs px-2.5 py-1 rounded-lg bg-slate-900 text-amber-300 border border-amber-500/30 font-medium flex items-center gap-1.5">
              <MapPin className="w-3.5 h-3.5 text-amber-400" />
              {w}
            </span>
          ))}
        </div>
      </div>

      {/* Recommended Community Interventions */}
      <div className="p-5 rounded-2xl bg-[#0f172a]/80 border border-slate-800 space-y-3">
        <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider">
          Actionable Public Health Counter-Measures (IDSP / NVBDCP):
        </h4>
        <div className="space-y-2">
          {wardOutbreak.recommendations.map((rec, idx) => (
            <div key={idx} className="p-3 rounded-xl bg-slate-900/80 border border-slate-800 flex items-start gap-2.5 text-xs text-slate-200">
              <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
              <span>{rec}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Ward Metric Breakdown Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-5 rounded-2xl bg-[#0f172a]/70 border border-slate-800">
          <span className="text-xs font-semibold text-slate-400">Surveillance Cohort</span>
          <div className="text-2xl font-bold text-white mt-1">{totalPatients} Patients</div>
          <p className="text-[11px] text-slate-500 mt-1">Screened across 5 community wards</p>
        </div>

        <div className="p-5 rounded-2xl bg-[#0f172a]/70 border border-slate-800">
          <span className="text-xs font-semibold text-slate-400">Acute Febrile Watch</span>
          <div className="text-2xl font-bold text-amber-400 mt-1">2 Active Cases</div>
          <p className="text-[11px] text-slate-500 mt-1">Platelet count surveillance under PHC</p>
        </div>

        <div className="p-5 rounded-2xl bg-[#0f172a]/70 border border-slate-800">
          <span className="text-xs font-semibold text-slate-400">Pending Immunization</span>
          <div className="text-2xl font-bold text-cyan-400 mt-1">1 Infant Record</div>
          <p className="text-[11px] text-slate-500 mt-1">Ward 3 catch-up round flagged</p>
        </div>
      </div>
    </div>
  );
};
