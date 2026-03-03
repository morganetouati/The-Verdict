package com.theverdict.app.ui.screens.video

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.graphics.Color
import androidx.media3.common.PlaybackException
import com.theverdict.app.ui.components.DetectionBar
import com.theverdict.app.ui.components.TagTimeline
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoScreen(
    viewModel: VideoViewModel,
    onVideoComplete: (videoId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ── Flash Néon Bleu : déclenché par chaque clic sur la DetectionBar ────────────
    var flashTrigger by remember { mutableIntStateOf(0) }
    var isFlashing by remember { mutableStateOf(false) }

    LaunchedEffect(flashTrigger) {
        if (flashTrigger > 0) {
            isFlashing = true
            delay(500)
            isFlashing = false
        }
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    // Set media when challenge is loaded
    LaunchedEffect(state.challenge) {
        state.challenge?.let { challenge ->
            val mediaItem = MediaItem.fromUri(challenge.videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = false
        }
    }

    // Track position
    LaunchedEffect(exoPlayer) {
        while (true) {
            if (exoPlayer.isPlaying) {
                viewModel.updatePosition(exoPlayer.currentPosition)
            }
            delay(100)
        }
    }

    // Listen for playback end + errors + buffering
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> viewModel.onVideoEnded()
                    Player.STATE_BUFFERING -> viewModel.setBuffering(true)
                    Player.STATE_READY -> viewModel.setBuffering(false)
                    else -> {}
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                val msg = when (error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ->
                        "Pas de connexion internet. Vérifiez votre réseau."
                    PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
                        "Vidéo introuvable. Réessayez plus tard."
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
                        "Connexion trop lente. Réessayez."
                    else ->
                        "Erreur de lecture vidéo. Réessayez."
                }
                viewModel.onPlayerError(msg)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Navigate when video ended
    LaunchedEffect(state.isVideoEnded) {
        if (state.isVideoEnded) {
            exoPlayer.pause()
            delay(500) // Brief pause before transition
            state.challenge?.let { onVideoComplete(it.id) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirDeep)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // -- Video player + Detection bar overlay --
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(NoirDeep)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Play/pause overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                                viewModel.setPlaying(false)
                            } else {
                                exoPlayer.play()
                                viewModel.setPlaying(true)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !state.isPlaying && !state.isVideoEnded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Gold,
                            modifier = Modifier
                                .size(64.dp)
                                .background(OverlayDark, CircleShape)
                                .padding(12.dp)
                        )
                    }
                }

                // Tag count badge
                if (state.playerTags.isNotEmpty()) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        containerColor = Gold
                    ) {
                        Text(
                            text = "${state.playerTags.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NoirDeep,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Loading / Buffering overlay
                if (state.isLoading || state.isBuffering) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OverlayDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Gold,
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (state.isLoading) "Ouverture du dossier…" else "Chargement…",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Error overlay
                val errorMsg = state.errorMessage
                if (errorMsg != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OverlayDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "⚠\uFE0F",
                                style = MaterialTheme.typography.displayMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = errorMsg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    viewModel.retryLoad()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Gold,
                                    contentColor = NoirDeep
                                )
                            ) {
                                Text("Réessayer", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // -- Detection bar (overlaid at bottom of video) --
                DetectionBar(
                    onDetection = { type -> viewModel.addDetectionTag(type) },
                    enabled = state.isPlaying && !state.isVideoEnded,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onFlash = { flashTrigger++ }
                )

                // ── Flash Néon Bleu overlay ────────────────────────────────────
                if (isFlashing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NeonBlue.copy(alpha = 0.18f))
                    )
                }
            }

            // -- Timeline --
            state.challenge?.let { challenge ->
                TagTimeline(
                    currentPositionMs = state.currentPositionMs,
                    durationMs = challenge.durationMs,
                    tags = state.playerTags,
                    onSeek = { /* Seeking disabled during play */ },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // -- Story prompt + archive context --
            state.challenge?.let { challenge ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        // Difficulty badge + title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Difficulty indicator — show chosen DailyCaseMode if available, else video's own difficulty
                            val modeLabel = state.dailyMode?.label ?: challenge.difficulty.label
                            val modeColor = when (state.dailyMode) {
                                com.theverdict.app.domain.model.DailyCaseMode.EASY -> com.theverdict.app.ui.theme.GreenTruth
                                com.theverdict.app.domain.model.DailyCaseMode.MEDIUM -> Gold
                                com.theverdict.app.domain.model.DailyCaseMode.HARD -> com.theverdict.app.ui.theme.RedLie
                                null -> when (challenge.difficulty) {
                                    com.theverdict.app.domain.model.Difficulty.EASY -> com.theverdict.app.ui.theme.GreenTruth
                                    com.theverdict.app.domain.model.Difficulty.MEDIUM -> Gold
                                    com.theverdict.app.domain.model.Difficulty.HARD -> com.theverdict.app.ui.theme.RedLie
                                }
                            }
                            Text(
                                text = modeLabel.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = modeColor,
                                fontWeight = FontWeight.Bold
                            )
                            // Hint count badge
                            if (state.hintsAvailable.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Gold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${state.hintsAvailable.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "•",
                                color = TextSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = challenge.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Gold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        // Story prompt (what the suspect says)
                        Text(
                            text = challenge.storyPrompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        // Archive context (Cold Case immersion)
                        if (challenge.archiveContext.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = challenge.archiveContext,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // -- Hints panel (daily case only) --
            if (state.hintsAvailable.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Surface)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Hint bulbs row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INDICES",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        state.hintsAvailable.forEachIndexed { index, _ ->
                            val isRevealed = index in state.hintsRevealed
                            IconButton(
                                onClick = { viewModel.revealHint(index) },
                                enabled = !isRevealed,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Indice ${index + 1}",
                                    tint = if (isRevealed) Gold else TextSecondary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    // Show revealed hints
                    state.hintsRevealed.sorted().forEach { index ->
                        if (index in state.hintsAvailable.indices) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "💡 ${state.hintsAvailable[index]}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gold
                            )
                        }
                    }
                }
            }

            // -- Credibility gauge --
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CRÉDIBILITÉ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.credibility}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            state.credibility > 60 -> Gold
                            state.credibility > 30 -> Color(0xFFE67E22)
                            else -> RedLie
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { state.credibility / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = when {
                        state.credibility > 60 -> Gold
                        state.credibility > 30 -> Color(0xFFE67E22)
                        else -> RedLie
                    },
                    trackColor = Surface
                )
            }
        }
    }
}
