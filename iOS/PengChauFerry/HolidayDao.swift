//
//  HolidayDao.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

class HolidayDao {
    static let formatter: ISO8601DateFormatter = {
        let df = ISO8601DateFormatter()
        df.formatOptions = [ .withFullDate ]
        return df
    }()
    private var set = Set<String>()

    func query(today: LocalDate) -> Bool {
//        return db.readableDatabase.query(
//            DbOpenHelper.HOLIDAYS,
//            arrayOf("date"),
//            "date=?",
//            arrayOf(today.toString()),
//            null,
//            null,
//            null
//        ).use {
//            it.moveToNext()
//        }
        return set.contains(HolidayDao.formatter.string(from: today))
    }

    func save(holidays: Array<LocalDate>) {
        holidays.forEach {
            insert(date: $0)
        }
    }

    func insert(date: LocalDate) {
        set.insert(HolidayDao.formatter.string(from: date))
//        val day = date.toString()
//        val contentValues = ContentValues()
//        contentValues.put("date", day)
//        db.writableDatabase.insertWithOnConflict(DbOpenHelper.HOLIDAYS, null, contentValues, CONFLICT_IGNORE)
    }

    func delete(date: LocalDate) {
        set.remove(HolidayDao.formatter.string(from: date))
//        val day = date.toString()
//        db.writableDatabase.delete(DbOpenHelper.HOLIDAYS, "date=?", arrayOf(day))
    }

}
