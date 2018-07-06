package io.github.phearing.phearing.room.audiometry

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AudiometryData::class], version = 1)
abstract class AudiometryDatabase : RoomDatabase() {

    abstract fun audiometryDataDao(): AudiometryDataDao
}