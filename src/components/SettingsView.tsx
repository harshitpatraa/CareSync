import React from 'react';
import { 
  Settings, 
  Wifi, 
  WifiOff, 
  Moon, 
  Globe, 
  ShieldCheck, 
  Database, 
  CloudCheck,
  RotateCcw,
  Sparkles
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export const SettingsView: React.FC = () => {
  const { 
    isOfflineOnly, 
    setIsOfflineOnly, 
    isDarkMode, 
    setIsDarkMode, 
    language, 
    setLanguage,
    syncAllRecords,
    patients 
  } = useApp();

  const handleReset = () => {
    if (confirm("Reset demo data to factory defaults?")) {
      localStorage.clear();
      window.location.reload();
    }
  };

  return (
    <div className="space-y-6 max-w-3xl mx-auto">
      <div>
        <h2 className="text-xl font-bold text-white flex items-center gap-2">
          <Settings className="w-5 h-5 text-blue-400" />
          System Settings & Vault Controls
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Configure CareSync Web application behavior, local vault sync, and AI engine preferences
        </p>
      </div>

      {/* Network & Offline Vault */}
      <div className="p-5 rounded-2xl bg-[#0f172a]/80 border border-slate-800 space-y-4">
        <h3 className="font-bold text-sm text-white">Offline Vault & Cloud Engine</h3>

        <div className="flex items-center justify-between p-3 rounded-xl bg-slate-900 border border-slate-800">
          <div className="flex items-center space-x-3">
            {isOfflineOnly ? <WifiOff className="w-5 h-5 text-amber-400" /> : <Wifi className="w-5 h-5 text-emerald-400" />}
            <div>
              <div className="text-xs font-semibold text-white">Offline-Only Vault Mode</div>
              <p className="text-[11px] text-slate-400">Keep all clinical patient records in browser local vault</p>
            </div>
          </div>

          <label className="relative inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              checked={isOfflineOnly}
              onChange={(e) => setIsOfflineOnly(e.target.checked)}
              className="sr-only peer"
            />
            <div className="w-11 h-6 bg-slate-800 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
          </label>
        </div>

        <button
          onClick={() => {
            const res = syncAllRecords();
            alert(res.message);
          }}
          className="w-full py-2.5 rounded-xl bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 border border-blue-500/30 text-xs font-semibold flex items-center justify-center space-x-2 cursor-pointer transition-colors"
        >
          <CloudCheck className="w-4 h-4" />
          <span>Synchronize Local Room Queue with CareSync Cloud</span>
        </button>
      </div>

      {/* Localization & Preferences */}
      <div className="p-5 rounded-2xl bg-[#0f172a]/80 border border-slate-800 space-y-4">
        <h3 className="font-bold text-sm text-white">Localization & Theme</h3>

        <div className="flex items-center justify-between p-3 rounded-xl bg-slate-900 border border-slate-800">
          <div className="flex items-center space-x-3">
            <Globe className="w-5 h-5 text-blue-400" />
            <div>
              <div className="text-xs font-semibold text-white">App Language</div>
              <p className="text-[11px] text-slate-400">MoHFW ASHA modules available in English & Hindi</p>
            </div>
          </div>

          <select
            value={language}
            onChange={(e) => setLanguage(e.target.value as 'en' | 'hi')}
            className="text-xs bg-slate-800 border border-slate-700 text-white rounded-xl px-3 py-1.5 focus:outline-none"
          >
            <option value="en">English (Official)</option>
            <option value="hi">हिन्दी (Devanagari)</option>
          </select>
        </div>
      </div>

      {/* Reset & Factory Controls */}
      <div className="p-5 rounded-2xl bg-red-950/20 border border-red-500/30 space-y-3">
        <h3 className="font-bold text-sm text-red-400">Data Management</h3>
        <p className="text-xs text-slate-400">
          Reset local browser storage and re-seed with standard MoHFW clinical cohort demo data.
        </p>
        <button
          onClick={handleReset}
          className="px-4 py-2 rounded-xl bg-red-600/20 hover:bg-red-600/30 text-red-400 border border-red-500/30 text-xs font-semibold flex items-center gap-2 cursor-pointer transition-colors"
        >
          <RotateCcw className="w-4 h-4" />
          <span>Reset All Demo Data</span>
        </button>
      </div>
    </div>
  );
};
