package io.github.phearing.phearing.ui.auth

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.User
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setSupportActionBar(auth_toolbar)
        auth_toolbar.setNavigationOnClickListener { onBackPressed() }

        if (savedInstanceState == null) {
            if (!isLogin()) {
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

    fun navigateTo(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
                .replace(R.id.auth_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun saveUserData(user: User) {
        val editor = applicationContext.getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
        editor.putString("username", user.username)
        editor.putInt("birthYear", user.birthYear)
        editor.putString("phoneNumber", user.phoneNumber)
        editor.putBoolean("sex", user.sex)
        editor.putBoolean("deaf", user.deaf)
        editor.putBoolean("hearingAid", user.hearingAid)
        editor.putBoolean("cochlearImplant", user.cochlearImplant)
        editor.apply()
    }

    private fun isLogin(): Boolean {
        val sharedPreferences = applicationContext.getSharedPreferences("userData", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        return token.isNotEmpty()
    }
}
