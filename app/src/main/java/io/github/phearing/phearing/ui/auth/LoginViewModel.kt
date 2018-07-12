package io.github.phearing.phearing.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    init {
        username.value = ""
        password.value = ""
    }
}
