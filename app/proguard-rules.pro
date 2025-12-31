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
