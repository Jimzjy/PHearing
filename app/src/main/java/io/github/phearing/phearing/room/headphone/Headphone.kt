package io.github.phearing.phearing.room.headphone

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Headphone(
        @PrimaryKey
        val name: String,

        @ColumnInfo(name = "left_volume")
        val leftVolume: String,

        @ColumnInfo(name = "right_volume")
        val rightVolume: String,

        @ColumnInfo(name = "create_time")
        val createTime: Long
)