package com.lunesu.pengchauferry

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getStringOrNull
import org.joda.time.Duration
import org.joda.time.LocalTime

class FerryDao(private val db: DbOpenHelper) {

    companion object {
        private fun ferry(from: FerryPier, it: Cursor): Ferry {
            val time = it.getString(0)
            val to = it.getString(1)
            val dur = it.getLong(2)
            val days = it.getInt(3)
            val fare = it.getString(4) ?: ""
            val via = it.getStringOrNull(5)
            return Ferry(
                Utils.parseTime(time)!!.first,
                from,
                FerryPier.valueOf(to),
                Duration.standardMinutes(dur),
                FerryDay.intToDays(days),
                fare,
                if (via != null) FerryPier.valueOf(via) else null
            )
        }
    }

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

    fun get(from: FerryPier, to: FerryPier, dow: FerryDay, time: LocalTime): Ferry? {
        return db.readableDatabase.query(
            DbOpenHelper.TIMES,
            arrayOf("time", "`to`", "durationMin", "days", "fare", "via"),
            "`from`=? and (days & ?) and time=? and `to`=?",
            arrayOf(from.name, dow.flag.toString(), time.toString(), to.name),
            null,
            null,
            null
        ).use {
            if (it.moveToNext()) ferry(from, it) else null
        }
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
                list.add(ferry(from, it))
            }
            list
        }
    }
}
