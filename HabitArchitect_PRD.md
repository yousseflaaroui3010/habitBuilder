# HABIT ARCHITECT — Product Requirements Document

**Version:** 1.0  
**Date:** December 31, 2024  
**Status:** Requirements Locked  
**Prepared by:** CAP (Client Advisory Panel)

---

## EXECUTIVE SUMMARY

Habit Architect is an Android application that helps users systematically build good habits and dismantle bad ones using behavioral science principles from James Clear's "Atomic Habits" framework. Unlike simple habit trackers, this app actively engineers behavior change through environmental design, psychological reframing, and strategic friction management.

**Core Value Proposition:** Transform vague intentions into automatic behaviors by making good habits obvious, attractive, easy, and satisfying — while making bad habits invisible, unattractive, difficult, and unsatisfying.

**Key Differentiators:**
- Socratic questioning system that builds personalized resistance/attraction lists
- Pre-built templates for common habits with evidence-based strategies
- Streak calendar with meaningful psychological weight
- Home screen widget for "temptation moments"
- Offline-first architecture (works without internet)

---

## TABLE OF CONTENTS

1. [Project Foundation](#1-project-foundation)
2. [Users & Access](#2-users--access)
3. [Features & Functionality](#3-features--functionality)
4. [Data Architecture](#4-data-architecture)
5. [Screen Specifications](#5-screen-specifications)
6. [Notification System](#6-notification-system)
7. [Template Habits Library](#7-template-habits-library)
8. [Technical Architecture](#8-technical-architecture)
9. [Security & Privacy](#9-security--privacy)
10. [Project Plan](#10-project-plan)

---

## 1. PROJECT FOUNDATION

### 1.1 Problem Statement

People fail at habit change not because they lack willpower, but because they:
- Rely on motivation instead of systems
- Don't understand the neurological habit loop (Cue → Craving → Response → Reward)
- Haven't engineered their environment to support desired behaviors
- Lack immediate consequences for slip-ups
- Don't have personalized "why" lists to reference during temptation moments

### 1.2 Solution

An app that guides users through a structured process to:
1. **Identify** their habit triggers (cues) and cravings
2. **Build** personalized resistance lists (for bad habits) or attraction lists (for good habits)
3. **Design** environmental changes that make success automatic
4. **Track** progress with meaningful streaks that hurt to break
5. **Intervene** at critical moments with reminders and reflection tools

### 1.3 Success Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| 7-day retention | >40% | Users who log at least once in days 7-14 |
| 30-day streak rate | >25% | Users who maintain any habit for 30+ days |
| Daily active usage | >60% of active users | Users who interact daily |
| Habit completion rate | >70% | Success marks / (Success + Failure marks) |
| App uninstall rate | <30% in first 7 days | Firebase/Play Console analytics |

### 1.4 Project Scope

**In Scope (V1):**
- Local-first Android app with Room database
- Google Sign-In and Email authentication
- Socratic onboarding flow for habit setup
- Template habits for 20+ common behaviors
- Streak calendar with celebrations/consequences
- Morning suggestion notification
- Evening success/failure prompt notification
- Home screen widget for temptation moments
- Partner accountability (optional, read-only sharing)

**Out of Scope (V1):**
- App/website blocking (defer to V2, integrate with existing blockers)
- ML-based trigger prediction (V2+)
- Backend sync (V2 — local-first means sync is enhancement, not requirement)
- iOS version
- Web dashboard
- Gamification/points system

### 1.5 Assumptions

| ID | Assumption | Impact if Wrong | Validation |
|----|------------|-----------------|------------|
| A-001 | Users will complete onboarding if kept under 5 minutes | Low completion rate | A/B test onboarding length |
| A-002 | Pre-built templates will cover 80% of use cases | Users abandon due to friction | Monitor custom habit creation rate |
| A-003 | Streak psychology is sufficient motivation | Users don't care about streaks | Survey users who drop off |
| A-004 | Users will grant notification permissions | Core feature unusable | Show value prop before permission request |

---

## 2. USERS & ACCESS

### 2.1 User Personas

#### Primary: The Struggling Self-Improver
- **Age:** 18-35
- **Technical Skill:** Moderate (comfortable with apps)
- **Context:** Has tried habit apps before, failed, feels frustrated
- **Goal:** Finally break a persistent bad habit OR establish a beneficial routine
- **Pain Points:** Generic advice, no personalization, trackers without strategy
- **Quote:** "I know what I should do. I just can't make myself do it consistently."

#### Secondary: The Accountability Seeker
- **Age:** 20-40
- **Context:** Wants external accountability
- **Goal:** Have someone (or something) to answer to
- **Pain Points:** Embarrassed to share struggles with humans, needs privacy + accountability

#### Tertiary: The Partner/Friend
- **Context:** Invited by primary user to be accountability partner
- **Goal:** Support their friend/family member
- **Usage:** Occasional check-ins, receives notifications on partner's progress

### 2.2 Authentication

#### Supported Methods
1. **Google Sign-In** (Primary)
   - One-tap authentication
   - Profile picture and name auto-populated
   - No password to remember

2. **Email + Password**
   - Email verification required (magic link)
   - Minimum 8 characters
   - No complexity requirements (length > complexity)

#### Session Management
| Setting | Value | Rationale |
|---------|-------|-----------|
| Session duration | 90 days | Reduce login friction |
| Remember device | Yes | Single-device typical use |
| Force re-auth | Never (unless logout) | Habit apps need instant access |
| Biometric unlock | Optional (device-level) | Leverage Android's biometric API |

### 2.3 Authorization Model

Simple role-based model:

| Role | Permissions |
|------|------------|
| **Owner** | Full access to own habits, lists, settings. Can invite partners. |
| **Partner** | Read-only access to shared habits/streaks. Cannot see resistance lists (private). |

#### Data Visibility Matrix

| Data | Owner | Partner |
|------|-------|---------|
| Habit names | ✓ | ✓ (if shared) |
| Streak calendar | ✓ | ✓ (if shared) |
| Success/Failure history | ✓ | ✓ (if shared) |
| Resistance/Attraction lists | ✓ | ✗ (private) |
| Onboarding answers | ✓ | ✗ (private) |
| Account settings | ✓ | ✗ |

---

## 3. FEATURES & FUNCTIONALITY

### 3.1 Feature Priority Matrix

| Priority | Feature | Rationale |
|----------|---------|-----------|
| **P0 - Must Have** | Habit creation with Socratic flow | Core value proposition |
| **P0** | Streak calendar | Primary motivation mechanism |
| **P0** | Morning/Evening notifications | Daily engagement driver |
| **P0** | Resistance/Attraction lists | Personalization engine |
| **P0** | Offline functionality | User requirement |
| **P1 - Should Have** | Template habits library | Reduces onboarding friction |
| **P1** | Home screen widget | Temptation intervention |
| **P1** | Partner accountability | Social motivation |
| **P1** | Celebration animations | Positive reinforcement |
| **P2 - Nice to Have** | Risk warning for habit overload | Prevents burnout |
| **P2** | Export data | User data ownership |

### 3.2 Feature Specifications

---

#### FEATURE: Socratic Habit Setup Flow

**Purpose:** Guide users through thoughtful habit configuration instead of rushed setup.

**Trigger:** User taps "Add Habit" or completes onboarding.

**Flow Type Selection:**
```
┌─────────────────────────────────────┐
│  What kind of habit is this?       │
│                                     │
│  ┌─────────────┐  ┌─────────────┐  │
│  │  🏗️ BUILD   │  │  🔨 BREAK   │  │
│  │  a good     │  │  a bad      │  │
│  │  habit      │  │  habit      │  │
│  └─────────────┘  └─────────────┘  │
└─────────────────────────────────────┘
```

**BUILD Flow (Good Habits) — Questions:**

| Step | Question | Purpose | Input Type |
|------|----------|---------|------------|
| 1 | "What habit do you want to build?" | Habit identification | Text input OR template selection |
| 2 | "When during your day would this habit fit best?" | Time anchoring | Time picker + context selector (Morning/Afternoon/Evening/Anytime) |
| 3 | "What existing habit could trigger this new one?" | Habit stacking | Text input with suggestions (e.g., "After I pour my coffee...") |
| 4 | "What would make this habit enjoyable for you?" | Attraction building | Multi-select from templates + custom text |
| 5 | "What's the absolute minimum version of this habit?" | Two-minute rule | Text input with examples (e.g., "Put on running shoes" instead of "Run 5K") |
| 6 | "How will you reward yourself immediately after?" | Satisfaction design | Multi-select + custom |

**BREAK Flow (Bad Habits) — Questions:**

| Step | Question | Purpose | Input Type |
|------|----------|---------|------------|
| 1 | "What habit do you want to break?" | Habit identification | Text input OR template selection |
| 2 | "When does this habit usually happen?" | Trigger identification | Time picker + context (Bored/Stressed/Tired/After X) |
| 3 | "What triggers the urge? What do you see, feel, or experience right before?" | Cue mapping | Multi-select + custom |
| 4 | "What does this habit really cost you? Be specific." | Unattractiveness building | **THIS BUILDS THE RESISTANCE LIST** — Multi-line text, one cost per line |
| 5 | "What friction can you add to make this harder?" | Difficulty design | Multi-select from suggestions + custom |
| 6 | "Who could hold you accountable?" | Social contract | Optional partner invite |

**Resistance List Generation:**
After Step 4 in BREAK flow, the user's answers are formatted into a numbered resistance list that appears when they tap "I'm Tempted" or use the widget.

**Example transformation:**
```
User input (Step 4):
- "My sleep gets disturbed"
- "Hard to wake up for Fajr"
- "Lose zinc I've been building"
- "Brain fog the next day"

Generated Resistance List:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🛑 BEFORE YOU DO THIS, REMEMBER:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. Your sleep will be disturbed
2. Fajr will be harder tomorrow
3. You'll lose the zinc you've built
4. Tomorrow you'll have brain fog
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         [I'm Still Tempted]
         [I'll Stay Strong 💪]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

**Acceptance Criteria:**
- Given a user selects "Build a good habit", when they complete all 6 questions, then a habit is created with: name, trigger time, habit stack anchor, attraction list, minimum version, reward
- Given a user selects "Break a bad habit", when they complete all 6 questions, then a habit is created with: name, trigger context, cue list, resistance list, friction strategies, optional partner
- Given a user is on any question, when they tap back, then they return to the previous question with their answer preserved
- Given a user selects a template habit, when they proceed, then questions are pre-populated with template defaults (editable)

---

#### FEATURE: Streak Calendar

**Purpose:** Visualize progress and create psychological weight around consistency.

**Design:**

```
┌─────────────────────────────────────────┐
│         DECEMBER 2024                   │
│  Mo Tu We Th Fr Sa Su                   │
│                          1              │
│   2  3  4  5  6  7  8                   │
│  ✓  ✓  ✓  ✓  ✓  ✓  ✓                   │
│   9 10 11 12 13 14 15                   │
│  ✓  ✓  ✓  ✓  ✓  ✓  ✓                   │
│  16 17 18 19 20 21 22                   │
│  ✓  ✓  ✓  ✓  ✓  ✓  ✓                   │
│  23 24 25 26 27 28 29                   │
│  ✓  ✓  ✓  ✓  ✓  ✗  ○                   │
│  30 31                                  │
│  ○  ○     🔥 Current Streak: 0 days     │
│           📈 Longest Streak: 25 days    │
└─────────────────────────────────────────┘
```

**Legend:**
- ✓ (Green checkmark): Success day
- ✗ (Red X): Failed day
- ○ (Empty circle): Pending/Future
- 🔥 Current streak counter
- 📈 Personal best streak

**Marking Success/Failure:**

For **Good Habits:**
- ✓ = "I did the habit today"
- ✗ = "I skipped it"

For **Bad Habits:**
- ✓ = "I resisted successfully"
- ✗ = "I relapsed"

**Streak Break Behavior:**
When a user marks ✗ (failure):
1. Screen dims slightly
2. Streak counter animates from current number down to 0
3. Subtle "chain breaking" sound effect
4. Message: "Your streak resets, but your progress doesn't. Every day is a new chance."
5. System records the failure with timestamp

**Success Celebration:**
When a user marks ✓:
1. Confetti animation (subtle, 1 second)
2. Pleasant "ding" sound
3. Streak counter increments with animation
4. If new personal best: "🎉 New Record! 26 days!"

**Acceptance Criteria:**
- Given a user views the calendar, when they tap any past date, then they can change its status (✓ ↔ ✗) with confirmation
- Given a user marks today as failure, when the animation completes, then streak shows 0 and longest streak is preserved
- Given a user marks today as success, when this creates a new longest streak, then celebratory message appears
- Given no internet connection, when user marks success/failure, then it saves locally and syncs when online (V2)

---

#### FEATURE: Home Screen Widget

**Purpose:** Immediate intervention during temptation moments.

**Widget Sizes:**
- **Small (2x2):** Single habit, tap to see resistance list
- **Medium (4x2):** Up to 3 habits, quick success/tempted buttons
- **Large (4x4):** Full habit list with mini-streaks

**Small Widget Layout:**
```
┌───────────────────┐
│ 🚫 No Porn        │
│ 🔥 Day 12         │
│                   │
│ [I'm Tempted]     │
└───────────────────┘
```

**Tap "I'm Tempted" → Shows:**
```
┌─────────────────────────────────────┐
│     🛑 STOP AND READ THIS 🛑        │
│                                     │
│  1. Your sleep will be disturbed    │
│  2. Fajr will be harder tomorrow    │
│  3. You'll lose your zinc           │
│  4. Brain fog awaits                │
│  5. You'll feel ashamed             │
│                                     │
│  ┌─────────────────────────────┐    │
│  │    I'll Stay Strong 💪      │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌─────────────────────────────┐    │
│  │    I Failed Today 😔        │    │
│  └─────────────────────────────┘    │
└─────────────────────────────────────┘
```

**Acceptance Criteria:**
- Given user adds widget, when they tap "I'm Tempted", then resistance list opens in overlay (not full app)
- Given user taps "I'll Stay Strong", when overlay closes, then no status change occurs
- Given user taps "I Failed Today", when confirmed, then streak resets and calendar updates

---

#### FEATURE: Partner Accountability

**Purpose:** Optional social accountability without public exposure.

**Invite Flow:**
1. User taps "Invite Accountability Partner"
2. System generates shareable link (valid 7 days)
3. Partner clicks link → prompted to create account or sign in
4. Partner sees read-only view of shared habits

**Partner View:**
```
┌─────────────────────────────────────┐
│  You're supporting: Ahmed           │
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 🚫 No Porn                  │    │
│  │ 🔥 12 day streak            │    │
│  │ Last update: 2 hours ago    │    │
│  └─────────────────────────────┘    │
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 🏃 Daily Exercise           │    │
│  │ 🔥 5 day streak             │    │
│  │ Last update: 18 hours ago   │    │
│  └─────────────────────────────┘    │
└─────────────────────────────────────┘
```

**Partner Notifications (Optional):**
- "Ahmed broke their streak on No Porn. Send encouragement?"
- "Ahmed just hit 30 days! Celebrate with them?"

**Privacy Controls:**
- User chooses which habits to share
- Resistance lists are NEVER shared
- User can revoke partner access anytime

**Acceptance Criteria:**
- Given user generates invite link, when partner uses it, then partner gains read-only access to selected habits only
- Given user revokes access, when partner opens app, then they see "Access removed" message
- Given user breaks streak on shared habit, when partner has notifications on, then partner receives notification

---

### 3.3 Explicit Non-Features (V1)

| Feature | Why Not |
|---------|---------|
| App/website blocking | Complex Android permissions, policy risks, existing apps do this better |
| ML-based predictions | Requires data collection period, adds complexity |
| Social feed | Privacy-sensitive context, not aligned with core value |
| Gamification/XP | Can trivialize serious habit struggles |
| Multiple themes | Focus on functionality first |
| Wearable integration | V2+ after core is stable |

---

## 4. DATA ARCHITECTURE

### 4.1 Entity Relationship Diagram

```
┌─────────────┐     ┌─────────────┐     ┌─────────────────┐
│    User     │────<│   Habit     │────<│   DailyLog      │
└─────────────┘     └─────────────┘     └─────────────────┘
      │                   │                     
      │                   │             ┌─────────────────┐
      │                   └────────────<│  ListItem       │
      │                                 │(Resistance/     │
      │                                 │ Attraction)     │
      │                                 └─────────────────┘
      │                                        
      │             ┌─────────────┐            
      └────────────<│ Partnership │            
                    └─────────────┘            
```

### 4.2 Entity Specifications

#### User
```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,           // UUID
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val authProvider: AuthProvider,        // GOOGLE, EMAIL
    val createdAt: Long,                   // Unix timestamp
    val lastActiveAt: Long,
    val onboardingCompleted: Boolean,
    val notificationsEnabled: Boolean,
    val morningReminderTime: String?,      // "07:30" format
    val eveningReminderTime: String?,      // "21:00" format
)

enum class AuthProvider { GOOGLE, EMAIL }
```

#### Habit
```kotlin
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey val id: String,           // UUID
    val userId: String,                   // FK to User
    val name: String,
    val type: HabitType,                  // BUILD, BREAK
    val category: String?,                // "Health", "Productivity", etc.
    val templateId: String?,              // If created from template
    
    // Scheduling
    val triggerTime: String?,             // "07:30" or null for anytime
    val triggerContext: String?,          // "After morning coffee"
    val frequency: HabitFrequency,        // DAILY, WEEKDAYS, CUSTOM
    val activeDays: List<Int>?,           // [1,2,3,4,5] for weekdays (1=Mon)
    
    // Two-minute rule
    val minimumVersion: String?,          // "Put on running shoes"
    
    // Stacking
    val stackAnchor: String?,             // "After I brush my teeth"
    
    // Reward (for BUILD)
    val reward: String?,                  // "Smoothie after workout"
    
    // Friction strategies (for BREAK)
    val frictionStrategies: List<String>?,
    
    // Streaks
    val currentStreak: Int,
    val longestStreak: Int,
    val totalSuccessDays: Int,
    val totalFailureDays: Int,
    
    // Sharing
    val isSharedWithPartner: Boolean,
    
    // Meta
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
)

enum class HabitType { BUILD, BREAK }
enum class HabitFrequency { DAILY, WEEKDAYS, WEEKENDS, CUSTOM }
```

#### ListItem (Resistance/Attraction Lists)
```kotlin
@Entity(tableName = "list_items")
data class ListItem(
    @PrimaryKey val id: String,           // UUID
    val habitId: String,                  // FK to Habit
    val type: ListType,                   // RESISTANCE, ATTRACTION
    val content: String,                  // "My sleep gets disturbed"
    val orderIndex: Int,                  // For ordering
    val isFromTemplate: Boolean,          // Was this pre-populated?
    val createdAt: Long,
)

enum class ListType { RESISTANCE, ATTRACTION }
```

#### DailyLog
```kotlin
@Entity(
    tableName = "daily_logs",
    primaryKeys = ["habitId", "date"]
)
data class DailyLog(
    val habitId: String,                  // FK to Habit
    val date: String,                     // "2024-12-31" ISO format
    val status: LogStatus,                // SUCCESS, FAILURE, SKIPPED
    val markedAt: Long,                   // When user marked it
    val note: String?,                    // Optional reflection
)

enum class LogStatus { SUCCESS, FAILURE, SKIPPED }
```

#### Partnership
```kotlin
@Entity(tableName = "partnerships")
data class Partnership(
    @PrimaryKey val id: String,
    val ownerId: String,                  // User who owns the habits
    val partnerId: String,                // User who has read access
    val inviteCode: String,               // For invite links
    val inviteExpiresAt: Long?,
    val status: PartnershipStatus,        // PENDING, ACTIVE, REVOKED
    val createdAt: Long,
)

enum class PartnershipStatus { PENDING, ACTIVE, REVOKED }
```

### 4.3 Room Database Setup

```kotlin
@Database(
    entities = [User::class, Habit::class, ListItem::class, DailyLog::class, Partnership::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class HabitArchitectDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao
    abstract fun listItemDao(): ListItemDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun partnershipDao(): PartnershipDao
}

class Converters {
    @TypeConverter
    fun fromListOfInt(value: List<Int>?): String? = value?.joinToString(",")
    
    @TypeConverter
    fun toListOfInt(value: String?): List<Int>? = value?.split(",")?.map { it.toInt() }
    
    @TypeConverter
    fun fromListOfString(value: List<String>?): String? = value?.joinToString("|||")
    
    @TypeConverter
    fun toListOfString(value: String?): List<String>? = value?.split("|||")
}
```

### 4.4 Key Queries (DAOs)

```kotlin
@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE userId = :userId AND isArchived = 0 ORDER BY createdAt DESC")
    fun getActiveHabits(userId: String): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE userId = :userId AND type = :type AND isArchived = 0")
    fun getHabitsByType(userId: String, type: HabitType): Flow<List<Habit>>
    
    @Query("UPDATE habits SET currentStreak = 0 WHERE id = :habitId")
    suspend fun resetStreak(habitId: String)
    
    @Query("UPDATE habits SET currentStreak = currentStreak + 1, longestStreak = MAX(longestStreak, currentStreak + 1), totalSuccessDays = totalSuccessDays + 1 WHERE id = :habitId")
    suspend fun incrementStreak(habitId: String)
}

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getLogsForRange(habitId: String, startDate: String, endDate: String): Flow<List<DailyLog>>
    
    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getLogForDate(habitId: String, date: String): DailyLog?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLog(log: DailyLog)
}
```

---

## 5. SCREEN SPECIFICATIONS

### 5.1 Screen Inventory

| Screen | Purpose | Entry Points |
|--------|---------|--------------|
| Splash | Brand moment, auth check | App launch |
| Onboarding (3 slides) | Value prop explanation | First launch |
| Sign In | Authentication | Onboarding completion, logout |
| Home/Dashboard | Habit overview, daily tasks | Main navigation |
| Habit Detail | Full habit view with calendar | Tap habit from home |
| Add Habit - Type Selection | Choose BUILD or BREAK | FAB on home |
| Add Habit - Socratic Flow | Guided setup questions | After type selection |
| Template Browser | Browse pre-built habits | During Add Habit |
| Resistance/Attraction List | View/edit list | Habit detail, widget |
| Settings | App configuration | Navigation drawer |
| Partner Management | Invite/manage partners | Settings |
| Partner View | Read-only shared habits | Partner's app |

### 5.2 Screen Specifications

---

#### SCREEN: Home/Dashboard

**Purpose:** Central hub for daily habit management.

**Layout:**
```
┌─────────────────────────────────────────┐
│ ☰  Habit Architect         👤 Profile   │
├─────────────────────────────────────────┤
│                                         │
│  Good morning, Ahmed                    │
│  December 31, 2024                      │
│                                         │
│  ┌─────────────────────────────────┐    │
│  │ 💡 Today's Focus                │    │
│  │ "After your morning coffee,     │    │
│  │  put on your workout clothes"   │    │
│  └─────────────────────────────────┘    │
│                                         │
│  ━━━━━ TO BUILD ━━━━━                   │
│                                         │
│  ┌─────────────────────────────────┐    │
│  │ 🏃 30-Min Exercise    🔥 5 days │    │
│  │ ○ Mark today                    │    │
│  └─────────────────────────────────┘    │
│                                         │
│  ┌─────────────────────────────────┐    │
│  │ 📖 Read 10 Pages      🔥 12 days│    │
│  │ ✓ Done today                    │    │
│  └─────────────────────────────────┘    │
│                                         │
│  ━━━━━ TO BREAK ━━━━━                   │
│                                         │
│  ┌─────────────────────────────────┐    │
│  │ 🚫 No Porn            🔥 8 days │    │
│  │ ○ Still going strong            │    │
│  │ [I'm Tempted]                   │    │
│  └─────────────────────────────────┘    │
│                                         │
│                              ╭───╮      │
│                              │ + │      │
│                              ╰───╯      │
└─────────────────────────────────────────┘
```

**Interactions:**

| Element | Tap Action | Long Press |
|---------|------------|------------|
| Habit card | Go to Habit Detail | Quick status menu |
| "Mark today" | Toggle success (animated) | — |
| "I'm Tempted" | Show resistance list overlay | — |
| FAB (+) | Open Add Habit flow | — |
| Profile icon | Open Settings | — |

**States:**
- **Empty:** Show motivational prompt + prominent Add Habit button
- **All done:** Celebratory message ("You crushed it today!")
- **Streak at risk:** Warning highlight on habits not yet marked

---

#### SCREEN: Add Habit - Socratic Flow

**Purpose:** Guided habit setup with one question per screen.

**Layout (Question Screen):**
```
┌─────────────────────────────────────────┐
│ ←                              Step 3/6 │
├─────────────────────────────────────────┤
│                                         │
│        ┌─────────────────────┐          │
│        │                     │          │
│        │    [Illustration]   │          │
│        │                     │          │
│        └─────────────────────┘          │
│                                         │
│   What existing habit could             │
│   trigger this new one?                 │
│                                         │
│   Link your new habit to something      │
│   you already do automatically.         │
│                                         │
│   ┌─────────────────────────────────┐   │
│   │ After I ___________________     │   │
│   └─────────────────────────────────┘   │
│                                         │
│   💡 Examples:                          │
│   • After I pour my morning coffee      │
│   • After I brush my teeth              │
│   • After I sit at my desk              │
│                                         │
│                                         │
│   ┌─────────────────────────────────┐   │
│   │           Continue →            │   │
│   └─────────────────────────────────┘   │
│                                         │
└─────────────────────────────────────────┘
```

**Progress Indicator:**
- Linear dots showing current position
- Completed steps are filled
- User can tap back but not skip ahead

**Input Types by Question:**

| Question | Input Component |
|----------|-----------------|
| Habit name | Text field + template suggestions |
| Time | Time picker + context chips (Morning/Evening/Anytime) |
| Habit stack anchor | Text field with autocomplete |
| Attraction/Resistance list | Multi-line text (one item per line) |
| Minimum version | Text field with character limit |
| Reward | Chips + custom text option |

**Validation:**
- Habit name: Required, 3-50 characters
- Resistance list: At least 3 items recommended (soft warning if fewer)
- All others: Optional but encouraged

---

#### SCREEN: Habit Detail

**Purpose:** Deep view of a single habit with calendar and list access.

**Layout:**
```
┌─────────────────────────────────────────┐
│ ←  No Porn                      ⋮ Menu  │
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐    │
│  │     🔥 CURRENT STREAK          │    │
│  │           8 days               │    │
│  │     Best: 25 days              │    │
│  └─────────────────────────────────┘    │
│                                         │
│  ┌─────────────────────────────────┐    │
│  │         DECEMBER 2024          │    │
│  │  Mo Tu We Th Fr Sa Su          │    │
│  │                       1        │    │
│  │   2  3  4  5  6  7  8          │    │
│  │  ✓  ✓  ✓  ✓  ✓  ✗  ✓          │    │
│  │   9 10 11 12 13 14 15          │    │
│  │  ✓  ✓  ✓  ✓  ✓  ✓  ✓          │    │
│  │  ...                           │    │
│  └─────────────────────────────────┘    │
│                                         │
│  ━━━━ Quick Actions ━━━━               │
│                                         │
│  ┌──────────────┐  ┌──────────────┐    │
│  │ I'm Tempted  │  │ View My Why  │    │
│  └──────────────┘  └──────────────┘    │
│                                         │
│  ━━━━ My Resistance List ━━━━          │
│                                         │
│  1. Sleep gets disturbed                │
│  2. Hard to wake up for Fajr            │
│  3. Lose zinc I've built                │
│  [View All / Edit]                      │
│                                         │
└─────────────────────────────────────────┘
```

**Menu Options (⋮):**
- Edit habit
- Share with partner
- Archive habit
- Delete habit (confirmation required)

---

## 6. NOTIFICATION SYSTEM

### 6.1 Notification Types

| Type | Timing | Content | Actions |
|------|--------|---------|---------|
| **Morning Suggestion** | User-set time (default 7:30 AM) | Habit stack reminder | Open app |
| **Evening Check-in** | User-set time (default 9:00 PM) | "How did today go?" | Quick mark buttons |
| **Streak Milestone** | When achieved | "🎉 7-day streak!" | Open app |
| **Partner Update** | When partner fails (if enabled) | "Ahmed needs support" | Open partner view |
| **Risk Warning** | After 2 days unmarked | "Don't break the chain!" | Mark now |

### 6.2 Notification Specifications

#### Morning Suggestion Notification

**Trigger:** Daily at user's configured time.

**Content Logic:**
1. Find user's habits with habit stacking configured
2. Select one randomly (or rotate)
3. Format: "After [anchor], remember: [minimum version]"

**Example:**
```
┌─────────────────────────────────────────┐
│ 🌅 Habit Architect                      │
│ After your morning coffee, put on your  │
│ workout clothes. Just show up today.    │
│                                         │
│ [Open App]                              │
└─────────────────────────────────────────┘
```

#### Evening Check-in Notification

**Trigger:** Daily at user's configured time.

**Content:** "Time to check in! How did your habits go today?"

**Actions (inline buttons):**
```
┌─────────────────────────────────────────┐
│ 🌙 Habit Architect                      │
│ Time to check in on your habits!        │
│                                         │
│ [All Good ✓]  [Open App]                │
└─────────────────────────────────────────┘
```

**"All Good" Action:**
- Marks all unmarked habits as SUCCESS for today
- Shows toast: "Great job! All habits marked."
- Does NOT open app (reduces friction)

### 6.3 Notification Permissions

**Permission Request Timing:** After user creates first habit (not during onboarding).

**Rationale Screen:**
```
┌─────────────────────────────────────────┐
│                                         │
│        🔔                               │
│                                         │
│   Stay on track with reminders          │
│                                         │
│   We'll send you:                       │
│   • Morning motivation (1x/day)         │
│   • Evening check-in (1x/day)           │
│   • Streak celebrations (occasional)    │
│                                         │
│   You can customize or disable these    │
│   anytime in Settings.                  │
│                                         │
│   ┌─────────────────────────────────┐   │
│   │        Enable Notifications     │   │
│   └─────────────────────────────────┘   │
│                                         │
│   [Maybe Later]                         │
│                                         │
└─────────────────────────────────────────┘
```

---

## 7. TEMPLATE HABITS LIBRARY

### 7.1 Template Structure

```kotlin
data class HabitTemplate(
    val id: String,
    val name: String,
    val type: HabitType,
    val category: String,
    val description: String,
    val defaultMinimumVersion: String?,
    val defaultStackAnchors: List<String>,
    val defaultRewards: List<String>,
    val defaultResistanceItems: List<String>,
    val defaultAttractionItems: List<String>,
    val defaultFrictionStrategies: List<String>,
    val iconEmoji: String,
)
```

### 7.2 Pre-Built Templates

#### BREAK Templates (Bad Habits)

---

**🚫 Porn/Masturbation Addiction**
```
Category: Health & Wellbeing
Description: Break free from pornography and compulsive masturbation

Default Resistance List:
1. My sleep quality will suffer tonight
2. I'll struggle to wake up for Fajr/morning routine
3. I'm depleting zinc and other nutrients I've worked to build
4. Brain fog will affect my focus tomorrow
5. I'll feel less confident and more ashamed
6. I'll be more likely to objectify people around me
7. This version of me isn't who I want to become
8. Every time I give in, the habit gets stronger

Default Friction Strategies:
• Install a website blocker (Cold Turkey, BlockSite)
• Have a friend set the blocker password
• Keep devices out of bedroom
• Never use phone/laptop in bed
• Log out of all accounts after each session
• Unfollow triggering accounts on social media

Default Trigger Contexts:
• Late at night alone
• Bored with nothing to do
• After seeing triggering content online
• Feeling stressed or anxious
```

---

**🚬 Smoking**
```
Category: Health
Description: Quit smoking cigarettes or vaping

Default Resistance List:
1. Each cigarette costs me [X] dirhams — that's [Y] per month
2. My lungs are healing; this would set them back
3. The craving will pass in 3 minutes whether I smoke or not
4. I'll smell like smoke and people will notice
5. My teeth and skin are improving; why reverse that?
6. I'm just feeding a chemical addiction, not a real need
7. The "relief" is just ending withdrawal I caused

Default Friction Strategies:
• Don't buy cigarettes; never have them at home
• Avoid smoking areas and smoking friends initially
• Replace the hand-to-mouth motion (toothpick, carrot)
• Wait 10 minutes before giving in; cravings pass
• Calculate money saved and put it in visible jar
```

---

**🍺 Alcohol**
```
Category: Health
Description: Reduce or eliminate alcohol consumption

Default Resistance List:
1. Alcohol disrupts my sleep architecture
2. Tomorrow's productivity will be lower
3. I'll feel dehydrated and sluggish
4. It's empty calories undermining my fitness goals
5. Drunk words are sober thoughts — I might regret something
6. The buzz fades quickly but the effects last all night

Default Friction Strategies:
• Don't keep alcohol at home
• Order first at restaurants (water/mocktail)
• Tell friends you're taking a break
• Have a go-to non-alcoholic drink ready
```

---

**😴 Oversleeping**
```
Category: Productivity
Description: Stop hitting snooze and wasting mornings

Default Resistance List:
1. I'll feel groggier from fragmented sleep, not better
2. My morning routine gets rushed or skipped
3. I miss the quiet productive hours of early morning
4. Hitting snooze trains my brain that alarms don't matter
5. I'll feel behind all day

Default Friction Strategies:
• Put alarm across the room
• Use an app that requires activity to dismiss (Alarmy)
• Set coffee maker to auto-brew at wake time
• Have tomorrow's clothes laid out
• Open blinds before bed (natural light wakes you)
```

---

**📱 Doomscrolling (Social Media)**
```
Category: Productivity
Description: Break the endless scroll habit

Default Resistance List:
1. I'm trading real life for curated highlight reels
2. This content is designed to be addictive, not valuable
3. I'll feel worse about myself afterward, not better
4. Time spent here is time not spent on my goals
5. The algorithm is optimizing for engagement, not my wellbeing

Default Friction Strategies:
• Set app time limits (Screen Time, Digital Wellbeing)
• Remove social apps from home screen
• Log out after each use
• Grayscale your phone display
• Put phone in another room while working
```

---

**💅 Nail Biting**
```
Category: Personal Care
Description: Stop biting nails and cuticles

Default Resistance List:
1. My nails will look damaged and unprofessional
2. I'm transferring bacteria from hands to mouth
3. I'll regret this when I see my fingers later
4. This is anxiety manifesting physically — address the root

Default Friction Strategies:
• Apply bitter nail polish
• Keep nails trimmed very short
• Wear bandaids on fingertips
• Hold something when anxious (stress ball, pen)
• Get a manicure (investment makes you protective)
```

---

#### BUILD Templates (Good Habits)

---

**🏃 Daily Exercise (30 minutes)**
```
Category: Health
Description: Build a consistent exercise routine

Default Minimum Version: "Put on my workout clothes"

Default Stack Anchors:
• After I pour my morning coffee
• After I drop kids at school
• After I finish work

Default Attraction Items:
1. I'll feel energized and accomplished after
2. This is building the body I want to see
3. Exercise clears my mind better than anything
4. I'm becoming someone who doesn't skip workouts
5. Even 10 minutes counts — just start

Default Rewards:
• Post-workout smoothie
• 10 minutes of guilt-free relaxation
• Check off the calendar (streak satisfaction)
• Sauna or hot shower
```

---

**📖 Daily Reading**
```
Category: Personal Growth
Description: Read books instead of screens

Default Minimum Version: "Read one page"

Default Stack Anchors:
• After I get into bed
• During my lunch break
• After my morning coffee

Default Attraction Items:
1. Reading makes me more interesting and knowledgeable
2. Each page is compound interest on my mind
3. This is how successful people spend their time
4. Books are conversations with the smartest people ever

Default Rewards:
• Track pages/books completed
• Cozy beverage while reading
• Share interesting quotes with friends
```

---

**🕌 Praying Fajr at the Mosque**
```
Category: Spiritual
Description: Attend Fajr prayer at the mosque consistently

Default Minimum Version: "Get out of bed at first alarm"

Default Stack Anchors:
• When I hear the adhan
• After my pre-dawn alarm

Default Attraction Items:
1. The barakah of praying in congregation (27x reward)
2. Starting the day with purpose and gratitude
3. The peace of the mosque at dawn
4. Joining a community of committed believers
5. My day goes better when I start with Fajr

Default Rewards:
• Peaceful walk back as the sun rises
• Breakfast after (earned, not just consumed)
• The streak itself becomes sacred
```

---

**📚 Quran Memorization**
```
Category: Spiritual
Description: Memorize Quran consistently

Default Minimum Version: "Review one ayah"

Default Stack Anchors:
• After Fajr prayer
• During commute
• Before bed

Default Attraction Items:
1. Each ayah memorized is permanent reward
2. The Quran will intercede for me
3. I'm joining generations of huffaz
4. My mind is sharper when engaged in memorization

Default Rewards:
• Track ayahs/pages completed
• Recite to a friend or teacher
• Reach a milestone surah
```

---

**🧘 Daily Meditation**
```
Category: Mental Health
Description: Build a meditation or mindfulness practice

Default Minimum Version: "Sit and take 3 deep breaths"

Default Stack Anchors:
• After I wake up
• After I finish work
• Before bed

Default Attraction Items:
1. 10 minutes of calm affects the whole day
2. I'm training my mind like I train my body
3. Stress doesn't control me when I meditate regularly
4. This is the highest-leverage investment in my wellbeing

Default Rewards:
• Cup of tea after
• Calm app streak badge
• Notice improved patience through the day
```

---

**🥗 Healthy Eating / Cooking**
```
Category: Health
Description: Cook more, eat out less, choose healthier options

Default Minimum Version: "Prepare one healthy snack"

Default Stack Anchors:
• When I get home from work (before sitting down)
• Sunday afternoon (meal prep)

Default Attraction Items:
1. I control exactly what goes into my body
2. Cooking is a skill that impresses and serves others
3. Home meals are cheaper AND healthier
4. My energy levels are better with real food

Default Rewards:
• Enjoy the meal you made
• Share food photos or with family
• Track money saved vs. eating out
```

---

**💼 Deep Work Block**
```
Category: Productivity
Description: Focused work without distractions

Default Minimum Version: "Clear desk and open project file"

Default Stack Anchors:
• After morning coffee
• After first meeting ends
• After lunch

Default Attraction Items:
1. This is when my best work happens
2. 2 hours of deep work > 6 hours of shallow work
3. My career advances through what I produce here
4. Flow state is its own reward

Default Rewards:
• Coffee break after the block
• Walk outside
• Check off high-priority task
```

---

### 7.3 Template Categories

| Category | Icon | Templates |
|----------|------|-----------|
| Health | ❤️ | Exercise, Quit Smoking, Quit Drinking, Healthy Eating |
| Productivity | ⚡ | Deep Work, No Doomscrolling, Wake Up Early |
| Spiritual | 🕌 | Fajr at Mosque, Quran Memorization, Daily Dhikr |
| Mental Health | 🧠 | Meditation, Journaling, Gratitude Practice |
| Personal Care | ✨ | Nail Biting, Skincare Routine |
| Relationships | 💝 | Quality Time, Daily Check-in |

---

## 8. TECHNICAL ARCHITECTURE

### 8.1 System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    HABIT ARCHITECT - V1                     │
│                    (Offline-First Android)                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐    ┌─────────────────┐                 │
│  │   Presentation  │    │    Widgets      │                 │
│  │     Layer       │    │  (Glance API)   │                 │
│  │  (Jetpack       │    │                 │                 │
│  │   Compose)      │    │                 │                 │
│  └────────┬────────┘    └────────┬────────┘                 │
│           │                      │                          │
│           ▼                      ▼                          │
│  ┌──────────────────────────────────────────┐               │
│  │           Domain Layer                   │               │
│  │  (Use Cases, Business Logic, Entities)   │               │
│  └────────────────────┬─────────────────────┘               │
│                       │                                     │
│                       ▼                                     │
│  ┌──────────────────────────────────────────┐               │
│  │           Data Layer                     │               │
│  │  ┌─────────────┐   ┌─────────────────┐   │               │
│  │  │    Room     │   │   DataStore     │   │               │
│  │  │  Database   │   │  (Preferences)  │   │               │
│  │  └─────────────┘   └─────────────────┘   │               │
│  └──────────────────────────────────────────┘               │
│                                                             │
│  ┌──────────────────────────────────────────┐               │
│  │           Services Layer                 │               │
│  │  ┌─────────────┐   ┌─────────────────┐   │               │
│  │  │ WorkManager │   │  Firebase Auth  │   │               │
│  │  │(Scheduling) │   │ (Google Sign-In)│   │               │
│  │  └─────────────┘   └─────────────────┘   │               │
│  └──────────────────────────────────────────┘               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 8.2 Technology Stack

| Layer | Technology | Version | Rationale |
|-------|------------|---------|-----------|
| **Language** | Kotlin | 1.9+ | Modern Android standard |
| **UI Framework** | Jetpack Compose | 1.5+ | Declarative, less boilerplate |
| **Architecture** | MVVM + Clean Architecture | — | Separation of concerns |
| **Database** | Room | 2.6+ | Offline-first, type-safe SQL |
| **Preferences** | DataStore | 1.0+ | Replaces SharedPreferences |
| **DI** | Hilt | 2.48+ | Official Android DI |
| **Async** | Kotlin Coroutines + Flow | — | Reactive data streams |
| **Navigation** | Navigation Compose | 2.7+ | Type-safe navigation |
| **Scheduling** | WorkManager | 2.8+ | Reliable background work |
| **Notifications** | NotificationCompat | — | Backward compatible |
| **Widgets** | Glance | 1.0+ | Compose-based widgets |
| **Auth** | Firebase Auth | 22+ | Google Sign-In, email |
| **Analytics** | Firebase Analytics | — | Usage tracking (V2) |

### 8.3 Project Structure

```
app/
├── src/main/
│   ├── java/com/habitarchitect/
│   │   ├── HabitArchitectApp.kt
│   │   │
│   │   ├── di/                          # Dependency Injection
│   │   │   ├── AppModule.kt
│   │   │   ├── DatabaseModule.kt
│   │   │   └── RepositoryModule.kt
│   │   │
│   │   ├── data/                        # Data Layer
│   │   │   ├── local/
│   │   │   │   ├── database/
│   │   │   │   │   ├── HabitArchitectDatabase.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── UserDao.kt
│   │   │   │   │   │   ├── HabitDao.kt
│   │   │   │   │   │   ├── DailyLogDao.kt
│   │   │   │   │   │   └── ListItemDao.kt
│   │   │   │   │   └── entity/
│   │   │   │   │       ├── UserEntity.kt
│   │   │   │   │       ├── HabitEntity.kt
│   │   │   │   │       └── ...
│   │   │   │   └── datastore/
│   │   │   │       └── UserPreferences.kt
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── HabitRepositoryImpl.kt
│   │   │   │   ├── UserRepositoryImpl.kt
│   │   │   │   └── TemplateRepository.kt
│   │   │   │
│   │   │   └── mapper/
│   │   │       └── EntityMappers.kt
│   │   │
│   │   ├── domain/                      # Domain Layer
│   │   │   ├── model/
│   │   │   │   ├── Habit.kt
│   │   │   │   ├── User.kt
│   │   │   │   ├── DailyLog.kt
│   │   │   │   └── HabitTemplate.kt
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── HabitRepository.kt
│   │   │   │   └── UserRepository.kt
│   │   │   │
│   │   │   └── usecase/
│   │   │       ├── habit/
│   │   │       │   ├── CreateHabitUseCase.kt
│   │   │       │   ├── MarkHabitSuccessUseCase.kt
│   │   │       │   ├── MarkHabitFailureUseCase.kt
│   │   │       │   ├── GetHabitsUseCase.kt
│   │   │       │   └── CalculateStreakUseCase.kt
│   │   │       ├── auth/
│   │   │       │   ├── SignInWithGoogleUseCase.kt
│   │   │       │   └── SignOutUseCase.kt
│   │   │       └── notification/
│   │   │           └── ScheduleNotificationsUseCase.kt
│   │   │
│   │   ├── presentation/                # Presentation Layer
│   │   │   ├── navigation/
│   │   │   │   ├── NavGraph.kt
│   │   │   │   └── Screen.kt
│   │   │   │
│   │   │   ├── theme/
│   │   │   │   ├── Theme.kt
│   │   │   │   ├── Color.kt
│   │   │   │   └── Typography.kt
│   │   │   │
│   │   │   ├── components/
│   │   │   │   ├── HabitCard.kt
│   │   │   │   ├── StreakCalendar.kt
│   │   │   │   ├── ResistanceListOverlay.kt
│   │   │   │   └── ConfettiAnimation.kt
│   │   │   │
│   │   │   ├── screen/
│   │   │   │   ├── splash/
│   │   │   │   ├── onboarding/
│   │   │   │   ├── auth/
│   │   │   │   ├── home/
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   ├── habitdetail/
│   │   │   │   ├── addhabit/
│   │   │   │   │   ├── AddHabitScreen.kt
│   │   │   │   │   ├── AddHabitViewModel.kt
│   │   │   │   │   └── SocraticQuestionScreen.kt
│   │   │   │   ├── templates/
│   │   │   │   └── settings/
│   │   │   │
│   │   │   └── widget/
│   │   │       ├── HabitWidget.kt
│   │   │       └── TemptationWidget.kt
│   │   │
│   │   └── service/
│   │       ├── notification/
│   │       │   ├── NotificationHelper.kt
│   │       │   └── NotificationWorker.kt
│   │       └── auth/
│   │           └── AuthService.kt
│   │
│   └── res/
│       ├── values/
│       ├── drawable/
│       └── raw/                         # Sound effects
│           ├── success_ding.mp3
│           └── streak_break.mp3
│
├── build.gradle.kts
└── proguard-rules.pro
```

### 8.4 Key Implementation Patterns

#### ViewModel Pattern
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val markHabitSuccessUseCase: MarkHabitSuccessUseCase,
    private val markHabitFailureUseCase: MarkHabitFailureUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            getHabitsUseCase()
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { habits ->
                    _uiState.update { it.copy(habits = habits, isLoading = false) }
                }
        }
    }

    fun onMarkSuccess(habitId: String) {
        viewModelScope.launch {
            markHabitSuccessUseCase(habitId, LocalDate.now().toString())
        }
    }

    fun onMarkFailure(habitId: String) {
        viewModelScope.launch {
            markHabitFailureUseCase(habitId, LocalDate.now().toString())
        }
    }
}

data class HomeUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)
```

#### Repository Pattern
```kotlin
interface HabitRepository {
    fun getHabits(): Flow<List<Habit>>
    fun getHabitById(id: String): Flow<Habit?>
    suspend fun createHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(id: String)
    suspend fun markSuccess(habitId: String, date: String)
    suspend fun markFailure(habitId: String, date: String)
}

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao,
) : HabitRepository {

    override fun getHabits(): Flow<List<Habit>> =
        habitDao.getActiveHabits().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun markSuccess(habitId: String, date: String) {
        dailyLogDao.upsertLog(
            DailyLogEntity(
                habitId = habitId,
                date = date,
                status = LogStatus.SUCCESS,
                markedAt = System.currentTimeMillis()
            )
        )
        habitDao.incrementStreak(habitId)
    }

    override suspend fun markFailure(habitId: String, date: String) {
        dailyLogDao.upsertLog(
            DailyLogEntity(
                habitId = habitId,
                date = date,
                status = LogStatus.FAILURE,
                markedAt = System.currentTimeMillis()
            )
        )
        habitDao.resetStreak(habitId)
    }
}
```

### 8.5 Widget Implementation

```kotlin
class HabitWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = // Get from Hilt
        val habit = repository.getPrimaryHabit()

        provideContent {
            HabitWidgetContent(habit)
        }
    }

    @Composable
    private fun HabitWidgetContent(habit: Habit?) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.White)
                .padding(12.dp)
        ) {
            if (habit != null) {
                Text(
                    text = "${habit.iconEmoji} ${habit.name}",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(text = "🔥 Day ${habit.currentStreak}")
                Spacer(modifier = GlanceModifier.height(8.dp))
                Button(
                    text = "I'm Tempted",
                    onClick = actionStartActivity<TemptationActivity>(
                        actionParametersOf(HABIT_ID_KEY to habit.id)
                    )
                )
            } else {
                Text("Add a habit to get started")
            }
        }
    }
}
```

### 8.6 Notification Scheduling

```kotlin
class NotificationScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val userPreferences: UserPreferences,
) {
    fun scheduleDailyNotifications() {
        // Morning notification
        val morningTime = userPreferences.morningReminderTime ?: "07:30"
        scheduleDailyWork(
            tag = "morning_reminder",
            time = morningTime,
            workerClass = MorningReminderWorker::class.java
        )

        // Evening notification
        val eveningTime = userPreferences.eveningReminderTime ?: "21:00"
        scheduleDailyWork(
            tag = "evening_checkin",
            time = eveningTime,
            workerClass = EveningCheckinWorker::class.java
        )
    }

    private fun scheduleDailyWork(tag: String, time: String, workerClass: Class<out Worker>) {
        val (hour, minute) = time.split(":").map { it.toInt() }
        
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<workerClass>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(tag)
            .build()

        workManager.enqueueUniquePeriodicWork(
            tag,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
```

---

## 9. SECURITY & PRIVACY

### 9.1 Data Protection

| Data Type | Storage | Encryption | Notes |
|-----------|---------|------------|-------|
| User credentials | Firebase Auth | Managed by Firebase | Never stored locally |
| Habit data | Room DB | SQLCipher (optional V2) | Local-only in V1 |
| Resistance lists | Room DB | SQLCipher (optional V2) | Highly sensitive |
| Preferences | DataStore | Android Keystore | Notification times, settings |

### 9.2 Privacy Principles

1. **Minimal Collection:** Only collect what's necessary for functionality
2. **Local-First:** All data stays on device in V1; sync is opt-in in V2
3. **No Ads:** No ad networks, no selling data
4. **User Control:** Export and delete all data anytime
5. **Sensitive Content:** Resistance lists are NEVER shared, even with partners

### 9.3 Authentication Security

| Measure | Implementation |
|---------|----------------|
| Google Sign-In | OAuth 2.0 via Firebase |
| Email auth | Firebase Auth with email verification |
| Session management | Firebase handles token refresh |
| Biometric | Optional, uses Android BiometricPrompt |

### 9.4 Privacy Policy Requirements

The app will need a privacy policy covering:
- What data is collected
- How it's stored (locally)
- What Firebase collects (auth only)
- User rights (export, delete)
- No data sharing/selling
- Contact information

---

## 10. PROJECT PLAN

### 10.1 Delivery Phases

#### Phase 1: Core MVP (6 weeks)
**Goal:** Working app with essential habit tracking

| Week | Focus | Deliverables |
|------|-------|--------------|
| 1-2 | Foundation | Project setup, Room database, basic navigation, theme |
| 3 | Auth | Google Sign-In, email auth, user entity |
| 4 | Habit CRUD | Create habit (basic), list view, streak calendar |
| 5 | Daily flow | Mark success/failure, streak logic, celebrations |
| 6 | Polish | Bug fixes, animations, basic notifications |

**MVP Features:**
- ✅ Google/Email authentication
- ✅ Create habits (basic flow, no Socratic yet)
- ✅ Streak calendar with success/failure marking
- ✅ Celebration animation on success
- ✅ Streak break animation on failure
- ✅ Basic home screen

#### Phase 2: Engagement Features (4 weeks)
**Goal:** Socratic flow, templates, notifications

| Week | Focus | Deliverables |
|------|-------|--------------|
| 7 | Socratic flow | BUILD flow questions, answer storage |
| 8 | Socratic flow | BREAK flow, resistance list generation |
| 9 | Templates | Template library, pre-populated defaults |
| 10 | Notifications | Morning/evening notifications, WorkManager |

**Phase 2 Features:**
- ✅ Full Socratic onboarding for habits
- ✅ Resistance/Attraction lists
- ✅ Template habits library (20+ templates)
- ✅ Morning suggestion notification
- ✅ Evening check-in notification with quick actions

#### Phase 3: Power Features (3 weeks)
**Goal:** Widget, partnerships, polish

| Week | Focus | Deliverables |
|------|-------|--------------|
| 11 | Widget | Home screen widget, temptation overlay |
| 12 | Partners | Invite flow, read-only sharing |
| 13 | Polish | Performance, edge cases, Play Store prep |

**Phase 3 Features:**
- ✅ Home screen widget
- ✅ "I'm Tempted" flow
- ✅ Partner accountability (invite, view)
- ✅ Settings screen complete
- ✅ Play Store ready

### 10.2 Timeline Summary

```
Week 1-6:   ████████████████████████  MVP
Week 7-10:  ████████████████          Engagement
Week 11-13: ████████████              Power Features
            ─────────────────────────────────────
Total:      13 weeks (~3 months)
```

### 10.3 Risk Register

| ID | Risk | Probability | Impact | Mitigation |
|----|------|-------------|--------|------------|
| R1 | Notification permissions denied | High | High | Value prop screen before request; app works without |
| R2 | Widget API complexity | Medium | Medium | Start with simplest widget; enhance iteratively |
| R3 | Scope creep (blocking feature) | High | High | Explicitly deferred to V2; documented |
| R4 | Firebase auth issues | Low | High | Test early; have email fallback |
| R5 | Play Store rejection | Medium | High | Follow all policies; no blocking features |

### 10.4 Success Criteria (V1 Launch)

| Criterion | Target | Measurement |
|-----------|--------|-------------|
| App launches without crash | 99%+ sessions | Firebase Crashlytics |
| Onboarding completion | >70% of new users | Analytics |
| Day-7 retention | >30% | Analytics |
| Play Store rating | >4.0 | Play Console |
| Core flows work offline | 100% | Manual testing |

---

## APPENDIX A: Glossary

| Term | Definition |
|------|------------|
| **Habit Loop** | Cue → Craving → Response → Reward cycle |
| **Habit Stacking** | Linking a new habit to an existing one |
| **Resistance List** | Personal reasons why a bad habit is harmful |
| **Attraction List** | Personal reasons why a good habit is beneficial |
| **Two-Minute Rule** | Scale habit down to 2 minutes to reduce friction |
| **Streak** | Consecutive days of success |
| **Temptation Bundling** | Pairing a habit you need with something you want |

---

## APPENDIX B: Acceptance Criteria Checklist

### Authentication
- [ ] User can sign in with Google (one-tap)
- [ ] User can sign in with email + password
- [ ] Email verification is sent and required
- [ ] User stays logged in for 90 days
- [ ] User can sign out
- [ ] User can delete account and all data

### Habit Management
- [ ] User can create a BUILD habit with Socratic flow
- [ ] User can create a BREAK habit with Socratic flow
- [ ] User can select from template habits
- [ ] Template defaults are editable
- [ ] User can edit existing habit
- [ ] User can archive habit
- [ ] User can delete habit (with confirmation)
- [ ] User sees risk warning when adding 6th+ habit

### Daily Tracking
- [ ] User can mark habit as success
- [ ] User can mark habit as failure
- [ ] Success triggers celebration animation + sound
- [ ] Failure triggers streak reset animation
- [ ] User can change past day's status
- [ ] Streak counter updates correctly
- [ ] Longest streak is preserved

### Calendar
- [ ] Calendar shows current month
- [ ] User can navigate to past months
- [ ] Success days show green checkmark
- [ ] Failure days show red X
- [ ] Pending days show empty circle
- [ ] Tapping day allows status change

### Resistance/Attraction Lists
- [ ] List is generated from Socratic answers
- [ ] User can view list from habit detail
- [ ] User can edit list items
- [ ] User can add new items
- [ ] User can reorder items
- [ ] List appears in temptation overlay

### Notifications
- [ ] Morning notification fires at configured time
- [ ] Evening notification fires at configured time
- [ ] User can configure notification times
- [ ] User can disable notifications
- [ ] "All Good" quick action marks all habits
- [ ] Notifications work when app is closed

### Widget
- [ ] Widget can be added to home screen
- [ ] Widget shows habit name and streak
- [ ] "I'm Tempted" button shows resistance list overlay
- [ ] Overlay actions update habit status
- [ ] Widget updates when habit status changes

### Partners
- [ ] User can generate invite link
- [ ] Partner can accept invite
- [ ] Partner sees shared habits (read-only)
- [ ] Partner sees streaks
- [ ] Partner does NOT see resistance lists
- [ ] User can revoke partner access

### Offline
- [ ] All features work without internet
- [ ] Data persists after app restart
- [ ] No error messages when offline

---

**END OF PRD**

*Document prepared by CAP (Client Advisory Panel)*
*Ready for development handoff*
