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
  UserCheck,
  Plus,
  BookmarkCheck
} from 'lucide-react';

interface OcrScannerViewProps {
  onBack?: () => void;
  onOpenDrawer?: () => void;
  onNavigateToAi?: (prefillQuery?: string) => void;
  userRole?: 'individual' | 'asha_worker';
}

export interface DemoMedicationSuggestion {
  name: string;
  dosage: string;
  indication: string;
  instructions: string;
  category: string;
  recommendedDoctor: string;
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
  demoSuggestions: DemoMedicationSuggestion[];
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
    ],
    demoSuggestions: [
      {
        name: 'Tab Paracetamol',
        dosage: '650 mg',
        indication: 'बुखार एवं गंभीर बदन दर्द कम करने हेतु',
        instructions: '1 गोली भोजन के बाद, दिन में 3 बार (आवश्यकतानुसार)',
        category: 'Antipyretic / Analgesic',
        recommendedDoctor: 'Dr. Arvind Mehta (PHC Medical Officer)'
      },
      {
        name: 'Tab Cetirizine HCl',
        dosage: '10 mg',
        indication: 'नाक बहना, छींकें और गले की एलर्जी शांत करने हेतु',
        instructions: '1 गोली रात में सोने से पहले',
        category: 'Antihistamine',
        recommendedDoctor: 'Dr. Arvind Mehta (PHC Medical Officer)'
      },
      {
        name: 'Syrup Ambroxol + Guaiphenesin',
        dosage: '100 ml (5 ml per dose)',
        indication: 'छाती की जकड़न एवं बलगम वाली खांसी बाहर निकालने हेतु',
        instructions: '5 मिली गुनगुने पानी के साथ दिन में 2 बार',
        category: 'Mucolytic Expectorant',
        recommendedDoctor: 'Dr. Arvind Mehta (PHC Medical Officer)'
      },
      {
        name: 'ORS Electrolyte Sachet (WHO Formula)',
        dosage: '21.8g Sachet in 1L Water',
        indication: 'शरीर में पानी और लवण की कमी (डिहाइड्रेशन) से बचाव हेतु',
        instructions: 'दिनभर घूंट-घूंट करके पिएं (24 घंटे में समाप्त करें)',
        category: 'Hydration & Electrolytes',
        recommendedDoctor: 'National Health Mission Standard'
      },
      {
        name: 'Tab Pantoprazole',
        dosage: '40 mg',
        indication: 'दवाइयों से पेट में होने वाली जलन और एसिडिटी की रोकथाम',
        instructions: '1 गोली सुबह खाली पेट, 3 दिन तक',
        category: 'Gastroprotective (PPI)',
        recommendedDoctor: 'Dr. Arvind Mehta (PHC Medical Officer)'
      }
    ]
  },
  {
    id: 'sample-2',
    title: 'NCD Follow-up Prescription: Hypertension & Sugar',
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
        isSupervisorVerified: false
      }
    ],
    demoSuggestions: [
      {
        name: 'Tab Telmisartan',
        dosage: '40 mg',
        indication: 'धमनियों का खिंचाव कम कर सिस्टोलिक बीपी सामान्य रखने हेतु',
        instructions: '1 गोली प्रतिदिन सुबह नाश्ते के बाद',
        category: 'ARB Antihypertensive',
        recommendedDoctor: 'Dr. Priya Kulkarni (Medical Officer)'
      },
      {
        name: 'Tab Metformin HCl (Sustained Release)',
        dosage: '500 mg',
        indication: 'फास्टिंग एवं पोस्ट-मील ब्लड शुगर नियंत्रित करने हेतु',
        instructions: '1 गोली रात के भोजन के साथ',
        category: 'Oral Hypoglycemic (Biguanide)',
        recommendedDoctor: 'Dr. Priya Kulkarni (Medical Officer)'
      },
      {
        name: 'Tab Atorvastatin',
        dosage: '10 mg',
        indication: 'रक्त में कोलेस्ट्रॉल और हृदय धमनियों में ब्लॉकेज से बचाव हेतु',
        instructions: '1 गोली रात को सोते समय',
        category: 'Lipid Lowering Statin',
        recommendedDoctor: 'Dr. Priya Kulkarni (Medical Officer)'
      },
      {
        name: 'Tab Neurobion Forte / B-Complex',
        dosage: '1 Tablet Daily',
        indication: 'डायबिटिक नसों की कमजोरी, झनझनाहट और सुन्नपन की रोकथाम',
        instructions: '1 गोली दोपहर के भोजन के बाद',
        category: 'Nerve & Micronutrient Support',
        recommendedDoctor: 'MoHFW NCD Protocol'
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
  const [addedMeds, setAddedMeds] = useState<Record<string, boolean>>({});

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

  const handleSimulateScan = () => {
    setIsScanning(true);
    setScanComplete(false);
    setTimeout(() => {
      setIsScanning(false);
      setScanComplete(true);
      showToast('OCR Prescription Analyzed! 5 Demo Medications & Home Precautions Extracted.');
    }, 1400);
  };

  const handleAddMedToSchedule = (medName: string) => {
    setAddedMeds(prev => ({ ...prev, [medName]: true }));
    showToast(`✅ ${medName} added to personal ABDM health vault!`);
  };

  return (
    <div className="space-y-3.5 pb-6 select-none text-slate-800">
      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white border-y border-slate-200"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Top Header Bar */}
      <div className="flex items-center justify-between py-1 border-b border-slate-200 pb-2">
        <div className="flex items-center space-x-2">
          <button 
            onClick={onOpenDrawer || onBack}
            className="p-1.5 rounded-lg bg-white border border-slate-300 text-black hover:bg-slate-50 cursor-pointer shadow-xs"
          >
            <Menu className="w-5 h-5 text-black" />
          </button>
          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-black text-sm text-black tracking-tight">Rx & Lab OCR Scanner</span>
              <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-black text-white border border-black uppercase">
                AI Vision
              </span>
            </div>
            <p className="text-[9px] font-bold text-slate-600 leading-none">
              ABDM Optical Clinical Parser · Demo Meds & Precautions
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-1.5">
          <div className="px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-black flex items-center gap-1 shadow-xs">
            <ShieldCheck className="w-3 h-3 text-[#15803D]" />
            <span>ABDM</span>
          </div>
          <button className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-800 hover:bg-slate-50 cursor-pointer shadow-xs">
            <Bell className="w-4 h-4 text-slate-800" />
          </button>
        </div>
      </div>

      {/* Toast Alert */}
      {toastMessage && (
        <div className="p-2.5 rounded-xl bg-black text-white border border-black text-xs font-bold flex items-center justify-between shadow-lg animate-in fade-in slide-in-from-top-2">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
            <span>{toastMessage}</span>
          </div>
          <button onClick={() => setToastMessage(null)} className="text-slate-400 hover:text-white text-xs px-1">✕</button>
        </div>
      )}

      {/* Scanner Viewfinder Box */}
      <div className="p-4 rounded-2xl bg-white border-2 border-slate-900 space-y-3 shadow-sm">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl bg-black text-white flex items-center justify-center">
              <Scan className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-xs font-black text-black">Scan Prescription / Lab Slip</h3>
              <p className="text-[10px] font-medium text-slate-600">Align OPD paper or upload slip image</p>
            </div>
          </div>
          <span className="text-[9px] font-bold px-2 py-0.5 rounded-full bg-slate-100 text-slate-800 border border-slate-300">
            Offline Safe
          </span>
        </div>

        {/* Camera Viewfinder Mockup */}
        <div className="relative w-full h-44 rounded-xl bg-slate-950 border-2 border-dashed border-slate-700 overflow-hidden flex flex-col items-center justify-center text-slate-400">
          {/* Laser Scanning Animation Bar */}
          {isScanning && (
            <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-emerald-500 via-amber-400 to-emerald-500 animate-bounce shadow-lg shadow-emerald-500/50" />
          )}

          {/* Corner brackets */}
          <div className="absolute top-3 left-3 w-5 h-5 border-t-2 border-l-2 border-emerald-400" />
          <div className="absolute top-3 right-3 w-5 h-5 border-t-2 border-r-2 border-emerald-400" />
          <div className="absolute bottom-3 left-3 w-5 h-5 border-b-2 border-l-2 border-emerald-400" />
          <div className="absolute bottom-3 right-3 w-5 h-5 border-b-2 border-r-2 border-emerald-400" />

          {isScanning ? (
            <div className="flex flex-col items-center space-y-2 text-white">
              <RefreshCw className="w-7 h-7 text-emerald-400 animate-spin" />
              <span className="text-xs font-black">AI Vision Parsing Handwriting & Meds...</span>
              <span className="text-[10px] text-slate-400">Extracting diagnosis, vitals & drug dosages</span>
            </div>
          ) : (
            <div className="flex flex-col items-center space-y-1.5 text-center px-4">
              <Camera className="w-8 h-8 text-slate-300" />
              <p className="text-xs font-black text-white">{selectedSlip.title}</p>
              <p className="text-[10px] text-slate-400">{selectedSlip.doctor} · {selectedSlip.facility}</p>
            </div>
          )}
        </div>

        {/* Sample Slip Selector Tabs */}
        <div className="space-y-1">
          <label className="text-[10px] font-black text-black block">Choose Sample Slip for Demo:</label>
          <div className="grid grid-cols-2 gap-2">
            {SAMPLE_SLIPS.map((slip) => (
              <button
                key={slip.id}
                type="button"
                onClick={() => {
                  setSelectedSlip(slip);
                  showToast(`Selected: ${slip.title}`);
                }}
                className={`p-2 rounded-xl text-left border cursor-pointer transition-all ${
                  selectedSlip.id === slip.id
                    ? 'bg-black text-white border-black shadow-xs'
                    : 'bg-slate-50 text-slate-800 border-slate-300 hover:bg-slate-100'
                }`}
              >
                <span className="text-[10px] font-black block truncate">{slip.title}</span>
                <span className={`text-[9px] block ${selectedSlip.id === slip.id ? 'text-slate-300' : 'text-slate-500'}`}>
                  {slip.patientName} · {slip.vitals.bp}
                </span>
              </button>
            ))}
          </div>
        </div>

        {/* Scan Action Button */}
        <button
          onClick={handleSimulateScan}
          disabled={isScanning}
          className="w-full py-2.5 rounded-xl bg-black hover:bg-neutral-800 text-white font-black text-xs flex items-center justify-center gap-2 cursor-pointer shadow-xs transition-colors"
        >
          <Scan className="w-3.5 h-3.5" />
          <span>{isScanning ? 'Extracting Clinical Data...' : 'Analyze Slip with CDSS OCR'}</span>
        </button>
      </div>

      {/* Scanned Results Container */}
      {scanComplete && (
        <div className="space-y-3 animate-in fade-in-50 duration-300">
          
          {/* Slip Patient Info Header Card */}
          <div className="p-3.5 rounded-2xl bg-white border border-slate-300 space-y-2 shadow-xs">
            <div className="flex items-start justify-between">
              <div>
                <span className="text-[9px] font-black px-1.5 py-0.5 rounded bg-emerald-100 text-emerald-950 uppercase border border-emerald-300">
                  Verified Slip
                </span>
                <h4 className="text-xs font-black text-black mt-1">{selectedSlip.title}</h4>
                <p className="text-[10px] font-medium text-slate-600">{selectedSlip.facility}</p>
              </div>
              <span className="text-[10px] font-mono font-bold text-slate-500">{selectedSlip.date}</span>
            </div>

            <div className="pt-2 border-t border-slate-100 grid grid-cols-2 gap-2 text-[10px]">
              <div>
                <span className="text-slate-500 block">Patient Name:</span>
                <span className="font-black text-black">{selectedSlip.patientName}</span>
              </div>
              <div>
                <span className="text-slate-500 block">ABHA ID:</span>
                <span className="font-mono font-bold text-black">{selectedSlip.abhaId}</span>
              </div>
            </div>

            <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
              <span className="text-[10px] text-slate-500 block font-bold">Detected Diagnosis:</span>
              <span className="text-xs font-black text-black">{selectedSlip.diagnosis}</span>
            </div>
          </div>

          {/* Extracted Vitals Grid */}
          <div className="p-3.5 rounded-2xl bg-white border border-slate-300 space-y-2 shadow-xs">
            <h4 className="text-xs font-black text-black flex items-center gap-1.5">
              <HeartPulse className="w-3.5 h-3.5 text-black" />
              <span>Extracted Clinical Vitals</span>
            </h4>

            <div className="grid grid-cols-4 gap-1.5 text-center">
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
                <span className="text-xs font-black text-black block">{selectedSlip.vitals.bp}</span>
                <span className="text-[9px] font-bold text-slate-500 uppercase">Blood Pres.</span>
              </div>
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
                <span className="text-xs font-black text-black block">{selectedSlip.vitals.spo2}</span>
                <span className="text-[9px] font-bold text-slate-500 uppercase">SpO2</span>
              </div>
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
                <span className="text-xs font-black text-black block">{selectedSlip.vitals.pulse}</span>
                <span className="text-[9px] font-bold text-slate-500 uppercase">Pulse</span>
              </div>
              <div className="p-2 rounded-xl bg-slate-50 border border-slate-200">
                <span className="text-xs font-black text-black block">{selectedSlip.vitals.sugar}</span>
                <span className="text-[9px] font-bold text-slate-500 uppercase">Glucose</span>
              </div>
            </div>
          </div>

          {/* 💊 NEW: AI DEMO MEDICATION SUGGESTIONS (Requested by User) */}
          <div className="p-4 rounded-2xl bg-white border-2 border-slate-900 space-y-3 shadow-sm">
            <div className="flex items-center justify-between border-b border-slate-100 pb-2">
              <div className="flex items-center gap-2">
                <div className="w-7 h-7 rounded-xl bg-black text-white flex items-center justify-center">
                  <Pill className="w-4 h-4" />
                </div>
                <div>
                  <h4 className="text-xs font-black text-black">AI Demo Medication Suggestions</h4>
                  <p className="text-[10px] font-medium text-slate-600">
                    Clinical suggestions based on parsed diagnosis: {selectedSlip.diagnosis}
                  </p>
                </div>
              </div>
              <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-300">
                CDSS Demo
              </span>
            </div>

            {/* List of Demo Suggested Medicines */}
            <div className="space-y-2">
              {selectedSlip.demoSuggestions.map((sug, idx) => {
                const isAdded = !!addedMeds[sug.name];
                return (
                  <div 
                    key={idx}
                    className="p-3 rounded-xl bg-slate-50 border border-slate-300 hover:border-black space-y-1.5 transition-colors shadow-xs"
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div>
                        <div className="flex items-center gap-2">
                          <h5 className="text-xs font-black text-black">{sug.name}</h5>
                          <span className="text-[10px] font-black px-1.5 py-0.2 rounded bg-black text-white">
                            {sug.dosage}
                          </span>
                        </div>
                        <span className="text-[9px] font-bold text-slate-500 block mt-0.5">
                          Category: {sug.category}
                        </span>
                      </div>

                      <button
                        type="button"
                        onClick={() => handleAddMedToSchedule(sug.name)}
                        className={`px-2.5 py-1 rounded-lg text-[10px] font-black flex items-center gap-1 cursor-pointer transition-all ${
                          isAdded
                            ? 'bg-emerald-600 text-white shadow-xs'
                            : 'bg-black text-white hover:bg-neutral-800 shadow-xs'
                        }`}
                      >
                        {isAdded ? (
                          <>
                            <BookmarkCheck className="w-3 h-3" />
                            <span>Added</span>
                          </>
                        ) : (
                          <>
                            <Plus className="w-3 h-3" />
                            <span>Add to Meds</span>
                          </>
                        )}
                      </button>
                    </div>

                    {/* Purpose / Indication */}
                    <div className="text-[11px] text-slate-800 font-medium">
                      <strong className="text-black font-black">🎯 उद्देश्य (Purpose):</strong> {sug.indication}
                    </div>

                    {/* Regimen & Timing */}
                    <div className="text-[10px] text-slate-700 font-bold flex items-center gap-1">
                      <Clock className="w-3 h-3 text-slate-500" />
                      <span>{sug.instructions}</span>
                    </div>

                    {/* Doctor Recommendation Badge */}
                    <div className="pt-1 border-t border-slate-200 text-[9px] text-[#15803D] font-mono font-bold flex items-center gap-1">
                      <UserCheck className="w-3 h-3" />
                      <span>Recommended by: {sug.recommendedDoctor}</span>
                    </div>
                  </div>
                );
              })}
            </div>

            <p className="text-[9px] text-slate-500 font-medium italic text-center">
              * Official demo suggestions aligned with National Health Mission (MoHFW) Standard Treatment Guidelines.
            </p>
          </div>

          {/* Direct Non-Pharmacological Precautions */}
          <div className="p-4 rounded-2xl bg-white border border-slate-300 space-y-2.5 shadow-xs">
            <div className="flex items-center justify-between">
              <h4 className="text-xs font-black text-black flex items-center gap-1.5">
                <ShieldCheck className="w-4 h-4 text-[#15803D]" />
                <span>Direct Home Precautions & Care Instructions</span>
              </h4>
              <span className="text-[9px] font-black px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-300">
                Immediate Action
              </span>
            </div>

            <p className="text-[11px] font-medium text-slate-600">
              Approved non-drug precautions to follow at home:
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
          <div className="p-4 rounded-2xl bg-white border border-slate-300 space-y-3 shadow-xs">
            <div className="flex items-center justify-between">
              <div>
                <h4 className="text-xs font-black text-black flex items-center gap-1.5">
                  <Pill className="w-4 h-4 text-black" />
                  <span>Parsed Rx Medications & Supervisor Verification</span>
                </h4>
                <p className="text-[10px] font-medium text-slate-500">
                  Prescription medicines require authorized Medical Officer sign-off
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
                      <h5 className="text-xs font-black text-black">{med.name} ({med.dosage})</h5>
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

            {/* Quick Button to Ask AI Assistant */}
            {onNavigateToAi && (
              <button
                onClick={() => onNavigateToAi(`Explain prescription for ${selectedSlip.diagnosis} in less words.`)}
                className="w-full py-2.5 rounded-xl bg-black hover:bg-neutral-800 text-white font-black text-xs flex items-center justify-center gap-2 cursor-pointer transition-colors shadow-xs"
              >
                <Sparkles className="w-3.5 h-3.5 text-amber-400" />
                <span>Ask Sehat Setu to Explain Prescription in Less Words</span>
                <ChevronRight className="w-3 h-3" />
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
