# Progress

## Issue #4: "I Failed Today" - Requirements
- Mark habit as FAILED for today
- Update home page UI immediately
- Update progress/streak
- Play break habit sound
- Trigger failure procedure (notify partner)

---

# HABIT FAILURE PROTOCOL (Brainstorm)

## BUILD Habit (e.g., "Exercise daily")

### When user clicks X (Failed/Skipped):
1. **Immediate Actions**
   - Mark day as FAILED in daily_logs
   - Reset current streak to 0
   - Increment totalFailureDays
   - Remove 2 paper clips (punishment)
   - Play streak break sound

2. **Visual Feedback**
   - Show streak break animation
   - Update weekly dots (red X)
   - Update paper clip jar (animate removal)

3. **Consequences**
   - If shared with partner → Send notification/email
   - Log failure reason (optional prompt)
   - Show motivational recovery message

4. **Progress Dashboard Impact**
   - Failure appears in calendar view
   - Success rate % decreases
   - Streak history updated
   - Weekly/monthly stats affected

---

## BREAK Habit (e.g., "Stop smoking")

### When user clicks X (I Failed Today):
1. **Immediate Actions**
   - Mark day as FAILED (relapsed)
   - Reset current streak to 0
   - Increment totalFailureDays
   - Remove 2 paper clips (punishment)
   - Play relapse sound

2. **Visual Feedback**
   - Show "relapse" animation
   - Update weekly dots (red X)
   - Paper clip jar decreases

3. **Consequences**
   - If shared with partner → Send alert/email
   - Prompt: "What triggered this?"
   - Show cost reminder (what this costs you)
   - Offer recovery tips

4. **Progress Dashboard Impact**
   - Relapse logged with timestamp
   - "Days clean" resets
   - Pattern detection: "You relapse on Fridays"
   - Cost accumulator (e.g., "$50 spent this month")

---

## FAILURE PROCEDURE (Both Types)

```
User clicks X
    ↓
markFailure(habitId)
    ↓
├── dailyLogRepository.markStatus(FAILURE)
├── habitRepository.resetStreak()
├── habitRepository.removePaperClips(2)
├── soundManager.playStreakBreakSound()
├── Show animation
    ↓
Check if shared with partner?
    ↓
YES → partnershipRepository.notifyPartner(failure)
    ↓
Refresh UI (home + progress)
```

---

## Pending Issues

| # | Issue | Status |
|---|-------|--------|
| 4 | I failed today button | Working on it |
| 2 | Cost input UX | Pending |
