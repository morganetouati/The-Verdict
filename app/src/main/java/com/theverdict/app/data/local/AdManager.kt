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
    private var isLoading = false

    // Test ad unit ID — replace with real ID before release
    private val adUnitId = "ca-app-pub-3940256099942544/1033173712"

    init {
        loadAd()
    }

    private fun loadAd() {
        if (isLoading) return
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                isLoading = false
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
                isLoading = false
            }
        })
    }

    fun onCaseCompleted(activity: Activity, onComplete: () -> Unit = {}) {
        casesPlayedSinceLastAd++
        if (casesPlayedSinceLastAd >= 3) {
            casesPlayedSinceLastAd = 0
            showAd(activity, onComplete)
        } else {
            onComplete()
        }
    }

    private fun showAd(activity: Activity, onComplete: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            loadAd()
            onComplete()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadAd()
                onComplete()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                loadAd()
                onComplete()
            }
        }
        ad.show(activity)
    }
}
