import React, { useState, useRef, useEffect } from 'react';
import { 
  Menu, 
  Bell, 
  Send, 
  Volume2, 
  Mic, 
  Bot, 
  ArrowLeft
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
      tags: ["Sehat Setu", "Offline MoHFW Engine"]
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
    <div className="flex flex-col h-full space-y-3">
      {/* Top Bar */}
      <div className="flex items-center justify-between py-1 border-b border-slate-800/60 pb-2">
        <button 
          onClick={onOpenDrawer || onBack}
          className="p-1.5 text-slate-300 hover:text-white cursor-pointer"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-2">
          {/* CareSync Logo icon */}
          <div className="w-6 h-6 rounded-lg bg-gradient-to-tr from-purple-500 via-indigo-500 to-cyan-400 p-[1.5px]">
            <div className="w-full h-full bg-[#0d1017] rounded-[5px] flex items-center justify-center">
              <div className="w-3 h-3 border border-cyan-400 rounded-xs rotate-45 flex items-center justify-center">
                <div className="w-1 h-1 bg-purple-400 rounded-full"></div>
              </div>
            </div>
          </div>
          <span className="font-bold text-sm text-white tracking-tight">CareSync AI Chat</span>
        </div>

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

      {/* Pill Badge: General Care */}
      <div className="flex items-center">
        <span className="px-3 py-1 rounded-xl bg-[#173022] text-[#4ade80] border border-[#22c55e]/30 text-xs font-semibold">
          General Care
        </span>
      </div>

      {/* Mini Banner: Sehat Setu MoHFW Clinical Intelligence */}
      <div className="px-3 py-1.5 rounded-xl bg-[#1e1a38] border border-purple-500/30 flex items-center gap-2 text-[10px] text-purple-300">
        <Bot className="w-3.5 h-3.5 text-purple-400" />
        <span>Sehat Setu · MoHFW Clinical Intelligence & Gemini Hybrid</span>
      </div>

      {/* Chat Messages Flow */}
      <div className="flex-1 overflow-y-auto space-y-3 pr-1 text-xs">
        {messages.map((m) => (
          <div key={m.id} className="space-y-1">
            {/* Message Box */}
            <div className={`p-4 rounded-3xl text-xs leading-relaxed space-y-2.5 ${
              m.isUser
                ? 'bg-blue-600 text-white ml-auto max-w-[85%] rounded-tr-none'
                : 'bg-[#181a2e] text-slate-200 border border-purple-500/20 max-w-[95%] rounded-tl-none'
            }`}>
              {!m.isUser && (
                <div className="flex items-center justify-between text-[#c084fc] font-bold text-xs pb-1 border-b border-purple-500/20">
                  <span className="flex items-center gap-1.5">
                    <Bot className="w-3.5 h-3.5" />
                    Sehat Setu
                  </span>
                  <Volume2 className="w-3.5 h-3.5 text-purple-400 cursor-pointer" />
                </div>
              )}

              <p className="whitespace-pre-line text-xs font-normal text-slate-200">{m.text}</p>

              {m.tags && (
                <div className="flex flex-wrap gap-1.5 pt-1">
                  {m.tags.map((t, idx) => (
                    <span key={idx} className="text-[10px] px-2.5 py-0.5 rounded-full bg-[#271f45] text-[#d8b4fe] border border-purple-500/30 font-medium">
                      {t}
                    </span>
                  ))}
                </div>
              )}
            </div>
            {!m.isUser && (
              <span className="text-[10px] text-slate-500 ml-3 block">Sehat Setu</span>
            )}
          </div>
        ))}

        {isTyping && (
          <div className="p-3.5 rounded-2xl bg-[#181a2e] border border-purple-500/20 text-xs text-purple-300 flex items-center gap-2 max-w-[85%]">
            <div className="w-2 h-2 rounded-full bg-purple-400 animate-bounce"></div>
            <div className="w-2 h-2 rounded-full bg-purple-400 animate-bounce [animation-delay:0.2s]"></div>
            <div className="w-2 h-2 rounded-full bg-purple-400 animate-bounce [animation-delay:0.4s]"></div>
            <span className="text-[10px] text-slate-400 ml-1">Sehat Setu generating clinical response...</span>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Quick Chips at Bottom */}
      <div className="flex gap-2 overflow-x-auto pb-1 shrink-0">
        {quickChips.map((chip, idx) => (
          <button
            key={idx}
            onClick={() => handleSend(chip.query)}
            className="px-3.5 py-1.5 rounded-full bg-[#121626] border border-slate-800 text-[11px] text-slate-300 whitespace-nowrap hover:border-purple-500/40 cursor-pointer transition-colors"
          >
            {chip.label}
          </button>
        ))}
      </div>

      {/* Input composer with mic and purple send button */}
      <div className="flex items-center gap-2 pt-1 shrink-0">
        <button 
          className="w-10 h-10 rounded-full bg-[#241f3d] text-[#c084fc] flex items-center justify-center shrink-0 hover:bg-[#2f2752] cursor-pointer"
          title="Voice input"
        >
          <Mic className="w-4 h-4" />
        </button>

        <div className="flex-1 relative">
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Ask Sehat Setu clinical assistant..."
            className="w-full pl-4 pr-3 py-2.5 rounded-full bg-[#121626] border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500/50"
          />
        </div>

        <button
          onClick={() => handleSend()}
          disabled={!inputText.trim() || isTyping}
          className="w-10 h-10 rounded-full bg-gradient-to-tr from-purple-600 to-indigo-500 hover:from-purple-500 hover:to-indigo-400 text-white flex items-center justify-center shrink-0 shadow-md shadow-purple-500/20 disabled:opacity-50 cursor-pointer"
        >
          <Send className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
