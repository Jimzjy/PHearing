package io.github.phearing.phearing.ui.headphone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.github.phearing.phearing.R
import kotlinx.android.synthetic.main.activity_measure.*

class HeadphoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)
        setSupportActionBar(measure_toolbar)
        measure_toolbar.setNavigationOnClickListener { onBackPressed() }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.measure_container, HeadphoneDataFragment.newInstance())
                    .commitNow()
        }
    }

    fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.measure_container, fragment)
                .addToBackStack(null)
                .commit()
    }
}
