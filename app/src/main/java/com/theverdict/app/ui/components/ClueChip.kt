package com.theverdict.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.theverdict.app.domain.model.Clue
import com.theverdict.app.ui.theme.ClueChipBackground
import com.theverdict.app.ui.theme.ClueChipText
import com.theverdict.app.ui.theme.GoldPrimary

@Composable
fun ClueChip(
    clue: Clue,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = ClueChipBackground
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = clue.toIcon(),
                contentDescription = clue.label,
                modifier = Modifier.size(18.dp),
                tint = GoldPrimary
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = clue.label,
                style = MaterialTheme.typography.labelMedium,
                color = ClueChipText
            )
        }
    }
}

fun Clue.toIcon(): ImageVector = when (this) {
    Clue.REGARDE_AILLEURS -> Icons.Outlined.VisibilityOff
    Clue.TRANSPIRE -> Icons.Outlined.WaterDrop
    Clue.HESITE -> Icons.Outlined.MoreHoriz
    Clue.TROP_CALME -> Icons.Outlined.SelfImprovement
    Clue.SE_CONTREDIT -> Icons.Outlined.SyncProblem
    Clue.PARLE_VITE -> Icons.Outlined.Speed
    Clue.NERVEUX -> Icons.Outlined.Bolt
    Clue.CONFIANT -> Icons.Outlined.SentimentSatisfied
    Clue.EVITE_REGARD -> Icons.Outlined.PersonOff
    Clue.MAINS_TREMBLENT -> Icons.Outlined.Person
    Clue.SOURIT_TROP -> Icons.Outlined.Mood
    Clue.VOIX_CHANGE -> Icons.Outlined.RecordVoiceOver
    Clue.BRAS_CROISES -> Icons.Outlined.Person
    Clue.REPOND_VITE -> Icons.Outlined.Bolt
    Clue.DETAIL_SUSPECT -> Icons.Outlined.Search
    Clue.HISTOIRE_FLOUE -> Icons.Outlined.MoreHoriz
}
