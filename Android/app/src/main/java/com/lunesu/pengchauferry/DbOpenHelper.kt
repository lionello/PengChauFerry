package com.lunesu.pengchauferry

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbOpenHelper(context: Context?) : SQLiteOpenHelper(context, if (context == null) null else DB_NAME, null, DB_VERSION) {

    companion object {
        const val TIMES = "times"
        const val HOLIDAYS = "holidays"

        private const val DB_NAME = "ferry2"
        private const val DB_VERSION = 11

        private const val CREATE_TABLE_TIMES =
            "CREATE TABLE $TIMES (time TEXT NOT NULL, `from` TEXT NOT NULL, `to` TEXT NOT NULL, durationMin INTEGER NOT NULL, days INTEGER NOT NULL, fare TEXT, via TEXT);"
        private const val CREATE_UNIQUE_INDEX_TIMES = "CREATE UNIQUE INDEX times_unique ON $TIMES (`from`,`to`,time,days);"

        private const val CREATE_TABLE_HOLIDAYS =
            "CREATE TABLE $HOLIDAYS (date TEXT NOT NULL);"
        private const val CREATE_INDEX_HOLIDAYS = "CREATE UNIQUE INDEX holidays_date ON $HOLIDAYS (date);"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_TIMES)
        db?.execSQL(CREATE_UNIQUE_INDEX_TIMES)
        db?.execSQL(CREATE_TABLE_HOLIDAYS)
        db?.execSQL(CREATE_INDEX_HOLIDAYS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            3 -> db?.execSQL(CREATE_TABLE_HOLIDAYS)
            4 -> db?.execSQL(CREATE_INDEX_HOLIDAYS)
            6 -> db?.execSQL(CREATE_UNIQUE_INDEX_TIMES)
            7 -> {
                db?.execSQL("DROP INDEX times_from;")
                // db?.execSQL(CREATE_INDEX_TIMES) no longer used
            }
            8 -> db?.execSQL("ALTER TABLE $TIMES ADD COLUMN fare TEXT;")
            9 -> db?.execSQL("ALTER TABLE $TIMES ADD COLUMN via TEXT;")
            10 -> {
                db?.execSQL("DROP INDEX times_from_time;")
                db?.execSQL("DROP INDEX times_unique;")
                db?.execSQL(CREATE_UNIQUE_INDEX_TIMES)
            }
        }
    }
}
