package com.theverdict.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theverdict.app.domain.model.Rank
import com.theverdict.app.ui.theme.*

@Composable
fun RankBadge(
    rank: Rank,
    modifier: Modifier = Modifier
) {
    val color = when (rank) {
        Rank.DEBUTANT -> RankDebutant
        Rank.JUGE -> RankJuge
        Rank.BON_JUGE -> RankBonJuge
        Rank.EXPERT -> RankExpert
        Rank.LEGENDE -> RankLegende
    }

    Surface(
        modifier = modifier
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.18f), Color.Transparent),
                        center = center,
                        radius = size.maxDimension * 0.6f
                    )
                )
            }
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(color.copy(alpha = 0.4f), color.copy(alpha = 0.15f))),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.10f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini judge gavel icon
            Canvas(modifier = Modifier.size(22.dp)) {
                val w = size.width
                val h = size.height
                val c = color
                // Handle
                drawLine(c, Offset(w * 0.5f, h * 0.2f), Offset(w * 0.5f, h * 0.75f), w * 0.08f, StrokeCap.Round)
                // Head
                drawRoundRect(c, Offset(w * 0.2f, h * 0.05f), Size(w * 0.6f, h * 0.25f), androidx.compose.ui.geometry.CornerRadius(w * 0.06f))
                // Base
                drawLine(c, Offset(w * 0.25f, h * 0.82f), Offset(w * 0.75f, h * 0.82f), w * 0.09f, StrokeCap.Round)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = rank.displayName,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}
