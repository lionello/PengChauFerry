//
//  HolidayDao.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

class HolidayDao {
    private var set = Set<String>()

    func query(today: LocalDate) -> Bool {
        let day = today.toString()
//        return db.readableDatabase.query(
//            DbOpenHelper.HOLIDAYS,
//            arrayOf("date"),
//            "date=?",
//            arrayOf(day),
//            null,
//            null,
//            null
//        ).use {
//            it.moveToNext()
//        }
        return set.contains(day)
    }

    func save(holidays: Array<LocalDate>) {
        holidays.forEach {
            insert(date: $0)
        }
    }

    func insert(date: LocalDate) {
        let day = date.toString()
        set.insert(day)
//        val contentValues = ContentValues()
//        contentValues.put("date", day)
//        db.writableDatabase.insertWithOnConflict(DbOpenHelper.HOLIDAYS, null, contentValues, CONFLICT_IGNORE)
    }

    func delete(date: LocalDate) {
        let day = date.toString()
        set.remove(day)
//        db.writableDatabase.delete(DbOpenHelper.HOLIDAYS, "date=?", arrayOf(day))
    }

}
