# CareSync · Sehat Setu for ASHA Workers

### AI-Powered Community Healthcare & Clinical Intelligence Platform

![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178C6?logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-8.3-646CFF?logo=vite&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-v4-38B2AC?logo=tailwindcss&logoColor=white)
![AI](https://img.shields.io/badge/AI-Google_Gemini-4285F4?logo=google&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green.svg)

## Overview

CareSync is an AI-powered healthcare platform designed for ASHA workers, Community Health Officers (CHOs), and frontline healthcare professionals across India.

Powered by the Google Gemini API, it combines intelligent clinical assistance, patient management, health risk assessment, and emergency coordination in one platform.

Its hybrid architecture combines cloud-based AI intelligence with locally stored patient data, allowing access to supported features even without internet connectivity.

## Key Features

### 1. Clinician Dashboard
- Quick demo login and clinician access.
- Patient and follow-up overview.
- ANC and HBNC monitoring.
- Immunization and community health indicators.
- Quick access to essential healthcare modules.

### 2. Sehat Setu AI Assistant
- Google Gemini API-powered conversational AI.
- English, Hindi, and Hinglish support.
- AI-assisted symptom understanding.
- Clinical guidance covering ANC, PNC, HBNC, Dengue, Malaria, Diabetes, and Hypertension.
- Contextual responses and audio assistance.

### 3. Emergency SOS – 108
- Emergency coordination dashboard.
- Patient location and clinical information.
- Emergency contact shortcuts.
- SMS SOS fallback interface.
- Clinical snapshot sharing.

### 4. Community Risk Assessment
- 12-question health risk assessment.
- Cardiovascular, diabetes, hypertension, and infectious disease screening.
- ANC danger sign assessment.
- Vitals-based alerts for elevated BP and low SpO2.

### 5. Patient Management
- Digital patient profiles and medical history.
- Vitals tracking and trend charts.
- Patient risk indicators.
- QR Health Card generation.
- Referral PDF export and follow-up management.

### 6. Reminders & Notifications
- Medicine reminders.
- Vaccination alerts.
- Patient follow-ups.
- Critical health notifications.

## AI & Connectivity Architecture

CareSync uses a hybrid architecture:

- Local Layer: Stores supported patient records, vitals, and application data for offline access.
- AI Layer: Uses Google Gemini API for intelligent conversations, multilingual assistance, and clinical information.
- Internet Dependency: Gemini-powered features require an active internet connection.

CareSync does not provide completely offline AI functionality. Offline access is limited to locally implemented features.

## Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | React 19, TypeScript |
| Build Tool | Vite 8.3 |
| Styling | Tailwind CSS v4 |
| State Management | React Context API |
| Local Storage | LocalStorage |
| AI Engine | Google Gemini API |
| Clinical Intelligence | Gemini AI + Rule-Based Assessment |
| Deployment | Vercel / Netlify |

## Getting Started

### Prerequisites
- Node.js
- npm
- Git

### Installation

```bash
git clone https://github.com/harshitpatraa/CareSync.git
cd CareSync
npm install
```

### Gemini API Configuration

Get your API key from [Google AI Studio](https://aistudio.google.com/).

Configure the environment variable expected by the project's Gemini integration in `.env.local`:

```env
VITE_GEMINI_API_KEY=your_gemini_api_key
```

Note: Vite exposes `VITE_` variables in frontend code. For production, use a secure backend or serverless function to protect API credentials.

### Run Locally

```bash
npm run dev
```

Open `http://localhost:5173` in your browser.

### Production Build

```bash
npm run build
```

The production files are generated in the `dist/` directory.

## Deployment

CareSync supports deployment through:

- Vercel
- Netlify
- Render
- Firebase Hosting

Build command: `npm run build`

Output directory: `dist`

A separate backend deployment may be required for secure Gemini API integration.

## Privacy & Clinical Safety

- Protect patient information through appropriate access controls.
- Secure API credentials using server-side environment variables.
- Minimize sensitive data shared with external AI services.
- Treat AI responses as decision-support information, not medical diagnoses.
- Refer critical cases to qualified medical professionals.

## Future Scope

- Encrypted patient database.
- Secure cloud synchronization.
- Advanced health analytics.
- Voice-based data entry.
- Regional language expansion.
- OCR-based medical document extraction.
- Government healthcare system integration.

## Team Innovix

| Team Member |
|---|
| Gulshan Gautam |
| Harshit Patra |

## License

# CareSync · Sehat Setu for ASHA Workers

### AI-Powered Community Healthcare & Clinical Intelligence Platform

![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178C6?logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-8.3-646CFF?logo=vite&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-v4-38B2AC?logo=tailwindcss&logoColor=white)
![AI](https://img.shields.io/badge/AI-Google_Gemini-4285F4?logo=google&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green.svg)

## Overview

CareSync is an AI-powered healthcare platform designed for ASHA workers, Community Health Officers (CHOs), and frontline healthcare professionals across India.

Powered by the Google Gemini API, it combines intelligent clinical assistance, patient management, health risk assessment, and emergency coordination in one platform.

Its hybrid architecture combines cloud-based AI intelligence with locally stored patient data, allowing access to supported features even without internet connectivity.

## Key Features

### 1. Clinician Dashboard
- Quick demo login and clinician access.
- Patient and follow-up overview.
- ANC and HBNC monitoring.
- Immunization and community health indicators.
- Quick access to essential healthcare modules.

### 2. Sehat Setu AI Assistant
- Google Gemini API-powered conversational AI.
- English, Hindi, and Hinglish support.
- AI-assisted symptom understanding.
- Clinical guidance covering ANC, PNC, HBNC, Dengue, Malaria, Diabetes, and Hypertension.
- Contextual responses and audio assistance.

### 3. Emergency SOS – 108
- Emergency coordination dashboard.
- Patient location and clinical information.
- Emergency contact shortcuts.
- SMS SOS fallback interface.
- Clinical snapshot sharing.

### 4. Community Risk Assessment
- 12-question health risk assessment.
- Cardiovascular, diabetes, hypertension, and infectious disease screening.
- ANC danger sign assessment.
- Vitals-based alerts for elevated BP and low SpO2.

### 5. Patient Management
- Digital patient profiles and medical history.
- Vitals tracking and trend charts.
- Patient risk indicators.
- QR Health Card generation.
- Referral PDF export and follow-up management.

### 6. Reminders & Notifications
- Medicine reminders.
- Vaccination alerts.
- Patient follow-ups.
- Critical health notifications.

## AI & Connectivity Architecture

CareSync uses a hybrid architecture:

- Local Layer: Stores supported patient records, vitals, and application data for offline access.
- AI Layer: Uses Google Gemini API for intelligent conversations, multilingual assistance, and clinical information.
- Internet Dependency: Gemini-powered features require an active internet connection.

CareSync does not provide completely offline AI functionality. Offline access is limited to locally implemented features.

## Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | React 19, TypeScript |
| Build Tool | Vite 8.3 |
| Styling | Tailwind CSS v4 |
| State Management | React Context API |
| Local Storage | LocalStorage |
| AI Engine | Google Gemini API |
| Clinical Intelligence | Gemini AI + Rule-Based Assessment |
| Deployment | Vercel / Netlify |

## Getting Started

### Prerequisites
- Node.js
- npm
- Git

### Installation

```bash
git clone https://github.com/harshitpatraa/CareSync.git
cd CareSync
npm install
```

### Gemini API Configuration

Get your API key from [Google AI Studio](https://aistudio.google.com/).

Configure the environment variable expected by the project's Gemini integration in `.env.local`:

```env
VITE_GEMINI_API_KEY=your_gemini_api_key
```

Note: Vite exposes `VITE_` variables in frontend code. For production, use a secure backend or serverless function to protect API credentials.

### Run Locally

```bash
npm run dev
```

Open `http://localhost:5173` in your browser.

### Production Build

```bash
npm run build
```

The production files are generated in the `dist/` directory.

## Deployment

CareSync supports deployment through:

- Vercel
- Netlify
- Render
- Firebase Hosting

Build command: `npm run build`

Output directory: `dist`

A separate backend deployment may be required for secure Gemini API integration.

## Privacy & Clinical Safety

- Protect patient information through appropriate access controls.
- Secure API credentials using server-side environment variables.
- Minimize sensitive data shared with external AI services.
- Treat AI responses as decision-support information, not medical diagnoses.
- Refer critical cases to qualified medical professionals.

## Future Scope

- Encrypted patient database.
- Secure cloud synchronization.
- Advanced health analytics.
- Voice-based data entry.
- Regional language expansion.
- OCR-based medical document extraction.
- Government healthcare system integration.

## Team Innovix

| Team Member |
|---|
| Gulshan Gautam |
| Harshit Patra |

## License

MIT License

Copyright (c) 2026 Gulshan Gautam and Harshit Patra

Permission is hereby granted, free of charge, to use, modify, and distribute this software, provided the copyright notice is retained.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
