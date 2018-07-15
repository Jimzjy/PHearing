package io.github.phearing.phearing.room.speech

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.room.headphone.DELETE_FLAG
import io.github.phearing.phearing.room.headphone.INSERT_FLAG
import io.github.phearing.phearing.room.headphone.UPDATE_FLAG

class SpeechDataRepo {
    private val mSpeechDataDao: SpeechDataDao = Room.databaseBuilder(PHApplication.instance.applicationContext,
            SpeechDatabase::class.java, "speech_data_database").build().speechDataDao()
    val allSpeechData: LiveData<List<SpeechData>> by lazy {
        mSpeechDataDao.loadAll()
    }

    fun insertSpeechData(vararg speechData: SpeechData) {
        PrimarySpeechDataTask(mSpeechDataDao, INSERT_FLAG).execute(*speechData)
    }

    fun deleteSpeechData(vararg speechData: SpeechData) {
        PrimarySpeechDataTask(mSpeechDataDao, DELETE_FLAG).execute(*speechData)
    }

    fun loadNextSpeechData(time: Long, num: Int): LiveData<List<SpeechData>> {
        return mSpeechDataDao.loadNext(time, num)
    }

    fun updateSpeechData(vararg speechData: SpeechData) {
        PrimarySpeechDataTask(mSpeechDataDao, UPDATE_FLAG).execute(*speechData)
    }

    private class PrimarySpeechDataTask(private val mDao: SpeechDataDao, private val mFlag: Int)
        : AsyncTask<SpeechData, Unit, Unit>() {

        override fun doInBackground(vararg p0: SpeechData?) {
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