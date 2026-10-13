package com.tictactoemaster.platform

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

enum class Sfx { PLACE_X, PLACE_O, WIN, DRAW, LOSE }

/**
 * Plays short sound effects. The sounds are synthesised in code, so the app ships no audio files.
 * Nothing is played when sound is switched off in settings or when the phone is on silent/vibrate.
 */
class SoundManager(context: Context) {

    private val audioManager =
        context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val handler = Handler(Looper.getMainLooper())
    private val activeTracks = mutableSetOf<AudioTrack>()

    private val samples: Map<Sfx, ShortArray> = mapOf(
        Sfx.PLACE_X to tone(660.0, 90),
        Sfx.PLACE_O to tone(495.0, 90),
        Sfx.WIN to concat(
            tone(523.25, 110), tone(659.25, 110), tone(783.99, 110), tone(1046.5, 320)
        ),
        Sfx.DRAW to concat(tone(392.0, 140), tone(329.63, 260)),
        Sfx.LOSE to concat(tone(329.63, 160), tone(261.63, 160), tone(196.0, 320))
    )

    fun play(sfx: Sfx, enabled: Boolean) {
        if (!enabled) return
        // Respect the phone's silent / vibrate mode.
        if (audioManager?.ringerMode != AudioManager.RINGER_MODE_NORMAL) return
        val data = samples[sfx] ?: return
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(data.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            if (track.state != AudioTrack.STATE_INITIALIZED) {
                track.release()
                return
            }
            track.write(data, 0, data.size)
            track.play()
            activeTracks.add(track)
            val lifetimeMs = data.size * 1000L / SAMPLE_RATE + 250L
            handler.postDelayed({
                activeTracks.remove(track)
                try {
                    track.release()
                } catch (e: Exception) {
                    // Already released: nothing to do.
                }
            }, lifetimeMs)
        } catch (e: Exception) {
            // Sound is a nice-to-have: never let an audio problem crash the game.
        }
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        for (track in activeTracks) {
            try {
                track.release()
            } catch (e: Exception) {
                // Ignore.
            }
        }
        activeTracks.clear()
    }

    private companion object {
        const val SAMPLE_RATE = 22050

        fun tone(frequency: Double, millis: Int, volume: Double = 0.5): ShortArray {
            val count = SAMPLE_RATE * millis / 1000
            val out = ShortArray(count)
            val attack = (SAMPLE_RATE * 0.008).toInt().coerceAtLeast(1)
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val fadeIn = minOf(1.0, i / attack.toDouble())
                val fadeOut = (1.0 - i.toDouble() / count).pow(1.5)
                val wave = sin(2 * PI * frequency * t) + 0.3 * sin(4 * PI * frequency * t)
                val sample = wave / 1.3 * fadeIn * fadeOut * volume
                out[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            return out
        }

        fun concat(vararg parts: ShortArray): ShortArray =
            parts.fold(ShortArray(0)) { acc, next -> acc + next }
    }
}
