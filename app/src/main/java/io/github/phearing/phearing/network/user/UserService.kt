package io.github.phearing.phearing.network.user

import io.github.phearing.phearing.common.User
import retrofit2.Call
import retrofit2.http.POST

interface UserService {
    companion object {

    }

    @POST("users")
    fun register(): Call<User>
}