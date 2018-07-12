package io.github.phearing.phearing.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordRe = MutableLiveData<String>()
    val birth = MutableLiveData<String>()
    val sexMale = MutableLiveData<Boolean>()
    val sexFemale = MutableLiveData<Boolean>()
    val deaf = MutableLiveData<Boolean>()
    val hearingAid = MutableLiveData<Boolean>()
    val cochlearImplant = MutableLiveData<Boolean>()

    init {
        username.value = ""
        password.value = ""
        passwordRe.value = ""
        birth.value = ""
        sexMale.value = true
        sexFemale.value = false
        deaf.value = false
        hearingAid.value = false
        cochlearImplant.value = false
    }
}
