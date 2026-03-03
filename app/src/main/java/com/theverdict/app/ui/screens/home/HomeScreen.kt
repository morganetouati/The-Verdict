package com.theverdict.app.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.app.Activity
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.platform.LocalContext
import com.theverdict.app.data.ads.AdManager
import com.theverdict.app.domain.model.MentalistRank
import androidx.compose.ui.graphics.Color
import com.theverdict.app.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartGame: () -> Unit,
    onStartDailyCase: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLessons: () -> Unit,
    adManager: AdManager
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    // -- Streak brisée : dialogue --
    if (state.streakBroken) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissStreakBrokenDialog() },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissStreakBrokenDialog() }) {
                    Text("Reprendre", color = FlameOrange)
                }
            },
            icon = {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = FlameRed,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = { Text("Série brisée !", color = FlameRed, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Tu avais une série de ${state.streakBrokenValue} jour(s).\nRecommence aujourd'hui pour la reconstruire.",
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            },
            containerColor = Surface
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirDeep)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // -- Title --
        Icon(
            imageVector = Icons.Default.Gavel,
            contentDescription = "The Verdict",
            tint = Gold,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "THE VERDICT",
            style = MaterialTheme.typography.displayLarge,
            color = Gold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Lis dans les gens comme dans un livre ouvert",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // -- Player card --
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Level badge with avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Brush.linearGradient(listOf(Gold, GoldDark)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.profile.avatarUrl.ifBlank { state.profile.rank.badge },
                        style = MaterialTheme.typography.headlineMedium,
                        color = NoirDeep,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.profile.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${state.profile.rank.badge} ${state.profile.rank.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(state.profile.rank.rankColorArgb)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // XP progress bar
                    val currentRank = state.profile.rank
                    val nextRank = MentalistRank.entries.let { ranks ->
                        val idx = ranks.indexOf(currentRank)
                        if (idx < ranks.size - 1) ranks[idx + 1] else null
                    }
                    val xpProgress = if (nextRank != null) {
                        ((state.profile.totalXp - currentRank.minXp).toFloat() /
                                (nextRank.minXp - currentRank.minXp)).coerceIn(0f, 1f)
                    } else 1f

                    LinearProgressIndicator(
                        progress = { xpProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Gold,
                        trackColor = SurfaceLight,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${state.profile.totalXp} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // -- Stats row --
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Parties", "${state.profile.gamesPlayed}")
            StatItem("Justes", "${state.profile.correctVerdicts}")
            StatItem("Série", "${state.profile.currentStreak}")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // -- Flamme de Lucidité (série) --
        LucidityFlameCard(streak = state.profile.currentStreak)

        Spacer(modifier = Modifier.height(12.dp))

        // -- Cas Quotidien --
        if (state.dailyChallenge != null) {
            DailyCaseCard(
                isDone = state.isDailyCasePlayed,
                secondsLeft = state.dailyCaseSecondsLeft,
                onStart = onStartDailyCase
            )
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }

        // -- Play button --
        val infiniteTransition = rememberInfiniteTransition(label = "glow")
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowAlpha"
        )

        Button(
            onClick = onStartGame,
            enabled = state.canPlay && !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .then(
                    if (state.canPlay) Modifier.border(
                        2.dp,
                        Gold.copy(alpha = glowAlpha),
                        RoundedCornerShape(16.dp)
                    ) else Modifier
                ),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = NoirDeep,
                disabledContainerColor = SurfaceLight,
                disabledContentColor = TextSecondary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Lancer une analyse",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "NOUVELLE ANALYSE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Remaining plays
        Text(
            text = if (state.canPlay)
                "${state.remainingPlays} analyses restantes aujourd'hui"
            else
                "Reviens demain ou regarde une pub !",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        // -- Comment jouer ? --
        var showHowToPlay by remember { mutableStateOf(false) }
        if (showHowToPlay) {
            AlertDialog(
                onDismissRequest = { showHowToPlay = false },
                confirmButton = {
                    TextButton(onClick = { showHowToPlay = false }) {
                        Text("Compris !", color = Gold)
                    }
                },
                title = { Text("Comment jouer ?", color = Gold, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🔟  Lis la déclaration du sujet, puis lance la vidéo.", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        Text("🔟  Pendant la vidéo, tappe les boutons quand tu détectes un signal : regard fuyant, lèvres pincées, geste d’apaisement…", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        Text("🔟  À la fin, rends ton verdict : MENSONGE ou VÉRITÉ.", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        Text("🔟  Découvre si tu avais raison et compare tes tags avec les vrais signaux.", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text("💡 Conseil : en mode Facile tu as plus de temps et des indices. En mode Difficile, quelques secondes seulement !", style = MaterialTheme.typography.bodySmall, color = Gold)
                    }
                },
                containerColor = Surface
            )
        }
        TextButton(
            onClick = { showHowToPlay = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("❓ Comment jouer ?", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // -- Jauge de Crédibilité --
        CredibilityBar(
            credibility = state.persistentCredibility,
            isLocked = state.isCredibilityLocked,
            lockSecondsLeft = state.credibilityLockSecondsLeft,
            tickets = state.credibilityTickets,
            onWatchAd = {
                val activity = context as? Activity
                if (activity != null) {
                    adManager.showCredibilityAd(
                        activity = activity,
                        onRewarded = { viewModel.onCredibilityTicketEarned() },
                        onDismissed = {}
                    )
                }
            },
            onUseTicket = { viewModel.useCredibilityTicket() },
            isAdReady = adManager.isCredibilityAdReady.collectAsState().value
        )

        Spacer(modifier = Modifier.height(32.dp))

        // -- Quick actions --
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAction(
                icon = Icons.Default.School,
                label = "Leçons",
                modifier = Modifier.weight(1f),
                onClick = onOpenLessons
            )
            QuickAction(
                icon = Icons.Default.Person,
                label = "Profil",
                modifier = Modifier.weight(1f),
                onClick = onOpenProfile
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LucidityFlameCard(streak: Int) {
    val flameScale by animateFloatAsState(
        targetValue = if (streak > 0) 1f + (streak.coerceAtMost(30) * 0.015f) else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "flameScale"
    )
    val flameColor = when {
        streak >= 14 -> FlameRed
        streak >= 7  -> FlameOrange
        streak >= 1  -> Gold
        else         -> TextSecondary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (streak > 0) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Flamme de Lucidité",
                tint = flameColor,
                modifier = Modifier
                    .size((28 * flameScale).dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Série : $streak jour${if (streak > 1) "s" else ""}",
                style = MaterialTheme.typography.titleSmall,
                color = flameColor,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = "Joue aujourd'hui pour lancer ta série 🔥",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun DailyCaseCard(
    isDone: Boolean,
    secondsLeft: Long,
    onStart: () -> Unit
) {
    val hours = secondsLeft / 3600
    val mins  = (secondsLeft % 3600) / 60
    val secs  = secondsLeft % 60
    val countdownText = "%02d:%02d:%02d".format(hours, mins, secs)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) SurfaceLight else Surface
        ),
        border = if (!isDone) androidx.compose.foundation.BorderStroke(1.5.dp, Gold.copy(alpha = 0.6f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Cas Quotidien",
                tint = if (isDone) TextSecondary else Gold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CAS DU JOUR",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDone) TextSecondary else Gold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isDone) "Complété ✓  Revient dans $countdownText" else "Vidéo archive exclusive",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            if (!isDone) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = NoirDeep
                    )
                ) {
                    Text("Analyser", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CredibilityBar(
    credibility: Int,
    isLocked: Boolean,
    lockSecondsLeft: Long,
    tickets: Int,
    isAdReady: Boolean,
    onWatchAd: () -> Unit,
    onUseTicket: () -> Unit
) {
    val barColor = when {
        isLocked           -> CrimeRed
        credibility >= 70  -> CredibilityGreen
        credibility >= 30  -> CredibilityAmber
        else               -> FlameRed
    }
    val lockHours = lockSecondsLeft / 3600
    val lockMins  = (lockSecondsLeft % 3600) / 60
    val lockSecs  = lockSecondsLeft % 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CrimeRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "Crédibilité",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isLocked) CrimeRed else TextSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (isLocked) "Bloquée – %02d:%02d:%02d".format(lockHours, lockMins, lockSecs)
                           else "$credibility / 100",
                    style = MaterialTheme.typography.labelSmall,
                    color = barColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (credibility / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = barColor,
                trackColor = SurfaceLight
            )

            if (isLocked) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = if (tickets > 0) onUseTicket else onWatchAd,
                    enabled = isAdReady || tickets > 0,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CredibilityAmber,
                        contentColor = NoirDeep
                    )
                ) {
                    Text(
                        text = if (tickets > 0) "Utiliser un ticket ($tickets)" else "📺 Regarder une pub",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = Gold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Gold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
        }
    }
}
