package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Tactile Terminal Sound & Haptic FX Engine.
 * Synthesizes retro cyber terminal sounds on-the-fly without external audio assets.
 */
open class TerminalSoundManager(protected val context: Context) {

    protected val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    protected val coroutineScope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    protected val sampleRate = 22050

    // Pre-synthesized PCM audio buffers for instantaneous playback
    private val keyClickBuffer: ByteArray by lazy { generateKeyClick() }
    private val beepBuffer: ByteArray by lazy { generateTone(1200.0, 50, 0.4) }
    private val accessGrantedBuffer: ByteArray by lazy { generateAccessGranted() }
    private val accessDeniedBuffer: ByteArray by lazy { generateAccessDenied() }
    private val sonarPingBuffer: ByteArray by lazy { generateSonarPing() }
    private val dataStreamBuffer: ByteArray by lazy { generateDataStream() }
    private val blackoutBuffer: ByteArray by lazy { generateBlackout() }
    private val phoneRingBuffer: ByteArray by lazy { generatePhoneRing() }
    private val radioStaticBuffer: ByteArray by lazy { generateRadioStatic() }
    private val bootBeepBuffer: ByteArray by lazy { generateBootBeep() }
    private val tileMoveChirpBuffer: ByteArray by lazy { generateTileMoveChirp() }
    private val letterTickBuffer: ByteArray by lazy { generateLetterTick() }
    private val hackStartBuffer: ByteArray by lazy { generateHackStart() }
    private val hackSuccessBuffer: ByteArray by lazy { generateHackSuccess() }
    private val hackFailBuffer: ByteArray by lazy { generateHackFail() }

    open fun playHackStartSound() {
        if (!isSoundEnabled) return
        playPcmAsync(hackStartBuffer)
        vibrateMedium()
    }

    open fun playHackSuccessSound() {
        if (!isSoundEnabled) return
        playPcmAsync(hackSuccessBuffer)
        vibrateDouble()
    }

    open fun playHackFailSound() {
        if (!isSoundEnabled) return
        playPcmAsync(hackFailBuffer)
        vibrateHeavy()
    }

    open fun playHackStart() = playHackStartSound()
    open fun playHackSuccess() = playHackSuccessSound()
    open fun playHackFailed() = playHackFailSound()

    open fun playTileMoveSound() {
        if (!isSoundEnabled) return
        playPcmAsync(tileMoveChirpBuffer)
        vibrateLight()
    }

    open fun playLetterLoadSound() {
        if (!isSoundEnabled) return
        playPcmAsync(letterTickBuffer)
    }

    open fun playPingPulse() {
        if (!isSoundEnabled) return
        playPcmAsync(sonarPingBuffer)
        vibrateLight()
    }

    open fun playPhoneRing() {
        if (!isSoundEnabled) return
        playPcmAsync(phoneRingBuffer)
        vibrateDouble()
    }

    open fun playRadioStatic() {
        if (!isSoundEnabled) return
        playPcmAsync(radioStaticBuffer)
        vibrateMedium()
    }

    open fun playKeyClick() {
        if (!isSoundEnabled) return
        playPcmAsync(keyClickBuffer)
        vibrateLight()
    }

    open fun playTerminalBeep() {
        if (!isSoundEnabled) return
        playPcmAsync(beepBuffer)
        vibrateLight()
    }

    open fun playAccessGranted() {
        if (!isSoundEnabled) return
        playPcmAsync(accessGrantedBuffer)
        vibrateDouble()
    }

    open fun playAccessDenied() {
        if (!isSoundEnabled) return
        playPcmAsync(accessDeniedBuffer)
        vibrateHeavy()
    }

    open fun playSonarPing() {
        if (!isSoundEnabled) return
        playPcmAsync(sonarPingBuffer)
    }

    open fun playDataStream() {
        if (!isSoundEnabled) return
        playPcmAsync(dataStreamBuffer)
    }

    open fun playBlackout() {
        if (!isSoundEnabled) return
        playPcmAsync(blackoutBuffer)
        vibrateHeavy()
    }

    open fun playBootBeep() {
        if (!isSoundEnabled) return
        playPcmAsync(bootBeepBuffer)
        vibrateMedium()
    }

    open fun playTerminalKeystroke() = playKeyClick()
    open fun playTerminalExecute() = playTerminalBeep()
    open fun playBootRelaySound() = playBootBeep()
    open fun playBootModemSound() = playDataStream()
    open fun playErrorBuzz() = playAccessDenied()

    protected fun playPcmAsync(buffer: ByteArray) {
        coroutineScope.launch {
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
                            .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                coroutineScope.launch {
                    val durationMs = (buffer.size * 1000L) / sampleRate + 30
                    kotlinx.coroutines.delay(durationMs)
                    track.stop()
                    track.release()
                }
            } catch (_: Exception) {}
        }
    }

    private fun generateKeyClick(): ByteArray {
        val durationMs = 12
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 400.0)
            val freq = 1800.0 + (i % 3) * 200.0
            val sample = (sin(2.0 * PI * freq * t) * envelope * 120.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateTone(frequency: Double, durationMs: Int, volume: Double = 0.5): ByteArray {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = when {
                i < numSamples * 0.1 -> i / (numSamples * 0.1)
                i > numSamples * 0.8 -> (numSamples - i) / (numSamples * 0.2)
                else -> 1.0
            }
            val sample = (sin(2.0 * PI * frequency * t) * envelope * volume * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateAccessGranted(): ByteArray {
        val durationMs = 160
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val freq = if (progress < 0.5) 880.0 else 1760.0
            val envelope = 1.0 - (progress * 0.3)
            val sample = (sin(2.0 * PI * freq * t) * envelope * 0.45 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateAccessDenied(): ByteArray {
        val durationMs = 180
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val square = if (sin(2.0 * PI * 220.0 * t) > 0) 1.0 else -1.0
            val envelope = exp(-t * 8.0)
            val sample = (square * envelope * 0.5 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateSonarPing(): ByteArray {
        val durationMs = 300
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 12.0)
            val sample = (sin(2.0 * PI * 1500.0 * t) * envelope * 0.6 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateDataStream(): ByteArray {
        val durationMs = 250
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val step = (i / 120) % 5
            val freq = 1100.0 + step * 300.0
            val sample = (sin(2.0 * PI * freq * t) * 0.35 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateBlackout(): ByteArray {
        val durationMs = 350
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val freq = (1.0 - progress) * 450.0 + 80.0
            val sample = (sin(2.0 * PI * freq * t) * (1.0 - progress) * 0.7 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generatePhoneRing(): ByteArray {
        val durationMs = 300
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val dualTone = sin(2.0 * PI * 440.0 * t) + sin(2.0 * PI * 480.0 * t)
            val sample = (dualTone * 0.3 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateRadioStatic(): ByteArray {
        val durationMs = 120
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val rand = kotlin.random.Random.nextDouble(-1.0, 1.0)
            val sample = (rand * 0.4 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateBootBeep(): ByteArray {
        val durationMs = 90
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val sample = (sin(2.0 * PI * 920.0 * t) * 0.45 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateTileMoveChirp(): ByteArray {
        val durationMs = 28
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val freq = 1600.0 + (progress * 1200.0)
            val envelope = (1.0 - progress) * (if (i < 30) i / 30.0 else 1.0)
            val sample = (sin(2.0 * PI * freq * t) * envelope * 0.5 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateLetterTick(): ByteArray {
        val durationMs = 8
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 600.0)
            val sample = (sin(2.0 * PI * 3400.0 * t) * envelope * 0.35 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateHackStart(): ByteArray {
        val durationMs = 380
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            // Ascending carrier chirp with stepped frequency modulation
            val stepFreq = when {
                progress < 0.25 -> 750.0
                progress < 0.50 -> 1100.0
                progress < 0.75 -> 1600.0
                else -> 2200.0
            }
            val sweep = sin(2.0 * PI * stepFreq * t)
            val vibrato = sin(2.0 * PI * 40.0 * t) * 0.2
            val noise = (kotlin.random.Random.nextDouble(-0.15, 0.15))
            val envelope = if (progress < 0.08) progress / 0.08 else (1.0 - progress * 0.4)
            val sample = ((sweep + vibrato + noise) * envelope * 0.45 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateHackSuccess(): ByteArray {
        val durationMs = 460
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        val noteDur = numSamples / 4
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
        for (i in 0 until numSamples) {
            val noteIndex = (i / noteDur).coerceIn(0, 3)
            val noteSampleIdx = i % noteDur
            val tNote = noteSampleIdx.toDouble() / sampleRate
            val freq = notes[noteIndex]
            val envelope = exp(-tNote * 8.0) * (if (noteIndex == 3) 1.2 else 1.0)
            val tone = sin(2.0 * PI * freq * tNote) + 0.3 * sin(2.0 * PI * (freq * 2.0) * tNote)
            val sample = (tone * envelope * 0.5 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateHackFail(): ByteArray {
        val durationMs = 380
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            // Discordant falling tone with sawtooth rasp
            val freq1 = 280.0 * (1.0 - progress * 0.6)
            val freq2 = 295.0 * (1.0 - progress * 0.6)
            val sq1 = if (sin(2.0 * PI * freq1 * t) > 0) 0.6 else -0.6
            val sq2 = if (sin(2.0 * PI * freq2 * t) > 0) 0.4 else -0.4
            val envelope = exp(-progress * 2.5)
            val sample = ((sq1 + sq2) * envelope * 0.45 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    protected fun vibrateLight() {
        if (!isHapticsEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(12)
                }
            }
        } catch (_: Exception) {}
    }

    protected fun vibrateMedium() {
        if (!isHapticsEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(30)
                }
            }
        } catch (_: Exception) {}
    }

    protected fun vibrateDouble() {
        if (!isHapticsEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 25, 40, 25), -1))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(50)
                }
            }
        } catch (_: Exception) {}
    }

    protected fun vibrateHeavy() {
        if (!isHapticsEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(120)
                }
            }
        } catch (_: Exception) {}
    }
}
