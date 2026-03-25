package com.theverdict.app.ui.navigation

sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object CasePresentation : Screen("case/{themeIndex}/{caseIndex}") {
        fun createRoute(themeIndex: Int, caseIndex: Int) = "case/$themeIndex/$caseIndex"
    }
    data object SuspectsList : Screen("suspects/{themeIndex}/{caseIndex}") {
        fun createRoute(themeIndex: Int, caseIndex: Int) = "suspects/$themeIndex/$caseIndex"
    }
    data object Interrogation : Screen("interrogation/{themeIndex}/{caseIndex}/{suspectId}") {
        fun createRoute(themeIndex: Int, caseIndex: Int, suspectId: Int) = "interrogation/$themeIndex/$caseIndex/$suspectId"
    }
    data object Verdict : Screen("verdict/{themeIndex}/{caseIndex}") {
        fun createRoute(themeIndex: Int, caseIndex: Int) = "verdict/$themeIndex/$caseIndex"
    }
    data object Result : Screen("result/{themeIndex}/{caseIndex}/{isCorrect}/{pointsChange}") {
        fun createRoute(themeIndex: Int, caseIndex: Int, isCorrect: Boolean, pointsChange: Int) =
            "result/$themeIndex/$caseIndex/$isCorrect/$pointsChange"
    }
    data object Reputation : Screen("reputation")
    data object GameOver : Screen("gameover")
    data object Victory : Screen("victory")
    data object Tutorial : Screen("tutorial")
}
