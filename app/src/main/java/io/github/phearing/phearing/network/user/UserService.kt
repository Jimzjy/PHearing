package io.github.phearing.phearing.network.user

import io.github.phearing.phearing.common.Token
import io.github.phearing.phearing.common.User
import io.github.phearing.phearing.room.audiometry.AudiometryData
import io.github.phearing.phearing.room.speech.SpeechData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "http://jimzjy.com/"

interface UserService {
    companion object {
        fun create(): UserService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(UserService::class.java)
        }
    }

    @POST("api-token-auth/")
    @FormUrlEncoded
    fun login(@Field("username") username: String, @Field("password") password: String): Call<Token>

    @POST("users/")
    fun register(@Body map: MutableMap<String, Any>): Call<User>

    @GET("users/{id}/")
    fun getUser(@Header("Authorization") token: String, @Path("id") id: Int): Call<User>

    @PATCH("users/{id}/")
    fun update(@Header("Authorization") token: String, @Path("id") id: Int, @Body map: MutableMap<String, Any>): Call<User>

    @POST("audiometry-data/")
    fun uploadAudiometryData(@Header("Authorization") token: String, @Body audiometryData: AudiometryData): Call<AudiometryData>

    @POST("speech-data/")
    fun uploadSpeechData(@Header("Authorization") token: String, @Body speechData: SpeechData): Call<SpeechData>

    @GET
    fun getImage(@Url url: String): Call<ResponseBody>

    @Multipart
    @PATCH("users/{id}/")
    fun updateAvatar(@Header("Authorization") token: String, @Path("id") id: Int, @Part file: MultipartBody.Part): Call<ResponseBody>
}