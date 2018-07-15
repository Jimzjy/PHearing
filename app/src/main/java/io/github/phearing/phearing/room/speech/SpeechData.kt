package io.github.phearing.phearing.room.speech

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "speech_data")
data class SpeechData (
        var id: Int,

        @PrimaryKey
        @ColumnInfo(name = "create_time")
        val createTime: Long,

        @ColumnInfo(name = "score")
        val score: Int,

        @ColumnInfo(name = "table_no")
        val tableNo: Int
)