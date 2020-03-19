package com.meranged.keeprel.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class Event(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "date_start")
    var date_start: Long = 0L,

    @ColumnInfo(name = "date_finish")
    var date_finish: Long = 0L,

    @ColumnInfo(name = "event_type")
    var event_type: Int = 0,

    @ColumnInfo(name = "uid")
    var uid: String = "",

    @ColumnInfo(name = "congrats_type")
    var congrats_type: Int = 0,

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "comment")
    var comment: String = ""
)