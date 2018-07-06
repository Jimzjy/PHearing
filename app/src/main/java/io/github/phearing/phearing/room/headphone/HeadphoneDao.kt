package io.github.phearing.phearing.room.headphone

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HeadphoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(headphone: Headphone)

    @Update
    fun update(headphone: Headphone)

    @Delete
    fun delete(headphone: Headphone)

    @Query("SELECT * FROM headphone ORDER BY create_time DESC")
    fun loadAll(): LiveData<List<Headphone>>
}