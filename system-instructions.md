# HABIT ARCHITECT — System Instructions for Claude Code

## CONTEXT

### What We're Building
**Habit Architect** is a production-ready Android application that helps users systematically build good habits and break bad ones using behavioral science principles from James Clear's "Atomic Habits" framework. This is NOT a simple habit tracker — it's a behavior change engineering system.

### Core Innovation
Unlike competitors that just track habits, Habit Architect:
1. **Guides habit setup through Socratic questioning** — building personalized resistance/attraction lists
2. **Provides temptation intervention** — home screen widget shows personalized "why not" list at critical moments
3. **Creates psychological weight** — streak calendar with meaningful celebrations and consequences
4. **Works offline-first** — all data local, no backend dependency for V1

### Target Users
- Primary: 18-35 year olds struggling with persistent bad habits (porn addiction, smoking, doomscrolling) or trying to establish beneficial routines (exercise, reading, prayer)
- Secondary: Accountability partners who need read-only access to support friends/family

### Key Constraints
1. **Offline-first architecture** — must work without internet connection
2. **Privacy-sensitive content** — resistance lists contain deeply personal information, NEVER shared
3. **No gamification/points** — serious context, don't trivialize struggles
4. **Android only for V1** — Kotlin, Jetpack Compose, Material 3
5. **Firebase Auth only** — no backend sync in V1

### Tech Stack (Non-Negotiable)
| Layer | Technology | Version |
|-------|------------|---------|
| Language | Kotlin | 1.9+ |
| UI | Jetpack Compose | 1.5+ |
| Architecture | MVVM + Clean Architecture | — |
| Database | Room | 2.6+ |
| Preferences | DataStore | 1.0+ |
| DI | Hilt | 2.48+ |
| Async | Coroutines + Flow | — |
| Navigation | Navigation Compose | 2.7+ |
| Background | WorkManager | 2.8+ |
| Widgets | Glance | 1.0+ |
| Auth | Firebase Auth | 22+ |

---

## OBJECTIVE

### Primary Objectives (Must Achieve)
1. **Complete Socratic habit setup flow** for both BUILD (good habits) and BREAK (bad habits)
2. **Streak calendar with psychological weight** — celebrations on success, meaningful reset animation on failure
3. **Home screen widget** with "I'm Tempted" button showing personalized resistance list
4. **Morning/Evening notification system** using WorkManager
5. **Offline-first data persistence** with Room database
6. **Google Sign-In and Email authentication** via Firebase
7. **20+ pre-built habit templates** with evidence-based defaults
8. **Partner accountability** with read-only sharing (resistance lists NEVER shared)

### Secondary Objectives (Should Achieve)
1. Beautiful Material 3 design with smooth animations
2. Confetti celebration on success, chain-break animation on failure
3. Sound effects (subtle ding on success, chain break on failure)
4. Export data functionality
5. Risk warning when adding 6th+ habit

### Out of Scope (Must NOT Do)
- App/website blocking features
- ML-based predictions
- Backend sync (defer to V2)
- iOS or web versions
- Gamification, points, leaderboards
- Multiple themes

### Success Criteria
| Criterion | Target |
|-----------|--------|
| App launches without crash | 99%+ sessions |
| All core flows work offline | 100% |
| Onboarding completion | >70% of new users |
| Code coverage on business logic | >80% |
| Play Store ready (policies compliant) | Yes |

---

## STYLE

### Code Architecture — Clean Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Compose UI, ViewModels, Navigation)   │
├─────────────────────────────────────────┤
│           Domain Layer                  │
│  (Use Cases, Business Logic, Models)    │
├─────────────────────────────────────────┤
│            Data Layer                   │
│  (Repositories, DAOs, Entities, APIs)   │
└─────────────────────────────────────────┘
```

### Package Structure
```
com.habitarchitect/
├── di/                     # Hilt modules
├── data/
│   ├── local/
│   │   ├── database/       # Room DB, DAOs, Entities
│   │   └── datastore/      # Preferences
│   ├── repository/         # Repository implementations
│   └── mapper/             # Entity ↔ Domain mappers
├── domain/
│   ├── model/              # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Business logic use cases
├── presentation/
│   ├── navigation/         # NavGraph, Screen routes
│   ├── theme/              # Material 3 theme
│   ├── components/         # Reusable Compose components
│   ├── screen/             # Feature screens
│   └── widget/             # Glance widgets
└── service/
    ├── notification/       # WorkManager workers
    └── auth/               # Firebase auth wrapper
```

### Coding Standards

**Naming Conventions:**
- Classes: PascalCase (e.g., `HabitRepository`, `CreateHabitUseCase`)
- Functions: camelCase (e.g., `markHabitSuccess`, `getActiveHabits`)
- Variables: camelCase (e.g., `habitList`, `currentStreak`)
- Constants: SCREAMING_SNAKE_CASE (e.g., `MAX_HABITS`, `DEFAULT_REMINDER_TIME`)
- Composables: PascalCase (e.g., `HabitCard`, `StreakCalendar`)

**Function Guidelines:**
- Max 40 lines per function (ideal: 20)
- Single responsibility
- Clear input/output types
- No `any` types — strong typing always

**File Guidelines:**
- Max 300 lines per file (ideal: 150)
- One class/responsibility per file
- Extract utilities for repeated patterns

**Comments:**
- Explain WHY, not WHAT
- 1 comment per 20-30 lines average
- KDoc for public APIs

**Error Handling:**
- Never swallow exceptions silently
- Use `Result<T>` for operations that can fail
- Structured error types, not generic exceptions

### Compose Best Practices
```kotlin
// ✅ DO: Stateless composables with hoisted state
@Composable
fun HabitCard(
    habit: Habit,
    onMarkSuccess: () -> Unit,
    onMarkFailure: () -> Unit,
    onTemptedClick: () -> Unit,
    modifier: Modifier = Modifier
)

// ❌ DON'T: Fat composables with internal state
@Composable
fun HabitCard(habitId: String) {
    val viewModel = hiltViewModel<HabitCardViewModel>()
    // Too much responsibility
}
```

### Repository Pattern
```kotlin
// Interface in domain layer
interface HabitRepository {
    fun getHabits(): Flow<List<Habit>>
    suspend fun createHabit(habit: Habit): Result<String>
    suspend fun markSuccess(habitId: String, date: LocalDate): Result<Unit>
}

// Implementation in data layer
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao,
) : HabitRepository
```

### Use Case Pattern
```kotlin
class MarkHabitSuccessUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
) {
    suspend operator fun invoke(habitId: String, date: LocalDate): Result<Unit> {
        return habitRepository.markSuccess(habitId, date)
    }
}
```

---

## TONE

### Development Philosophy
1. **Quality over speed** — Production-ready code, not quick hacks
2. **Pragmatic over perfect** — Ship working features, iterate
3. **Readable over clever** — Code is read 100x more than written
4. **Maintainable over minimal** — Future developers will thank you
5. **Offline-first mindset** — Assume no network, delight when available

### User Experience Philosophy
1. **Encouraging, never punishing** — "Your streak resets, but your progress doesn't"
2. **Privacy-first** — Resistance lists are deeply personal, treat them as sacred
3. **Reduce friction** — One-tap actions, smart defaults, minimal typing
4. **Psychological weight** — Streaks should hurt to break (but not shame)
5. **Celebrate small wins** — Confetti for every success, not just milestones

---

## AUDIENCE

### Claude Code Execution Context
You are building this app in an Android development environment with:
- Android Studio project structure
- Gradle (Kotlin DSL) build system
- Min SDK 26 (Android 8.0), Target SDK 34
- Material 3 design system

### Assumptions Claude Code Can Make
1. Firebase project is configured (google-services.json will be provided)
2. Sound files will be provided (success_ding.mp3, streak_break.mp3)
3. App icons and splash screen assets will be provided
4. Play Store listing assets are out of scope

### What Claude Code Must Verify
1. All dependencies compile correctly
2. Database migrations work (up AND down)
3. Notifications schedule correctly with WorkManager
4. Widget updates when habit status changes
5. All screens work in portrait and landscape

---

## RESPONSE

### Expected Deliverables

**1. Complete Android Project Structure:**
```
habit-architect/
├── app/
│   ├── src/main/
│   │   ├── java/com/habitarchitect/
│   │   └── res/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── tracking-files/
    ├── Contextlog.md
    ├── requirements.md
    ├── decisionlog.md
    └── Project-summary.md
```

**2. All Screens Implemented:**
- Splash → Onboarding (3 slides) → Sign In
- Home/Dashboard with habit cards
- Add Habit (Type Selection → Socratic Flow)
- Template Browser
- Habit Detail with calendar
- Resistance/Attraction List viewer/editor
- Settings
- Partner Management

**3. Core Components:**
- Room database with all entities and DAOs
- Repository implementations
- Use cases for all business logic
- ViewModels for all screens
- Glance widget
- WorkManager notification workers

**4. Quality Requirements:**
- No `TODO` comments in final code
- All public APIs documented with KDoc
- Unit tests for use cases and repositories
- UI tests for critical flows
- ProGuard rules configured

---

## DETAILED TECHNICAL SPECIFICATIONS

### Database Schema

#### UserEntity
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,           // Firebase UID
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val authProvider: String,              // "GOOGLE" or "EMAIL"
    val createdAt: Long,
    val lastActiveAt: Long,
    val onboardingCompleted: Boolean,
    val notificationsEnabled: Boolean,
    val morningReminderTime: String?,      // "07:30" format
    val eveningReminderTime: String?,      // "21:00" format
)
```

#### HabitEntity
```kotlin
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,           // UUID
    val userId: String,
    val name: String,
    val type: String,                      // "BUILD" or "BREAK"
    val category: String?,
    val templateId: String?,
    val iconEmoji: String,
    
    // Scheduling
    val triggerTime: String?,
    val triggerContext: String?,
    val frequency: String,                 // "DAILY", "WEEKDAYS", "CUSTOM"
    val activeDays: String?,               // "1,2,3,4,5" for weekdays
    
    // Two-minute rule
    val minimumVersion: String?,
    
    // Stacking
    val stackAnchor: String?,
    
    // Reward (for BUILD)
    val reward: String?,
    
    // Friction strategies (for BREAK)
    val frictionStrategies: String?,       // Pipe-separated
    
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
```

#### ListItemEntity (Resistance/Attraction Lists)
```kotlin
@Entity(tableName = "list_items")
data class ListItemEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val type: String,                      // "RESISTANCE" or "ATTRACTION"
    val content: String,
    val orderIndex: Int,
    val isFromTemplate: Boolean,
    val createdAt: Long,
)
```

#### DailyLogEntity
```kotlin
@Entity(
    tableName = "daily_logs",
    primaryKeys = ["habitId", "date"]
)
data class DailyLogEntity(
    val habitId: String,
    val date: String,                      // "2024-12-31" ISO format
    val status: String,                    // "SUCCESS", "FAILURE", "SKIPPED"
    val markedAt: Long,
    val note: String?,
)
```

#### PartnershipEntity
```kotlin
@Entity(tableName = "partnerships")
data class PartnershipEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val partnerId: String,
    val inviteCode: String,
    val inviteExpiresAt: Long?,
    val status: String,                    // "PENDING", "ACTIVE", "REVOKED"
    val createdAt: Long,
)
```

### DAO Specifications

#### HabitDao
```kotlin
@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE userId = :userId AND isArchived = 0 ORDER BY createdAt DESC")
    fun getActiveHabits(userId: String): Flow<List<HabitEntity>>
    
    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun getHabitById(id: String): Flow<HabitEntity?>
    
    @Query("SELECT * FROM habits WHERE userId = :userId AND type = :type AND isArchived = 0")
    fun getHabitsByType(userId: String, type: String): Flow<List<HabitEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)
    
    @Update
    suspend fun updateHabit(habit: HabitEntity)
    
    @Query("UPDATE habits SET currentStreak = 0, totalFailureDays = totalFailureDays + 1, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun resetStreak(habitId: String, timestamp: Long)
    
    @Query("UPDATE habits SET currentStreak = currentStreak + 1, longestStreak = MAX(longestStreak, currentStreak + 1), totalSuccessDays = totalSuccessDays + 1, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun incrementStreak(habitId: String, timestamp: Long)
    
    @Query("UPDATE habits SET isArchived = 1, updatedAt = :timestamp WHERE id = :habitId")
    suspend fun archiveHabit(habitId: String, timestamp: Long)
    
    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: String)
    
    @Query("SELECT COUNT(*) FROM habits WHERE userId = :userId AND isArchived = 0")
    suspend fun getActiveHabitCount(userId: String): Int
}
```

#### DailyLogDao
```kotlin
@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getLogsForRange(habitId: String, startDate: String, endDate: String): Flow<List<DailyLogEntity>>
    
    @Query("SELECT * FROM daily_logs WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getLogForDate(habitId: String, date: String): DailyLogEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLog(log: DailyLogEntity)
    
    @Query("SELECT * FROM daily_logs WHERE habitId IN (:habitIds) AND date = :date")
    suspend fun getLogsForHabitsOnDate(habitIds: List<String>, date: String): List<DailyLogEntity>
}
```

#### ListItemDao
```kotlin
@Dao
interface ListItemDao {
    @Query("SELECT * FROM list_items WHERE habitId = :habitId ORDER BY orderIndex ASC")
    fun getListItemsForHabit(habitId: String): Flow<List<ListItemEntity>>
    
    @Query("SELECT * FROM list_items WHERE habitId = :habitId AND type = :type ORDER BY orderIndex ASC")
    fun getListItemsByType(habitId: String, type: String): Flow<List<ListItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListItems(items: List<ListItemEntity>)
    
    @Update
    suspend fun updateListItem(item: ListItemEntity)
    
    @Delete
    suspend fun deleteListItem(item: ListItemEntity)
    
    @Query("DELETE FROM list_items WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: String)
}
```

### Template Data

Create a `HabitTemplateRepository` with hardcoded templates (no database):

```kotlin
object HabitTemplates {
    
    val breakTemplates = listOf(
        HabitTemplate(
            id = "break_porn",
            name = "No Porn/Masturbation",
            type = HabitType.BREAK,
            category = "Health & Wellbeing",
            iconEmoji = "🚫",
            description = "Break free from pornography and compulsive masturbation",
            defaultResistanceItems = listOf(
                "My sleep quality will suffer tonight",
                "I'll struggle to wake up for Fajr/morning routine",
                "I'm depleting zinc and other nutrients I've worked to build",
                "Brain fog will affect my focus tomorrow",
                "I'll feel less confident and more ashamed",
                "I'll be more likely to objectify people around me",
                "This version of me isn't who I want to become",
                "Every time I give in, the habit gets stronger"
            ),
            defaultFrictionStrategies = listOf(
                "Install a website blocker (Cold Turkey, BlockSite)",
                "Have a friend set the blocker password",
                "Keep devices out of bedroom",
                "Never use phone/laptop in bed",
                "Log out of all accounts after each session",
                "Unfollow triggering accounts on social media"
            ),
            defaultTriggerContexts = listOf(
                "Late at night alone",
                "Bored with nothing to do",
                "After seeing triggering content online",
                "Feeling stressed or anxious"
            )
        ),
        
        HabitTemplate(
            id = "break_smoking",
            name = "Quit Smoking",
            type = HabitType.BREAK,
            category = "Health",
            iconEmoji = "🚬",
            description = "Quit smoking cigarettes or vaping",
            defaultResistanceItems = listOf(
                "Each cigarette costs me money — that's adding up fast",
                "My lungs are healing; this would set them back",
                "The craving will pass in 3 minutes whether I smoke or not",
                "I'll smell like smoke and people will notice",
                "My teeth and skin are improving; why reverse that?",
                "I'm just feeding a chemical addiction, not a real need",
                "The \"relief\" is just ending withdrawal I caused"
            ),
            defaultFrictionStrategies = listOf(
                "Don't buy cigarettes; never have them at home",
                "Avoid smoking areas and smoking friends initially",
                "Replace the hand-to-mouth motion (toothpick, carrot)",
                "Wait 10 minutes before giving in; cravings pass",
                "Calculate money saved and put it in visible jar"
            )
        ),
        
        HabitTemplate(
            id = "break_doomscrolling",
            name = "No Doomscrolling",
            type = HabitType.BREAK,
            category = "Productivity",
            iconEmoji = "📱",
            description = "Break the endless scroll habit",
            defaultResistanceItems = listOf(
                "I'm trading real life for curated highlight reels",
                "This content is designed to be addictive, not valuable",
                "I'll feel worse about myself afterward, not better",
                "Time spent here is time not spent on my goals",
                "The algorithm is optimizing for engagement, not my wellbeing"
            ),
            defaultFrictionStrategies = listOf(
                "Set app time limits (Screen Time, Digital Wellbeing)",
                "Remove social apps from home screen",
                "Log out after each use",
                "Grayscale your phone display",
                "Put phone in another room while working"
            )
        ),
        
        HabitTemplate(
            id = "break_oversleeping",
            name = "Stop Oversleeping",
            type = HabitType.BREAK,
            category = "Productivity",
            iconEmoji = "😴",
            description = "Stop hitting snooze and wasting mornings",
            defaultResistanceItems = listOf(
                "I'll feel groggier from fragmented sleep, not better",
                "My morning routine gets rushed or skipped",
                "I miss the quiet productive hours of early morning",
                "Hitting snooze trains my brain that alarms don't matter",
                "I'll feel behind all day"
            ),
            defaultFrictionStrategies = listOf(
                "Put alarm across the room",
                "Use an app that requires activity to dismiss (Alarmy)",
                "Set coffee maker to auto-brew at wake time",
                "Have tomorrow's clothes laid out",
                "Open blinds before bed (natural light wakes you)"
            )
        ),
        
        HabitTemplate(
            id = "break_alcohol",
            name = "Reduce Alcohol",
            type = HabitType.BREAK,
            category = "Health",
            iconEmoji = "🍺",
            description = "Reduce or eliminate alcohol consumption",
            defaultResistanceItems = listOf(
                "Alcohol disrupts my sleep architecture",
                "Tomorrow's productivity will be lower",
                "I'll feel dehydrated and sluggish",
                "It's empty calories undermining my fitness goals",
                "Drunk words are sober thoughts — I might regret something",
                "The buzz fades quickly but the effects last all night"
            ),
            defaultFrictionStrategies = listOf(
                "Don't keep alcohol at home",
                "Order first at restaurants (water/mocktail)",
                "Tell friends you're taking a break",
                "Have a go-to non-alcoholic drink ready"
            )
        ),
        
        HabitTemplate(
            id = "break_nailbiting",
            name = "Stop Nail Biting",
            type = HabitType.BREAK,
            category = "Personal Care",
            iconEmoji = "💅",
            description = "Stop biting nails and cuticles",
            defaultResistanceItems = listOf(
                "My nails will look damaged and unprofessional",
                "I'm transferring bacteria from hands to mouth",
                "I'll regret this when I see my fingers later",
                "This is anxiety manifesting physically — address the root"
            ),
            defaultFrictionStrategies = listOf(
                "Apply bitter nail polish",
                "Keep nails trimmed very short",
                "Wear bandaids on fingertips",
                "Hold something when anxious (stress ball, pen)",
                "Get a manicure (investment makes you protective)"
            )
        )
    )
    
    val buildTemplates = listOf(
        HabitTemplate(
            id = "build_exercise",
            name = "Daily Exercise",
            type = HabitType.BUILD,
            category = "Health",
            iconEmoji = "🏃",
            description = "Build a consistent exercise routine",
            defaultMinimumVersion = "Put on my workout clothes",
            defaultStackAnchors = listOf(
                "After I pour my morning coffee",
                "After I drop kids at school",
                "After I finish work"
            ),
            defaultAttractionItems = listOf(
                "I'll feel energized and accomplished after",
                "This is building the body I want to see",
                "Exercise clears my mind better than anything",
                "I'm becoming someone who doesn't skip workouts",
                "Even 10 minutes counts — just start"
            ),
            defaultRewards = listOf(
                "Post-workout smoothie",
                "10 minutes of guilt-free relaxation",
                "Check off the calendar (streak satisfaction)",
                "Sauna or hot shower"
            )
        ),
        
        HabitTemplate(
            id = "build_reading",
            name = "Daily Reading",
            type = HabitType.BUILD,
            category = "Personal Growth",
            iconEmoji = "📖",
            description = "Read books instead of screens",
            defaultMinimumVersion = "Read one page",
            defaultStackAnchors = listOf(
                "After I get into bed",
                "During my lunch break",
                "After my morning coffee"
            ),
            defaultAttractionItems = listOf(
                "Reading makes me more interesting and knowledgeable",
                "Each page is compound interest on my mind",
                "This is how successful people spend their time",
                "Books are conversations with the smartest people ever"
            ),
            defaultRewards = listOf(
                "Track pages/books completed",
                "Cozy beverage while reading",
                "Share interesting quotes with friends"
            )
        ),
        
        HabitTemplate(
            id = "build_fajr",
            name = "Fajr at Mosque",
            type = HabitType.BUILD,
            category = "Spiritual",
            iconEmoji = "🕌",
            description = "Attend Fajr prayer at the mosque consistently",
            defaultMinimumVersion = "Get out of bed at first alarm",
            defaultStackAnchors = listOf(
                "When I hear the adhan",
                "After my pre-dawn alarm"
            ),
            defaultAttractionItems = listOf(
                "The barakah of praying in congregation (27x reward)",
                "Starting the day with purpose and gratitude",
                "The peace of the mosque at dawn",
                "Joining a community of committed believers",
                "My day goes better when I start with Fajr"
            ),
            defaultRewards = listOf(
                "Peaceful walk back as the sun rises",
                "Breakfast after (earned, not just consumed)",
                "The streak itself becomes sacred"
            )
        ),
        
        HabitTemplate(
            id = "build_quran",
            name = "Quran Memorization",
            type = HabitType.BUILD,
            category = "Spiritual",
            iconEmoji = "📚",
            description = "Memorize Quran consistently",
            defaultMinimumVersion = "Review one ayah",
            defaultStackAnchors = listOf(
                "After Fajr prayer",
                "During commute",
                "Before bed"
            ),
            defaultAttractionItems = listOf(
                "Each ayah memorized is permanent reward",
                "The Quran will intercede for me",
                "I'm joining generations of huffaz",
                "My mind is sharper when engaged in memorization"
            ),
            defaultRewards = listOf(
                "Track ayahs/pages completed",
                "Recite to a friend or teacher",
                "Reach a milestone surah"
            )
        ),
        
        HabitTemplate(
            id = "build_meditation",
            name = "Daily Meditation",
            type = HabitType.BUILD,
            category = "Mental Health",
            iconEmoji = "🧘",
            description = "Build a meditation or mindfulness practice",
            defaultMinimumVersion = "Sit and take 3 deep breaths",
            defaultStackAnchors = listOf(
                "After I wake up",
                "After I finish work",
                "Before bed"
            ),
            defaultAttractionItems = listOf(
                "10 minutes of calm affects the whole day",
                "I'm training my mind like I train my body",
                "Stress doesn't control me when I meditate regularly",
                "This is the highest-leverage investment in my wellbeing"
            ),
            defaultRewards = listOf(
                "Cup of tea after",
                "Notice improved patience through the day"
            )
        ),
        
        HabitTemplate(
            id = "build_cooking",
            name = "Healthy Cooking",
            type = HabitType.BUILD,
            category = "Health",
            iconEmoji = "🥗",
            description = "Cook more, eat out less, choose healthier options",
            defaultMinimumVersion = "Prepare one healthy snack",
            defaultStackAnchors = listOf(
                "When I get home from work (before sitting down)",
                "Sunday afternoon (meal prep)"
            ),
            defaultAttractionItems = listOf(
                "I control exactly what goes into my body",
                "Cooking is a skill that impresses and serves others",
                "Home meals are cheaper AND healthier",
                "My energy levels are better with real food"
            ),
            defaultRewards = listOf(
                "Enjoy the meal you made",
                "Share food photos or with family",
                "Track money saved vs. eating out"
            )
        ),
        
        HabitTemplate(
            id = "build_deepwork",
            name = "Deep Work Block",
            type = HabitType.BUILD,
            category = "Productivity",
            iconEmoji = "💼",
            description = "Focused work without distractions",
            defaultMinimumVersion = "Clear desk and open project file",
            defaultStackAnchors = listOf(
                "After morning coffee",
                "After first meeting ends",
                "After lunch"
            ),
            defaultAttractionItems = listOf(
                "This is when my best work happens",
                "2 hours of deep work > 6 hours of shallow work",
                "My career advances through what I produce here",
                "Flow state is its own reward"
            ),
            defaultRewards = listOf(
                "Coffee break after the block",
                "Walk outside",
                "Check off high-priority task"
            )
        ),
        
        HabitTemplate(
            id = "build_journaling",
            name = "Daily Journaling",
            type = HabitType.BUILD,
            category = "Mental Health",
            iconEmoji = "📝",
            description = "Write to clarify thoughts and track growth",
            defaultMinimumVersion = "Write one sentence about today",
            defaultStackAnchors = listOf(
                "Before bed",
                "After morning coffee",
                "During lunch"
            ),
            defaultAttractionItems = listOf(
                "Writing clarifies my thinking",
                "I'm documenting my life and growth",
                "Problems feel smaller when written down",
                "Future me will thank me for these records"
            ),
            defaultRewards = listOf(
                "Read old entries occasionally",
                "Cup of tea while writing"
            )
        ),
        
        HabitTemplate(
            id = "build_water",
            name = "Drink Water",
            type = HabitType.BUILD,
            category = "Health",
            iconEmoji = "💧",
            description = "Stay hydrated throughout the day",
            defaultMinimumVersion = "Take one sip",
            defaultStackAnchors = listOf(
                "First thing after waking up",
                "After every bathroom break",
                "Before every meal"
            ),
            defaultAttractionItems = listOf(
                "Hydration improves energy and focus",
                "My skin looks better when hydrated",
                "Hunger is often thirst in disguise",
                "Water is the simplest health hack"
            ),
            defaultRewards = listOf(
                "Use a nice water bottle you enjoy",
                "Track glasses with a simple tally"
            )
        ),
        
        HabitTemplate(
            id = "build_gratitude",
            name = "Gratitude Practice",
            type = HabitType.BUILD,
            category = "Mental Health",
            iconEmoji = "🙏",
            description = "Cultivate gratitude daily",
            defaultMinimumVersion = "Think of one thing I'm grateful for",
            defaultStackAnchors = listOf(
                "Before bed",
                "During morning coffee",
                "After Fajr/morning prayer"
            ),
            defaultAttractionItems = listOf(
                "Gratitude rewires the brain for happiness",
                "I notice more good when I look for it",
                "Perspective improves my whole day",
                "This is the antidote to entitlement"
            ),
            defaultRewards = listOf(
                "Share one gratitude with someone",
                "Feel the warmth of appreciation"
            )
        )
    )
    
    val allTemplates = breakTemplates + buildTemplates
}
```

### Socratic Flow Questions

**BUILD Flow (6 Steps):**
```kotlin
sealed class BuildFlowStep {
    object HabitName : BuildFlowStep()        // "What habit do you want to build?"
    object TriggerTime : BuildFlowStep()      // "When during your day would this fit best?"
    object HabitStack : BuildFlowStep()       // "What existing habit could trigger this?"
    object Attractions : BuildFlowStep()      // "What would make this enjoyable?"
    object MinimumVersion : BuildFlowStep()   // "What's the absolute minimum version?"
    object Reward : BuildFlowStep()           // "How will you reward yourself?"
}
```

**BREAK Flow (6 Steps):**
```kotlin
sealed class BreakFlowStep {
    object HabitName : BreakFlowStep()        // "What habit do you want to break?"
    object TriggerTime : BreakFlowStep()      // "When does this usually happen?"
    object TriggerCues : BreakFlowStep()      // "What triggers the urge?"
    object Costs : BreakFlowStep()            // "What does this habit really cost you?"
    object Friction : BreakFlowStep()         // "What friction can you add?"
    object Accountability : BreakFlowStep()   // "Who could hold you accountable?"
}
```

### Navigation Graph

```kotlin
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object SignIn : Screen("sign_in")
    object Home : Screen("home")
    object HabitDetail : Screen("habit/{habitId}") {
        fun createRoute(habitId: String) = "habit/$habitId"
    }
    object AddHabitTypeSelection : Screen("add_habit/type")
    object AddHabitSocratic : Screen("add_habit/socratic/{type}") {
        fun createRoute(type: String) = "add_habit/socratic/$type"
    }
    object TemplateBrowser : Screen("templates/{type}") {
        fun createRoute(type: String) = "templates/$type"
    }
    object ResistanceList : Screen("habit/{habitId}/list") {
        fun createRoute(habitId: String) = "habit/$habitId/list"
    }
    object Settings : Screen("settings")
    object PartnerManagement : Screen("partners")
    object PartnerView : Screen("partner_view")
}
```

### Theme Specification

```kotlin
// Use Material 3 with these color seeds
val PrimaryColor = Color(0xFF4CAF50)       // Green - growth, success
val SecondaryColor = Color(0xFF2196F3)     // Blue - calm, trust
val ErrorColor = Color(0xFFE53935)         // Red - failure, warning
val SuccessColor = Color(0xFF4CAF50)       // Green - same as primary
val StreakColor = Color(0xFFFF9800)        // Orange - fire/streak

// Dark mode support is REQUIRED
// Use MaterialTheme.colorScheme throughout
```

### Animation Specifications

**Success Celebration:**
- Confetti burst from bottom center (1 second duration)
- Haptic feedback (subtle)
- Sound: success_ding.mp3
- Streak counter animates up (+1)

**Failure/Streak Break:**
- Screen dims slightly (0.3 alpha overlay)
- Streak counter animates down to 0 (number by number)
- Sound: streak_break.mp3
- Message appears: "Your streak resets, but your progress doesn't. Every day is a new chance."
- Haptic feedback (heavy)

**Card Interactions:**
- Press: Scale down to 0.97
- Release: Scale back to 1.0 with spring animation
- Loading states: Shimmer effect

---

## DEVELOPMENT PHASES

### Phase 1: Foundation (Week 1-2)
**Goal:** Project setup, database, basic navigation

**Tasks:**
1. Create Android project with all dependencies in build.gradle.kts
2. Set up Hilt dependency injection modules
3. Implement Room database with all entities, DAOs, type converters
4. Create repository interfaces (domain) and implementations (data)
5. Set up Navigation Compose with all screen routes
6. Implement Material 3 theme with light/dark mode
7. Create basic MainActivity with navigation host

**Deliverables:**
- [ ] Project compiles without errors
- [ ] Database schema complete
- [ ] Navigation works between empty placeholder screens
- [ ] Theme toggles between light/dark correctly

### Phase 2: Authentication (Week 3)
**Goal:** Google Sign-In and email authentication

**Tasks:**
1. Configure Firebase project and add google-services.json
2. Implement AuthService wrapper around Firebase Auth
3. Create SignIn screen with Google one-tap and email option
4. Implement email verification flow (magic link)
5. Create UserRepository and store user in Room
6. Handle auth state persistence (stay logged in)
7. Implement sign out functionality

**Deliverables:**
- [ ] Google Sign-In works
- [ ] Email sign-in works with verification
- [ ] User persists across app restarts
- [ ] Sign out clears session

### Phase 3: Core Habit CRUD (Week 4-5)
**Goal:** Create, read, update habits with basic flow

**Tasks:**
1. Implement Home screen with habit list
2. Create HabitCard component with streak display
3. Implement Add Habit Type Selection screen (BUILD vs BREAK)
4. Create basic habit creation (skip Socratic for now, just name + type)
5. Implement Habit Detail screen with calendar
6. Create StreakCalendar component showing month view
7. Implement mark success/failure with streak logic
8. Add celebration animation on success
9. Add streak break animation on failure

**Deliverables:**
- [ ] Can create habits
- [ ] Habits display on home screen
- [ ] Calendar shows success/failure days
- [ ] Streak increments on success
- [ ] Streak resets on failure with animation

### Phase 4: Socratic Flow (Week 6-7)
**Goal:** Full guided habit setup

**Tasks:**
1. Implement BUILD flow with 6 question screens
2. Implement BREAK flow with 6 question screens
3. Create resistance list from Step 4 answers (BREAK)
4. Create attraction list from Step 4 answers (BUILD)
5. Store all Socratic answers in habit entity
6. Implement back navigation preserving answers
7. Create ListItemRepository for resistance/attraction lists
8. Build Resistance/Attraction List viewer/editor screen

**Deliverables:**
- [ ] BUILD flow creates habit with all metadata
- [ ] BREAK flow creates habit with resistance list
- [ ] Can view and edit lists after creation
- [ ] Back navigation preserves progress

### Phase 5: Templates (Week 8)
**Goal:** Pre-built habits with evidence-based defaults

**Tasks:**
1. Create HabitTemplateRepository with all 20+ templates
2. Implement Template Browser screen with categories
3. Pre-populate Socratic flow from template defaults
4. Allow editing template defaults during setup
5. Mark template-originated items in list_items table

**Deliverables:**
- [ ] Can browse templates by category
- [ ] Selecting template pre-fills Socratic answers
- [ ] All answers editable
- [ ] 20+ templates available

### Phase 6: Notifications (Week 9)
**Goal:** Morning suggestions and evening check-ins

**Tasks:**
1. Create NotificationHelper for building notifications
2. Implement MorningReminderWorker with WorkManager
3. Implement EveningCheckinWorker with "All Good" action
4. Create notification permission request flow (after first habit)
5. Add notification time configuration in Settings
6. Handle "All Good" action to mark all habits success

**Deliverables:**
- [ ] Morning notification fires at configured time
- [ ] Evening notification fires with quick actions
- [ ] "All Good" marks all habits without opening app
- [ ] Notifications work when app is closed

### Phase 7: Widget (Week 10)
**Goal:** Home screen widget with temptation intervention

**Tasks:**
1. Create HabitWidget using Glance API (2x2 small widget)
2. Show habit name, streak, and "I'm Tempted" button
3. Create TemptationActivity for overlay
4. Display resistance list in overlay
5. Implement "I'll Stay Strong" (close overlay)
6. Implement "I Failed Today" (mark failure, update widget)
7. Update widget when habit status changes

**Deliverables:**
- [ ] Widget can be added to home screen
- [ ] "I'm Tempted" shows resistance list
- [ ] Actions update habit and widget
- [ ] Widget reflects current streak

### Phase 8: Partners (Week 11)
**Goal:** Accountability partner sharing

**Tasks:**
1. Create Partnership entity and DAO
2. Implement invite link generation (deep link)
3. Create Partner Management screen
4. Handle incoming invite links
5. Implement read-only Partner View screen
6. Selective habit sharing (user chooses which)
7. Revoke partner access functionality
8. CRITICAL: Ensure resistance lists are NEVER visible to partners

**Deliverables:**
- [ ] Can generate and share invite link
- [ ] Partner can accept and view habits
- [ ] Only selected habits visible
- [ ] Resistance lists hidden from partners
- [ ] Can revoke access

### Phase 9: Polish (Week 12-13)
**Goal:** Production-ready quality

**Tasks:**
1. Implement Settings screen (notifications, account, data export)
2. Add risk warning when adding 6th+ habit
3. Implement data export (JSON)
4. Add delete account functionality
5. Create Onboarding slides (3 screens)
6. Create Splash screen with branding
7. Implement offline error handling (no error messages when offline)
8. Performance optimization (lazy loading, image caching)
9. Accessibility pass (content descriptions, touch targets)
10. Write unit tests for use cases
11. Write UI tests for critical flows
12. Configure ProGuard rules
13. Prepare for Play Store (privacy policy, screenshots)

**Deliverables:**
- [ ] All settings functional
- [ ] Data export works
- [ ] Account deletion works
- [ ] App works completely offline
- [ ] Tests pass
- [ ] Ready for Play Store submission

---

## FILE MAINTENANCE REQUIREMENTS

### 1. Contextlog.md
Update at the START and END of each work session with:
- Completed tasks
- In-progress work
- Next up (priority order)
- Any blockers

### 2. requirements.md
Document any setup the user must complete:
- Firebase project configuration
- google-services.json placement
- Sound file locations
- Signing keys for release

### 3. decisionlog.md
Log significant technical decisions:
- Library choices and rationale
- Architecture deviations from spec
- Bug fix approaches
- Performance optimizations

### 4. Project-summary.md
Keep updated with:
- Architecture overview
- Key component descriptions
- File structure
- Getting started guide

---

## ANTI-PATTERNS & WARNINGS

### ⛔ FORBIDDEN PATTERNS

**Architecture:**
- God ViewModels that manage multiple screens
- Direct Room calls from ViewModels (use repositories)
- Business logic in Composables
- Circular dependencies between modules

**Code:**
- Copy-pasting Composables (extract to components/)
- Magic strings for routes (use sealed class Screen)
- Hardcoded colors (use MaterialTheme.colorScheme)
- Silent exception swallowing (log errors, show user feedback)

**Database:**
- Missing indexes on queried columns (userId, habitId, date)
- N+1 queries (use @Transaction and eager loading)
- String concatenation in queries

**Security:**
- Storing Firebase credentials in code
- Logging sensitive data (resistance lists)
- Exposing resistance lists via partner features

**UX:**
- Punitive language ("You failed", "Streak lost")
- Blocking UI during database operations
- Missing loading states
- Ignoring offline scenarios

---

## QUALITY GATES

### Phase Completion Checklist

**Before marking a phase complete:**
- [ ] All acceptance criteria from PRD met
- [ ] No compiler warnings
- [ ] No crashes on happy path
- [ ] Works in offline mode
- [ ] Dark mode looks correct
- [ ] Contextlog.md updated
- [ ] decisionlog.md has new decisions

### Pre-Release Checklist

- [ ] All 13 weeks of development complete
- [ ] Unit test coverage >80% on use cases
- [ ] UI tests pass for critical flows
- [ ] No crashes in 100 test launches
- [ ] All features work offline
- [ ] ProGuard configured and tested
- [ ] Privacy policy written
- [ ] Play Store listing prepared

---

## APPENDIX: Key Acceptance Criteria

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

**END OF SYSTEM INSTRUCTIONS**

*This document is the source of truth for Claude Code. Follow it precisely.*
