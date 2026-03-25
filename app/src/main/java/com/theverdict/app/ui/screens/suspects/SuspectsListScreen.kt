package com.theverdict.app.ui.screens.suspects

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theverdict.app.data.repository.CaseRepository
import com.theverdict.app.domain.model.CaseTheme
import com.theverdict.app.domain.model.Suspect
import com.theverdict.app.ui.components.SuspectAvatar
import com.theverdict.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuspectsListScreen(
    caseRepository: CaseRepository,
    themeIndex: Int,
    caseIndex: Int,
    onInterrogate: (suspectId: Int) -> Unit,
    onGoToVerdict: () -> Unit,
    onBack: () -> Unit
) {
    val theme = CaseTheme.entries[themeIndex]
    val case = caseRepository.getCase(theme, caseIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = { Text("Les Suspects") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = TextWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DarkBackground,
                titleContentColor = TextWhite
            )
        )

        if (case != null) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(case.suspects) { suspect ->
                    SuspectCard(
                        suspect = suspect,
                        onClick = { onInterrogate(suspect.id) }
                    )
                }
            }

            // Go to verdict button
            Button(
                onClick = onGoToVerdict,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Gavel, contentDescription = null, tint = DarkBackground)
                Spacer(Modifier.width(8.dp))
                Text("RENDRE LE VERDICT", style = MaterialTheme.typography.titleLarge, color = DarkBackground)
            }
        }
    }
}

@Composable
private fun SuspectCard(
    suspect: Suspect,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = DarkCard
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SuspectAvatar(
                config = suspect.avatar,
                clues = suspect.indices,
                size = 64.dp
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = suspect.nom,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Appuyer pour interroger",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
        }
    }
}
