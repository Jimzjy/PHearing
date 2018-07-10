package io.github.phearing.phearing.ui.headphone

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.Component
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.ApplicationComponent
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.common.audio.HeadphoneMeasurer
import io.github.phearing.phearing.common.audio.TONE_SIDE_LEFT
import io.github.phearing.phearing.common.audio.TONE_SIDE_RIGHT
import io.github.phearing.phearing.room.headphone.Headphone
import io.github.phearing.phearing.room.headphone.HeadphoneRepo
import java.util.*
import javax.inject.Inject
import javax.inject.Scope

const val MEASURE_START = 0
const val MEASURE_PREPARE = 1
const val MEASURE_CONTINUE = 2
const val MEASURE_FINISH = 3

class MeasureViewModel(application: Application) : AndroidViewModel(application) {
    val dataText = MutableLiveData<String>()
    val state = MutableLiveData<Int>()
    val isHintOpen = MutableLiveData<Boolean>()
    val minDbListRight = MutableLiveData<FloatArray>()
    val maxDbListRight = MutableLiveData<FloatArray>()
    val minDbListLeft = MutableLiveData<FloatArray>()
    val maxDbListLeft = MutableLiveData<FloatArray>()

    @Inject
    lateinit var headphoneRepo: HeadphoneRepo
    var micOffset = 10
    private var mHpMeasurer: HeadphoneMeasurer? = null
    private var mDbHLVolume = arrayOf("", "")
    private var mSide = TONE_SIDE_RIGHT

    init {
        dataText.value = getDataFormatText(0, 0f)
        state.value = MEASURE_PREPARE
        isHintOpen.value = true

        DaggerMeasureViewModelComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .build().inject(this)
    }

    fun startMeasure() {
        state.value = MEASURE_START
        isHintOpen.value = false
        if (mHpMeasurer == null) initHpMeasurer()
        mHpMeasurer?.micOffset = micOffset
        mHpMeasurer?.startMeasure(mSide)
    }

    fun stop() {
        state.value = MEASURE_PREPARE
        isHintOpen.value = true
        if (mHpMeasurer != null && mHpMeasurer?.isStart == true) {
            mHpMeasurer?.stop()
            mHpMeasurer = null
        }
    }

    fun insertHeadphoneData(name: String): Boolean {
        return if (mDbHLVolume[0].isNotEmpty() && mDbHLVolume[1].isNotEmpty()) {
            headphoneRepo.insertHeadphones(Headphone(name, mDbHLVolume[1], mDbHLVolume[0], Date().time))
            true
        } else {
            false
        }
    }

    private fun initHpMeasurer() {
        mHpMeasurer = HeadphoneMeasurer()
        mHpMeasurer?.setFinishListener { dbHLVolumeList -> stopMeasure(dbHLVolumeList) }
        mHpMeasurer?.setMeasurerListener { freq, db ->
            dataText.value = getDataFormatText(freq, db)
        }
    }

    private fun stopMeasure(dbHLVolumeList: List<FloatArray>) {
        val maxList = mutableListOf<Float>()
        val minList = mutableListOf<Float>()
        var freq = 125f
        val volume = StringBuilder()

        dbHLVolumeList.forEach {
            for (i in 0 until it.size) {
                if (it[i] >= 0) {
                    minList.add(freq)
                    minList.add(i * 5f)
                    break
                }
            }

            for (i in (it.size - 1) downTo 0) {
                if (it[i] >= 0) {
                    maxList.add(freq)
                    maxList.add(i * 5f)
                    break
                }
            }

            it.forEach {
                volume.append("$it|")
            }

            freq *= 2
        }

        when(mSide) {
            TONE_SIDE_LEFT -> {
                mDbHLVolume[0] = volume.toString()
                minDbListLeft.value = minList.toFloatArray()
                maxDbListLeft.value = maxList.toFloatArray()
                mSide = TONE_SIDE_RIGHT
                dataText.value = getDataFormatText(0, 0f)
                state.value = MEASURE_FINISH
                isHintOpen.value = true
            }
            TONE_SIDE_RIGHT -> {
                mDbHLVolume[1] = volume.toString()
                minDbListRight.value = minList.toFloatArray()
                maxDbListRight.value = maxList.toFloatArray()
                mSide = TONE_SIDE_LEFT
                dataText.value = getDataFormatText(0, 0f)
                state.value = MEASURE_CONTINUE
                isHintOpen.value = true
            }
        }
    }

    private fun getDataFormatText(freq: Int, db: Float): String {
        val resources = getApplication<Application>()
        return when(mSide) {
            TONE_SIDE_LEFT -> {
                String.format(resources.getString(R.string.measure_data_format),
                        resources.getString(R.string.left), freq, db)
            }
            TONE_SIDE_RIGHT -> {
                String.format(resources.getString(R.string.measure_data_format),
                        resources.getString(R.string.right), freq, db)
            }
            else -> {
                String.format(resources.getString(R.string.measure_data_format),
                        resources.getString(R.string.stereo), freq, db)
            }
        }
    }
}

@MeasureScope
@Component(dependencies = [ApplicationComponent::class])
interface MeasureViewModelComponent {
    fun inject(measureViewModel: MeasureViewModel)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class MeasureScope
