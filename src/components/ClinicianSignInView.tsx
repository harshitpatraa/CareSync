import React, { useState } from 'react';
import { User, Mail, Lock, CheckCircle2, Zap, ShieldCheck } from 'lucide-react';

interface ClinicianSignInViewProps {
  onSignInSuccess: () => void;
}

export const ClinicianSignInView: React.FC<ClinicianSignInViewProps> = ({ onSignInSuccess }) => {
  const [name, setName] = useState('Dr. Priya Kulkarni');
  const [email, setEmail] = useState('priya.kulkarni@gmail.com');
  const [password, setPassword] = useState('••••••••••••');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSignInSuccess();
  };

  return (
    <div className="min-h-full flex flex-col justify-center items-center py-6 px-3 space-y-5 select-none text-slate-800">
      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white border-y border-slate-200"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Brand Logo & Header */}
      <div className="flex flex-col items-center text-center space-y-2">
        {/* Emblem / Govt seal */}
        <div className="w-16 h-16 rounded-2xl bg-white border-2 border-slate-900 p-2 shadow-sm flex items-center justify-center text-3xl">
          🏛️
        </div>

        {/* Title */}
        <div>
          <div className="flex items-center justify-center gap-1.5">
            <h1 className="text-xl font-black text-[#0F172A] tracking-tight">CareSync Portal</h1>
            <span className="text-[9px] font-black px-1.5 py-0.2 rounded-xs bg-black text-white border border-black uppercase">
              NHM Govt
            </span>
          </div>
          <p className="text-[11px] font-bold text-slate-600 mt-0.5">
            Ministry of Health & Family Welfare · ABDM Citizen Registry
          </p>
        </div>

        {/* Local Vault Pill */}
        <div className="px-3.5 py-1 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-bold flex items-center gap-1.5 shadow-xs">
          <span>☁️</span>
          <span>ABDM Field Node · 100% Offline Vault Sync</span>
        </div>
      </div>

      {/* Clinician Sign-In Card */}
      <div className="w-full rounded-2xl bg-white border-2 border-slate-900 p-5 space-y-4 shadow-sm">
        <div>
          <h2 className="text-base font-black text-[#0F172A] tracking-tight">Clinician Authentication</h2>
          <div className="flex items-start gap-1.5 mt-1">
            <ShieldCheck className="w-3.5 h-3.5 text-[#15803D] mt-0.5 shrink-0" />
            <p className="text-[11px] font-medium text-slate-600 leading-snug">
              Sign in with your MoHFW medical officer credentials. Offline clinical session allowed.
            </p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-3">
          {/* Clinician Name */}
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
            <label className="text-[10px] text-slate-600 font-bold block mb-0.5">Clinician Name</label>
            <div className="flex items-center gap-2">
              <User className="w-3.5 h-3.5 text-slate-600 shrink-0" />
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none"
                placeholder="Dr. Priya Kulkarni"
              />
            </div>
          </div>

          {/* Email Address */}
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
            <label className="text-[10px] text-slate-600 font-bold block mb-0.5">Official Govt / Email Address</label>
            <div className="flex items-center gap-2">
              <Mail className="w-3.5 h-3.5 text-slate-600 shrink-0" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none"
                placeholder="priya.kulkarni@gmail.com"
              />
            </div>
          </div>

          {/* Password */}
          <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
            <label className="text-[10px] text-slate-600 font-bold block mb-0.5">Security PIN / Password</label>
            <div className="flex items-center gap-2">
              <Lock className="w-3.5 h-3.5 text-slate-600 shrink-0" />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none tracking-widest"
                placeholder="••••••••••••"
              />
            </div>
          </div>

          {/* Sign In Button */}
          <button
            type="submit"
            className="w-full py-3 rounded-xl bg-black hover:bg-neutral-800 text-white font-black text-xs shadow-xs flex items-center justify-center gap-2 cursor-pointer transition-all active:scale-[0.99]"
          >
            <CheckCircle2 className="w-4 h-4" />
            <span>Sign In as Medical Officer</span>
          </button>

          {/* 1-Tap Quick Demo Access Button */}
          <button
            type="button"
            onClick={onSignInSuccess}
            className="w-full py-2.5 rounded-xl bg-slate-100 border border-slate-300 hover:bg-slate-200 text-slate-800 font-bold text-xs flex items-center justify-center gap-1.5 cursor-pointer transition-all active:scale-[0.99]"
          >
            <Zap className="w-3.5 h-3.5 text-slate-900 fill-slate-900" />
            <span>1-Tap Immediate Field Demo Access</span>
          </button>
        </form>
      </div>
    </div>
  );
};
