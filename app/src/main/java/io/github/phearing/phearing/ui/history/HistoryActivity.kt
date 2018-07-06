package io.github.phearing.phearing.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.github.phearing.phearing.R
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.history_container, HistoryFragment.newInstance())
                    .commitNow()
        }
        setSupportActionBar(history_toolbar)
        history_toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.history_container, fragment)
                .addToBackStack(null)
                .commit()
    }
}
