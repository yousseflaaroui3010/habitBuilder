# Habit Architect — Context Log

**Last Updated:** 2026-01-07 | **Repo:** github.com/yousseflaaroui3010/habitBuilder

---

## BRANCHES

### Ready to Merge
| Branch | Issue | Status |
|--------|-------|--------|
| HB-1-add-weekly-reflection-summary | #1 | Done |
| HB-13-modify-habit-creation-ux | #13 | Done |
| HB-18-modify-fab-visibility | #18 | Done |

### Needs Review
| Branch | Issue | Notes |
|--------|-------|-------|
| HB-17-19-modify-header-colors | #17+#19 | Still needs checking |

---

## CLOSED ISSUES
| # | Issue | Notes |
|---|-------|-------|
| 2 | Cost input UX | Done, works well |
| 3 | Tempted screen flashcards | Done, but: add manual slide back/forward |
| 4 | I Failed Today button | Done, but: needs confirmation popup |

---

## OPEN ISSUES
| # | Issue | Priority | Notes |
|---|-------|----------|-------|
| 10 | Break habit Protocol | Complex | |
| 12 | Notifications | Postponed | Needs AlarmManager |
| 14 | Progress Page | Medium | |
| 15 | Widget Privacy | Medium | |
| 16 | Habit creation in home | Low | |
| 20 | I'm Tempted slides | Medium | Add manual navigation |
| 21 | Guest Mode | Low | |
| 22 | Profile options | Low | |

---

## NEW ISSUES TO CREATE
| Issue | Description |
|-------|-------------|
| Failure confirmation | Add popup before marking habit as failed (protect streaks) |
| Flashcard navigation | Allow manual swipe forward/back on tempted slides |
| Top bar spacing | Minimize space above profile picture and logo |

---

## KNOWN ISSUES
| Issue | Status |
|-------|--------|
| PaperClipJar not visible | Component exists, needs UI integration |
| OnboardingScreen missing logo | Needs logo added |
| Notifications timing | WorkManager unreliable, needs AlarmManager |

---

## DATABASE
**Version:** 3

---

## BUILD
```bash
./gradlew assembleDebug
```
