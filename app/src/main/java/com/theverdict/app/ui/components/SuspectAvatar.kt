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

private val clothingColors = listOf(
    Color(0xFFC62828), Color(0xFF1565C0), Color(0xFF2E7D32),
    Color(0xFF4527A0), Color(0xFF00695C), Color(0xFF37474F),
    Color(0xFF6A1B9A), Color(0xFFAD1457)
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
    val skinShadow = Color.Black.copy(alpha = 0.2f).compositeOver(skinColor)
    val clothingColor = clothingColors[(config.skinTone * 3 + config.hairStyle * 2 + config.hairColor) % clothingColors.size]

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

    // Animated idle breathing (subtle body scale oscillation)
    val breathAnim by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val centerX = w / 2f
        val headRadius = w * 0.3f
        val headCenterY = h * 0.35f
        val trembleOffset = if (isNervous) tremble * w * 0.005f else 0f
        val breathOffset = breathAnim * w * 0.006f

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

        // Shadow under body (ground shadow)
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(centerX, h * 0.92f)
            ),
            topLeft = Offset(centerX - w * 0.32f, h * 0.88f),
            size = Size(w * 0.64f, h * 0.1f)
        )

        // Neck with shadow
        val neckTop = headCenterY + headRadius * 0.7f
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - w * 0.07f + trembleOffset, neckTop),
            size = Size(w * 0.14f, h * 0.15f)
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(skinShadow, Color.Transparent),
                startY = neckTop, endY = neckTop + headRadius * 0.4f
            ),
            topLeft = Offset(centerX - w * 0.07f + trembleOffset, neckTop),
            size = Size(w * 0.14f, headRadius * 0.4f)
        )

        // Body (shoulders) — with breathing
        val bodyTop = h * 0.65f
        drawOval(
            color = clothingColor,
            topLeft = Offset(centerX - w * 0.35f + trembleOffset - breathOffset, bodyTop - breathOffset * 0.5f),
            size = Size(w * 0.7f + breathOffset * 2f, h * 0.4f + breathOffset)
        )
        // Body highlight
        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                startY = bodyTop, endY = bodyTop + h * 0.13f
            ),
            topLeft = Offset(centerX - w * 0.3f + trembleOffset, bodyTop + h * 0.01f),
            size = Size(w * 0.6f, h * 0.14f)
        )
        // V-neckline
        val necklinePath = Path().apply {
            moveTo(centerX - w * 0.09f + trembleOffset, bodyTop)
            lineTo(centerX + trembleOffset, bodyTop + h * 0.07f)
            lineTo(centerX + w * 0.09f + trembleOffset, bodyTop)
        }
        drawPath(necklinePath, skinColor)

        // Arms crossed visual cue
        if (Clue.BRAS_CROISES in clues) {
            val armColor = Color.White.copy(alpha = 0.12f).compositeOver(clothingColor)
            drawLine(
                armColor,
                Offset(centerX - w * 0.3f, h * 0.78f),
                Offset(centerX + w * 0.3f, h * 0.72f),
                strokeWidth = w * 0.04f
            )
            drawLine(
                armColor,
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

        // Ears (behind head)
        val earW = headRadius * 0.22f
        val earH = headRadius * 0.32f
        val earCenterY = headCenterY + headRadius * 0.05f
        drawOval(skinColor, Offset(centerX - headRadius - earW * 0.35f + trembleOffset, earCenterY - earH / 2), Size(earW, earH))
        drawOval(skinShadow, Offset(centerX - headRadius - earW * 0.1f + trembleOffset, earCenterY - earH * 0.3f), Size(earW * 0.5f, earH * 0.6f))
        drawOval(skinColor, Offset(centerX + headRadius - earW * 0.65f + trembleOffset, earCenterY - earH / 2), Size(earW, earH))
        drawOval(skinShadow, Offset(centerX + headRadius - earW * 0.4f + trembleOffset, earCenterY - earH * 0.3f), Size(earW * 0.5f, earH * 0.6f))

        // Head
        drawCircle(color = skinColor, radius = headRadius, center = Offset(centerX + trembleOffset, headCenterY))
        // Top-left highlight
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(centerX - headRadius * 0.25f + trembleOffset, headCenterY - headRadius * 0.35f),
                radius = headRadius * 0.7f
            ),
            radius = headRadius,
            center = Offset(centerX + trembleOffset, headCenterY)
        )
        // Bottom shadow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f)),
                center = Offset(centerX + trembleOffset, headCenterY + headRadius * 0.5f),
                radius = headRadius * 0.9f
            ),
            radius = headRadius,
            center = Offset(centerX + trembleOffset, headCenterY)
        )
        // Side shadows
        drawCircle(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.08f),
                    Color.Transparent,
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.08f)
                ),
                startX = centerX - headRadius + trembleOffset,
                endX = centerX + headRadius + trembleOffset
            ),
            radius = headRadius,
            center = Offset(centerX + trembleOffset, headCenterY)
        )
        // Cheek blush
        drawCircle(Color(0xFFFF9999).copy(alpha = 0.1f), headRadius * 0.14f,
            Offset(centerX - headRadius * 0.4f + trembleOffset, headCenterY + headRadius * 0.15f))
        drawCircle(Color(0xFFFF9999).copy(alpha = 0.1f), headRadius * 0.14f,
            Offset(centerX + headRadius * 0.4f + trembleOffset, headCenterY + headRadius * 0.15f))
        // Head outline
        drawCircle(
            color = Color.Black.copy(alpha = 0.2f),
            radius = headRadius,
            center = Offset(centerX + trembleOffset, headCenterY),
            style = Stroke(width = headRadius * 0.02f)
        )

        // Hair
        drawHair(config.hairStyle, hairColor, centerX + trembleOffset, headCenterY, headRadius)

        // Eyes — larger and more expressive
        val eyeY = headCenterY - headRadius * 0.05f
        val eyeSpacing = headRadius * 0.38f
        val eyeW = headRadius * 0.28f
        val eyeH = headRadius * 0.3f
        val eyeOffsetX = if (hasShiftyEyes) eyeShift * headRadius * 0.1f else 0f

        // Eye whites (oval)
        drawOval(Color.White, Offset(centerX - eyeSpacing - eyeW / 2 + trembleOffset, eyeY - eyeH / 2), Size(eyeW, eyeH))
        drawOval(Color.White, Offset(centerX + eyeSpacing - eyeW / 2 + trembleOffset, eyeY - eyeH / 2), Size(eyeW, eyeH))
        // Iris
        val irisR = headRadius * 0.11f
        drawCircle(Color(0xFF5D4037), irisR, Offset(centerX - eyeSpacing + eyeOffsetX + trembleOffset, eyeY + headRadius * 0.02f))
        drawCircle(Color(0xFF5D4037), irisR, Offset(centerX + eyeSpacing + eyeOffsetX + trembleOffset, eyeY + headRadius * 0.02f))
        // Pupils
        drawCircle(Color(0xFF1A1A1A), headRadius * 0.065f, Offset(centerX - eyeSpacing + eyeOffsetX + trembleOffset, eyeY + headRadius * 0.02f))
        drawCircle(Color(0xFF1A1A1A), headRadius * 0.065f, Offset(centerX + eyeSpacing + eyeOffsetX + trembleOffset, eyeY + headRadius * 0.02f))
        // Eye highlights
        drawCircle(Color.White, headRadius * 0.045f, Offset(centerX - eyeSpacing + headRadius * 0.05f + trembleOffset, eyeY - headRadius * 0.05f))
        drawCircle(Color.White, headRadius * 0.045f, Offset(centerX + eyeSpacing + headRadius * 0.05f + trembleOffset, eyeY - headRadius * 0.05f))
        // Eye outlines
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(centerX - eyeSpacing - eyeW / 2 + trembleOffset, eyeY - eyeH / 2), Size(eyeW, eyeH), style = Stroke(headRadius * 0.02f))
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(centerX + eyeSpacing - eyeW / 2 + trembleOffset, eyeY - eyeH / 2), Size(eyeW, eyeH), style = Stroke(headRadius * 0.02f))

        // Eyebrows (thicker, colored like hair)
        val browY = eyeY - headRadius * 0.24f
        val browTilt = if (isNervous) browAnim * headRadius * 0.08f else 0f
        drawLine(
            hairColor,
            Offset(centerX - eyeSpacing - headRadius * 0.14f + trembleOffset, browY + browTilt),
            Offset(centerX - eyeSpacing + headRadius * 0.14f + trembleOffset, browY),
            strokeWidth = headRadius * 0.07f
        )
        drawLine(
            hairColor,
            Offset(centerX + eyeSpacing - headRadius * 0.14f + trembleOffset, browY),
            Offset(centerX + eyeSpacing + headRadius * 0.14f + trembleOffset, browY + browTilt),
            strokeWidth = headRadius * 0.07f
        )

        // Nose
        drawCircle(skinShadow, headRadius * 0.055f, Offset(centerX + trembleOffset, headCenterY + headRadius * 0.2f))

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
    val shadow = Color.Black.copy(alpha = 0.25f).compositeOver(color)
    val highlight = Color.White.copy(alpha = 0.18f).compositeOver(color)
    when (style % 4) {
        0 -> { // Short swept
            drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.08f, cy - r * 1.05f), Size(r * 2.16f, r * 1.35f))
            drawArc(color, 180f, 180f, true, Offset(cx - r * 1.08f, cy - r * 1.12f), Size(r * 2.16f, r * 1.4f))
            drawArc(color, 200f, 140f, true, Offset(cx - r * 0.9f, cy - r * 1.25f), Size(r * 1.8f, r * 0.9f))
            drawArc(highlight, 210f, 60f, true, Offset(cx - r * 0.6f, cy - r * 1.2f), Size(r * 1.0f, r * 0.5f))
        }
        1 -> { // Crew cut
            drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.02f, cy - r * 1.0f), Size(r * 2.04f, r * 1.1f))
            drawArc(color, 180f, 180f, true, Offset(cx - r * 1.02f, cy - r * 1.08f), Size(r * 2.04f, r * 1.15f))
            drawRect(color, Offset(cx - r * 0.85f, cy - r * 1.15f), Size(r * 1.7f, r * 0.45f))
            drawRect(highlight, Offset(cx - r * 0.5f, cy - r * 1.15f), Size(r * 0.8f, r * 0.15f))
        }
        2 -> { // Long flowing
            drawOval(shadow, Offset(cx - r * 1.18f, cy - r * 0.3f), Size(r * 0.42f, r * 1.1f))
            drawOval(shadow, Offset(cx + r * 0.76f, cy - r * 0.3f), Size(r * 0.42f, r * 1.1f))
            drawOval(color, Offset(cx - r * 1.15f, cy - r * 0.35f), Size(r * 0.4f, r * 1.05f))
            drawOval(color, Offset(cx + r * 0.75f, cy - r * 0.35f), Size(r * 0.4f, r * 1.05f))
            drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.15f, cy - r * 1.1f), Size(r * 2.3f, r * 1.45f))
            drawArc(color, 180f, 180f, true, Offset(cx - r * 1.15f, cy - r * 1.18f), Size(r * 2.3f, r * 1.5f))
            drawArc(highlight, 200f, 80f, true, Offset(cx - r * 0.7f, cy - r * 1.15f), Size(r * 1.2f, r * 0.6f))
        }
        3 -> { // Spiky
            drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.05f, cy - r * 1.1f), Size(r * 2.1f, r * 1.2f))
            drawArc(color, 180f, 180f, true, Offset(cx - r * 1.05f, cy - r * 1.2f), Size(r * 2.1f, r * 1.25f))
            val spikePath = Path().apply {
                moveTo(cx - r * 0.7f, cy - r * 1.1f)
                lineTo(cx - r * 0.55f, cy - r * 1.45f)
                lineTo(cx - r * 0.25f, cy - r * 1.15f)
                lineTo(cx - r * 0.05f, cy - r * 1.5f)
                lineTo(cx + r * 0.2f, cy - r * 1.15f)
                lineTo(cx + r * 0.45f, cy - r * 1.4f)
                lineTo(cx + r * 0.7f, cy - r * 1.1f)
                close()
            }
            drawPath(spikePath, color)
            drawArc(highlight, 210f, 60f, true, Offset(cx - r * 0.5f, cy - r * 1.3f), Size(r * 0.8f, r * 0.4f))
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
