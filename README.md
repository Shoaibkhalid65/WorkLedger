<div align="center">

# ProgressTracker

**A modern Android productivity app for tracking daily tasks, work durations, and long-term goals — with an analytics dashboard built on top.**

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-757575?style=flat-square)](https://m3.material.io)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

</div>

---

## Video Demo

> 📹 *[Click here to watch the full app demo](#)*  


https://github.com/user-attachments/assets/80b39af7-7174-4934-bd32-2e41cc0507bf



---

## Overview

ProgressTracker helps you answer three questions every day:

- **What did I work on?** — Log daily tasks with title, description, satisfaction rating, and remarks.
- **How long did I work?** — Track live work sessions via a persistent foreground service with notification controls, or log manual durations.
- **Am I making progress?** — View weekly bar charts, 30-day satisfaction trends, and today's summary on an analytics dashboard.

Goals are tracked separately with a priority matrix (urgency × importance × difficulty) and an expected completion date, giving you a structured way to plan longer-horizon work alongside day-to-day activity.

---

## Features

### Daily Task Tracking
- Create tasks with title, description, and remarks
- Rate daily satisfaction from 0 – 100% per task
- Dual calendar display: Gregorian and Islamic (Hijri) dates
- Filter by Today / This Week / This Month / This Year / All
- Sort by date, work time, or satisfaction (ascending and descending)
- Full-text search across task titles

### Work Duration Tracking
- **Live timer** via an Android foreground service with a persistent notification
- Notification action buttons: **Discard** (cancel with no save) and **Save & Stop** (persists duration, then stops)
- Survives app kills — the service saves to the database directly if the app process is gone
- Manual duration entry: set start time and end time via time picker dialogs
- Multiple duration slots per task
- Duration history list with delete support

### Goal Management
- Create goals with title and description
- Set an expected completion date via date picker
- Classify each goal by three independent axes:
  - **Difficulty**: Easy / Medium / Hard
  - **Importance**: Average / Important / Very Important
  - **Urgency**: Not Urgent / Average / Urgent
- Toggle completion status — records completion date automatically
- Filter by All / Pending / Completed, with search and sort

### Analytics Dashboard
- **Today's Overview**: total hours, session count, average satisfaction
- **Weekly Bar Chart**: work hours per day for the past 7 days, with tap-to-select tooltips
- **30-Day Satisfaction Trend**: animated line chart with cubic Bézier smoothing and tap-to-select tooltips
- Both charts animate on load and support tapping individual data points

### Appearance Customization
- Three theme modes: System default / Light / Dark
- Five hand-tuned Material 3 color schemes: **Terracotta**, **Ocean**, **Forest**, **Violet**, **Slate**
- Dynamic Color support on Android 12+ (wallpaper-based palette)
- All preferences persisted via DataStore — survive process kills and reinstalls

---


## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI framework | Jetpack Compose + Material 3 |
| Architecture | MVVM + Repository pattern |
| Dependency injection | Dagger Hilt |
| Local database | Room (SQLite) |
| Reactive state | StateFlow / SharedFlow |
| Preferences | Jetpack DataStore |
| Background work | Android Foreground Service |
| Animations | Lottie (timer animation) |
| Navigation | Jetpack Navigation Compose |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 |

---

## Architecture

The project follows a clean, layered MVVM architecture:

```
UI Layer         →  Jetpack Compose screens + Hilt ViewModels
ViewModel Layer  →  StateFlow/SharedFlow state holders, business logic coordination
Repository Layer →  Single source of truth, abstracts DAO and DataStore access
Data Layer       →  Room DAOs, DataStore, ForegroundService + TrackingServiceManager
```

**Key architectural decisions:**

- `TrackingServiceManager` is a Hilt `@Singleton` with its own `CoroutineScope(SupervisorJob() + Dispatchers.IO)`, decoupled from ViewModel lifetime. This ensures the save operation completes even if the app process is backgrounded.
- Notification action buttons write directly to the database through the service (avoiding a dead ViewModel); in-app buttons write through the ViewModel (avoiding double-saves). The paths are strictly separated.
- Events between the service and ViewModel use a `SharedFlow(replay=0)` — stale events are never replayed to new collectors.
- An `@ApplicationScope` `CoroutineScope` is provided via Hilt for application-lifetime coroutines.

---

## Project Structure

```
app/src/main/java/com/example/progresstracker/
│
├── data/
│   ├── local/
│   │   ├── datastore/          # AppPreferencesDataStore
│   │   └── db/
│   │       ├── dao/            # DailyTaskDao, TaskDurationDao, GoalDao
│   │       ├── database/       # AppDatabase (Room), migrations
│   │       └── entity/         # DailyTaskEntity, TaskDurationEntity, GoalEntity
│   ├── mapper/                 # ModelMappingExt (entity ↔ domain model)
│   └── repository/             # DailyTaskRepository, GoalRepository, DashboardRepository
│
├── di/                         # Hilt modules: DatabaseModule, DataStoreModule, CoroutinesModule
│
├── model/                      # Domain models: DailyTask, TaskDuration, Goal, Dashboard models
│
├── navigation/                 # AppNavGraph, Screen sealed class, BottomBarDestination
│
├── service/
│   ├── TrackingForegroundService.kt
│   └── TrackingServiceManager.kt
│
├── ui/
│   ├── components/             # Shared composables (GoalBadge)
│   ├── dailyTask/
│   │   ├── DailyTaskMainScreen.kt
│   │   ├── tasksList/          # TasksListScreen, TasksListViewModel
│   │   └── taskDuration/       # TaskDurationScreen, TaskDurationViewModel
│   ├── dashboard/
│   │   ├── DashboardScreen.kt
│   │   ├── DashboardViewModel.kt
│   │   └── charts/             # BarChart.kt, LineChart.kt
│   ├── gaols/                  # GoalsListScreen, GoalsListViewModel
│   ├── goalcreation/           # CreateEditGoalScreen, CreateEditGoalViewModel
│   ├── settings/               # AppearanceSettingsScreen, AppPreferencesViewModel
│   ├── taskcreation/           # CreateEditTaskScreen, CreateEditTaskViewModel
│   └── theme/                  # AppTheme, Color.kt (5 full M3 color schemes), Type.kt
│
├── utils/                      # DateTimeUtils (formatting, Islamic calendar, epoch math)
│
├── MainActivity.kt
└── ProgressTrackerApp.kt       # @HiltAndroidApp entry point
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 35

### Clone and run

```bash
git clone https://github.com/yourusername/ProgressTracker.git
cd ProgressTracker
```

Open in Android Studio, sync Gradle, and run on a device or emulator running Android 8.0+.

### Permissions

The app requests `POST_NOTIFICATIONS` at runtime on Android 13+ (required for the foreground service tracking notification).

---

## Database Schema

```
daily_tasks
  id (PK), title, description, remarks, satisfyPercentage, englishDate

task_durations
  id (PK), dailyTaskId (FK → daily_tasks.id CASCADE DELETE),
  startTime, endTime, durationTime, dateEpoch

goals
  id (PK), createdAt, title, description, isCompleted,
  expectedCompletionDate, completionDate,
  difficultyLevel, importanceLevel, urgencyLevel
```

Current schema version: **2**. Migration from v1 → v2 adds the `dateEpoch` column to `task_durations`.

---

## Roadmap

- [ ] Data export (CSV / JSON)
- [ ] Reminders and daily check-in notifications
- [ ] Task and goal linking (attach a goal to a task)
- [ ] Widget for home screen (today's session count + hours)
- [ ] Backup and restore via Google Drive

---

## Contributing

Pull requests are welcome. For major changes please open an issue first to discuss what you would like to change.

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## License

```
MIT License

Copyright (c) 2025 [Muhammad Shoaib Khalid]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

