import React, { useState } from 'react';
import { User, Mail, Lock, Cloud, Zap } from 'lucide-react';

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
    <div className="min-h-full flex flex-col justify-center items-center py-6 px-3 space-y-6">
      {/* Brand Logo & Header */}
      <div className="flex flex-col items-center text-center space-y-3">
        {/* CareSync Icon */}
        <div className="w-20 h-20 rounded-[24px] bg-gradient-to-tr from-[#6366f1] via-[#818cf8] to-[#38bdf8] p-[2px] shadow-lg shadow-indigo-500/20 flex items-center justify-center">
          <div className="w-full h-full bg-[#5252ed]/90 rounded-[22px] flex items-center justify-center relative overflow-hidden">
            {/* Connected nodes icon */}
            <div className="relative w-12 h-12 flex items-center justify-center">
              {/* Center Cross / Diamond */}
              <div className="w-7 h-7 bg-white rounded-lg shadow-md flex items-center justify-center">
                <div className="w-3.5 h-3.5 bg-blue-600 rounded-sm"></div>
              </div>
              {/* Surrounding Nodes */}
              <div className="absolute top-0 left-1/2 -translate-x-1/2 w-2 h-2 rounded-full bg-emerald-400"></div>
              <div className="absolute bottom-0 left-1/2 -translate-x-1/2 w-2 h-2 rounded-full bg-emerald-400"></div>
              <div className="absolute left-0 top-1/2 -translate-y-1/2 w-2 h-2 rounded-full bg-purple-300"></div>
              <div className="absolute right-0 top-1/2 -translate-y-1/2 w-2 h-2 rounded-full bg-purple-300"></div>
            </div>
          </div>
        </div>

        {/* Title */}
        <div>
          <h1 className="text-xl font-bold text-slate-100 tracking-tight">Welcome to CareSync</h1>
          <p className="text-[11px] text-slate-400 mt-0.5">
            Offline-First Healthcare · Patient Records · Sehat Setu AI
          </p>
        </div>

        {/* Local Vault Pill */}
        <div className="px-3.5 py-1.5 rounded-full bg-[#142e22] text-[#4ade80] border border-[#22c55e]/30 text-[11px] font-semibold flex items-center gap-1.5 shadow-xs">
          <span className="text-[12px]">☁️</span>
          <span>CareSync Local Vault + Cloud Ready</span>
        </div>
      </div>

      {/* Clinician Sign-In Card */}
      <div className="w-full rounded-3xl bg-[#131926] border border-slate-800/90 p-5 space-y-4 shadow-xl">
        <div>
          <h2 className="text-base font-bold text-white tracking-tight">Clinician Sign-In</h2>
          <div className="flex items-start gap-2 mt-1.5">
            <Lock className="w-3.5 h-3.5 text-[#a855f7] mt-0.5 shrink-0" />
            <p className="text-[11px] text-slate-400 leading-snug">
              Enter your details to sign in immediately. No cloud account verification required.
            </p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-3.5">
          {/* Clinician Name */}
          <div className="p-2.5 rounded-2xl bg-[#0f1420] border border-slate-800 focus-within:border-purple-500/50 transition-colors">
            <label className="text-[10px] text-slate-400 block mb-0.5">Clinician Name</label>
            <div className="flex items-center gap-2.5">
              <div className="w-6 h-6 rounded-lg bg-purple-900/40 text-purple-400 flex items-center justify-center shrink-0">
                <User className="w-3.5 h-3.5" />
              </div>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full bg-transparent text-xs text-white font-medium focus:outline-none"
                placeholder="Dr. Priya Kulkarni"
              />
            </div>
          </div>

          {/* Email Address */}
          <div className="p-2.5 rounded-2xl bg-[#0f1420] border border-slate-800 focus-within:border-purple-500/50 transition-colors">
            <label className="text-[10px] text-slate-400 block mb-0.5">Gmail / Email Address</label>
            <div className="flex items-center gap-2.5">
              <div className="w-6 h-6 rounded-lg bg-purple-900/40 text-purple-400 flex items-center justify-center shrink-0">
                <Mail className="w-3.5 h-3.5" />
              </div>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full bg-transparent text-xs text-white font-medium focus:outline-none"
                placeholder="priya.kulkarni@gmail.com"
              />
            </div>
          </div>

          {/* Password */}
          <div className="p-2.5 rounded-2xl bg-[#0f1420] border border-slate-800 focus-within:border-purple-500/50 transition-colors">
            <label className="text-[10px] text-slate-400 block mb-0.5">Password</label>
            <div className="flex items-center gap-2.5">
              <div className="w-6 h-6 rounded-lg bg-purple-900/40 text-purple-400 flex items-center justify-center shrink-0">
                <Lock className="w-3.5 h-3.5" />
              </div>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full bg-transparent text-xs text-white font-medium focus:outline-none tracking-widest"
                placeholder="••••••••••••"
              />
            </div>
          </div>

          {/* Sign In Button */}
          <button
            type="submit"
            className="w-full py-3.5 rounded-2xl bg-gradient-to-r from-[#8b5cf6] to-[#7c3aed] hover:from-[#7c3aed] hover:to-[#6d28d9] text-white font-semibold text-xs shadow-lg shadow-purple-600/30 flex items-center justify-center gap-2 cursor-pointer transition-all active:scale-[0.99]"
          >
            <Cloud className="w-4 h-4" />
            <span>Sign In as Clinician</span>
          </button>

          {/* 1-Tap Quick Demo Access Button */}
          <button
            type="button"
            onClick={onSignInSuccess}
            className="w-full py-3 rounded-2xl bg-[#141926] border border-purple-500/30 hover:border-purple-500/60 text-purple-300 font-semibold text-xs flex items-center justify-center gap-2 cursor-pointer transition-all active:scale-[0.99]"
          >
            <Zap className="w-3.5 h-3.5 text-amber-400 fill-amber-400" />
            <Zap className="w-3.5 h-3.5 text-amber-400 fill-amber-400 -ml-1.5" />
            <span>1-Tap Quick Demo Access</span>
          </button>
        </form>
      </div>
    </div>
  );
};
