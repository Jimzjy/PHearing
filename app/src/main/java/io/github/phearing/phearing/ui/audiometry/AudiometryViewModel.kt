package io.github.phearing.phearing.ui.audiometry

import android.app.Application
import android.app.Service
import android.media.AudioManager
import androidx.lifecycle.*
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.application.PHApplication
import io.github.phearing.phearing.common.audio.TONE_SIDE_LEFT
import io.github.phearing.phearing.common.audio.TONE_SIDE_RIGHT
import io.github.phearing.phearing.common.audio.Tone
import io.github.phearing.phearing.room.audiometry.AudiometryData
import io.github.phearing.phearing.room.audiometry.AudiometryDataRepo
import io.github.phearing.phearing.room.headphone.Headphone
import io.github.phearing.phearing.room.headphone.HeadphoneRepo
import java.util.*
import kotlin.math.abs
import kotlin.math.log2

const val MAX_FREQ = 8000
const val MIN_FREQ = 125
const val MAX_DB_HL = 70
const val DEFAULT_DB = 10
const val DEFAULT_DURATION = 500

const val AUDIOMETRY_START = 0
const val AUDIOMETRY_PREPARE = 1
const val AUDIOMETRY_FINISH = 2

class AudiometryViewModel(application: Application) : AndroidViewModel(application) {
    val dataText = MutableLiveData<String>()
    val state = MutableLiveData<Int>()
    val isHintOpen = MutableLiveData<Boolean>()
    val pointList = MutableLiveData<List<Float>>()
    val xPointList = MutableLiveData<List<Float>>()
    val startPlayAnimation = MutableLiveData<Int>()
    val allHeadphones: LiveData<List<Headphone>> = HeadphoneRepo().allHeadphones

    var headphone = MutableLiveData<Headphone>()
    var rightVolumeDBHL = mutableListOf<FloatArray>()
    var leftVolumeDBHL = mutableListOf<FloatArray>()
    // 0: min 1: max
    var minMaxRightDBHL = arrayOf(mutableListOf<Int>(), mutableListOf())
    var minMaxLeftDBHL = arrayOf(mutableListOf<Int>(), mutableListOf())
    var rightLevel = 0f
    var leftLevel = 0f
    private var mFreq = MIN_FREQ
    private var mDb = DEFAULT_DB
    private var mSide = TONE_SIDE_RIGHT
    private var mLastDb = -1
    private var mTone: Tone? = null
    private var mAudioManager: AudioManager? = null
    private var mVolumeDefault = 1
    // right
    private val mPointList = mutableListOf<Float>()
    // left
    private val mXPointList = mutableListOf<Float>()
    private val mAudiometryDataRepo by lazy { AudiometryDataRepo() }

    init {
        state.value = AUDIOMETRY_PREPARE
        dataText.value = getFormatDataText()
        isHintOpen.value = true
        pointList.value = mutableListOf()
        xPointList.value = mutableListOf()
        startPlayAnimation.value = 0
    }

    fun startTest() {
        if (headphone.value == null) return
        mAudioManager = getApplication<PHApplication>().getSystemService(Service.AUDIO_SERVICE) as AudioManager
        mVolumeDefault = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 1
        state.value = AUDIOMETRY_START
        isHintOpen.value = false
        if (mPointList.isNotEmpty()) {
            mPointList.clear()
            pointList.value = mPointList
        }
        if (mXPointList.isNotEmpty()) {
            mXPointList.clear()
            xPointList.value = mXPointList
        }

        mDb = minMaxRightDBHL[0][0]
        dataText.value = getFormatDataText()
        setVolume()
        play()
    }

    fun stop() {
        if (mTone != null) {
            mTone?.release()
            mTone = null
        }
        mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeDefault, 0)
        mAudioManager = null
    }

    fun play() {
        if (mTone == null) mTone = Tone(mFreq, DEFAULT_DURATION, mSide)
        startPlayAnimation.value = (startPlayAnimation.value ?: 0) + 1
        mTone?.play()
    }

    fun heard() {
        dealWithStep(-5)
    }

    fun unHeard() {
        dealWithStep(5)
    }

    private fun dealWithStep(step: Int) {
        if (mLastDb >= 0 && ((mDb - mLastDb < 0 && step > 0) || (mDb - mLastDb > 0 && step < 0))) {
            mDb = (mDb + mLastDb) / 2
            addPoint()
            toNext()
            return
        }

        var baseDb = 0
        if (step < 0) {
            when(mSide) {
                TONE_SIDE_RIGHT -> {
                    baseDb = minMaxRightDBHL[0][log2(mFreq/MIN_FREQ.toDouble()).toInt()]
                }
                TONE_SIDE_LEFT -> {
                    baseDb = minMaxLeftDBHL[0][log2(mFreq/MIN_FREQ.toDouble()).toInt()]
                }
            }
        } else {
            when(mSide) {
                TONE_SIDE_RIGHT -> {
                    baseDb = minMaxRightDBHL[1][log2(mFreq/MIN_FREQ.toDouble()).toInt()]
                }
                TONE_SIDE_LEFT -> {
                    baseDb = minMaxLeftDBHL[1][log2(mFreq/MIN_FREQ.toDouble()).toInt()]
                }
            }
        }

        val count = abs(mDb - baseDb) / abs(step)
        if (count == 0 && baseDb >= 0 && baseDb <= 25) {
            // too low, out of range
            addPoint()
            toNext()
            return
        } else if (count == 0 && baseDb >= MAX_DB_HL) {
            // over max, out of range
            addPoint()
            toNext()
            return
        } else if (count == 0 ) {
            // out of range
            toNext()
            return
        }

        for (i in 1..count) {
            if (isDbValidate(mDb + step * i)) {
                mLastDb = mDb
                mDb += step * i
                setVolume()
                dataText.value = getFormatDataText()
                play()
                break
            }
        }
    }

    // when data confirmed
    private fun toNext() {
        when {
            mFreq < MAX_FREQ -> {
                mFreq *= 2
                mLastDb = -1
            }
            mSide == TONE_SIDE_LEFT && mFreq >= MAX_FREQ -> {
                mFreq = 125
                mSide = TONE_SIDE_RIGHT
                mLastDb = -1
                stop()
                rightLevel = getAverageLevel(mPointList)
                leftLevel = getAverageLevel(mXPointList)
                insertAudiometryData()
                state.value = AUDIOMETRY_FINISH
                isHintOpen.value = true
            }
            mFreq >= MAX_FREQ -> {
                mSide = TONE_SIDE_LEFT
                mFreq = 125
                mLastDb = -1
            }
        }

        var minDb = -1
        when(mSide) {
            TONE_SIDE_RIGHT -> {
                minDb = minMaxRightDBHL[0][log2(mFreq/MIN_FREQ.toDouble()).toInt()]
            }
            TONE_SIDE_LEFT -> {
                minDb = minMaxLeftDBHL[0][log2(mFreq/MIN_FREQ.toDouble()).toInt()]
            }
        }

        mDb = minDb
        dataText.value = getFormatDataText()
        if (state.value != AUDIOMETRY_FINISH) {
            mTone = Tone(mFreq, DEFAULT_DURATION, mSide)
            setVolume()
            play()
        }
    }

    private fun insertAudiometryData() {
        mAudiometryDataRepo.insertAudiometryData(AudiometryData(Date().time,
                mPointList.joinToString("|"),
                mXPointList.joinToString("|"),
                rightLevel, leftLevel))
    }

    private fun getFormatDataText(): String {
        val resources = getApplication<Application>().resources
        return when(mSide) {
            TONE_SIDE_RIGHT -> {
                String.format(resources.getString(R.string.audiometry_data_format),
                        resources.getString(R.string.right), mFreq, mDb)
            }
            TONE_SIDE_LEFT -> {
                String.format(resources.getString(R.string.audiometry_data_format),
                        resources.getString(R.string.left), mFreq, mDb)
            }
            else -> {
                String.format(resources.getString(R.string.audiometry_data_format),
                        resources.getString(R.string.stereo), mFreq, mDb)
            }
        }
    }

    private fun addPoint() {
        when(mSide) {
            TONE_SIDE_RIGHT -> {
                mPointList.addAll(arrayOf(mFreq.toFloat(), mDb.toFloat()))
                pointList.value = mPointList
            }
            TONE_SIDE_LEFT -> {
                mXPointList.addAll(arrayOf(mFreq.toFloat(), mDb.toFloat()))
                xPointList.value = mXPointList
            }
        }
    }

    private fun isDbValidate(db: Int): Boolean {
        return getVolume(mFreq, db, mSide) >= 0
    }

    /**
     * @return 1.0: system(1), tone(1.0). 1.1: system(1), tone(0.1). -1.0: unvalidated
     */
    private fun getVolume(freq: Int, db: Int, side: Int): Float {
        if (db < 0 || db > 70) return -1f
        return when(side) {
            TONE_SIDE_RIGHT -> {
                if (rightVolumeDBHL.size == 7) {
                    rightVolumeDBHL[log2(freq/ MIN_FREQ.toDouble()).toInt()][db / 5]
                } else {
                    -1f
                }
            }
            TONE_SIDE_LEFT -> {
                if (leftVolumeDBHL.size == 7) {
                    leftVolumeDBHL[log2(freq/ MIN_FREQ.toDouble()).toInt()][db / 5]
                } else {
                    -1f
                }
            }
            else -> {
                -1f
            }
        }
    }

    private fun setVolume(): Boolean {
        if (mAudioManager == null) mAudioManager = getApplication<PHApplication>().getSystemService(Service.AUDIO_SERVICE) as AudioManager
        val volume = getVolume(mFreq, mDb, mSide)
        if (volume < 0) return false
        val toneVolume = volume % 1
        val systemVolume = volume.toInt()

        mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, systemVolume, 0)
        mTone?.volume = toneVolume
        return true
    }

    private fun getAverageLevel(pointList: List<Float>): Float {
        var total = 0f
        for (i in 1..(pointList.size / 2)) {
            total += pointList[i*2 - 1]
        }
        return total / (pointList.size / 2)
    }
}
