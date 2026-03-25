package com.theverdict.app.data.local

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdManager(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private var casesPlayedSinceLastAd = 0

    // Test ad unit ID — replace with real ID before release
    private val adUnitId = "ca-app-pub-3940256099942544/1033173712"

    init {
        loadAd()
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
            }
        })
    }

    fun onCaseCompleted(activity: Activity) {
        casesPlayedSinceLastAd++
        if (casesPlayedSinceLastAd >= 3) {
            showAd(activity)
            casesPlayedSinceLastAd = 0
        }
    }

    private fun showAd(activity: Activity) {
        val ad = interstitialAd ?: return
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadAd()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                loadAd()
            }
        }
        ad.show(activity)
    }
}
