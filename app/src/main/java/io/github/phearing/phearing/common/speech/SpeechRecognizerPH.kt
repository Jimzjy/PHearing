package io.github.phearing.phearing.common.speech

import android.content.Context
import android.os.AsyncTask
import dagger.Component
import edu.cmu.pocketsphinx.*
import io.github.phearing.phearing.common.PHApplication
import java.io.File
import javax.inject.Inject
import javax.inject.Scope

const val INIT_ERROR = 0
const val TIME_OUT = 2
const val RECOGNITION_LISTENER_ERROR = 1

class SpeechRecognizerPH : RecognitionListener {
    companion object {
        @JvmStatic
        val tables = arrayOf(arrayOf("哀愁", "扣压", "枯草", "兰山", "狼藉", "里约", "鲁班", "落幕", "脉博", "脉冲", "瞒报", "盲文", "煤砖", "美丽", "扫描", "溶岩", "聘用", "苹果", "平衡", "平均", "气压", "巧合", "切片", "青菜", "人口"))
    }

    var errorCallBack: ((errorMessage: Int) -> Unit)? = null
    var resultCallback: ((result: String) -> Unit)? = null
    var startCallback: (() -> Unit)? = null
    var initCallback: (() -> Unit)? = null
        private set

    private var mSpeechRecognizer: SpeechRecognizerWithStartCallback? = null
    private var mTableNo = -1

    val context: Context = PHApplication.instance.applicationContext

    fun init(tableNo: Int, init: () -> Unit) {
        mTableNo = tableNo
        initCallback = init
        SetupTask(this).execute()
    }

    fun stop() {
        mSpeechRecognizer?.stop()
    }

    fun start(timeout: Int = 5000) {
        mSpeechRecognizer?.stop()
        mSpeechRecognizer?.startListening(mTableNo.toString(), timeout)
    }

    fun release() {
        mSpeechRecognizer?.let {
            it.cancel()
            it.shutdown()
        }
        mSpeechRecognizer = null
    }

    private fun setupRecognizer(assetsDir: File) {
        val setup = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(File(assetsDir, "zh-ptm"))
                .setDictionary(File(assetsDir, "speech.dic"))
        mSpeechRecognizer = SpeechRecognizerWithStartCallback(setup.getConfig())
        mSpeechRecognizer?.addListener(this)
        mSpeechRecognizer?.startCallback = startCallback

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
                e.printStackTrace()
                return e
            }
            return null
        }

        override fun onPostExecute(result: Exception?) {
            super.onPostExecute(result)
            if (result == null) {
                mSpeechRecognizerPH.initCallback?.invoke()
            } else {
                mSpeechRecognizerPH.errorCallBack?.invoke(INIT_ERROR)
            }
        }
    }

    override fun onResult(p0: Hypothesis?) {}

    override fun onPartialResult(p0: Hypothesis?) {
        p0?.let {
            resultCallback?.invoke(it.hypstr)
        }
    }

    override fun onTimeout() { errorCallBack?.invoke(TIME_OUT) }

    override fun onBeginningOfSpeech() {}

    override fun onEndOfSpeech() {}

    override fun onError(p0: java.lang.Exception?) {
        errorCallBack?.invoke(RECOGNITION_LISTENER_ERROR)
        p0?.printStackTrace()
    }
}