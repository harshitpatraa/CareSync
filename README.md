# CareSync 🏥

CareSync is an offline-first Android healthcare application built with Kotlin, Jetpack Compose, Room, Hilt, and Gemini AI. It empowers healthcare workers and patients with offline-first synchronization, clinical triage, symptom analysis, emergency SOS response, and secure health data management.

## ✨ Key Features

- **Offline-First Synchronization**: Built-in SQLite/Room local database with a robust sync engine and conflict handling when connectivity is restored.
- **AI Clinical Assistant & Symptom Checker**: Powered by Gemini AI for rapid clinical triage, conversational health guidance, and diagnostic support.
- **Patient Management**: Complete record keeping, maternal/child care tracking, vitals history, and visit logs.
- **Emergency SOS Response**: Rapid distress dispatch with offline caching and emergency workflows.
- **Health Records & Analytics**: Track vitals, prescriptions, lab results, and patient health progress.
- **Modern Jetpack Compose UI**: Accessible, responsive Material 3 design system with dark and light theme support.

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose with Material 3
- **Dependency Injection**: Dagger Hilt
- **Local Persistence**: Room Database (SQLite)
- **AI Integration**: Google Gemini AI & Firebase AI Logic
- **Architecture**: MVVM + Clean Architecture (UI -> ViewModel -> Repository -> Local/Remote Data Sources)
- **Networking & Serialization**: Kotlinx Serialization

## 🚀 Getting Started

### Prerequisites

- [Android Studio Ladybug or newer](https://developer.android.com/studio)
- JDK 17 or higher
- Android SDK (targetSdk 36, minSdk 24)

### Run Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/harshitpatraa/CareSync.git
   cd CareSync
   ```
2. Open the project in **Android Studio**.
3. Configure environment settings if needed (see `.env.example`).
4. Allow Gradle to sync dependencies and build the project.
5. In `app/build.gradle.kts`, ensure signing configs match your local debug setup (or remove `signingConfig = signingConfigs.getByName("debugConfig")` if needed).
6. Run the app on an Android emulator (API 24+) or physical Android device.

## 📄 License

This project is open-source.
