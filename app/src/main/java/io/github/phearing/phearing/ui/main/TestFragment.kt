package io.github.phearing.phearing.ui.main


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.github.phearing.phearing.R
import io.github.phearing.phearing.ui.audiometry.AudiometryActivity
import io.github.phearing.phearing.ui.speech.SpeechActivity
import kotlinx.android.synthetic.main.fragment_test.view.*


class TestFragment : Fragment() {
    companion object {
        fun newInstance() = TestFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.main_test_am_bt.setOnClickListener {
            startActivity(Intent(context, AudiometryActivity::class.java))
        }
        view.main_test_spam_bt.setOnClickListener {
            startActivity(Intent(context, SpeechActivity::class.java))
        }
    }
}
