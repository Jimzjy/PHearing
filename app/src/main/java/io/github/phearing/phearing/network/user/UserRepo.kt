package io.github.phearing.phearing.network.user

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.common.Token
import io.github.phearing.phearing.common.User
import io.github.phearing.phearing.room.audiometry.AudiometryData
import io.github.phearing.phearing.room.speech.SpeechData
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class UserRepo {
    private val mUserService = UserService.create()

    val token = MutableLiveData<Token>()
    val user = MutableLiveData<User>()
    val avatar = MutableLiveData<Drawable>()
    val audiometryDataLiveData = MutableLiveData<AudiometryData>()
    val speechDataLiveData = MutableLiveData<SpeechData>()

    fun login(username: String, password: String) {
        mUserService.login(username, password).enqueue(object : Callback<Token> {
            override fun onFailure(call: Call<Token>?, t: Throwable?) {
                makeToast(R.string.auth_login_failed, true)
                token.value = null
                Log.e("login", "${t?.message}")
            }

            override fun onResponse(call: Call<Token>?, response: Response<Token>?) {
                response?.let {
                    if (!it.isSuccessful) {
                        makeToast(R.string.auth_login_failed, true)
                        token.value = null
                        return@let
                    }
                    makeToast(R.string.auth_login_successful, false)
                    token.value = it.body()
                }
            }
        })
    }

    fun register(mutableMap: MutableMap<String, Any>) {
        mUserService.register(mutableMap).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>?, t: Throwable?) {
                makeToast(R.string.auth_register_failed, true)
                user.value = null
                Log.e("register", "${t?.message}")
            }

            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                response?.let {
                    if (!it.isSuccessful) {
                        makeToast(R.string.auth_register_failed, true)
                        user.value = null
                        return@let
                    }
                    makeToast(R.string.auth_register_successful, false)
                    user.value = it.body()
                }
            }
        })
    }

    fun update(map: MutableMap<String, Any>) {
        val id = getUserId()
        if (id == -1) {
            return
        }
        mUserService.update(getToken(), id, map).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>?, t: Throwable?) {
                makeToast(R.string.auth_change_data_failed, true)
                user.value = null
                Log.e("update", "${t?.message}")
            }

            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                response?.let {
                    if (!it.isSuccessful) {
                        makeToast(R.string.auth_change_data_failed, true)
                        user.value = null
                        return@let
                    }
                    makeToast(R.string.auth_change_data_successful, false)
                    user.value = it.body()
                }
            }
        })
    }

    fun getUser() {
        val id = getUserId()
        if (id == -1) {
            return
        }
        mUserService.getUser(getToken(), id).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>?, t: Throwable?) {
                makeToast(R.string.auth_getUser_failed, true)
                user.value = null
                Log.e("getUser", "${t?.message}")
            }

            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                response?.let {
                    if (!it.isSuccessful) {
                        makeToast(R.string.auth_getUser_failed, true)
                        user.value = null
                        return@let
                    }
                    makeToast(R.string.auth_getUser_successful, false)
                    user.value = it.body()
                }
            }
        })
    }

    fun getAvatar(url: String) {
        mUserService.getImage(url).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                makeToast(R.string.auth_getAvatar_failed, true)
                avatar.value = null
                Log.e("avatar", "${t?.message}")
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.let {
                    if (!it.isSuccessful) {
                        makeToast(R.string.auth_getAvatar_failed, true)
                        avatar.value = null
                        return@let
                    }
                    val file = File(PHApplication.instance.getExternalFilesDir(null), url.split("/").last())
                    if (file.exists()) file.delete()
                    var outputStream: FileOutputStream? = null
                    var inputStream: InputStream? = null
                    try {
                        val buf = ByteArray(4096)
                        outputStream = FileOutputStream(file)

                        it.body()?.let {
                            inputStream = it.byteStream()

                            while (true) {
                                val i: Int = inputStream?.read(buf) ?: -1
                                if (i == -1) break
                                outputStream.write(buf, 0, i)
                            }
                            outputStream.flush()

                            val bitmapDrawable = BitmapDrawable(PHApplication.instance.resources, file.path)
                            val editor = PHApplication.instance.getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
                            editor.putString("avatar", file.absolutePath)
                            editor.apply()
                            makeToast(R.string.auth_getAvatar_successful, false)
                            avatar.value = bitmapDrawable
                        }
                    } catch (e: Exception) {
                        makeToast(R.string.auth_getAvatar_failed, true)
                        e.printStackTrace()
                        avatar.value = null
                    } finally {
                        outputStream?.close()
                        inputStream?.close()
                    }
                }
            }
        })
    }

    fun updateAvatar(file: File) {
        val id = getUserId()
        if (id == -1) {
            return
        }
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
        mUserService.updateAvatar(getToken(), id, body).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                makeToast(R.string.auth_update_avatar_failed, true)
                Log.e("avatar", "${t?.message}")
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.let {
                    if (!it.isSuccessful) {
                        makeToast(R.string.auth_update_avatar_failed, true)
                        return@let
                    }
                    val editor = PHApplication.instance.getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
                    editor.putString("avatar", file.absolutePath)
                    editor.apply()
                    makeToast(R.string.auth_update_avatar_successful, false)
                    avatar.value = BitmapDrawable(PHApplication.instance.resources, file.absolutePath)
                }
            }
        })
    }

    fun uploadAudiometryData(audiometryData: AudiometryData) {
        mUserService.uploadAudiometryData(getToken(), audiometryData).enqueue(object : Callback<AudiometryData> {
            override fun onFailure(call: Call<AudiometryData>?, t: Throwable?) {
                Log.e("uploadAudiometryData", "${t?.message}")
            }

            override fun onResponse(call: Call<AudiometryData>?, response: Response<AudiometryData>?) {
                response?.body()?.let {
                    audiometryDataLiveData.value = it
                }
            }
        })
    }

    fun uploadSpeechData(speechData: SpeechData) {
        mUserService.uploadSpeechData(getToken(), speechData).enqueue(object : Callback<SpeechData> {
            override fun onFailure(call: Call<SpeechData>?, t: Throwable?) {
                Log.e("uploadSpeechData", "${t?.message}")
            }

            override fun onResponse(call: Call<SpeechData>?, response: Response<SpeechData>?) {
                response?.body()?.let {
                    speechDataLiveData.value = it
                }
            }
        })
    }

    private fun makeToast(resId: Int, isLong: Boolean) {
        if (isLong) {
            Toast.makeText(PHApplication.instance.applicationContext,
                    PHApplication.instance.applicationContext.getString(resId),
                    Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(PHApplication.instance.applicationContext,
                    PHApplication.instance.applicationContext.getString(resId),
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSharedPreferences()
            = PHApplication.instance.getSharedPreferences("userData", Context.MODE_PRIVATE)

    private fun getUserId(): Int {
        var id: Int = getSharedPreferences().getInt("userId", -1)
        if (id == -1) {
            id = token.value?.userId ?: -1
            if (id == -1) {
                id = user.value?.id ?: -1
            }
        }
        if (id == -1) {
            makeToast(R.string.auth_getId_failed, true)
        }
        return id
    }

    private fun getToken(): String {
        var tmp: String = getSharedPreferences().getString("token", "")
        if (tmp.isEmpty()) {
            tmp = token.value?.token ?: ""
        }
        return "Token $tmp"
    }
}