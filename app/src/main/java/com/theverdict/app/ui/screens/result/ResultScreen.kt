package com.theverdict.app.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.theverdict.app.ui.components.VerdictStamp
import com.theverdict.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    caseRepository: CaseRepository,
    playerRepository: PlayerRepository,
    themeIndex: Int,
    caseIndex: Int,
    isCorrect: Boolean,
    pointsChange: Int,
    onNextCase: (themeIndex: Int, caseIndex: Int) -> Unit,
    onGameOver: () -> Unit,
    onVictory: () -> Unit,
    onMenu: () -> Unit
) {
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())
    val scope = rememberCoroutineScope()
    val theme = CaseTheme.entries[themeIndex]
    val case = caseRepository.getCase(theme, caseIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        // Verdict stamp animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            VerdictStamp(isCorrect = isCorrect)
        }

        Spacer(Modifier.height(16.dp))

        // Points change
        Text(
            text = if (pointsChange >= 0) "+$pointsChange" else "$pointsChange",
            style = MaterialTheme.typography.displayMedium,
            color = if (isCorrect) VerdictCorrect else VerdictWrong
        )
        Text(
            text = "points de réputation",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray
        )

        Spacer(Modifier.height(24.dp))

        // Explanation
        if (case != null) {
            val coupableNames = case.suspects
                .filter { it.id in case.coupableIds }
                .joinToString(", ") { it.nom }

            val explanation = when {
                case.coupableIds.isEmpty() -> "Personne ne mentait dans cette affaire."
                case.coupableIds.size == case.suspects.size -> "Tous les suspects mentaient !"
                case.coupableIds.size > 1 -> "Les coupables étaient : $coupableNames"
                else -> "Le coupable était : $coupableNames"
            }

            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyLarge,
                color = TextWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(32.dp))

        // Current reputation
        RankBadge(rank = profile.rank)
        Spacer(Modifier.height(12.dp))
        ReputationBar(
            reputation = profile.reputation,
            rank = profile.rank,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        // Actions
        when {
            profile.reputation <= 0 -> {
                Button(
                    onClick = onGameOver,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdictWrong),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("GAME OVER", style = MaterialTheme.typography.titleLarge, color = TextWhite)
                }
            }
            profile.allCasesCompleted -> {
                Button(
                    onClick = onVictory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RankLegende),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("VICTOIRE !", style = MaterialTheme.typography.titleLarge, color = DarkBackground)
                }
            }
            else -> {
                Button(
                    onClick = {
                        scope.launch {
                            val updated = playerRepository.advanceToNextCase(profile)
                            onNextCase(updated.currentThemeIndex, updated.currentCaseIndex)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("AFFAIRE SUIVANTE", style = MaterialTheme.typography.titleLarge, color = DarkBackground)
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("MENU", style = MaterialTheme.typography.titleMedium, color = TextWhite)
                }
            }
        }
    }
}
