# Project Summary — Habit Architect

## What This Is

Habit Architect is an Android app that helps people build good habits and break bad ones using behavioral science principles. Unlike simple habit trackers, it guides users through thoughtful habit setup with Socratic questioning and provides intervention tools (like a "I'm Tempted" widget) for critical moments.

## How It Works (Simple)

1. **User signs in** with Google or email
2. **Creates a habit** by answering guided questions (Socratic flow)
3. **System generates** a personalized "resistance list" (reasons NOT to do the bad habit) or "attraction list" (reasons TO do the good habit)
4. **Daily tracking** — user marks success or failure, building a streak
5. **When tempted** — user taps the home screen widget to see their personal "why not" list
6. **Notifications** remind users morning and evening to stay on track

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     HABIT ARCHITECT                          │
│                   (Offline-First Android)                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              PRESENTATION LAYER                       │   │
│  │  ┌────────────────┐   ┌────────────────────────────┐ │   │
│  │  │ Jetpack        │   │ Glance Widgets             │ │   │
│  │  │ Compose UI     │   │ (Home Screen)              │ │   │
│  │  └───────┬────────┘   └───────────┬────────────────┘ │   │
│  │          │                        │                   │   │
│  │  ┌───────┴────────────────────────┴────────────────┐ │   │
│  │  │              ViewModels (MVVM)                  │ │   │
│  │  │         State holders, UI logic                 │ │   │
│  │  └───────────────────┬─────────────────────────────┘ │   │
│  └──────────────────────┼───────────────────────────────┘   │
│                         │                                    │
│  ┌──────────────────────┼───────────────────────────────┐   │
│  │              DOMAIN LAYER                             │   │
│  │  ┌───────────────────┴─────────────────────────────┐ │   │
│  │  │                  Use Cases                       │ │   │
│  │  │  CreateHabit, MarkSuccess, GetHabits, etc.      │ │   │
│  │  └───────────────────┬─────────────────────────────┘ │   │
│  │  ┌───────────────────┴─────────────────────────────┐ │   │
│  │  │           Repository Interfaces                  │ │   │
│  │  └───────────────────┬─────────────────────────────┘ │   │
│  └──────────────────────┼───────────────────────────────┘   │
│                         │                                    │
│  ┌──────────────────────┼───────────────────────────────┐   │
│  │              DATA LAYER                               │   │
│  │  ┌───────────────────┴─────────────────────────────┐ │   │
│  │  │         Repository Implementations               │ │   │
│  │  └─────────┬─────────────────────┬─────────────────┘ │   │
│  │  ┌─────────┴─────────┐ ┌─────────┴─────────────────┐ │   │
│  │  │   Room Database   │ │     DataStore             │ │   │
│  │  │   (Habits, Logs,  │ │   (Preferences)           │ │   │
│  │  │    Users, Lists)  │ │                           │ │   │
│  │  └───────────────────┘ └───────────────────────────┘ │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              SERVICES LAYER                           │   │
│  │  ┌───────────────────┐ ┌───────────────────────────┐ │   │
│  │  │   WorkManager     │ │    Firebase Auth          │ │   │
│  │  │  (Notifications)  │ │   (Google Sign-In)        │ │   │
│  │  └───────────────────┘ └───────────────────────────┘ │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## Key Components

### Room Database
- **What it does:** Stores all user data locally (habits, daily logs, resistance lists)
- **Key files:** `HabitArchitectDatabase.kt`, `HabitDao.kt`, `DailyLogDao.kt`
- **Talks to:** Repository implementations

### Repositories
- **What it does:** Provides clean data access API, abstracts storage details
- **Key files:** `HabitRepository.kt`, `UserRepository.kt`, `ListItemRepository.kt`
- **Talks to:** DAOs, Use Cases

### Use Cases
- **What it does:** Encapsulates business logic (create habit, mark success, calculate streak)
- **Key files:** `CreateHabitUseCase.kt`, `MarkHabitSuccessUseCase.kt`, `GetHabitsUseCase.kt`
- **Talks to:** Repositories, ViewModels

### ViewModels
- **What it does:** Manages UI state, handles user actions
- **Key files:** `HomeViewModel.kt`, `AddHabitViewModel.kt`, `HabitDetailViewModel.kt`
- **Talks to:** Use Cases, Compose UI

### Navigation
- **What it does:** Handles screen transitions and deep links
- **Key files:** `NavGraph.kt`, `Screen.kt` (sealed class)
- **Talks to:** All screens

### Glance Widget
- **What it does:** Home screen widget with "I'm Tempted" button
- **Key files:** `HabitWidget.kt`, `TemptationActivity.kt`
- **Talks to:** Repository (to get habit data)

### WorkManager Workers
- **What it does:** Schedules and delivers notifications
- **Key files:** `MorningReminderWorker.kt`, `EveningCheckinWorker.kt`
- **Talks to:** NotificationHelper, Repository

## Data Flow

### Creating a Habit
```
User taps "Add Habit"
    → AddHabitScreen shows Socratic questions
    → User answers all questions
    → AddHabitViewModel calls CreateHabitUseCase
    → CreateHabitUseCase calls HabitRepository.createHabit()
    → HabitRepositoryImpl inserts into Room database
    → Home screen refreshes via Flow
```

### Marking Success
```
User taps "Mark Success" on habit card
    → HomeViewModel calls MarkHabitSuccessUseCase
    → Use case calls HabitRepository.markSuccess()
    → Repository creates DailyLog and increments streak
    → UI shows celebration animation
    → Widget updates via GlanceStateDefinition
```

### Temptation Intervention
```
User taps "I'm Tempted" on widget
    → TemptationActivity launches as overlay
    → Fetches resistance list from Repository
    → Displays personalized "why not" list
    → User taps "I'll Stay Strong" → closes overlay
    → User taps "I Failed" → marks failure, resets streak
```

## Technology Choices

| Layer | Technology | Why |
|-------|------------|-----|
| Language | Kotlin 1.9+ | Modern Android standard, null safety |
| UI | Jetpack Compose | Declarative, less boilerplate, official |
| Architecture | MVVM + Clean | Separation of concerns, testable |
| Database | Room | Offline-first, type-safe SQL |
| DI | Hilt | Official Android DI, less boilerplate |
| Navigation | Navigation Compose | Type-safe, deep link support |
| Background | WorkManager | Reliable notifications, battery-friendly |
| Widgets | Glance | Compose-based, modern API |
| Auth | Firebase Auth | Secure, no password storage needed |

## File Structure

```
app/src/main/java/com/habitarchitect/
├── HabitArchitectApp.kt          # Application class (Hilt)
├── MainActivity.kt               # Single activity, hosts Compose
│
├── di/                           # Dependency Injection
│   ├── AppModule.kt              # Application-scoped deps
│   ├── DatabaseModule.kt         # Room database provider
│   └── RepositoryModule.kt       # Repository bindings
│
├── data/                         # Data Layer
│   ├── local/
│   │   ├── database/
│   │   │   ├── HabitArchitectDatabase.kt
│   │   │   ├── dao/              # UserDao, HabitDao, etc.
│   │   │   └── entity/           # UserEntity, HabitEntity, etc.
│   │   └── datastore/
│   │       └── UserPreferences.kt
│   ├── repository/               # Implementations
│   └── mapper/                   # Entity ↔ Domain mappers
│
├── domain/                       # Domain Layer
│   ├── model/                    # Habit, User, DailyLog, etc.
│   ├── repository/               # Interfaces
│   └── usecase/                  # Business logic
│
├── presentation/                 # Presentation Layer
│   ├── navigation/               # NavGraph, Screen sealed class
│   ├── theme/                    # Material 3 theme
│   ├── components/               # Reusable composables
│   ├── screen/                   # Feature screens
│   │   ├── splash/
│   │   ├── onboarding/
│   │   ├── auth/
│   │   ├── home/
│   │   ├── habitdetail/
│   │   ├── addhabit/
│   │   ├── templates/
│   │   └── settings/
│   └── widget/                   # Glance widgets
│
└── service/                      # Services Layer
    ├── notification/             # WorkManager workers
    └── auth/                     # Firebase Auth wrapper
```

## Getting Started

### Prerequisites
1. Android Studio Hedgehog or newer
2. JDK 17
3. Firebase project with Authentication enabled

### Setup
1. Clone the repository
2. Open in Android Studio
3. Add `google-services.json` to `app/` directory
4. Sync Gradle
5. Run on emulator or device

### Building
```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease

# Run tests
./gradlew test
```

## Common Tasks

### Add a new screen
1. Create sealed class entry in `Screen.kt`
2. Add composable in `screen/` directory
3. Create ViewModel if needed
4. Add to NavGraph

### Add a new database entity
1. Create entity in `data/local/database/entity/`
2. Create DAO in `data/local/database/dao/`
3. Add DAO to Database class
4. Create domain model in `domain/model/`
5. Create mapper in `data/mapper/`
6. Update repository interface and implementation

### Add a new habit template
1. Add to `HabitTemplates.breakTemplates` or `HabitTemplates.buildTemplates`
2. Include all default fields (resistance items, friction strategies, etc.)

### Debug notifications
1. Use `adb shell dumpsys jobscheduler` to check WorkManager jobs
2. Check logcat filter: `tag:WM-`
3. Test with short intervals first, then adjust to daily

---

## Key Contacts

**Project Owner:** [Your Name]  
**Primary Technology:** Android (Kotlin)  
**Target Launch:** Q1 2025 (Pilot)
