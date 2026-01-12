# HabitArchitect Deployment Checklist

## Executive Summary
Minimum viable deployment for Play Store with ad monetization and future premium tier.

---

## 1. PLAY STORE REQUIREMENTS

### Developer Account
- [ ] Google Play Developer Account ($25 one-time fee)
- [ ] Business verification (if using company name)

### App Listing Assets
- [ ] App icon (512x512 PNG)
- [ ] Feature graphic (1024x500)
- [ ] Screenshots (min 2, phone + tablet recommended)
- [ ] Short description (80 chars)
- [ ] Full description (4000 chars)
- [ ] Privacy Policy URL (REQUIRED - hosted publicly)
- [ ] App category: Health & Fitness

### Technical Requirements
- [ ] Target SDK 34+ (current requirement)
- [ ] Signed release APK/AAB with upload key
- [ ] Version code strategy (increment each release)

---

## 2. AD MONETIZATION (AdMob)

### Setup
- [ ] Google AdMob account (FREE, linked to Google account)
- [ ] Create AdMob app → get App ID
- [ ] Create ad units → get Ad Unit IDs

### Recommended Ad Placements (Non-Intrusive)
| Placement | Ad Type | When |
|-----------|---------|------|
| Home screen bottom | Banner | Always visible |
| After habit completion | Interstitial | Every 5th success |
| Weekly reflection | Native | Embedded in content |

### Code Changes Required
```kotlin
// build.gradle.kts (app)
implementation("com.google.android.gms:play-services-ads:23.0.0")

// AndroidManifest.xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY"/>
```

### Cost: FREE (Google pays YOU)
- Banner: ~$0.10-0.50 per 1000 impressions
- Interstitial: ~$1-5 per 1000 impressions
- Expect: $50-500/month at 10K MAU

---

## 3. REMOVE ADS (In-App Purchase)

### Google Play Billing
```kotlin
implementation("com.android.billingclient:billing-ktx:6.1.0")
```

### Options
| Model | Price | Complexity |
|-------|-------|------------|
| One-time purchase | $2.99-4.99 | Simple |
| Monthly subscription | $0.99-1.99/mo | Medium |
| Yearly subscription | $9.99-14.99/yr | Medium |

**Recommendation:** Start with one-time purchase ($3.99) - simplest to implement.

### What to Store
- Purchase token (verify with Google Play)
- User premium status in Firestore: `users/{uid}/isPremium: true`

---

## 4. BACKEND CONSIDERATIONS

### Current Architecture (Firebase)
You're already using Firebase. Current costs at scale:

| Service | Free Tier | Cost After |
|---------|-----------|------------|
| Auth | 50K MAU | $0.0055/MAU |
| Firestore | 50K reads/day | $0.06/100K reads |
| Storage | 5GB | $0.026/GB |

**Estimated Cost at 10K MAU:** $5-20/month

### What Needs Verification
- [ ] Firestore security rules (prevent data theft)
- [ ] Firebase App Check (prevent API abuse)
- [ ] Enable anonymous auth in Firebase Console (for guest mode)

---

## 5. SECURITY CHECKLIST

### Before Release
- [ ] Remove all debug logs with sensitive data
- [ ] Obfuscate code (R8/ProGuard enabled in release)
- [ ] Verify no hardcoded API keys in code
- [ ] Test Firestore security rules
- [ ] Enable Firebase App Check

### google-services.json
- Already gitignored (good)
- Release vs Debug configs if needed

---

## 6. ARCHITECTURE CHANGES NEEDED

### For Ads
```
presentation/
  ads/
    AdManager.kt          # Singleton to manage ad loading
    BannerAdView.kt       # Composable wrapper
    InterstitialAdHelper.kt
```

### For Premium
```
domain/
  model/
    PremiumStatus.kt
  repository/
    BillingRepository.kt

data/
  billing/
    BillingRepositoryImpl.kt
    PurchaseVerifier.kt
```

### Estimated Dev Time
- AdMob integration: 4-8 hours
- Basic in-app purchase: 8-16 hours
- Premium status sync: 4-8 hours

---

## 7. COST SUMMARY

### One-Time Costs
| Item | Cost |
|------|------|
| Play Store Developer | $25 |
| **Total** | **$25** |

### Monthly Costs (at 10K users)
| Item | Cost |
|------|------|
| Firebase | $5-20 |
| Domain (privacy policy) | $1 (or free with GitHub Pages) |
| **Total** | **~$10/month** |

### Expected Revenue (10K MAU)
| Source | Est. Monthly |
|--------|--------------|
| Ads | $50-200 |
| Premium (5% conversion) | $100-200 |
| **Total** | **$150-400/month** |

---

## 8. IMMEDIATE ACTION ITEMS

### Week 1: Pre-Launch
1. [ ] Create AdMob account
2. [ ] Create Play Store developer account
3. [ ] Host Privacy Policy (GitHub Pages = free)
4. [ ] Enable Firebase anonymous auth
5. [ ] Add Firebase App Check

### Week 2: Ad Integration
1. [ ] Add AdMob SDK
2. [ ] Create banner ad composable
3. [ ] Add banner to HomeScreen
4. [ ] Test with test ad IDs

### Week 3: Release
1. [ ] Generate signed AAB
2. [ ] Create Play Store listing
3. [ ] Submit for review
4. [ ] Plan in-app purchase for v1.1

---

## 9. PRIVACY POLICY REQUIREMENTS

Must disclose:
- Data collected (email, habit data)
- Firebase Analytics usage
- AdMob (personalized ads)
- Data retention policy
- User rights (GDPR if targeting EU)

**Free hosting:** GitHub Pages, Firebase Hosting, or Notion public page

---

## 10. POST-LAUNCH MONITORING

### Free Tools
- Firebase Crashlytics (crash reports)
- Firebase Analytics (user behavior)
- Play Console (reviews, installs, revenue)
- AdMob dashboard (ad performance)

---

## Decision Points for Discussion

1. **Ad frequency:** How often to show interstitials?
2. **Premium price:** One-time vs subscription?
3. **Premium features:** Just ad-free, or additional features?
4. **Target markets:** Global or specific countries first?
