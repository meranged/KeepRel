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

        var ical: ICalendar?

        val fileInputStream: InputStream? = App.context!!.getContentResolver().openInputStream(afile)

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

    var kr_cal = KRCalendar()

    var cal_name: String?
    var cal_desc: String?

    try {
        cal_name = cal.getExperimentalProperty("X-WR-CALNAME").value
    } catch (e: Exception){
        val sdf = SimpleDateFormat("ddMyyyyhhmmss")
        val currentDate = sdf.format(Date())
        cal_name = "Calendar" + currentDate
    }

    try {
        cal_desc = cal.getExperimentalProperty("X-WR-CALDESC").value
    } catch (e: Exception){
        cal_desc = ""
    }

    kr_cal.name = cal_name!!
    kr_cal.description = cal_desc

    kr_cal.locale = Locale.getDefault().displayLanguage

    return kr_cal
}

fun insertKRCalendar(ical: KRCalendar): KRCalendar {
    val id = App.database!!.dao.insert(ical)
    ical.id = id
    return ical
}

fun getKREventFromVEvent(ev: VEvent): KREvent {
    var kr_event = KREvent()

    var dateStart = GregorianCalendar.getInstance()
    var dateEnd = GregorianCalendar.getInstance()
    dateStart.setTime(ev.dateStart.value)

    if (ev.dateEnd != null)
        dateEnd.setTime(ev.dateEnd.value)

    dateStart.set(Calendar.HOUR, 0)
    dateStart.set(Calendar.MINUTE, 0)
    dateStart.set(Calendar.SECOND, 0)

    kr_event.date_start = dateStart.timeInMillis

    //check if we deal with 1 day event
    if (ev.dateEnd == null) {
        kr_event.date_end = 0
    } else {
        dateEnd.setTime(ev.dateEnd.value)

        if ((dateStart == dateEnd) or (dateStart.after(dateEnd))) {
            kr_event.date_end = 0
        } else {
            if ((dateStart.get(Calendar.DAY_OF_MONTH) == dateEnd.get(Calendar.DAY_OF_MONTH))
                && (dateStart.get(Calendar.YEAR) == dateEnd.get(Calendar.YEAR))
                && (dateStart.get(Calendar.MONTH) == dateEnd.get(Calendar.MONTH))){
                kr_event.date_end = 0
            } else {
                dateStart.add(Calendar.DAY_OF_MONTH, 1)

                if (((dateStart.get(Calendar.DAY_OF_MONTH) == dateEnd.get(Calendar.DAY_OF_MONTH))
                            && (dateStart.get(Calendar.YEAR) == dateEnd.get(Calendar.YEAR))
                            && (dateStart.get(Calendar.MONTH) == dateEnd.get(Calendar.MONTH)))
                    && (dateEnd.get(Calendar.HOUR_OF_DAY) < 2)){
                    kr_event.date_end = 0
                } else {

                    dateEnd.set(Calendar.HOUR, 0)
                    dateEnd.set(Calendar.MINUTE, 0)
                    dateEnd.set(Calendar.SECOND, 0)

                    kr_event.date_end = dateEnd.timeInMillis
                }
            }
        }
    }

    kr_event.title = ev.summary.value
    kr_event.description = ev.description.value
    kr_event.uid = ev.uid.value

    return kr_event
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
            Log.i("KRLog, EndDate: ", dateEndStr)
        }
    }
}