package com.theverdict.app.ui.screens.victory

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VictoryScreen(
    playerRepository: PlayerRepository,
    onMenu: () -> Unit,
    onInfiniteMode: (themeIndex: Int, caseIndex: Int) -> Unit
) {
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())
    val scope = rememberCoroutineScope()
    val goldGlow = RankLegende.copy(alpha = 0.25f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, Color(0xFF1A1500), Color(0xFF141008), DarkBackground)
                )
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Canvas-drawn trophy icon with golden glow
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(goldGlow, Color.Transparent),
                            center = center,
                            radius = size.minDimension * 0.85f
                        )
                    )
                }
        ) {
            Canvas(modifier = Modifier.size(72.dp)) {
                val w = size.width
                val h = size.height
                val gold = Color(0xFFFFD700)
                val goldD = Color(0xFFD4A24C)
                val sw = w * 0.05f
                // Cup bowl
                drawArc(gold, 0f, 180f, false, Offset(w * 0.18f, h * 0.08f), Size(w * 0.64f, h * 0.50f), style = androidx.compose.ui.graphics.drawscope.Stroke(sw * 1.5f))
                // Left handle
                drawArc(goldD, 90f, 180f, false, Offset(w * 0.04f, h * 0.15f), Size(w * 0.22f, h * 0.30f), style = androidx.compose.ui.graphics.drawscope.Stroke(sw))
                // Right handle
                drawArc(goldD, -90f, 180f, false, Offset(w * 0.74f, h * 0.15f), Size(w * 0.22f, h * 0.30f), style = androidx.compose.ui.graphics.drawscope.Stroke(sw))
                // Stem
                drawLine(gold, Offset(w * 0.5f, h * 0.55f), Offset(w * 0.5f, h * 0.72f), sw * 1.2f, StrokeCap.Round)
                // Base
                drawRoundRect(goldD, Offset(w * 0.28f, h * 0.72f), Size(w * 0.44f, h * 0.08f), CornerRadius(w * 0.02f))
                drawRoundRect(gold, Offset(w * 0.22f, h * 0.80f), Size(w * 0.56f, h * 0.10f), CornerRadius(w * 0.03f))
                // Star in cup
                val cx = w * 0.5f; val cy = h * 0.28f; val r = w * 0.08f
                drawCircle(gold, r, Offset(cx, cy))
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "JUGE LÉGENDAIRE !",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                shadow = Shadow(
                    color = RankLegende.copy(alpha = 0.5f),
                    offset = Offset(0f, 4f),
                    blurRadius = 16f
                )
            ),
            color = RankLegende,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Vous avez résolu les 80 affaires.\nVotre sagesse est inégalée !",
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Réputation finale : ${profile.reputation}/100",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                shadow = Shadow(color = GoldPrimary.copy(alpha = 0.4f), offset = Offset(0f, 2f), blurRadius = 8f)
            ),
            color = GoldPrimary
        )
        Text(
            text = "Taux de réussite : ${profile.successRate}%",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TextWhite
        )

        Spacer(Modifier.height(48.dp))

        // MODE INFINI — gold gradient button
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.96f else 1f,
            animationSpec = tween(100), label = "vicBtn"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0x40FFD700), Color.Transparent)),
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
                    .background(Brush.horizontalGradient(listOf(GoldDark, RankLegende, GoldLight)))
                    .clickable(interactionSource = interactionSource, indication = null) {
                        val randomTheme = (0 until CaseTheme.entries.size).random()
                        onInfiniteMode(randomTheme, 0)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "MODE INFINI ♾️",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                    color = DarkBackground
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // MENU — secondary styled button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
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
