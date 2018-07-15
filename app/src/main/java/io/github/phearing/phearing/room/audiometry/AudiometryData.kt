package io.github.phearing.phearing.room.audiometry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audiometry_data")
data class AudiometryData (
        var id: Int,

        @PrimaryKey
        @ColumnInfo(name = "create_time")
        val createTime: Long,

        @ColumnInfo(name = "right_data")
        val rightData: String,

        @ColumnInfo(name = "left_data")
        val leftData: String,

        @ColumnInfo(name = "right_result")
        val rightResult: Float,

        @ColumnInfo(name = "left_result")
        val leftResult: Float
)