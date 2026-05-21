package com.theverdict.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.ui.theme.*
import com.theverdict.app.ui.util.LocalHapticManager
import kotlinx.coroutines.delay

/**
 * Full-screen cinematic verdict reveal animation.
 * Phase 0 → dark overlay fades in
 * Phase 1 → "VERDICT" text appears
 * Phase 2 → Gavel drops with rotation impact + haptic
 * Phase 3 → Result (INNOCENT / COUPABLE) bursts in
 * After animation completes, [onFinished] is called.
 */
@Composable
fun VerdictCinematic(
    isCorrect: Boolean,
    onFinished: () -> Unit
) {
    var phase by remember { mutableIntStateOf(0) }
    var overlayVisible by remember { mutableIntStateOf(0) }
    val haptic = LocalHapticManager.current

    LaunchedEffect(Unit) {
        overlayVisible = 1     // trigger overlay fade-in
        delay(150)
        phase = 1          // VERDICT text
        delay(650)
        phase = 2          // Gavel
        haptic.heavyImpact()
        delay(750)
        phase = 3          // Result
        delay(2000)
        onFinished()
    }

    // Dark overlay
    val overlayAlpha by animateFloatAsState(
        targetValue = if (overlayVisible >= 1) 0.93f else 0f,
        animationSpec = tween(300),
        label = "overlay"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = overlayAlpha)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // ── VERDICT label ──────────────────────────────────────────
            AnimatedVisibility(
                visible = phase >= 1,
                enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy), initialScale = 0.4f) + fadeIn(tween(300)),
                exit = fadeOut(tween(200))
            ) {
                Text(
                    text = "VERDICT",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 8.sp,
                    color = GoldPrimary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall.copy(
                        brush = Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, GoldLight, GoldPrimary, GoldDark))
                    )
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Gavel drop ────────────────────────────────────────────
            AnimatedVisibility(
                visible = phase >= 2,
                enter = scaleIn(tween(250), initialScale = 1.8f) + fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                val gavelRotation = remember { Animatable(-55f) }
                LaunchedEffect(phase) {
                    if (phase >= 2) {
                        gavelRotation.animateTo(
                            10f,
                            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = "Verdict",
                    tint = GoldPrimary,
                    modifier = Modifier
                        .size(88.dp)
                        .graphicsLayer { rotationZ = gavelRotation.value }
                )
            }

            Spacer(Modifier.height(36.dp))

            // ── Result (INNOCENT / COUPABLE) ─────────────────────────
            AnimatedVisibility(
                visible = phase >= 3,
                enter = scaleIn(
                    spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
                    initialScale = 2.2f
                ) + fadeIn(tween(250)),
                exit = scaleOut(tween(200)) + fadeOut(tween(200))
            ) {
                val resultColor = if (isCorrect) VerdictCorrect else VerdictWrong
                val resultText = if (isCorrect) "INNOCENT" else "COUPABLE"
                val resultEmoji = if (isCorrect) "🟢" else "🔴"

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = resultEmoji,
                        fontSize = 40.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = resultText,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp,
                        color = resultColor,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displaySmall.copy(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    resultColor.copy(alpha = 0.7f),
                                    resultColor,
                                    resultColor.copy(alpha = 0.7f)
                                )
                            )
                        )
                    )
                }
            }
        }
    }
}
