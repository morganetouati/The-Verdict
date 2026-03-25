package com.theverdict.app.data.local

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundManager(context: Context) {

    private val soundPool: SoundPool
    private val sounds = mutableMapOf<SoundType, Int>()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sounds from raw resources if they exist
        loadSoundSafe(context, SoundType.CORRECT, "sound_correct")
        loadSoundSafe(context, SoundType.WRONG, "sound_wrong")
        loadSoundSafe(context, SoundType.STAMP, "sound_stamp")
        loadSoundSafe(context, SoundType.CLICK, "sound_click")
        loadSoundSafe(context, SoundType.TIMER_TICK, "sound_tick")
    }

    private fun loadSoundSafe(context: Context, type: SoundType, resName: String) {
        val resId = context.resources.getIdentifier(resName, "raw", context.packageName)
        if (resId != 0) {
            sounds[type] = soundPool.load(context, resId, 1)
        }
    }

    fun play(type: SoundType) {
        val soundId = sounds[type] ?: return
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }

    enum class SoundType {
        CORRECT, WRONG, STAMP, CLICK, TIMER_TICK
    }
}
