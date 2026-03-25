package com.theverdict.app.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.ui.components.RankBadge
import com.theverdict.app.ui.components.ReputationBar
import com.theverdict.app.ui.theme.*

@Composable
fun MainMenuScreen(
    playerRepository: PlayerRepository,
    caseRepository: CaseRepository,
    onPlay: (themeIndex: Int, caseIndex: Int) -> Unit,
    onReputation: () -> Unit,
    onTutorial: () -> Unit = {}
) {
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Title
        Text(
            text = "⚖️",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "THE VERDICT",
            style = MaterialTheme.typography.displayLarge,
            color = GoldPrimary
        )
        Text(
            text = "Rendez justice. Trouvez le menteur.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray
        )

        Spacer(Modifier.height(24.dp))

        // Rank + Reputation
        RankBadge(rank = profile.rank)
        Spacer(Modifier.height(12.dp))
        ReputationBar(
            reputation = profile.reputation,
            rank = profile.rank,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        // Play button
        Button(
            onClick = { onPlay(profile.currentThemeIndex, profile.currentCaseIndex) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = DarkBackground)
            Spacer(Modifier.width(8.dp))
            Text("JOUER", style = MaterialTheme.typography.titleLarge, color = DarkBackground)
        }

        Spacer(Modifier.height(12.dp))

        // Reputation button
        Button(
            onClick = onReputation,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("RÉPUTATION", style = MaterialTheme.typography.titleMedium, color = TextWhite)
        }

        Spacer(Modifier.height(8.dp))

        // Tutorial button
        Button(
            onClick = onTutorial,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("MODE D'EMPLOI", style = MaterialTheme.typography.titleMedium, color = TextWhite)
        }

        Spacer(Modifier.height(24.dp))

        // Theme selection
        Text(
            text = "Thèmes",
            style = MaterialTheme.typography.headlineMedium,
            color = TextWhite,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(CaseTheme.entries.toList()) { index, theme ->
                val isUnlocked = playerRepository.isThemeUnlocked(theme, profile)
                val progress = profile.themeProgress[index] ?: 0

                ThemeCard(
                    theme = theme,
                    isUnlocked = isUnlocked,
                    progress = progress,
                    onClick = {
                        if (isUnlocked) onPlay(index, 0)
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: CaseTheme,
    isUnlocked: Boolean,
    progress: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isUnlocked) DarkCard else DarkSurfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = theme.emoji,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isUnlocked) TextWhite else TextDimmed
                )
                Text(
                    text = if (isUnlocked) "$progress/10 affaires" else "🔒 ${theme.casesRequiredToUnlock} affaires + ${theme.reputationRequiredToUnlock} réputation",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) TextGray else TextDimmed
                )
            }
            if (!isUnlocked) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = TextDimmed)
            }
        }
    }
}
