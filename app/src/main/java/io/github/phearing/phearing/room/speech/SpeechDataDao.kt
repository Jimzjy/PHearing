package io.github.phearing.phearing.room.speech

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SpeechDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(speechData: SpeechData)

    @Update
    fun update(speechData: SpeechData)

    @Delete
    fun delete(speechData: SpeechData)

    @Query("SELECT * FROM speech_data ORDER BY create_time DESC")
    fun loadAll(): LiveData<List<SpeechData>>

    @Query("SELECT * FROM speech_data WHERE create_time < :time ORDER BY create_time DESC LIMIT 0,:num")
    fun loadNext(time: Long, num: Int): LiveData<List<SpeechData>>
}