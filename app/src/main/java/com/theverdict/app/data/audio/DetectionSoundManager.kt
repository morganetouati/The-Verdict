package com.theverdict.app.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Generates a crisp high-quality "ping" sound when the player clicks a detection button.
 * Pure sine wave synthesis — no audio files required.
 * 880 Hz, 200 ms, exponential decay envelope (crystalline feel).
 */
object DetectionSoundManager {

    private const val SAMPLE_RATE = 44100
    private const val FREQUENCY = 880.0   // A5 — high, crystalline
    private const val DURATION_MS = 200
    private const val DECAY = 0.045       // Exponential decay constant

    fun playPing() {
        Thread {
            try {
                val numSamples = SAMPLE_RATE * DURATION_MS / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val envelope = exp(-t / DECAY)
                    val sample = envelope * sin(2.0 * PI * FREQUENCY * t)
                    buffer[i] = (sample * Short.MAX_VALUE * 0.85).toInt().toShort()
                }

                val minBufSize = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(SAMPLE_RATE)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(maxOf(minBufSize, buffer.size * 2))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                Thread.sleep(DURATION_MS.toLong() + 50)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
                // Silently ignore — never crash on sound failure
            }
        }.also { it.isDaemon = true }.start()
    }
}
