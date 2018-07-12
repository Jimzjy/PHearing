package io.github.phearing.phearing.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.github.phearing.phearing.R
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private var isLogin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setSupportActionBar(auth_toolbar)
        auth_toolbar.setNavigationOnClickListener { onBackPressed() }

        if (savedInstanceState == null) {
            if (!isLogin) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.auth_container, LoginFragment.newInstance())
                        .commitNow()
            } else {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.auth_container, UserFragment.newInstance())
                        .commitNow()
            }
        }
    }

    fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.auth_container, fragment)
                .addToBackStack(null)
                .commit()
    }
}
