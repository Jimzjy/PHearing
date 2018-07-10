package io.github.phearing.phearing.room.speech

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SpeechData::class], version = 1)
abstract class SpeechDatabase : RoomDatabase() {
    abstract fun speechDataDao(): SpeechDataDao
}