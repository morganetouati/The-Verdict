package com.theverdict.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.theverdict.app.data.audio.AmbientMusicManager
import com.theverdict.app.data.local.DailyPlayManager
import com.theverdict.app.data.local.PreferencesManager
import com.theverdict.app.ui.navigation.VerdictNavGraph
import com.theverdict.app.ui.theme.NoirDeep
import com.theverdict.app.ui.theme.VerdictTheme

class MainActivity : ComponentActivity() {

    private lateinit var ambientMusic: AmbientMusicManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start ambient music from bundled MP4
        ambientMusic = AmbientMusicManager(this)
        ambientMusic.initialize()

        val app = application as TheVerdictApp
        val preferencesManager = PreferencesManager(this)
        val dailyPlayManager = DailyPlayManager(this)

        setContent {
            VerdictTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NoirDeep
                ) {
                    VerdictNavGraph(
                        preferencesManager = preferencesManager,
                        dailyPlayManager = dailyPlayManager,
                        adManager = app.adManager,
                        ambientMusic = ambientMusic
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ambientMusic.setForeground(true)
    }

    override fun onPause() {
        super.onPause()
        ambientMusic.setForeground(false)
    }

    override fun onDestroy() {
        ambientMusic.release()
        super.onDestroy()
    }
}
