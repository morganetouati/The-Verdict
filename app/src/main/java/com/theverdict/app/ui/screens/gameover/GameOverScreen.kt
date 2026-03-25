package com.theverdict.app.ui.screens.gameover

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚖️",
            fontSize = 80.sp
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "RENVOYÉ !",
            style = MaterialTheme.typography.displayLarge,
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
            style = MaterialTheme.typography.bodyLarge,
            color = TextWhite
        )
        Text(
            text = "Verdicts corrects : ${profile.correctVerdicts}",
            style = MaterialTheme.typography.bodyLarge,
            color = TextWhite
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = {
                scope.launch {
                    playerRepository.resetAll()
                    onRestart()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("RECOMMENCER", style = MaterialTheme.typography.titleLarge, color = DarkBackground)
        }
    }
}
