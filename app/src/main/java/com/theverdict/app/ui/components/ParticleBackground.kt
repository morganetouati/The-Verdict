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
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class Particle(
    val x: Float,       // 0..1 relative
    val startY: Float,  // 0..1
    val size: Float,     // dp radius
    val speed: Float,    // relative speed
    val alpha: Float,    // 0..1
    val phase: Float     // 0..1 for horizontal drift
)

@Composable
fun ParticleBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 18,
    color: Color = Color(0xFFD4A24C)
) {
    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                startY = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 0.8f,
                speed = Random.nextFloat() * 0.4f + 0.2f,
                alpha = Random.nextFloat() * 0.25f + 0.05f,
                phase = Random.nextFloat()
            )
        }
    }

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

    // Separate slow oscillation for horizontal drift
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            // Particle drifts upward, wraps around
            val y = ((p.startY + time * p.speed) % 1f) * h
            // Horizontal sway using sine
            val sway = kotlin.math.sin((drift + p.phase) * 2 * Math.PI).toFloat() * w * 0.02f
            val x = p.x * w + sway

            // Pulsing alpha
            val pulseAlpha = p.alpha * (0.6f + 0.4f * kotlin.math.sin((time + p.phase) * 4 * Math.PI).toFloat())

            drawCircle(
                color = color.copy(alpha = pulseAlpha),
                radius = p.size * density,
                center = androidx.compose.ui.geometry.Offset(x, h - y)
            )
        }
    }
}
