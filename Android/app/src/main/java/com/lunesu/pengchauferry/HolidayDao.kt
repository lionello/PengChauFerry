package com.lunesu.pengchauferry

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import org.joda.time.LocalDate

class HolidayDao(private val db: DbOpenHelper) {

    fun query(today: LocalDate): Boolean {
        return db.readableDatabase.query(
            DbOpenHelper.HOLIDAYS,
            arrayOf("date"),
            "date=?",
            arrayOf(today.toString()),
            null,
            null,
            null
        ).use {
            it.moveToNext()
        }
    }

    fun save(holidays: List<LocalDate>) {
        holidays.forEach {
            insert(it)
        }
    }

    fun insert(date: LocalDate) {
        val day = date.toString()
        val contentValues = ContentValues()
        contentValues.put("date", day)
        db.writableDatabase.insertWithOnConflict(DbOpenHelper.HOLIDAYS, null, contentValues, CONFLICT_IGNORE)
    }

    fun delete(date: LocalDate) {
        val day = date.toString()
        db.writableDatabase.delete(DbOpenHelper.HOLIDAYS, "date=?", arrayOf(day))
    }
}
