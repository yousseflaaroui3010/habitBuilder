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

## Quick Reference: Key Decisions

| ID | Decision | Reversal |
|----|----------|----------|
| DEC-001 | Kotlin + Compose + Room | Hard |
| DEC-002 | Offline-first, no sync V1 | Medium |
| DEC-003 | Firebase Auth only | Medium |
| DEC-004 | Glance API for widgets | Medium |
| DEC-005 | WorkManager for notifications | Easy |
| DEC-006 | No gamification/points | Easy |
