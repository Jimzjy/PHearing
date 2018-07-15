package io.github.phearing.phearing.room.headphone

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room
import io.github.phearing.phearing.common.PHApplication

const val DELETE_FLAG = 0
const val INSERT_FLAG = 1
const val UPDATE_FLAG = 2

class HeadphoneRepo {
    private val mHeadphoneDao: HeadphoneDao = Room.databaseBuilder(PHApplication.instance.applicationContext,
            HeadphoneDatabase::class.java, "headphone_database")
            .build().headphoneDao()
    val allHeadphones: LiveData<List<Headphone>> by lazy {
        mHeadphoneDao.loadAll()
    }

    fun insertHeadphones(vararg headphones: Headphone) {
        PrimaryHeadphonesTask(mHeadphoneDao, INSERT_FLAG).execute(*headphones)
    }

    fun deleteHeadphones(vararg headphones: Headphone) {
        PrimaryHeadphonesTask(mHeadphoneDao, DELETE_FLAG).execute(*headphones)
    }

    private class PrimaryHeadphonesTask(private val mDao: HeadphoneDao, private val mFlag: Int)
        : AsyncTask<Headphone, Unit, Unit>() {

        override fun doInBackground(vararg p0: Headphone?) {
            when(mFlag) {
                DELETE_FLAG -> {
                    p0.forEach { it?.let { mDao.delete(it) } }
                }
                INSERT_FLAG -> {
                    p0.forEach { it?.let { mDao.insert(it) } }
                }
                UPDATE_FLAG -> {
                    p0.forEach { it?.let { mDao.update(it) } }
                }
            }
        }
    }
}