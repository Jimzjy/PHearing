package io.github.phearing.phearing.common.speech

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.Component
import io.github.phearing.phearing.common.PHApplication
import java.util.*
import javax.inject.Inject
import javax.inject.Scope

const val TTS_NOT_SUPPORT = -1
const val TTS_ERROR = -2

class SpeechUtil : UtteranceProgressListener() {
    var tableNo = -1
    var textNo = -1
    var errorCallback: ((error: Int) -> Unit)? = null
    var resultCallback: ((aTrue: Boolean, text: String) -> Unit)? = null
    var recognizerStartCallback: (() -> Unit)? = null

    private val context: Context = PHApplication.instance.applicationContext
    private var mTextToSpeech: TextToSpeech? = null
    private var mRecognizer: SpeechRecognizerPH? = null

    fun init(tableNo: Int, initCallback: () -> Unit) {
        mTextToSpeech = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                val code = mTextToSpeech?.setLanguage(Locale.CHINA)
                if (code == TextToSpeech.LANG_MISSING_DATA || code == TextToSpeech.LANG_NOT_SUPPORTED) {
                    errorCallback?.invoke(TTS_NOT_SUPPORT)
                    return@TextToSpeech
                }
                mTextToSpeech?.setSpeechRate(0.8f)
                mTextToSpeech?.setOnUtteranceProgressListener(this@SpeechUtil)

                mRecognizer = SpeechRecognizerPH()
                mRecognizer?.startCallback = {
                    recognizerStartCallback?.invoke()
                }
                mRecognizer?.errorCallBack = { errorCallback?.invoke(it) }
                mRecognizer?.init(tableNo) {
                    mRecognizer?.resultCallback = {
                        if (this.textNo >= 0) {
                            if (it == SpeechRecognizerPH.tables[this.tableNo][textNo]){
                                resultCallback?.invoke(true, it)
                            } else {
                                resultCallback?.invoke(false, it)
                            }
                            stop()
                        }
                    }

                    this.tableNo = tableNo
                    textNo = -1
                    initCallback.invoke()
                    mRecognizer?.start()
                }
            } else {
                errorCallback?.invoke(INIT_ERROR)
            }
        }
    }

    fun next() {
        textNo++
        speak()
    }

    fun stop() {
        mRecognizer?.stop()
        if (mTextToSpeech?.isSpeaking == true) mTextToSpeech?.stop()
    }

    fun release() {
        mRecognizer?.release()
        mTextToSpeech?.shutdown()
    }

    fun speakText(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            @Suppress("DEPRECATION")
            mTextToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun speak() {
        if (textNo in 0..24) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextToSpeech?.speak(SpeechRecognizerPH.tables[tableNo][textNo], TextToSpeech.QUEUE_FLUSH, null, "$tableNo|$textNo")
            } else {
                @Suppress("DEPRECATION")
                mTextToSpeech?.speak(SpeechRecognizerPH.tables[tableNo][textNo], TextToSpeech.QUEUE_FLUSH, hashMapOf(Pair(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "$tableNo|$textNo")))
            }
        }
        Log.e("", SpeechRecognizerPH.tables[tableNo][textNo])
    }

    override fun onDone(p0: String?) {
        mRecognizer?.start()
    }

    override fun onError(p0: String?) {
        errorCallback?.invoke(TTS_ERROR)
    }

    override fun onStart(p0: String?) {}
}