package com.habitarchitect.presentation.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.habitarchitect.presentation.theme.HabitArchitectTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Navigation integration tests.
 * Tests screen transitions and deep links.
 */
@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun navigation_fromHome_toCreateHabit() {
        composeRule.setContent {
            HabitArchitectTheme {
                val navController = rememberNavController()
                // Navigation host
            }
        }

        // Click FAB to create habit
        composeRule.onNodeWithContentDescription("Add habit")
            .performClick()

        // Verify create habit screen is shown
        composeRule.onNodeWithText("Create Habit", ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun navigation_fromHome_toSettings() {
        composeRule.setContent {
            HabitArchitectTheme {
                val navController = rememberNavController()
                // Navigation host
            }
        }

        // Click settings icon
        composeRule.onNodeWithContentDescription("Settings")
            .performClick()

        // Verify settings screen is shown
        composeRule.onNodeWithText("Settings", ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun navigation_backButton_returnsToHome() {
        composeRule.setContent {
            HabitArchitectTheme {
                val navController = rememberNavController()
                // Navigation host
            }
        }

        // Navigate to settings
        composeRule.onNodeWithContentDescription("Settings")
            .performClick()

        // Press back
        composeRule.onNodeWithContentDescription("Back")
            .performClick()

        // Verify back on home screen
        composeRule.onNodeWithText("Today", ignoreCase = true)
            .assertIsDisplayed()
    }
}
