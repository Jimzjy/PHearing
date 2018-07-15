package io.github.phearing.phearing.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.common.User
import io.github.phearing.phearing.network.user.UserRepo
import javax.inject.Inject

class RegisterViewModel : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordRe = MutableLiveData<String>()
    val birth = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val sexMale = MutableLiveData<Boolean>()
    val sexFemale = MutableLiveData<Boolean>()
    val deaf = MutableLiveData<Boolean>()
    val hearingAid = MutableLiveData<Boolean>()
    val cochlearImplant = MutableLiveData<Boolean>()
    val user: LiveData<User>

    @Inject
    lateinit var userRepo: UserRepo

    init {
        DaggerUserComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .build().inject(this)
        user = userRepo.user

        username.value = ""
        password.value = ""
        passwordRe.value = ""
        birth.value = ""
        phoneNumber.value = ""
        sexMale.value = true
        sexFemale.value = false
        deaf.value = false
        hearingAid.value = false
        cochlearImplant.value = false
    }

    fun register() {
        val map = mutableMapOf(
                Pair("username", username.value ?: ""),
                Pair("password", password.value ?: ""),
                Pair("birthYear", birth.value?.toInt() ?: 1970),
                Pair("phoneNumber", phoneNumber.value ?: ""),
                Pair("sex", sexMale.value ?: true),
                Pair("deaf", deaf.value ?: false),
                Pair("hearingAid", hearingAid.value ?: false),
                Pair("cochlearImplant", cochlearImplant.value ?: false)
        )

        userRepo.register(map)
    }
}
