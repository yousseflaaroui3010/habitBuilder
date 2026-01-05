package com.habitarchitect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.habitarchitect.presentation.screen.addhabit.QuickAddHabitScreen
import com.habitarchitect.presentation.screen.auth.SignInScreen
import com.habitarchitect.presentation.screen.dashboard.DashboardScreen
import com.habitarchitect.presentation.screen.habitdetail.EditHabitScreen
import com.habitarchitect.presentation.screen.habitdetail.HabitDetailScreen
import com.habitarchitect.presentation.screen.habitdetail.ResistanceListScreen
import com.habitarchitect.presentation.screen.home.HomeScreen
import com.habitarchitect.presentation.screen.main.MainScreen
import com.habitarchitect.presentation.screen.onboarding.OnboardingScreen
import com.habitarchitect.presentation.screen.profile.ProfileScreen
import com.habitarchitect.presentation.screen.partner.AcceptPartnerInviteScreen
import com.habitarchitect.presentation.screen.partner.PartnerViewScreen
import com.habitarchitect.presentation.screen.settings.PartnerManagementScreen
import com.habitarchitect.presentation.screen.settings.SettingsScreen
import com.habitarchitect.presentation.screen.splash.SplashScreen
import com.habitarchitect.presentation.screen.templates.TemplateBrowserScreen
import com.habitarchitect.presentation.screen.templates.TemplateConfirmScreen
import com.habitarchitect.presentation.screen.breaktools.CostJournalScreen
import com.habitarchitect.presentation.screen.breaktools.CueEliminationScreen
import com.habitarchitect.presentation.screen.breaktools.FrictionTrackerScreen
import com.habitarchitect.presentation.screen.bundle.TemptationBundleScreen
import com.habitarchitect.presentation.screen.reflection.WeeklyReflectionScreen
import com.habitarchitect.presentation.screen.identity.IdentityScreen

/**
 * Main navigation host for Habit Architect.
 */
@Composable
fun HabitArchitectNavHost(
    navController: NavHostController = rememberNavController(),
    deepLinkInviteCode: String? = null
) {
    // Handle deep link navigation after auth
    val pendingInviteCode = deepLinkInviteCode
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
                    // If there's a pending invite, navigate to accept screen first
                    if (pendingInviteCode != null) {
                        navController.navigate(Screen.AcceptPartnerInvite.createRoute(pendingInviteCode)) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
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

        // Main (with bottom navigation)
        composable(Screen.Home.route) {
            MainScreen(
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate(Screen.HabitDetail.createRoute(habitId))
                },
                onNavigateToAddHabit = {
                    navController.navigate(Screen.AddHabitTypeSelection.route)
                },
                onNavigateToEditHabit = { habitId ->
                    navController.navigate(Screen.EditHabit.createRoute(habitId))
                },
                onNavigateToPartners = {
                    navController.navigate(Screen.PartnerManagement.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToWeeklyReflection = {
                    navController.navigate(Screen.WeeklyReflection.route)
                },
                onSignOut = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
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
                },
                onNavigateToEditHabit = {
                    navController.navigate(Screen.EditHabit.createRoute(habitId))
                },
                onNavigateToCueElimination = {
                    navController.navigate(Screen.CueElimination.createRoute(habitId))
                },
                onNavigateToCostJournal = {
                    navController.navigate(Screen.CostJournal.createRoute(habitId))
                },
                onNavigateToTemptationBundle = {
                    navController.navigate(Screen.TemptationBundle.createRoute(habitId))
                },
                onNavigateToFrictionTracker = {
                    navController.navigate(Screen.FrictionTracker.createRoute(habitId))
                }
            )
        }

        // Edit Habit
        composable(
            route = Screen.EditHabit.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            EditHabitScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Add Habit Type Selection
        composable(Screen.AddHabitTypeSelection.route) {
            AddHabitTypeSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSelectType = { type ->
                    navController.navigate(Screen.QuickAddHabit.createRoute(type.name))
                },
                onBrowseTemplates = { type ->
                    navController.navigate(Screen.TemplateBrowser.createRoute(type.name))
                }
            )
        }

        // Quick Add Habit (optionally pre-filled from template)
        composable(
            route = Screen.QuickAddHabit.route,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("templateId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            QuickAddHabitScreen(
                onNavigateBack = { navController.popBackStack() },
                onHabitCreated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.AddHabitTypeSelection.route) { inclusive = true }
                    }
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
                    navController.navigate(Screen.TemplateConfirm.createRoute(templateId))
                }
            )
        }

        // Template Confirm (one-tap habit creation from template)
        composable(
            route = Screen.TemplateConfirm.route,
            arguments = listOf(navArgument("templateId") { type = NavType.StringType })
        ) {
            TemplateConfirmScreen(
                onNavigateBack = { navController.popBackStack() },
                onHabitCreated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.AddHabitTypeSelection.route) { inclusive = true }
                    }
                },
                onCustomize = { type, templateId ->
                    // Navigate to QuickAddHabit with template pre-filled
                    navController.navigate(Screen.QuickAddHabit.createRoute(type, templateId)) {
                        popUpTo(Screen.TemplateConfirm.route) { inclusive = true }
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
                onNavigateToWeeklyReflection = {
                    navController.navigate(Screen.WeeklyReflection.route)
                },
                onNavigateToIdentity = {
                    navController.navigate(Screen.Identity.route)
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
                onNavigateBack = { navController.popBackStack() },
                onViewPartner = { partnerId ->
                    navController.navigate(Screen.PartnerView.createRoute(partnerId))
                }
            )
        }

        // Partner View (view partner's shared habits)
        composable(
            route = Screen.PartnerView.route,
            arguments = listOf(navArgument("partnerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val partnerId = backStackEntry.arguments?.getString("partnerId") ?: return@composable
            PartnerViewScreen(
                partnerId = partnerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Accept Partner Invite (via deep link)
        composable(
            route = Screen.AcceptPartnerInvite.route,
            arguments = listOf(navArgument("inviteCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val inviteCode = backStackEntry.arguments?.getString("inviteCode") ?: return@composable
            AcceptPartnerInviteScreen(
                inviteCode = inviteCode,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.AcceptPartnerInvite.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Temptation Bundle (Make It Attractive)
        composable(
            route = Screen.TemptationBundle.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            TemptationBundleScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        // Cue Elimination (Make It Invisible - for BREAK habits)
        composable(
            route = Screen.CueElimination.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            CueEliminationScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Cost Journal (Make It Unattractive - for BREAK habits)
        composable(
            route = Screen.CostJournal.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            CostJournalScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Weekly Reflection
        composable(Screen.WeeklyReflection.route) {
            WeeklyReflectionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Identity Page
        composable(Screen.Identity.route) {
            IdentityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Friction Tracker (Make It Difficult - for BREAK habits)
        composable(
            route = Screen.FrictionTracker.route,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            FrictionTrackerScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
