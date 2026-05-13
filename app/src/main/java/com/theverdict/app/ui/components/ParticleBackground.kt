package com.theverdict.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.theverdict.app.ui.theme.GoldPrimary
import com.theverdict.app.ui.theme.NeonCyan
import com.theverdict.app.ui.theme.NeonPurple
import com.theverdict.app.ui.util.isReducedMotion
import kotlin.random.Random

private data class Particle(
    val x: Float,
    val startY: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float,
    val phase: Float,
    val colorIndex: Int
)

@Composable
fun ParticleBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 28
) {
    // Neon-noir palette: gold, cyan, purple
    val palette = remember {
        listOf(
            GoldPrimary,
            NeonCyan,
            NeonPurple,
            GoldPrimary, // Gold weighted heavier
            NeonCyan     // Cyan weighted heavier
        )
    }

    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                startY = Random.nextFloat(),
                size = Random.nextFloat() * 2.2f + 0.6f,
                speed = Random.nextFloat() * 0.35f + 0.15f,
                alpha = Random.nextFloat() * 0.22f + 0.04f,
                phase = Random.nextFloat(),
                colorIndex = Random.nextInt(5)
            )
        }
    }

    val reducedMotion = isReducedMotion()

    val transition = rememberInfiniteTransition(label = "particles")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )

    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            val y: Float
            val x: Float
            val pulseAlpha: Float
            if (reducedMotion) {
                // Static dots — no animation
                y = p.startY * h
                x = p.x * w
                pulseAlpha = p.alpha
            } else {
                y = ((p.startY + time * p.speed) % 1f) * h
                val sway = kotlin.math.sin((drift + p.phase) * 2 * Math.PI).toFloat() * w * 0.02f
                x = p.x * w + sway
                pulseAlpha = p.alpha * (0.5f + 0.5f * kotlin.math.sin((time + p.phase) * 4 * Math.PI).toFloat())
            }
            val color = palette[p.colorIndex]

            drawCircle(
                color = color.copy(alpha = pulseAlpha),
                radius = p.size * density,
                center = androidx.compose.ui.geometry.Offset(x, h - y)
            )
        }
    }
}
