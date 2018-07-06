package io.github.phearing.phearing.room.audiometry

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AudiometryDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(audiometryData: AudiometryData)

    @Update
    fun update(audiometryData: AudiometryData)

    @Delete
    fun delete(audiometryData: AudiometryData)

    @Query("SELECT * FROM audiometry_data ORDER BY create_time DESC")
    fun loadAll(): LiveData<List<AudiometryData>>

    @Query("SELECT * FROM audiometry_data WHERE create_time < :time ORDER BY create_time DESC LIMIT 0,:num")
    fun loadNext(time: Long, num: Int): LiveData<List<AudiometryData>>
}