package com.theverdict.app.ui.screens.gameover

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun GameOverScreen(
    playerRepository: PlayerRepository,
    onRestart: () -> Unit
) {
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())
    val scope = rememberCoroutineScope()
    val redGlow = VerdictWrong.copy(alpha = 0.25f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, Color(0xFF1A0A0A), Color(0xFF120808), DarkBackground)
                )
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Canvas-drawn gavel icon with red glow
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(redGlow, Color.Transparent),
                            center = center,
                            radius = size.minDimension * 0.8f
                        )
                    )
                }
        ) {
            Canvas(modifier = Modifier.size(64.dp)) {
                val w = size.width
                val h = size.height
                val red = Color(0xFFE53935)
                val redL = Color(0xFFEF5350)
                val sw = w * 0.06f
                // Gavel handle
                drawLine(red, Offset(w * 0.25f, h * 0.75f), Offset(w * 0.65f, h * 0.35f), sw, StrokeCap.Round)
                // Gavel head
                drawLine(redL, Offset(w * 0.50f, h * 0.18f), Offset(w * 0.82f, h * 0.50f), sw * 2.5f, StrokeCap.Round)
                // Sound block
                drawRoundRect(red, Offset(w * 0.08f, h * 0.82f), Size(w * 0.45f, h * 0.12f), CornerRadius(w * 0.03f))
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "RENVOYÉ !",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                shadow = Shadow(
                    color = VerdictWrong.copy(alpha = 0.5f),
                    offset = Offset(0f, 4f),
                    blurRadius = 16f
                )
            ),
            color = VerdictWrong,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Votre réputation a atteint zéro.\nLe tribunal vous a relevé de vos fonctions.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Affaires traitées : ${profile.casesPlayed}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TextWhite
        )
        Text(
            text = "Verdicts corrects : ${profile.correctVerdicts}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TextWhite
        )

        Spacer(Modifier.height(48.dp))

        // Gold gradient restart button with press animation
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.96f else 1f,
            animationSpec = tween(100), label = "goBtn"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0x40D4A24C), Color.Transparent)),
                        cornerRadius = CornerRadius(24.dp.toPx()),
                        topLeft = Offset(0f, 4.dp.toPx()),
                        size = Size(size.width, size.height + 6.dp.toPx())
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.horizontalGradient(listOf(GoldDark, GoldPrimary, GoldLight)))
                    .clickable(interactionSource = interactionSource, indication = null) {
                        scope.launch {
                            playerRepository.resetAll()
                            onRestart()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "RECOMMENCER",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                    color = DarkBackground
                )
            }
        }
    }
}
