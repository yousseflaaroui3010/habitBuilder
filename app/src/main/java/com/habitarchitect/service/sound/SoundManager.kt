package com.habitarchitect.service.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.habitarchitect.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages sound effects and haptic feedback for habit actions.
 */
@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var soundPool: SoundPool? = null
    private var successSoundId: Int = 0
    private var streakBreakSoundId: Int = 0
    private var isLoaded = false
    private var soundEnabled = true

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    init {
        initializeSoundPool()
    }

    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
            .apply {
                setOnLoadCompleteListener { _, _, status ->
                    if (status == 0) {
                        isLoaded = true
                    }
                }

                successSoundId = load(context, R.raw.success_ding, 1)
                streakBreakSoundId = load(context, R.raw.streak_break, 1)
            }
    }

    /**
     * Play success sound when habit is marked complete.
     */
    fun playSuccessSound() {
        if (soundEnabled && isLoaded && successSoundId != 0) {
            soundPool?.play(successSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
            vibrateSuccess()
        }
    }

    /**
     * Play streak break sound when habit is marked as failed.
     */
    fun playStreakBreakSound() {
        if (soundEnabled && isLoaded && streakBreakSoundId != 0) {
            soundPool?.play(streakBreakSoundId, 0.8f, 0.8f, 1, 0, 1.0f)
            vibrateFailure()
        }
    }

    /**
     * Play a milestone celebration sound (longer success).
     */
    fun playMilestoneSound() {
        if (soundEnabled && isLoaded && successSoundId != 0) {
            // Play success sound twice for milestone
            soundPool?.play(successSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
            vibrateMilestone()
        }
    }

    private fun vibrateSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    private fun vibrateFailure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 100, 50, 100), -1)
        }
    }

    private fun vibrateMilestone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 100, 100, 200), -1)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 100, 100, 100, 100, 200), -1)
        }
    }

    /**
     * Enable or disable sound effects.
     */
    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    /**
     * Release sound pool resources.
     */
    fun release() {
        soundPool?.release()
        soundPool = null
        isLoaded = false
    }
}
