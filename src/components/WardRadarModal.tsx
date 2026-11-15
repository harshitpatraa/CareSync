import React from 'react';
import { 
  X, 
  Target, 
  AlertTriangle, 
  MapPin, 
  CheckCircle2, 
  ShieldAlert, 
  Droplets, 
  Users, 
  ArrowRight,
  Download
} from 'lucide-react';

interface WardRadarModalProps {
  onClose: () => void;
  onNavigateToRisk?: () => void;
}

export const WardRadarModal: React.FC<WardRadarModalProps> = ({ onClose, onNavigateToRisk }) => {
  return (
    <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="w-full max-w-sm rounded-3xl bg-[#0f172a] border border-slate-800 p-5 space-y-4 max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center border-b border-slate-800 pb-2">
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 rounded-xl bg-indigo-600/20 text-indigo-400 border border-indigo-500/40 flex items-center justify-center">
              <Target className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-xs font-bold text-white">Ward Outbreak & Trend Radar</h3>
              <p className="text-[10px] text-slate-400">MoHFW Integrated Disease Surveillance (IDSP)</p>
            </div>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-white cursor-pointer">
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Hotspot Alert Banner (2-color theme: Indigo & Emerald, Alert in Crimson) */}
        <div className="p-3.5 rounded-2xl bg-red-950/40 border border-red-500/40 space-y-1.5">
          <div className="flex items-center gap-2 text-red-400 text-xs font-bold">
            <ShieldAlert className="w-4 h-4" />
            <span>Active Outbreak Warning: Ward 4 (Rampur East)</span>
          </div>
          <p className="text-[11px] text-slate-300 leading-snug">
            Cluster of <strong>12 Febrile / Dengue cases</strong> identified in last 72 hours. Vector control fogging and larvicide distribution advised.
          </p>
        </div>

        {/* Ward Breakdown List */}
        <div className="space-y-2">
          <h4 className="text-xs font-bold text-white">Community Sector Surveillance</h4>

          <div className="space-y-2">
            <div className="p-3 rounded-2xl bg-[#0b101c] border border-slate-800 space-y-1.5">
              <div className="flex justify-between items-center text-xs">
                <span className="font-bold text-white flex items-center gap-1">
                  <MapPin className="w-3.5 h-3.5 text-indigo-400" />
                  Ward 4 · Rampur East
                </span>
                <span className="px-2 py-0.5 rounded-full bg-red-500/20 text-red-400 text-[10px] font-bold border border-red-500/30">
                  HIGH ALERT
                </span>
              </div>
              <p className="text-[10px] text-slate-400">12 High Fever cases · 4 confirmed Dengue NS1 positive · Standing water detected</p>
            </div>

            <div className="p-3 rounded-2xl bg-[#0b101c] border border-slate-800 space-y-1.5">
              <div className="flex justify-between items-center text-xs">
                <span className="font-bold text-white flex items-center gap-1">
                  <MapPin className="w-3.5 h-3.5 text-indigo-400" />
                  Ward 7 · Old Mill Colony
                </span>
                <span className="px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-400 text-[10px] font-bold border border-amber-500/30">
                  MODERATE
                </span>
              </div>
              <p className="text-[10px] text-slate-400">5 Respiratory wheezing reports · NCD elderly monitoring active</p>
            </div>

            <div className="p-3 rounded-2xl bg-[#0b101c] border border-slate-800 space-y-1.5">
              <div className="flex justify-between items-center text-xs">
                <span className="font-bold text-white flex items-center gap-1">
                  <MapPin className="w-3.5 h-3.5 text-indigo-400" />
                  Sector 2 · Subhash Nagar
                </span>
                <span className="px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 text-[10px] font-bold border border-emerald-500/30">
                  NORMAL
                </span>
              </div>
              <p className="text-[10px] text-slate-400">100% UIP vaccine coverage · 0 epidemic vector signals</p>
            </div>
          </div>
        </div>

        {/* Public Health Actions */}
        <div className="p-3 rounded-2xl bg-indigo-950/30 border border-indigo-500/30 space-y-1.5">
          <h5 className="text-[11px] font-bold text-indigo-300">ASHA Field Directives:</h5>
          <ul className="text-[10px] text-slate-300 space-y-1 list-disc pl-4">
            <li>Check water coolers and barrels for mosquito larvae in Ward 4.</li>
            <li>Distribute ORS and Paracetamol to households reporting fever &gt; 38°C.</li>
            <li>Refer any patient with platelet count drop or petechiae to PHC Rampur.</li>
          </ul>
        </div>

        <button
          onClick={() => {
            onClose();
            if (onNavigateToRisk) onNavigateToRisk();
          }}
          className="w-full py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-xs flex items-center justify-center gap-1.5 cursor-pointer shadow-md"
        >
          <span>Open Dengue Diagnostic Triage</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </button>
      </div>
    </div>
  );
};
