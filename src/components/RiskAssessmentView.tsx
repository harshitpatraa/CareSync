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
    <div className="space-y-3.5 pb-4 select-none text-slate-800">
      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white border-y border-slate-200"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Top Header Bar - Clean Light Mode */}
      <div className="flex items-center justify-between py-1 border-b border-slate-200 pb-2">
        <button onClick={onBack} className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs">
          <ArrowLeft className="w-5 h-5" />
        </button>

        <h2 className="font-black text-sm text-[#0F172A] tracking-tight">Medical Assessment</h2>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-black flex items-center gap-1 shadow-xs">
            <span className="text-[9px]">☁️</span>
            <span>Sync</span>
          </div>
          <button className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs">
            <Bell className="w-4 h-4 text-slate-700" />
          </button>
        </div>
      </div>

      {/* Disease Risk Assessment Header */}
      <div className="p-4 rounded-2xl bg-white border-2 border-slate-900 space-y-3 shadow-sm">
        <div className="flex items-start justify-between">
          <div>
            <h3 className="font-black text-sm text-[#0F172A] tracking-tight">Disease Risk Assessment</h3>
            <p className="text-[11px] font-medium text-slate-600 mt-0.5">
              Multi-disease probability scoring powered by Sehat Setu AI
            </p>
          </div>

          <div className="px-2.5 py-1 rounded-full bg-slate-100 text-slate-900 border border-slate-300 text-[10px] font-black whitespace-nowrap shadow-xs">
            Sehat Setu Engine
          </div>
        </div>

        {/* Tab pills */}
        <div className="flex gap-2 pt-1">
          <button
            onClick={() => setActiveTab('screening')}
            className={`flex-1 py-2 rounded-xl text-xs font-black cursor-pointer transition-colors shadow-xs ${
              activeTab === 'screening'
                ? 'bg-black text-white'
                : 'bg-slate-100 text-slate-700 border border-slate-300 hover:bg-slate-200'
            }`}
          >
            12-Question Screening
          </button>
          <button
            onClick={() => setActiveTab('vitals')}
            className={`flex-1 py-2 rounded-xl text-xs font-black cursor-pointer transition-colors shadow-xs ${
              activeTab === 'vitals'
                ? 'bg-black text-white'
                : 'bg-slate-100 text-slate-700 border border-slate-300 hover:bg-slate-200'
            }`}
          >
            Numeric Vitals Triage
          </button>
        </div>

        {/* 1-Tap Diagnostic Presets */}
        {activeTab === 'screening' && (
          <div className="space-y-1.5 pt-1">
            <span className="text-[11px] font-bold text-slate-700 block">1-Tap Diagnostic Presets:</span>
            <div className="flex gap-2 overflow-x-auto pb-1 select-none scrollbar-none">
              <button
                onClick={() => {
                  setAnswers(initialAnswers);
                  setScreeningResult(null);
                }}
                className="px-3 py-1.5 rounded-xl bg-slate-100 border border-slate-300 text-slate-700 hover:bg-slate-200 text-xs font-bold whitespace-nowrap cursor-pointer shadow-xs"
              >
                Reset (All No)
              </button>
              <button
                onClick={() => handleApplyPreset('dengue')}
                className="px-3 py-1.5 rounded-xl bg-amber-50 border border-amber-300 text-amber-900 hover:bg-amber-100 text-xs font-bold whitespace-nowrap cursor-pointer shadow-xs"
              >
                Preset: Dengue / Febrile
              </button>
              <button
                onClick={() => handleApplyPreset('anc')}
                className="px-3 py-1.5 rounded-xl bg-emerald-50 border border-emerald-300 text-emerald-900 hover:bg-emerald-100 text-xs font-bold whitespace-nowrap cursor-pointer shadow-xs"
              >
                Preset: ANC Danger
              </button>
              <button
                onClick={() => handleApplyPreset('cardiac')}
                className="px-3 py-1.5 rounded-xl bg-red-50 border border-red-300 text-red-900 hover:bg-red-100 text-xs font-bold whitespace-nowrap cursor-pointer shadow-xs"
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
            <div className="p-4 rounded-2xl bg-slate-50 border border-slate-300 space-y-3 shadow-sm animate-in fade-in-50">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="text-sm font-black text-[#0F172A]">AI Diagnostic Assessment</h4>
                  <p className="text-[11px] font-bold text-slate-600">Community Risk Score: {screeningResult.riskScore}/100</p>
                </div>
                <div className={`px-3 py-1 rounded-full text-xs font-bold ${
                  screeningResult.riskLevel === 'CRITICAL' ? 'bg-red-100 text-red-800 border border-red-300' :
                  screeningResult.riskLevel === 'HIGH' ? 'bg-amber-100 text-amber-900 border border-amber-300' :
                  'bg-emerald-100 text-emerald-900 border border-emerald-300'
                }`}>
                  {screeningResult.riskLevel} PRIORITY
                </div>
              </div>

              <div className="space-y-1.5">
                {screeningResult.diseaseRisksWithPercentage.map((d, idx) => (
                  <div key={idx} className="flex items-center justify-between text-xs p-2.5 rounded-xl bg-white border border-slate-200 shadow-xs">
                    <span className="font-bold text-[#0F172A]">{d.diseaseName}</span>
                    <span className="font-mono font-black text-slate-900">{d.riskPercentage}%</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {screeningQuestions.map((q) => {
            const currentVal = answers[q.key] || "No";
            return (
              <div key={q.key} className="p-3.5 rounded-2xl bg-white border border-slate-200 space-y-2.5 shadow-xs">
                <div className="flex items-start justify-between gap-2">
                  <h4 className="font-black text-xs text-[#0F172A] leading-tight">{q.title}</h4>
                  <span className="px-2 py-0.5 rounded-md bg-slate-100 text-slate-700 border border-slate-300 text-[10px] font-bold whitespace-nowrap">
                    {q.tag}
                  </span>
                </div>

                <p className="text-[11px] font-medium text-slate-600 leading-snug">{q.desc}</p>

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
                        className={`flex-1 py-1.5 rounded-xl text-xs font-bold cursor-pointer transition-colors shadow-xs ${
                          isSelected
                            ? opt === 'No'
                              ? 'bg-emerald-100 text-emerald-900 border border-emerald-400'
                              : opt === 'Yes'
                              ? 'bg-red-100 text-red-900 border border-red-400'
                              : 'bg-amber-100 text-amber-900 border border-amber-400'
                            : 'bg-slate-50 text-slate-700 border border-slate-300 hover:bg-slate-100'
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
        <div className="p-4 rounded-2xl bg-white border-2 border-slate-900 space-y-4 shadow-sm">
          <div>
            <h3 className="text-xs font-black text-[#0F172A] tracking-wide">Numeric Vitals Screening</h3>
            <p className="text-[11px] font-medium text-slate-600 mt-0.5">
              Enter clinical vitals to calculate all potential disease probabilities and differential diagnoses
            </p>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black">
              <label className="text-[10px] text-slate-600 font-bold block mb-0.5">Systolic BP (mmHg)</label>
              <input
                type="number"
                value={sys}
                onChange={(e) => setSys(Number(e.target.value))}
                className="w-full bg-transparent text-sm text-[#0F172A] font-black focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black">
              <label className="text-[10px] text-slate-600 font-bold block mb-0.5">Diastolic BP (mmHg)</label>
              <input
                type="number"
                value={dia}
                onChange={(e) => setDia(Number(e.target.value))}
                className="w-full bg-transparent text-sm text-[#0F172A] font-black focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black">
              <label className="text-[10px] text-slate-600 font-bold block mb-0.5">SpO2 Percentage (%)</label>
              <input
                type="number"
                value={spo2}
                onChange={(e) => setSpo2(Number(e.target.value))}
                className="w-full bg-transparent text-sm text-[#0F172A] font-black focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black">
              <label className="text-[10px] text-slate-600 font-bold block mb-0.5">Heart Rate (bpm)</label>
              <input
                type="number"
                value={hr}
                onChange={(e) => setHr(Number(e.target.value))}
                className="w-full bg-transparent text-sm text-[#0F172A] font-black focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-300 focus-within:border-black">
              <label className="text-[10px] text-slate-600 font-bold block mb-0.5">Blood Glucose (mg/dL)</label>
              <input
                type="number"
                value={sugar}
                onChange={(e) => setSugar(Number(e.target.value))}
                className="w-full bg-transparent text-sm text-[#0F172A] font-black focus:outline-none"
              />
            </div>

            <div className="p-2.5 rounded-xl bg-slate-50 border border-slate-300 flex items-center justify-between">
              <div>
                <label className="text-[10px] text-slate-600 font-bold block">Pregnancy Flag</label>
                <span className="text-[11px] font-bold text-[#0F172A]">{isPregnant ? 'Active ANC' : 'Not Pregnant'}</span>
              </div>
              <button
                type="button"
                onClick={() => setIsPregnant(!isPregnant)}
                className={`px-2.5 py-1 rounded-xl text-[10px] font-black cursor-pointer transition-colors shadow-xs ${
                  isPregnant ? 'bg-black text-white' : 'bg-slate-200 text-slate-700'
                }`}
              >
                {isPregnant ? 'ON' : 'OFF'}
              </button>
            </div>
          </div>

          <button
            onClick={handleRunVitals}
            className="w-full py-3 rounded-xl bg-black hover:bg-neutral-800 text-white font-black text-xs shadow-xs cursor-pointer transition-all active:scale-[0.99]"
          >
            Calculate Vitals Triage
          </button>

          {/* Exhaustive Disease Possibilities & Differential Diagnosis Breakdown */}
          {vitalsResult && (
            <div className="space-y-3.5 pt-2 animate-in fade-in-50 duration-300">
              {/* Primary Clinical Risk Score Banner with 48px Typography */}
              <div className="p-4 rounded-2xl bg-slate-50 border border-slate-300 flex items-center justify-between shadow-xs">
                <div>
                  <span className="text-[10px] uppercase font-bold text-slate-600 tracking-wider block">
                    Composite Clinical Risk Score
                  </span>
                  <div className="stat-headline-bold text-[#0F172A] mt-1">
                    {vitalsResult.riskScore}
                    <span className="text-xl text-slate-500 font-normal">/100</span>
                  </div>
                  <p className="text-[11px] text-slate-600 mt-1 font-medium">
                    Priority Tier: <strong className={vitalsResult.riskLevel === 'CRITICAL' ? 'text-red-700' : 'text-emerald-800'}>{vitalsResult.riskLevel}</strong>
                  </p>
                </div>

                <div className="text-right space-y-1">
                  <div className={`px-3 py-1 rounded-full text-[10px] font-black inline-block shadow-xs ${
                    vitalsResult.riskLevel === 'CRITICAL' 
                      ? 'bg-red-100 text-red-800 border border-red-300 animate-pulse'
                      : 'bg-emerald-100 text-emerald-800 border border-emerald-300'
                  }`}>
                    {vitalsResult.riskLevel}
                  </div>
                  <p className="text-[10px] text-slate-500 font-medium block">MoHFW Guideline Match</p>
                </div>
              </div>

              {/* All Possibilities of Diseases List */}
              <div className="space-y-2.5">
                <div className="flex items-center justify-between">
                  <h4 className="text-xs font-black text-[#0F172A] tracking-wide">
                    All Differential Disease Possibilities ({vitalsResult.diseaseRisksWithPercentage.length})
                  </h4>
                  <span className="text-[10px] text-slate-600 font-bold">Scored by Probability</span>
                </div>

                <div className="space-y-2">
                  {vitalsResult.diseaseRisksWithPercentage.map((d, idx) => {
                    const isCrit = d.riskPercentage >= 70;
                    const isHigh = d.riskPercentage >= 40 && d.riskPercentage < 70;
                    return (
                      <div
                        key={idx}
                        className={`p-3.5 rounded-2xl border space-y-2 shadow-xs ${
                          isCrit
                            ? 'bg-red-50/70 border-red-300'
                            : isHigh
                            ? 'bg-amber-50/50 border-amber-300'
                            : 'bg-white border-slate-200'
                        }`}
                      >
                        <div className="flex items-start justify-between gap-2">
                          <div>
                            <h5 className="text-xs font-black text-[#0F172A]">{d.diseaseName}</h5>
                            <span className="text-[10px] text-slate-500 font-medium mt-0.5 block">{d.riskTier}</span>
                          </div>

                          <div className="text-right">
                            <span className={`text-sm font-mono font-black ${
                              isCrit ? 'text-red-700' : isHigh ? 'text-amber-800' : 'text-emerald-700'
                            }`}>
                              {d.riskPercentage}%
                            </span>
                            <span className="text-[9px] text-slate-500 block">probability</span>
                          </div>
                        </div>

                        {/* Progress Bar */}
                        <div className="w-full bg-slate-200 h-1.5 rounded-full overflow-hidden">
                          <div
                            className={`h-full rounded-full transition-all duration-500 ${
                              isCrit ? 'bg-red-600' : isHigh ? 'bg-amber-600' : 'bg-emerald-600'
                            }`}
                            style={{ width: `${d.riskPercentage}%` }}
                          />
                        </div>

                        {/* Clinical Rationale & Action */}
                        <p className="text-[11px] text-slate-700 font-medium leading-snug">
                          <strong className="text-[#0F172A]">Clinical Rationale:</strong> {d.rationale}
                        </p>
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Immediate Clinical Directives */}
              <div className="p-3.5 rounded-2xl bg-white border border-slate-200 space-y-2 shadow-xs">
                <h5 className="text-xs font-black text-[#0F172A]">ASHA Protocol & Immediate Directives:</h5>
                <div className="space-y-1">
                  {vitalsResult.recommendedActions.map((action, i) => (
                    <div key={i} className="flex items-start gap-2 text-[11px] text-slate-700 font-medium">
                      <CheckCircle2 className="w-3.5 h-3.5 text-[#15803D] shrink-0 mt-0.5" />
                      <span>{action}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};


