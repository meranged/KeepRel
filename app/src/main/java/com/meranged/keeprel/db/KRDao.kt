package com.meranged.keeprel.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update

@Dao
interface KRDao {
    @Insert
    fun insert(calendar: Calendar)

    @Insert
    fun insert(event: Event)

    @Update
    fun update(calendar: Calendar)

    @Update
    fun update(event: Event)

}