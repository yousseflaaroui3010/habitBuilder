# Progress

## Issue Status

| # | Fix | Status |
|---|-----|--------|
| 19 | Color contrast AA compliance | DONE - Ready for testing |
| 17 | Logo size + header layout | DONE - Ready for testing |
| 18 | FAB visibility (invisible bar) | DONE - Ready for testing |
| 13 | Break Habit / Build Habit UX | DONE - Ready for testing |
| 12 | Notifications | POSTPONED (deployment issue) |
| 4 | I Failed Today | MERGED |
| 3 | Tempted screen flashcards | MERGED |
| 2 | Cost input UX | MERGED |
| 1 | Weekly reflections summary | DONE - Ready for testing |

---

## Issue #1: Weekly Reflections Summary - DONE

### What Was Added

The Dashboard (Progress page) now shows a **summary of your weekly reflection** if you've written one:

- Shows "Went well", "Struggled with", "Learned" summaries
- Text is truncated to 100 chars with "..."
- Card shows "Tap to edit" when reflection exists
- Card shows "Reflect on your progress" when no reflection yet

### Files Changed

- **DashboardViewModel.kt** - Added `WeeklyReflectionDao` injection, loads current week's reflection
- **DashboardScreen.kt** - Updated `WeeklyReflectionCard` to show summary content

### Build Status

**BUILD SUCCESSFUL**

---

## Issue #13: Break Habit / Build Habit UX - DONE

### What Was Changed

**1. Templates as Default**
- Clicking BUILD or BREAK now goes directly to template browser
- Removed separate "Browse templates" buttons

**2. Custom Habit Option**
- Added "Create Custom Habit" card at top of template browser
- Users can still create non-template habits

**3. Break Habit Questions Improved**
- Changed from 6 questions to 5 focused questions:
  1. "What will you Stop?" (was "What habit do you want to break?")
  2. "What makes it Attractive?" (why you keep doing it)
  3. "What makes it Difficult to quit?" (barriers)
  4. "What makes it Visible/Easy?" (cues/triggers)
  5. "What does this cost you?" (consequences)
- Removed time/location questions (not relevant for break habits)

### Files Changed

- **AddHabitTypeSelectionScreen.kt** - Goes to templates by default
- **TemplateBrowserScreen.kt** - Added "Create Custom Habit" option
- **AddHabitViewModel.kt** - New break habit questions, saves answers as list items
- **NavGraph.kt** - Added onCreateCustom navigation

### Build Status

**BUILD SUCCESSFUL**

---

## Issue #18: FAB Visibility (Invisible Bar) - DONE

### What Was Changed

- Moved FAB (+ button) from `HomeContentScreen` to `MainScreen`
- FAB now visible on ALL tabs (Home, Progress, Identity, Settings)
- Previously only showed on Home tab

### Files Changed

- **MainScreen.kt** - Added FAB to Scaffold
- **HomeContentScreen.kt** - Removed duplicate FAB

### Build Status

**BUILD SUCCESSFUL**

---

## Issue #17 + #19: Logo/Header + Color Contrast - DONE

### #17: Header Reorganization

**What Was Changed:**
- Logo moved to top LEFT and made BIGGER (48dp height)
- Profile picture moved to top RIGHT
- Added theme toggle (sun/moon icon) next to profile
- Clicking theme icon toggles dark/light mode

### #19: Color Contrast AA Compliance

**What Was Fixed:**
- All colors now meet WCAG AA standards (4.5:1 for text, 3:1 for graphics)
- Fixed low-contrast colors:
  - `LightOnSurfaceVariant`: #49454F → #3D3846 (darker)
  - `LightOutline`: #79747E → #6B6574 (darker)
  - `DarkOnSurfaceVariant`: #CAC4D0 → #D4CED8 (lighter)
  - `DarkOutline`: #938F99 → #A8A2AC (lighter)
  - `CalendarPending`: #9E9E9E → #616161 (darker)
  - Calendar colors adjusted for better contrast

### Files Changed

- **HomeContentScreen.kt** - Reorganized TopAppBar layout
- **HomeViewModel.kt** - Added `toggleTheme()` function
- **Color.kt** - Updated all colors for AA compliance

### Build Status

**BUILD SUCCESSFUL**

---

## Next Issues

Remaining open issues:
- #22: Profile options
- #21: Guest Mode
- #20: I'm Tempted slides
- #16: Habit creation in home page
- #15: Widget Secrecy and Privacy
- #14: Progress Page
- #10: Break habit Protocol (complex)
