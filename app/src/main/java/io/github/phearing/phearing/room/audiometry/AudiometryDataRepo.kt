package io.github.phearing.phearing.room.audiometry

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room
import io.github.phearing.phearing.common.PHApplication
import io.github.phearing.phearing.room.headphone.DELETE_FLAG
import io.github.phearing.phearing.room.headphone.INSERT_FLAG
import io.github.phearing.phearing.room.headphone.UPDATE_FLAG


class AudiometryDataRepo {
    private val audiometryDataDao: AudiometryDataDao = Room.databaseBuilder(PHApplication.instance.applicationContext,
            AudiometryDatabase::class.java, "audiometry_data_database")
            .build().audiometryDataDao()
    val allAudiometryData: LiveData<List<AudiometryData>> by lazy {
        audiometryDataDao.loadAll()
    }

    fun insertAudiometryData(vararg audiometryData: AudiometryData) {
        PrimaryAudiometryDataTask(audiometryDataDao, INSERT_FLAG).execute(*audiometryData)
    }

    fun deleteAudiometryData(vararg audiometryData: AudiometryData) {
        PrimaryAudiometryDataTask(audiometryDataDao, DELETE_FLAG).execute(*audiometryData)
    }

    fun loadNextAudiometryData(time: Long, num: Int): LiveData<List<AudiometryData>> {
        return audiometryDataDao.loadNext(time, num)
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