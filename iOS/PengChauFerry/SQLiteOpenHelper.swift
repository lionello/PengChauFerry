//
//  SQLiteOpenHelper.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 23/2/2019.
//  Copyright © 2019 Lionello Lunesu. All rights reserved.
//

import Foundation

class SQLiteOpenHelper {
    private var database: SQLiteDatabase?

    private let name: String
    private let newVersion: Int
    private let minimumSupportedVersion: Int

    static func getDatabasePath(_ name: String) -> String {
        let fileURL = try! FileManager.default.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: false)
            .appendingPathComponent("\(name)")
        return fileURL.path
    }

    init(name: String, version: Int, minimumSupportedVersion: Int = 0) {
        self.name = name
        self.newVersion = version
        self.minimumSupportedVersion = max(0, minimumSupportedVersion)
    }

    func close() throws {
        if self.database?.isOpen == true {
            try self.database!.close()
            self.database = nil
        }
    }

    func onCreate(_ database: SQLiteDatabase?) throws {
        // nop; override
    }

    func onDowngrade(_ database: SQLiteDatabase?, _ oldVersion: Int, _ newVersion: Int) throws {
        // nop; override
    }

    func onUpgrade(_ database: SQLiteDatabase?, _ oldVersion: Int, _ newVersion: Int) throws {
        // nop; override
    }

    public var writableDatabase: SQLiteDatabase {
        get { return try! getDatabaseLocked(writable: true) }
    }

    public var readableDatabase: SQLiteDatabase {
        get { return try! getDatabaseLocked(writable: false) }
    }

    private func getDatabaseLocked(writable: Bool) throws -> SQLiteDatabase {
        if self.database != nil {
            if !self.database!.isOpen {
                self.database = nil
            } else if !writable || !self.database!.isReadOnly {
                return self.database!
            }
        }

        let db: SQLiteDatabase
        if self.database != nil {
            db = self.database!
            if writable && db.isReadOnly {
                try db.reopenReadWrite()
            }
        } else {
            let path = SQLiteOpenHelper.getDatabasePath(self.name)
            db = try SQLiteDatabase.openDatabase(path: path, rw: writable)
        }

        let version = try db.getVersion()
        if version != self.newVersion {
            if db.isReadOnly {
                throw SQLException.string("Can't upgrade read-only database from version \(version) to \(newVersion): \(name)")
            }

            if version > 0 && version < minimumSupportedVersion {
                try db.close()
                if try SQLiteDatabase.deleteDatabase(path: self.name) {
                    return try getDatabaseLocked(writable: writable)
                }
            } else {
                try db.beginTransaction()
                defer {
                    try? db.endTransaction()
                }
                if version == 0 {
                    try onCreate(db)
                } else {
                    if version > newVersion {
                        try onDowngrade(db, version, newVersion)
                    } else {
                        try onUpgrade(db, version, newVersion)
                    }
                }
                try db.setVersion(newVersion)
                try db.setTransactionSuccessful()
            }
        }
        return db
    }
}
