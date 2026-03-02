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
import com.theverdict.app.domain.model.PlayerLevel
import com.theverdict.app.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartGame: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLessons: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
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
                        text = state.profile.avatarUrl.ifBlank { state.profile.level.name.first().toString() },
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
                        text = state.profile.level.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Gold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // XP progress bar
                    val currentLevel = state.profile.level
                    val nextLevel = PlayerLevel.entries.let { levels ->
                        val idx = levels.indexOf(currentLevel)
                        if (idx < levels.size - 1) levels[idx + 1] else null
                    }
                    val xpProgress = if (nextLevel != null) {
                        ((state.profile.totalXp - currentLevel.minXp).toFloat() /
                                (nextLevel.minXp - currentLevel.minXp)).coerceIn(0f, 1f)
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

        Spacer(modifier = Modifier.height(32.dp))

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
