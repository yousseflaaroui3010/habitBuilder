# Decision Log — Habit Architect

## Purpose

This log tracks all significant technical decisions, trade-offs, and pivots made during development. Essential for debugging, maintenance, and future development.

---

## Decision Log

### [DEC-001] Technology Stack Selection
**Date:** Pre-development (from PRD)  
**Category:** Architecture

**Context:**  
Selecting the core technology stack for an offline-first Android habit tracking app with sensitive personal data.

**Options Considered:**
1. **Kotlin + Jetpack Compose + Room** — Modern Android stack
   - Pros: Official Google support, declarative UI, type-safe, great tooling
   - Cons: Compose still evolving, learning curve for XML developers

2. **React Native** — Cross-platform
   - Pros: Cross-platform, large community
   - Cons: Performance overhead, native module complexity for widgets/notifications

3. **Flutter** — Cross-platform
   - Pros: Great performance, beautiful UI
   - Cons: Less mature Android integration for widgets, different paradigm

**Decision:**  
Kotlin + Jetpack Compose + Room

**Consequences:**
- Positive: Best-in-class Android experience, official widget support (Glance), seamless WorkManager integration
- Negative: Android-only for V1 (iOS requires separate codebase)
- Technical Debt: None — this is the recommended modern Android stack

**Reversal Difficulty:** Hard (complete rewrite required)

---

### [DEC-002] Offline-First with Room (No Backend Sync V1)
**Date:** Pre-development (from PRD)  
**Category:** Architecture

**Context:**  
Deciding whether to build backend sync infrastructure for V1 or defer to V2.

**Options Considered:**
1. **Offline-first, sync later** — Room as single source of truth
   - Pros: Simpler architecture, works without internet, faster development
   - Cons: No cross-device sync, data loss if device lost

2. **Backend from day one** — Firebase Firestore or custom backend
   - Pros: Cross-device sync, backup, partner sharing easier
   - Cons: More complex, internet dependency, higher cost, longer timeline

**Decision:**  
Offline-first with Room. Backend sync deferred to V2.

**Consequences:**
- Positive: 100% offline functionality, simpler architecture, faster time-to-market
- Negative: No cross-device sync, partner sharing requires device-to-device mechanism
- Technical Debt: Partner feature will need redesign for proper sync in V2

**Reversal Difficulty:** Medium (sync layer can be added on top of Room)

---

### [DEC-003] Firebase Auth Only (No Custom Auth)
**Date:** Pre-development (from PRD)  
**Category:** Security

**Context:**  
Authentication approach for user accounts.

**Options Considered:**
1. **Firebase Auth** — Managed authentication service
   - Pros: No password storage, Google Sign-In built-in, email verification handled
   - Cons: Firebase dependency, limited customization

2. **Custom auth backend** — Self-managed JWT/OAuth
   - Pros: Full control, no vendor lock-in
   - Cons: Security risk, more code, must handle password hashing/storage

**Decision:**  
Firebase Auth with Google Sign-In and Email/Password.

**Consequences:**
- Positive: Secure by default, Google one-tap UX, no password leaks possible
- Negative: Firebase dependency (acceptable for auth-only usage)
- Technical Debt: None

**Reversal Difficulty:** Medium (would require custom backend)

---

### [DEC-004] Glance API for Widgets (Not RemoteViews)
**Date:** Pre-development (from PRD)  
**Category:** UI/UX

**Context:**  
Widget implementation approach for Android home screen widgets.

**Options Considered:**
1. **Glance API** — Compose-based widgets
   - Pros: Compose syntax, type-safe, modern API
   - Cons: Newer API (1.0), different from regular Compose

2. **RemoteViews** — Traditional widget approach
   - Pros: Mature, well-documented
   - Cons: XML-based, limited components, harder to maintain alongside Compose

**Decision:**  
Glance API for all widgets.

**Consequences:**
- Positive: Consistent with Compose architecture, cleaner code
- Negative: Less documentation/examples available
- Technical Debt: None

**Reversal Difficulty:** Medium (would require rewriting widget layer)

---

### [DEC-005] WorkManager for Notifications (Not AlarmManager)
**Date:** Pre-development (from PRD)  
**Category:** Background Processing

**Context:**  
Scheduling daily morning and evening notifications reliably.

**Options Considered:**
1. **WorkManager** — Jetpack background processing
   - Pros: Battery-friendly, handles Doze mode, guaranteed execution
   - Cons: Not exact timing (can be delayed)

2. **AlarmManager with exact alarms** — Precise timing
   - Pros: Exact timing
   - Cons: Requires SCHEDULE_EXACT_ALARM permission (Android 12+), battery drain

**Decision:**  
WorkManager with periodic work requests.

**Consequences:**
- Positive: Reliable delivery even in Doze mode, no special permissions
- Negative: Notifications may be slightly delayed (acceptable for habit reminders)
- Technical Debt: None

**Reversal Difficulty:** Easy (can switch to AlarmManager if exact timing needed)

---

### [DEC-006] No Gamification/Points System
**Date:** Pre-development (from PRD)  
**Category:** Product

**Context:**  
Whether to include XP, levels, leaderboards, or other gamification elements.

**Options Considered:**
1. **Include gamification** — Points, levels, badges, leaderboards
   - Pros: Can increase engagement, familiar pattern
   - Cons: Can trivialize serious struggles, creates anxiety, unhealthy competition

2. **Minimal gamification** — Streaks and celebrations only
   - Pros: Respects serious context (addiction recovery), focuses on intrinsic motivation
   - Cons: Might feel "boring" to some users

**Decision:**  
Streaks and celebration animations only. No XP, levels, or leaderboards.

**Consequences:**
- Positive: Appropriate for sensitive habits (porn addiction, etc.), less anxiety
- Negative: May feel less "fun" than gamified competitors
- Technical Debt: None (gamification can be added later if desired)

**Reversal Difficulty:** Easy (additive feature)

---

## Template for New Decisions

```markdown
### [DEC-XXX] [Short Title]
**Date:** [YYYY-MM-DD]  
**Category:** Architecture / Code / Data / Security / Integration / Bug Fix / Performance / Product

**Context:**  
What situation or problem prompted this decision?

**Options Considered:**
1. Option A — Pros: ... / Cons: ...
2. Option B — Pros: ... / Cons: ...

**Decision:**  
What was chosen and why?

**Consequences:**
- Positive: What benefits does this bring?
- Negative: What trade-offs were accepted?
- Technical Debt: Any debt incurred?

**Reversal Difficulty:** Easy / Medium / Hard
```

---

---

### [DEC-007] SoundPool over MediaPlayer for Sound Effects
**Date:** 2025-12-31
**Category:** Performance

**Context:**
Need to play short sound effects (success ding, streak break) with low latency on habit actions.

**Options Considered:**
1. **SoundPool** — Pre-loaded audio pool
   - Pros: Low latency (<10ms), multiple simultaneous sounds, efficient memory
   - Cons: Limited to short sounds (<1MB), requires pre-loading

2. **MediaPlayer** — Full-featured player
   - Pros: Handles any audio length/format, streaming support
   - Cons: Higher latency (~200ms), heavier resource usage

**Decision:**
SoundPool for all habit sound effects.

**Consequences:**
- Positive: Instant feedback on habit actions, minimal battery/memory impact
- Negative: Would need MediaPlayer if longer audio needed (not a concern)
- Technical Debt: None

**Reversal Difficulty:** Easy (swap implementation in SoundManager)

---

### [DEC-008] SharedFlow for One-Time UI Events
**Date:** 2025-12-31
**Category:** Architecture

**Context:**
HomeViewModel needs to trigger one-time events (launch activity, show celebration) without replay on configuration changes.

**Options Considered:**
1. **SharedFlow** — Hot flow that doesn't replay
   - Pros: No replay on rotation, type-safe, coroutine-native
   - Cons: Slightly more complex than LiveData

2. **SingleLiveEvent (LiveData wrapper)** — Traditional approach
   - Pros: Familiar pattern
   - Cons: Not native to Compose, custom implementation needed

3. **Channel** — Conflated channel for events
   - Pros: Guaranteed delivery
   - Cons: Can lose events if not collected

**Decision:**
MutableSharedFlow with default replay=0 for UI events.

**Consequences:**
- Positive: Clean separation of state (StateFlow) vs events (SharedFlow)
- Negative: Must ensure collection is active (handled with LaunchedEffect)
- Technical Debt: None

**Reversal Difficulty:** Easy (pattern is localized to ViewModel/Screen pairs)

---

### [DEC-009] Glance Widget State via DataStore Preferences
**Date:** 2025-12-31
**Category:** Data

**Context:**
Glance widgets can't directly inject Hilt dependencies or easily access Room. Need a way to pass habit data to widget.

**Options Considered:**
1. **DataStore Preferences** — Built into Glance
   - Pros: Native Glance support, simple key-value storage, auto-updates widget
   - Cons: Denormalized data, manual sync with database

2. **Direct Room access** — Query database from widget
   - Pros: Always fresh data
   - Cons: Complex context handling, potential main thread issues

3. **Broadcast data** — Send data via broadcast to widget
   - Pros: Decoupled
   - Cons: More complex, Android restrictions on implicit broadcasts

**Decision:**
DataStore Preferences with database sync on widget update.

**Consequences:**
- Positive: Simple, reliable, works with Glance API naturally
- Negative: Data could be stale until next widget update (30 min default)
- Technical Debt: Could add explicit widget update on habit status change

**Reversal Difficulty:** Medium (would need to refactor widget data flow)

---

### [DEC-010] LocalDate for DailyLog Date Field
**Date:** 2025-12-31
**Category:** Data

**Context:**
Initially assumed DailyLog.date was a Long timestamp, but domain model uses LocalDate. Calendar component needed to map dates.

**Options Considered:**
1. **Use LocalDate directly** — Domain model already has it
   - Pros: Clean, no conversion needed
   - Cons: None

2. **Convert to Long** — Milliseconds since epoch
   - Pros: Database-friendly primitive
   - Cons: Conversion overhead, timezone issues

**Decision:**
Keep LocalDate in domain model, Room TypeConverter handles storage.

**Consequences:**
- Positive: Clean calendar mapping with associateBy { it.date }
- Negative: None
- Technical Debt: None

**Reversal Difficulty:** Hard (would affect entire data layer)

---

### [DEC-011] Removed Debug Application ID Suffix
**Date:** 2025-12-31
**Category:** Build Configuration

**Context:**
Firebase google-services.json was configured for `com.habitarchitect` but debug builds were getting `com.habitarchitect.debug` due to applicationIdSuffix.

**Options Considered:**
1. **Remove suffix** — Same package for debug/release
   - Pros: Firebase works, simpler configuration
   - Cons: Can't install debug and release side-by-side

2. **Add debug package to Firebase** — Register both packages
   - Pros: Can install both versions
   - Cons: More Firebase configuration, larger google-services.json

**Decision:**
Remove applicationIdSuffix for debug builds.

**Consequences:**
- Positive: Firebase auth works immediately
- Negative: Can't have debug and release installed simultaneously
- Technical Debt: Could add second Firebase app registration if needed

**Reversal Difficulty:** Easy (add suffix back and register in Firebase)

---

### [DEC-012] Disabled Gradle Configuration Cache
**Date:** 2025-12-31
**Category:** Build Configuration

**Context:**
Gradle configuration cache was causing JdkImageTransform/jlink errors during build.

**Options Considered:**
1. **Disable configuration cache** — Set to false in gradle.properties
   - Pros: Build works immediately
   - Cons: Slightly slower incremental builds

2. **Debug the cache issue** — Find root cause
   - Pros: Faster builds with cache
   - Cons: Time-consuming, may be Gradle/JDK version mismatch

**Decision:**
Disable configuration cache for stability.

**Consequences:**
- Positive: Build works reliably
- Negative: ~10-20% slower incremental builds (acceptable)
- Technical Debt: Could re-enable after updating Gradle/AGP

**Reversal Difficulty:** Easy (toggle in gradle.properties)

---

## Quick Reference: Key Decisions

| ID | Decision | Reversal |
|----|----------|----------|
| DEC-001 | Kotlin + Compose + Room | Hard |
| DEC-002 | Offline-first, no sync V1 | Medium |
| DEC-003 | Firebase Auth only | Medium |
| DEC-004 | Glance API for widgets | Medium |
| DEC-005 | WorkManager for notifications | Easy |
| DEC-006 | No gamification/points | Easy |
| DEC-007 | SoundPool for sound effects | Easy |
| DEC-008 | SharedFlow for UI events | Easy |
| DEC-009 | Widget state via DataStore | Medium |
| DEC-010 | LocalDate for DailyLog | Hard |
| DEC-011 | No debug app ID suffix | Easy |
| DEC-012 | Disabled config cache | Easy |
| DEC-013 | FileProvider for export sharing | Easy |
| DEC-014 | Animatable for streak break | Easy |

---

### [DEC-013] FileProvider for Data Export Sharing
**Date:** 2025-12-31
**Category:** Security

**Context:**
Need to share exported JSON file with other apps (email, cloud storage) from cache directory.

**Options Considered:**
1. **FileProvider** — Content URI approach
   - Pros: Secure, works with Android's scoped storage, temporary permission grant
   - Cons: Requires XML configuration

2. **External storage** — Write to Downloads folder
   - Pros: User can access directly
   - Cons: Requires WRITE_EXTERNAL_STORAGE permission, less secure

**Decision:**
FileProvider with cache-path for exports directory.

**Consequences:**
- Positive: Secure sharing via Intent.ACTION_SEND, no special permissions needed
- Negative: File only accessible through share intent
- Technical Debt: None

**Reversal Difficulty:** Easy (file saving location change)

---

### [DEC-014] Animatable for Streak Break Counter Animation
**Date:** 2025-12-31
**Category:** UI/UX

**Context:**
Streak break animation needs to show counter counting down from previous streak to 0.

**Options Considered:**
1. **Animatable + LaunchedEffect** — Compose animation primitives
   - Pros: Full control, coroutine-based, integrates with Compose lifecycle
   - Cons: More code than pre-built animations

2. **AnimatedContent** — Built-in number animation
   - Pros: Simpler API
   - Cons: Less control over timing and intermediate values

**Decision:**
Animatable with linear easing for smooth countdown effect.

**Consequences:**
- Positive: Smooth animation with variable duration based on streak size
- Negative: None
- Technical Debt: None

**Reversal Difficulty:** Easy (localized to StreakBreakAnimation component)

---

### [DEC-015] Deep Link Navigation via Splash Screen
**Date:** 2025-12-31
**Category:** Architecture

**Context:**
Need to handle partner invite deep links that arrive before user is authenticated.

**Options Considered:**
1. **Splash screen redirect** — Pass invite code through splash, redirect after auth
   - Pros: Works with existing auth flow, clean separation
   - Cons: Requires state passing through multiple screens

2. **Global deep link handler** — Intercept at NavHost level
   - Pros: Centralized handling
   - Cons: May conflict with auth state, more complex

**Decision:**
Pass invite code from MainActivity to NavHost, then redirect from splash screen after auth check.

**Consequences:**
- Positive: Invite processed after user confirmed authenticated
- Negative: Deep link not immediately visible (must wait for splash)
- Technical Debt: None

**Reversal Difficulty:** Medium (would need to refactor NavHost structure)
