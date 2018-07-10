package io.github.phearing.phearing.common.audio

import android.app.Service
import android.content.Context
import android.media.AudioManager
import android.os.Handler
import dagger.Component
import io.github.phearing.phearing.common.PHApplication
import javax.inject.Inject
import javax.inject.Scope
import kotlin.math.log2


class HeadphoneMeasurer {
    companion object {
        /*
            ANSI S3.6 2010
            freq: 125 250 500 1000 2000 4000 8000
         */
        val DB_SPL_2_DB_HL = arrayOf(-45f, -27f, -13.5f, -7.5f, -9f, -12f, -15.5f)
    }
    private var mTone: Tone? = null
    private var mRecorder: RecorderAverage? = null
    private var mAudioManager: AudioManager? = null
    private var mMeasurerCallBack: ((freq: Int, db: Float) -> Unit)? = null
    private var mFinishCallBack: ((dbHLVolumeList: List<FloatArray>) -> Unit)? = null
    private var mVolumeDefault = 1
    private val context = PHApplication.instance.applicationContext

    var micOffset = 10
    var isStart = false

    /**
     * @param side [TONE_SIDE_DEFAULT], [TONE_SIDE_LEFT], [TONE_SIDE_RIGHT]
     */
    fun startMeasure(side: Int) {
        isStart = true
        mAudioManager = context.getSystemService(Service.AUDIO_SERVICE) as AudioManager
        var maxVolume = 0
        mAudioManager?.let {
            mVolumeDefault = it.getStreamVolume(AudioManager.STREAM_MUSIC)
            maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        }

        // 1.0 : system 1, track 1.0 . 1.1 : system 1, track 0.1
        val dbHLVolumeList = mutableListOf<FloatArray>()
        val handler = Handler()

        if (mTone == null) mTone = Tone(125, 150, TONE_SIDE_RIGHT)
        mTone?.side = side
        if (mRecorder == null) mRecorder = RecorderAverage()
        mRecorder?.justStart()

        Thread{
            try {
                val dbList = mutableListOf<Float>()
                var freq = 125

                DB_SPL_2_DB_HL.forEach {
                    var volume = 1
                    dbList.clear()

                    while (volume <= maxVolume && mRecorder != null) {
                        mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
                        mTone?.play(freq)
                        Thread.sleep(100)
                        mRecorder?.let {
                            val db = it.getDb()
                            handler.post {
                                try {
                                    mMeasurerCallBack?.invoke(freq, db)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            dbList.add(db + micOffset)
                        }
                        volume += 1
                    }

                    val map = getValidatedDBHL(dbList, freq, handler)
                    dbHLVolumeList.add(get0to70dBHLVolume(map))

                    freq *= 2
                }

                mRecorder?.let {
                    handler.post {
                        try {
                            mFinishCallBack?.invoke(dbHLVolumeList)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stop()
            }
        }.start()
    }

    fun stop() {
        mRecorder?.stop()
        mTone?.release()
        mTone = null
        mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeDefault, 0)
        mRecorder = null
        mAudioManager = null
        isStart = false
    }

    fun setMeasurerListener(listener: (freq: Int, db: Float) -> Unit) {
        mMeasurerCallBack = listener
    }

    fun setFinishListener(listener: (dbHLVolumeList: List<FloatArray>) -> Unit) {
        mFinishCallBack = listener
    }

    // unvalidated: -1f
    private fun getValidatedDBHL(dbList: List<Float>, freq: Int, handler: Handler, step: Int = 2): Map<Int, Array<Float>> {
        var last = dbList[0]
        // K: volume(system) V: track volume 0.1~1.0
        val map = mutableMapOf<Int, Array<Float>>()
        val list = mutableListOf<Int>()
        val change = DB_SPL_2_DB_HL[log2(freq.toDouble()/125).toInt()]
        list.add(1)

        for (i in 1 until dbList.size) {
            if (dbList[i] - last >= step) {
                list.add(i + 1)
                last = dbList[i]
            }
        }

        list.forEach {
            mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, it, 0)
            var volume = 0.1f
            val array = Array(10) { -1f }
            while (mRecorder != null && volume < 1.1f) {
                mTone?.volume = volume
                mTone?.play()
                Thread.sleep(100)
                mRecorder?.let {
                    val db = it.getDb()
                    array[(volume * 10).toInt() - 1] = db + change
                    handler.post {
                        mMeasurerCallBack?.invoke(freq, db)
                    }
                }
                volume += 0.1f
            }
            map[it] = array
        }
        return map
    }

    // step: 5db, freq: Hz
    private fun get0to70dBHLVolume(dBHL_map: Map<Int, Array<Float>>): FloatArray {
        val list = mutableListOf<Float>()
        // K: dBHL V: volume(system + track(if == 1.0 ? 0))
        var map = mutableMapOf<Float, Float>()

        for ((k, v) in dBHL_map) {
            for (i in 0..9) {
                if (i == 9) {
                    map[v[i]] = k.toFloat()
                } else {
                    map[v[i]] = k + i / 10f + 0.1f
                }
            }
        }
        map = map.toSortedMap()

        var last = -5
        while (last < 70) {
            list.add(getStepVolume(last, map))
            last += 5
        }
        return list.toFloatArray()
    }

    private fun getStepVolume(last: Int, map: Map<Float, Float>): Float {
        for ((k, v) in map) {
            if (k - last in 4..6) {
                return v
            }
        }
        return -1f
    }
}