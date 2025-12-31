package com.habitarchitect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.habitarchitect.presentation.screen.addhabit.AddHabitSocraticScreen
import com.habitarchitect.presentation.screen.addhabit.AddHabitTypeSelectionScreen
import com.habitarchitect.presentation.screen.auth.SignInScreen
import com.habitarchitect.presentation.screen.habitdetail.HabitDetailScreen
import com.habitarchitect.presentation.screen.habitdetail.ResistanceListScreen
import com.habitarchitect.presentation.screen.home.HomeScreen
import com.habitarchitect.presentation.screen.onboarding.OnboardingScreen
import com.habitarchitect.presentation.screen.settings.PartnerManagementScreen
import com.habitarchitect.presentation.screen.settings.SettingsScreen
import com.habitarchitect.presentation.screen.splash.SplashScreen
import com.habitarchitect.presentation.screen.templates.TemplateBrowserScreen

/**
 * Main navigation host for Habit Architect.
 */
@Composable
fun HabitArchitectNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Sign In
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate(Screen.HabitDetail.createRoute(habitId))
                },
                onNavigateToAddHabit = {
                    navController.navigate(Screen.AddHabitTypeSelection.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Habit Detail
        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            HabitDetailScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResistanceList = {
                    navController.navigate(Screen.ResistanceList.createRoute(habitId))
                }
            )
        }

        // Add Habit Type Selection
        composable(Screen.AddHabitTypeSelection.route) {
            AddHabitTypeSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSelectType = { type ->
                    navController.navigate(Screen.AddHabitSocratic.createRoute(type.name))
                },
                onBrowseTemplates = { type ->
                    navController.navigate(Screen.TemplateBrowser.createRoute(type.name))
                }
            )
        }

        // Add Habit Socratic Flow
        composable(
            route = Screen.AddHabitSocratic.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@composable
            AddHabitSocraticScreen(
                habitType = type,
                onNavigateBack = { navController.popBackStack() },
                onHabitCreated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.AddHabitTypeSelection.route) { inclusive = true }
                    }
                }
            )
        }

        // Template Browser
        composable(
            route = Screen.TemplateBrowser.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@composable
            TemplateBrowserScreen(
                habitType = type,
                onNavigateBack = { navController.popBackStack() },
                onTemplateSelected = { templateId ->
                    navController.navigate(Screen.AddHabitSocratic.createRoute(type)) {
                        // Pass template ID via saved state handle
                    }
                }
            )
        }

        // Resistance/Attraction List
        composable(
            route = Screen.ResistanceList.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            ResistanceListScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPartners = {
                    navController.navigate(Screen.PartnerManagement.route)
                },
                onSignOut = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Partner Management
        composable(Screen.PartnerManagement.route) {
            PartnerManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
