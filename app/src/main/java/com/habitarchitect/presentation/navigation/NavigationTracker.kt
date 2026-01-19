package com.habitarchitect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.habitarchitect.data.analytics.AnalyticsTracker
import kotlinx.coroutines.launch

/**
 * Tracks screen navigation events for analytics.
 * Call this composable once with the main NavController to enable tracking.
 */
@Composable
fun NavigationTracker(
    navController: NavController,
    analyticsTracker: AnalyticsTracker
) {
    val scope = rememberCoroutineScope()
    var previousScreen by remember { mutableStateOf<String?>(null) }
    var screenEntryTime by remember { mutableStateOf(System.currentTimeMillis()) }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val currentRoute = destination.route ?: return@OnDestinationChangedListener
            val screenName = extractScreenName(currentRoute)

            // Track exit from previous screen
            previousScreen?.let { prevScreen ->
                val timeSpent = System.currentTimeMillis() - screenEntryTime
                scope.launch {
                    analyticsTracker.trackScreenExited(
                        screenName = prevScreen,
                        timeSpentMs = timeSpent,
                        exitAction = "navigation"
                    )
                }
            }

            // Track entry to new screen
            scope.launch {
                analyticsTracker.trackScreenViewed(
                    screenName = screenName,
                    fromScreen = previousScreen
                )
            }

            previousScreen = screenName
            screenEntryTime = System.currentTimeMillis()
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}

/**
 * Extracts a clean screen name from a route.
 * Strips argument placeholders and path parameters.
 */
private fun extractScreenName(route: String): String {
    return route
        .substringBefore("?") // Remove query params
        .replace(Regex("\\{[^}]+\\}"), "") // Remove path params like {habitId}
        .replace(Regex("/+$"), "") // Remove trailing slashes
        .replace("/", "_") // Convert path separators to underscores
        .ifEmpty { "unknown" }
}
