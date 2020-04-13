//
//  DBOpenHelper.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 23/2/2019.
//  Copyright © 2019 Lionello Lunesu. All rights reserved.
//

import Foundation
import SQLite3

enum SQLException: Error {
    case error(Int32)
    case string(String)
}

class SQLiteDatabase {
    fileprivate let name: String

    fileprivate var dbPointer: OpaquePointer?
    fileprivate var _isReadOnly: Bool = false

    public var isReadOnly: Bool {
        get { return self._isReadOnly }
    }

    public var isOpen: Bool {
        get { return self.dbPointer != nil }
    }

    fileprivate func Test(_ ret: Int32) throws {
        if ret != SQLITE_OK {
            throw SQLException.error(ret)
        }
    }

    init(name: String) {
        self.name = name
    }

    public func reopenReadWrite() throws {
        // nop
    }

    public static func openDatabase(path: String, rw: Bool) throws -> SQLiteDatabase {
        return try openDatabase(path: path, rw: rw, create: false)
    }

    public static func openOrCreateDatabase(path: String, rw: Bool) throws -> SQLiteDatabase {
        return try openDatabase(path: path, rw: rw, create: true)
    }

    fileprivate static func openDatabase(path: String, rw: Bool, create: Bool) throws -> SQLiteDatabase {
        let db = SQLiteDatabase(name: path)
        try db.open()
        return db
    }

    public static func deleteDatabase(path: String) throws -> Bool {
        return false
    }

    func open() throws {
        try Test(sqlite3_open(self.name, &self.dbPointer))
    }

    public func close() throws {
        try Test(sqlite3_close(self.dbPointer!))
        self.dbPointer = nil
    }

    public func getVersion() throws -> Int {
        return try Int(executeForLong("PRAGMA user_version;"))
    }

    public func needUpgrade(_ newVersion: Int) throws -> Bool {
        let version = try getVersion()
        return newVersion > version
    }

    public func setVersion(_ version: Int) throws {
        try execSQL("PRAGMA user_version = \(version)")
    }

    func execSQL(_ sql: String) throws {
        try Test(sqlite3_exec(dbPointer!, sql, nil, nil, nil))
    }

    fileprivate var markedSuccessful: Bool?

    fileprivate func throwIfNoTransaction() throws {
        if markedSuccessful == nil {
            throw SQLException.string("Cannot perform this operation because there is no current transaction.")
        }
    }

    fileprivate func throwIfTransactionMarkedSuccessful() throws {
        if markedSuccessful == true {
            throw SQLException.string("Cannot perform this operation because "
                + "the transaction has already been marked successful.  The only "
                + "thing you can do now is call endTransaction().")
        }
    }

    public func beginTransaction() throws {
        try throwIfTransactionMarkedSuccessful()
        try execSQL("BEGIN EXCLUSIVE;")
        self.markedSuccessful = false
    }

    public func endTransaction() throws {
        try throwIfNoTransaction()
        if self.markedSuccessful == true {
            try execSQL("COMMIT;")
        } else {
            try execSQL("ROLLBACK;")
        }
        self.markedSuccessful = nil
   }

    public func setTransactionSuccessful() throws {
        try throwIfNoTransaction()
        try throwIfTransactionMarkedSuccessful()
        self.markedSuccessful = true
    }

//    public func compileStatement(_ cmd: String) throws -> SQLiteStatement {
//        return SQLiteStatement(self, cmd)
//    }

    func executeForLong(_ queryStatementString: String) throws -> Int64 {
        var queryStatement: OpaquePointer?

        try Test(sqlite3_prepare_v2(dbPointer, queryStatementString, -1, &queryStatement, nil))
        defer {
            sqlite3_finalize(queryStatement)
        }

        let result = sqlite3_step(queryStatement)
        if result != SQLITE_ROW {
            throw SQLException.error(result)
        }
        return sqlite3_column_int64(queryStatement, 0)
    }

    deinit {
        try? close()
    }

    public func delete(_ table: String, whereClause: String, whereArgs: [String]) -> Int {
        return -1
    }

    public enum Conflict: Int {
        case None = 0
        case Rollback = 1
        case Abort = 2
        case Fail = 3
        case Ignore = 4
        case Replace = 5
    }

    public func insert(_ table: String, _ nullColumnHack: String?, values: [String: Any]) -> Int {
        do {
            return try insertWithOnConflict(table, nullColumnHack, initialValues: values, conflict: .None)
        } catch {
            print("Error inserting \(values): \(error)")
            return -1
        }
    }

    public func insertOrThrow(_ table: String, _ nullColumnHack: String?, initialValues: [String: Any]) throws -> Int {
        return try insertWithOnConflict(table, nullColumnHack, initialValues: initialValues, conflict: .None)
    }

    static func toString(_ any: Any) -> String {
        if any is String {
            return "\"\(any)\""
        }
        return String(describing: any)
    }

//    static func escape(_ string: String) -> String {
//        return ""
//    }

    private static let CONFLICT_VALUES = [
        "", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "
    ]

    public func insertWithOnConflict(_ table: String, _ nullColumnHack: String?, initialValues: [String: Any], conflict: Conflict) throws -> Int {
        let conflict_value = SQLiteDatabase.CONFLICT_VALUES[conflict.rawValue]
        let sql: String
        if initialValues.count > 0 {
            let columns = initialValues.keys.joined(separator: ",")
            // TODO: use bound args
            let values = initialValues.values.map(SQLiteDatabase.toString).joined(separator: ",")
            sql = "INSERT \(conflict_value) INTO \(table) (\(columns)) VALUES(\(values))"
        } else {
            sql = "INSERT \(conflict_value) INTO \(table) (\(nullColumnHack!)) VALUES(NULL)"
        }
        return try Int(executeForLastInsertedRowId(sql))
    }

    func executeForLastInsertedRowId(_ sql: String) throws -> Int64 {
        try execSQL(sql)
        return try executeForLong("SELECT last_insert_rowid()")
    }
}
