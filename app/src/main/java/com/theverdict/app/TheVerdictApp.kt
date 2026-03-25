package com.theverdict.app

import android.app.Application
import com.google.android.gms.ads.MobileAds

class TheVerdictApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}
