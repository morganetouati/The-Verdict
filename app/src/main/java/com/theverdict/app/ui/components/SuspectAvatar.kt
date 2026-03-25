package com.theverdict.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val centerX = w / 2f
        val headRadius = w * 0.3f
        val headCenterY = h * 0.35f

        // Neck
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - w * 0.08f, headCenterY + headRadius * 0.7f),
            size = Size(w * 0.16f, h * 0.15f)
        )

        // Body (shoulders)
        drawOval(
            color = Color(0xFF444444),
            topLeft = Offset(centerX - w * 0.35f, h * 0.65f),
            size = Size(w * 0.7f, h * 0.4f)
        )

        // Head
        drawCircle(color = skinColor, radius = headRadius, center = Offset(centerX, headCenterY))

        // Hair
        drawHair(config.hairStyle, hairColor, centerX, headCenterY, headRadius)

        // Eyes
        val eyeY = headCenterY - headRadius * 0.05f
        val eyeSpacing = headRadius * 0.45f
        val hasShiftyEyes = clues.any { it == Clue.REGARDE_AILLEURS || it == Clue.EVITE_REGARD }
        val eyeOffsetX = if (hasShiftyEyes) headRadius * 0.08f else 0f

        drawCircle(Color.White, headRadius * 0.18f, Offset(centerX - eyeSpacing, eyeY))
        drawCircle(Color.White, headRadius * 0.18f, Offset(centerX + eyeSpacing, eyeY))
        drawCircle(Color(0xFF2C1B0E), headRadius * 0.1f, Offset(centerX - eyeSpacing + eyeOffsetX, eyeY))
        drawCircle(Color(0xFF2C1B0E), headRadius * 0.1f, Offset(centerX + eyeSpacing + eyeOffsetX, eyeY))

        // Eyebrows
        val isNervous = clues.any { it == Clue.NERVEUX || it == Clue.TRANSPIRE }
        val browY = eyeY - headRadius * 0.22f
        val browTilt = if (isNervous) headRadius * 0.06f else 0f
        drawLine(Color(0xFF2C1B0E), Offset(centerX - eyeSpacing - headRadius * 0.12f, browY + browTilt), Offset(centerX - eyeSpacing + headRadius * 0.12f, browY), strokeWidth = headRadius * 0.06f)
        drawLine(Color(0xFF2C1B0E), Offset(centerX + eyeSpacing - headRadius * 0.12f, browY), Offset(centerX + eyeSpacing + headRadius * 0.12f, browY + browTilt), strokeWidth = headRadius * 0.06f)

        // Mouth
        val mouthY = headCenterY + headRadius * 0.35f
        val isConfident = Clue.CONFIANT in clues
        val isSmilingTooMuch = Clue.SOURIT_TROP in clues
        if (isConfident || isSmilingTooMuch) {
            drawArc(Color(0xFF2C1B0E), 0f, 180f, false, Offset(centerX - headRadius * 0.2f, mouthY - headRadius * 0.08f), Size(headRadius * 0.4f, headRadius * 0.2f))
        } else if (isNervous) {
            drawLine(Color(0xFF2C1B0E), Offset(centerX - headRadius * 0.12f, mouthY), Offset(centerX + headRadius * 0.12f, mouthY + headRadius * 0.04f), strokeWidth = headRadius * 0.04f)
        } else {
            drawLine(Color(0xFF2C1B0E), Offset(centerX - headRadius * 0.15f, mouthY), Offset(centerX + headRadius * 0.15f, mouthY), strokeWidth = headRadius * 0.04f)
        }

        // Sweat drop
        if (Clue.TRANSPIRE in clues || Clue.MAINS_TREMBLENT in clues) {
            drawCircle(Color(0xFF42A5F5), headRadius * 0.07f, Offset(centerX + headRadius * 0.85f, headCenterY - headRadius * 0.1f))
            drawCircle(Color(0xFF42A5F5), headRadius * 0.05f, Offset(centerX + headRadius * 0.95f, headCenterY + headRadius * 0.1f))
        }

        // Accessory
        drawAccessory(config.accessory, centerX, headCenterY, headRadius)
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
