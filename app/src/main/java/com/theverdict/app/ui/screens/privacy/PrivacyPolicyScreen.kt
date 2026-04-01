package com.theverdict.app.ui.screens.privacy

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.theverdict.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dim = LocalDimensions.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkBackground)
                )
            )
    ) {
        TopAppBar(
            title = { Text("Mentions Légales", color = GoldPrimary) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = TextWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(dim.paddingLarge)
        ) {
            // App Privacy Policy
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkCard
            ) {
                Column(modifier = Modifier.padding(dim.paddingMedium)) {
                    Text(
                        text = "Politique de Confidentialité",
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldPrimary
                    )
                    Spacer(Modifier.height(dim.paddingSmall))
                    Text(
                        text = "The Verdict respecte votre vie privée. Cette application ne collecte aucune donnée personnelle identifiable. " +
                                "Toutes les données de jeu (progression, pseudo, préférences) sont stockées uniquement sur votre appareil.\n\n" +
                                "L'application utilise Google AdMob pour afficher des publicités. " +
                                "AdMob peut utiliser des identifiants publicitaires et des données d'utilisation conformément à sa propre politique de confidentialité.\n\n" +
                                "Aucune donnée n'est partagée avec des tiers en dehors du cadre publicitaire d'AdMob.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
            }

            Spacer(Modifier.height(dim.paddingMedium))

            // AdMob Privacy Policy Link
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkCard
            ) {
                Column(modifier = Modifier.padding(dim.paddingMedium)) {
                    Text(
                        text = "Publicités (Google AdMob)",
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldPrimary
                    )
                    Spacer(Modifier.height(dim.paddingSmall))
                    Text(
                        text = "Les publicités sont fournies par Google AdMob. Pour en savoir plus sur la façon dont Google utilise vos données :",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                    Spacer(Modifier.height(dim.paddingSmall))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/privacy"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Politique de confidentialité Google", color = TextWhite)
                    }
                }
            }

            Spacer(Modifier.height(dim.paddingMedium))

            // Contact / App info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkCard
            ) {
                Column(modifier = Modifier.padding(dim.paddingMedium)) {
                    Text(
                        text = "À propos",
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldPrimary
                    )
                    Spacer(Modifier.height(dim.paddingSmall))
                    Text(
                        text = "The Verdict v1.0.0\n© 2025 The Verdict\nTous droits réservés.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
            }

            Spacer(Modifier.height(dim.paddingLarge))
        }
    }
}
