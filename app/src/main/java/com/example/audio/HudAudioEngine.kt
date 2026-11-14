package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class HudAudioEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var ambientJob: Job? = null
    private var ambientTrack: AudioTrack? = null

    @Volatile
    var isMuted: Boolean = false
        private set

    @Volatile
    var isMusicEnabled: Boolean = false
        private set

    @Volatile
    var masterVolume: Float = 0.65f
        private set

    private val sampleRate = 22050

    fun updateSettings(muted: Boolean, musicEnabled: Boolean, volume: Float) {
        isMuted = muted
        masterVolume = volume.coerceIn(0f, 1f)
        val shouldPlayMusic = musicEnabled && !muted
        if (shouldPlayMusic && !isMusicEnabled) {
            isMusicEnabled = true
            startAmbientMusic()
        } else if (!shouldPlayMusic) {
            isMusicEnabled = musicEnabled && !muted
            stopAmbientMusic()
        }
    }

    fun playQuestCompleted() {
        if (isMuted || masterVolume <= 0.01f) return
        scope.launch {
            playToneSequence(
                frequencies = listOf(523.25, 659.25, 783.99, 1046.50),
                noteDurationMs = 75,
                gain = 0.28f * masterVolume
            )
        }
    }

    fun playLevelUp() {
        if (isMuted || masterVolume <= 0.01f) return
        scope.launch {
            playToneSequence(
                frequencies = listOf(261.63, 329.63, 392.00, 523.25, 659.25, 783.99, 1046.50),
                noteDurationMs = 95,
                gain = 0.34f * masterVolume
            )
        }
    }

    fun playAchievementUnlocked() {
        if (isMuted || masterVolume <= 0.01f) return
        scope.launch {
            playToneSequence(
                frequencies = listOf(587.33, 880.00, 1174.66),
                noteDurationMs = 110,
                gain = 0.30f * masterVolume
            )
        }
    }

    fun playTimerDone() {
        if (isMuted || masterVolume <= 0.01f) return
        scope.launch {
            playToneSequence(
                frequencies = listOf(659.25, 659.25, 880.00),
                noteDurationMs = 130,
                gain = 0.30f * masterVolume
            )
        }
    }

    private fun playToneSequence(frequencies: List<Double>, noteDurationMs: Int, gain: Float) {
        val samplesPerNote = (sampleRate * noteDurationMs) / 1000
        val totalSamples = samplesPerNote * frequencies.size
        val pcm = ShortArray(totalSamples)

        frequencies.forEachIndexed { noteIdx, freq ->
            val offset = noteIdx * samplesPerNote
            for (i in 0 until samplesPerNote) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / samplesPerNote
                val attack = if (progress < 0.08) progress / 0.08 else 1.0
                val decay = exp(-3.4 * progress)
                val wave = 0.72 * sin(2.0 * PI * freq * t) + 0.28 * sin(2.0 * PI * freq * 2.0 * t)
                val sample = (wave * attack * decay * gain * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                pcm[offset + i] = sample.toShort()
            }
        }

        playOneShotBuffer(pcm)
    }

    private fun playOneShotBuffer(pcm: ShortArray) {
        var track: AudioTrack? = null
        try {
            val bufferBytes = pcm.size * 2
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferBytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcm, 0, pcm.size)
            track.play()
            val durationMs = (pcm.size * 1000L) / sampleRate + 60L
            Thread.sleep(durationMs)
        } catch (_: Throwable) {
            // Safe fallback if device audio hardware is unavailable in tests or headless runners
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Throwable) {
            }
        }
    }

    private fun startAmbientMusic() {
        if (ambientJob?.isActive == true) return
        ambientJob = scope.launch {
            val chunkSeconds = 4
            val chunkSamples = sampleRate * chunkSeconds
            val bufferBytes = chunkSamples * 2
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferBytes)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                ambientTrack = track
                track.play()

                var phaseOffset = 0.0
                val chordProgression = listOf(
                    Triple(110.0, 164.81, 220.0),  // A2 dark fifth + octave
                    Triple(98.0, 146.83, 196.0),   // G2 dark fifth + octave
                    Triple(87.31, 130.81, 174.61), // F2 ambient pad
                    Triple(98.0, 146.83, 246.94)   // G2 suspended pulse
                )
                var chordIndex = 0

                while (isActive && isMusicEnabled && !isMuted) {
                    val chord = chordProgression[chordIndex % chordProgression.size]
                    chordIndex++
                    val pcm = ShortArray(chunkSamples)
                    val vol = 0.10f * masterVolume
                    for (i in 0 until chunkSamples) {
                        val t = phaseOffset + i.toDouble() / sampleRate
                        val lfo = 0.65 + 0.35 * sin(2.0 * PI * 0.25 * (i.toDouble() / sampleRate))
                        val pad = (
                            0.50 * sin(2.0 * PI * chord.first * t) +
                                0.30 * sin(2.0 * PI * chord.second * t) +
                                0.20 * sin(2.0 * PI * chord.third * t)
                            ) * lfo
                        val edgeFade = when {
                            i < 1500 -> i / 1500.0
                            i > chunkSamples - 1500 -> (chunkSamples - i) / 1500.0
                            else -> 1.0
                        }
                        pcm[i] = (pad * edgeFade * vol * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            .toShort()
                    }
                    phaseOffset += chunkSeconds.toDouble()
                    track.write(pcm, 0, pcm.size)
                    delay(100)
                }
            } catch (_: Throwable) {
                // Ignore audio hardware failures on headless test environments
            } finally {
                stopAmbientTrackInternal()
            }
        }
    }

    fun stopAmbientMusic() {
        ambientJob?.cancel()
        ambientJob = null
        stopAmbientTrackInternal()
    }

    private fun stopAmbientTrackInternal() {
        try {
            ambientTrack?.pause()
            ambientTrack?.flush()
            ambientTrack?.stop()
            ambientTrack?.release()
        } catch (_: Throwable) {
        } finally {
            ambientTrack = null
        }
    }

    fun release() {
        stopAmbientMusic()
    }
}
