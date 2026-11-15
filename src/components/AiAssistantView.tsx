import React, { useState, useRef, useEffect } from 'react';
import { 
  Menu, 
  Bell, 
  Send, 
  Bot,
  ShieldCheck
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { ChatMessage } from '../types';
import { queryMedicalAi } from '../services/medicalAiService';

interface AiAssistantViewProps {
  onBack?: () => void;
  onOpenDrawer?: () => void;
}

export const AiAssistantView: React.FC<AiAssistantViewProps> = ({ onBack, onOpenDrawer }) => {
  const { language } = useApp();
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: "msg-welcome",
      isUser: false,
      text: "Namaste! I am Sehat Setu, your dedicated offline ASHA clinical and maternal-child health assistant based on official MoHFW & National Health Mission guidelines. Ask me any question in English, Hindi, or Hinglish regarding pregnancy (ANC/PNC), newborn care (HBNC), universal immunization, nutrition, anemia, family planning, or emergency danger signs.",
      timestamp: "Sehat Setu",
      tags: ["Sehat Setu", "MoHFW Protocol"]
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
        tags: response.tags
      };
      setMessages(prev => [...prev, botMsg]);
      setIsTyping(false);
    }, 450);
  };

  const quickChips = [
    { label: "Fever & dengue watch protocol", query: "Fever & dengue watch protocol" },
    { label: "ANC high BP warning signs", query: "ANC high BP warning signs" },
    { label: "Type 2 diabetes meal plan", query: "Type 2 diabetes meal plan" }
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
              <span className="text-[8px] font-black px-1.5 py-0.2 rounded-xs bg-blue-100 text-[#1E40AF] border border-blue-300 uppercase">
                MoHFW
              </span>
            </div>
            <p className="text-[9px] font-bold text-slate-600 leading-none">
              Clinical Decision Support System (CDSS) · Govt of India
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

      {/* Official MoHFW Protocol Badge Banner */}
      <div className="px-3 py-1.5 rounded-xl bg-blue-50 border border-blue-200 flex items-center gap-2 text-[10px] text-[#1E40AF] font-bold shadow-xs">
        <Bot className="w-3.5 h-3.5 text-[#1E40AF]" />
        <span>Sehat Setu · Verified against Official MoHFW & National Health Mission Standard 2026</span>
      </div>

      {/* Chat Messages Flow */}
      <div className="flex-1 overflow-y-auto space-y-2.5 pr-1 text-xs">
        {messages.map((m) => (
          <div key={m.id} className="space-y-1">
            {/* Message Box */}
            <div className={`p-3.5 rounded-2xl text-xs leading-relaxed space-y-2 shadow-xs ${
              m.isUser
                ? 'bg-[#1E40AF] text-white ml-auto max-w-[85%] rounded-tr-none font-medium'
                : 'bg-white text-slate-800 border border-slate-200 max-w-[95%] rounded-tl-none font-medium'
            }`}>
              {!m.isUser && (
                <div className="flex items-center justify-between text-[#1E40AF] font-extrabold text-xs pb-1 border-b border-slate-100">
                  <span className="flex items-center gap-1.5">
                    <Bot className="w-3.5 h-3.5" />
                    Sehat Setu
                  </span>
                </div>
              )}
              <p className={m.isUser ? 'text-white font-medium leading-relaxed' : 'text-slate-800 font-medium leading-relaxed'}>
                {m.text}
              </p>

              {/* Tags */}
              {m.tags && (
                <div className="flex flex-wrap gap-1.5 pt-1">
                  {m.tags.map(t => (
                    <span key={t} className="px-2 py-0.5 rounded-full bg-blue-50 text-[#1E40AF] border border-blue-200 text-[9px] font-bold">
                      {t}
                    </span>
                  ))}
                </div>
              )}
            </div>
          </div>
        ))}

        {isTyping && (
          <div className="p-3 rounded-xl bg-white border border-slate-200 text-[#1E40AF] text-xs font-bold inline-flex items-center gap-2 shadow-xs">
            <span className="animate-spin">🌀</span>
            <span>Sehat Setu is analyzing clinical protocols...</span>
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
            className="px-3 py-1.5 rounded-lg bg-white border border-slate-200 hover:border-[#1E40AF] text-slate-700 hover:text-slate-900 text-[11px] font-bold whitespace-nowrap cursor-pointer transition-colors shadow-xs"
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
          placeholder="Ask Sehat Setu clinical assistant..."
          className="flex-1 py-2.5 px-3.5 rounded-xl bg-white border border-slate-300 text-xs text-[#0F172A] placeholder-slate-400 font-medium focus:outline-none focus:border-[#1E40AF] shadow-xs"
        />
        <button
          onClick={() => handleSend()}
          className="p-2.5 rounded-xl bg-[#1E40AF] hover:bg-[#1E3A8A] text-white cursor-pointer transition-all shadow-xs shrink-0"
        >
          <Send className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
