package com.theverdict.app.ui.screens.victory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🏆",
            fontSize = 80.sp
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "JUGE LÉGENDAIRE !",
            style = MaterialTheme.typography.displayMedium,
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
            style = MaterialTheme.typography.titleLarge,
            color = GoldPrimary
        )
        Text(
            text = "Taux de réussite : ${profile.successRate}%",
            style = MaterialTheme.typography.bodyLarge,
            color = TextWhite
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = {
                // Start infinite mode from a random theme/case
                val randomTheme = (0 until CaseTheme.entries.size).random()
                onInfiniteMode(randomTheme, 0)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RankLegende),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("MODE INFINI ♾️", style = MaterialTheme.typography.titleLarge, color = DarkBackground)
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
