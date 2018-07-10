package io.github.phearing.phearing.common.speech

import edu.cmu.pocketsphinx.Config
import edu.cmu.pocketsphinx.SpeechRecognizer
import edu.cmu.pocketsphinx.SpeechRecognizerSetup

class SpeechRecognizerWithStartCallback(config: Config) : SpeechRecognizer(config) {
    var startCallback: (() -> Unit)? = null

    override fun startListening(searchName: String?): Boolean {
        val b = super.startListening(searchName)
        startCallback?.invoke()
        return b
    }

    override fun startListening(searchName: String?, timeout: Int): Boolean {
        val b = super.startListening(searchName, timeout)
        startCallback?.invoke()
        return b
    }
}

fun SpeechRecognizerSetup.getConfig(): Config {
    val config = SpeechRecognizerSetup::class.java.getDeclaredField("config")
    config.isAccessible = true

    return config[this] as Config
}