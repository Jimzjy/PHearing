package io.github.phearing.phearing.ui.auth

import android.app.Application
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.common.User
import io.github.phearing.phearing.network.user.UserRepo
import java.io.*
import java.lang.reflect.Field
import java.net.URI
import java.nio.file.Paths
import javax.inject.Inject

class UserViewModel(application: Application) : AndroidViewModel(application) {
    val username = MutableLiveData<String>()
    val birthYear = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val sex = MutableLiveData<String>()
    val deaf = MutableLiveData<String>()
    val hearingAid = MutableLiveData<String>()
    val cochlearImplant = MutableLiveData<String>()
    val user: LiveData<User>
    val avatar: LiveData<Drawable>

    @Inject
    lateinit var userRepo: UserRepo

    init {
        DaggerUserComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .build().inject(this)
        user = userRepo.user
        avatar = userRepo.avatar

        if (user.value == null) {
            val sharedPreferences = application.getSharedPreferences("userData", Context.MODE_PRIVATE)

            username.value = sharedPreferences.getString("username", "")
            val year = sharedPreferences.getInt("birthYear", -1)
            if (year == -1) {
                birthYear.value = ""
            } else {
                birthYear.value = year.toString()
            }
            phoneNumber.value = sharedPreferences.getString("phoneNumber", "")
            sex.value = getSexString(sharedPreferences.getBoolean("sex", false))
            deaf.value = getBooleanString(sharedPreferences.getBoolean("deaf", false))
            hearingAid.value = getBooleanString(sharedPreferences.getBoolean("hearingAid", false))
            cochlearImplant.value = getBooleanString(sharedPreferences.getBoolean("cochlearImplant", false))
        }

        if (avatar.value == null) {
            val sharedPreferences = application.getSharedPreferences("userData", Context.MODE_PRIVATE)
            val fileName = sharedPreferences.getString("avatar", "")
            if (fileName.isNotEmpty()) {
                val file = File(fileName)
                if (file.exists()) {
                    avatar.value = BitmapDrawable(getApplication<PHApplication>().resources, file.path)
                }
            }
        }
    }

    fun signOut() {
        val editor = getApplication<PHApplication>()
                .getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }

    fun refreshUserData() {
        user.value?.let {
            username.value = it.username
            birthYear.value = it.birthYear.toString()
            phoneNumber.value = it.phoneNumber
            sex.value = getSexString(it.sex)
            deaf.value = getBooleanString(it.deaf)
            hearingAid.value = getBooleanString(it.hearingAid)
            cochlearImplant.value = getBooleanString(it.cochlearImplant)
        }
    }

    fun refreshUserDataFromNetwork() {
        userRepo.getUser()
    }

    fun updateAvatar(uri: Uri) {
        val sharedPreferences = getApplication<PHApplication>().getSharedPreferences("userData", Context.MODE_PRIVATE)
        val extension = uri.path.substring(uri.path.lastIndexOf('.'))

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = getApplication<PHApplication>().contentResolver.openInputStream(uri)
            val file = File(getApplication<PHApplication>().getExternalFilesDir(null),
                    "avatar_${sharedPreferences.getString("username", "")}$extension")
            if (file.exists()) file.delete()
            outputStream = FileOutputStream(file)
            val buf = ByteArray(4096)
            while (true) {
                val i = inputStream.read(buf) ?: -1
                if (i == -1) break
                outputStream.write(buf, 0, i)
            }
            outputStream.flush()
            userRepo.updateAvatar(file)
        } catch (e: Exception) {
            Toast.makeText(getApplication<PHApplication>(),
                    getApplication<PHApplication>().resources.getString(R.string.auth_update_avatar_failed),
                    Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } finally {
            outputStream?.close()
            inputStream?.close()
        }
    }

    private fun getBooleanString(bool: Boolean): String {
        return if (bool) {
            getApplication<PHApplication>().getString(R.string.true_ph)
        } else {
            getApplication<PHApplication>().getString(R.string.false_ph)
        }
    }

    private fun getSexString(bool: Boolean): String {
        return if (bool){
            getApplication<PHApplication>().getString(R.string.auth_male)
        } else {
            getApplication<PHApplication>().getString(R.string.auth_female)
        }
    }
}
