package com.theverdict.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = rank.emoji, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(6.dp))
            Text(
                text = rank.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = color
            )
        }
    }
}
