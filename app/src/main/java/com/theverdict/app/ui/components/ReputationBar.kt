package com.theverdict.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theverdict.app.domain.model.Rank
import com.theverdict.app.ui.theme.*

@Composable
fun ReputationBar(
    reputation: Int,
    rank: Rank,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val animatedValue by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "reputation"
    )

    LaunchedEffect(reputation) {
        animatedProgress = reputation / 100f
    }

    // Shimmer animation for the bar
    val inf = rememberInfiniteTransition(label = "barShine")
    val shimmerX by inf.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val rankColor = when (rank) {
        Rank.DEBUTANT -> RankDebutant
        Rank.JUGE -> RankJuge
        Rank.BON_JUGE -> RankBonJuge
        Rank.EXPERT -> RankExpert
        Rank.LEGENDE -> RankLegende
    }

    // Golden gradient for bar, tinted by rank
    val barColor = Brush.horizontalGradient(
        listOf(GoldDark, rankColor, GoldPrimary, GoldLight)
    )

    Column(modifier = modifier) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\u2696\uFE0F Réputation",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = GoldLight
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$reputation / 100",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = rankColor
                )
            }
            Spacer(Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkSurfaceVariant)
        ) {
            // Filled bar with golden gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedValue)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(barColor)
                    .drawWithContent {
                        drawContent()
                        // Animated shine highlight
                        val shineW = size.width * 0.25f
                        val shineX = shimmerX * size.width
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.35f), Color.Transparent),
                                startX = shineX - shineW,
                                endX = shineX + shineW
                            )
                        )
                    }
            )
        }
    }
}
