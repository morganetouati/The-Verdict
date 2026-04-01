package com.theverdict.app.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.staticCompositionLocalOf

val LocalHapticManager = staticCompositionLocalOf<HapticManager> {
    error("No HapticManager provided")
}

class HapticManager(context: Context) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    /** Light tap for button presses */
    fun lightTap() {
        vibrate(10L, VibrationEffect.DEFAULT_AMPLITUDE)
    }

    /** Medium feedback for verdict confirmation */
    fun medium() {
        vibrate(40L, 120)
    }

    /** Heavy impact for stamp landing / result reveal */
    fun heavyImpact() {
        vibrate(60L, 200)
    }

    /** Error pattern for wrong verdict */
    fun errorBuzz() {
        val timings = longArrayOf(0, 50, 60, 50)
        val amplitudes = intArrayOf(0, 150, 0, 100)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    }

    /** Success pattern for correct verdict */
    fun successPulse() {
        val timings = longArrayOf(0, 30, 50, 30)
        val amplitudes = intArrayOf(0, 80, 0, 120)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    }

    private fun vibrate(duration: Long, amplitude: Int) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
    }
}
