package com.tictactoemaster.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Short vibration feedback, only when vibration is enabled in settings. */
@Suppress("DEPRECATION")
class Haptics(context: Context) {

    private val vibrator: Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                ?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    fun tap(enabled: Boolean) {
        vibrate(enabled, VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun win(enabled: Boolean) {
        vibrate(enabled, VibrationEffect.createWaveform(longArrayOf(0, 60, 60, 60, 60, 160), -1))
    }

    fun draw(enabled: Boolean) {
        vibrate(enabled, VibrationEffect.createOneShot(90, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun vibrate(enabled: Boolean, effect: VibrationEffect) {
        if (!enabled) return
        try {
            val v = vibrator ?: return
            if (v.hasVibrator()) v.vibrate(effect)
        } catch (e: Exception) {
            // Vibration is optional.
        }
    }
}
