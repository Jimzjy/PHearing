package io.github.phearing.phearing.ui.speech

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.Component
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.ApplicationComponent
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.common.speech.*
import io.github.phearing.phearing.room.speech.SpeechData
import io.github.phearing.phearing.room.speech.SpeechDataRepo
import java.util.*
import javax.inject.Inject
import javax.inject.Scope

class SpeechViewModel(application: Application) : AndroidViewModel(application) {
    private val mSpeechUtil = SpeechUtil()

    val scoreText = MutableLiveData<String>()
    val hypText = MutableLiveData<String>()
    val countText = MutableLiveData<String>()
    val trueHintShow = MutableLiveData<Int>()
    val falseHintShow = MutableLiveData<Int>()
    val state = MutableLiveData<Int>()
    val isHintOpen = MutableLiveData<Boolean>()
    val isNextButtonEnable = MutableLiveData<Boolean>()
    val choiceList = MutableLiveData<List<String>>()

    @Inject
    lateinit var mSpeechDataRepo: SpeechDataRepo

    var score = 0
    var falseHintEnable = true

    init {
        scoreText.value = "0"
        hypText.value = "..."
        countText.value = "0 / 25"
        trueHintShow.value = 0
        falseHintShow.value = 0
        isHintOpen.value = true
        state.value = SPEECH_WAIT
        isNextButtonEnable.value = false

        DaggerSpeechViewModelComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .build().inject(this)

        init()
    }

    fun init() {
        state.value = SPEECH_WAIT
        isHintOpen.value = true
        mSpeechUtil.recognizerStartCallback = {
            hypText.postValue(getApplication<PHApplication>().getString(R.string.Recognizing))
        }
        mSpeechUtil.errorCallback = {
            when(it) {
                TTS_NOT_SUPPORT -> {
                    state.value = SPEECH_NOT_SUPPORT
                }
                TTS_ERROR -> {
                    Log.e("Speech", "TTS_ERROR")
                }
                INIT_ERROR -> {
                    mSpeechUtil.init((Math.random() * SpeechRecognizerPH.tables.size).toInt()) {
                        state.value = SPEECH_PREPARE
                        isHintOpen.value = true
                    }
                    Log.e("Speech", "Init_Error")
                }
                TIME_OUT -> {
                    answerWrong()
                    Log.e("Speech", "TIME_OUT")
                }
                RECOGNITION_LISTENER_ERROR -> {
                    Log.e("Speech", "Recognizer_Error")
                }
            }
        }
        mSpeechUtil.resultCallback = { aTrue, text ->
            hypText.value = text
            if (aTrue) {
                answerRight()
            } else {
                answerWrong()
            }
        }
        mSpeechUtil.init((Math.random() * SpeechRecognizerPH.tables.size).toInt()) {
            state.value = SPEECH_PREPARE
            isHintOpen.value = true
        }
    }

    fun speak(text: String) {
        mSpeechUtil.speakText(text)
    }

    fun startTest() {
        isHintOpen.value = false
        score = 0
        scoreText.value = score.toString()
        state.value = SPEECH_START

        next()
    }

    fun next() {
        if (mSpeechUtil.textNo == 24) {
            insertSpeechData()
            state.value = SPEECH_FINISH
            isHintOpen.value = true
            return
        }

        hypText.value = getApplication<PHApplication>().resources.getString(R.string.playing)
        falseHintEnable = true
        mSpeechUtil.next()
        countText.value = "${mSpeechUtil.textNo + 1} / 25"
    }

    fun destroy() {
        mSpeechUtil.release()
    }

    fun setChoiceList() {
        val array = arrayOf("", "", "", "")
        array[(Math.random() * 4).toInt()] = SpeechRecognizerPH.tables[mSpeechUtil.tableNo][mSpeechUtil.textNo]

        for (i in 0 until 4) {
            if (array[i].isEmpty()) {
                var text = ""
                while (text in array || text.isEmpty()) {
                    text = SpeechRecognizerPH.tables[(Math.random() * SpeechRecognizerPH.tables.size).toInt()][(Math.random() * 25).toInt()]
                }
                array[i] = text
            }
        }
        choiceList.value = array.toList()
    }

    fun checkAnswer(i: Int) {
        if (choiceList.value?.get(i) == SpeechRecognizerPH.tables[mSpeechUtil.tableNo][mSpeechUtil.textNo]) {
            answerRight()
        } else {
            answerWrong()
            falseHintEnable = false
        }
    }

    private fun answerRight() {
        score += 4
        scoreText.value = score.toString()
        trueHintShow.value = (trueHintShow.value ?: 0) + 1
        isNextButtonEnable.value = true
    }

    private fun answerWrong() {
        falseHintShow.value = (falseHintShow.value ?: 0) + 1
        isNextButtonEnable.value = true
    }

    private fun insertSpeechData() {
        mSpeechDataRepo.insertSpeechData(SpeechData(Date().time, score, mSpeechUtil.tableNo))
    }
}

@SpeechScope
@Component(dependencies = [ApplicationComponent::class])
interface SpeechViewModelComponent {
    fun inject(speechViewModel: SpeechViewModel)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SpeechScope
