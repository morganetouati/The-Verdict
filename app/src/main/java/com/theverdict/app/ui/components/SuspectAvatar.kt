package com.theverdict.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theverdict.app.domain.model.AvatarConfig
import com.theverdict.app.domain.model.Clue

private val skinTones = listOf(
    Color(0xFFFDDEB4), Color(0xFFF1C27D), Color(0xFFE0AC69),
    Color(0xFFC68642), Color(0xFF8D5524), Color(0xFF6B3A2A)
)

private val hairColors = listOf(
    Color(0xFF2C1B0E), Color(0xFF4A3218), Color(0xFF8B6914),
    Color(0xFFD4A24C), Color(0xFFE53935), Color(0xFF333333)
)

@Composable
fun SuspectAvatar(
    config: AvatarConfig,
    clues: List<Clue> = emptyList(),
    size: Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    val skinColor = skinTones.getOrElse(config.skinTone) { skinTones[0] }
    val hairColor = hairColors.getOrElse(config.hairColor) { hairColors[0] }

    val hasClues = clues.isNotEmpty()
    val inf = rememberInfiniteTransition(label = "avatar")

    // Animated eye shift (for shifty eyes clue)
    val hasShiftyEyes = clues.any { it == Clue.REGARDE_AILLEURS || it == Clue.EVITE_REGARD }
    val eyeShift by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eyeShift"
    )

    // Animated sweat drop falling
    val hasSweat = Clue.TRANSPIRE in clues || Clue.MAINS_TREMBLENT in clues
    val sweatDrop by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweatDrop"
    )

    // Trembling (for nervous/trembling hands)
    val isNervous = clues.any { it == Clue.NERVEUX || it == Clue.MAINS_TREMBLENT }
    val tremble by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tremble"
    )

    // Mouth animation (for talking fast / hesitating)
    val isTalking = clues.any { it == Clue.PARLE_VITE || it == Clue.HESITE || it == Clue.VOIX_CHANGE }
    val mouthAnim by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (Clue.PARLE_VITE in clues) 300 else 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mouthAnim"
    )

    // Brow raise for nervous
    val browAnim by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "browAnim"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val centerX = w / 2f
        val headRadius = w * 0.3f
        val headCenterY = h * 0.35f
        val trembleOffset = if (isNervous) tremble * w * 0.005f else 0f

        // Dark circular background behind suspect
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF2A2A2A), Color(0xFF1A1A1A), Color.Transparent),
                center = Offset(centerX, h * 0.45f),
                radius = w * 0.48f
            ),
            radius = w * 0.48f,
            center = Offset(centerX, h * 0.45f)
        )

        // Neck
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - w * 0.08f + trembleOffset, headCenterY + headRadius * 0.7f),
            size = Size(w * 0.16f, h * 0.15f)
        )

        // Body (shoulders)
        drawOval(
            color = Color(0xFF444444),
            topLeft = Offset(centerX - w * 0.35f + trembleOffset, h * 0.65f),
            size = Size(w * 0.7f, h * 0.4f)
        )

        // Arms crossed visual cue
        if (Clue.BRAS_CROISES in clues) {
            drawLine(
                Color(0xFF555555),
                Offset(centerX - w * 0.3f, h * 0.78f),
                Offset(centerX + w * 0.3f, h * 0.72f),
                strokeWidth = w * 0.04f
            )
            drawLine(
                Color(0xFF555555),
                Offset(centerX + w * 0.3f, h * 0.78f),
                Offset(centerX - w * 0.3f, h * 0.72f),
                strokeWidth = w * 0.04f
            )
        }

        // Shadow under head
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent),
                center = Offset(centerX + trembleOffset, headCenterY + headRadius * 0.95f)
            ),
            topLeft = Offset(centerX - headRadius * 0.7f + trembleOffset, headCenterY + headRadius * 0.7f),
            size = Size(headRadius * 1.4f, headRadius * 0.5f)
        )

        // Head with gradient
        drawCircle(color = skinColor, radius = headRadius, center = Offset(centerX + trembleOffset, headCenterY))
        // Face gradient overlay (subtle light from top)
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.10f), Color.Transparent, Color.Black.copy(alpha = 0.10f)),
                startY = headCenterY - headRadius,
                endY = headCenterY + headRadius
            ),
            radius = headRadius,
            center = Offset(centerX + trembleOffset, headCenterY)
        )
        // Thinner head outline
        drawCircle(
            color = skinColor.copy(alpha = 0.5f).compositeOver(Color(0xFF333333)),
            radius = headRadius,
            center = Offset(centerX + trembleOffset, headCenterY),
            style = Stroke(width = headRadius * 0.025f)
        )

        // Hair
        drawHair(config.hairStyle, hairColor, centerX + trembleOffset, headCenterY, headRadius)

        // Eyes with animation
        val eyeY = headCenterY - headRadius * 0.05f
        val eyeSpacing = headRadius * 0.45f
        val eyeOffsetX = if (hasShiftyEyes) eyeShift * headRadius * 0.1f else 0f

        // Eye whites
        drawCircle(Color.White, headRadius * 0.18f, Offset(centerX - eyeSpacing + trembleOffset, eyeY))
        drawCircle(Color.White, headRadius * 0.18f, Offset(centerX + eyeSpacing + trembleOffset, eyeY))
        // Pupils (animated)
        drawCircle(Color(0xFF2C1B0E), headRadius * 0.1f, Offset(centerX - eyeSpacing + eyeOffsetX + trembleOffset, eyeY))
        drawCircle(Color(0xFF2C1B0E), headRadius * 0.1f, Offset(centerX + eyeSpacing + eyeOffsetX + trembleOffset, eyeY))

        // Eyebrows (animated for nervous)
        val browY = eyeY - headRadius * 0.22f
        val browTilt = if (isNervous) browAnim * headRadius * 0.08f else 0f
        drawLine(
            Color(0xFF2C1B0E),
            Offset(centerX - eyeSpacing - headRadius * 0.12f + trembleOffset, browY + browTilt),
            Offset(centerX - eyeSpacing + headRadius * 0.12f + trembleOffset, browY),
            strokeWidth = headRadius * 0.06f
        )
        drawLine(
            Color(0xFF2C1B0E),
            Offset(centerX + eyeSpacing - headRadius * 0.12f + trembleOffset, browY),
            Offset(centerX + eyeSpacing + headRadius * 0.12f + trembleOffset, browY + browTilt),
            strokeWidth = headRadius * 0.06f
        )

        // Mouth (animated)
        val mouthY = headCenterY + headRadius * 0.35f
        val isConfident = Clue.CONFIANT in clues
        val isSmilingTooMuch = Clue.SOURIT_TROP in clues
        if (isConfident || isSmilingTooMuch) {
            val smileWidth = headRadius * (0.2f + if (isSmilingTooMuch) mouthAnim * 0.08f else 0f)
            drawArc(
                Color(0xFF2C1B0E), 0f, 180f, false,
                Offset(centerX - smileWidth + trembleOffset, mouthY - headRadius * 0.08f),
                Size(smileWidth * 2f, headRadius * 0.2f)
            )
        } else if (isTalking) {
            val openAmount = mouthAnim * headRadius * 0.12f
            drawOval(
                Color(0xFF2C1B0E),
                Offset(centerX - headRadius * 0.1f + trembleOffset, mouthY - openAmount / 2),
                Size(headRadius * 0.2f, openAmount.coerceAtLeast(headRadius * 0.04f))
            )
        } else if (isNervous) {
            // Wobbly worried line
            drawLine(
                Color(0xFF2C1B0E),
                Offset(centerX - headRadius * 0.12f + trembleOffset, mouthY + tremble * headRadius * 0.02f),
                Offset(centerX + headRadius * 0.12f + trembleOffset, mouthY - tremble * headRadius * 0.02f),
                strokeWidth = headRadius * 0.04f
            )
        } else {
            drawLine(
                Color(0xFF2C1B0E),
                Offset(centerX - headRadius * 0.15f + trembleOffset, mouthY),
                Offset(centerX + headRadius * 0.15f + trembleOffset, mouthY),
                strokeWidth = headRadius * 0.04f
            )
        }

        // Animated sweat drops
        if (hasSweat) {
            val sweatY1 = headCenterY - headRadius * 0.3f + sweatDrop * headRadius * 1.2f
            val sweatAlpha = (1f - sweatDrop).coerceIn(0f, 1f)
            val sweatColor = Color(0xFF42A5F5).copy(alpha = sweatAlpha)

            // Main drop (teardrop shape)
            drawCircle(sweatColor, headRadius * 0.06f, Offset(centerX + headRadius * 0.85f, sweatY1))
            // Trail
            drawCircle(
                sweatColor.copy(alpha = sweatAlpha * 0.5f),
                headRadius * 0.04f,
                Offset(centerX + headRadius * 0.85f, sweatY1 - headRadius * 0.12f)
            )

            // Second drop (delayed by using offset calculation)
            val sweatDrop2 = (sweatDrop + 0.4f) % 1f
            val sweatY2 = headCenterY - headRadius * 0.1f + sweatDrop2 * headRadius * 1.0f
            val sweatAlpha2 = (1f - sweatDrop2).coerceIn(0f, 1f)
            drawCircle(
                Color(0xFF42A5F5).copy(alpha = sweatAlpha2),
                headRadius * 0.05f,
                Offset(centerX + headRadius * 0.95f, sweatY2)
            )
        }

        // Accessory
        drawAccessory(config.accessory, centerX + trembleOffset, headCenterY, headRadius)
    }
}

private fun DrawScope.drawHair(style: Int, color: Color, cx: Float, cy: Float, r: Float) {
    when (style % 4) {
        0 -> { // Short
            drawArc(color, 180f, 180f, true, Offset(cx - r * 1.05f, cy - r * 1.1f), Size(r * 2.1f, r * 1.4f))
        }
        1 -> { // Flat top
            drawRect(color, Offset(cx - r * 0.9f, cy - r * 1.15f), Size(r * 1.8f, r * 0.6f))
        }
        2 -> { // Long
            drawArc(color, 180f, 180f, true, Offset(cx - r * 1.1f, cy - r * 1.15f), Size(r * 2.2f, r * 1.5f))
            drawRect(color, Offset(cx - r * 1.1f, cy - r * 0.2f), Size(r * 0.3f, r * 0.8f))
            drawRect(color, Offset(cx + r * 0.8f, cy - r * 0.2f), Size(r * 0.3f, r * 0.8f))
        }
        3 -> { // Spiky
            drawArc(color, 180f, 180f, true, Offset(cx - r, cy - r * 1.2f), Size(r * 2f, r * 1.2f))
        }
    }
}

private fun DrawScope.drawAccessory(accessory: Int, cx: Float, cy: Float, r: Float) {
    when (accessory % 4) {
        1 -> { // Glasses
            val eyeY = cy - r * 0.05f
            val spacing = r * 0.45f
            drawCircle(Color(0xFF666666), r * 0.24f, Offset(cx - spacing, eyeY), style = androidx.compose.ui.graphics.drawscope.Stroke(r * 0.04f))
            drawCircle(Color(0xFF666666), r * 0.24f, Offset(cx + spacing, eyeY), style = androidx.compose.ui.graphics.drawscope.Stroke(r * 0.04f))
            drawLine(Color(0xFF666666), Offset(cx - spacing + r * 0.24f, eyeY), Offset(cx + spacing - r * 0.24f, eyeY), strokeWidth = r * 0.03f)
        }
        2 -> { // Hat
            drawRect(Color(0xFF444444), Offset(cx - r * 1.2f, cy - r * 1.05f), Size(r * 2.4f, r * 0.12f))
            drawRect(Color(0xFF444444), Offset(cx - r * 0.6f, cy - r * 1.5f), Size(r * 1.2f, r * 0.5f))
        }
        3 -> { // Earring
            drawCircle(GoldPrimaryRaw, r * 0.06f, Offset(cx - r * 0.9f, cy + r * 0.2f))
        }
        else -> {} // No accessory
    }
}

private val GoldPrimaryRaw = Color(0xFFD4A24C)
