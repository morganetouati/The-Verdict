package com.theverdict.app.ui.screens.result

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.ui.components.RankBadge
import com.theverdict.app.ui.components.ReputationBar
import com.theverdict.app.ui.components.VerdictStamp
import com.theverdict.app.ui.theme.*
import com.theverdict.app.ui.util.LocalHapticManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    caseRepository: CaseRepository,
    playerRepository: PlayerRepository,
    themeIndex: Int,
    caseIndex: Int,
    isCorrect: Boolean,
    pointsChange: Int,
    onNextCase: (themeIndex: Int, caseIndex: Int) -> Unit,
    onGameOver: () -> Unit,
    onVictory: () -> Unit,
    onMenu: () -> Unit
) {
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())
    val scope = rememberCoroutineScope()
    val theme = CaseTheme.entries[themeIndex]
    val case = caseRepository.getCase(theme, caseIndex)
    val dim = LocalDimensions.current
    val haptic = LocalHapticManager.current

    // Screen shake effect on stamp impact
    val shakeX = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(400) // Wait for stamp to land
        haptic.heavyImpact()
        if (isCorrect) haptic.successPulse() else haptic.errorBuzz()
        shakeX.animateTo(8f, tween(40))
        shakeX.animateTo(-6f, tween(40))
        shakeX.animateTo(4f, tween(40))
        shakeX.animateTo(-2f, tween(40))
        shakeX.animateTo(0f, tween(40))
    }

    // Animated counter for points
    var targetPoints by remember { mutableIntStateOf(0) }
    val animatedPoints by animateIntAsState(
        targetValue = targetPoints,
        animationSpec = tween(800, delayMillis = 600),
        label = "points"
    )
    LaunchedEffect(pointsChange) {
        targetPoints = pointsChange
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { translationX = shakeX.value }
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, Color(0xFF111111), DarkSurface, DarkBackground)
                )
            )
            .padding(dim.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(dim.topSpacing))

        // Verdict stamp animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            VerdictStamp(isCorrect = isCorrect)
        }

        Spacer(Modifier.height(16.dp))

        // Points change (animated counter)
        Text(
            text = if (animatedPoints >= 0) "+$animatedPoints" else "$animatedPoints",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                shadow = Shadow(
                    color = (if (isCorrect) VerdictCorrect else VerdictWrong).copy(alpha = 0.4f),
                    offset = Offset(0f, 3f),
                    blurRadius = 12f
                )
            ),
            color = if (isCorrect) VerdictCorrect else VerdictWrong
        )
        Text(
            text = "points de réputation",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray
        )

        Spacer(Modifier.height(24.dp))

        // Explanation
        if (case != null) {
            val coupableNames = case.suspects
                .filter { it.id in case.coupableIds }
                .joinToString(", ") { it.nom }

            val explanation = when {
                case.coupableIds.isEmpty() -> "Personne ne mentait dans cette affaire."
                case.coupableIds.size == case.suspects.size -> "Tous les suspects mentaient !"
                case.coupableIds.size > 1 -> "Les coupables étaient : $coupableNames"
                else -> "Le coupable était : $coupableNames"
            }

            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
                color = TextWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(32.dp))

        // Current reputation
        RankBadge(rank = profile.rank)
        Spacer(Modifier.height(12.dp))
        ReputationBar(
            reputation = profile.reputation,
            rank = profile.rank,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        // Actions
        when {
            profile.reputation <= 0 -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dim.buttonHeight)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.horizontalGradient(listOf(VerdictWrong, VerdictWrongLight, VerdictWrong)))
                        .clickable { onGameOver() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "GAME OVER",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                        color = TextWhite
                    )
                }
            }
            profile.allCasesCompleted -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dim.buttonHeight)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.horizontalGradient(listOf(GoldDark, RankLegende, GoldLight)))
                        .clickable { onVictory() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "VICTOIRE !",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                        color = DarkBackground
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawRoundRect(
                                brush = Brush.verticalGradient(listOf(Color(0x40D4A24C), Color.Transparent)),
                                cornerRadius = CornerRadius(24.dp.toPx()),
                                topLeft = Offset(0f, 4.dp.toPx()),
                                size = Size(size.width, size.height + 4.dp.toPx())
                            )
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dim.buttonHeight)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, GoldLight)))
                            .clickable {
                                scope.launch {
                                    val updated = playerRepository.advanceToNextCase(profile)
                                    onNextCase(updated.currentThemeIndex, updated.currentCaseIndex)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "AFFAIRE SUIVANTE",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                            color = DarkBackground
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dim.buttonHeightSmall)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, GoldDark.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .background(DarkSurfaceVariant)
                        .clickable { onMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "MENU",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldLight
                    )
                }
            }
        }
    }
}
