package com.theverdict.app.data.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages AdMob rewarded ads for The Verdict.
 * Two ad placements:
 * 1. "Unlock +5 analyses" when daily limit reached
 * 2. "Double XP" after verdict
 */
class AdManager(private val context: Context) {

    companion object {
        // Test ad unit IDs (replace with real ones for production)
        private const val AD_UNIT_UNLOCK = "ca-app-pub-3940256099942544/5224354917"
        private const val AD_UNIT_DOUBLE_XP = "ca-app-pub-3940256099942544/5224354917"
    }

    private var unlockAd: RewardedAd? = null
    private var doubleXpAd: RewardedAd? = null

    private val _isUnlockAdReady = MutableStateFlow(false)
    val isUnlockAdReady: StateFlow<Boolean> = _isUnlockAdReady

    private val _isDoubleXpAdReady = MutableStateFlow(false)
    val isDoubleXpAdReady: StateFlow<Boolean> = _isDoubleXpAdReady

    fun initialize() {
        MobileAds.initialize(context) {
            preloadUnlockAd()
            preloadDoubleXpAd()
        }
    }

    fun preloadUnlockAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, AD_UNIT_UNLOCK, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                unlockAd = ad
                _isUnlockAdReady.value = true
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                unlockAd = null
                _isUnlockAdReady.value = false
            }
        })
    }

    fun preloadDoubleXpAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, AD_UNIT_DOUBLE_XP, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                doubleXpAd = ad
                _isDoubleXpAdReady.value = true
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                doubleXpAd = null
                _isDoubleXpAdReady.value = false
            }
        })
    }

    fun showUnlockAd(activity: Activity, onRewarded: () -> Unit, onDismissed: () -> Unit = {}) {
        val ad = unlockAd ?: return
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                unlockAd = null
                _isUnlockAdReady.value = false
                preloadUnlockAd()
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                unlockAd = null
                _isUnlockAdReady.value = false
                preloadUnlockAd()
            }
        }
        ad.show(activity) { onRewarded() }
    }

    fun showDoubleXpAd(activity: Activity, onRewarded: () -> Unit, onDismissed: () -> Unit = {}) {
        val ad = doubleXpAd ?: return
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                doubleXpAd = null
                _isDoubleXpAdReady.value = false
                preloadDoubleXpAd()
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                doubleXpAd = null
                _isDoubleXpAdReady.value = false
                preloadDoubleXpAd()
            }
        }
        ad.show(activity) { onRewarded() }
    }
}
