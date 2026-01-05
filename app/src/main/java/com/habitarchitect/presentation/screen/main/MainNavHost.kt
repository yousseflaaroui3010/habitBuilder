package com.habitarchitect.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.habitarchitect.presentation.screen.dashboard.DashboardScreen
import com.habitarchitect.presentation.screen.home.HomeContentScreen
import com.habitarchitect.presentation.screen.identity.IdentityScreen
import com.habitarchitect.presentation.screen.settings.SettingsContentScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onNavigateToHabitDetail: (String) -> Unit,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToEditHabit: (String) -> Unit,
    onNavigateToPartners: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToWeeklyReflection: () -> Unit,
    onSignOut: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            HomeContentScreen(
                onNavigateToHabitDetail = onNavigateToHabitDetail,
                onNavigateToAddHabit = onNavigateToAddHabit,
                onNavigateToEditHabit = onNavigateToEditHabit,
                onNavigateToProfile = onNavigateToProfile
            )
        }

        composable(BottomNavItem.Progress.route) {
            DashboardScreen(
                onNavigateBack = { navController.popBackStack() },
                showBackButton = false,
                onNavigateToWeeklyReflection = onNavigateToWeeklyReflection
            )
        }

        composable(BottomNavItem.Identity.route) {
            IdentityScreen(
                onNavigateBack = { navController.popBackStack() },
                showBackButton = false
            )
        }

        composable(BottomNavItem.Settings.route) {
            SettingsContentScreen(
                onNavigateToPartners = onNavigateToPartners,
                onSignOut = onSignOut
            )
        }
    }
}
