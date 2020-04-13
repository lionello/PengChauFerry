//
//  DbOpenHelper.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 23/2/2019.
//  Copyright © 2019 Lionello Lunesu. All rights reserved.
//

import Foundation
import SQLite3

class DBOpenHelper: SQLiteOpenHelper {
    init() {
        super.init(name: "ferry", version: DBOpenHelper.DATABASE_VERSION)
    }

    static let TIMES = "times"
    static let HOLIDAYS = "holidays"

    static private let DATABASE_VERSION = 7
    static private let CREATE_TABLE_TIMES =
        "CREATE TABLE \(TIMES) (time TEXT NOT NULL, `from` TEXT NOT NULL, `to` TEXT NOT NULL, durationMin INTEGER NOT NULL, days INTEGER NOT NULL, via TEXT);"
    static private let CREATE_INDEX_TIMES = "CREATE INDEX times_from_time ON \(TIMES) (`from`,time)"
    static private let CREATE_UNIQUE_INDEX_TIMES =
        "CREATE UNIQUE INDEX times_unique ON \(TIMES) (time,`from`,`to`,days)"
    static private let CREATE_TABLE_HOLIDAYS = "CREATE TABLE \(HOLIDAYS) (date TEXT NOT NULL);"
    static private let CREATE_INDEX_HOLIDAYS = "CREATE UNIQUE INDEX holidays_date ON \(HOLIDAYS) (date)"

    override func onCreate(_ db: SQLiteDatabase?) throws {
        try db?.execSQL(DBOpenHelper.CREATE_TABLE_TIMES)
        try db?.execSQL(DBOpenHelper.CREATE_INDEX_TIMES)
        try db?.execSQL(DBOpenHelper.CREATE_UNIQUE_INDEX_TIMES)
        try db?.execSQL(DBOpenHelper.CREATE_TABLE_HOLIDAYS)
        try db?.execSQL(DBOpenHelper.CREATE_INDEX_HOLIDAYS)
    }

    override func onUpgrade(_ database: SQLiteDatabase?, _ oldVersion: Int, _ newVersion: Int) throws {
        // todo
    }

}
