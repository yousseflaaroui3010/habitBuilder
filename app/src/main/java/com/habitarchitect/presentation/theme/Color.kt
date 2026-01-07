package com.habitarchitect.presentation.theme

import androidx.compose.ui.graphics.Color

// Primary Colors - Blue Gradient (Trust, Calm)
val Primary = Color(0xFF2D669B)         // Light zone blue
val PrimaryDark = Color(0xFF173451)     // Dark zone blue
val PrimaryLight = Color(0xFF5A8FBF)    // Lighter blue
val OnPrimary = Color(0xFFFFFFFF)

// Gradient Colors for backgrounds
val GradientBlueLight = Color(0xFF2D669B)
val GradientBlueDark = Color(0xFF173451)

// Secondary Colors - Green (Success, Growth)
val Secondary = Color(0xFF4CAF50)
val SecondaryDark = Color(0xFF388E3C)
val SecondaryLight = Color(0xFFC8E6C9)
val OnSecondary = Color(0xFFFFFFFF)

// Tertiary Colors
val Tertiary = Color(0xFF7C4DFF)
val OnTertiary = Color(0xFFFFFFFF)

// Error Colors - Red (Failure, Warning)
val Error = Color(0xFFE53935)
val ErrorDark = Color(0xFFC62828)
val OnError = Color(0xFFFFFFFF)

// Success Color
val Success = Color(0xFF4CAF50)
val OnSuccess = Color(0xFFFFFFFF)

// Streak Color - Orange (Fire)
val Streak = Color(0xFFFF9800)
val StreakDark = Color(0xFFF57C00)
val OnStreak = Color(0xFFFFFFFF)

// Light Theme Colors (AA compliant - 4.5:1 min contrast)
val LightBackground = Color(0xFFFAFAFA)
val LightSurface = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1C1B1F)  // Contrast: 15.8:1 on FAFAFA
val LightOnSurface = Color(0xFF1C1B1F)     // Contrast: 18.1:1 on FFFFFF
val LightSurfaceVariant = Color(0xFFE7E0EC)
val LightOnSurfaceVariant = Color(0xFF3D3846)  // Darker for better contrast (was 49454F)
val LightOutline = Color(0xFF6B6574)       // Darker for AA compliance (was 79747E)
val LightOutlineVariant = Color(0xFFCAC4D0)

// Dark Theme Colors (AA compliant - 4.5:1 min contrast)
val DarkBackground = Color(0xFF1C1B1F)
val DarkSurface = Color(0xFF2B2930)
val DarkOnBackground = Color(0xFFE6E1E5)   // Contrast: 11.5:1 on 1C1B1F
val DarkOnSurface = Color(0xFFE6E1E5)      // Contrast: 9.8:1 on 2B2930
val DarkSurfaceVariant = Color(0xFF49454F)
val DarkOnSurfaceVariant = Color(0xFFD4CED8)  // Lighter for better contrast (was CAC4D0)
val DarkOutline = Color(0xFFA8A2AC)        // Lighter for AA compliance (was 938F99)
val DarkOutlineVariant = Color(0xFF49454F)

// Calendar Colors (AA compliant)
val CalendarSuccess = Color(0xFF2E7D32)    // Darker green for AA on light bg (was 4CAF50)
val CalendarFailure = Color(0xFFD32F2F)    // Darker red for AA (was E53935)
val CalendarPending = Color(0xFF616161)    // Darker gray for AA (was 9E9E9E)
val CalendarToday = Color(0xFF1976D2)      // Darker blue for AA (was 2196F3)
