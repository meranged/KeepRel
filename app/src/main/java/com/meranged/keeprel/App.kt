package com.meranged.keeprel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.meranged.keeprel.db.KRDatabase

class App : Application() {

    companion object {
        private var mContext: Context? = null
        val context: Context?
            get() = mContext

        var database: KRDatabase? = null

    }

    override fun onCreate() {
        super.onCreate()
        mContext = this

        database = KRDatabase.getInstance(this)
    }

}
