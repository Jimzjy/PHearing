package io.github.phearing.phearing.ui.auth

import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import dagger.Component
import io.github.phearing.phearing.common.ApplicationComponent
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.common.Token
import io.github.phearing.phearing.common.User
import io.github.phearing.phearing.network.user.UserRepo
import javax.inject.Inject
import javax.inject.Scope

class LoginViewModel : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val token: LiveData<Token>
    val user: LiveData<User>
    val avatar: LiveData<Drawable>

    @Inject
    lateinit var userRepo: UserRepo

    init {
        DaggerUserComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .build().inject(this)

        username.value = ""
        password.value = ""
        token = userRepo.token
        user = userRepo.user
        avatar = userRepo.avatar
    }

    fun login() {
        userRepo.login(username.value!!, password.value!!)
    }

    fun getUser() {
        userRepo.getUser()
    }

    fun getAvatar() {
        userRepo.getAvatar(user.value?.avatar ?: "jc.jpg")
    }
}

@UserScope
@Component(dependencies = [ApplicationComponent::class])
interface UserComponent {
    fun inject(loginViewModel: LoginViewModel)
    fun inject(dataChangeViewModel: DataChangeViewModel)
    fun inject(userViewModel: UserViewModel)
    fun inject(registerViewModel: RegisterViewModel)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class UserScope
