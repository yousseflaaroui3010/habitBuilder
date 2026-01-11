# Habit Architect — Product Blueprint & PRD

**Version:** 1.0 | **Last Updated:** 2026-01-11

---

## 1. Product Vision

**One-liner:** A science-backed habit app that helps users BUILD good habits and BREAK bad ones using behavioral psychology principles from "Atomic Habits."

**Problem:** Most habit apps are simple trackers. They don't address WHY habits fail — cravings, triggers, and lack of friction/attraction systems.

**Solution:** Habit Architect uses:
- **Resistance Lists** — Reasons NOT to do bad habits (shown during temptation)
- **Attraction Lists** — Reasons TO do good habits (motivation on demand)
- **Friction Strategies** — Make bad habits harder (e.g., "delete app", "unplug TV")
- **Pause & Reflect** — 30-second cooldown with rotating flashcards when tempted
- **Trigger Detection** — ML-powered pattern recognition for failure triggers

---

## 2. Core Features

### 2.1 Habit Management

| Feature | BUILD Habits | BREAK Habits |
|---------|-------------|--------------|
| Daily Tracking | ✅ Success/Failure | ✅ Success/Failure |
| Streak Counter | ✅ | ✅ |
| Attraction List | ✅ Reasons TO do it | ❌ |
| Resistance List | ❌ | ✅ Reasons NOT to do it |
| Friction Strategies | ❌ | ✅ Make it harder |
| "I'm Tempted" Button | ❌ | ✅ Shows resistance cards |
| Trigger Logging | ❌ | ✅ After failure |

### 2.2 User Flows

```
┌─────────────────────────────────────────────────────────────┐
│                     HOME SCREEN                              │
├─────────────────────────────────────────────────────────────┤
│  Today's Focus: "Stay strong, one day at a time"            │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ 🏃 Morning Exercise          [✓] [✗]    🔥 12 days  │    │
│  │ 🚫 No Doomscrolling          [✓] [✗] [😰]  🔥 5 days │    │
│  │ 📖 Read 10 Pages             [✓] [✗]    🔥 8 days   │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                              │
│  [+] Add Habit                                               │
└─────────────────────────────────────────────────────────────┘

[😰] = "I'm Tempted" button (BREAK habits only)
```

### 2.3 "I'm Tempted" Flow (Key Differentiator)

```
User taps "I'm Tempted" on BREAK habit
           ↓
┌─────────────────────────────────┐
│      PAUSE SCREEN (30s)         │
│                                 │
│    "Remember why you started"   │
│                                 │
│  ┌───────────────────────────┐  │
│  │ 😴 "My sleep will suffer   │  │
│  │    tonight if I do this"   │  │
│  └───────────────────────────┘  │
│                                 │
│     ← [●] [○] [○] [○] →        │  ← Auto-rotating cards
│                                 │
│        ⏱️ 0:24                   │
│                                 │
│  [Stay Strong]  [I Failed]      │
└─────────────────────────────────┘
           ↓
    If "I Failed" → Trigger Dialog
           ↓
┌─────────────────────────────────┐
│   What triggered this?          │
│                                 │
│   [Bored] [Stressed] [Phone]    │
│   [Alone] [Late Night]          │
│   [Custom: ____________]        │
└─────────────────────────────────┘
           ↓
    Saved for ML pattern detection
```

### 2.4 Progress Dashboard

- **Pie Charts:** BUILD vs BREAK distribution, Success rate
- **Bar Chart:** Weekly progress (color-coded)
- **Streak Stats:** Current, longest, total success days
- **Weekly Reflection:** What went well, what didn't, lessons learned

### 2.5 Accountability Partners

```
User A generates invite code → User B joins
           ↓
Partners can see:
  ✅ Habit names
  ✅ Daily success/failure
  ✅ Streak counts

Partners CANNOT see:
  ❌ Resistance lists (private/sensitive)
  ❌ Trigger logs
  ❌ Attraction lists
```

---

## 3. Technical Architecture

### 3.1 Current Stack (Local-First)

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Screens   │  │  ViewModels │  │ Components  │         │
│  │  (Compose)  │  │   (Hilt)    │  │ (Reusable)  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
├─────────────────────────────────────────────────────────────┤
│                        DOMAIN                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Models    │  │ Repositories│  │  Use Cases  │         │
│  │ (Data Class)│  │ (Interface) │  │  (Future)   │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
├─────────────────────────────────────────────────────────────┤
│                         DATA                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │    Room     │  │   Mappers   │  │ DataStore   │         │
│  │  Database   │  │ Entity↔Model│  │ Preferences │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| DI | Hilt |
| Database | Room (SQLite) |
| Preferences | DataStore |
| Auth | Firebase Auth (Google Sign-In) |
| Async | Kotlin Coroutines + Flow |
| Notifications | AlarmManager (exact timing) |

### 3.3 File Structure

```
app/src/main/java/com/habitarchitect/
├── data/
│   ├── local/database/
│   │   ├── dao/           # HabitDao, UserDao, DailyLogDao...
│   │   ├── entity/        # HabitEntity, UserEntity...
│   │   └── HabitArchitectDatabase.kt
│   ├── mapper/            # Entity ↔ Domain mappers
│   ├── preferences/       # AppPreferences (DataStore)
│   ├── repository/        # Repository implementations
│   └── HabitTemplates.kt  # Pre-built habit templates
│
├── di/                    # Hilt modules
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── domain/
│   ├── model/             # Habit, User, DailyLog, ListItem...
│   └── repository/        # Repository interfaces
│
├── presentation/
│   ├── components/        # Reusable UI components
│   ├── navigation/        # NavGraph
│   ├── screen/            # Feature screens + ViewModels
│   │   ├── home/
│   │   ├── habit/
│   │   ├── pause/         # "I'm Tempted" screen
│   │   ├── dashboard/
│   │   └── settings/
│   ├── theme/             # Colors, Typography
│   └── widget/            # Home screen widget
│
├── service/
│   ├── notification/      # AlarmScheduler, Receivers
│   ├── auth/              # GoogleAuthService
│   ├── sound/             # SoundManager
│   └── export/            # DataExportService
│
├── HabitArchitectApp.kt   # Application class
└── MainActivity.kt        # Single activity
```

---

## 4. Database Schema

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    users     │     │    habits    │     │  daily_logs  │
├──────────────┤     ├──────────────┤     ├──────────────┤
│ id (PK)      │←────│ userId (FK)  │     │ habitId (FK) │────→
│ email        │     │ id (PK)      │←────│ date (PK)    │
│ displayName  │     │ name         │     │ status       │
│ authProvider │     │ type (B/B)   │     │ markedAt     │
│ createdAt    │     │ category     │     │ note         │
└──────────────┘     │ currentStreak│     └──────────────┘
                     │ longestStreak│
                     │ priority     │     ┌──────────────┐
                     └──────────────┘     │  list_items  │
                                          ├──────────────┤
┌──────────────┐     ┌──────────────┐     │ id (PK)      │
│ partnerships │     │weekly_reflect│     │ habitId (FK) │
├──────────────┤     ├──────────────┤     │ type (R/A/T) │
│ id (PK)      │     │ id (PK)      │     │ content      │
│ ownerId      │     │ userId       │     │ orderIndex   │
│ partnerId    │     │ weekStartDate│     └──────────────┘
│ inviteCode   │     │ wentWell     │
│ status       │     │ didntGoWell  │     R = Resistance
└──────────────┘     │ learned      │     A = Attraction
                     └──────────────┘     T = Trigger
```

---

## 5. Monetization Strategy

### 5.1 Subscription Tiers

| Feature | Basic (Free) | Plus ($2.99/mo) | CEO of Life ($7.99/mo) |
|---------|-------------|-----------------|------------------------|
| Habits | 3 | 10 | Unlimited |
| Ads | Banner + Interstitial | None | None |
| Templates | 5 | All | All + Custom |
| Partner Sharing | ❌ | 1 partner | Unlimited |
| AI Habit Builder | ❌ | ❌ | ✅ |
| Trigger Analytics | Basic | Detailed | ML Insights |
| Data Export | ❌ | CSV | CSV + PDF Report |
| Widgets | 1 | 3 | Unlimited |
| Themes | Light/Dark | + 5 colors | Custom colors |

### 5.2 Ad Integration (AdMob)

```kotlin
// build.gradle
implementation("com.google.android.gms:play-services-ads:22.6.0")

// Ad Placements
┌─────────────────────────────────────┐
│ Home Screen      │ Banner (bottom)  │
│ After marking    │ Interstitial     │ ← Every 5th mark
│ Dashboard        │ Native (card)    │
│ Settings         │ Banner (bottom)  │
└─────────────────────────────────────┘

// Implementation
class AdManager @Inject constructor() {
    fun showInterstitial(activity: Activity, onComplete: () -> Unit)
    fun loadBanner(adView: AdView)
    fun shouldShowAd(): Boolean // Check subscription
}
```

### 5.3 In-App Purchases

```kotlin
// Google Play Billing
implementation("com.android.billingclient:billing-ktx:6.1.0")

// Products
val PRODUCTS = mapOf(
    "plus_monthly" to "$2.99",
    "plus_yearly" to "$29.99",      // 2 months free
    "ceo_monthly" to "$7.99",
    "ceo_yearly" to "$79.99"        // 2 months free
)
```

---

## 6. Cloud Migration (Local → Hybrid)

### 6.1 Current: Local-Only

```
[App] → [Room Database] → [Local Storage]
         ↓
      [Firebase Auth] (only for user identity)
```

### 6.2 Target: Hybrid (Local + Cloud Sync)

```
[App] → [Room Database] → [Local Storage]
              ↓ sync
        [Firebase Firestore]
              ↓
        [Cloud Functions] → [ML Processing]
```

### 6.3 Migration Steps

```
Phase 1: Add Firestore (2-3 weeks)
├── Add Firebase Firestore dependency
├── Create FirestoreRepository implementations
├── Add sync flags to entities (isSynced, lastSyncedAt)
├── Implement conflict resolution (last-write-wins)
└── Background sync with WorkManager

Phase 2: Real-time Sync (1-2 weeks)
├── Firestore listeners for partner data
├── Offline-first with optimistic updates
└── Sync queue for failed operations

Phase 3: Cloud Functions (1 week)
├── Trigger analytics aggregation
├── Partner notification dispatch
└── Data cleanup (orphaned records)
```

### 6.4 Firestore Schema

```javascript
// Firestore Collections
users/{userId}
  ├── habits/{habitId}
  │     ├── dailyLogs/{date}
  │     └── listItems/{itemId}
  ├── partnerships/{partnershipId}
  └── weeklyReflections/{weekId}

// Sync Strategy
{
  "localId": "uuid",
  "cloudId": "firestore-doc-id",
  "isSynced": true,
  "lastModified": 1704931200000,
  "pendingSync": false
}
```

---

## 7. AI Habit Builder (Future)

### 7.1 Concept

Instead of manual habit creation, users describe their goal:
- **Voice:** "I want to stop watching porn"
- **Text:** "Help me wake up earlier"
- **Choices:** Guided questionnaire

The AI creates a complete habit with:
- Customized resistance/attraction lists
- Personalized friction strategies
- Trigger predictions based on user profile
- Optimal reminder times

### 7.2 Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     USER INPUT                               │
│  [Voice] → Whisper API → Text                               │
│  [Text] → Direct                                            │
│  [Choices] → Structured data                                │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                   LANGCHAIN AGENT                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Intent    │→ │   Habit     │→ │  Content    │         │
│  │  Classifier │  │  Generator  │  │  Generator  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│         ↓               ↓                ↓                  │
│  "Break: Porn"    Type: BREAK     Resistance items          │
│                   Category: Health Friction strategies      │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                    ML MODELS                                 │
│                                                              │
│  1. Trigger Prediction (Classification)                     │
│     Input: time, day, location, mood, history               │
│     Output: [bored: 0.7, stressed: 0.2, tired: 0.1]        │
│                                                              │
│  2. Optimal Time Prediction (Regression)                    │
│     Input: user patterns, habit type, success history       │
│     Output: best_time = 07:30                               │
│                                                              │
│  3. Relapse Risk Score (Time Series)                        │
│     Input: streak length, trigger frequency, time of day    │
│     Output: risk_score = 0.73 (high)                        │
│     → Push notification: "Stay strong, high-risk hour"      │
└─────────────────────────────────────────────────────────────┘
```

### 7.3 ML Models (Similar to Period Tracking Apps)

**Period apps predict:** Next period date based on historical cycle data
**We predict:** Next relapse risk based on historical trigger data

| Model | Type | Input | Output | Use Case |
|-------|------|-------|--------|----------|
| Trigger Classifier | Multi-label Classification | Context features | Trigger probabilities | Post-failure analysis |
| Risk Predictor | Time Series (LSTM) | Historical patterns | Risk score (0-1) | Proactive notifications |
| Time Optimizer | Regression | User behavior | Best reminder time | Smart scheduling |
| Content Personalizer | Collaborative Filtering | User + similar users | Ranked resistance items | Relevant content |

### 7.4 Data Collection for ML

```kotlin
// Features collected per failure
data class FailureContext(
    val timestamp: Long,
    val dayOfWeek: Int,          // 1-7
    val hourOfDay: Int,          // 0-23
    val daysSinceLastFailure: Int,
    val currentStreak: Int,
    val triggers: List<String>,  // User-selected
    val location: String?,       // Optional
    val mood: String?            // Optional (future)
)

// Training data format (anonymized, aggregated)
{
  "features": [6, 23, 3, 12, ["bored", "alone"]],
  "label": "relapse"  // or "stayed_strong"
}
```

### 7.5 LangChain Implementation

```python
# Backend: FastAPI + LangChain
from langchain.agents import create_openai_agent
from langchain.tools import Tool

# Tools for the agent
tools = [
    Tool(name="create_habit", func=create_habit_in_db),
    Tool(name="generate_resistance", func=generate_resistance_items),
    Tool(name="suggest_friction", func=suggest_friction_strategies),
    Tool(name="predict_triggers", func=predict_user_triggers),
]

# Agent prompt
SYSTEM_PROMPT = """
You are a habit coach. Help users create effective habits using
Atomic Habits principles. For BREAK habits, focus on resistance
lists and friction. For BUILD habits, focus on attraction and
minimum viable versions.

Always be empathetic. Many users deal with sensitive habits
(addiction, bad habits). Never judge.
"""

# Example conversation
User: "I want to stop watching porn"
Agent:
  1. Creates BREAK habit "No Porn"
  2. Generates personalized resistance list
  3. Suggests friction strategies
  4. Predicts likely triggers (boredom, late night)
  5. Sets up smart notifications
```

---

## 8. Feature Roadmap

### Phase 1: MVP ✅ (Current)
- [x] Habit CRUD (BUILD/BREAK)
- [x] Daily tracking with streaks
- [x] Resistance/Attraction lists
- [x] "I'm Tempted" pause screen
- [x] Trigger logging
- [x] Progress dashboard with charts
- [x] Weekly reflections
- [x] Notifications (morning/evening)
- [x] Dark mode
- [x] Home screen widget

### Phase 2: Monetization (Next)
- [ ] AdMob integration
- [ ] Subscription tiers
- [ ] Premium templates
- [ ] Data export (CSV/PDF)
- [ ] Custom themes

### Phase 3: Cloud & Social
- [ ] Firestore sync
- [ ] Real-time partner updates
- [ ] Public habit challenges
- [ ] Leaderboards (opt-in)
- [ ] Share milestones

### Phase 4: AI & ML
- [ ] Trigger pattern detection
- [ ] Risk prediction notifications
- [ ] AI habit builder (LangChain)
- [ ] Voice input
- [ ] Personalized content ranking

### Phase 5: Platform Expansion
- [ ] iOS app (KMM or Flutter)
- [ ] Web dashboard
- [ ] Wear OS companion
- [ ] API for integrations

---

## 9. Play Store Deployment Checklist

```
Pre-Launch
├── [ ] Privacy Policy URL
├── [ ] Terms of Service URL
├── [ ] App icon (512x512)
├── [ ] Feature graphic (1024x500)
├── [ ] Screenshots (phone + tablet)
├── [ ] Short description (80 chars)
├── [ ] Full description (4000 chars)
├── [ ] Content rating questionnaire
├── [ ] Target audience declaration
├── [ ] Data safety form
└── [ ] Release signing key (upload key)

Technical
├── [ ] Minify + R8 enabled
├── [ ] ProGuard rules verified
├── [ ] App Bundle (.aab) not APK
├── [ ] Version code incremented
├── [ ] Crashlytics enabled
├── [ ] Analytics events defined
└── [ ] Deep links configured

Testing
├── [ ] Internal testing track
├── [ ] Closed alpha (10 users)
├── [ ] Open beta (100 users)
└── [ ] Production rollout (staged 10%)
```

---

## 10. Key Metrics (KPIs)

| Metric | Target | Measurement |
|--------|--------|-------------|
| DAU/MAU | >40% | Daily engagement |
| 7-day Retention | >30% | Users returning |
| Habit Success Rate | >60% | Streaks maintained |
| "Tempted" → "Stay Strong" | >70% | Pause screen effectiveness |
| Subscription Conversion | >5% | Free → Paid |
| Average Streak | >14 days | Habit stickiness |
| Crash-free Rate | >99.5% | Stability |

---

## 11. Competitive Advantage

| Feature | Habit Architect | Habitica | Streaks | Loop |
|---------|----------------|----------|---------|------|
| BUILD habits | ✅ | ✅ | ✅ | ✅ |
| BREAK habits | ✅ Full | ❌ | ❌ | Partial |
| Resistance Lists | ✅ | ❌ | ❌ | ❌ |
| "I'm Tempted" Flow | ✅ | ❌ | ❌ | ❌ |
| Trigger Detection | ✅ | ❌ | ❌ | ❌ |
| AI Habit Builder | 🔜 | ❌ | ❌ | ❌ |
| Partner Privacy | ✅ | ❌ | ❌ | ❌ |
| Gamification | Clips | RPG | ❌ | ❌ |

**Our moat:** Deep focus on BREAK habits with psychological tools (resistance lists, pause screens, trigger tracking). No competitor offers this combination.

---

## 12. Contact & Resources

- **Repository:** github.com/yousseflaaroui3010/habitBuilder
- **Tech Stack:** Kotlin, Jetpack Compose, Room, Hilt, Firebase
- **Architecture:** Clean Architecture (Data → Domain → Presentation)
- **Design:** Material 3, WCAG AA compliant colors

---

*This blueprint is a living document. Update as features evolve.*
