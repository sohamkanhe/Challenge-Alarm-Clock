# Alarm Clock with Challenges

A robust Android alarm clock application built with modern technologies. It ensures you wake up by requiring you to solve a mental challenge before the alarm can be dismissed.

## 🚀 Features

- **Smart Scheduling**: Leverages Android's `AlarmManager` for precise alarm triggering, including support for exact alarms on modern Android versions.
- **Wake-up Challenges**: Choose from three different challenges to ensure you're fully awake:
  - **Math Puzzle**: Solve randomized arithmetic problems.
  - **Memory Match**: A classic card-matching game to test your short-term memory.
  - **Sequence Recall**: Repeat a flashing sequence of tiles to prove your alertness.
- **Lock Screen Overlay**: Alarms appear instantly even when the device is locked, using full-screen intents.
- **Anti-Snooze Design**: The back button is disabled during challenges to prevent accidental or intentional bypass.
- **Material 3 UI**: A clean, modern interface built entirely with Jetpack Compose.

## 🛠️ Technology Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Design System**: [Material Design 3](https://m3.material.io/)
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14.0)
- **Build System**: Gradle Kotlin DSL (KTS)

## 📱 Key Android Components & Functions

This project demonstrates the use of several core Android framework components:

- **`AlarmManager`**: Used via `AlarmScheduler` to set exact alarms that wake the device.
- **`BroadcastReceiver`**: `AlarmReceiver` listens for alarm events and initiates the wake-up flow.
- **`NotificationManager` & `NotificationChannel`**: Creates high-priority notification channels for alarm alerts.
- **`FullScreenIntent`**: Utilized to launch the challenge activity directly from the lock screen.
- **`Intent` & `PendingIntent`**: Orchestrates navigation between `MainActivity`, `ChallengeActivity`, and the system alarm service.
- **`KeyguardManager`**: Manages dismissing the keyguard so the user can interact with the challenge immediately.
- **`WindowManager.LayoutParams`**: Configures flags like `FLAG_KEEP_SCREEN_ON` and `FLAG_SHOW_WHEN_LOCKED`.
- **`ActivityResultContracts`**: Modern way to request runtime permissions, specifically for `POST_NOTIFICATIONS`.
- **`TimePickerDialog`**: Standard Android dialog for intuitive time selection.

## 📂 Project Structure

- `com.example.alarm_clock.alarm`: Core logic for scheduling (`AlarmScheduler`), receiving (`AlarmReceiver`), and ringtone playback (`RingtonePlayer`).
- `com.example.alarm_clock.challenge`: Contains `ChallengeActivity` and the various UI challenge implementations (Math, Memory, Sequence).
- `MainActivity.kt`: The main entry point where users can configure and schedule their alarms.

## ⚙️ Setup

1. Clone the repository.
2. Open the project in **Android Studio (Hedgehog or newer)**.
3. Sync Gradle and run the app on an emulator or physical device.
4. Ensure you grant **Notification** and **Exact Alarm** permissions when prompted for the best experience.
