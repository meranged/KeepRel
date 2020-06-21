package com.meranged.keeprel.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar")
data class KRCalendar(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "locale")
    var locale: String? = "",

    @ColumnInfo(name = "description")
    var description: String? = "",

    @ColumnInfo(name = "comment")
    var comment: String? = "",

    @ColumnInfo(name = "color")
    var color: Int? = 0,

    @ColumnInfo(name = "person_id")
    var person_id: Long? = 0L
)