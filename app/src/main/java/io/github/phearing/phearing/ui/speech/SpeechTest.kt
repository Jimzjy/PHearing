package io.github.phearing.phearing.ui.speech


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer

import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.recognition.SpeechRecognizerPH
import kotlinx.android.synthetic.main.fragment_speech_test.view.*


class SpeechTest : Fragment() {
    companion object {
        fun newInstance() = SpeechTest()
    }

    private val mRecognizer = SpeechRecognizerPH()
    private var mTextView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRecognizer.errorCallBack = {
            Log.e("", "error $it")
        }
        mRecognizer.timeoutCallBack = {
            Log.e("", "timeout")
        }
        mRecognizer.resultCallBack = {
            Log.e("", it)
            mRecognizer.stop()
            mTextView?.text = it
        }

        mRecognizer.isStart.observe(this, Observer {
            if (it) {
                Log.e("", "start")
                mTextView?.text = "..."
            } else {
                Log.e("", "stop")
            }
        })

        return inflater.inflate(R.layout.fragment_speech_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.speech_start.setOnClickListener {
            mRecognizer.start()
        }
        mTextView = view.findViewById(R.id.speech_text)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mRecognizer.init(1)
        mRecognizer.start()
    }
}
