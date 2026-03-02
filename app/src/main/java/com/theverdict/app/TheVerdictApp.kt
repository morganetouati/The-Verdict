package com.theverdict.app

import android.app.Application
import com.theverdict.app.data.ads.AdManager
import com.theverdict.app.data.crash.CrashReporter

class TheVerdictApp : Application() {

    lateinit var adManager: AdManager
        private set

    override fun onCreate() {
        super.onCreate()

        // Install crash reporter first
        CrashReporter(this).install()

        // Initialize ad manager (handles MobileAds.initialize internally)
        adManager = AdManager(this)
        adManager.initialize()
    }
}
