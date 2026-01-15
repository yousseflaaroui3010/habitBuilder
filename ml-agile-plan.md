# Machine Learning Implementation - Agile Plan

## Project Vision
Implement a data collection and machine learning system that provides users with personalized insights, predictions, and recommendations to improve their habit success rates.

---

## Epics Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           ML IMPLEMENTATION ROADMAP                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  EPIC 1: Data Foundation (Sprints 1-2)                                      │
│  ├── Analytics infrastructure                                                │
│  ├── Event tracking system                                                   │
│  └── Consent management                                                      │
│                                                                              │
│  EPIC 2: Data Collection (Sprints 3-4)                                      │
│  ├── User interaction events                                                 │
│  ├── Temporal context capture                                                │
│  └── Behavioral data logging                                                 │
│                                                                              │
│  EPIC 3: Backend Infrastructure (Sprints 5-6)                               │
│  ├── Data pipeline setup                                                     │
│  ├── Feature store                                                           │
│  └── Model serving infrastructure                                            │
│                                                                              │
│  EPIC 4: Prediction Models (Sprints 7-9)                                    │
│  ├── Habit success prediction                                                │
│  ├── Streak risk detection                                                   │
│  └── Churn prediction                                                        │
│                                                                              │
│  EPIC 5: Recommendation Engine (Sprints 10-12)                              │
│  ├── Time recommendations                                                    │
│  ├── Trigger recommendations                                                 │
│  └── Strategy recommendations                                                │
│                                                                              │
│  EPIC 6: Forecasting (Sprints 13-14)                                        │
│  ├── Success rate forecasting                                                │
│  ├── Habit formation timeline                                                │
│  └── Weekly performance predictions                                          │
│                                                                              │
│  EPIC 7: Continuous Learning (Sprint 15+)                                   │
│  ├── Feedback loops                                                          │
│  ├── Model retraining                                                        │
│  └── A/B testing                                                             │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Epic 1: Data Foundation

### Sprint 1: Analytics Infrastructure

**Goal**: Set up the core analytics infrastructure for event tracking

#### User Stories

**US-1.1: Analytics Event Model**
```
As a developer
I want a standardized event model for analytics
So that all events follow a consistent structure

Acceptance Criteria:
- [ ] Create AnalyticsEvent data class with all required fields
- [ ] Create EventContext data class for temporal/device context
- [ ] Create TimeOfDayBucket enum (EARLY_MORNING, MORNING, AFTERNOON, EVENING, NIGHT, LATE_NIGHT)
- [ ] Create ConsentTier enum (ESSENTIAL, ENHANCED, RESEARCH, PERSONAL)
- [ ] Unit tests for all models

Story Points: 3
Priority: Critical
```

**US-1.2: Local Analytics Storage**
```
As a developer
I want to store analytics events locally before syncing
So that we don't lose data when offline

Acceptance Criteria:
- [ ] Create AnalyticsEventEntity Room entity
- [ ] Create AnalyticsDao with insert, query, delete operations
- [ ] Add analytics_events table to database migration
- [ ] Implement batch query for sync (oldest first, limit 100)
- [ ] Implement cleanup after successful sync
- [ ] Unit tests for DAO operations

Story Points: 5
Priority: Critical
```

**US-1.3: Analytics SDK Interface**
```
As a developer
I want a clean SDK interface for tracking events
So that tracking is easy to use throughout the app

Acceptance Criteria:
- [ ] Create AnalyticsSDK interface with trackEvent(), setUserProperty(), flush()
- [ ] Create HabitAnalytics implementation class
- [ ] Inject via Hilt as singleton
- [ ] Auto-enrich events with context (time, device, session)
- [ ] Implement batching logic (100 events or 60 seconds)
- [ ] Integration tests

Story Points: 5
Priority: Critical
```

**US-1.4: Session Management**
```
As a developer
I want to track user sessions
So that we can analyze session-based behavior

Acceptance Criteria:
- [ ] Create SessionManager class
- [ ] Generate unique session ID on app open
- [ ] Track session start/end timestamps
- [ ] Calculate session duration
- [ ] Detect session timeout (30 min inactivity)
- [ ] Persist current session across process death

Story Points: 3
Priority: High
```

---

### Sprint 2: Consent Management

**Goal**: Implement user consent system for data collection

#### User Stories

**US-2.1: Consent Data Model**
```
As a user
I want to control what data is collected about me
So that my privacy is respected

Acceptance Criteria:
- [ ] Create ConsentPreferences data class
- [ ] Store consent level in DataStore
- [ ] Create ConsentManager class with get/set operations
- [ ] Default to ESSENTIAL tier (anonymous only)
- [ ] Provide migration path for existing users

Story Points: 3
Priority: Critical
```

**US-2.2: Consent UI Screen**
```
As a user
I want to see and change my data sharing preferences
So that I understand and control what's collected

Acceptance Criteria:
- [ ] Create ConsentSettingsScreen composable
- [ ] Show 4 consent tiers with clear descriptions
- [ ] Show what data each tier collects (expandable)
- [ ] Show benefits of each tier
- [ ] Save button with confirmation
- [ ] Add to Settings navigation

Story Points: 5
Priority: Critical
```

**US-2.3: Consent-Aware Event Filtering**
```
As a developer
I want events filtered based on consent level
So that we only collect permitted data

Acceptance Criteria:
- [ ] Add requiredConsentTier field to AnalyticsEvent
- [ ] Filter events in HabitAnalytics.trackEvent() based on consent
- [ ] Log filtered events count (for debugging, not stored)
- [ ] Handle consent level changes (don't retroactively delete)
- [ ] Unit tests for filtering logic

Story Points: 3
Priority: Critical
```

**US-2.4: Anonymous ID Generation**
```
As a developer
I want to generate anonymous user IDs
So that we can analyze behavior without identifying users

Acceptance Criteria:
- [ ] Generate installation-specific salt (stored securely)
- [ ] Create anonymousUserId from hash(userId + salt)
- [ ] Ensure same user always gets same anonymous ID
- [ ] Different anonymous ID on reinstall
- [ ] Never log or store the mapping

Story Points: 2
Priority: Critical
```

---

## Epic 2: Data Collection

### Sprint 3: Core Event Tracking

**Goal**: Implement tracking for all major user interactions

#### User Stories

**US-3.1: Habit Lifecycle Events**
```
As a data analyst
I want to track habit creation, editing, and deletion
So that I can analyze habit lifecycle patterns

Acceptance Criteria:
- [ ] Track habit_created (type, category, from_template, template_id)
- [ ] Track habit_edited (fields_changed, habit_age_days)
- [ ] Track habit_deleted (habit_age, success_rate, total_logs)
- [ ] Track habit_archived (habit_age, success_rate)
- [ ] Track habit_unarchived
- [ ] Track habit_reordered (old_position, new_position)
- [ ] All events include habit_type and habit_category (not name)

Story Points: 5
Priority: Critical
```

**US-3.2: Habit Completion Events**
```
As a data analyst
I want to track when users mark habits
So that I can analyze completion patterns

Acceptance Criteria:
- [ ] Track habit_marked_success with full context
- [ ] Track habit_marked_failure with full context
- [ ] Track habit_marked_skipped with full context
- [ ] Track undo_action_used (original_action, time_to_undo_ms)
- [ ] Include: habit_category, habit_type, current_streak, time_of_day
- [ ] Include: is_weekend, day_of_week, hours_after_trigger_time
- [ ] Include: days_since_habit_created, notification_opened_recently

Story Points: 5
Priority: Critical
```

**US-3.3: Strategy Engagement Events**
```
As a data analyst
I want to track strategy usage
So that I can measure strategy effectiveness

Acceptance Criteria:
- [ ] Track cue_added / cue_deleted
- [ ] Track friction_added / friction_implemented
- [ ] Track cost_journaled (category, word_count)
- [ ] Track temptation_bundle_set
- [ ] Track resistance_item_added / attraction_item_added
- [ ] Include habit_type, habit_age, strategy_count

Story Points: 3
Priority: High
```

**US-3.4: Navigation Events**
```
As a data analyst
I want to track screen navigation
So that I can analyze user flows

Acceptance Criteria:
- [ ] Track screen_viewed (screen_name, from_screen)
- [ ] Track screen_exited (screen_name, time_spent_ms, exit_action)
- [ ] Track bottom_nav_clicked (tab_name, from_tab)
- [ ] Track habit_card_clicked (position, scroll_position)
- [ ] Implement automatic screen tracking via Navigation callbacks

Story Points: 5
Priority: Medium
```

---

### Sprint 4: Contextual Data Collection

**Goal**: Capture temporal and behavioral context

#### User Stories

**US-4.1: Temporal Context Enrichment**
```
As a data analyst
I want automatic temporal context on all events
So that I can analyze time-based patterns

Acceptance Criteria:
- [ ] Add local_hour (0-23) to all events
- [ ] Add day_of_week (1-7) to all events
- [ ] Add is_weekend boolean
- [ ] Add time_of_day_bucket enum
- [ ] Add week_of_year, month
- [ ] Add is_monday, is_sunday flags
- [ ] Detect holidays (if possible via API or local list)

Story Points: 3
Priority: High
```

**US-4.2: Notification Events**
```
As a data analyst
I want to track notification interactions
So that I can optimize notification timing

Acceptance Criteria:
- [ ] Track notification_sent (type, habit_category, scheduled_time)
- [ ] Track notification_opened (type, delay_from_sent_seconds)
- [ ] Track notification_dismissed (type, delay_seconds)
- [ ] Track all_good_clicked (pending_habits_count)
- [ ] Track notification_settings_changed
- [ ] Include notification channel type

Story Points: 5
Priority: High
```

**US-4.3: Social/Partnership Events**
```
As a data analyst
I want to track partnership interactions
So that I can measure social feature impact

Acceptance Criteria:
- [ ] Track partner_invited (days_since_signup)
- [ ] Track partner_accepted (invite_age_hours)
- [ ] Track partner_removed (partnership_duration_days)
- [ ] Track habit_shared / habit_unshared
- [ ] Track partner_habits_viewed (frequency)
- [ ] All events anonymous (partner IDs hashed)

Story Points: 3
Priority: Medium
```

**US-4.4: Reflection Events**
```
As a data analyst
I want to track reflection engagement
So that I can measure reflection feature usage

Acceptance Criteria:
- [ ] Track reflection_started
- [ ] Track reflection_completed (word_counts per field, time_to_complete_ms)
- [ ] Track reflection_edited (edit_count)
- [ ] Optionally (RESEARCH tier): extract sentiment scores
- [ ] Optionally (RESEARCH tier): extract mentioned habits
- [ ] Never store raw reflection text in analytics

Story Points: 3
Priority: Medium
```

---

## Epic 3: Backend Infrastructure

### Sprint 5: Data Pipeline

**Goal**: Set up cloud infrastructure for data processing

#### User Stories

**US-5.1: Analytics Sync Service**
```
As a developer
I want to sync analytics events to the backend
So that data is available for ML processing

Acceptance Criteria:
- [ ] Create AnalyticsSyncService
- [ ] Implement batch upload (max 100 events per request)
- [ ] Handle network errors with exponential backoff
- [ ] Mark events as synced after successful upload
- [ ] Delete synced events older than 7 days
- [ ] Use WorkManager for background sync
- [ ] Sync on app open, close, and periodic (4 hours)

Story Points: 5
Priority: Critical
```

**US-5.2: Backend API Endpoints**
```
As a developer
I want backend endpoints to receive analytics data
So that we can store and process it

Acceptance Criteria:
- [ ] POST /analytics/events - batch event upload
- [ ] POST /analytics/consent - update consent level
- [ ] GET /analytics/export - user data export (GDPR)
- [ ] DELETE /analytics/user - delete user data (GDPR)
- [ ] Authentication via Firebase token
- [ ] Rate limiting (1000 events/min per user)

Story Points: 8
Priority: Critical
```

**US-5.3: Data Validation Pipeline**
```
As a data engineer
I want to validate incoming events
So that we maintain data quality

Acceptance Criteria:
- [ ] Validate event schema (required fields present)
- [ ] Validate timestamp ranges (not future, not too old)
- [ ] Validate enum values (status, type, etc.)
- [ ] Quarantine invalid events for review
- [ ] Log validation metrics (pass/fail rates)
- [ ] Alert on validation failure spike (>5%)

Story Points: 5
Priority: High
```

**US-5.4: Data Storage Setup**
```
As a data engineer
I want structured data storage
So that data is queryable for ML

Acceptance Criteria:
- [ ] Set up BigQuery dataset (or equivalent)
- [ ] Create raw_events table (partitioned by date)
- [ ] Create daily_aggregates table
- [ ] Create user_features table
- [ ] Create habit_features table
- [ ] Set up retention policies (90 days raw, 2 years aggregated)
- [ ] Set up backup schedule

Story Points: 5
Priority: Critical
```

---

### Sprint 6: Feature Store & Model Infrastructure

**Goal**: Set up ML infrastructure

#### User Stories

**US-6.1: Feature Engineering Pipeline**
```
As a data scientist
I want automated feature engineering
So that ML models have fresh features

Acceptance Criteria:
- [ ] Create daily feature extraction job
- [ ] Extract user-level features (tenure, habits count, success rates)
- [ ] Extract habit-level features (age, streaks, patterns)
- [ ] Extract temporal features (best hours, weekend patterns)
- [ ] Store in feature store with versioning
- [ ] Track feature freshness

Story Points: 8
Priority: Critical
```

**US-6.2: Feature Store Setup**
```
As a data scientist
I want a feature store
So that models can access consistent features

Acceptance Criteria:
- [ ] Set up feature store (Feast, Vertex AI, or custom)
- [ ] Define feature schemas
- [ ] Implement online serving (low latency)
- [ ] Implement offline serving (batch training)
- [ ] Feature versioning support
- [ ] Feature monitoring (drift detection)

Story Points: 8
Priority: Critical
```

**US-6.3: Model Registry**
```
As a data scientist
I want a model registry
So that I can version and deploy models

Acceptance Criteria:
- [ ] Set up model registry (MLflow, Vertex AI, or custom)
- [ ] Model versioning with metadata
- [ ] Model staging (dev, staging, prod)
- [ ] Rollback capability
- [ ] Model performance tracking
- [ ] A/B test assignment integration

Story Points: 5
Priority: High
```

**US-6.4: Model Serving Infrastructure**
```
As a developer
I want to call ML models from the app
So that users get predictions

Acceptance Criteria:
- [ ] Set up model serving endpoint
- [ ] Support batch predictions (daily user scores)
- [ ] Support real-time predictions (on-demand)
- [ ] Caching layer for frequent predictions
- [ ] Fallback to defaults if model unavailable
- [ ] Latency monitoring (<100ms p99)

Story Points: 8
Priority: Critical
```

---

## Epic 4: Prediction Models

### Sprint 7: Habit Success Prediction

**Goal**: Predict if user will complete habit today

#### User Stories

**US-7.1: Training Data Preparation**
```
As a data scientist
I want prepared training data
So that I can train the success prediction model

Acceptance Criteria:
- [ ] Create training dataset from historical logs
- [ ] Label: did user succeed on this day?
- [ ] Features: all temporal, habit, user, streak features
- [ ] Handle class imbalance (oversample failures)
- [ ] Train/validation/test split (70/15/15)
- [ ] Time-based split (no future leakage)

Story Points: 5
Priority: Critical
```

**US-7.2: Model Training Pipeline**
```
As a data scientist
I want an automated training pipeline
So that models are reproducible

Acceptance Criteria:
- [ ] Create training script for XGBoost classifier
- [ ] Hyperparameter tuning (grid search or Optuna)
- [ ] Cross-validation (5-fold, time-aware)
- [ ] Log metrics (AUC, precision, recall, F1)
- [ ] Save model to registry
- [ ] Generate feature importance report

Story Points: 5
Priority: Critical
```

**US-7.3: Model Validation**
```
As a data scientist
I want model validation before deployment
So that we only deploy good models

Acceptance Criteria:
- [ ] Validate on holdout test set
- [ ] Minimum AUC threshold: 0.70
- [ ] Minimum precision: 0.65
- [ ] Check for bias across user segments
- [ ] Check for temporal stability
- [ ] Compare to baseline (overall average)
- [ ] Generate validation report

Story Points: 3
Priority: Critical
```

**US-7.4: Success Prediction API**
```
As a developer
I want an API to get success predictions
So that the app can show predictions to users

Acceptance Criteria:
- [ ] POST /predictions/success endpoint
- [ ] Input: user_id, habit_id, date
- [ ] Output: probability (0-1), confidence, factors
- [ ] Batch endpoint for all user habits
- [ ] Cache predictions daily
- [ ] Fallback to average if model fails

Story Points: 5
Priority: High
```

---

### Sprint 8: Streak Risk Detection

**Goal**: Predict likelihood of breaking streak

#### User Stories

**US-8.1: Streak Risk Model**
```
As a data scientist
I want a streak risk prediction model
So that we can warn users before streak breaks

Acceptance Criteria:
- [ ] Define target: streak broken within 24 hours
- [ ] Create feature set focused on streak psychology
- [ ] Include: streak length, similar streak history, time factors
- [ ] Train binary classifier
- [ ] Optimize for recall (catch most at-risk streaks)
- [ ] Validate on holdout set

Story Points: 5
Priority: High
```

**US-8.2: Risk Alert Logic**
```
As a user
I want to be warned when my streak is at risk
So that I can take action to protect it

Acceptance Criteria:
- [ ] Define risk thresholds (high: >0.6, medium: >0.4)
- [ ] Generate alerts only for streaks >3 days
- [ ] Max 1 risk alert per habit per day
- [ ] Don't alert after user already completed
- [ ] Include suggested action in alert

Story Points: 3
Priority: High
```

**US-8.3: Risk Alert UI**
```
As a user
I want to see streak risk indicators
So that I know which habits need attention

Acceptance Criteria:
- [ ] Show risk indicator on habit card (icon/color)
- [ ] Risk tooltip with explanation
- [ ] "Protect your streak" quick action
- [ ] Link to extra reminder scheduling
- [ ] Don't show for new habits (<7 days)

Story Points: 5
Priority: Medium
```

---

### Sprint 9: Churn Prediction

**Goal**: Predict users likely to stop using the app

#### User Stories

**US-9.1: Churn Prediction Model**
```
As a product manager
I want to identify users likely to churn
So that we can intervene early

Acceptance Criteria:
- [ ] Define churn: no activity for 14 days
- [ ] Create feature set (engagement, success, recency)
- [ ] Train survival analysis or classification model
- [ ] Predict: probability of churn in next 7 days
- [ ] Identify top churn risk factors
- [ ] Weekly batch predictions for all users

Story Points: 8
Priority: Medium
```

**US-9.2: Re-engagement Triggers**
```
As a user at risk of churning
I want helpful re-engagement prompts
So that I'm motivated to return

Acceptance Criteria:
- [ ] Define re-engagement notification templates
- [ ] Trigger based on churn risk score
- [ ] Personalize message based on risk factors
- [ ] Max 2 re-engagement messages per week
- [ ] Track effectiveness (did user return?)
- [ ] Respect notification preferences

Story Points: 5
Priority: Medium
```

---

## Epic 5: Recommendation Engine

### Sprint 10: Time Recommendations

**Goal**: Recommend optimal times for habits

#### User Stories

**US-10.1: Time Pattern Analysis**
```
As a data scientist
I want to analyze user time patterns
So that I can recommend optimal times

Acceptance Criteria:
- [ ] Calculate success rate by hour for each user
- [ ] Calculate success rate by hour for habit categories
- [ ] Calculate success rate by hour for similar users
- [ ] Identify user's natural activity windows
- [ ] Account for schedule changes (weekday vs weekend)

Story Points: 5
Priority: High
```

**US-10.2: Time Recommendation Model**
```
As a data scientist
I want a time recommendation model
So that users get personalized time suggestions

Acceptance Criteria:
- [ ] Combine personal, category, and cluster patterns
- [ ] Weight: 50% personal, 30% similar users, 20% category
- [ ] Handle cold start (new users/habits)
- [ ] Generate top 3 time recommendations
- [ ] Include confidence score for each
- [ ] Include explanation (why this time)

Story Points: 5
Priority: High
```

**US-10.3: Time Recommendation UI**
```
As a user
I want to see recommended times for my habits
So that I can schedule them optimally

Acceptance Criteria:
- [ ] Show recommendations in habit edit screen
- [ ] Show recommendations when creating habit
- [ ] "Your best time" badge on top recommendation
- [ ] Explanation tooltip ("85% success at this time")
- [ ] One-tap to apply recommendation
- [ ] Track recommendation acceptance

Story Points: 5
Priority: High
```

---

### Sprint 11: Trigger Recommendations

**Goal**: Recommend effective triggers for habits

#### User Stories

**US-11.1: Trigger Effectiveness Analysis**
```
As a data scientist
I want to analyze trigger effectiveness
So that I can recommend good triggers

Acceptance Criteria:
- [ ] Categorize existing triggers (time, event, location, emotion)
- [ ] Calculate success rate by trigger type
- [ ] Identify high-performing trigger patterns
- [ ] Analyze trigger-habit category correlations
- [ ] Build trigger embedding model

Story Points: 5
Priority: High
```

**US-11.2: Trigger Recommendation Model**
```
As a data scientist
I want a trigger recommendation model
So that users get effective trigger suggestions

Acceptance Criteria:
- [ ] Score triggers based on predicted effectiveness
- [ ] Consider user's existing routines
- [ ] Consider habit category patterns
- [ ] Generate top 5 trigger recommendations
- [ ] Include effectiveness score
- [ ] Include "why" explanation

Story Points: 5
Priority: High
```

**US-11.3: Trigger Recommendation UI**
```
As a user
I want trigger suggestions when creating habits
So that I can set up effective triggers

Acceptance Criteria:
- [ ] Show trigger recommendations in habit creation
- [ ] Show in trigger editing screen
- [ ] Explain why each trigger is recommended
- [ ] One-tap to select recommendation
- [ ] Allow customization of recommended trigger
- [ ] Track acceptance and effectiveness

Story Points: 5
Priority: High
```

---

### Sprint 12: Strategy Recommendations

**Goal**: Recommend strategies for struggling habits

#### User Stories

**US-12.1: Strategy Effectiveness Analysis**
```
As a data scientist
I want to analyze which strategies work
So that I can recommend effective strategies

Acceptance Criteria:
- [ ] Calculate success rate before/after strategy adoption
- [ ] Compare strategies across habit types (BUILD vs BREAK)
- [ ] Identify strategy combinations that work well
- [ ] Analyze user archetypes and strategy fit
- [ ] Build strategy effectiveness model

Story Points: 5
Priority: High
```

**US-12.2: Strategy Recommendation Model**
```
As a data scientist
I want a strategy recommendation model
So that struggling users get help

Acceptance Criteria:
- [ ] Identify struggling habits (declining success, broken streaks)
- [ ] Match user profile to effective strategies
- [ ] Rank strategies by predicted effectiveness
- [ ] Generate top 3 strategy recommendations
- [ ] Personalize recommendations to user context

Story Points: 5
Priority: High
```

**US-12.3: Recovery Recommendation UI**
```
As a user struggling with a habit
I want personalized recovery suggestions
So that I can get back on track

Acceptance Criteria:
- [ ] Show recovery suggestions after failures
- [ ] Show proactive suggestions on declining trends
- [ ] "Try this strategy" cards with explanations
- [ ] Link to strategy implementation screens
- [ ] Track strategy adoption and outcomes

Story Points: 5
Priority: High
```

---

## Epic 6: Forecasting

### Sprint 13: Success Rate Forecasting

**Goal**: Forecast user's weekly/monthly success rates

#### User Stories

**US-13.1: Time Series Forecasting Model**
```
As a data scientist
I want to forecast success rates
So that users can see predicted performance

Acceptance Criteria:
- [ ] Build Prophet/ARIMA model for success rates
- [ ] Forecast 7 days ahead
- [ ] Include confidence intervals
- [ ] Account for weekly seasonality
- [ ] Account for known patterns (Monday dips, etc.)
- [ ] Validate forecast accuracy (MAPE < 20%)

Story Points: 8
Priority: Medium
```

**US-13.2: Forecast API**
```
As a developer
I want a forecast API
So that the app can display predictions

Acceptance Criteria:
- [ ] GET /forecast/weekly endpoint
- [ ] Returns: daily predicted rates, confidence bounds
- [ ] Caching (update daily)
- [ ] Fallback to simple moving average

Story Points: 3
Priority: Medium
```

**US-13.3: Forecast UI**
```
As a user
I want to see my predicted performance
So that I can plan ahead

Acceptance Criteria:
- [ ] Show weekly forecast on dashboard
- [ ] Highlight predicted challenging days
- [ ] Show trend direction (improving/declining)
- [ ] "Heads up" alerts for predicted dips
- [ ] Motivational messaging based on forecast

Story Points: 5
Priority: Medium
```

---

### Sprint 14: Habit Formation Timeline

**Goal**: Predict when habits will become automatic

#### User Stories

**US-14.1: Formation Timeline Model**
```
As a data scientist
I want to predict habit formation timelines
So that users have realistic expectations

Acceptance Criteria:
- [ ] Define "formed" = 90% success rate over 2 weeks
- [ ] Build survival model for time-to-formation
- [ ] Features: habit type, initial success, user history
- [ ] Predict: days until habit becomes automatic
- [ ] Confidence interval around prediction
- [ ] Validate against historical data

Story Points: 8
Priority: Medium
```

**US-14.2: Formation Progress UI**
```
As a user
I want to see my progress toward habit formation
So that I stay motivated during the hard phase

Acceptance Criteria:
- [ ] Show "X% formed" progress indicator
- [ ] Show estimated days remaining
- [ ] Celebrate formation milestones
- [ ] Explain the science ("habits take 66 days on average")
- [ ] Adjust estimate as user progresses

Story Points: 5
Priority: Medium
```

---

## Epic 7: Continuous Learning

### Sprint 15+: Ongoing Improvements

#### User Stories

**US-15.1: Feedback Collection System**
```
As a data scientist
I want to collect feedback on recommendations
So that models can improve

Acceptance Criteria:
- [ ] Track recommendation_shown events
- [ ] Track recommendation_accepted / rejected
- [ ] Track outcome (success after following recommendation)
- [ ] Optional explicit feedback ("Was this helpful?")
- [ ] Max 1 feedback prompt per day
- [ ] Aggregate feedback for model retraining

Story Points: 5
Priority: High
```

**US-15.2: Model Retraining Pipeline**
```
As a data scientist
I want automated model retraining
So that models stay fresh

Acceptance Criteria:
- [ ] Weekly retraining schedule
- [ ] Data drift detection triggers retraining
- [ ] Performance drop triggers retraining
- [ ] Automated validation before deployment
- [ ] Rollback if new model underperforms
- [ ] Alerting on training failures

Story Points: 8
Priority: High
```

**US-15.3: A/B Testing Framework**
```
As a product manager
I want to A/B test ML features
So that we ship improvements with confidence

Acceptance Criteria:
- [ ] Experiment configuration system
- [ ] Deterministic user assignment
- [ ] Metrics tracking per variant
- [ ] Statistical significance calculation
- [ ] Experiment analysis dashboard
- [ ] Auto-rollout winners

Story Points: 8
Priority: Medium
```

**US-15.4: ML Monitoring Dashboard**
```
As a data scientist
I want to monitor model performance
So that I catch issues early

Acceptance Criteria:
- [ ] Real-time prediction volume monitoring
- [ ] Prediction distribution monitoring
- [ ] Feature drift monitoring
- [ ] Model accuracy tracking
- [ ] Recommendation acceptance rates
- [ ] Alerting on anomalies

Story Points: 5
Priority: High
```

---

## Sprint Planning Summary

| Sprint | Epic | Focus | Key Deliverables |
|--------|------|-------|------------------|
| 1 | Data Foundation | Analytics Infrastructure | Event model, local storage, SDK |
| 2 | Data Foundation | Consent Management | Consent UI, filtering, anonymous IDs |
| 3 | Data Collection | Core Events | Habit, completion, strategy events |
| 4 | Data Collection | Context | Temporal, notification, social events |
| 5 | Backend | Data Pipeline | Sync service, API, validation, storage |
| 6 | Backend | ML Infrastructure | Feature store, model registry, serving |
| 7 | Predictions | Success Prediction | Training pipeline, validation, API |
| 8 | Predictions | Streak Risk | Risk model, alerts, UI |
| 9 | Predictions | Churn | Churn model, re-engagement |
| 10 | Recommendations | Time | Time analysis, model, UI |
| 11 | Recommendations | Triggers | Trigger analysis, model, UI |
| 12 | Recommendations | Strategies | Strategy analysis, recovery UI |
| 13 | Forecasting | Success Forecast | Time series model, API, UI |
| 14 | Forecasting | Formation Timeline | Formation model, progress UI |
| 15+ | Continuous | Learning Loop | Feedback, retraining, A/B testing |

---

## Definition of Done

For each user story to be considered "Done":

1. **Code Complete**: All acceptance criteria implemented
2. **Tests**: Unit tests with >80% coverage
3. **Code Review**: Approved by at least 1 reviewer
4. **Documentation**: Code documented, README updated if needed
5. **Privacy**: Verified consent requirements respected
6. **Analytics**: Events tracked for the feature
7. **QA**: Manual testing completed
8. **Performance**: No regression in app performance

---

## Risks & Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Insufficient data for ML | High | Medium | Start with rule-based, switch to ML when data available |
| Model accuracy too low | High | Medium | Extensive validation, fallback to simpler models |
| Privacy concerns | High | Low | Strong consent system, anonymous by default |
| Backend costs | Medium | Medium | Efficient batching, caching, start with free tiers |
| User distrust of ML | Medium | Low | Transparent explanations, opt-in features |

---

## Success Metrics

### Phase 1 (Sprints 1-6): Foundation
- [ ] >10,000 events collected daily
- [ ] <1% invalid event rate
- [ ] >50% user consent for enhanced analytics

### Phase 2 (Sprints 7-12): Core ML
- [ ] Success prediction AUC >0.70
- [ ] Recommendation acceptance rate >30%
- [ ] User satisfaction score maintained

### Phase 3 (Sprints 13+): Advanced
- [ ] 10% improvement in habit success rates
- [ ] 15% reduction in churn
- [ ] Positive user feedback on ML features

---

## Current Sprint Backlog

### Sprint 1: Analytics Infrastructure

**Sprint Goal**: Establish the foundation for event tracking

**Committed Stories**:
1. US-1.1: Analytics Event Model (3 SP)
2. US-1.2: Local Analytics Storage (5 SP)
3. US-1.3: Analytics SDK Interface (5 SP)
4. US-1.4: Session Management (3 SP)

**Total Story Points**: 16

**Definition of Ready**:
- [ ] Requirements clear
- [ ] Acceptance criteria defined
- [ ] Dependencies identified
- [ ] No blockers

---

*This is a living document. Update after each sprint retrospective.*
