package io.github.phearing.phearing.room.audiometry

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.room.headphone.DELETE_FLAG
import io.github.phearing.phearing.room.headphone.INSERT_FLAG
import io.github.phearing.phearing.room.headphone.UPDATE_FLAG


class AudiometryDataRepo {
    private val mAudiometryDataDao: AudiometryDataDao = Room.databaseBuilder(PHApplication.instance.applicationContext,
            AudiometryDatabase::class.java, "audiometry_data_database")
            .build().audiometryDataDao()
    val allAudiometryData: LiveData<List<AudiometryData>> by lazy {
        mAudiometryDataDao.loadAll()
    }

    fun insertAudiometryData(vararg audiometryData: AudiometryData) {
        PrimaryAudiometryDataTask(mAudiometryDataDao, INSERT_FLAG).execute(*audiometryData)
    }

    fun deleteAudiometryData(vararg audiometryData: AudiometryData) {
        PrimaryAudiometryDataTask(mAudiometryDataDao, DELETE_FLAG).execute(*audiometryData)
    }

    fun loadNextAudiometryData(time: Long, num: Int): LiveData<List<AudiometryData>> {
        return mAudiometryDataDao.loadNext(time, num)
    }

    fun updateAudiometryData(vararg audiometryData: AudiometryData) {
        PrimaryAudiometryDataTask(mAudiometryDataDao, UPDATE_FLAG).execute(*audiometryData)
    }

    private class PrimaryAudiometryDataTask(private val mDao: AudiometryDataDao, private val mFlag: Int)
        : AsyncTask<AudiometryData, Unit, Unit>() {

        override fun doInBackground(vararg p0: AudiometryData?) {
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