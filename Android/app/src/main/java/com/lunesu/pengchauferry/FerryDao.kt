package com.lunesu.pengchauferry

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import org.joda.time.Duration
import org.joda.time.LocalTime

class FerryDao(private val db: DbOpenHelper) {

    fun save(result: List<Ferry>, vararg piers: FerryPier) {
        db.writableDatabase.beginTransaction()
        try {
            delete(*piers)
            result.forEach {
                insert(it)
            }
            db.writableDatabase.setTransactionSuccessful()
        } finally {
            db.writableDatabase.endTransaction()
        }
    }

    fun delete(vararg piers: FerryPier) {
        val parameters = piers.mapIndexed { i, _ -> i + 1 }.joinToString { "?$it" }
        val whereArgs = piers.map { it.name }.toTypedArray()
        db.writableDatabase.delete(
            DbOpenHelper.TIMES,
            "`from` in ($parameters) and `to` in ($parameters)",
            whereArgs
        )
    }

    fun insert(ferry: Ferry) {
        val values = ContentValues()
        values.put("time", ferry.time.toString())
        values.put("`from`", ferry.from.name)
        values.put("`to`", ferry.to.name)
        values.put("durationMin", ferry.dur.standardMinutes)
        values.put("days", FerryDay.daysToInt(ferry.days))
        values.put("fare", ferry.fare)
        values.put("via", ferry.via?.name)
        db.writableDatabase.insertWithOnConflict(
            DbOpenHelper.TIMES, null, values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun query(from: FerryPier, dow: FerryDay): List<Ferry> {
        return db.readableDatabase.query(
            DbOpenHelper.TIMES,
            arrayOf("time", "`to`", "durationMin", "days", "fare", "via"),
            "`from`=? and (days & ?)",
            arrayOf(from.name, dow.flag.toString()),
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
                val fare = it.getString(4) ?: ""
                val via = it.getStringOrNull(5)
                val ferryTime = Ferry(
                    LocalTime.parse(time),
                    from,
                    FerryPier.valueOf(to),
                    Duration.standardMinutes(dur),
                    FerryDay.intToDays(days),
                    fare,
                    if (via != null) FerryPier.valueOf(via) else null
                )
                list.add(ferryTime)
            }
            list
        }
    }
}
