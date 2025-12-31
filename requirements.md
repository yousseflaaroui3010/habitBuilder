# Setup Requirements — Habit Architect

## ⚠️ USER ACTION REQUIRED

These items require manual intervention before/during development. Claude Code cannot complete these automatically.

---

## 🔧 ENVIRONMENT SETUP

### Required Installations

- [ ] **Android Studio** — Hedgehog (2023.1.1) or newer
  - Download: https://developer.android.com/studio
  - Ensure Android SDK 34 is installed via SDK Manager

- [ ] **JDK 17** — Required for Gradle builds
  - Android Studio bundles this, but verify in Settings → Build → Gradle → JDK

- [ ] **Git** — For version control
  - Download: https://git-scm.com/downloads

### SDK/API Setup

- [ ] **Firebase Project** — Create a new Firebase project
  1. Go to https://console.firebase.google.com
  2. Click "Add Project" → Name it "Habit Architect"
  3. Enable Google Analytics (optional but recommended)
  4. Wait for project creation

- [ ] **Firebase Android App Registration**
  1. In Firebase Console → Project Settings → Add App → Android
  2. Package name: `com.habitarchitect`
  3. App nickname: "Habit Architect"
  4. SHA-1 (for Google Sign-In):
     ```bash
     # Debug SHA-1 (for development)
     keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
     ```
  5. Download `google-services.json`

- [ ] **Enable Firebase Authentication**
  1. Firebase Console → Authentication → Get Started
  2. Sign-in method → Enable "Google"
  3. Sign-in method → Enable "Email/Password"

### File Placements

- [ ] **google-services.json**
  - Download from Firebase Console
  - Place in: `app/google-services.json`
  - ⚠️ Do NOT commit to public repos (add to .gitignore)

- [ ] **Sound Files** (Create or obtain)
  - `app/src/main/res/raw/success_ding.mp3` — Short pleasant "ding" sound
  - `app/src/main/res/raw/streak_break.mp3` — Chain breaking sound effect
  - Free sources: https://freesound.org, https://pixabay.com/sound-effects/

---

## 🔑 API KEYS & CREDENTIALS

### Firebase (Handled by google-services.json)
- No manual API keys needed — `google-services.json` contains all required configuration

### Google Sign-In OAuth
- [ ] Configure OAuth consent screen (Firebase Console → Authentication → Sign-in method → Google)
  - App name: "Habit Architect"
  - Support email: Your email
  - No scopes needed beyond basic profile

### Play Store (For Release)
- [ ] **Signing Key** — Generate a release keystore
  ```bash
  keytool -genkey -v -keystore habit-architect-release.keystore -alias habit-architect -keyalg RSA -keysize 2048 -validity 10000
  ```
  - Store password and key password securely
  - Never commit keystore to version control

- [ ] **Play Console Account** — $25 one-time fee
  - https://play.google.com/console

---

## 📦 EXTERNAL DEPENDENCIES

All dependencies are managed via Gradle — no manual installation required.

### Dependency Versions (Reference)
```kotlin
// These are specified in build.gradle.kts
kotlin = "1.9.21"
compose = "1.5.4"
composeCompiler = "1.5.7"
room = "2.6.1"
hilt = "2.48.1"
navigation = "2.7.6"
workmanager = "2.9.0"
glance = "1.0.0"
firebase-auth = "22.3.0"
firebase-bom = "32.7.0"
```

---

## 🎨 ASSETS TO PROVIDE

### App Icon
- [ ] Provide app icon in multiple densities OR provide a 1024x1024 source image
  - Location: `app/src/main/res/mipmap-*/ic_launcher.png`
  - Can use Android Studio's Image Asset Studio to generate

### Splash Screen Logo
- [ ] Provide splash screen logo (optional — can use app icon)
  - Recommended: SVG or high-res PNG
  - Location: `app/src/main/res/drawable/splash_logo.xml` (vector) or PNG

### Onboarding Illustrations (Optional)
- [ ] 3 illustrations for onboarding slides
  - Recommended size: 400x400dp
  - Can be vector drawables or PNGs

---

## ✅ COMPLETED SETUP

_Move items here once confirmed complete_

---

## 📋 VERIFICATION CHECKLIST

Before starting development:
- [ ] Android Studio opens project without errors
- [ ] `./gradlew build` succeeds
- [ ] Emulator or physical device connected
- [ ] `google-services.json` is in `app/` directory
- [ ] Firebase project shows Android app registered
- [ ] Google Sign-In enabled in Firebase Auth
- [ ] Email/Password enabled in Firebase Auth

---

## 🆘 TROUBLESHOOTING

### "google-services.json not found"
- Ensure file is in `app/` directory (not project root)
- Check file name is exactly `google-services.json`

### Google Sign-In fails with error 10
- SHA-1 fingerprint mismatch
- Re-run keytool command and add SHA-1 to Firebase Console
- Remember: Debug and Release have different SHA-1s

### Gradle sync fails
- File → Invalidate Caches and Restart
- Delete `.gradle` and `build` folders, re-sync

### Room schema export error
- Add to `app/build.gradle.kts`:
  ```kotlin
  ksp {
      arg("room.schemaLocation", "$projectDir/schemas")
  }
  ```
