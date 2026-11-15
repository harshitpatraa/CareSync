import React, { useState } from 'react';
import { 
  User, 
  Mail, 
  Lock, 
  CheckCircle2, 
  ShieldCheck, 
  Phone, 
  IdCard, 
  Building2, 
  Sparkles,
  ArrowRight,
  UserCheck
} from 'lucide-react';

export type UserRole = 'individual' | 'asha_worker';

interface ClinicianSignInViewProps {
  onSignInSuccess: (role: UserRole, userProfile?: any) => void;
}

export const ClinicianSignInView: React.FC<ClinicianSignInViewProps> = ({ onSignInSuccess }) => {
  const [activeTab, setActiveTab] = useState<'login' | 'register'>('login');
  const [role, setRole] = useState<UserRole>('individual');

  // Citizen Login Form
  const [citizenAbha, setCitizenAbha] = useState('91-8842-1092-4412');
  const [citizenPin, setCitizenPin] = useState('4412');

  // ASHA Worker Login Form
  const [ashaEmail, setAshaEmail] = useState('priya.kulkarni@nhm.gov.in');
  const [ashaPin, setAshaPin] = useState('••••••••••••');

  // Registration Form States
  const [regName, setRegName] = useState('');
  const [regPhone, setRegPhone] = useState('');
  const [regId, setRegId] = useState('');
  const [regWard, setRegWard] = useState('Ward 4 · Rampur');
  const [regPin, setRegPin] = useState('');
  const [regSuccess, setRegSuccess] = useState(false);

  const handleLoginSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (role === 'individual') {
      onSignInSuccess('individual', {
        name: 'Ramesh Sharma',
        abhaId: citizenAbha || '91-8842-1092-4412',
        role: 'individual',
        phone: '+91 98231 44102',
        ward: 'Ward 4 · Shanti Nagar, Rampur'
      });
    } else {
      onSignInSuccess('asha_worker', {
        name: 'Dr. Priya Kulkarni',
        email: ashaEmail || 'priya.kulkarni@nhm.gov.in',
        role: 'asha_worker',
        designation: 'Medical Officer / ASHA Supervisor',
        councilReg: 'MH-2024-8812',
        phc: 'Rampur Primary Health Centre'
      });
    }
  };

  const handleRegisterSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setRegSuccess(true);
    setTimeout(() => {
      onSignInSuccess(role, {
        name: regName || (role === 'individual' ? 'New Citizen' : 'Registered ASHA Worker'),
        abhaId: regId || '91-7711-2233-4455',
        role: role,
        phone: regPhone || '+91 98765 00000',
        ward: regWard
      });
    }, 900);
  };

  const quickLoginAsCitizen = () => {
    setRole('individual');
    onSignInSuccess('individual', {
      name: 'Ramesh Sharma',
      abhaId: '91-8842-1092-4412',
      role: 'individual',
      phone: '+91 98231 44102',
      ward: 'Ward 4 · Shanti Nagar, Rampur'
    });
  };

  const quickLoginAsAsha = () => {
    setRole('asha_worker');
    onSignInSuccess('asha_worker', {
      name: 'Dr. Priya Kulkarni',
      email: 'priya.kulkarni@nhm.gov.in',
      role: 'asha_worker',
      designation: 'Medical Officer / ASHA Supervisor',
      councilReg: 'MH-2024-8812',
      phc: 'Rampur Primary Health Centre'
    });
  };

  return (
    <div className="min-h-full flex flex-col justify-center items-center py-4 px-2 space-y-4 select-none text-slate-800">
      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white border-y border-slate-200"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Brand Logo & Header */}
      <div className="flex flex-col items-center text-center space-y-1.5">
        <div className="w-14 h-14 rounded-2xl bg-white border-2 border-slate-900 p-2 shadow-xs flex items-center justify-center text-2xl">
          🏛️
        </div>

        <div>
          <div className="flex items-center justify-center gap-1.5">
            <h1 className="text-xl font-black text-[#0F172A] tracking-tight">CareSync Portal</h1>
            <span className="text-[9px] font-black px-1.5 py-0.2 rounded-xs bg-black text-white border border-black uppercase">
              ABDM Govt
            </span>
          </div>
          <p className="text-[10px] font-bold text-slate-600 mt-0.5">
            Ministry of Health & Family Welfare · National Digital Health Mission
          </p>
        </div>
      </div>

      {/* Role Selector Segmented Controls */}
      <div className="w-full bg-slate-200/80 p-1 rounded-xl border border-slate-300 grid grid-cols-2 gap-1 shadow-xs">
        <button
          type="button"
          onClick={() => setRole('individual')}
          className={`py-2 px-2 rounded-lg text-xs font-black flex items-center justify-center gap-1.5 transition-all cursor-pointer ${
            role === 'individual'
              ? 'bg-black text-white shadow-xs'
              : 'text-slate-700 hover:text-black'
          }`}
        >
          <span>👤 Individual / Citizen</span>
        </button>
        <button
          type="button"
          onClick={() => setRole('asha_worker')}
          className={`py-2 px-2 rounded-lg text-xs font-black flex items-center justify-center gap-1.5 transition-all cursor-pointer ${
            role === 'asha_worker'
              ? 'bg-black text-white shadow-xs'
              : 'text-slate-700 hover:text-black'
          }`}
        >
          <span>👩‍⚕️ ASHA / Health Worker</span>
        </button>
      </div>

      {/* Role Permission Scope Info Banner */}
      <div className={`w-full p-2.5 rounded-xl border text-[10px] font-bold leading-relaxed shadow-xs ${
        role === 'individual'
          ? 'bg-amber-50/80 text-amber-950 border-amber-300'
          : 'bg-emerald-50/80 text-emerald-950 border-emerald-300'
      }`}>
        {role === 'individual' ? (
          <div>
            <div className="flex items-center gap-1 font-black text-[11px] text-amber-900 mb-0.5">
              <span>👤</span>
              <span>Citizen Access Mode (व्यक्तिगत स्वास्थ्य खाता)</span>
            </div>
            <p className="font-medium text-amber-900">
              Access your personal ABHA card, prescription OCR scanner, 108 SOS emergency dispatch, and Sehat Setu AI health guidance. Community census records are strictly private.
            </p>
          </div>
        ) : (
          <div>
            <div className="flex items-center gap-1 font-black text-[11px] text-emerald-900 mb-0.5">
              <span>👩‍⚕️</span>
              <span>ASHA / Clinical Supervisor Mode (स्वास्थ्य कार्यकर्ता)</span>
            </div>
            <p className="font-medium text-emerald-900">
              Access ward beneficiary registers, 12-question NCD screening, clinical vitals triage, and authorized prescription signing controls.
            </p>
          </div>
        )}
      </div>

      {/* Auth Main Card */}
      <div className="w-full rounded-2xl bg-white border-2 border-slate-900 p-4 space-y-3.5 shadow-sm">
        {/* Login / Register Tab Switcher */}
        <div className="flex items-center border-b border-slate-200 pb-2">
          <div className="flex gap-4">
            <button
              onClick={() => setActiveTab('login')}
              className={`text-xs font-black pb-1 cursor-pointer transition-colors relative ${
                activeTab === 'login'
                  ? 'text-black border-b-2 border-black -mb-[9px]'
                  : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              🔑 Login to Account
            </button>
            <button
              onClick={() => setActiveTab('register')}
              className={`text-xs font-black pb-1 cursor-pointer transition-colors relative ${
                activeTab === 'register'
                  ? 'text-black border-b-2 border-black -mb-[9px]'
                  : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              📝 Register New {role === 'individual' ? 'Citizen' : 'Worker'}
            </button>
          </div>
        </div>

        {activeTab === 'login' ? (
          /* LOGIN FORM */
          <form onSubmit={handleLoginSubmit} className="space-y-3">
            {role === 'individual' ? (
              <>
                <div className="p-2 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
                  <label className="text-[10px] text-slate-600 font-bold block mb-0.5">
                    ABHA ID / Mobile Number
                  </label>
                  <div className="flex items-center gap-2">
                    <IdCard className="w-3.5 h-3.5 text-slate-600 shrink-0" />
                    <input
                      type="text"
                      value={citizenAbha}
                      onChange={(e) => setCitizenAbha(e.target.value)}
                      className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none"
                      placeholder="91-8842-1092-4412"
                      required
                    />
                  </div>
                </div>

                <div className="p-2 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
                  <label className="text-[10px] text-slate-600 font-bold block mb-0.5">
                    ABHA Security PIN / OTP
                  </label>
                  <div className="flex items-center gap-2">
                    <Lock className="w-3.5 h-3.5 text-slate-600 shrink-0" />
                    <input
                      type="password"
                      value={citizenPin}
                      onChange={(e) => setCitizenPin(e.target.value)}
                      className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none tracking-widest"
                      placeholder="••••"
                      required
                    />
                  </div>
                </div>
              </>
            ) : (
              <>
                <div className="p-2 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
                  <label className="text-[10px] text-slate-600 font-bold block mb-0.5">
                    Official MoHFW Email / ASHA Reg No.
                  </label>
                  <div className="flex items-center gap-2">
                    <Mail className="w-3.5 h-3.5 text-slate-600 shrink-0" />
                    <input
                      type="text"
                      value={ashaEmail}
                      onChange={(e) => setAshaEmail(e.target.value)}
                      className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none"
                      placeholder="priya.kulkarni@nhm.gov.in"
                      required
                    />
                  </div>
                </div>

                <div className="p-2 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
                  <label className="text-[10px] text-slate-600 font-bold block mb-0.5">
                    Clinical Security Password
                  </label>
                  <div className="flex items-center gap-2">
                    <Lock className="w-3.5 h-3.5 text-slate-600 shrink-0" />
                    <input
                      type="password"
                      value={ashaPin}
                      onChange={(e) => setAshaPin(e.target.value)}
                      className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none tracking-widest"
                      placeholder="••••••••••••"
                      required
                    />
                  </div>
                </div>
              </>
            )}

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-black hover:bg-neutral-800 text-white font-black text-xs cursor-pointer shadow-xs transition-colors flex items-center justify-center gap-1.5"
            >
              <span>Sign In as {role === 'individual' ? 'Citizen' : 'Healthcare Worker'}</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </form>
        ) : (
          /* REGISTRATION FORM */
          <form onSubmit={handleRegisterSubmit} className="space-y-2.5">
            <div className="p-2 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
              <label className="text-[10px] text-slate-600 font-bold block mb-0.5">
                Full Name ({role === 'individual' ? 'Citizen' : 'Health Worker'})
              </label>
              <div className="flex items-center gap-2">
                <User className="w-3.5 h-3.5 text-slate-600 shrink-0" />
                <input
                  type="text"
                  value={regName}
                  onChange={(e) => setRegName(e.target.value)}
                  className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none"
                  placeholder={role === 'individual' ? 'Ramesh Sharma' : 'Dr. Priya Kulkarni'}
                  required
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2">
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
                <label className="text-[10px] text-slate-600 font-bold block mb-0.5">Mobile Phone</label>
                <div className="flex items-center gap-1.5">
                  <Phone className="w-3 h-3 text-slate-600 shrink-0" />
                  <input
                    type="tel"
                    value={regPhone}
                    onChange={(e) => setRegPhone(e.target.value)}
                    className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none"
                    placeholder="+91 98231..."
                    required
                  />
                </div>
              </div>

              <div className="p-2 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
                <label className="text-[10px] text-slate-600 font-bold block mb-0.5">
                  {role === 'individual' ? 'Aadhaar / ABHA' : 'Council Reg / ID'}
                </label>
                <div className="flex items-center gap-1.5">
                  <IdCard className="w-3 h-3 text-slate-600 shrink-0" />
                  <input
                    type="text"
                    value={regId}
                    onChange={(e) => setRegId(e.target.value)}
                    className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none"
                    placeholder={role === 'individual' ? '91-8842...' : 'MH-2024-881'}
                    required
                  />
                </div>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2">
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
                <label className="text-[10px] text-slate-600 font-bold block mb-0.5">
                  {role === 'individual' ? 'Ward / Area' : 'Primary Centre'}
                </label>
                <div className="flex items-center gap-1.5">
                  <Building2 className="w-3 h-3 text-slate-600 shrink-0" />
                  <input
                    type="text"
                    value={regWard}
                    onChange={(e) => setRegWard(e.target.value)}
                    className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none"
                    placeholder="Ward 4, Rampur"
                    required
                  />
                </div>
              </div>

              <div className="p-2 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black transition-colors">
                <label className="text-[10px] text-slate-600 font-bold block mb-0.5">Create PIN</label>
                <div className="flex items-center gap-1.5">
                  <Lock className="w-3 h-3 text-slate-600 shrink-0" />
                  <input
                    type="password"
                    value={regPin}
                    onChange={(e) => setRegPin(e.target.value)}
                    className="w-full bg-transparent text-xs text-[#0F172A] font-bold focus:outline-none tracking-widest"
                    placeholder="••••"
                    required
                  />
                </div>
              </div>
            </div>

            {regSuccess && (
              <div className="p-2 rounded-lg bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-bold flex items-center gap-1.5">
                <CheckCircle2 className="w-3.5 h-3.5 text-[#15803D]" />
                <span>Account registered on ABDM Registry! Logging in...</span>
              </div>
            )}

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-black hover:bg-neutral-800 text-white font-black text-xs cursor-pointer shadow-xs transition-colors flex items-center justify-center gap-1.5"
            >
              <UserCheck className="w-3.5 h-3.5" />
              <span>Complete ABDM Registration</span>
            </button>
          </form>
        )}

        {/* 1-Tap Quick Access Demo Buttons */}
        <div className="pt-2 border-t border-slate-200 space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-[10px] font-bold text-slate-600">⚡ Instant 1-Tap Demo Access:</span>
            <span className="text-[9px] font-black px-1.5 py-0.5 rounded bg-slate-100 text-slate-700">Official Preview</span>
          </div>

          <div className="grid grid-cols-2 gap-2">
            <button
              type="button"
              onClick={quickLoginAsCitizen}
              className="p-2 rounded-xl bg-amber-50 hover:bg-amber-100 border border-amber-300 text-left cursor-pointer transition-colors shadow-xs"
            >
              <div className="flex items-center gap-1 text-[11px] font-black text-amber-950">
                <span>👤</span>
                <span>Citizen Demo</span>
              </div>
              <p className="text-[9px] font-medium text-amber-900 mt-0.5">
                Ramesh Sharma · ABHA Vault, OCR & SOS
              </p>
            </button>

            <button
              type="button"
              onClick={quickLoginAsAsha}
              className="p-2 rounded-xl bg-emerald-50 hover:bg-emerald-100 border border-emerald-300 text-left cursor-pointer transition-colors shadow-xs"
            >
              <div className="flex items-center gap-1 text-[11px] font-black text-emerald-950">
                <span>👩‍⚕️</span>
                <span>ASHA Demo</span>
              </div>
              <p className="text-[9px] font-medium text-emerald-900 mt-0.5">
                Dr. Priya Kulkarni · Full Clinical Suite
              </p>
            </button>
          </div>
        </div>
      </div>

      {/* ABDM Security Footer */}
      <div className="flex items-center justify-center gap-1.5 text-[10px] text-slate-600 font-bold">
        <ShieldCheck className="w-3.5 h-3.5 text-[#15803D]" />
        <span>256-bit ABDM Offline Vault · Ayushman Bharat Digital Mission</span>
      </div>
    </div>
  );
};
