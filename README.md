# CareSync · Sehat Setu (सेहत सेतु)
### Offline-First Community Healthcare & Clinical Intelligence Platform

[![React](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-8.3-646CFF.svg)](https://vitejs.dev/)
[![Tailwind CSS](https://img.shields.io/badge/TailwindCSS-v4-38B2AC.svg)](https://tailwindcss.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**CareSync** is a mission-critical, offline-first digital healthcare system designed specifically for **ASHA (Accredited Social Health Activist) workers**, Community Health Officers (CHOs), and field clinicians operating in low-connectivity rural and semi-urban environments across India. Built strictly in compliance with **MoHFW (Ministry of Health and Family Welfare)** and **National Health Mission (NHM)** clinical guidelines.

Presented inside a pixel-perfect **Interactive Android Phone Simulator** for intuitive desktop demonstrations and instant cloud web deployments.

---

## 🌟 Key Features & Clinical Modules

### 1. 🏥 Clinician Sign-In & 1-Tap Quick Demo Access
- Secure local authentication tailored for field clinicians (**Dr. Priya Kulkarni**).
- **"⚡ 1-Tap Quick Demo Access"** allowing instantaneous login with zero cloud latency.
- CareSync Local Vault with offline cryptographic encryption.

### 2. 📊 Field Hub Dashboard (Home)
- **Real-Time Clinical Status**: Live synchronization status indicator with 1-click **"Sync Now"**.
- **Quick Action Bar**: Add Patient, Symptom Checker, AI Chat, and Risk Assessment.
- **Clinical Care Overview**: Real-time metrics for Today's Patients, Pending Visits, Emergency Cases, Follow-Ups, Pregnancy Tracking (ANC), and Child Monitoring (HBNC).
- **Surveillance & Ward Radar**: Cohort immunization coverage tracking (85%), Ward Outbreak & Trend Radar, and live clinical activity ledgers.

### 3. 🤖 Sehat Setu AI Clinical Assistant (सेहत सेतु)
- Multilingual clinical intelligence engine (English, Hindi, Hinglish).
- Standardized protocols covering:
  - Antenatal & Postnatal Care (ANC / PNC) danger signs.
  - Home-Based Newborn Care (HBNC) & Universal Immunization Programme (UIP).
  - High Fever & Dengue watch protocols.
  - Hypertension & Diabetes management.
- Dynamic quick-prompt chips, real-time query responses, and audio speech synthesis.

### 4. 🚨 National Emergency 108 SOS Dispatch
- Dedicated **CareSync Emergency Dispatch** console.
- **Large Central SOS Beacon**: 1-tap emergency broadcast that transmits patient GPS coordinates (`GPS: 28.7387, 77.1213`) to nearest on-duty doctor and 108 ambulance dispatch.
- **Offline SMS Fallback**: "SMS SOS (No Data)" protocol for zero-network situations.
- **Clinical Snapshot Sharing**: 1-click "Share Brief" to copy vital triage notes to clipboard.
- **Critical Patient Tagging**: Direct monitoring for high-risk patients (e.g. Harish Verma, Sunita Rao).
- **Pre-Configured Emergency Contacts**: One-tap dialing for National Ambulance (108), PHC Rampur Medical Officer, and District Trauma Hub.

### 5. 🩺 12-Question Community Disease Risk Assessment & Triage
- Evidence-based screening scoring for cardiovascular risk, vector-borne infections (Dengue/Malaria), diabetes, hypertension, and obstetric emergencies.
- **1-Tap Diagnostic Presets**: Febrile/Dengue, Cardiac Urgency, ANC Danger Signs, and Diabetes.
- **Numeric Vitals Triage**: Automated clinical alerts when BP exceeds 140/90 mmHg or SpO2 drops below 94%.

### 6. 👤 Patient Profile & Vitals Tracker (Harish Verma · PT-1003)
- Comprehensive electronic medical record with audio narration.
- **Vitals Trend Line Chart**: Dual trend visualizer for Systolic & Diastolic BP with highlighted clinical alert line (`140 mmHg High Sys`).
- Actions: One-tap Call, Unflag, Risk AI, QR Health Card generation, Referral PDF export, and vital logging.

### 7. 🔔 Reminders & Notifications
- Categorized alerts with filter chips: `All (6)`, `Medicine`, `Vaccination`, `Follow-up`.
- Instant escalation alerts (e.g. Critical SpO2 Alert, Diabetic Ulcer Review, MMR Dose 1 due).

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend Framework** | React 19, TypeScript |
| **Bundler & Tooling** | Vite 8.3, ESBuild |
| **Styling & UI** | Tailwind CSS v4, Lucide Icons |
| **State & Persistence** | React Context API, LocalStorage Room DB Simulator |
| **Clinical Intelligence** | Sehat Setu Hybrid Rule & Protocol Engine (MoHFW Guidelines) |
| **Design System** | Obsidian Navy `#0B0F19`, Emerald `#22C55E`, Radiant Violet `#8B5CF6`, Crimson `#F43F5E` |

---

## 🚀 Getting Started

### Prerequisites
- [Node.js](https://nodejs.org/) (version 18 or higher)
- npm or yarn

### Installation & Local Run
```bash
# Clone the repository
git clone https://github.com/harshitpatraa/CareSync.git

# Navigate to project directory
cd CareSync

# Install dependencies
npm install

# Start local development server
npm run dev
```

Open **`http://localhost:5173`** in your browser to view the interactive Android simulator.

### Production Build
```bash
npm run build
```
The compiled, production-ready static assets will be in the `dist/` directory.

---

## 🌐 Instant Cloud Deployment

This web application can be deployed with zero configuration on:
- **Vercel**: Import repository and deploy (`Build Command: npm run build`, `Output Directory: dist`).
- **Netlify**: Connect GitHub repo, set publish directory to `dist`.
- **Render / Firebase Hosting**: Upload static build from `dist/`.

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
