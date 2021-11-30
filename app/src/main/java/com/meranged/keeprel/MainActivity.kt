package com.meranged.keeprel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.io.text.ICalReader
import com.meranged.keeprel.db.KRCalendar
import com.meranged.keeprel.db.KREvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val REQUEST_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_PERMISSION
            )
        } else {
            //getContacts()
            //chooseFile()
            val intent = Intent(this, KRActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, "test")
            }
            startActivity(intent)
        }
    }

    private fun chooseFile() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((requestCode == REQUEST_PERMISSION)) {
            getContacts()
        }
    }

    private fun getContacts() {
        val adapter = ListAdapter(this, getContactsData())
        contacts_list.adapter = adapter
    }

    private fun getContactsData(): ArrayList<Contact> {
        val contactList = ArrayList<Contact>()
        val contactsCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if ((contactsCursor?.count ?: 0) > 0) {

            while ((contactsCursor != null) && contactsCursor.moveToNext()) {

                if (contactsCursor.getInt(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    val rowID =
                        contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val lookupKey =
                        contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))

                    var name: String?

                    name =
                        contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))


                    val photoUri =
                        contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))

                    if (name != null)
                        contactList.add(Contact(name, photoUri))
                }
            }


            contactsCursor?.close()

        }
        return contactList
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            readCalendar(selectedFile)
        }
    }

    fun readCalendar(afile: Uri?) {

        val ical = readCalendarFromUri(afile)

        val viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        uiScope.launch {
            insertTheWholeCal(ical)
        }
    }

    private suspend fun insertTheWholeCal(ical: ICalendar?){
        withContext(Dispatchers.IO) {
            if (ical != null){
                App.database!!.dao.insertCalendarWithEvents(ical)
            }
        }
    }
}
