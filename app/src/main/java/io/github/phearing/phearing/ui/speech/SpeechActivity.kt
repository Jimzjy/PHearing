package io.github.phearing.phearing.ui.speech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.github.phearing.phearing.R
import kotlinx.android.synthetic.main.activity_speech.*

class SpeechActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speech)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.speech_container, SpeechFragment.newInstance())
                    .commitNow()
        }
        setSupportActionBar(speech_toolbar)
        speech_toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.speech_container, fragment)
                .addToBackStack(null)
                .commit()
    }
}
