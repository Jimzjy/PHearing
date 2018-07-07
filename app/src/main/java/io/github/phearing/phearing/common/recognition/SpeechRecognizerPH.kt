package io.github.phearing.phearing.common.recognition

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.MutableLiveData
import dagger.Component
import edu.cmu.pocketsphinx.*
import io.github.phearing.phearing.common.application.ApplicationComponent
import io.github.phearing.phearing.common.application.PHApplication
import java.io.File
import javax.inject.Inject
import javax.inject.Scope

const val INIT_ERROR = 0
const val LISTENER_ERROR = 1

class SpeechRecognizerPH : RecognitionListener {
    var isStart = MutableLiveData<Boolean>()
    var errorCallBack: ((errorMessage: Int) -> Unit)? = null
    var timeoutCallBack: (() -> Unit)? = null
    var resultCallBack: ((result: String) -> Unit)? = null

    @Inject
    lateinit var context: Context
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private var mTableNo = -1

    init {
        DaggerSpeechRecognitionComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .build().inject(this)
        isStart.value = false
    }

    fun init(tableNo: Int) {
        this.mTableNo = tableNo
        SetupTask(this).execute()
    }

    fun stop() {
        mSpeechRecognizer?.stop()
        isStart.value = false
    }

    fun start(timeout: Int = 10000) {
        mSpeechRecognizer?.stop()
        mSpeechRecognizer?.startListening(mTableNo.toString(), timeout)
        isStart.value = true
    }

    fun destroy() {
        mSpeechRecognizer?.let {
            it.cancel()
            it.shutdown()
        }
        mSpeechRecognizer = null
    }

    private fun setupRecognizer(assetsDir: File) {
        mSpeechRecognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(File(assetsDir, "zh-ptm"))
                .setDictionary(File(assetsDir, "speech.dic"))
                .recognizer
        mSpeechRecognizer?.addListener(this)

        val tableFile = File(assetsDir, "table_$mTableNo")
        mSpeechRecognizer?.addGrammarSearch(mTableNo.toString(), tableFile)
    }

    private class SetupTask(private val mSpeechRecognizerPH: SpeechRecognizerPH) : AsyncTask<Unit, Unit, Exception?>() {

        override fun doInBackground(vararg p0: Unit?): Exception? {
            try{
                val assets = Assets(mSpeechRecognizerPH.context)
                val assetDir = assets.syncAssets()
                mSpeechRecognizerPH.setupRecognizer(assetDir)
            } catch (e: Exception) {
                return e
            }
            return null
        }

        override fun onPostExecute(result: Exception?) {
            super.onPostExecute(result)
            if (result != null) {
                mSpeechRecognizerPH.errorCallBack?.invoke(INIT_ERROR)
            } else {
                mSpeechRecognizerPH.start()
            }
        }
    }

    override fun onResult(p0: Hypothesis?) {
        Log.e("", "onResult")
    }

    override fun onPartialResult(p0: Hypothesis?) {
        p0?.let {
            resultCallBack?.invoke(it.hypstr)
            isStart.value = false
            Log.e("", "onPartialResult")
        }
    }

    override fun onTimeout() {
        timeoutCallBack?.invoke()
        isStart.value = false
        Log.e("", "onTimeout")
    }

    override fun onBeginningOfSpeech() {
        Log.e("", "onBeginningOfSpeech")
    }

    override fun onEndOfSpeech() {
        Log.e("", "onEndOfSpeech")
    }

    override fun onError(p0: java.lang.Exception?) {
        errorCallBack?.invoke(LISTENER_ERROR)
        p0?.printStackTrace()
        isStart.value = false
        Log.e("", "onError")
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SpeechRecognitionScope

@SpeechRecognitionScope
@Component(dependencies = [ApplicationComponent::class])
interface SpeechRecognitionComponent {
    fun inject(speechRecognizerPH: SpeechRecognizerPH)
}