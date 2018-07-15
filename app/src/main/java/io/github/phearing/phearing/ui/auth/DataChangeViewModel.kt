package io.github.phearing.phearing.ui.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.common.User
import io.github.phearing.phearing.network.user.UserRepo
import javax.inject.Inject


class DataChangeViewModel(application: Application) : AndroidViewModel(application) {
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

        val sharedPreferences = application.getSharedPreferences("userData", Context.MODE_PRIVATE)
        // true: male, false: female
        val sex = sharedPreferences.getBoolean("sex", true)

        password.value = ""
        passwordRe.value = ""
        val year = sharedPreferences.getInt("birthYear", -1)
        birth.value = if (year == -1) {
            ""
        } else {
            year.toString()
        }
        phoneNumber.value = sharedPreferences.getString("phoneNumber", "")
        if (sex) {
            sexMale.value = true
            sexFemale.value = false
        } else {
            sexMale.value = false
            sexFemale.value = true
        }
        deaf.value = sharedPreferences.getBoolean("deaf", false)
        hearingAid.value = sharedPreferences.getBoolean("hearingAid", false)
        cochlearImplant.value = sharedPreferences.getBoolean("cochlearImplant", false)

        user = userRepo.user
    }

    fun changeData() {
        val map = mutableMapOf<String, Any>(
                Pair("deaf", deaf.value ?: false),
                Pair("hearingAid", hearingAid.value ?: false),
                Pair("cochlearImplant", cochlearImplant.value ?: false)
        )
        password.value?.let {
            if (it.isNotEmpty()) map["password"] = it
        }
        phoneNumber.value?.let {
            if (it.isNotEmpty()) map["phoneNumber"] = it
        }
        birth.value?.let {
            if (it.isNotEmpty()) map["birthYear"] = it.toInt()
        }
        map["sex"] = sexMale.value ?: true

        userRepo.update(map)
    }
}
