package com.meranged.keeprel.db

import android.util.Log
import androidx.room.*
import biweekly.ICalendar
import com.meranged.keeprel.getKRCalendarFromICalendar
import com.meranged.keeprel.getKREventFromVEvent

@Dao
interface KRDao {
    @Insert
    fun insert(calendar: KRCalendar):Long

    @Insert
    fun insert(event: KREvent):Long

    @Update
    fun update(calendar: KRCalendar)

    @Update
    fun update(event: KREvent)

    @Query("SELECT * FROM event WHERE uid = :key")
    fun getEventByUid(key: String): KREvent?

    @Query("SELECT * FROM event WHERE id = :key")
    fun getEventById(key: Long): KREvent?

    @Query("DELETE FROM event WHERE id = :key")
    fun deleteEvent(key: Long)

    @Query("DELETE FROM event WHERE uid = :key")
    fun deleteEvent(key: String)

    @Query("SELECT * FROM calendar WHERE id = :key")
    fun getCalendarById(key: Long): KRCalendar?

    @Query("DELETE FROM calendar WHERE id = :key")
    fun deleteCalendar(key: Long)

    @Query("DELETE FROM calendar")
    fun deleteAllCalendars()

    @Transaction
    fun insertCalendarWithEvents(ical: ICalendar){
        val kr_cal = getKRCalendarFromICalendar(ical)

        val id = insert(kr_cal)
        if (id > 0){
            for (e in ical.events){
                val kr_event = getKREventFromVEvent(e)
                kr_event.calendar_id = id
                insert(kr_event)
                Log.i("KRLog, event = ", kr_event.toString())
            }
        }
    }

}