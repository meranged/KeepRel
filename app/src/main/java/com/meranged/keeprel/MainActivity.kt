package com.meranged.keeprel

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val REQUEST_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_PERMISSION
            )
        } else {
            getContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        name =
                            contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                    else
                        name =
                            contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))


                    val photoUri =
                        contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))

                    if (name != null)
                        contactList.add(Contact(name, photoUri))
                }
            }

            contactsCursor.close()
        }
        return contactList
    }
}
