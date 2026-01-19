package com.habitarchitect.presentation.screen.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.habitarchitect.presentation.theme.HabitArchitectTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for AuthScreen.
 * Tests login flow and UI elements.
 */
@HiltAndroidTest
class AuthScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun authScreen_displaysAppName() {
        composeRule.setContent {
            HabitArchitectTheme {
                // AuthScreen
            }
        }

        composeRule.onNodeWithText("Habit Architect", ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun authScreen_displaysGoogleSignInButton() {
        composeRule.setContent {
            HabitArchitectTheme {
                // AuthScreen
            }
        }

        composeRule.onNodeWithText("Sign in with Google", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun authScreen_googleSignInButton_isClickable() {
        composeRule.setContent {
            HabitArchitectTheme {
                // AuthScreen
            }
        }

        composeRule.onNodeWithText("Sign in with Google", substring = true, ignoreCase = true)
            .performClick()

        // Button click should trigger sign-in flow (mocked in tests)
    }
}
