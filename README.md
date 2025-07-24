# ⏳ Chronos: Your Personal AI Assistant

Chronos is a modern Android application built with **Jetpack Compose**, **Firebase**, and **Hilt** for dependency injection. It enables users to manage reminders with titles, notes, dates, times, and optional images. The app integrates **Google Sign-In**, **Firebase Firestore**, **Firebase Storage**, and also provides AI-generated greetings via an external API.

---

## 📋 Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Permissions](#permissions)
- [Contributing](#contributing)
- [License](#license)

---

## ✅ Features

- 🔐 **User Authentication**: Secure login via Google Sign-In using Firebase Authentication.
- ⏰ **Reminder Management**: Create, edit, delete, and view reminders with titles, notes, dates, times, and optional images.
- 🤖 **AI Greetings**: Generate and share AI-powered messages (e.g., birthday wishes) using an external API.
- 🖼️ **Image Upload**: Capture or pick images from the gallery, stored securely in Firebase Storage.
- 🔔 **Notifications**: Schedule precise reminders using Android's AlarmManager.
- 🌗 **Theme Switching**: Toggle light and dark themes with smooth animations.
- 📡 **Network Monitoring**: Offline status is indicated with a Lottie animation.
- ✨ **Shimmer Loading**: Visually pleasant shimmer effects during data loading.
- 🧩 **Responsive UI**: Fully built using Jetpack Compose for a modern experience.

---
## ✅ Download APP
https://drive.google.com/file/d/1kfAO9HgIpuTYv6lpAscqiaCWoTLdZPkA/view?usp=sharing
## 📸 Screenshots

> _Add screenshots of major screens like Authentication, Reminder List, Create Reminder Dialog, AI Greeting, etc._

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Dependency Injection**: Hilt
- **Backend**: Firebase Authentication, Firestore, and Storage
- **Networking**: [Ktor Client](https://ktor.io/)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Animations**: [Lottie](https://airbnb.io/lottie/)
- **Notifications**: AlarmManager + NotificationCompat
- **Async Handling**: Coroutines and Flow

---

## 🏛️ Architecture

The app uses the **MVVM (Model-View-ViewModel)** pattern.

- **Model**: 
  - Data classes: `Reminder`, `UserInfo`
  - Repositories: `ReminderRepository`, `GreetingRepository`
- **View**: 
  - Jetpack Compose components like `AuthScreen`, `ReminderScreen`, `ReminderCreateDialog`, `ReminderItem`, etc.
- **ViewModel**: 
  - `ReminderViewModel` manages UI state and business logic.
- **Use Case**:
  - `GetAIGreetingUseCase` encapsulates greeting logic.
- **DI**: 
  - Hilt provides dependencies like Firebase instances and the Ktor client.
- **Notifications**:
  - `ReminderReceiver` handles exact alarms and notifications.

---

## ⚙️ Setup and Installation

### ✅ Prerequisites

- Android Studio (latest stable)
- Android SDK 31 or above
- Firebase project with:
  - Google Sign-In enabled
  - Firestore and Storage configured
- `google-services.json` file in `app/`
- Required assets:
  - **Lottie**: `hello_animation`, `ai_animation`, `no_internet` → `res/raw`
  - **Icons**: `google_icon`, `ic_dark`, `ic_light`, `logo` → `res/drawable`

---

### 🚀 Steps

1. **Clone the Repository**

```bash
git clone https://github.com/your-username/chronos.git
cd chronos
