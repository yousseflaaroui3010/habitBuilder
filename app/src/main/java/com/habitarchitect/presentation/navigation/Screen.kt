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

    object TemplateConfirm : Screen("template/{templateId}/confirm") {
        fun createRoute(templateId: String) = "template/$templateId/confirm"
    }

    object QuickAddHabit : Screen("add_habit/quick/{type}?templateId={templateId}") {
        fun createRoute(type: String, templateId: String? = null): String {
            return if (templateId != null) {
                "add_habit/quick/$type?templateId=$templateId"
            } else {
                "add_habit/quick/$type"
            }
        }
    }

    object ResistanceList : Screen("habit/{habitId}/list") {
        fun createRoute(habitId: String) = "habit/$habitId/list"
    }

    object EditHabit : Screen("habit/{habitId}/edit") {
        fun createRoute(habitId: String) = "habit/$habitId/edit"
    }

    object Settings : Screen("settings")

    object Dashboard : Screen("dashboard")

    object Profile : Screen("profile")

    object WeeklyReflection : Screen("weekly_reflection")

    object Identity : Screen("identity")

    object TemptationBundle : Screen("temptation_bundle/{habitId}") {
        fun createRoute(habitId: String) = "temptation_bundle/$habitId"
    }

    object CueElimination : Screen("cue_elimination/{habitId}") {
        fun createRoute(habitId: String) = "cue_elimination/$habitId"
    }

    object CostJournal : Screen("cost_journal/{habitId}") {
        fun createRoute(habitId: String) = "cost_journal/$habitId"
    }

    object FrictionTracker : Screen("friction_tracker/{habitId}") {
        fun createRoute(habitId: String) = "friction_tracker/$habitId"
    }

    object PartnerManagement : Screen("partners")

    object PartnerView : Screen("partner_view/{partnerId}") {
        fun createRoute(partnerId: String) = "partner_view/$partnerId"
    }

    object AcceptPartnerInvite : Screen("partner/invite/{inviteCode}") {
        fun createRoute(inviteCode: String) = "partner/invite/$inviteCode"
    }
}
