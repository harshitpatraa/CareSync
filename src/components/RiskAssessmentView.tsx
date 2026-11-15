import React, { useState } from 'react';
import { 
  ArrowLeft,
  Bell,
  Stethoscope, 
  Activity, 
  FileText, 
  CheckCircle2, 
  AlertTriangle, 
  Sparkles, 
  ShieldAlert, 
  RotateCcw
} from 'lucide-react';
import { evaluateQuestionnaireRisk, evaluateVitalsRisk } from '../services/medicalAiService';
import { ClinicalAssessmentResult } from '../types';

interface RiskAssessmentViewProps {
  onBack?: () => void;
}

export const RiskAssessmentView: React.FC<RiskAssessmentViewProps> = ({ onBack }) => {
  const [activeTab, setActiveTab] = useState<'screening' | 'vitals'>('screening');

  // 12-Question Screening State
  const initialAnswers: Record<string, string> = {
    fever: "No",
    chest_pain: "No",
    blood_pressure: "No",
    sugar_thirst: "No",
    cough_wheeze: "No",
    headache_vision: "No",
    joint_fatigue: "No",
    abdominal_digestive: "No",
    skin_wound: "No",
    mosquito_exposure: "No",
    lifestyle_stress: "No",
    family_history: "No"
  };
  const [answers, setAnswers] = useState<Record<string, string>>(initialAnswers);
  const [screeningResult, setScreeningResult] = useState<ClinicalAssessmentResult | null>(null);

  // Vitals State
  const [sys, setSys] = useState(138);
  const [dia, setDia] = useState(88);
  const [hr, setHr] = useState(84);
  const [spo2, setSpo2] = useState(97);
  const [temp, setTemp] = useState(37.2);
  const [sugar, setSugar] = useState(140);
  const [isPregnant, setIsPregnant] = useState(false);
  const [vitalsResult, setVitalsResult] = useState<ClinicalAssessmentResult | null>(null);

  const screeningQuestions = [
    { 
      key: "fever", 
      title: "1. High Fever / Shivering Chills", 
      tag: "Vector / Infection",
      desc: "Body temperature > 38°C (100.4°F), sudden onset chills, or severe body ache in last 48 hours?" 
    },
    { 
      key: "chest_pain", 
      title: "2. Chest Pressure / Radiating Heaviness", 
      tag: "Cardiovascular",
      desc: "Squeezing pain, tightness across chest, or heaviness spreading to left arm/jaw on exertion?" 
    },
    { 
      key: "blood_pressure", 
      title: "3. Elevated Blood Pressure / Dizziness", 
      tag: "Hypertension",
      desc: "Known hypertension history or recent systolic reading above 140 mmHg with lightheadedness?" 
    },
    { 
      key: "sugar_thirst", 
      title: "4. Excessive Thirst / Polyuria / High Sugar", 
      tag: "Metabolic / Diabetes",
      desc: "Abnormally frequent urination (especially night), intense thirst, or known blood glucose > 180 mg/dL?" 
    },
    { 
      key: "cough_wheeze", 
      title: "5. Persistent Cough / Wheezing / Dyspnea", 
      tag: "Respiratory",
      desc: "Continuous productive cough lasting >1 week, noisy wheezing, or SpO2 < 95% at rest?" 
    },
    { 
      key: "headache_vision", 
      title: "6. Severe Cephalalgia / Blurred Vision", 
      tag: "Neurological",
      desc: "Pulsating headache, photophobia, eye pain, or intermittent blurred vision?" 
    },
    { 
      key: "joint_fatigue", 
      title: "7. Incapacitating Joint Pain & Extreme Fatigue", 
      tag: "Musculoskeletal",
      desc: "Symmetrical swelling in wrists, knees, or ankles making standing or morning walking painful?" 
    },
    { 
      key: "abdominal_digestive", 
      title: "8. Acid Reflux / Gastric Pain / Nausea", 
      tag: "Gastrointestinal",
      desc: "Burning epigastric pain after eating, frequent nausea, vomiting, or altered bowel habits?" 
    },
    { 
      key: "skin_wound", 
      title: "9. Chronic Non-Healing Wound / Rash", 
      tag: "Dermatology",
      desc: "Ulcerated skin sore taking weeks to heal, peeling rash, or intense localized pruritus?" 
    },
    { 
      key: "mosquito_exposure", 
      title: "10. Mosquito-Dense Environment", 
      tag: "Environmental",
      desc: "Recent presence in neighborhood with stagnant water, known dengue clusters, or mosquito bites?" 
    },
    { 
      key: "lifestyle_stress", 
      title: "11. High Sedentary Stress & Tobacco", 
      tag: "Lifestyle",
      desc: "Lack of daily physical activity (<20 min walk), high stress, or chronic tobacco/beedi usage?" 
    },
    { 
      key: "family_history", 
      title: "12. Hereditary Family History", 
      tag: "Genetics",
      desc: "First-degree relative (parent/sibling) diagnosed with heart attack, stroke, or diabetes < 55y?" 
    }
  ];

  const handleApplyPreset = (presetName: string) => {
    const fresh = { ...initialAnswers };
    if (presetName === 'dengue') {
      fresh.fever = "Yes";
      fresh.mosquito_exposure = "Yes";
      fresh.joint_fatigue = "Yes";
      fresh.headache_vision = "Yes";
    } else if (presetName === 'cardiac') {
      fresh.chest_pain = "Yes";
      fresh.blood_pressure = "Yes";
      fresh.lifestyle_stress = "Yes";
      fresh.family_history = "Yes";
    } else if (presetName === 'anc') {
      fresh.blood_pressure = "Yes";
      fresh.headache_vision = "Yes";
      fresh.fever = "Yes";
    } else if (presetName === 'diabetes') {
      fresh.sugar_thirst = "Yes";
      fresh.skin_wound = "Yes";
      fresh.blood_pressure = "Yes";
      fresh.family_history = "Yes";
    }
    setAnswers(fresh);
    setScreeningResult(evaluateQuestionnaireRisk(fresh));
  };

  const handleRunScreening = () => {
    setScreeningResult(evaluateQuestionnaireRisk(answers));
  };

  const handleRunVitals = () => {
    setVitalsResult(evaluateVitalsRisk(sys, dia, hr, spo2, temp, sugar, isPregnant));
  };

  return (
    <div className="space-y-4">
      {/* Top Header Bar */}
      <div className="flex items-center justify-between py-1 border-b border-slate-800/60 pb-2">
        <button onClick={onBack} className="p-1.5 text-slate-300 hover:text-white cursor-pointer">
          <ArrowLeft className="w-5 h-5" />
        </button>

        <h2 className="font-bold text-base text-white tracking-tight">Medical Assessment</h2>

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

      {/* Disease Risk Assessment Header */}
      <div className="p-4 rounded-3xl bg-[#131926] border border-slate-800 space-y-3">
        <div className="flex items-start justify-between">
          <div>
            <h3 className="font-bold text-sm text-white tracking-tight">Disease Risk Assessment</h3>
            <p className="text-[11px] text-slate-400 mt-0.5">
              Multi-disease probability scoring powered by Sehat Setu AI
            </p>
          </div>

          <div className="px-2.5 py-1 rounded-full bg-[#163a29] text-[#4ade80] border border-[#22c55e]/30 text-[10px] font-semibold whitespace-nowrap">
            Sehat Setu Offline Engine
          </div>
        </div>

        {/* Tab pills */}
        <div className="flex gap-2 pt-1">
          <button
            onClick={() => setActiveTab('screening')}
            className={`flex-1 py-2 rounded-xl text-xs font-semibold cursor-pointer transition-colors ${
              activeTab === 'screening'
                ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                : 'bg-[#101421] text-slate-400 border border-slate-800'
            }`}
          >
            12-Question Screening
          </button>
          <button
            onClick={() => setActiveTab('vitals')}
            className={`flex-1 py-2 rounded-xl text-xs font-semibold cursor-pointer transition-colors ${
              activeTab === 'vitals'
                ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                : 'bg-[#101421] text-slate-400 border border-slate-800'
            }`}
          >
            Numeric Vitals Triage
          </button>
        </div>

        {/* 1-Tap Diagnostic Presets */}
        {activeTab === 'screening' && (
          <div className="space-y-1.5 pt-1">
            <span className="text-[11px] font-semibold text-slate-400 block">1-Tap Diagnostic Presets:</span>
            <div className="flex gap-2 overflow-x-auto pb-1 select-none">
              <button
                onClick={() => {
                  setAnswers(initialAnswers);
                  setScreeningResult(null);
                }}
                className="px-3 py-1.5 rounded-xl bg-[#101421] border border-slate-800 text-slate-400 hover:text-white text-xs font-semibold whitespace-nowrap cursor-pointer"
              >
                Reset (All No)
              </button>
              <button
                onClick={() => handleApplyPreset('dengue')}
                className="px-3 py-1.5 rounded-xl bg-[#101421] border border-slate-800 text-amber-400 hover:border-amber-500/40 text-xs font-semibold whitespace-nowrap cursor-pointer"
              >
                Preset: Dengue / Febrile
              </button>
              <button
                onClick={() => handleApplyPreset('anc')}
                className="px-3 py-1.5 rounded-xl bg-[#101421] border border-slate-800 text-purple-400 hover:border-purple-500/40 text-xs font-semibold whitespace-nowrap cursor-pointer"
              >
                Preset: ANC Danger
              </button>
              <button
                onClick={() => handleApplyPreset('cardiac')}
                className="px-3 py-1.5 rounded-xl bg-[#101421] border border-slate-800 text-red-400 hover:border-red-500/40 text-xs font-semibold whitespace-nowrap cursor-pointer"
              >
                Preset: Cardiac
              </button>
            </div>
          </div>
        )}
      </div>

      {/* ────────────────────────────────────────────────────────── */}
      {/* 12-Question Screening Questions */}
      {/* ────────────────────────────────────────────────────────── */}
      {activeTab === 'screening' && (
        <div className="space-y-3">
          {screeningResult && (
            <div className="p-4 rounded-3xl bg-[#1b152b] border border-purple-500/40 space-y-3 animate-in fade-in-50">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="text-sm font-bold text-white">AI Diagnostic Assessment</h4>
                  <p className="text-[11px] text-purple-300">Community Risk Score: {screeningResult.riskScore}/100</p>
                </div>
                <div className={`px-3 py-1 rounded-full text-xs font-bold ${
                  screeningResult.riskLevel === 'CRITICAL' ? 'bg-red-500/20 text-red-400 border border-red-500/40' :
                  screeningResult.riskLevel === 'HIGH' ? 'bg-amber-500/20 text-amber-400 border border-amber-500/40' :
                  'bg-emerald-500/20 text-emerald-400 border border-emerald-500/40'
                }`}>
                  {screeningResult.riskLevel} PRIORITY
                </div>
              </div>

              <div className="space-y-1.5">
                {screeningResult.diseaseRisksWithPercentage.map((d, idx) => (
                  <div key={idx} className="flex items-center justify-between text-xs p-2 rounded-xl bg-[#121021]">
                    <span className="text-white">{d.diseaseName}</span>
                    <span className="font-bold text-purple-400">{d.riskPercentage}%</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {screeningQuestions.map((q) => {
            const currentVal = answers[q.key] || "No";
            return (
              <div key={q.key} className="p-3.5 rounded-2xl bg-[#131926] border border-slate-800 space-y-2.5">
                <div className="flex items-start justify-between gap-2">
                  <h4 className="font-bold text-xs text-white leading-tight">{q.title}</h4>
                  <span className="px-2 py-0.5 rounded-md bg-[#251d3b] text-[#c084fc] border border-purple-500/30 text-[10px] font-medium whitespace-nowrap">
                    {q.tag}
                  </span>
                </div>

                <p className="text-[11px] text-slate-400 leading-snug">{q.desc}</p>

                {/* 3 Buttons */}
                <div className="flex gap-2">
                  {["No", "Yes", "Unsure"].map((opt) => {
                    const isSelected = currentVal === opt;
                    return (
                      <button
                        key={opt}
                        type="button"
                        onClick={() => {
                          const updated = { ...answers, [q.key]: opt };
                          setAnswers(updated);
                          setScreeningResult(evaluateQuestionnaireRisk(updated));
                        }}
                        className={`flex-1 py-1.5 rounded-xl text-xs font-semibold cursor-pointer transition-colors ${
                          isSelected
                            ? opt === 'No'
                              ? 'bg-[#183a2b] text-[#4ade80] border border-[#22c55e]/40'
                              : opt === 'Yes'
                              ? 'bg-[#3b151e] text-[#f43f5e] border border-red-500/40'
                              : 'bg-[#2b1f14] text-[#fb923c] border border-amber-500/40'
                            : 'bg-[#101421] text-slate-400 border border-slate-800 hover:text-white'
                        }`}
                      >
                        {opt}
                      </button>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* ────────────────────────────────────────────────────────── */}
      {/* Numeric Vitals Triage Tab */}
      {/* ────────────────────────────────────────────────────────── */}
      {activeTab === 'vitals' && (
        <div className="p-4 rounded-3xl bg-[#131926] border border-slate-800 space-y-4">
          <h3 className="text-xs font-bold text-white">Numeric Vitals Screening</h3>

          <div className="grid grid-cols-2 gap-3">
            <div className="p-2.5 rounded-2xl bg-[#101421] border border-slate-800">
              <label className="text-[10px] text-slate-400 block mb-0.5">Systolic BP</label>
              <input
                type="number"
                value={sys}
                onChange={(e) => setSys(Number(e.target.value))}
                className="w-full bg-transparent text-xs text-white font-semibold focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-2xl bg-[#101421] border border-slate-800">
              <label className="text-[10px] text-slate-400 block mb-0.5">Diastolic BP</label>
              <input
                type="number"
                value={dia}
                onChange={(e) => setDia(Number(e.target.value))}
                className="w-full bg-transparent text-xs text-white font-semibold focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-2xl bg-[#101421] border border-slate-800">
              <label className="text-[10px] text-slate-400 block mb-0.5">SpO2 %</label>
              <input
                type="number"
                value={spo2}
                onChange={(e) => setSpo2(Number(e.target.value))}
                className="w-full bg-transparent text-xs text-white font-semibold focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-2xl bg-[#101421] border border-slate-800">
              <label className="text-[10px] text-slate-400 block mb-0.5">Heart Rate (bpm)</label>
              <input
                type="number"
                value={hr}
                onChange={(e) => setHr(Number(e.target.value))}
                className="w-full bg-transparent text-xs text-white font-semibold focus:outline-none"
              />
            </div>
          </div>

          <button
            onClick={handleRunVitals}
            className="w-full py-3 rounded-2xl bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-bold text-xs shadow-lg shadow-purple-600/30 cursor-pointer"
          >
            Calculate Vitals Triage
          </button>

          {vitalsResult && (
            <div className="p-3.5 rounded-2xl bg-[#161a2b] border border-purple-500/40 space-y-2">
              <div className="flex justify-between items-center">
                <span className="text-xs font-bold text-white">{vitalsResult.possibleConditions[0] || 'Vitals Normal'}</span>
                <span className="text-xs font-bold text-purple-400">{vitalsResult.riskScore}/100</span>
              </div>
              <p className="text-[11px] text-slate-300">{vitalsResult.primarySummary}</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
