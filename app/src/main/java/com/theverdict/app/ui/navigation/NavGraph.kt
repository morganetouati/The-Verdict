package com.theverdict.app.ui.navigation

import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.theverdict.app.data.local.AdManager
import com.theverdict.app.data.local.CaseLoader
import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.ui.screens.case.CasePresentationScreen
import com.theverdict.app.ui.screens.gameover.GameOverScreen
import com.theverdict.app.ui.screens.interrogation.InterrogationScreen
import com.theverdict.app.ui.screens.menu.MainMenuScreen
import com.theverdict.app.ui.screens.privacy.PrivacyPolicyScreen
import com.theverdict.app.ui.screens.profile.PlayerProfileScreen
import com.theverdict.app.ui.screens.reputation.ReputationScreen
import com.theverdict.app.ui.screens.result.ResultScreen
import com.theverdict.app.ui.screens.splash.SplashScreen
import com.theverdict.app.ui.screens.suspects.SuspectsListScreen
import com.theverdict.app.ui.screens.tutorial.TutorialScreen
import com.theverdict.app.ui.screens.verdict.VerdictScreen
import com.theverdict.app.ui.screens.victory.VictoryScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// ─── Transition helpers ───
private const val DURATION = 400
private const val DURATION_FAST = 300
private const val DURATION_SLOW = 600

private fun slideInFromRight(): EnterTransition =
    slideInHorizontally(tween(DURATION)) { it } + fadeIn(tween(DURATION))

private fun slideOutToLeft(): ExitTransition =
    slideOutHorizontally(tween(DURATION)) { -it / 3 } + fadeOut(tween(DURATION))

private fun slideInFromLeft(): EnterTransition =
    slideInHorizontally(tween(DURATION)) { -it } + fadeIn(tween(DURATION))

private fun slideOutToRight(): ExitTransition =
    slideOutHorizontally(tween(DURATION)) { it } + fadeOut(tween(DURATION))

private fun slideInFromBottom(): EnterTransition =
    slideInVertically(tween(DURATION)) { it / 2 } + fadeIn(tween(DURATION_FAST))

private fun slideOutToBottom(): ExitTransition =
    slideOutVertically(tween(DURATION)) { it / 2 } + fadeOut(tween(DURATION_FAST))

private fun cinematicFadeIn(): EnterTransition =
    fadeIn(tween(DURATION_SLOW)) + scaleIn(tween(DURATION_SLOW), initialScale = 0.92f)

private fun cinematicFadeOut(): ExitTransition =
    fadeOut(tween(DURATION_FAST)) + scaleOut(tween(DURATION_FAST), targetScale = 1.05f)

private fun scaleZoomIn(): EnterTransition =
    scaleIn(tween(DURATION), initialScale = 0.85f) + fadeIn(tween(DURATION))

private fun scaleZoomOut(): ExitTransition =
    scaleOut(tween(DURATION_FAST), targetScale = 0.85f) + fadeOut(tween(DURATION_FAST))

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val caseLoader = remember { CaseLoader(context) }
    val caseRepository = remember { CaseRepository(caseLoader) }
    val preferencesManager = remember { PreferencesManager(context) }
    val playerRepository = remember { PlayerRepository(preferencesManager) }
    val adManager = remember { AdManager(context) }

    val hasSeenTutorial = remember {
        runBlocking {
            preferencesManager.hasSeenTutorial.first()
        }
    }
    val startDest = Screen.Splash.route
    val menuOrTutorial = if (hasSeenTutorial) Screen.Menu.route else Screen.Tutorial.route

    NavHost(navController = navController, startDestination = startDest) {

        // ─── Splash (animated intro) ───
        composable(
            Screen.Splash.route,
            exitTransition = { cinematicFadeOut() }
        ) {
            SplashScreen(
                onFinished = {
                    navController.navigate(menuOrTutorial) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ─── Tutorial ───
        composable(
            Screen.Tutorial.route,
            enterTransition = { cinematicFadeIn() },
            exitTransition = { cinematicFadeOut() }
        ) {
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

        // ─── Menu (fade from splash/tutorial, scale zoom on return) ───
        composable(
            Screen.Menu.route,
            enterTransition = { cinematicFadeIn() },
            exitTransition = { fadeOut(tween(DURATION_FAST)) },
            popEnterTransition = { cinematicFadeIn() },
            popExitTransition = { fadeOut(tween(DURATION_FAST)) }
        ) {
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
                },
                onProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onPrivacyPolicy = {
                    navController.navigate(Screen.PrivacyPolicy.route)
                }
            )
        }

        // ─── Case Presentation (slide up from menu) ───
        composable(
            route = Screen.CasePresentation.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType }
            ),
            enterTransition = { slideInFromBottom() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToBottom() }
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

        // ─── Suspects List (slide from right) ───
        composable(
            route = Screen.SuspectsList.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType }
            ),
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
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

        // ─── Interrogation (scale zoom into suspect) ───
        composable(
            route = Screen.Interrogation.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType },
                navArgument("suspectId") { type = NavType.IntType }
            ),
            enterTransition = { scaleZoomIn() },
            exitTransition = { scaleZoomOut() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { scaleZoomOut() }
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

        // ─── Verdict (dramatic slide up) ───
        composable(
            route = Screen.Verdict.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType }
            ),
            enterTransition = { slideInFromBottom() },
            exitTransition = { cinematicFadeOut() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToBottom() }
        ) { backStackEntry ->
            val themeIndex = backStackEntry.arguments?.getInt("themeIndex") ?: 0
            val caseIndex = backStackEntry.arguments?.getInt("caseIndex") ?: 0
            VerdictScreen(
                caseRepository = caseRepository,
                playerRepository = playerRepository,
                themeIndex = themeIndex,
                caseIndex = caseIndex,
                onResult = { isCorrect, pointsChange ->
                    val activity = context as? Activity
                    if (activity != null) {
                        adManager.onCaseCompleted(activity) {
                            navController.navigate(
                                Screen.Result.createRoute(themeIndex, caseIndex, isCorrect, pointsChange)
                            ) {
                                popUpTo(Screen.CasePresentation.createRoute(themeIndex, caseIndex)) { inclusive = true }
                            }
                        }
                    } else {
                        navController.navigate(
                            Screen.Result.createRoute(themeIndex, caseIndex, isCorrect, pointsChange)
                        ) {
                            popUpTo(Screen.CasePresentation.createRoute(themeIndex, caseIndex)) { inclusive = true }
                        }
                    }
                }
            )
        }

        // ─── Result (crossfade + scale from verdict) ───
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("themeIndex") { type = NavType.IntType },
                navArgument("caseIndex") { type = NavType.IntType },
                navArgument("isCorrect") { type = NavType.BoolType },
                navArgument("pointsChange") { type = NavType.IntType }
            ),
            enterTransition = { scaleZoomIn() },
            exitTransition = { slideOutToBottom() }
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

        // ─── Reputation (slide from right) ───
        composable(
            Screen.Reputation.route,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToRight() },
            popExitTransition = { slideOutToRight() }
        ) {
            ReputationScreen(
                playerRepository = playerRepository,
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Game Over (cinematic slow fade) ───
        composable(
            Screen.GameOver.route,
            enterTransition = { cinematicFadeIn() },
            exitTransition = { cinematicFadeOut() }
        ) {
            GameOverScreen(
                playerRepository = playerRepository,
                onRestart = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.GameOver.route) { inclusive = true }
                    }
                }
            )
        }

        // ─── Victory (cinematic slow fade) ───
        composable(
            Screen.Victory.route,
            enterTransition = { cinematicFadeIn() },
            exitTransition = { cinematicFadeOut() }
        ) {
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

        // ─── Profile (slide from right) ───
        composable(
            Screen.Profile.route,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToRight() },
            popExitTransition = { slideOutToRight() }
        ) {
            PlayerProfileScreen(
                playerRepository = playerRepository,
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Privacy Policy (slide from right) ───
        composable(
            Screen.PrivacyPolicy.route,
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToRight() },
            popExitTransition = { slideOutToRight() }
        ) {
            PrivacyPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
