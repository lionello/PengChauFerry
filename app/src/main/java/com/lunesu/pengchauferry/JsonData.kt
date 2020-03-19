package com.lunesu.pengchauferry

import android.content.Context
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class JsonData(val holidays: Array<String>, val ferries: Array<Ferry>) {
    companion object {

        fun load(context: Context): JsonData {
            val inputStream = context.assets.open("db.json")
            val obj = JSONObject(String(inputStream.readBytes(), Charsets.UTF_8))
            return JsonData(
                obj.getJSONArray("holidays").map{ it.toString() },
                obj.getJSONArray("ferries").map{ parseFerry(it as JSONObject) }
            )
        }

        private inline fun <reified R>JSONArray.map(block: (Any)->R): Array<R> =
            Array(this.length()) { block(this.get(it)) }

        private fun parseFerry(obj: JSONObject): Ferry =
            Ferry(
                LocalTime.parse(obj.getString("time")),
                FerryPier.valueOf(obj.getString("from")),
                FerryPier.valueOf(obj.getString("to")),
                Duration.standardMinutes(obj.getLong("dur")),
                FerryDay.intToDays(obj.getInt("days")),
                obj.getString("fare"),
                obj.optString("via").let{ if (it == "") null else FerryPier.valueOf(it) }
            )
    }
}
