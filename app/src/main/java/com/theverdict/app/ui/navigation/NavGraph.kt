package com.theverdict.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.theverdict.app.data.local.CaseLoader
import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.ui.screens.case.CasePresentationScreen
import com.theverdict.app.ui.screens.gameover.GameOverScreen
import com.theverdict.app.ui.screens.interrogation.InterrogationScreen
import com.theverdict.app.ui.screens.menu.MainMenuScreen
import com.theverdict.app.ui.screens.reputation.ReputationScreen
import com.theverdict.app.ui.screens.result.ResultScreen
import com.theverdict.app.ui.screens.suspects.SuspectsListScreen
import com.theverdict.app.ui.screens.tutorial.TutorialScreen
import com.theverdict.app.ui.screens.verdict.VerdictScreen
import com.theverdict.app.ui.screens.victory.VictoryScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val caseLoader = remember { CaseLoader(context) }
    val caseRepository = remember { CaseRepository(caseLoader) }
    val preferencesManager = remember { PreferencesManager(context) }
    val playerRepository = remember { PlayerRepository(preferencesManager) }

    val hasSeenTutorial = remember {
        runBlocking {
            preferencesManager.hasSeenTutorial.first()
        }
    }
    val startDest = if (hasSeenTutorial) Screen.Menu.route else Screen.Tutorial.route

    NavHost(navController = navController, startDestination = startDest) {

        composable(Screen.Tutorial.route) {
            TutorialScreen(
                onFinish = {
                    MainScope().launch {
                        preferencesManager.setTutorialSeen()
                    }
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Tutorial.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Menu.route) {
            MainMenuScreen(
                playerRepository = playerRepository,
                caseRepository = caseRepository,
                onPlay = { themeIndex, caseIndex ->
                    navController.navigate(Screen.CasePresentation.createRoute(themeIndex, caseIndex))
                },
                onReputation = {
                    navController.navigate(Screen.Reputation.route)
                },
                onTutorial = {
                    navController.navigate(Screen.Tutorial.route)
                }
            )
        }

        composable(
            route = Screen.CasePresentation.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val themeIndex = backStackEntry.arguments?.getInt("themeIndex") ?: 0
            val caseIndex = backStackEntry.arguments?.getInt("caseIndex") ?: 0
            CasePresentationScreen(
                caseRepository = caseRepository,
                themeIndex = themeIndex,
                caseIndex = caseIndex,
                onSeeSuspects = {
                    navController.navigate(Screen.SuspectsList.createRoute(themeIndex, caseIndex))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SuspectsList.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val themeIndex = backStackEntry.arguments?.getInt("themeIndex") ?: 0
            val caseIndex = backStackEntry.arguments?.getInt("caseIndex") ?: 0
            SuspectsListScreen(
                caseRepository = caseRepository,
                themeIndex = themeIndex,
                caseIndex = caseIndex,
                onInterrogate = { suspectId ->
                    navController.navigate(Screen.Interrogation.createRoute(themeIndex, caseIndex, suspectId))
                },
                onGoToVerdict = {
                    navController.navigate(Screen.Verdict.createRoute(themeIndex, caseIndex))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Interrogation.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType },
                navArgument("suspectId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val themeIndex = backStackEntry.arguments?.getInt("themeIndex") ?: 0
            val caseIndex = backStackEntry.arguments?.getInt("caseIndex") ?: 0
            val suspectId = backStackEntry.arguments?.getInt("suspectId") ?: 0
            InterrogationScreen(
                caseRepository = caseRepository,
                themeIndex = themeIndex,
                caseIndex = caseIndex,
                suspectId = suspectId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Verdict.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val themeIndex = backStackEntry.arguments?.getInt("themeIndex") ?: 0
            val caseIndex = backStackEntry.arguments?.getInt("caseIndex") ?: 0
            VerdictScreen(
                caseRepository = caseRepository,
                playerRepository = playerRepository,
                themeIndex = themeIndex,
                caseIndex = caseIndex,
                onResult = { isCorrect, pointsChange ->
                    navController.navigate(
                        Screen.Result.createRoute(themeIndex, caseIndex, isCorrect, pointsChange)
                    ) {
                        popUpTo(Screen.CasePresentation.createRoute(themeIndex, caseIndex)) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType },
                navArgument("isCorrect") { type = NavType.BoolType },
                navArgument("pointsChange") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val themeIndex = backStackEntry.arguments?.getInt("themeIndex") ?: 0
            val caseIndex = backStackEntry.arguments?.getInt("caseIndex") ?: 0
            val isCorrect = backStackEntry.arguments?.getBoolean("isCorrect") ?: false
            val pointsChange = backStackEntry.arguments?.getInt("pointsChange") ?: 0
            ResultScreen(
                caseRepository = caseRepository,
                playerRepository = playerRepository,
                themeIndex = themeIndex,
                caseIndex = caseIndex,
                isCorrect = isCorrect,
                pointsChange = pointsChange,
                onNextCase = { nextTheme, nextCase ->
                    navController.navigate(Screen.CasePresentation.createRoute(nextTheme, nextCase)) {
                        popUpTo(Screen.Menu.route)
                    }
                },
                onGameOver = {
                    navController.navigate(Screen.GameOver.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                },
                onVictory = {
                    navController.navigate(Screen.Victory.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                },
                onMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Reputation.route) {
            ReputationScreen(
                playerRepository = playerRepository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.GameOver.route) {
            GameOverScreen(
                playerRepository = playerRepository,
                onRestart = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.GameOver.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Victory.route) {
            VictoryScreen(
                playerRepository = playerRepository,
                onMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Victory.route) { inclusive = true }
                    }
                },
                onInfiniteMode = { themeIndex, caseIndex ->
                    navController.navigate(Screen.CasePresentation.createRoute(themeIndex, caseIndex)) {
                        popUpTo(Screen.Victory.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
