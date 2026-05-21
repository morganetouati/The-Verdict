package com.theverdict.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theverdict.app.domain.model.AvatarConfig
import com.theverdict.app.domain.model.Clue
import kotlinx.coroutines.delay

private val skinTones = listOf(
    Color(0xFFFDDEB4), Color(0xFFF1C27D), Color(0xFFE0AC69),
    Color(0xFFC68642), Color(0xFF8D5524), Color(0xFF6B3A2A)
)

private val hairColors = listOf(
    Color(0xFF2C1B0E), Color(0xFF4A3218), Color(0xFF8B6914),
    Color(0xFFD4A24C), Color(0xFFE53935), Color(0xFF333333)
)

private val eyeColors = listOf(
    Color(0xFF5D4037),  // Brown
    Color(0xFF1565C0),  // Blue
    Color(0xFF2E7D32),  // Green
    Color(0xFF78909C),  // Gray
    Color(0xFF6A1B9A),  // Hazel/Violet
    Color(0xFF1A1A1A),  // Near black
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
    pressureLevel: Int = 0,
    modifier: Modifier = Modifier
) {
    val isFemale = config.gender == 1
    val skinColor = skinTones.getOrElse(config.skinTone) { skinTones[0] }
    val hairColor = hairColors.getOrElse(config.hairColor) { hairColors[0] }
    val irisColor = eyeColors.getOrElse(config.eyeColor) { eyeColors[0] }
    val skinShadow = Color.Black.copy(alpha = 0.2f).compositeOver(skinColor)
    val clothingColor = clothingColors[(config.skinTone * 3 + config.hairStyle * 2 + config.hairColor) % clothingColors.size]

    val inf = rememberInfiniteTransition(label = "avatar")

    val hasShiftyEyes = clues.any { it == Clue.REGARDE_AILLEURS || it == Clue.EVITE_REGARD }
    val eyeShift by inf.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "eyeShift"
    )

    val sweatDrop by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "sweatDrop"
    )

    val isNervous = clues.any { it == Clue.NERVEUX || it == Clue.MAINS_TREMBLENT }
    val tremble by inf.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(100, easing = LinearEasing), RepeatMode.Reverse),
        label = "tremble"
    )

    val isTalking = clues.any { it == Clue.PARLE_VITE || it == Clue.HESITE || it == Clue.VOIX_CHANGE }
    val mouthAnim by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(if (Clue.PARLE_VITE in clues) 300 else 800, easing = LinearEasing), RepeatMode.Reverse),
        label = "mouthAnim"
    )

    val browAnim by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse),
        label = "browAnim"
    )

    val breathAnim by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Reverse),
        label = "breath"
    )

    // === IDLE ANIMATIONS ===
    // Floating: gentle sinusoidal vertical drift
    val floatAnim by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Reverse),
        label = "float"
    )
    // Pressure shake: fast horizontal oscillation when pressure is high
    val pressureShake by inf.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(130, easing = LinearEasing), RepeatMode.Reverse),
        label = "pressShake"
    )
    // Blink: random coroutine-driven eye close
    val blinkScale = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay((2500L..5000L).random())
            blinkScale.animateTo(0f, tween(80))
            blinkScale.animateTo(1f, tween(80))
        }
    }

    val blinkScaleVal = blinkScale.value
    val floatOffset = (floatAnim - 0.5f) * 12f
    val shakeOffset = if (pressureLevel >= 80) pressureShake * 5f else 0f

    Box(modifier = modifier.size(size)) {
        // Red pressure glow halo when stress is critical
        if (pressureLevel >= 80) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF1744).copy(alpha = 0.30f), Color.Transparent),
                        radius = this.size.width * 0.56f
                    )
                )
            }
        }
        Canvas(modifier = Modifier.size(size).graphicsLayer {
            translationY = floatOffset
            translationX = shakeOffset
        }) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val headRadius = w * 0.3f
        val headCenterY = h * 0.34f
        val trembleOffset = if (isNervous) tremble * w * 0.005f else 0f
        val breathOffset = breathAnim * w * 0.006f

        // Face oval dimensions differ by gender
        val headW = if (isFemale) headRadius * 1.88f else headRadius * 2.0f
        val headH = if (isFemale) headRadius * 2.25f else headRadius * 2.16f

        // Dark circular background
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF2A2A2A), Color(0xFF1A1A1A), Color.Transparent),
                center = Offset(cx, h * 0.45f), radius = w * 0.48f
            ),
            radius = w * 0.48f, center = Offset(cx, h * 0.45f)
        )

        // Ground shadow
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(cx, h * 0.92f)
            ),
            topLeft = Offset(cx - w * 0.32f, h * 0.88f),
            size = Size(w * 0.64f, h * 0.1f)
        )

        // === BODY WITH ARMS ===
        val bodyTop = h * 0.63f
        val bodyWidth = if (isFemale) w * 0.54f else w * 0.64f
        val armLength = h * 0.21f
        val armWidth = w * 0.095f
        val armTopY = bodyTop + h * 0.02f
        val armBottomY = armTopY + armLength
        val leftArmCx = cx - bodyWidth / 2 - armWidth * 0.3f + trembleOffset
        val rightArmCx = cx + bodyWidth / 2 + armWidth * 0.3f + trembleOffset

        // Arms
        drawOval(clothingColor, Offset(leftArmCx - armWidth / 2, armTopY), Size(armWidth, armLength))
        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent),
                startX = leftArmCx - armWidth / 2, endX = leftArmCx + armWidth
            ),
            topLeft = Offset(leftArmCx - armWidth / 2, armTopY), size = Size(armWidth, armLength)
        )
        drawOval(skinColor, Offset(leftArmCx - armWidth * 0.45f, armBottomY - armWidth * 0.35f), Size(armWidth * 0.9f, armWidth * 0.65f))

        drawOval(clothingColor, Offset(rightArmCx - armWidth / 2, armTopY), Size(armWidth, armLength))
        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f)),
                startX = rightArmCx - armWidth, endX = rightArmCx + armWidth / 2
            ),
            topLeft = Offset(rightArmCx - armWidth / 2, armTopY), size = Size(armWidth, armLength)
        )
        drawOval(skinColor, Offset(rightArmCx - armWidth * 0.45f, armBottomY - armWidth * 0.35f), Size(armWidth * 0.9f, armWidth * 0.65f))

        // Body torso
        drawOval(
            color = clothingColor,
            topLeft = Offset(cx - bodyWidth / 2 + trembleOffset - breathOffset, bodyTop - breathOffset * 0.5f),
            size = Size(bodyWidth + breathOffset * 2f, h * 0.42f + breathOffset)
        )
        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.14f), Color.Transparent),
                startY = bodyTop, endY = bodyTop + h * 0.13f
            ),
            topLeft = Offset(cx - bodyWidth * 0.44f + trembleOffset, bodyTop + h * 0.01f),
            size = Size(bodyWidth * 0.88f, h * 0.14f)
        )

        // Neckline / collar
        if (isFemale) {
            val necklinePath = Path().apply {
                moveTo(cx - w * 0.09f + trembleOffset, bodyTop)
                lineTo(cx + trembleOffset, bodyTop + h * 0.09f)
                lineTo(cx + w * 0.09f + trembleOffset, bodyTop)
            }
            drawPath(necklinePath, skinColor)
        } else {
            val collarPath = Path().apply {
                moveTo(cx - w * 0.08f + trembleOffset, bodyTop)
                lineTo(cx + trembleOffset, bodyTop + h * 0.06f)
                lineTo(cx + w * 0.08f + trembleOffset, bodyTop)
            }
            drawPath(collarPath, skinColor)
            val tiePath = Path().apply {
                moveTo(cx - w * 0.024f + trembleOffset, bodyTop + h * 0.035f)
                lineTo(cx + w * 0.024f + trembleOffset, bodyTop + h * 0.035f)
                lineTo(cx + w * 0.016f + trembleOffset, bodyTop + h * 0.11f)
                lineTo(cx + trembleOffset, bodyTop + h * 0.135f)
                lineTo(cx - w * 0.016f + trembleOffset, bodyTop + h * 0.11f)
                close()
            }
            drawPath(tiePath, GoldPrimaryRaw.copy(alpha = 0.75f))
        }

        if (Clue.BRAS_CROISES in clues) {
            val armColor = Color.White.copy(alpha = 0.12f).compositeOver(clothingColor)
            drawLine(armColor, Offset(cx - w * 0.3f, h * 0.78f), Offset(cx + w * 0.3f, h * 0.72f), strokeWidth = w * 0.04f)
            drawLine(armColor, Offset(cx + w * 0.3f, h * 0.78f), Offset(cx - w * 0.3f, h * 0.72f), strokeWidth = w * 0.04f)
        }

        // === NECK ===
        val neckTop = headCenterY + headH / 2 - headRadius * 0.12f
        val neckWidth = if (isFemale) w * 0.11f else w * 0.14f
        drawRect(skinColor, Offset(cx - neckWidth / 2 + trembleOffset, neckTop), Size(neckWidth, h * 0.16f))
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(skinShadow, Color.Transparent),
                startY = neckTop, endY = neckTop + headRadius * 0.4f
            ),
            topLeft = Offset(cx - neckWidth / 2 + trembleOffset, neckTop),
            size = Size(neckWidth, headRadius * 0.4f)
        )

        // Shadow under head
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent),
                center = Offset(cx + trembleOffset, headCenterY + headH * 0.42f)
            ),
            topLeft = Offset(cx - headW * 0.55f + trembleOffset, headCenterY + headH * 0.35f),
            size = Size(headW * 1.1f, headH * 0.22f)
        )

        // === EARS ===
        val earW = headRadius * 0.22f
        val earH = headRadius * 0.32f
        val earCY = headCenterY + headRadius * 0.08f
        val earX = headW / 2 * 0.96f
        drawOval(skinColor, Offset(cx - earX - earW * 0.5f + trembleOffset, earCY - earH / 2), Size(earW, earH))
        drawOval(skinShadow, Offset(cx - earX - earW * 0.15f + trembleOffset, earCY - earH * 0.3f), Size(earW * 0.5f, earH * 0.6f))
        drawOval(skinColor, Offset(cx + earX - earW * 0.5f + trembleOffset, earCY - earH / 2), Size(earW, earH))
        drawOval(skinShadow, Offset(cx + earX - earW * 0.15f + trembleOffset, earCY - earH * 0.3f), Size(earW * 0.5f, earH * 0.6f))

        // === HEAD (realistic face shape via Path) ===
        drawFaceShape(isFemale, cx + trembleOffset, headCenterY, headW, headH, skinColor)

        // Face highlights and shadows
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.14f), Color.Transparent),
                center = Offset(cx - headW * 0.18f + trembleOffset, headCenterY - headH * 0.28f),
                radius = headRadius * 0.85f
            ),
            topLeft = Offset(cx - headW / 2 + trembleOffset, headCenterY - headH / 2),
            size = Size(headW, headH)
        )
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.1f)),
                center = Offset(cx + trembleOffset, headCenterY + headH * 0.3f),
                radius = headRadius * 0.95f
            ),
            topLeft = Offset(cx - headW / 2 + trembleOffset, headCenterY - headH / 2),
            size = Size(headW, headH)
        )
        // Face outline
        drawFaceShape(isFemale, cx + trembleOffset, headCenterY, headW, headH, Color.Black.copy(alpha = 0.18f), strokeWidth = headRadius * 0.022f)

        // === CHEEK BLUSH ===
        val blushAlpha = if (isFemale) 0.2f else 0.07f
        val blushR = headRadius * (if (isFemale) 0.2f else 0.14f)
        drawCircle(Color(0xFFFF8888).copy(alpha = blushAlpha), blushR, Offset(cx - headW * 0.3f + trembleOffset, headCenterY + headRadius * 0.22f))
        drawCircle(Color(0xFFFF8888).copy(alpha = blushAlpha), blushR, Offset(cx + headW * 0.3f + trembleOffset, headCenterY + headRadius * 0.22f))

        // === HAIR ===
        drawHair(config.hairStyle, config.gender, hairColor, cx + trembleOffset, headCenterY, headRadius)

        // === EYES ===
        val eyeY = headCenterY - headRadius * 0.04f
        val eyeSpacing = headRadius * 0.37f
        val eyeW = headRadius * 0.3f
        val eyeH = (if (isFemale) headRadius * 0.27f else headRadius * 0.24f) * blinkScaleVal.coerceAtLeast(0.02f)
        val eyeOffsetX = if (hasShiftyEyes) eyeShift * headRadius * 0.1f else 0f
        val leftEyeCx = cx - eyeSpacing + trembleOffset
        val rightEyeCx = cx + eyeSpacing + trembleOffset

        // Eye whites
        drawOval(Color.White, Offset(leftEyeCx - eyeW / 2, eyeY - eyeH / 2), Size(eyeW, eyeH))
        drawOval(Color.White, Offset(rightEyeCx - eyeW / 2, eyeY - eyeH / 2), Size(eyeW, eyeH))

        // Iris (colored)
        val irisR = headRadius * 0.105f
        val irisY = eyeY + headRadius * 0.015f
        drawCircle(irisColor, irisR, Offset(leftEyeCx + eyeOffsetX, irisY))
        drawCircle(irisColor, irisR, Offset(rightEyeCx + eyeOffsetX, irisY))
        // Iris limbal ring
        drawCircle(Color.Black.copy(alpha = 0.4f), irisR, Offset(leftEyeCx + eyeOffsetX, irisY), style = Stroke(irisR * 0.18f))
        drawCircle(Color.Black.copy(alpha = 0.4f), irisR, Offset(rightEyeCx + eyeOffsetX, irisY), style = Stroke(irisR * 0.18f))

        // Pupils
        drawCircle(Color(0xFF0D0D0D), headRadius * 0.06f, Offset(leftEyeCx + eyeOffsetX, irisY))
        drawCircle(Color(0xFF0D0D0D), headRadius * 0.06f, Offset(rightEyeCx + eyeOffsetX, irisY))

        // Highlights
        drawCircle(Color.White.copy(alpha = 0.92f), headRadius * 0.038f, Offset(leftEyeCx + eyeOffsetX + headRadius * 0.048f, irisY - headRadius * 0.042f))
        drawCircle(Color.White.copy(alpha = 0.92f), headRadius * 0.038f, Offset(rightEyeCx + eyeOffsetX + headRadius * 0.048f, irisY - headRadius * 0.042f))

        // Eye outlines (upper lid thicker = lash line)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(leftEyeCx - eyeW / 2, eyeY - eyeH / 2), Size(eyeW, eyeH), style = Stroke(headRadius * 0.024f))
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(rightEyeCx - eyeW / 2, eyeY - eyeH / 2), Size(eyeW, eyeH), style = Stroke(headRadius * 0.024f))

        // Eyelashes (female only)
        if (isFemale) {
            drawLashes(leftEyeCx, eyeY, eyeW, eyeH)
            drawLashes(rightEyeCx, eyeY, eyeW, eyeH)
        }

        // === EYEBROWS ===
        val browY = eyeY - headRadius * 0.26f
        val browTilt = if (isNervous) browAnim * headRadius * 0.08f else 0f
        val browSpan = headRadius * 0.15f

        if (isFemale) {
            // Thin arched brows
            val browPathL = Path().apply {
                moveTo(leftEyeCx - browSpan + trembleOffset, browY + browTilt + headRadius * 0.04f)
                cubicTo(leftEyeCx + trembleOffset, browY + browTilt - headRadius * 0.06f,
                    leftEyeCx + trembleOffset, browY + browTilt - headRadius * 0.06f,
                    leftEyeCx + browSpan + trembleOffset, browY + browTilt + headRadius * 0.02f)
            }
            drawPath(browPathL, hairColor, style = Stroke(headRadius * 0.04f))
            val browPathR = Path().apply {
                moveTo(rightEyeCx - browSpan + trembleOffset, browY + browTilt + headRadius * 0.02f)
                cubicTo(rightEyeCx + trembleOffset, browY + browTilt - headRadius * 0.06f,
                    rightEyeCx + trembleOffset, browY + browTilt - headRadius * 0.06f,
                    rightEyeCx + browSpan + trembleOffset, browY + browTilt + headRadius * 0.04f)
            }
            drawPath(browPathR, hairColor, style = Stroke(headRadius * 0.04f))
        } else {
            // Thick straight brows
            drawLine(hairColor, Offset(leftEyeCx - browSpan + trembleOffset, browY + browTilt), Offset(leftEyeCx + browSpan + trembleOffset, browY), strokeWidth = headRadius * 0.078f)
            drawLine(hairColor, Offset(rightEyeCx - browSpan + trembleOffset, browY), Offset(rightEyeCx + browSpan + trembleOffset, browY + browTilt), strokeWidth = headRadius * 0.078f)
        }

        // === NOSE (nostrils + bridge) ===
        val noseY = headCenterY + headRadius * 0.24f
        val nostrilSize = headRadius * 0.075f
        drawLine(skinShadow.copy(alpha = 0.28f), Offset(cx + trembleOffset, headCenterY - headRadius * 0.08f), Offset(cx + trembleOffset, noseY - nostrilSize * 0.5f), strokeWidth = headRadius * 0.022f)
        drawOval(skinShadow.copy(alpha = 0.45f), Offset(cx - headRadius * 0.14f + trembleOffset, noseY - nostrilSize * 0.4f), Size(nostrilSize, nostrilSize * 1.1f))
        drawOval(skinShadow.copy(alpha = 0.45f), Offset(cx + headRadius * 0.065f + trembleOffset, noseY - nostrilSize * 0.4f), Size(nostrilSize, nostrilSize * 1.1f))

        // === MOUTH (upper + lower lip) ===
        val mouthY = headCenterY + headRadius * 0.44f
        val isConfident = Clue.CONFIANT in clues
        val isSmilingTooMuch = Clue.SOURIT_TROP in clues
        val lipColor = if (isFemale) Color(0xFFCC6677) else Color(0xFF2C1B0E)
        val lowerLipColor = if (isFemale) Color(0xFFDD7788) else Color(0xFF3A2515)

        when {
            isConfident || isSmilingTooMuch -> {
                val smileW = headRadius * (0.23f + if (isSmilingTooMuch) mouthAnim * 0.08f else 0f)
                drawArc(lipColor, 0f, 180f, false,
                    Offset(cx - smileW + trembleOffset, mouthY - headRadius * 0.09f),
                    Size(smileW * 2f, headRadius * 0.22f))
                drawOval(lowerLipColor.copy(alpha = 0.55f), Offset(cx - smileW * 0.75f + trembleOffset, mouthY + headRadius * 0.01f), Size(smileW * 1.5f, headRadius * 0.1f))
            }
            isTalking -> {
                val openAmt = mouthAnim * headRadius * 0.14f
                drawOval(Color(0xFF1A0800), Offset(cx - headRadius * 0.12f + trembleOffset, mouthY - openAmt / 2), Size(headRadius * 0.24f, openAmt.coerceAtLeast(headRadius * 0.04f)))
                drawOval(lipColor, Offset(cx - headRadius * 0.13f + trembleOffset, mouthY - headRadius * 0.04f), Size(headRadius * 0.26f, headRadius * 0.06f))
                drawOval(lowerLipColor, Offset(cx - headRadius * 0.11f + trembleOffset, mouthY + openAmt / 2 - headRadius * 0.04f), Size(headRadius * 0.22f, headRadius * 0.07f))
            }
            isNervous -> {
                drawLine(lipColor, Offset(cx - headRadius * 0.12f + trembleOffset, mouthY + tremble * headRadius * 0.025f), Offset(cx + headRadius * 0.12f + trembleOffset, mouthY - tremble * headRadius * 0.025f), strokeWidth = headRadius * 0.048f)
            }
            else -> {
                // Neutral lips: thin upper, fuller lower
                drawLine(lipColor, Offset(cx - headRadius * 0.14f + trembleOffset, mouthY - headRadius * 0.015f), Offset(cx + headRadius * 0.14f + trembleOffset, mouthY - headRadius * 0.015f), strokeWidth = headRadius * 0.032f)
                drawOval(lowerLipColor.copy(alpha = if (isFemale) 0.65f else 0.4f), Offset(cx - headRadius * 0.1f + trembleOffset, mouthY), Size(headRadius * 0.2f, headRadius * (if (isFemale) 0.09f else 0.06f)))
            }
        }

        // === SWEAT DROPS on cheek ===
        if (Clue.TRANSPIRE in clues) {
            val cheekX = cx + headW * 0.36f
            val sweatStartY = headCenterY + headRadius * 0.1f
            val sweatY1 = sweatStartY + sweatDrop * headRadius * 0.45f
            val sweatAlpha = (1f - sweatDrop * 0.8f).coerceIn(0.2f, 1f)
            val sweatColor = Color(0xFF42A5F5).copy(alpha = sweatAlpha)
            drawCircle(sweatColor, headRadius * 0.055f, Offset(cheekX, sweatY1))
            drawCircle(sweatColor.copy(alpha = sweatAlpha * 0.5f), headRadius * 0.035f, Offset(cheekX, sweatY1 - headRadius * 0.1f))
            val sweatDrop2 = (sweatDrop + 0.5f) % 1f
            val sweatY2 = sweatStartY + sweatDrop2 * headRadius * 0.45f
            val sweatAlpha2 = (1f - sweatDrop2 * 0.8f).coerceIn(0.2f, 1f)
            drawCircle(Color(0xFF42A5F5).copy(alpha = sweatAlpha2), headRadius * 0.045f, Offset(cheekX + headRadius * 0.1f, sweatY2))
        }

        // === ACCESSORIES ===
        drawAccessory(config.accessory, isFemale, cx + trembleOffset, headCenterY, headRadius)
        }  // end Canvas
    }  // end Box
}

// Draw face as a realistic oval path with jaw/chin shape
private fun DrawScope.drawFaceShape(
    isFemale: Boolean, cx: Float, cy: Float,
    headW: Float, headH: Float, color: Color,
    strokeWidth: Float = 0f
) {
    val halfW = headW / 2f
    val halfH = headH / 2f
    val jaw = if (isFemale) 0.46f else 0.56f  // female = narrower pointed chin

    val facePath = Path().apply {
        moveTo(cx, cy - halfH)
        // Right side: forehead → temple → cheek → jaw → chin
        cubicTo(cx + halfW * 0.68f, cy - halfH, cx + halfW, cy - halfH * 0.3f, cx + halfW, cy)
        cubicTo(cx + halfW, cy + halfH * 0.45f, cx + halfW * jaw, cy + halfH, cx, cy + halfH)
        // Left side (mirror)
        cubicTo(cx - halfW * jaw, cy + halfH, cx - halfW, cy + halfH * 0.45f, cx - halfW, cy)
        cubicTo(cx - halfW, cy - halfH * 0.3f, cx - halfW * 0.68f, cy - halfH, cx, cy - halfH)
        close()
    }
    if (strokeWidth > 0f) {
        drawPath(facePath, color, style = Stroke(strokeWidth))
    } else {
        drawPath(facePath, color)
    }
}

// Draw upper eyelashes for feminine avatars
private fun DrawScope.drawLashes(eyeCenterX: Float, eyeCenterY: Float, eyeW: Float, eyeH: Float) {
    val lashColor = Color(0xFF0D0D0D)
    val topY = eyeCenterY - eyeH / 2
    val lashCount = 5
    val lashLength = eyeH * 0.5f
    for (i in 0 until lashCount) {
        val t = i / (lashCount - 1).toFloat()
        val x = eyeCenterX - eyeW * 0.45f + t * eyeW * 0.9f
        val spread = (t - 0.5f) * 0.55f
        drawLine(lashColor, Offset(x, topY), Offset(x + spread * lashLength, topY - lashLength), strokeWidth = eyeH * 0.072f)
    }
}

private fun DrawScope.drawHair(style: Int, gender: Int, color: Color, cx: Float, cy: Float, r: Float) {
    val shadow = Color.Black.copy(alpha = 0.28f).compositeOver(color)
    val highlight = Color.White.copy(alpha = 0.2f).compositeOver(color)
    val isFemale = gender == 1

    if (isFemale) {
        when (style % 8) {
            0 -> { // Long flowing
                drawOval(shadow, Offset(cx - r * 1.18f, cy - r * 0.28f), Size(r * 0.44f, r * 1.3f))
                drawOval(shadow, Offset(cx + r * 0.74f, cy - r * 0.28f), Size(r * 0.44f, r * 1.3f))
                drawOval(color, Offset(cx - r * 1.15f, cy - r * 0.32f), Size(r * 0.42f, r * 1.25f))
                drawOval(color, Offset(cx + r * 0.73f, cy - r * 0.32f), Size(r * 0.42f, r * 1.25f))
                drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.12f, cy - r * 1.15f), Size(r * 2.24f, r * 1.5f))
                drawArc(color, 180f, 180f, true, Offset(cx - r * 1.12f, cy - r * 1.22f), Size(r * 2.24f, r * 1.55f))
                drawArc(highlight, 200f, 80f, true, Offset(cx - r * 0.68f, cy - r * 1.18f), Size(r * 1.2f, r * 0.6f))
            }
            1 -> { // Ponytail
                drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.05f, cy - r * 1.05f), Size(r * 2.1f, r * 1.3f))
                drawArc(color, 180f, 180f, true, Offset(cx - r * 1.05f, cy - r * 1.12f), Size(r * 2.1f, r * 1.35f))
                drawOval(color, Offset(cx + r * 0.65f, cy - r * 0.95f), Size(r * 0.5f, r * 0.35f))
                drawOval(shadow, Offset(cx + r * 0.62f, cy - r * 0.82f), Size(r * 0.38f, r * 0.22f))
                drawOval(color, Offset(cx + r * 0.72f, cy - r * 0.62f), Size(r * 0.27f, r * 0.9f))
                drawOval(highlight, Offset(cx + r * 0.76f, cy - r * 0.6f), Size(r * 0.1f, r * 0.35f))
            }
            2 -> { // Bob (carré)
                drawOval(shadow, Offset(cx - r * 1.1f, cy - r * 0.32f), Size(r * 0.35f, r * 0.62f))
                drawOval(shadow, Offset(cx + r * 0.75f, cy - r * 0.32f), Size(r * 0.35f, r * 0.62f))
                drawOval(color, Offset(cx - r * 1.08f, cy - r * 0.35f), Size(r * 0.33f, r * 0.62f))
                drawOval(color, Offset(cx + r * 0.75f, cy - r * 0.35f), Size(r * 0.33f, r * 0.62f))
                drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.08f, cy - r * 1.08f), Size(r * 2.16f, r * 1.35f))
                drawArc(color, 180f, 180f, true, Offset(cx - r * 1.08f, cy - r * 1.15f), Size(r * 2.16f, r * 1.4f))
                drawRect(color, Offset(cx - r * 1.08f, cy - r * 0.35f), Size(r * 2.16f, r * 0.28f))
                drawArc(highlight, 200f, 80f, true, Offset(cx - r * 0.65f, cy - r * 1.1f), Size(r * 1.1f, r * 0.55f))
            }
            3 -> { // Chignon (bun)
                drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.02f, cy - r * 0.98f), Size(r * 2.04f, r * 1.1f))
                drawArc(color, 180f, 180f, true, Offset(cx - r * 1.02f, cy - r * 1.05f), Size(r * 2.04f, r * 1.15f))
                drawCircle(shadow, r * 0.3f, Offset(cx + r * 0.28f, cy - r * 1.06f))
                drawCircle(color, r * 0.27f, Offset(cx + r * 0.28f, cy - r * 1.09f))
                drawCircle(highlight, r * 0.1f, Offset(cx + r * 0.22f, cy - r * 1.14f))
            }
            4 -> { // Curly long
                drawOval(color, Offset(cx - r * 1.18f, cy - r * 0.4f), Size(r * 0.5f, r * 1.3f))
                drawOval(color, Offset(cx + r * 0.68f, cy - r * 0.4f), Size(r * 0.5f, r * 1.3f))
                drawArc(color, 180f, 180f, true, Offset(cx - r * 1.15f, cy - r * 1.2f), Size(r * 2.3f, r * 1.6f))
                for (i in 0..4) {
                    val xOff = cx - r * 0.82f + i * r * 0.41f
                    drawOval(shadow, Offset(xOff, cy - r * 1.28f), Size(r * 0.24f, r * 0.22f))
                }
                drawArc(highlight, 200f, 70f, true, Offset(cx - r * 0.62f, cy - r * 1.22f), Size(r * 1.0f, r * 0.5f))
            }
            5 -> { // Braids (tresses)
                drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.05f, cy - r * 1.05f), Size(r * 2.1f, r * 1.3f))
                drawArc(color, 180f, 180f, true, Offset(cx - r * 1.05f, cy - r * 1.12f), Size(r * 2.1f, r * 1.35f))
                val bx = cx - r * 0.44f
                val bx2 = cx + r * 0.44f
                for (i in 0..5) {
                    val yOff = cy - r * 0.08f + i * r * 0.18f
                    val alt = if (i % 2 == 0) r * 0.06f else -r * 0.06f
                    drawOval(if (i % 2 == 0) color else shadow, Offset(bx - r * 0.12f + alt, yOff), Size(r * 0.26f, r * 0.2f))
                    drawOval(if (i % 2 == 0) color else shadow, Offset(bx2 - r * 0.12f - alt, yOff), Size(r * 0.26f, r * 0.2f))
                }
            }
            6 -> { // Pixie cut
                drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.02f, cy - r * 0.98f), Size(r * 2.04f, r * 1.1f))
                drawArc(color, 180f, 180f, true, Offset(cx - r * 1.02f, cy - r * 1.05f), Size(r * 2.04f, r * 1.15f))
                drawRect(color, Offset(cx - r * 0.82f, cy - r * 1.08f), Size(r * 1.64f, r * 0.42f))
                val sweepPath = Path().apply {
                    moveTo(cx + r * 0.28f, cy - r * 1.1f)
                    lineTo(cx + r * 0.82f, cy - r * 0.95f)
                    lineTo(cx + r * 0.82f, cy - r * 1.18f)
                    close()
                }
                drawPath(sweepPath, color)
                drawArc(highlight, 210f, 50f, true, Offset(cx - r * 0.38f, cy - r * 1.12f), Size(r * 0.7f, r * 0.35f))
            }
            7 -> { // Frange (bangs)
                drawOval(shadow, Offset(cx - r * 1.15f, cy - r * 0.3f), Size(r * 0.42f, r * 1.12f))
                drawOval(shadow, Offset(cx + r * 0.73f, cy - r * 0.3f), Size(r * 0.42f, r * 1.12f))
                drawOval(color, Offset(cx - r * 1.12f, cy - r * 0.35f), Size(r * 0.4f, r * 1.08f))
                drawOval(color, Offset(cx + r * 0.72f, cy - r * 0.35f), Size(r * 0.4f, r * 1.08f))
                drawArc(shadow, 180f, 180f, true, Offset(cx - r * 1.12f, cy - r * 1.1f), Size(r * 2.24f, r * 1.45f))
                drawArc(color, 180f, 180f, true, Offset(cx - r * 1.12f, cy - r * 1.18f), Size(r * 2.24f, r * 1.5f))
                drawRect(color, Offset(cx - r * 0.9f, cy - r * 1.18f), Size(r * 1.8f, r * 0.42f))
                drawArc(highlight, 200f, 70f, true, Offset(cx - r * 0.55f, cy - r * 1.15f), Size(r * 0.9f, r * 0.4f))
            }
        }
    } else {
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
}

private fun DrawScope.drawAccessory(accessory: Int, isFemale: Boolean, cx: Float, cy: Float, r: Float) {
    when (accessory % 4) {
        1 -> { // Glasses
            val eyeY = cy - r * 0.04f
            val spacing = r * 0.45f
            drawCircle(Color(0xFF666666), r * 0.24f, Offset(cx - spacing, eyeY), style = Stroke(r * 0.04f))
            drawCircle(Color(0xFF666666), r * 0.24f, Offset(cx + spacing, eyeY), style = Stroke(r * 0.04f))
            drawLine(Color(0xFF666666), Offset(cx - spacing + r * 0.24f, eyeY), Offset(cx + spacing - r * 0.24f, eyeY), strokeWidth = r * 0.03f)
        }
        2 -> { // Hat
            drawRect(Color(0xFF444444), Offset(cx - r * 1.2f, cy - r * 1.05f), Size(r * 2.4f, r * 0.12f))
            drawRect(Color(0xFF444444), Offset(cx - r * 0.6f, cy - r * 1.5f), Size(r * 1.2f, r * 0.5f))
        }
        3 -> { // Earrings (females)
            if (isFemale) {
                val eYOff = cy + r * 0.28f
                drawCircle(GoldPrimaryRaw, r * 0.07f, Offset(cx - r * 0.94f, eYOff))
                drawCircle(GoldPrimaryRaw, r * 0.07f, Offset(cx + r * 0.94f, eYOff))
                drawLine(GoldPrimaryRaw, Offset(cx - r * 0.94f, eYOff + r * 0.07f), Offset(cx - r * 0.94f, eYOff + r * 0.16f), strokeWidth = r * 0.025f)
                drawLine(GoldPrimaryRaw, Offset(cx + r * 0.94f, eYOff + r * 0.07f), Offset(cx + r * 0.94f, eYOff + r * 0.16f), strokeWidth = r * 0.025f)
            }
        }
        else -> {}
    }
}

private val GoldPrimaryRaw = Color(0xFFD4A24C)
