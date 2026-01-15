# Data Collection & Machine Learning Strategy

## Table of Contents
1. [Data Categories Overview](#1-data-categories-overview)
2. [Detailed Data Points](#2-detailed-data-points)
3. [Consent & Privacy Framework](#3-consent--privacy-framework)
4. [Data Collection Implementation](#4-data-collection-implementation)
5. [Data Cleaning & Preparation](#5-data-cleaning--preparation)
6. [Machine Learning Pipeline](#6-machine-learning-pipeline)
7. [Forecasting & Predictive Analysis](#7-forecasting--predictive-analysis)
8. [Recommendation Engine](#8-recommendation-engine)
9. [Continuous Learning Architecture](#9-continuous-learning-architecture)
10. [Technical Implementation](#10-technical-implementation)

---

## 1. Data Categories Overview

### Category A: User-Consented Personal Data
Data that requires explicit user consent and is tied to user identity.

### Category B: Anonymous Behavioral Data
Data collected using only anonymous IDs - no personal information attached.

### Category C: App Improvement Data (Internal Analytics)
Technical data for improving app performance and UX.

---

## 2. Detailed Data Points

### 2.1 User Profile Data (Category A - Consent Required)

| Data Point | Description | Purpose |
|------------|-------------|---------|
| `user_id` | Unique identifier | Link all user data |
| `email` | User email | Account management |
| `display_name` | User's name | Personalization |
| `auth_provider` | GOOGLE/EMAIL/GUEST | Auth analytics |
| `account_created_at` | Account creation timestamp | User lifecycle |
| `timezone` | User's timezone | Time-based analysis |
| `locale` | Language/region | Localization |
| `age_range` | Optional demographic | Pattern analysis |
| `occupation_type` | Optional (student/professional/etc) | Context patterns |

---

### 2.2 Habit Definition Data (Category A)

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `habit_id` | Unique habit identifier | Reference key |
| `habit_name` | Name of the habit | NLP categorization |
| `habit_type` | BUILD or BREAK | Model segmentation |
| `category` | Health/Productivity/etc | Clustering |
| `icon_emoji` | Visual representation | Sentiment analysis |
| `frequency` | DAILY/WEEKLY/CUSTOM | Pattern modeling |
| `active_days` | Which days habit is active | Schedule optimization |
| `trigger_time` | When reminder fires | Time pattern analysis |
| `trigger_context` | After X, Before Y, etc | Context modeling |
| `priority` | HIGH/MEDIUM/LOW | Priority patterns |
| `minimum_version` | 2-minute rule version | Engagement analysis |
| `stack_anchor` | Linked habit for stacking | Dependency graphs |
| `reward` | User-defined reward | Motivation analysis |
| `created_at` | Habit creation time | Lifecycle tracking |
| `is_from_template` | Template vs custom | Template effectiveness |
| `template_id` | Which template used | Template success rates |

---

### 2.3 Daily Log Data (Category B - Anonymous)

**Core Completion Events:**

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `anonymous_user_id` | Hashed user identifier | Cohort analysis |
| `habit_category` | Category only (not name) | Pattern by category |
| `habit_type` | BUILD or BREAK | Type performance |
| `log_date` | Date of the log | Time series |
| `status` | SUCCESS/FAILURE/SKIPPED/PENDING | Outcome prediction |
| `marked_at_timestamp` | Exact time marked | Behavioral timing |
| `time_of_day_bucket` | Morning/Afternoon/Evening/Night | Peak performance times |
| `day_of_week` | Monday-Sunday | Weekly patterns |
| `is_weekend` | Boolean | Weekend vs weekday |
| `is_holiday` | Boolean (if detectable) | Holiday patterns |
| `streak_at_time` | Current streak when marked | Streak psychology |
| `days_since_habit_created` | Habit age | Learning curves |
| `note_length` | Length of note (not content) | Engagement depth |
| `note_sentiment` | Positive/Negative/Neutral | Emotional patterns |

**Derived Timing Metrics:**

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `hours_after_trigger_time` | Time delta from reminder | Procrastination patterns |
| `hours_since_wakeup` | Estimated wakeup offset | Circadian analysis |
| `minutes_since_last_action` | App engagement timing | Session patterns |
| `consecutive_success_count` | Running success count | Momentum analysis |
| `consecutive_failure_count` | Running failure count | Struggle detection |

---

### 2.4 User Interaction Events (Category B - Anonymous)

**Button/Action Events:**

| Event Type | Data Collected | Purpose |
|------------|----------------|---------|
| `habit_marked_success` | habit_category, time, context | Success patterns |
| `habit_marked_failure` | habit_category, time, context | Failure patterns |
| `habit_marked_skipped` | habit_category, time, reason | Skip patterns |
| `habit_created` | type, category, from_template | Creation patterns |
| `habit_deleted` | days_active, success_rate, reason | Churn analysis |
| `habit_archived` | days_active, success_rate | Archive patterns |
| `habit_edited` | fields_changed, days_since_creation | Modification patterns |
| `habit_reordered` | old_position, new_position | Priority insights |
| `undo_action_used` | original_action, time_to_undo | Mistake recovery |

**Strategy Engagement Events:**

| Event Type | Data Collected | Purpose |
|------------|----------------|---------|
| `cue_added` | habit_type, habit_age | Cue awareness |
| `cue_deleted` | days_since_added | Cue relevance |
| `friction_added` | friction_category, habit_age | Friction strategies |
| `friction_implemented` | days_to_implement | Implementation time |
| `cost_journaled` | cost_category, word_count | Journaling patterns |
| `temptation_bundle_set` | habit_age, bundle_category | Reward pairing |
| `resistance_item_added` | habit_type, count_total | Reflection depth |
| `attraction_item_added` | habit_type, count_total | Motivation building |

**Navigation Events:**

| Event Type | Data Collected | Purpose |
|------------|----------------|---------|
| `screen_viewed` | screen_name, time_spent_ms | UX analysis |
| `screen_exited` | screen_name, exit_action | Flow analysis |
| `bottom_nav_clicked` | tab_name, from_tab | Navigation patterns |
| `habit_card_clicked` | habit_position, scroll_position | Engagement patterns |
| `calendar_date_clicked` | days_from_today, had_log | Historical review |
| `settings_opened` | from_screen | Settings interest |
| `profile_viewed` | own_or_partner | Social engagement |

---

### 2.5 Notification & Reminder Data (Category B - Anonymous)

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `notification_sent` | type, time, habit_category | Delivery tracking |
| `notification_opened` | type, delay_seconds | Engagement timing |
| `notification_dismissed` | type, delay_seconds | Dismissal patterns |
| `all_good_clicked` | time, pending_habits_count | Quick engagement |
| `morning_reminder_opened` | delay_from_scheduled | Morning routine |
| `evening_checkin_opened` | delay_from_scheduled | Evening routine |
| `habit_reminder_opened` | delay_from_scheduled | Habit-specific |
| `recovery_reminder_opened` | hours_after_failure | Recovery patterns |
| `notification_disabled` | type, after_how_many_days | Fatigue detection |

---

### 2.6 Social/Partnership Data (Category B - Anonymous)

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `partner_invited` | days_since_signup | Social timing |
| `partner_accepted` | invite_age_hours | Acceptance rate |
| `habit_shared` | habit_age, success_rate | Sharing psychology |
| `habit_unshared` | days_shared, reason | Privacy patterns |
| `partner_habits_viewed` | frequency, duration | Accountability engagement |
| `partner_removed` | partnership_duration | Partnership lifecycle |

---

### 2.7 Reflection Data (Category A - Consent for content, Category B for metadata)

**Metadata (Anonymous):**

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `reflection_completed` | week_number, day_submitted | Reflection habits |
| `went_well_word_count` | Integer | Engagement depth |
| `didnt_go_well_word_count` | Integer | Self-criticism depth |
| `learned_word_count` | Integer | Growth mindset |
| `reflection_edit_count` | Times edited | Thoughtfulness |
| `time_to_complete_ms` | Duration | Effort invested |

**Content Analysis (Requires Consent):**

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `went_well_sentiment` | Positive/Neutral/Negative | Emotional tracking |
| `didnt_go_well_themes` | Extracted topics | Common struggles |
| `learned_actionability` | Actionable/Reflective | Growth patterns |
| `mentioned_habits` | Which habits referenced | Connection to habits |

---

### 2.8 Trigger & Context Data (Category A)

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `trigger_text` | User-defined trigger | NLP analysis |
| `trigger_type` | Time/Location/Event/Emotion | Trigger categories |
| `trigger_effectiveness` | Success rate when triggered | Trigger optimization |
| `trigger_changed` | Before/after versions | Trigger evolution |
| `context_location` | Home/Work/Gym/etc | Location patterns |
| `context_preceding_habit` | What comes before | Habit stacking |
| `context_following_habit` | What comes after | Routine building |
| `context_emotional_state` | User-tagged emotion | Emotional triggers |

---

### 2.9 Session & App Usage Data (Category C - App Improvement)

| Data Point | Description | Purpose |
|------------|-------------|---------|
| `session_start` | Timestamp | Usage patterns |
| `session_end` | Timestamp | Session duration |
| `session_duration_ms` | Total time | Engagement depth |
| `screens_visited` | Count | Navigation complexity |
| `actions_performed` | Count | Activity level |
| `app_opened_from` | Notification/Widget/Direct | Entry points |
| `app_version` | Version number | Version analysis |
| `device_type` | Phone/Tablet | Device patterns |
| `os_version` | Android version | Compatibility |
| `screen_size_bucket` | Small/Medium/Large | UI optimization |
| `crash_occurred` | Boolean + stack trace hash | Stability |
| `error_encountered` | Error type | Bug tracking |
| `slow_operation` | Operation + duration | Performance |

---

### 2.10 Temporal Context Data (Category B - Derived)

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `local_hour` | 0-23 | Hourly patterns |
| `local_minute_bucket` | 0-11 (5-min buckets) | Fine timing |
| `day_of_week` | 1-7 | Weekly patterns |
| `day_of_month` | 1-31 | Monthly patterns |
| `week_of_year` | 1-52 | Yearly patterns |
| `month` | 1-12 | Seasonal patterns |
| `is_weekend` | Boolean | Weekend effect |
| `is_holiday` | Boolean | Holiday effect |
| `is_payday_week` | Boolean (estimated) | Financial timing |
| `season` | Spring/Summer/Fall/Winter | Seasonal patterns |
| `days_since_new_year` | Integer | New Year effect |
| `is_monday` | Boolean | Monday effect |
| `is_sunday` | Boolean | Week prep effect |

---

### 2.11 Streak & Progress Data (Category B - Anonymous)

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `current_streak` | Integer | Momentum |
| `longest_streak` | Integer | Peak performance |
| `streak_just_broken` | Boolean | Failure psychology |
| `days_to_first_failure` | Integer | Initial commitment |
| `failure_recovery_days` | Days to next success | Resilience |
| `total_success_days` | Integer | Overall success |
| `total_failure_days` | Integer | Overall struggle |
| `success_rate_7d` | Percentage | Recent performance |
| `success_rate_30d` | Percentage | Monthly performance |
| `success_rate_all_time` | Percentage | Overall performance |
| `paper_clip_count` | Integer | Gamification engagement |
| `paper_clip_goal` | Integer | Goal setting |
| `paper_clip_progress` | Percentage | Goal progress |

---

### 2.12 Cohort & Lifecycle Data (Category B - Anonymous)

| Data Point | Description | ML Usage |
|------------|-------------|----------|
| `days_since_signup` | Integer | User lifecycle |
| `total_habits_created` | Integer | Engagement level |
| `active_habits_count` | Integer | Current load |
| `archived_habits_count` | Integer | Churn |
| `build_vs_break_ratio` | Float | User preference |
| `onboarding_completed` | Boolean | Funnel completion |
| `has_partner` | Boolean | Social engagement |
| `data_export_count` | Integer | Data awareness |
| `settings_change_count` | Integer | Customization |
| `reflection_completion_rate` | Percentage | Reflection engagement |

---

## 3. Consent & Privacy Framework

### 3.1 Consent Tiers

```
TIER 1: Essential (No consent needed - anonymized)
├── Anonymous usage analytics
├── Crash reporting
├── Performance metrics
└── Aggregated statistics

TIER 2: Enhanced Analytics (Opt-in)
├── Detailed behavioral patterns
├── Cross-habit correlations
├── Personalized insights
└── Recommendation generation

TIER 3: Research Contribution (Explicit consent)
├── De-identified data for ML training
├── Academic research participation
├── Pattern sharing for global insights
└── Feature effectiveness studies

TIER 4: Personal Insights (Consent + Identity)
├── Personal progress reports
├── Exported personal data
├── Reflection content analysis
└── Trigger text analysis
```

### 3.2 Data Storage Rules

| Category | Storage | Retention | Deletion |
|----------|---------|-----------|----------|
| Category A | Encrypted, user-linked | Until account deleted | On request |
| Category B | Anonymous, aggregated | 2 years rolling | Auto-purge |
| Category C | Server logs | 90 days | Auto-purge |

### 3.3 Anonymization Methods

```kotlin
// Anonymous ID generation
fun generateAnonymousId(userId: String): String {
    val salt = getInstallationSalt() // Unique per installation
    return sha256(userId + salt).substring(0, 16)
}

// Data anonymization rules
- Remove: email, name, photo, exact timestamps (round to hour)
- Hash: user_id, habit_id, partner_id
- Generalize: age -> age_range, location -> region
- Suppress: any field with <5 users in cohort
```

---

## 4. Data Collection Implementation

### 4.1 Event Tracking Architecture

```kotlin
// Core analytics event structure
data class AnalyticsEvent(
    val eventName: String,
    val timestamp: Long,
    val anonymousUserId: String,
    val sessionId: String,
    val properties: Map<String, Any>,
    val context: EventContext
)

data class EventContext(
    val timeOfDay: TimeOfDayBucket,    // MORNING/AFTERNOON/EVENING/NIGHT
    val dayOfWeek: Int,                 // 1-7
    val isWeekend: Boolean,
    val localHour: Int,
    val appVersion: String,
    val sessionDurationMs: Long
)

enum class TimeOfDayBucket {
    EARLY_MORNING,  // 5-8
    MORNING,        // 8-12
    AFTERNOON,      // 12-17
    EVENING,        // 17-21
    NIGHT,          // 21-24
    LATE_NIGHT      // 0-5
}
```

### 4.2 Event Categories to Implement

```kotlin
// Habit Events
object HabitEvents {
    const val CREATED = "habit_created"
    const val MARKED_SUCCESS = "habit_marked_success"
    const val MARKED_FAILURE = "habit_marked_failure"
    const val MARKED_SKIPPED = "habit_marked_skipped"
    const val EDITED = "habit_edited"
    const val DELETED = "habit_deleted"
    const val ARCHIVED = "habit_archived"
    const val UNARCHIVED = "habit_unarchived"
    const val REORDERED = "habit_reordered"
    const val SHARED = "habit_shared"
    const val UNSHARED = "habit_unshared"
    const val REMINDER_ENABLED = "habit_reminder_enabled"
    const val REMINDER_DISABLED = "habit_reminder_disabled"
}

// Strategy Events
object StrategyEvents {
    const val CUE_ADDED = "cue_added"
    const val CUE_DELETED = "cue_deleted"
    const val FRICTION_ADDED = "friction_added"
    const val FRICTION_IMPLEMENTED = "friction_implemented"
    const val COST_JOURNALED = "cost_journaled"
    const val TEMPTATION_BUNDLE_SET = "temptation_bundle_set"
    const val RESISTANCE_ADDED = "resistance_item_added"
    const val ATTRACTION_ADDED = "attraction_item_added"
}

// Session Events
object SessionEvents {
    const val APP_OPENED = "app_opened"
    const val APP_BACKGROUNDED = "app_backgrounded"
    const val SCREEN_VIEWED = "screen_viewed"
    const val SCREEN_EXITED = "screen_exited"
}

// Notification Events
object NotificationEvents {
    const val SENT = "notification_sent"
    const val OPENED = "notification_opened"
    const val DISMISSED = "notification_dismissed"
    const val ALL_GOOD_CLICKED = "all_good_clicked"
}

// Social Events
object SocialEvents {
    const val PARTNER_INVITED = "partner_invited"
    const val PARTNER_ACCEPTED = "partner_accepted"
    const val PARTNER_REMOVED = "partner_removed"
    const val PARTNER_VIEWED = "partner_viewed"
}

// Reflection Events
object ReflectionEvents {
    const val STARTED = "reflection_started"
    const val COMPLETED = "reflection_completed"
    const val EDITED = "reflection_edited"
}
```

### 4.3 Local Data Collection (Before Sync)

```kotlin
// Local analytics database table
@Entity(tableName = "analytics_events")
data class AnalyticsEventEntity(
    @PrimaryKey val eventId: String = UUID.randomUUID().toString(),
    val eventName: String,
    val timestamp: Long,
    val anonymousUserId: String,
    val sessionId: String,
    val propertiesJson: String,  // Serialized Map
    val contextJson: String,     // Serialized EventContext
    val synced: Boolean = false,
    val consentTier: Int         // Which consent level this requires
)
```

---

## 5. Data Cleaning & Preparation

### 5.1 Data Quality Rules

```python
# Data validation rules
VALIDATION_RULES = {
    'timestamp': {
        'not_null': True,
        'range': (app_launch_date, current_time),
        'format': 'unix_ms'
    },
    'status': {
        'values': ['SUCCESS', 'FAILURE', 'SKIPPED', 'PENDING']
    },
    'streak': {
        'min': 0,
        'max': 9999,
        'type': 'integer'
    },
    'success_rate': {
        'min': 0.0,
        'max': 1.0,
        'type': 'float'
    }
}

# Outlier detection
def detect_outliers(data, column):
    q1, q3 = data[column].quantile([0.25, 0.75])
    iqr = q3 - q1
    lower_bound = q1 - 1.5 * iqr
    upper_bound = q3 + 1.5 * iqr
    return data[(data[column] < lower_bound) | (data[column] > upper_bound)]
```

### 5.2 Feature Engineering Pipeline

```python
# Time-based features
def create_time_features(df):
    df['hour'] = df['timestamp'].dt.hour
    df['day_of_week'] = df['timestamp'].dt.dayofweek
    df['is_weekend'] = df['day_of_week'].isin([5, 6])
    df['is_monday'] = df['day_of_week'] == 0
    df['week_of_year'] = df['timestamp'].dt.isocalendar().week
    df['month'] = df['timestamp'].dt.month
    df['time_of_day'] = pd.cut(df['hour'],
        bins=[0, 5, 8, 12, 17, 21, 24],
        labels=['late_night', 'early_morning', 'morning', 'afternoon', 'evening', 'night']
    )
    return df

# Habit-based features
def create_habit_features(df):
    df['habit_age_days'] = (df['log_date'] - df['habit_created_at']).dt.days
    df['days_since_last_success'] = df.groupby('habit_id')['is_success'].transform(
        lambda x: x.cumsum().shift().fillna(0)
    )
    df['rolling_success_7d'] = df.groupby('habit_id')['is_success'].transform(
        lambda x: x.rolling(7, min_periods=1).mean()
    )
    df['rolling_success_30d'] = df.groupby('habit_id')['is_success'].transform(
        lambda x: x.rolling(30, min_periods=1).mean()
    )
    return df

# Streak features
def create_streak_features(df):
    df['streak_momentum'] = df['current_streak'] / (df['longest_streak'] + 1)
    df['streak_at_risk'] = df['current_streak'] >= 7  # High streak to protect
    df['recovery_mode'] = df['consecutive_failures'] >= 2
    return df

# User behavior features
def create_behavior_features(df):
    df['avg_completion_hour'] = df.groupby('user_id')['completion_hour'].transform('mean')
    df['completion_hour_variance'] = df.groupby('user_id')['completion_hour'].transform('std')
    df['preferred_day'] = df.groupby('user_id')['day_of_week'].transform(
        lambda x: x.value_counts().index[0]
    )
    return df
```

### 5.3 Data Aggregation Pipelines

```python
# Daily user summary
daily_user_summary = df.groupby(['user_id', 'date']).agg({
    'habit_id': 'count',                    # habits_attempted
    'is_success': 'sum',                    # successes
    'is_failure': 'sum',                    # failures
    'completion_hour': 'mean',              # avg_completion_time
    'session_duration': 'sum',              # total_app_time
    'notification_opened': 'sum',           # notifications_engaged
}).reset_index()

# Weekly habit summary
weekly_habit_summary = df.groupby(['habit_id', 'week']).agg({
    'is_success': ['sum', 'count', 'mean'], # success metrics
    'streak': 'max',                         # max streak that week
    'time_to_complete': 'mean',             # avg completion time
}).reset_index()

# Cohort analysis
cohort_summary = df.groupby(['signup_week', 'weeks_since_signup']).agg({
    'user_id': 'nunique',                   # retained users
    'habit_completed': 'sum',               # total completions
    'is_active': 'mean',                    # retention rate
}).reset_index()
```

---

## 6. Machine Learning Pipeline

### 6.1 Model Categories

```
┌─────────────────────────────────────────────────────────────┐
│                    ML MODEL CATEGORIES                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. PREDICTION MODELS                                        │
│     ├── Habit Success Predictor                             │
│     ├── Streak Break Predictor                              │
│     ├── User Churn Predictor                                │
│     └── Optimal Time Predictor                              │
│                                                              │
│  2. FORECASTING MODELS                                       │
│     ├── Weekly Success Rate Forecast                        │
│     ├── Streak Duration Forecast                            │
│     ├── User Engagement Forecast                            │
│     └── Habit Formation Timeline                            │
│                                                              │
│  3. RECOMMENDATION MODELS                                    │
│     ├── Trigger Recommendation                              │
│     ├── Time Slot Recommendation                            │
│     ├── Strategy Recommendation                             │
│     ├── Habit Pairing Recommendation                        │
│     └── Recovery Action Recommendation                      │
│                                                              │
│  4. CLUSTERING MODELS                                        │
│     ├── User Behavior Clusters                              │
│     ├── Habit Difficulty Clusters                           │
│     └── Success Pattern Clusters                            │
│                                                              │
│  5. ANOMALY DETECTION                                        │
│     ├── Unusual Behavior Detection                          │
│     ├── Struggle Pattern Detection                          │
│     └── Breakthrough Detection                              │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 6.2 Feature Sets by Model

```python
# Habit Success Prediction Features
HABIT_SUCCESS_FEATURES = [
    # Historical performance
    'success_rate_7d', 'success_rate_30d', 'success_rate_all_time',
    'current_streak', 'longest_streak', 'streak_momentum',
    'days_since_last_failure', 'consecutive_successes',

    # Temporal context
    'hour_of_day', 'day_of_week', 'is_weekend', 'is_monday',
    'time_since_trigger', 'time_of_day_bucket',

    # Habit characteristics
    'habit_type', 'habit_age_days', 'habit_category',
    'frequency', 'priority', 'has_reminder',

    # User context
    'user_tenure_days', 'total_active_habits',
    'avg_daily_completion_rate', 'notification_engagement_rate',

    # Strategy engagement
    'has_triggers', 'trigger_count', 'has_friction',
    'friction_implemented_count', 'has_temptation_bundle',

    # Social factors
    'has_partner', 'is_shared_with_partner',
    'partner_success_today'
]

# Churn Prediction Features
CHURN_PREDICTION_FEATURES = [
    'days_since_last_session', 'sessions_last_7d', 'sessions_last_30d',
    'success_rate_trend', 'streak_break_count_30d',
    'habits_deleted_30d', 'habits_archived_30d',
    'notification_dismiss_rate', 'avg_session_duration_trend',
    'reflection_completion_rate', 'settings_changed_recently'
]

# Time Recommendation Features
TIME_RECOMMENDATION_FEATURES = [
    'historical_success_by_hour', 'user_activity_pattern',
    'completion_time_variance', 'preferred_time_of_day',
    'work_schedule_indicators', 'weekend_vs_weekday_pattern',
    'notification_open_times', 'similar_users_optimal_times'
]
```

### 6.3 Model Architectures

```python
# 1. Habit Success Predictor (XGBoost)
from xgboost import XGBClassifier

habit_success_model = XGBClassifier(
    n_estimators=100,
    max_depth=6,
    learning_rate=0.1,
    objective='binary:logistic',
    eval_metric='auc'
)

# 2. Streak Duration Forecast (LSTM)
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout

streak_forecast_model = Sequential([
    LSTM(64, return_sequences=True, input_shape=(sequence_length, n_features)),
    Dropout(0.2),
    LSTM(32),
    Dropout(0.2),
    Dense(16, activation='relu'),
    Dense(1, activation='linear')  # Days until streak break
])

# 3. Recommendation Model (Two-Tower)
import tensorflow as tf

class RecommendationModel(tf.keras.Model):
    def __init__(self, user_embedding_dim, item_embedding_dim):
        super().__init__()
        # User tower
        self.user_tower = tf.keras.Sequential([
            tf.keras.layers.Dense(64, activation='relu'),
            tf.keras.layers.Dense(user_embedding_dim)
        ])
        # Item tower (triggers/times/strategies)
        self.item_tower = tf.keras.Sequential([
            tf.keras.layers.Dense(64, activation='relu'),
            tf.keras.layers.Dense(item_embedding_dim)
        ])

    def call(self, inputs):
        user_features, item_features = inputs
        user_embedding = self.user_tower(user_features)
        item_embedding = self.item_tower(item_features)
        return tf.reduce_sum(user_embedding * item_embedding, axis=1)

# 4. User Clustering (K-Means + UMAP)
from sklearn.cluster import KMeans
from umap import UMAP

def cluster_users(user_features, n_clusters=5):
    # Dimensionality reduction
    reducer = UMAP(n_components=10, random_state=42)
    reduced_features = reducer.fit_transform(user_features)

    # Clustering
    kmeans = KMeans(n_clusters=n_clusters, random_state=42)
    clusters = kmeans.fit_predict(reduced_features)

    return clusters, kmeans, reducer
```

---

## 7. Forecasting & Predictive Analysis

### 7.1 Forecasting Models

```python
# Weekly Success Rate Forecast
class WeeklySuccessForecast:
    """Forecasts user's success rate for next week"""

    def __init__(self):
        self.model = Prophet(
            yearly_seasonality=True,
            weekly_seasonality=True,
            daily_seasonality=False
        )

    def prepare_data(self, user_daily_logs):
        df = user_daily_logs.groupby('date').agg({
            'is_success': 'mean'
        }).reset_index()
        df.columns = ['ds', 'y']
        return df

    def forecast(self, user_daily_logs, periods=7):
        df = self.prepare_data(user_daily_logs)
        self.model.fit(df)
        future = self.model.make_future_dataframe(periods=periods)
        forecast = self.model.predict(future)
        return forecast[['ds', 'yhat', 'yhat_lower', 'yhat_upper']].tail(periods)


# Habit Formation Timeline Predictor
class HabitFormationPredictor:
    """Predicts when a habit will become 'automatic' (>90% success)"""

    def __init__(self):
        self.model = GradientBoostingRegressor()

    def features_for_prediction(self, habit_data):
        return [
            habit_data['habit_type'],           # BUILD habits form differently than BREAK
            habit_data['initial_success_rate'], # First week performance
            habit_data['trigger_clarity'],      # How clear is the trigger
            habit_data['reward_present'],       # Has reward defined
            habit_data['similar_habits_avg'],   # How long similar habits took
            habit_data['user_experience'],      # User's overall track record
        ]

    def predict_days_to_formation(self, habit_data):
        features = self.features_for_prediction(habit_data)
        return max(21, self.model.predict([features])[0])  # Minimum 21 days


# Streak Break Risk Predictor
class StreakBreakRiskPredictor:
    """Predicts probability of breaking streak today"""

    def __init__(self):
        self.model = XGBClassifier()

    def get_risk_score(self, habit_context):
        features = [
            habit_context['current_streak'],
            habit_context['day_of_week'],
            habit_context['is_weekend'],
            habit_context['hours_since_usual_completion'],
            habit_context['notification_opened'],
            habit_context['similar_days_success_rate'],
            habit_context['recent_failure_count'],
            habit_context['user_mood_indicator'],  # From reflection sentiment
        ]
        return self.model.predict_proba([features])[0][1]  # Probability of failure
```

### 7.2 Predictive Alerts System

```python
# Alert Generation Logic
class PredictiveAlertSystem:

    ALERT_TYPES = {
        'streak_at_risk': {
            'threshold': 0.6,  # 60% chance of breaking
            'message': "Your {habit_name} streak might be at risk today. Want to set an extra reminder?",
            'action': 'schedule_extra_reminder'
        },
        'optimal_time_passing': {
            'threshold': 30,  # minutes past optimal time
            'message': "It's your best time for {habit_name}. Ready to start?",
            'action': 'send_nudge'
        },
        'weekly_dip_predicted': {
            'threshold': 0.15,  # 15% drop predicted
            'message': "This week might be challenging. Let's review your strategies.",
            'action': 'show_strategy_review'
        },
        'breakthrough_predicted': {
            'threshold': 0.8,  # 80% chance of hitting milestone
            'message': "You're close to a {streak_count}-day streak! Keep going!",
            'action': 'show_encouragement'
        }
    }

    def check_alerts(self, user_id, current_context):
        alerts = []

        for habit in get_user_habits(user_id):
            # Check streak risk
            risk = streak_risk_model.get_risk_score(habit, current_context)
            if risk > self.ALERT_TYPES['streak_at_risk']['threshold']:
                alerts.append(self.create_alert('streak_at_risk', habit, risk))

            # Check optimal time
            optimal_time = time_model.get_optimal_time(habit)
            if self.is_optimal_time_window(optimal_time, current_context['time']):
                alerts.append(self.create_alert('optimal_time_passing', habit))

        return alerts
```

---

## 8. Recommendation Engine

### 8.1 Trigger Recommendations

```python
class TriggerRecommendationEngine:
    """Recommends triggers based on user behavior and similar users"""

    def __init__(self):
        self.embedding_model = load_trigger_embeddings()
        self.success_model = load_trigger_success_model()

    def recommend_triggers(self, habit, user_context, n_recommendations=5):
        # Get candidate triggers
        candidates = self.get_candidate_triggers(habit.category)

        # Score each candidate
        scored_triggers = []
        for trigger in candidates:
            score = self.score_trigger(trigger, habit, user_context)
            scored_triggers.append((trigger, score))

        # Sort and return top N
        scored_triggers.sort(key=lambda x: x[1], reverse=True)
        return scored_triggers[:n_recommendations]

    def score_trigger(self, trigger, habit, user_context):
        features = {
            'trigger_type': trigger.type,
            'user_schedule_compatibility': self.calc_schedule_match(trigger, user_context),
            'similar_users_success_rate': self.get_similar_users_rate(trigger, habit.category),
            'trigger_specificity': self.calc_specificity(trigger),
            'user_existing_routines': self.get_routine_match(trigger, user_context),
        }
        return self.success_model.predict([features])[0]

    def get_candidate_triggers(self, category):
        """Returns potential triggers for a habit category"""
        return [
            # Time-based
            Trigger('After waking up', 'time', category),
            Trigger('After breakfast', 'time', category),
            Trigger('Before lunch', 'time', category),
            Trigger('After work', 'time', category),
            Trigger('Before bed', 'time', category),

            # Event-based
            Trigger('After checking phone', 'event', category),
            Trigger('After showering', 'event', category),
            Trigger('After commute', 'event', category),

            # Location-based
            Trigger('When at home', 'location', category),
            Trigger('When at gym', 'location', category),
            Trigger('When at desk', 'location', category),
        ]
```

### 8.2 Optimal Time Recommendations

```python
class OptimalTimeRecommender:
    """Recommends best times for habits based on user patterns"""

    def recommend_time(self, habit, user_id):
        # Get user's historical success by hour
        user_patterns = self.get_user_time_patterns(user_id)

        # Get similar users' patterns
        cluster = self.get_user_cluster(user_id)
        cluster_patterns = self.get_cluster_patterns(cluster)

        # Get habit-specific patterns
        habit_patterns = self.get_habit_category_patterns(habit.category)

        # Combine signals
        combined_scores = {}
        for hour in range(24):
            combined_scores[hour] = (
                0.5 * user_patterns.get(hour, 0) +      # Personal history
                0.3 * cluster_patterns.get(hour, 0) +   # Similar users
                0.2 * habit_patterns.get(hour, 0)       # Habit category
            )

        # Find top 3 time slots
        sorted_hours = sorted(combined_scores.items(), key=lambda x: x[1], reverse=True)
        return [
            TimeRecommendation(hour=h, score=s, reason=self.get_reason(h, user_id))
            for h, s in sorted_hours[:3]
        ]

    def get_reason(self, hour, user_id):
        """Explains why this time is recommended"""
        patterns = self.analyze_hour(hour, user_id)
        if patterns['personal_success_high']:
            return f"You complete habits 85% of the time at this hour"
        if patterns['low_distraction']:
            return f"This is typically a low-distraction time for you"
        if patterns['energy_peak']:
            return f"Your energy levels tend to be highest around this time"
        return "Similar users have high success rates at this time"
```

### 8.3 Strategy Recommendations

```python
class StrategyRecommender:
    """Recommends strategies based on habit type and user struggles"""

    STRATEGIES = {
        'BUILD': [
            ('habit_stacking', 'Link to existing habit'),
            ('temptation_bundle', 'Pair with enjoyable activity'),
            ('two_minute_rule', 'Start with 2-minute version'),
            ('environment_design', 'Make cues obvious'),
            ('reward_system', 'Immediate satisfaction'),
            ('accountability', 'Share with partner'),
        ],
        'BREAK': [
            ('cue_elimination', 'Remove triggers from environment'),
            ('friction_addition', 'Make habit harder to do'),
            ('substitution', 'Replace with healthier alternative'),
            ('cost_awareness', 'Journal the true costs'),
            ('commitment_device', 'Create stakes'),
            ('identity_shift', 'Change self-narrative'),
        ]
    }

    def recommend_strategy(self, habit, user_history, current_struggles):
        habit_type = habit.type
        strategies = self.STRATEGIES[habit_type]

        # Analyze what's working for similar users
        similar_success = self.get_similar_user_successes(habit, user_history)

        # Analyze user's unused strategies
        unused = self.get_unused_strategies(user_history, strategies)

        # Score strategies
        recommendations = []
        for strategy_id, description in strategies:
            score = self.score_strategy(
                strategy_id, habit, user_history,
                similar_success, current_struggles
            )
            if score > 0.3:  # Threshold for recommendation
                recommendations.append({
                    'strategy': strategy_id,
                    'description': description,
                    'score': score,
                    'reason': self.get_reason(strategy_id, score, similar_success)
                })

        return sorted(recommendations, key=lambda x: x['score'], reverse=True)[:3]
```

### 8.4 Recovery Recommendations

```python
class RecoveryRecommender:
    """Recommends actions after failures or streak breaks"""

    def get_recovery_plan(self, habit, failure_context):
        analysis = self.analyze_failure(habit, failure_context)

        recommendations = []

        # Time-based issues
        if analysis['wrong_time']:
            recommendations.append({
                'action': 'reschedule',
                'message': f"Your success rate at {failure_context['hour']}:00 is only {analysis['time_success_rate']}%. Consider moving to {analysis['suggested_time']}:00.",
                'priority': 'high'
            })

        # Trigger issues
        if analysis['weak_trigger']:
            recommendations.append({
                'action': 'strengthen_trigger',
                'message': "Your current trigger might not be clear enough. Try: " + analysis['suggested_trigger'],
                'priority': 'high'
            })

        # Motivation issues
        if analysis['motivation_drop']:
            recommendations.append({
                'action': 'review_why',
                'message': "Let's revisit why this habit matters to you.",
                'priority': 'medium'
            })

        # Difficulty issues
        if analysis['too_hard']:
            recommendations.append({
                'action': 'reduce_scope',
                'message': f"Consider starting smaller. Instead of '{habit.name}', try '{habit.minimum_version}'.",
                'priority': 'high'
            })

        # Streak psychology
        if failure_context['broken_streak'] > 7:
            recommendations.append({
                'action': 'reframe',
                'message': f"Remember: {failure_context['broken_streak']} days of progress isn't erased. You've built the neural pathways.",
                'priority': 'high'
            })

        return recommendations
```

---

## 9. Continuous Learning Architecture

### 9.1 Model Retraining Pipeline

```python
class ContinuousLearningPipeline:
    """Manages model retraining and deployment"""

    def __init__(self):
        self.models = {
            'habit_success': HabitSuccessModel(),
            'streak_risk': StreakRiskModel(),
            'time_recommendation': TimeRecommendationModel(),
            'trigger_recommendation': TriggerRecommendationModel(),
            'churn_prediction': ChurnPredictionModel(),
        }
        self.retrain_schedule = {
            'habit_success': 'weekly',
            'streak_risk': 'daily',
            'time_recommendation': 'weekly',
            'trigger_recommendation': 'monthly',
            'churn_prediction': 'weekly',
        }

    def check_data_drift(self, model_name, new_data):
        """Detect if new data distribution differs significantly"""
        reference = self.get_reference_distribution(model_name)

        drift_metrics = {
            'psi': self.calc_psi(reference, new_data),        # Population Stability Index
            'ks_stat': self.calc_ks_statistic(reference, new_data),
            'feature_drift': self.calc_feature_drift(reference, new_data),
        }

        return drift_metrics['psi'] > 0.2 or drift_metrics['ks_stat'] > 0.1

    def retrain_model(self, model_name, new_data):
        """Retrain model with new data"""
        model = self.models[model_name]

        # Combine historical and new data
        training_data = self.prepare_training_data(model_name, new_data)

        # Train new model
        new_model = model.train(training_data)

        # Validate performance
        metrics = self.validate_model(new_model, model_name)

        if self.is_improvement(metrics, model_name):
            self.deploy_model(new_model, model_name)
            self.log_retraining(model_name, metrics)
        else:
            self.log_rejected_retrain(model_name, metrics)

    def validate_model(self, model, model_name):
        """Validate model on holdout set"""
        holdout_data = self.get_holdout_data(model_name)
        predictions = model.predict(holdout_data['X'])

        return {
            'accuracy': accuracy_score(holdout_data['y'], predictions),
            'precision': precision_score(holdout_data['y'], predictions),
            'recall': recall_score(holdout_data['y'], predictions),
            'auc': roc_auc_score(holdout_data['y'], model.predict_proba(holdout_data['X'])[:, 1]),
        }
```

### 9.2 Feedback Loop Integration

```python
class FeedbackLoopManager:
    """Collects and integrates user feedback into models"""

    FEEDBACK_TYPES = [
        'recommendation_accepted',      # User followed suggestion
        'recommendation_rejected',      # User dismissed suggestion
        'recommendation_successful',    # Followed suggestion led to success
        'recommendation_failed',        # Followed suggestion led to failure
        'explicit_rating',             # User rated a recommendation
    ]

    def record_feedback(self, user_id, recommendation_id, feedback_type, context):
        """Record user feedback on a recommendation"""
        feedback = {
            'user_id': anonymize(user_id),
            'recommendation_id': recommendation_id,
            'feedback_type': feedback_type,
            'context': context,
            'timestamp': datetime.now(),
        }
        self.feedback_store.insert(feedback)

        # Trigger immediate learning for critical feedback
        if feedback_type == 'recommendation_failed':
            self.trigger_immediate_review(recommendation_id)

    def aggregate_feedback(self, model_name, time_window='7d'):
        """Aggregate feedback for model improvement"""
        feedback = self.feedback_store.query(
            model=model_name,
            window=time_window
        )

        aggregated = {
            'acceptance_rate': self.calc_acceptance_rate(feedback),
            'success_rate': self.calc_success_rate(feedback),
            'common_rejections': self.analyze_rejections(feedback),
            'user_ratings': self.aggregate_ratings(feedback),
        }

        return aggregated

    def generate_improvement_insights(self, model_name):
        """Generate actionable insights from feedback"""
        feedback = self.aggregate_feedback(model_name)

        insights = []

        if feedback['acceptance_rate'] < 0.5:
            insights.append({
                'issue': 'low_acceptance',
                'action': 'review_recommendation_relevance',
                'data': feedback['common_rejections']
            })

        if feedback['success_rate'] < 0.6:
            insights.append({
                'issue': 'low_effectiveness',
                'action': 'retrain_with_outcome_data',
                'data': feedback['failed_recommendations']
            })

        return insights
```

### 9.3 A/B Testing Framework

```python
class ABTestingFramework:
    """Manages A/B tests for model improvements"""

    def create_experiment(self, name, variants, allocation):
        """Create a new A/B test"""
        experiment = {
            'name': name,
            'variants': variants,  # {'control': model_v1, 'treatment': model_v2}
            'allocation': allocation,  # {'control': 0.5, 'treatment': 0.5}
            'status': 'running',
            'created_at': datetime.now(),
            'metrics': ['acceptance_rate', 'success_rate', 'user_satisfaction'],
        }
        self.experiments.insert(experiment)
        return experiment['id']

    def assign_variant(self, user_id, experiment_id):
        """Deterministically assign user to variant"""
        experiment = self.experiments.get(experiment_id)
        hash_value = hash(f"{user_id}:{experiment_id}") % 100

        cumulative = 0
        for variant, allocation in experiment['allocation'].items():
            cumulative += allocation * 100
            if hash_value < cumulative:
                return variant

        return 'control'  # Default fallback

    def analyze_experiment(self, experiment_id):
        """Analyze experiment results"""
        experiment = self.experiments.get(experiment_id)

        results = {}
        for variant in experiment['variants']:
            variant_data = self.get_variant_data(experiment_id, variant)
            results[variant] = {
                'sample_size': len(variant_data),
                'acceptance_rate': variant_data['accepted'].mean(),
                'success_rate': variant_data['successful'].mean(),
                'confidence_interval': self.calc_ci(variant_data),
            }

        # Statistical significance test
        results['statistical_significance'] = self.calc_significance(
            results['control'], results['treatment']
        )

        return results
```

---

## 10. Technical Implementation

### 10.1 Data Collection SDK (Android)

```kotlin
// Analytics SDK Interface
interface AnalyticsSDK {
    fun trackEvent(event: AnalyticsEvent)
    fun setUserProperty(key: String, value: Any)
    fun setConsentLevel(level: ConsentTier)
    fun flush()
}

// Implementation
class HabitAnalytics(
    private val localStorage: AnalyticsLocalStorage,
    private val remoteSync: AnalyticsSyncService,
    private val consentManager: ConsentManager
) : AnalyticsSDK {

    override fun trackEvent(event: AnalyticsEvent) {
        // Check consent level
        if (!consentManager.canTrack(event.requiredConsentTier)) {
            return
        }

        // Enrich with context
        val enrichedEvent = event.copy(
            context = EventContext(
                timeOfDay = getCurrentTimeOfDayBucket(),
                dayOfWeek = LocalDate.now().dayOfWeek.value,
                isWeekend = isWeekend(),
                localHour = LocalTime.now().hour,
                appVersion = BuildConfig.VERSION_NAME,
                sessionDurationMs = sessionManager.currentSessionDuration()
            )
        )

        // Store locally
        localStorage.store(enrichedEvent)

        // Sync if batch is full or important event
        if (localStorage.pendingCount() > BATCH_SIZE || event.isHighPriority) {
            remoteSync.sync()
        }
    }
}

// Consent Manager
class ConsentManager(private val preferences: DataStore<Preferences>) {

    enum class ConsentTier {
        ESSENTIAL,      // No consent needed
        ENHANCED,       // Opt-in analytics
        RESEARCH,       // Explicit research consent
        PERSONAL        // Personal data with identity
    }

    private val consentLevel = MutableStateFlow(ConsentTier.ESSENTIAL)

    suspend fun setConsentLevel(level: ConsentTier) {
        preferences.edit { it[CONSENT_LEVEL_KEY] = level.name }
        consentLevel.value = level
    }

    fun canTrack(requiredLevel: ConsentTier): Boolean {
        return consentLevel.value.ordinal >= requiredLevel.ordinal
    }
}
```

### 10.2 Backend Data Pipeline

```yaml
# Data Pipeline Architecture
pipeline:
  ingestion:
    - source: mobile_app
      format: json
      transport: https
      batching: 100_events_or_60_seconds

  processing:
    - stage: validation
      rules: ./validation_rules.yaml
      output: valid_events, invalid_events

    - stage: enrichment
      add_fields:
        - geo_region (from IP, then discard IP)
        - device_category
        - user_cohort

    - stage: anonymization
      hash_fields: [user_id, habit_id]
      round_timestamps: hour
      generalize: [age -> age_range, city -> country]

    - stage: aggregation
      outputs:
        - daily_user_summary
        - daily_habit_summary
        - hourly_app_metrics

  storage:
    raw_events:
      destination: bigquery
      retention: 90_days
      partition_by: date

    aggregated:
      destination: bigquery
      retention: 2_years
      partition_by: month

    ml_features:
      destination: feature_store
      refresh: daily

  ml_pipeline:
    schedule: daily_2am
    steps:
      - extract_features
      - check_data_drift
      - retrain_if_needed
      - validate_models
      - deploy_approved
```

### 10.3 Monitoring Dashboard

```python
# Key Metrics to Monitor

MONITORING_METRICS = {
    # Data Quality
    'event_volume': 'Events collected per hour',
    'invalid_event_rate': 'Percentage of events failing validation',
    'data_latency': 'Time from event to storage',
    'missing_fields_rate': 'Events with missing required fields',

    # Model Performance
    'prediction_accuracy': 'Rolling 7-day accuracy',
    'recommendation_acceptance': 'User acceptance rate',
    'recommendation_effectiveness': 'Success rate after following recommendation',
    'model_latency': 'Inference time (ms)',

    # User Engagement
    'daily_active_users': 'DAU',
    'habits_tracked_daily': 'Total habit completions',
    'notification_engagement': 'Open rate for notifications',
    'feature_adoption': 'Usage of ML-powered features',

    # Privacy
    'consent_rate': 'Users opting into enhanced analytics',
    'data_deletion_requests': 'GDPR deletion requests',
    'anonymization_failures': 'Events with PII leakage',
}
```

---

## Discussion Points

### Questions to Resolve:

1. **Consent UI Design**: How should we present consent options? Progressive disclosure vs upfront?

2. **Data Retention**: How long should we keep different data types?
   - Raw events: 90 days?
   - Aggregated: 2 years?
   - ML training data: Indefinite but anonymized?

3. **On-Device vs Cloud ML**:
   - On-device: Privacy-preserving, works offline
   - Cloud: More powerful models, easier updates
   - Hybrid approach?

4. **Feedback Collection**: How to collect feedback without being annoying?
   - Implicit (user actions)
   - Explicit (occasional prompts)
   - Both?

5. **Model Update Frequency**:
   - User-specific models: On each sync?
   - Global models: Weekly?
   - How to handle model versioning?

6. **Privacy Compliance**:
   - GDPR requirements
   - CCPA requirements
   - App Store guidelines (Apple/Google)

7. **Data Export for Users**:
   - What format?
   - What data to include?
   - How to make it useful?

---

## Next Steps

1. [ ] Finalize consent tiers and UI
2. [ ] Implement analytics event tracking
3. [ ] Set up backend data pipeline
4. [ ] Create initial ML models
5. [ ] Build monitoring dashboard
6. [ ] A/B testing framework
7. [ ] User feedback collection
8. [ ] Privacy compliance audit

---

*This document is a living specification. Update as requirements evolve.*
