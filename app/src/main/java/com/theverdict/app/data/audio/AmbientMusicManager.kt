package com.theverdict.app.data.audio

import android.content.Context
import android.media.MediaPlayer
import com.theverdict.app.R

/**
 * Plays ambient music from a bundled MP4 resource file (res/raw/ambient_music.mp4).
 * Loops automatically, pauses during video playback and when app is backgrounded.
 *
 * Usage:
 *   manager.initialize()              → starts looped playback
 *   manager.setVideoPlaying(true)     → pauses for video
 *   manager.setVideoPlaying(false)    → resumes after video
 *   manager.setForeground(false)      → pauses when app goes to background
 *   manager.release()                 → cleanup
 */
class AmbientMusicManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    @Volatile
    private var isInitialized = false

    @Volatile
    private var shouldPlay = true

    @Volatile
    private var isInForeground = true

    fun initialize() {
        if (isInitialized) return
        try {
            val mp = MediaPlayer.create(context, R.raw.ambient_music)
            mp?.let {
                it.isLooping = true
                it.setVolume(VOLUME, VOLUME)
                mediaPlayer = it
                isInitialized = true
                if (shouldPlay && isInForeground) {
                    it.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVideoPlaying(videoPlaying: Boolean) {
        shouldPlay = !videoPlaying
        updatePlayState()
    }

    fun setForeground(foreground: Boolean) {
        isInForeground = foreground
        updatePlayState()
    }

    private fun updatePlayState() {
        val mp = mediaPlayer ?: return
        try {
            if (shouldPlay && isInForeground) {
                if (!mp.isPlaying) {
                    mp.start()
                }
            } else {
                if (mp.isPlaying) {
                    mp.pause()
                }
            }
        } catch (_: IllegalStateException) {
            // MediaPlayer already released
        }
    }

    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
        isInitialized = false
    }

    companion object {
        private const val VOLUME = 0.15f // Subtle background presence
    }
}
