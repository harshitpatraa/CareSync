import React, { useState } from 'react';
import { 
  ArrowLeft, 
  Menu, 
  Bell, 
  Camera, 
  Upload, 
  FileText, 
  CheckCircle2, 
  AlertTriangle, 
  Sparkles, 
  RefreshCw, 
  Scan, 
  ShieldCheck,
  ChevronRight,
  HeartPulse,
  Pill,
  Clock,
  UserCheck
} from 'lucide-react';

interface OcrScannerViewProps {
  onBack?: () => void;
  onOpenDrawer?: () => void;
  onNavigateToAi?: (prefillQuery?: string) => void;
  userRole?: 'individual' | 'asha_worker';
}

interface ScannedSlip {
  id: string;
  title: string;
  doctor: string;
  facility: string;
  date: string;
  patientName: string;
  abhaId: string;
  diagnosis: string;
  vitals: {
    bp: string;
    spo2: string;
    pulse: string;
    sugar: string;
  };
  precautions: string[];
  medications: {
    name: string;
    dosage: string;
    frequency: string;
    isSupervisorVerified: boolean;
    supervisorName?: string;
  }[];
}

const SAMPLE_SLIPS: ScannedSlip[] = [
  {
    id: 'sample-1',
    title: 'OPD Clinical Slip: Acute Febrile & Cough',
    doctor: 'Dr. Arvind Mehta (MBBS, MD)',
    facility: 'Primary Health Centre (PHC) Rampur Sector 4',
    date: 'Today, 09:30 AM',
    patientName: 'Ramesh Sharma',
    abhaId: '91-8492-3310-9281',
    diagnosis: 'Acute Viral Febrile Illness with Mild Bronchial Irritation',
    vitals: {
      bp: '124/82 mmHg',
      spo2: '97%',
      pulse: '84 bpm',
      sugar: '108 mg/dL'
    },
    precautions: [
      'Strict bed rest for 48 hours; avoid heavy physical exertion.',
      'Continuous hydration: Drink at least 2.5 - 3 Litres of fluids (warm water, soup, ORS).',
      'Steam inhalation twice daily for 5 minutes.',
      'Seek emergency 108 care if SpO2 drops below 94% or high fever (>102°F) persists.'
    ],
    medications: [
      {
        name: 'Tab Paracetamol',
        dosage: '500 mg',
        frequency: '1 tab every 6 hours PRN fever',
        isSupervisorVerified: true,
        supervisorName: 'Dr. Arvind Mehta (Reg: MCI-78192)'
      },
      {
        name: 'Syp Cetirizine',
        dosage: '5 ml',
        frequency: 'Once at bedtime',
        isSupervisorVerified: true,
        supervisorName: 'Dr. Arvind Mehta (Reg: MCI-78192)'
      },
      {
        name: 'Oral Rehydration Salts (ORS)',
        dosage: '1 sachet / 1 Litre water',
        frequency: 'Sip continuously through the day',
        isSupervisorVerified: true,
        supervisorName: 'Dr. Arvind Mehta (Reg: MCI-78192)'
      }
    ]
  },
  {
    id: 'sample-2',
    title: 'NCD Follow-up Prescription: Hypertension',
    doctor: 'Dr. Priya Kulkarni (MBBS, DGO)',
    facility: 'Ayushman Bharat HWC Rampur Hub',
    date: 'Yesterday, 11:15 AM',
    patientName: 'Sunita Devi',
    abhaId: '91-7712-4401-1190',
    diagnosis: 'Stage-1 Essential Hypertension & Borderline Glycemia',
    vitals: {
      bp: '148/92 mmHg',
      spo2: '98%',
      pulse: '76 bpm',
      sugar: '142 mg/dL'
    },
    precautions: [
      'Low Sodium Diet: Restrict dietary salt to < 3 grams (half teaspoon) daily.',
      'Avoid fried foods, processed snacks, papad, and pickles.',
      '30 minutes brisk walking daily morning or evening.',
      'Maintain daily home BP log; report systolic > 160 mmHg immediately.'
    ],
    medications: [
      {
        name: 'Tab Amlodipine Besylate',
        dosage: '5 mg',
        frequency: '1 tab once daily in morning after breakfast',
        isSupervisorVerified: true,
        supervisorName: 'Dr. Priya Kulkarni (Reg: NHM-DEL-88102)'
      },
      {
        name: 'Tab Metformin HCl',
        dosage: '500 mg',
        frequency: '1 tab twice daily after meals',
        isSupervisorVerified: false // Unverified demo to show guardrail!
      }
    ]
  }
];

export const OcrScannerView: React.FC<OcrScannerViewProps> = ({
  onBack,
  onOpenDrawer,
  onNavigateToAi,
  userRole = 'individual'
}) => {
  const [selectedSlip, setSelectedSlip] = useState<ScannedSlip>(SAMPLE_SLIPS[0]);
  const [isScanning, setIsScanning] = useState<boolean>(false);
  const [scanComplete, setScanComplete] = useState<boolean>(true);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const handleTriggerScan = (slip: ScannedSlip) => {
    setSelectedSlip(slip);
    setIsScanning(true);
    setScanComplete(false);
    setTimeout(() => {
      setIsScanning(false);
      setScanComplete(true);
      showToast("OCR Parsing Complete: Extracted vitals, precautions & Rx!");
    }, 1200);
  };

  return (
    <div className="space-y-3.5 pb-4 select-none text-slate-800">
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed top-12 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-xl bg-[#0F172A] border border-slate-700 text-white text-xs font-black shadow-2xl flex items-center gap-2 animate-in fade-in-50">
          <CheckCircle2 className="w-4 h-4 text-[#15803D] shrink-0" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white border-y border-slate-200"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Top Header Bar */}
      <div className="flex items-center justify-between py-1 border-b border-slate-200 pb-2">
        <div className="flex items-center space-x-2">
          {onOpenDrawer ? (
            <button 
              onClick={onOpenDrawer}
              className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs"
            >
              <Menu className="w-5 h-5" />
            </button>
          ) : onBack ? (
            <button 
              onClick={onBack}
              className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
          ) : null}

          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-black text-sm text-[#0F172A] tracking-tight">Prescription OCR Scanner</span>
              <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-black text-white border border-black uppercase">
                AI Vision
              </span>
            </div>
            <p className="text-[9px] font-bold text-slate-600 leading-none">
              Ayushman Bharat Digital Mission (ABDM) · Smart Slip Parser
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-1.5">
          <div className="px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-black flex items-center gap-1 shadow-xs">
            <ShieldCheck className="w-3 h-3 text-[#15803D]" />
            <span>ABDM</span>
          </div>
          <button className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs">
            <Bell className="w-4 h-4 text-slate-700" />
          </button>
        </div>
      </div>

      {/* Scanner Visual Viewport Card */}
      <div className="p-4 rounded-2xl bg-white border-2 border-slate-900 space-y-3.5 shadow-sm relative overflow-hidden">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-xs font-black text-[#0F172A] uppercase tracking-wider flex items-center gap-1.5">
              <Scan className="w-4 h-4 text-slate-900" />
              <span>Smart Optical Recognition</span>
            </h3>
            <p className="text-[11px] font-medium text-slate-600 mt-0.5">
              Scan doctor handwritten slip, clinic Rx, or diagnostic report
            </p>
          </div>

          <span className="px-2.5 py-1 rounded-full bg-slate-100 text-slate-900 text-[10px] font-black border border-slate-300">
            Offline Engine
          </span>
        </div>

        {/* Mock Camera Viewport / Scanner frame */}
        <div className="relative w-full h-44 rounded-xl bg-slate-900 border-2 border-slate-800 flex flex-col items-center justify-center overflow-hidden p-3 text-white">
          {/* Laser scanning beam animation */}
          {isScanning && (
            <div className="absolute inset-x-0 h-1 bg-[#22C55E] shadow-[0_0_15px_#22C55E] animate-bounce z-20"></div>
          )}

          {/* Corner target reticles */}
          <div className="absolute top-2 left-2 w-4 h-4 border-t-2 border-l-2 border-white z-10"></div>
          <div className="absolute top-2 right-2 w-4 h-4 border-t-2 border-r-2 border-white z-10"></div>
          <div className="absolute bottom-2 left-2 w-4 h-4 border-b-2 border-l-2 border-white z-10"></div>
          <div className="absolute bottom-2 right-2 w-4 h-4 border-b-2 border-r-2 border-white z-10"></div>

          {/* Slip Content Preview inside Camera */}
          <div className="w-[90%] h-[85%] rounded-lg bg-white/95 text-slate-900 p-2.5 text-[10px] space-y-1 shadow-inner relative flex flex-col justify-between">
            <div className="flex items-start justify-between border-b border-slate-200 pb-1">
              <div>
                <span className="font-extrabold text-[10px] text-[#0F172A] block leading-tight">{selectedSlip.facility}</span>
                <span className="text-[9px] text-slate-600 font-bold">{selectedSlip.doctor}</span>
              </div>
              <span className="px-1.5 py-0.2 rounded-xs bg-emerald-50 text-emerald-800 border border-emerald-300 text-[8px] font-black">
                Rx Slip
              </span>
            </div>

            <div className="space-y-0.5">
              <p><strong className="text-slate-900">Citizen:</strong> {selectedSlip.patientName} (ABHA: {selectedSlip.abhaId})</p>
              <p><strong className="text-slate-900">Diagnosis:</strong> {selectedSlip.diagnosis}</p>
              <p><strong className="text-slate-900">Extracted Vitals:</strong> BP {selectedSlip.vitals.bp} · SpO2 {selectedSlip.vitals.spo2} · HR {selectedSlip.vitals.pulse}</p>
            </div>

            <div className="flex items-center justify-between text-[9px] text-slate-500 pt-0.5 border-t border-slate-100">
              <span>{selectedSlip.date}</span>
              <span className="text-emerald-700 font-bold">100% OCR Match</span>
            </div>
          </div>
        </div>

        {/* Scan Actions: Take Photo or Switch Preset Slip */}
        <div className="space-y-2 pt-1">
          <div className="grid grid-cols-2 gap-2">
            <button
              onClick={() => handleTriggerScan(SAMPLE_SLIPS[0])}
              className={`py-2 px-2.5 rounded-xl border text-[11px] font-black flex items-center justify-center gap-1.5 cursor-pointer transition-all ${
                selectedSlip.id === 'sample-1'
                  ? 'bg-black text-white border-black shadow-xs'
                  : 'bg-slate-100 text-slate-700 border-slate-300 hover:bg-slate-200'
              }`}
            >
              <FileText className="w-3.5 h-3.5" />
              <span>Sample 1: Febrile OPD</span>
            </button>

            <button
              onClick={() => handleTriggerScan(SAMPLE_SLIPS[1])}
              className={`py-2 px-2.5 rounded-xl border text-[11px] font-black flex items-center justify-center gap-1.5 cursor-pointer transition-all ${
                selectedSlip.id === 'sample-2'
                  ? 'bg-black text-white border-black shadow-xs'
                  : 'bg-slate-100 text-slate-700 border-slate-300 hover:bg-slate-200'
              }`}
            >
              <FileText className="w-3.5 h-3.5" />
              <span>Sample 2: BP & Sugar</span>
            </button>
          </div>

          <button
            onClick={() => handleTriggerScan(selectedSlip)}
            disabled={isScanning}
            className="w-full py-2.5 rounded-xl bg-black hover:bg-neutral-800 text-white font-black text-xs shadow-xs cursor-pointer flex items-center justify-center gap-2 transition-all active:scale-[0.99]"
          >
            {isScanning ? (
              <>
                <RefreshCw className="w-4 h-4 animate-spin" />
                <span>Scanning & Parsing Prescription...</span>
              </>
            ) : (
              <>
                <Camera className="w-4 h-4" />
                <span>Snap Photo / Rescan Prescription</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Extracted Vitals Cards (Auto-parsed from Prescription) */}
      {scanComplete && (
        <div className="space-y-3">
          <div className="p-3.5 rounded-2xl bg-white border border-slate-200 space-y-2 shadow-xs">
            <div className="flex items-center justify-between">
              <h4 className="text-xs font-black text-[#0F172A] flex items-center gap-1.5">
                <HeartPulse className="w-4 h-4 text-red-600" />
                <span>Auto-Parsed Clinical Vitals</span>
              </h4>
              <span className="text-[10px] font-bold text-emerald-700">Ready to save</span>
            </div>

            <div className="grid grid-cols-4 gap-2 text-center pt-1 border-t border-slate-100">
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
                <span className="text-xs font-black text-[#0F172A] block">{selectedSlip.vitals.bp}</span>
                <span className="text-[9px] font-bold text-slate-500 uppercase">Blood Pressure</span>
              </div>
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
                <span className="text-xs font-black text-[#0F172A] block">{selectedSlip.vitals.spo2}</span>
                <span className="text-[9px] font-bold text-slate-500 uppercase">SpO2 Oxygen</span>
              </div>
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
                <span className="text-xs font-black text-[#0F172A] block">{selectedSlip.vitals.pulse}</span>
                <span className="text-[9px] font-bold text-slate-500 uppercase">Pulse Rate</span>
              </div>
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
                <span className="text-xs font-black text-[#0F172A] block">{selectedSlip.vitals.sugar}</span>
                <span className="text-[9px] font-bold text-slate-500 uppercase">Glucose</span>
              </div>
            </div>
          </div>

          {/* Direct Non-Pharmacological Precautions (Always Direct & Safe) */}
          <div className="p-4 rounded-2xl bg-white border-2 border-slate-900 space-y-2.5 shadow-sm">
            <div className="flex items-center justify-between">
              <h4 className="text-xs font-black text-[#0F172A] flex items-center gap-1.5">
                <ShieldCheck className="w-4 h-4 text-[#15803D]" />
                <span>Direct Home Precautions & Care Instructions</span>
              </h4>
              <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-emerald-50 text-[#15803D] border border-emerald-300">
                Immediate Action
              </span>
            </div>

            <p className="text-[11px] font-medium text-slate-600">
              Follow these approved non-drug precautions directly at home:
            </p>

            <div className="space-y-1.5">
              {selectedSlip.precautions.map((prec, idx) => (
                <div key={idx} className="flex items-start gap-2 p-2 rounded-xl bg-slate-50 border border-slate-200 text-xs text-slate-800">
                  <CheckCircle2 className="w-3.5 h-3.5 text-[#15803D] shrink-0 mt-0.5" />
                  <span className="font-bold">{prec}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Prescribed Medications with Medical Supervisor Verification Gate */}
          <div className="p-4 rounded-2xl bg-white border border-slate-200 space-y-3 shadow-xs">
            <div className="flex items-center justify-between">
              <div>
                <h4 className="text-xs font-black text-[#0F172A] flex items-center gap-1.5">
                  <Pill className="w-4 h-4 text-slate-900" />
                  <span>Parsed Medications & Supervisor Verification</span>
                </h4>
                <p className="text-[10px] font-medium text-slate-500">
                  Pharmacological drugs require verified sign-off by Medical Officer
                </p>
              </div>
            </div>

            <div className="space-y-2">
              {selectedSlip.medications.map((med, idx) => (
                <div 
                  key={idx} 
                  className={`p-3 rounded-xl border space-y-1.5 ${
                    med.isSupervisorVerified 
                      ? 'bg-white border-slate-200' 
                      : 'bg-amber-50/70 border-amber-300'
                  }`}
                >
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <h5 className="text-xs font-black text-[#0F172A]">{med.name} ({med.dosage})</h5>
                      <p className="text-[10px] font-bold text-slate-600 mt-0.5">{med.frequency}</p>
                    </div>

                    {med.isSupervisorVerified ? (
                      <span className="px-2 py-0.5 rounded-full bg-emerald-50 text-[#15803D] border border-emerald-300 text-[9px] font-black flex items-center gap-1 shrink-0">
                        <CheckCircle2 className="w-3 h-3 text-[#15803D]" />
                        <span>Supervisor Verified</span>
                      </span>
                    ) : (
                      <span className="px-2 py-0.5 rounded-full bg-amber-100 text-amber-900 border border-amber-300 text-[9px] font-black flex items-center gap-1 shrink-0 animate-pulse">
                        <AlertTriangle className="w-3 h-3 text-amber-700" />
                        <span>Verification Required</span>
                      </span>
                    )}
                  </div>

                  {med.isSupervisorVerified ? (
                    <p className="text-[9px] text-[#15803D] font-mono font-bold flex items-center gap-1">
                      <UserCheck className="w-3 h-3" />
                      <span>Authorized by: {med.supervisorName}</span>
                    </p>
                  ) : (
                    <div className="p-2 rounded-lg bg-amber-100/60 border border-amber-200 text-[10px] text-amber-900 font-bold">
                      ⚠️ Caution: Medication withheld until verified by assigned PHC Medical Officer. Take only verified home precautions until verified.
                    </div>
                  )}
                </div>
              ))}
            </div>

            {/* Quick Button to Ask AI Assistant about this prescription */}
            {onNavigateToAi && (
              <button
                onClick={() => onNavigateToAi(`I scanned a prescription for ${selectedSlip.diagnosis}. What direct precautions and diet should I follow, and has the medication been verified by the medical supervisor?`)}
                className="w-full py-2.5 rounded-xl bg-slate-100 border border-slate-300 hover:bg-slate-200 text-slate-900 font-black text-xs flex items-center justify-center gap-2 cursor-pointer transition-colors shadow-xs"
              >
                <Sparkles className="w-3.5 h-3.5 text-slate-900" />
                <span>Ask Sehat Setu AI to Explain this Prescription</span>
                <ChevronRight className="w-3 h-3" />
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
