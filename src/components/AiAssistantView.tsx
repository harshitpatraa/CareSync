import React, { useState, useRef, useEffect } from 'react';
import { 
  Menu, 
  Bell, 
  Send, 
  Bot,
  ShieldCheck,
  CheckCircle2,
  AlertTriangle,
  Lock,
  Unlock,
  HeartPulse,
  Apple,
  Pill,
  UserCheck
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { ChatMessage } from '../types';
import { queryMedicalAi } from '../services/medicalAiService';

interface AiAssistantViewProps {
  onBack?: () => void;
  onOpenDrawer?: () => void;
  userRole?: 'individual' | 'asha_worker';
}

export const AiAssistantView: React.FC<AiAssistantViewProps> = ({ 
  onBack, 
  onOpenDrawer,
  userRole = 'asha_worker'
}) => {
  const { language } = useApp();
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: "msg-welcome",
      isUser: false,
      text: "Namaste! I am Sehat Setu, your official AI Health Assistant certified under Ministry of Health & Family Welfare (MoHFW) protocols. I provide direct, verified non-pharmacological precautions, home care, dietary guidance, and emergency danger signs immediately. In compliance with ABDM safety standards, prescription medications remain locked until authorized by a certified Medical Supervisor.",
      timestamp: "Sehat Setu",
      tags: ["Sehat Setu", "MoHFW Protocol", "Safety Guardrail Active"],
      precautions: [
        "Record baseline vitals: BP, SpO2, Temperature, and Pulse Rate",
        "Maintain clean boiled water and oral fluid hydration",
        "Keep restful sleep schedule and avoid strenuous physical labor during illness"
      ],
      dietAdvice: [
        "Nutritious home meals: light khichdi, vegetable soups, sprouts",
        "Avoid fried, spicy street food and sugary beverages"
      ],
      warningRedFlags: [
        "Fever > 103°F or persisting > 48 hours",
        "Shortness of breath (SpO2 < 94%) or chest pain",
        "Continuous vomiting or severe dizziness upon standing"
      ]
    }
  ]);
  const [inputText, setInputText] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isTyping]);

  const handleSend = (textToSend?: string) => {
    const text = textToSend || inputText;
    if (!text.trim()) return;

    const userMsg: ChatMessage = {
      id: `u-${Date.now()}`,
      isUser: true,
      text: text.trim(),
      timestamp: "Just now"
    };

    setMessages(prev => [...prev, userMsg]);
    if (!textToSend) setInputText("");
    setIsTyping(true);

    setTimeout(() => {
      const response = queryMedicalAi(text, language);
      const botMsg: ChatMessage = {
        id: `b-${Date.now()}`,
        isUser: false,
        text: response.text,
        timestamp: "Sehat Setu",
        tags: response.tags,
        precautions: response.precautions,
        dietAdvice: response.dietAdvice,
        warningRedFlags: response.warningRedFlags,
        prescriptions: response.prescriptions,
        requiresSupervisorVerification: response.requiresSupervisorVerification,
        supervisorVerified: false
      };
      setMessages(prev => [...prev, botMsg]);
      setIsTyping(false);
    }, 450);
  };

  const handleVerifyPrescription = (messageId: string) => {
    setMessages(prev => prev.map(msg => {
      if (msg.id === messageId) {
        return {
          ...msg,
          supervisorVerified: true,
          verifiedBy: "Dr. Priya Kulkarni (PHC Medical Officer, Reg #MH-88421)",
          verifiedAt: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        };
      }
      return msg;
    }));
  };

  const quickChips = [
    { label: "Fever & dengue precautions", query: "Fever & dengue watch protocol" },
    { label: "High blood pressure home care", query: "ANC high BP warning signs" },
    { label: "Diabetes dietary rules", query: "Type 2 diabetes meal plan" },
    { label: "Burn & wound first aid", query: "First aid for burns and cuts" }
  ];

  return (
    <div className="flex flex-col h-full space-y-2.5 select-none text-slate-800">
      {/* Official Government Tricolor Header Bar */}
      <div className="h-1.5 w-full rounded-full flex overflow-hidden shadow-xs">
        <div className="w-1/3 bg-[#FF9933]"></div>
        <div className="w-1/3 bg-white border-y border-slate-200"></div>
        <div className="w-1/3 bg-[#138808]"></div>
      </div>

      {/* Top Bar - Clean Light Mode */}
      <div className="flex items-center justify-between py-1 border-b border-slate-200 pb-2">
        <div className="flex items-center space-x-2">
          <button 
            onClick={onOpenDrawer || onBack}
            className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs"
          >
            <Menu className="w-5 h-5" />
          </button>

          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-black text-sm text-[#0F172A] tracking-tight">Sehat Setu AI</span>
              <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-black text-white border border-black uppercase">
                MoHFW CDSS
              </span>
            </div>
            <p className="text-[9px] font-bold text-slate-600 leading-none">
              Direct Precautions Engine · Supervisor-Verified Prescriptions
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-2">
          <div className="px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-800 border border-emerald-300 text-[10px] font-black flex items-center gap-1 shadow-xs">
            <ShieldCheck className="w-3 h-3 text-[#15803D]" />
            <span>ABDM</span>
          </div>
          <button className="p-1.5 rounded-lg bg-white border border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer shadow-xs">
            <Bell className="w-4 h-4 text-slate-700" />
          </button>
        </div>
      </div>

      {/* Safety Protocol Banner */}
      <div className="px-3 py-1.5 rounded-xl bg-slate-100 border border-slate-300 flex items-center justify-between text-[10px] text-slate-900 font-bold shadow-xs">
        <div className="flex items-center gap-1.5">
          <Bot className="w-3.5 h-3.5 text-slate-900 shrink-0" />
          <span>Clinical Rule: Precautions given directly · Medicines require Supervisor Sign-Off</span>
        </div>
        <span className="px-1.5 py-0.2 bg-black text-white rounded text-[8px] font-black shrink-0">
          Govt Active
        </span>
      </div>

      {/* Chat Messages Flow */}
      <div className="flex-1 overflow-y-auto space-y-3 pr-1 text-xs">
        {messages.map((m) => (
          <div key={m.id} className="space-y-1.5">
            {/* User or Bot Bubble */}
            <div className={`p-3.5 rounded-2xl text-xs leading-relaxed space-y-2.5 shadow-xs ${
              m.isUser
                ? 'bg-black text-white ml-auto max-w-[85%] rounded-tr-none font-medium'
                : 'bg-white text-slate-800 border border-slate-200 max-w-[98%] rounded-tl-none font-medium'
            }`}>
              {!m.isUser && (
                <div className="flex items-center justify-between text-slate-900 font-black text-xs pb-1.5 border-b border-slate-100">
                  <span className="flex items-center gap-1.5">
                    <Bot className="w-3.5 h-3.5" />
                    <span>Sehat Setu Clinical Response</span>
                  </span>
                  <span className="text-[9px] font-bold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800 border border-emerald-300 flex items-center gap-1">
                    <CheckCircle2 className="w-2.5 h-2.5 text-[#15803D]" />
                    <span>MoHFW Certified</span>
                  </span>
                </div>
              )}

              {/* Response Text Overview */}
              <div className={m.isUser ? 'text-white font-medium leading-relaxed' : 'text-slate-800 font-medium leading-relaxed whitespace-pre-line'}>
                {m.text}
              </div>

              {/* 1. DIRECT PRECAUTIONS (Given Directly as requested) */}
              {!m.isUser && m.precautions && m.precautions.length > 0 && (
                <div className="p-3 rounded-xl bg-emerald-50/70 border border-emerald-300 space-y-1.5">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-1.5 text-[11px] font-black text-emerald-950">
                      <HeartPulse className="w-3.5 h-3.5 text-[#15803D]" />
                      <span>तत्काल सावधानियां एवं प्राथमिक देखभाल (Direct Precautions)</span>
                    </div>
                    <span className="text-[8px] font-black px-1.5 py-0.5 rounded bg-emerald-200 text-emerald-900 uppercase">
                      Direct Access
                    </span>
                  </div>
                  <ul className="space-y-1 text-[11px] text-emerald-950 font-medium pl-1">
                    {m.precautions.map((prec, i) => (
                      <li key={i} className="flex items-start gap-1.5">
                        <span className="text-[#15803D] font-black">✓</span>
                        <span>{prec}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* Dietary Advice */}
              {!m.isUser && m.dietAdvice && m.dietAdvice.length > 0 && (
                <div className="p-3 rounded-xl bg-amber-50/70 border border-amber-300 space-y-1.5">
                  <div className="flex items-center gap-1.5 text-[11px] font-black text-amber-950">
                    <Apple className="w-3.5 h-3.5 text-amber-800" />
                    <span>आहार व पोषण दिशानिर्देश (Dietary & Hydration Care)</span>
                  </div>
                  <ul className="space-y-1 text-[11px] text-amber-950 font-medium pl-1">
                    {m.dietAdvice.map((diet, i) => (
                      <li key={i} className="flex items-start gap-1.5">
                        <span className="text-amber-800 font-bold">•</span>
                        <span>{diet}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* Danger Red Flags */}
              {!m.isUser && m.warningRedFlags && m.warningRedFlags.length > 0 && (
                <div className="p-3 rounded-xl bg-red-50/80 border border-red-300 space-y-1.5">
                  <div className="flex items-center gap-1.5 text-[11px] font-black text-red-900">
                    <AlertTriangle className="w-3.5 h-3.5 text-red-600" />
                    <span>खतरे के संकेत (Emergency Red Flags - Call 108 If Observed)</span>
                  </div>
                  <ul className="space-y-1 text-[11px] text-red-950 font-medium pl-1">
                    {m.warningRedFlags.map((flag, i) => (
                      <li key={i} className="flex items-start gap-1.5">
                        <span className="text-red-600 font-black">⚠</span>
                        <span>{flag}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* 2. PRESCRIPTION MEDICINES GUARDRAIL (दवाइयां - सिर्फ सुपरवाइजर वेरिफिकेशन के बाद) */}
              {!m.isUser && m.prescriptions && m.prescriptions.length > 0 && (
                <div className={`p-3.5 rounded-xl border-2 space-y-2.5 transition-all shadow-xs ${
                  m.supervisorVerified
                    ? 'bg-emerald-50/90 border-emerald-600'
                    : 'bg-slate-50 border-slate-400'
                }`}>
                  {/* Guardrail Status Header */}
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-1.5">
                      <Pill className={`w-4 h-4 ${m.supervisorVerified ? 'text-[#15803D]' : 'text-slate-700'}`} />
                      <span className="text-xs font-black text-[#0F172A]">
                        {m.supervisorVerified ? 'प्रमाणित दवाइयां (Authorized Prescriptions)' : 'दवाइयां (Prescriptions - Verification Locked)'}
                      </span>
                    </div>

                    <div className="flex items-center gap-1">
                      {m.supervisorVerified ? (
                        <span className="px-2 py-0.5 rounded-full bg-emerald-600 text-white text-[9px] font-black flex items-center gap-1 shadow-xs">
                          <CheckCircle2 className="w-3 h-3" />
                          <span>MO Verified</span>
                        </span>
                      ) : (
                        <span className="px-2 py-0.5 rounded-full bg-amber-500 text-slate-950 text-[9px] font-black flex items-center gap-1 shadow-xs">
                          <Lock className="w-3 h-3" />
                          <span>Verification Required</span>
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Verification Banner */}
                  {!m.supervisorVerified ? (
                    <div className="p-2.5 rounded-lg bg-amber-50 border border-amber-300 text-[10px] text-amber-950 space-y-1 font-medium">
                      <div className="flex items-center gap-1 font-black text-amber-900">
                        <AlertTriangle className="w-3 h-3 text-amber-800" />
                        <span>सुरक्षा नियम: बिना सुपरवाइजर सत्यापन कोई दवा सीधे नहीं दी जाएगी</span>
                      </div>
                      <p className="leading-snug">
                        MoHFW safety mandate: AI directly gives precautions and first-aid only. All pharmaceutical medications remain restricted until verified by the designated Medical Officer / Clinical Supervisor.
                      </p>
                    </div>
                  ) : (
                    <div className="p-2 rounded-lg bg-emerald-100 border border-emerald-400 text-[10px] text-emerald-950 space-y-0.5 font-bold">
                      <div className="flex items-center gap-1">
                        <UserCheck className="w-3.5 h-3.5 text-[#15803D]" />
                        <span>Authorized By: {m.verifiedBy}</span>
                      </div>
                      <p className="text-[9px] text-emerald-800">
                        Verification Timestamp: Today at {m.verifiedAt} · ABDM Clinical Audit ID #ABDM-CDSS-8812
                      </p>
                    </div>
                  )}

                  {/* Medication List */}
                  <div className="space-y-1.5">
                    {m.prescriptions.map((rx, idx) => (
                      <div 
                        key={idx} 
                        className={`p-2 rounded-lg border text-xs flex items-center justify-between transition-colors ${
                          m.supervisorVerified
                            ? 'bg-white border-emerald-300'
                            : 'bg-white/80 border-slate-300 opacity-80'
                        }`}
                      >
                        <div>
                          <div className="flex items-center gap-2">
                            <span className="font-black text-[#0F172A]">{rx.name}</span>
                            <span className="text-[10px] font-bold px-1.5 py-0.2 rounded bg-slate-100 text-slate-800 border border-slate-300">
                              {rx.dosage}
                            </span>
                          </div>
                          <p className="text-[10px] text-slate-600 font-medium mt-0.5">
                            {rx.frequency} · {rx.duration}
                          </p>
                        </div>

                        <div className="text-right shrink-0">
                          {m.supervisorVerified ? (
                            <span className="text-[9px] font-black text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-300">
                              Approved
                            </span>
                          ) : (
                            <span className="text-[9px] font-black text-slate-500 bg-slate-100 px-2 py-0.5 rounded border border-slate-300 flex items-center gap-0.5">
                              <Lock className="w-2.5 h-2.5" />
                              Locked
                            </span>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>

                  {/* Verification Action Button */}
                  {!m.supervisorVerified && (
                    <div className="pt-1">
                      {userRole === 'asha_worker' ? (
                        <button
                          type="button"
                          onClick={() => handleVerifyPrescription(m.id)}
                          className="w-full py-2 px-3 rounded-xl bg-black hover:bg-neutral-800 text-white text-[11px] font-black cursor-pointer shadow-xs transition-colors flex items-center justify-center gap-1.5"
                        >
                          <Unlock className="w-3.5 h-3.5" />
                          <span>Verify & Authorize as Medical Supervisor (Dr. Priya Kulkarni, MO)</span>
                        </button>
                      ) : (
                        <div className="flex items-center justify-between p-2 rounded-lg bg-slate-100 border border-slate-300">
                          <span className="text-[10px] font-bold text-slate-700">
                            👤 Citizen Mode: Prescription sent to your PHC doctor for digital sign-off
                          </span>
                          <button
                            type="button"
                            onClick={() => handleVerifyPrescription(m.id)}
                            className="px-2 py-1 rounded bg-black text-white text-[9px] font-black cursor-pointer"
                          >
                            Demo Sign-Off
                          </button>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              )}

              {/* Tags */}
              {m.tags && (
                <div className="flex flex-wrap gap-1.5 pt-1">
                  {m.tags.map(t => (
                    <span key={t} className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-800 border border-slate-300 text-[9px] font-bold">
                      {t}
                    </span>
                  ))}
                </div>
              )}
            </div>
          </div>
        ))}

        {isTyping && (
          <div className="p-3 rounded-xl bg-white border border-slate-200 text-slate-900 text-xs font-bold inline-flex items-center gap-2 shadow-xs">
            <span className="animate-spin">🌀</span>
            <span>Sehat Setu is preparing direct precautions & checking verification rules...</span>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Quick Suggestion Chips */}
      <div className="flex gap-1.5 overflow-x-auto pb-1 select-none scrollbar-none">
        {quickChips.map((chip, idx) => (
          <button
            key={idx}
            onClick={() => handleSend(chip.query)}
            className="px-3 py-1.5 rounded-lg bg-white border border-slate-200 hover:border-black text-slate-700 hover:text-black text-[11px] font-bold whitespace-nowrap cursor-pointer transition-colors shadow-xs"
          >
            {chip.label}
          </button>
        ))}
      </div>

      {/* Input Row */}
      <div className="flex items-center space-x-2 pt-1 pb-1">
        <input
          type="text"
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSend()}
          placeholder="Ask Sehat Setu for health precautions & care..."
          className="flex-1 py-2.5 px-3.5 rounded-xl bg-white border border-slate-300 text-xs text-[#0F172A] placeholder-slate-400 font-medium focus:outline-none focus:border-black shadow-xs"
        />
        <button
          onClick={() => handleSend()}
          className="p-2.5 rounded-xl bg-black hover:bg-neutral-800 text-white cursor-pointer transition-all shadow-xs shrink-0"
        >
          <Send className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
