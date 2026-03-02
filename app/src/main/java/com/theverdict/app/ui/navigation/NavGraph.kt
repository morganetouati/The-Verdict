package com.theverdict.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.theverdict.app.data.ads.AdManager
import com.theverdict.app.data.audio.AmbientMusicManager
import com.theverdict.app.data.local.DailyPlayManager
import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.data.repository.GameRepositoryImpl
import com.theverdict.app.data.repository.PlayerRepositoryImpl
import com.theverdict.app.data.repository.VideoRepositoryImpl
import com.theverdict.app.domain.model.DetectionTag
import com.theverdict.app.ui.screens.home.HomeScreen
import com.theverdict.app.ui.screens.home.HomeViewModel
import com.theverdict.app.ui.screens.lesson.LessonScreen
import com.theverdict.app.ui.screens.onboarding.OnboardingScreen
import com.theverdict.app.ui.screens.profile.ProfileScreen
import com.theverdict.app.ui.screens.verdict.VerdictScreen
import com.theverdict.app.ui.screens.verdict.VerdictViewModel
import com.theverdict.app.ui.screens.video.VideoScreen
import com.theverdict.app.ui.screens.video.VideoViewModel

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val VIDEO = "video"
    const val VERDICT = "verdict/{videoId}"
    const val LESSON = "lesson"
    const val PROFILE = "profile"

    fun verdict(videoId: String) = "verdict/$videoId"
}

@Composable
fun VerdictNavGraph(
    preferencesManager: PreferencesManager,
    dailyPlayManager: DailyPlayManager,
    adManager: AdManager,
    ambientMusic: AmbientMusicManager,
    navController: NavHostController = rememberNavController()
) {
    // Shared repositories
    val videoRepo = remember { VideoRepositoryImpl(preferencesManager) }
    val playerRepo = remember { PlayerRepositoryImpl(preferencesManager) }
    val gameRepo = remember { GameRepositoryImpl(dailyPlayManager, preferencesManager) }

    // Shared HomeViewModel
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(playerRepo, gameRepo)
    )

    // State to pass between video and verdict screens
    var lastPlayerTags by remember { mutableStateOf<List<DetectionTag>>(emptyList()) }
    var lastIntuitionScore by remember { mutableStateOf(0) }
    var lastCredibility by remember { mutableStateOf(100) }
    var lastUselessClicks by remember { mutableStateOf(0) }
    var lastVideoViewModel by remember { mutableStateOf<VideoViewModel?>(null) }

    // Check if onboarding was completed
    val hasSeenOnboarding by remember {
        mutableStateOf(preferencesManager.hasSeenOnboardingSync())
    }
    var onboardingDone by remember { mutableStateOf(hasSeenOnboarding) }

    val startRoute = if (onboardingDone) Routes.HOME else Routes.ONBOARDING

    // Auto-pause ambient music when on video screen, resume elsewhere
    val currentEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentEntry?.destination?.route) {
        val route = currentEntry?.destination?.route
        ambientMusic.setVideoPlaying(route == Routes.VIDEO)
    }

    // Transition animations
    val enterAnim: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 }
    }
    val exitAnim: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 }
    }

    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        composable(
            Routes.ONBOARDING,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) }
        ) {
            OnboardingScreen(
                onComplete = {
                    preferencesManager.setOnboardingComplete()
                    onboardingDone = true
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Routes.HOME,
            enterTransition = enterAnim,
            exitTransition = exitAnim
        ) {
            HomeScreen(
                viewModel = homeViewModel,
                onStartGame = {
                    navController.navigate(Routes.VIDEO) {
                        launchSingleTop = true
                    }
                },
                onOpenProfile = {
                    navController.navigate(Routes.PROFILE) {
                        launchSingleTop = true
                    }
                },
                onOpenLessons = {
                    navController.navigate(Routes.LESSON) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            Routes.VIDEO,
            enterTransition = enterAnim,
            exitTransition = exitAnim
        ) {
            val appContext = LocalContext.current.applicationContext
            val videoViewModel: VideoViewModel = viewModel(
                factory = VideoViewModel.Factory(videoRepo, appContext)
            )
            lastVideoViewModel = videoViewModel

            VideoScreen(
                viewModel = videoViewModel,
                onVideoComplete = { videoId ->
                    // Save tags, score, credibility, and useless clicks before navigating
                    lastPlayerTags = videoViewModel.evaluateTags()
                    lastIntuitionScore = videoViewModel.calculateScore()
                    lastCredibility = videoViewModel.getCredibility()
                    lastUselessClicks = videoViewModel.getUselessClicks()
                    navController.navigate(Routes.verdict(videoId)) {
                        popUpTo(Routes.VIDEO) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Routes.VERDICT,
            enterTransition = enterAnim,
            exitTransition = exitAnim
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: return@composable

            val verdictViewModel: VerdictViewModel = viewModel(
                factory = VerdictViewModel.Factory(
                    videoId = videoId,
                    playerTags = lastPlayerTags,
                    intuitionScore = lastIntuitionScore,
                    credibility = lastCredibility,
                    uselessClicks = lastUselessClicks,
                    videoRepo = videoRepo,
                    playerRepo = playerRepo,
                    gameRepo = gameRepo,
                    adManager = adManager
                )
            )

            VerdictScreen(
                viewModel = verdictViewModel,
                adManager = adManager,
                onGoHome = {
                    homeViewModel.loadData() // Refresh profile data
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onGoToLesson = {
                    navController.navigate(Routes.LESSON) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            Routes.LESSON,
            enterTransition = enterAnim,
            exitTransition = exitAnim
        ) {
            LessonScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.PROFILE,
            enterTransition = enterAnim,
            exitTransition = exitAnim
        ) {
            ProfileScreen(
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
