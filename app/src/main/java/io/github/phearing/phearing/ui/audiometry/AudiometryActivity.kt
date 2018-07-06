package io.github.phearing.phearing.ui.audiometry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import io.github.phearing.phearing.R

class AudiometryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audiometry)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.audiometry_container, AudiometryFragment.newInstance())
                    .commitNow()
        }
        val toolbar = findViewById<Toolbar>(R.id.audiometry_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}
