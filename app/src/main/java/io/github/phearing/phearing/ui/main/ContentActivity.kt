package io.github.phearing.phearing.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.phearing.phearing.R
import kotlinx.android.synthetic.main.activity_content.*

class ContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        content_toolbar.setOnClickListener { onBackPressed() }

        if (savedInstanceState == null) {
            val url = intent.getStringExtra("url")
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_container, ContentFragment.newInstance(url))
                    .commitNow()
        }
    }
}
