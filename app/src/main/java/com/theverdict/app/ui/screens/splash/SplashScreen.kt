package com.theverdict.app.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.ui.theme.DarkBackground
import com.theverdict.app.ui.theme.GoldDark
import com.theverdict.app.ui.theme.GoldLight
import com.theverdict.app.ui.theme.GoldPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val iconScale = remember { Animatable(0f) }
    val iconAlpha = remember { Animatable(0f) }
    val glowRadius = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val lineProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. Icon scales in with glow
        launch {
            iconAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            iconScale.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        }
        launch {
            glowRadius.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        }

        delay(500)

        // 2. Title fades in
        launch {
            titleAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        }

        delay(200)

        // 3. Gold line sweeps
        launch {
            lineProgress.animateTo(1f, tween(600, easing = LinearEasing))
        }

        delay(300)

        // 4. Tagline fades in
        launch {
            taglineAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        }

        // Wait then navigate
        delay(800)
        onFinished()
    }

    val goldGlow = GoldPrimary.copy(alpha = 0.35f)
    val goldGlowOuter = GoldPrimary.copy(alpha = 0.10f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scales of Justice icon with animated glow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .scale(iconScale.value)
                    .alpha(iconAlpha.value)
                    .drawBehind {
                        val glowProgress = glowRadius.value
                        if (glowProgress > 0f) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(goldGlowOuter, Color.Transparent),
                                    center = center,
                                    radius = size.minDimension * 0.85f * glowProgress
                                )
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(goldGlow, Color.Transparent),
                                    center = center,
                                    radius = size.minDimension * 0.5f * glowProgress
                                )
                            )
                        }
                    }
            ) {
                Canvas(modifier = Modifier.size(98.dp)) {
                    val w = size.width
                    val h = size.height
                    val gold = Color(0xFFD4A24C)
                    val goldL = Color(0xFFE8C97A)
                    val sw = w * 0.028f

                    // Central pillar
                    drawLine(gold, Offset(w * 0.5f, h * 0.12f), Offset(w * 0.5f, h * 0.82f), sw * 1.3f, StrokeCap.Round)
                    // Base
                    drawLine(gold, Offset(w * 0.3f, h * 0.82f), Offset(w * 0.7f, h * 0.82f), sw * 1.5f, StrokeCap.Round)
                    drawLine(goldL, Offset(w * 0.35f, h * 0.87f), Offset(w * 0.65f, h * 0.87f), sw, StrokeCap.Round)
                    // Pivot
                    drawCircle(goldL, w * 0.04f, Offset(w * 0.5f, h * 0.12f))
                    // Left arm
                    drawLine(gold, Offset(w * 0.5f, h * 0.15f), Offset(w * 0.18f, h * 0.30f), sw, StrokeCap.Round)
                    // Right arm
                    drawLine(gold, Offset(w * 0.5f, h * 0.15f), Offset(w * 0.82f, h * 0.25f), sw, StrokeCap.Round)
                    // Left pan chains + bowl
                    drawLine(gold, Offset(w * 0.18f, h * 0.30f), Offset(w * 0.10f, h * 0.52f), sw * 0.7f)
                    drawLine(gold, Offset(w * 0.18f, h * 0.30f), Offset(w * 0.26f, h * 0.52f), sw * 0.7f)
                    drawArc(goldL, 0f, 180f, false, Offset(w * 0.06f, h * 0.47f), Size(w * 0.24f, h * 0.14f), style = Stroke(sw))
                    // Right pan chains + bowl
                    drawLine(gold, Offset(w * 0.82f, h * 0.25f), Offset(w * 0.74f, h * 0.47f), sw * 0.7f)
                    drawLine(gold, Offset(w * 0.82f, h * 0.25f), Offset(w * 0.90f, h * 0.47f), sw * 0.7f)
                    drawArc(goldL, 0f, 180f, false, Offset(w * 0.70f, h * 0.42f), Size(w * 0.24f, h * 0.14f), style = Stroke(sw))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Title
            Text(
                text = "THE VERDICT",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 38.sp,
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.ExtraBold,
                    shadow = Shadow(
                        color = GoldPrimary.copy(alpha = 0.6f),
                        offset = Offset(0f, 4f),
                        blurRadius = 16f
                    )
                ),
                color = GoldPrimary,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(Modifier.height(8.dp))

            // Gold line sweep
            Canvas(
                modifier = Modifier
                    .size(width = 200.dp, height = 2.dp)
            ) {
                val progress = lineProgress.value
                val lineWidth = size.width * progress
                val startX = (size.width - lineWidth) / 2
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, GoldLight, GoldPrimary, GoldLight, Color.Transparent)
                    ),
                    start = Offset(startX, size.height / 2),
                    end = Offset(startX + lineWidth, size.height / 2),
                    strokeWidth = size.height
                )
            }

            Spacer(Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Rendez justice. Trouvez le menteur.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp,
                    shadow = Shadow(
                        color = GoldPrimary.copy(alpha = 0.25f),
                        offset = Offset(0f, 2f),
                        blurRadius = 8f
                    )
                ),
                color = GoldLight,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}
