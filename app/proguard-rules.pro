# Proguard Rules for Habit Architect

# Keep Room entities
-keep class com.habitarchitect.data.local.database.entity.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }

# Firebase Auth
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class com.habitarchitect.** {
  @com.google.firebase.** *;
}

# Kotlin serialization (if used later)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Compose
-dontwarn androidx.compose.**

# Keep data classes for reflection
-keepclassmembers class * {
    @androidx.room.* <fields>;
}

# WorkManager
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}

# Glance Widgets
-keep class * extends androidx.glance.appwidget.GlanceAppWidget { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver { *; }

# Keep domain models
-keep class com.habitarchitect.domain.model.** { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# BroadcastReceivers
-keep class * extends android.content.BroadcastReceiver { *; }

# Keep R classes
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Google Play Services Auth
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }

# Konfetti animation library
-dontwarn nl.dionsegijn.konfetti.**
-keep class nl.dionsegijn.konfetti.** { *; }
