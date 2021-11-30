package com.meranged.keeprel

import android.net.Uri
import android.util.Log
import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.io.text.ICalReader
import com.meranged.keeprel.db.KRCalendar
import com.meranged.keeprel.db.KREvent
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

fun readCalendarFromUri(afile: Uri?): ICalendar?{
    if (afile != null) {

        val ical: ICalendar?

        val fileInputStream: InputStream? = App.context!!.contentResolver.openInputStream(afile)

        val reader = ICalReader(fileInputStream)

        try {
            ical = reader.readNext()
        } finally {
            reader.close()
        }
        return ical
    }
    return null
}

fun getKRCalendarFromICalendar(cal: ICalendar): KRCalendar {

    val krCal = KRCalendar()

    var calName: String?
    var calDesc: String?

    try {
        calName = cal.getExperimentalProperty("X-WR-CALNAME").value
    } catch (e: Exception){
        val sdf = SimpleDateFormat("ddMyyyyhhmmss")
        val currentDate = sdf.format(Date())
        calName = "Calendar$currentDate"
    }

    try {
        calDesc = cal.getExperimentalProperty("X-WR-CALDESC").value
    } catch (e: Exception){
        calDesc = ""
    }

    krCal.name = calName!!
    krCal.description = calDesc

    krCal.locale = Locale.getDefault().displayLanguage

    return krCal
}

fun insertKRCalendar(ical: KRCalendar): KRCalendar {
    val id = App.database!!.dao.insert(ical)
    ical.id = id
    return ical
}

fun getKREventFromVEvent(ev: VEvent): KREvent {
    val krEvent = KREvent()

    val dateStart = GregorianCalendar.getInstance()
    val dateEnd = GregorianCalendar.getInstance()
    dateStart.time = ev.dateStart.value

    if (ev.dateEnd != null)
        dateEnd.time = ev.dateEnd.value

    dateStart.set(Calendar.HOUR, 0)
    dateStart.set(Calendar.MINUTE, 0)
    dateStart.set(Calendar.SECOND, 0)

    krEvent.date_start = dateStart.timeInMillis

    //check if we deal with 1 day event
    if (ev.dateEnd == null) {
        krEvent.date_end = 0
    } else {
        dateEnd.time = ev.dateEnd.value

        if ((dateStart == dateEnd) or (dateStart.after(dateEnd))) {
            krEvent.date_end = 0
        } else {
            if ((dateStart.get(Calendar.DAY_OF_MONTH) == dateEnd.get(Calendar.DAY_OF_MONTH))
                && (dateStart.get(Calendar.YEAR) == dateEnd.get(Calendar.YEAR))
                && (dateStart.get(Calendar.MONTH) == dateEnd.get(Calendar.MONTH))){
                krEvent.date_end = 0
            } else {
                dateStart.add(Calendar.DAY_OF_MONTH, 1)

                if (((dateStart.get(Calendar.DAY_OF_MONTH) == dateEnd.get(Calendar.DAY_OF_MONTH))
                            && (dateStart.get(Calendar.YEAR) == dateEnd.get(Calendar.YEAR))
                            && (dateStart.get(Calendar.MONTH) == dateEnd.get(Calendar.MONTH)))
                    && (dateEnd.get(Calendar.HOUR_OF_DAY) < 2)){
                    krEvent.date_end = 0
                } else {

                    dateEnd.set(Calendar.HOUR, 0)
                    dateEnd.set(Calendar.MINUTE, 0)
                    dateEnd.set(Calendar.SECOND, 0)

                    krEvent.date_end = dateEnd.timeInMillis
                }
            }
        }
    }

    krEvent.title = ev.summary.value
    krEvent.description = ev.description.value
    krEvent.uid = ev.uid.value

    return krEvent
}

fun printKREvents(ev: ArrayList<KREvent>){
    if (ev.isNotEmpty()){
        for (e in ev){
            Log.i("KRLog, ETitle", e.title)
            var formatter = SimpleDateFormat("dd/MM/yyyy = HH:mm:ss");
            var dateStartStr = formatter.format( Date(e.date_start))
            Log.i("KRLog, startDate: ", dateStartStr)
            var dateEndStr: String?
            if (e.date_end > 0) {
                dateEndStr = formatter.format(Date(e.date_end))
            } else {
                dateEndStr = ""
            }
            if (dateEndStr != null) {
                Log.i("KRLog, EndDate: ", dateEndStr)
            }
        }
    }
}