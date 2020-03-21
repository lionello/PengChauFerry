//
//  FerryDao.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

class FerryDao {
    var map: [FerryPier:[Ferry]] = [:]

    private func query(from: FerryPier) -> [Ferry] {
        map[from] ?? []
    }

    func save(result: Array<Ferry>, piers: FerryPier...) {
//        db.writableDatabase.beginTransaction()
//        try {
        delete(piers: piers)
        result.forEach {
            insert(ferry: $0)
        }
//            db.writableDatabase.setTransactionSuccessful()
//        } finally {
//            db.writableDatabase.endTransaction()
//        }
    }

    private func delete(piers: [FerryPier]) {
        piers.forEach { from in
            map[from] = query(from: from).filter{ !piers.contains($0.to) }
        }
    }

    func delete(piers: FerryPier...) {
        delete(piers: piers)
//        val parameters = piers.mapIndexed { i, _ -> i+1 }.joinToString { "?$it" }
//        val whereArgs = piers.map { it.name }.toTypedArray()
//        db.writableDatabase.delete(
//            DbOpenHelper.TIMES,
//            "`from` in ($parameters) and `to` in ($parameters)",
//            whereArgs
//        )
    }

    func insert(ferry: Ferry) {
        if map[ferry.from]?.append(ferry) == nil {
            map[ferry.from] = [ferry]
        }
//        val values = ContentValues()
//        values.put("time", ferry.time.toString())
//        values.put("`from`", ferry.from.name)
//        values.put("`to`", ferry.to.name)
//        values.put("durationMin", ferry.dur.standardMinutes)
//        values.put("days", FerryDay.daysToInt(ferry.days))
//        values.put("fare", ferry.fare)
//        values.put("via", ferry.via?.name)
//        db.writableDatabase.insertWithOnConflict(
//            DbOpenHelper.TIMES, null, values,
//            SQLiteDatabase.CONFLICT_REPLACE
//        )
    }

    func query(from: FerryPier, dow: FerryDay) -> Array<Ferry> {
        return query(from: from)
            .filter{ $0.days.contains(dow) }
            .sorted{ $0.time < $1.time }
    }

}
