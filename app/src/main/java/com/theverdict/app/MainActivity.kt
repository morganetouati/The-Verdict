package com.theverdict.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.theverdict.app.ui.navigation.NavGraph
import com.theverdict.app.ui.theme.TheVerdictTheme
import com.theverdict.app.ui.util.HapticManager
import com.theverdict.app.ui.util.LocalHapticManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val hapticManager = remember { HapticManager(this@MainActivity) }
            CompositionLocalProvider(LocalHapticManager provides hapticManager) {
                TheVerdictTheme {
                    NavGraph()
                }
            }
        }
    }
}
