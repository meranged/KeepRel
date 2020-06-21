package com.meranged.keeprel.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [KRCalendar::class, KREvent::class],
    version = 1,
    exportSchema = false
)
abstract class KRDatabase : RoomDatabase() {

    abstract val dao: KRDao

    companion object {

        @Volatile
        private var INSTANCE: KRDatabase? = null

        fun getInstance(context: Context): KRDatabase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            KRDatabase::class.java,
                            "keeprel_database"
                        )
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                Thread(Runnable { prepopulateDb(getInstance(context))}).start()
                            }
                        })
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }

        private fun prepopulateDb(db: KRDatabase) {

        }
    }
}