package com.habitarchitect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.habitarchitect.presentation.navigation.HabitArchitectNavHost
import com.habitarchitect.presentation.theme.HabitArchitectTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity that hosts the Compose navigation graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Parse deep link invite code if present
        val inviteCode = parseInviteCodeFromIntent(intent)

        setContent {
            HabitArchitectTheme {
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
