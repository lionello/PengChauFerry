package com.lunesu.pengchauferry

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import org.joda.time.Duration
import org.joda.time.LocalTime

class FerryDao(private val db: DbOpenHelper) {

    fun save(result: List<Ferry>, vararg piers: FerryPier) {
        val parameters = piers.mapIndexed { i, _ -> i+1 }.joinToString { "?$it" }
        val whereArgs = piers.map { it.toString() }.toTypedArray()
        db.writableDatabase.beginTransaction()
        try {
            db.writableDatabase.delete(
                DbOpenHelper.TIMES,
                "`from` in ($parameters) and `to` in ($parameters)",
                whereArgs
            )
            result.forEach {
                val values = ContentValues()
                values.put("time", it.time.toString())
                values.put("`from`", it.from.name)
                values.put("`to`", it.to.name)
                values.put("durationMin", it.dur.standardMinutes)
                values.put("days", FerryDay.daysToInt(it.days))
                db.writableDatabase.insertWithOnConflict(DbOpenHelper.TIMES, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            db.writableDatabase.setTransactionSuccessful()
        } finally {
            db.writableDatabase.endTransaction()
        }
    }

    fun query(from: FerryPier, dow: FerryDay): List<Ferry> {
        return db.readableDatabase.query(
            DbOpenHelper.TIMES,
            arrayOf("time", "`to`", "durationMin", "days"),
            "`from`=?",
            arrayOf(from.toString()),
            null,
            null,
            "time"
        ).use {
            val list = mutableListOf<Ferry>()
            while (it.moveToNext()) {
                val time = it.getString(0)
                val to = it.getString(1)
                val dur = it.getLong(2)
                val days = it.getInt(3)
                val ferryTime = Ferry(
                    LocalTime.parse(time),
                    from,
                    FerryPier.valueOf(to),
                    Duration.standardMinutes(dur),
                    FerryDay.intToDays(days)
                )
                list.add(ferryTime)
            }
            list
        }.filter { it.days.contains(dow) }
    }

}