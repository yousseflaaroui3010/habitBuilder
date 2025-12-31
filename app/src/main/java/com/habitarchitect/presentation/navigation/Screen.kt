package com.habitarchitect.presentation.navigation

/**
 * Sealed class defining all navigation destinations in the app.
 */
sealed class Screen(val route: String) {

    object Splash : Screen("splash")

    object Onboarding : Screen("onboarding")

    object SignIn : Screen("sign_in")

    object Home : Screen("home")

    object HabitDetail : Screen("habit/{habitId}") {
        fun createRoute(habitId: String) = "habit/$habitId"
    }

    object AddHabitTypeSelection : Screen("add_habit/type")

    object AddHabitSocratic : Screen("add_habit/socratic/{type}") {
        fun createRoute(type: String) = "add_habit/socratic/$type"
    }

    object TemplateBrowser : Screen("templates/{type}") {
        fun createRoute(type: String) = "templates/$type"
    }

    object ResistanceList : Screen("habit/{habitId}/list") {
        fun createRoute(habitId: String) = "habit/$habitId/list"
    }

    object Settings : Screen("settings")

    object PartnerManagement : Screen("partners")

    object PartnerView : Screen("partner_view")

    object AcceptPartnerInvite : Screen("partner/invite/{inviteCode}") {
        fun createRoute(inviteCode: String) = "partner/invite/$inviteCode"
    }
}
