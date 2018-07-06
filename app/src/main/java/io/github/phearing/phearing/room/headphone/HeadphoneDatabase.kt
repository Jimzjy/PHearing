package io.github.phearing.phearing.room.headphone

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Headphone::class], version = 1)
abstract class HeadphoneDatabase : RoomDatabase() {

    abstract fun headphoneDao(): HeadphoneDao
}