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
import com.theverdict.app.domain.model.DailyCaseMode
import com.theverdict.app.ui.screens.daily.DailyCaseDifficultyScreen
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
    const val VIDEO = "video/{mode}"
    const val DAILY_VIDEO = "daily_video/{mode}"
    const val DAILY_DIFFICULTY = "daily_difficulty"
    const val DIFFICULTY = "difficulty"
    const val VERDICT = "verdict/{videoId}"
    const val LESSON = "lesson"
    const val PROFILE = "profile"

    fun verdict(videoId: String) = "verdict/$videoId"
    fun dailyVideo(mode: String) = "daily_video/$mode"
    fun video(mode: String) = "video/$mode"
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
        factory = HomeViewModel.Factory(playerRepo, gameRepo, videoRepo, preferencesManager)
    )

    // State to pass between video and verdict screens
    var lastPlayerTags by remember { mutableStateOf<List<DetectionTag>>(emptyList()) }
    var lastIntuitionScore by remember { mutableStateOf(0) }
    var lastCredibility by remember { mutableStateOf(100) }
    var lastUselessClicks by remember { mutableStateOf(0) }
    var lastVideoViewModel by remember { mutableStateOf<VideoViewModel?>(null) }
    var lastIsDailyCase by remember { mutableStateOf(false) }
    var lastDailyMultiplier by remember { mutableIntStateOf(1) }

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
        ambientMusic.setVideoPlaying(route == Routes.VIDEO || route == Routes.DAILY_VIDEO)
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
                    lastIsDailyCase = false
                    navController.navigate(Routes.DIFFICULTY) { launchSingleTop = true }
                },
                onStartDailyCase = {
                    lastIsDailyCase = true
                    navController.navigate(Routes.DAILY_DIFFICULTY) { launchSingleTop = true }
                },
                onOpenProfile = {
                    navController.navigate(Routes.PROFILE) { launchSingleTop = true }
                },
                onOpenLessons = {
                    navController.navigate(Routes.LESSON) { launchSingleTop = true }
                },
                adManager = adManager
            )
        }

        composable(
            Routes.DIFFICULTY,
            enterTransition = enterAnim,
            exitTransition = exitAnim
        ) {
            DailyCaseDifficultyScreen(
                screenTitle = "NOUVELLE ANALYSE",
                challengeTitle = "",
                onModeSelected = { mode ->
                    lastDailyMultiplier = mode.scoreMultiplier
                    navController.navigate(Routes.video(mode.name)) {
                        popUpTo(Routes.DIFFICULTY) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.VIDEO,
            enterTransition = enterAnim,
            exitTransition = exitAnim
        ) { backStackEntry ->
            val modeName = backStackEntry.arguments?.getString("mode") ?: DailyCaseMode.EASY.name
            val selectedMode = try { DailyCaseMode.valueOf(modeName) } catch (_: Exception) { DailyCaseMode.EASY }
            val appContext = LocalContext.current.applicationContext
            val videoViewModel: VideoViewModel = viewModel(
                key = "video_vm_$modeName",
                factory = VideoViewModel.Factory(videoRepo, appContext, mode = selectedMode)
            )
            lastVideoViewModel = videoViewModel

            VideoScreen(
                viewModel = videoViewModel,
                onVideoComplete = { videoId ->
                    lastPlayerTags = videoViewModel.evaluateTags()
                    lastIntuitionScore = videoViewModel.calculateScore()
                    lastCredibility = videoViewModel.getCredibility()
                    lastUselessClicks = videoViewModel.getUselessClicks()
                    lastDailyMultiplier = videoViewModel.getDailyMultiplier()
                    navController.navigate(Routes.verdict(videoId)) {
                        popUpTo(Routes.VIDEO) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Routes.DAILY_DIFFICULTY,
            enterTransition = enterAnim,
            exitTransition = exitAnim
        ) {
            val dailyTitle = homeViewModel.uiState.collectAsState().value.dailyChallenge?.title ?: "Cas du Jour"
            DailyCaseDifficultyScreen(
                screenTitle = "CAS DU JOUR",
                challengeTitle = dailyTitle,
                onModeSelected = { mode ->
                    lastDailyMultiplier = mode.scoreMultiplier
                    navController.navigate(Routes.dailyVideo(mode.name)) {
                        popUpTo(Routes.DAILY_DIFFICULTY) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.DAILY_VIDEO,
            enterTransition = enterAnim,
            exitTransition = exitAnim
        ) { backStackEntry ->
            val modeName = backStackEntry.arguments?.getString("mode") ?: DailyCaseMode.EASY.name
            val dailyMode = try { DailyCaseMode.valueOf(modeName) } catch (_: Exception) { DailyCaseMode.EASY }
            val appContext = LocalContext.current.applicationContext
            val videoViewModel: VideoViewModel = viewModel(
                key = "daily_video_vm_$modeName",
                factory = VideoViewModel.DailyFactory(videoRepo, appContext, dailyMode)
            )
            lastVideoViewModel = videoViewModel

            VideoScreen(
                viewModel = videoViewModel,
                onVideoComplete = { videoId ->
                    lastPlayerTags = videoViewModel.evaluateTags()
                    lastIntuitionScore = videoViewModel.calculateScore()
                    lastCredibility = videoViewModel.getCredibility()
                    lastUselessClicks = videoViewModel.getUselessClicks()
                    lastDailyMultiplier = videoViewModel.getDailyMultiplier()
                    navController.navigate(Routes.verdict(videoId)) {
                        popUpTo(Routes.DAILY_VIDEO) { inclusive = true }
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
                    isDailyCase = lastIsDailyCase,
                    dailyMultiplier = lastDailyMultiplier,
                    videoRepo = videoRepo,
                    playerRepo = playerRepo,
                    gameRepo = gameRepo,
                    adManager = adManager,
                    prefs = preferencesManager
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
