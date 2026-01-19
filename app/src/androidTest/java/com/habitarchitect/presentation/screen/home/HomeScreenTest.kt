package com.habitarchitect.presentation.screen.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.habitarchitect.presentation.theme.HabitArchitectTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for HomeScreen.
 * Tests core habit tracking interactions.
 */
@HiltAndroidTest
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun homeScreen_displaysEmptyState_whenNoHabits() {
        composeRule.setContent {
            HabitArchitectTheme {
                // HomeScreen would show empty state
            }
        }

        // Verify empty state message is shown
        composeRule.onNodeWithText("No habits yet", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsFab_forAddingHabit() {
        composeRule.setContent {
            HabitArchitectTheme {
                // HomeScreen with FAB
            }
        }

        // Verify FAB is visible
        composeRule.onNodeWithContentDescription("Add habit")
            .assertIsDisplayed()
    }

    @Test
    fun habitCard_showsCheckAndXButtons_whenNotCompleted() {
        composeRule.setContent {
            HabitArchitectTheme {
                // HabitCard in uncompleted state
            }
        }

        // Verify action buttons are visible
        composeRule.onNodeWithContentDescription("Mark success")
            .assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Mark failure")
            .assertIsDisplayed()
    }

    @Test
    fun habitCard_markingSuccess_updatesUI() {
        composeRule.setContent {
            HabitArchitectTheme {
                // HabitCard
            }
        }

        // Click success button
        composeRule.onNodeWithContentDescription("Mark success")
            .performClick()

        // Verify completion icon shown
        composeRule.onNodeWithContentDescription("Completed")
            .assertIsDisplayed()
    }
}
