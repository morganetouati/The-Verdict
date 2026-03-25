package com.theverdict.app.ui.screens.reputation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theverdict.app.data.repository.PlayerRepository
import com.theverdict.app.domain.model.PlayerProfile
import com.theverdict.app.ui.components.RankBadge
import com.theverdict.app.ui.components.ReputationBar
import com.theverdict.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReputationScreen(
    playerRepository: PlayerRepository,
    onBack: () -> Unit
) {
    val profile by playerRepository.profile.collectAsState(initial = PlayerProfile())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Réputation", color = GoldPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            RankBadge(rank = profile.rank)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "${profile.reputation}/100",
                style = MaterialTheme.typography.displaySmall,
                color = GoldPrimary
            )

            Spacer(Modifier.height(12.dp))

            ReputationBar(
                reputation = profile.reputation,
                rank = profile.rank,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(40.dp))

            // Stats cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Statistiques",
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldPrimary
                    )
                    Spacer(Modifier.height(16.dp))

                    StatRow("Affaires traitées", "${profile.casesPlayed}")
                    StatRow("Verdicts corrects", "${profile.correctVerdicts}")
                    StatRow("Verdicts erronés", "${profile.wrongVerdicts}")
                    StatRow("Taux de réussite", "${profile.successRate}%")
                    StatRow("Progression", "${profile.completedCaseIds.size}/80")
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = TextGray)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = TextWhite)
    }
}
