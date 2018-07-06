package io.github.phearing.phearing.common.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build

const val TONE_SIDE_DEFAULT = 0
const val TONE_SIDE_LEFT = 1
const val TONE_SIDE_RIGHT = 2

class Tone(frequency: Int, private val mDuration: Int = 1000, side: Int = TONE_SIDE_DEFAULT) {
    companion object {
        @JvmStatic
        fun generate(frequency: Int, duration: Int, side: Int): AudioTrack {
            val count = (44100f * 2 * (duration / 1000f)).toInt().and(1.inv())
            val tone = ShortArray(count)
            var i = 0
            when(side) {
                TONE_SIDE_DEFAULT -> {
                    while (i < count) {
                        val sample = (Math.sin(2 * Math.PI * i / (44100f / frequency * 2)) * 0x7FFF).toShort()
                        tone[i + 0] = sample
                        tone[i + 1] = sample
                        i += 2
                    }
                }
                TONE_SIDE_RIGHT -> {
                    while (i < count) {
                        val sample = (Math.sin(2 * Math.PI * i / (44100f / frequency * 2)) * 0x7FFF).toShort()
                        tone[i + 0] = 0
                        tone[i + 1] = sample
                        i += 2
                    }
                }
                TONE_SIDE_LEFT -> {
                    while (i < count) {
                        val sample = (Math.sin(2 * Math.PI * i / (44100f / frequency * 2)) * 0x7FFF).toShort()
                        tone[i + 0] = sample
                        tone[i + 1] = 0
                        i += 2
                    }
                }
            }


            val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AudioTrack.Builder()
                        .setAudioFormat(AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(44100)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                                .build())
                        .setBufferSizeInBytes(count * 2)
                        .setTransferMode(AudioTrack.MODE_STATIC)
                        .build()
            } else {
                @Suppress("DEPRECATION")
                AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                        AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                        count * 2, AudioTrack.MODE_STATIC)
            }

            track.write(tone, 0, count)
            return track
        }
    }

    private var mTone: AudioTrack = generate(frequency, mDuration, side)
    var frequency: Int = frequency
        set(value) {
            if (field != value) {
                field = value
                mTone = generate(value, mDuration, side)
            }
        }
    var volume: Float = 1f
        set(value) {
            field = value
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTone.setVolume(value)
            } else {
                @Suppress("DEPRECATION")
                mTone.setStereoVolume(value, value)
            }
        }
    var side: Int = side
        set(value) {
            if (field != value) {
                field = value
                mTone = generate(frequency, mDuration, value)
            }
        }

    fun release() {
        mTone.release()
    }

    fun play(freq: Int = frequency) {
        when(mTone.playState) {
            AudioTrack.PLAYSTATE_PLAYING -> {
                mTone.stop()
                mTone.reloadStaticData()
            }
        }

        if (freq == frequency) {
            mTone.play()
        } else {
            frequency = freq
            mTone.play()
        }
    }
}