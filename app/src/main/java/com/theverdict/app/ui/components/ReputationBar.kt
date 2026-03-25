package com.theverdict.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Brush
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
        animationSpec = tween(durationMillis = 800),
        label = "reputation"
    )

    LaunchedEffect(reputation) {
        animatedProgress = reputation / 100f
    }

    val barColor = when (rank) {
        Rank.DEBUTANT -> Brush.horizontalGradient(listOf(RankDebutant, RankDebutant))
        Rank.JUGE -> Brush.horizontalGradient(listOf(RankJuge, RankJuge))
        Rank.BON_JUGE -> Brush.horizontalGradient(listOf(RankBonJuge, RankBonJuge))
        Rank.EXPERT -> Brush.horizontalGradient(listOf(RankExpert, RankExpert))
        Rank.LEGENDE -> Brush.horizontalGradient(listOf(GoldPrimary, RankLegende))
    }

    Column(modifier = modifier) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Réputation",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$reputation/100",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextWhite
                )
            }
            Spacer(Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(DarkSurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedValue)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(barColor)
            )
        }
    }
}
