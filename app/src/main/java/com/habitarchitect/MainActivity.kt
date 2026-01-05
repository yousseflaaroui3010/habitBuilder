package com.habitarchitect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.habitarchitect.data.preferences.ThemeMode
import com.habitarchitect.data.preferences.ThemePreferences
import com.habitarchitect.presentation.navigation.HabitArchitectNavHost
import com.habitarchitect.presentation.theme.HabitArchitectTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Single activity that hosts the Compose navigation graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Parse deep link invite code if present
        val inviteCode = parseInviteCodeFromIntent(intent)

        setContent {
            val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val isDarkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            HabitArchitectTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HabitArchitectNavHost(deepLinkInviteCode = inviteCode)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep links when app is already running
        val inviteCode = parseInviteCodeFromIntent(intent)
        if (inviteCode != null) {
            // Recreate to re-compose with new deep link
            recreate()
        }
    }

    private fun parseInviteCodeFromIntent(intent: Intent?): String? {
        val uri = intent?.data ?: return null
        // Expected format: https://habitarchitect.app/invite/{inviteCode}
        if (uri.host == "habitarchitect.app" && uri.pathSegments.size >= 2) {
            if (uri.pathSegments[0] == "invite") {
                return uri.pathSegments[1]
            }
        }
        return null
    }
}
