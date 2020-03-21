//
//  FerryDay.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

struct FerryDay: OptionSet, Codable {
    let rawValue: UInt8

    static let Monday = FerryDay(rawValue: 1)
    static let Tuesday = FerryDay(rawValue: 2)
    static let Wednesday = FerryDay(rawValue: 4)
    static let Thursday = FerryDay(rawValue: 8)
    static let Friday = FerryDay(rawValue: 16)
    static let Saturday = FerryDay(rawValue: 32)
    static let Sunday = FerryDay(rawValue: 64)
    static let Holiday = FerryDay(rawValue: 128)

    private static let ENUMS = (0...7).map { return FerryDay(rawValue: 1<<$0) }

    static let MondayToSaturday = FerryDays(arrayLiteral: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday)
    static let SundayAndHolidays = FerryDays(arrayLiteral: Sunday, Holiday)
    static let EVERYDAY = FerryDays(ENUMS)

    static func fromDate(_ date: Date) -> FerryDay {
        let weekday = Calendar.current.component(.weekday, from: date)
        // Sunday=1, Monday=2, etc...
        return FerryDay(rawValue: 1<<((5+weekday)%7))
    }

    static func today() -> FerryDay { fromDate(Date()) }

    static func intToDays(_ int: UInt8) -> FerryDays {
//        FerryDays(sequence: EVERYDAY.compactMap { (int & $0.rawValue != 0) ? $0 : nil })
        FerryDays(rawValue: int)
    }

    static func daysToInt(_ days: FerryDays) -> UInt8 {
//        days.reduce(0) { $0 + $1.rawValue }
        days.rawValue
    }

    var count: Int {
        get { rawValue.nonzeroBitCount }
    }

    private static let STRINGS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Holiday"]

    func toString() -> String {
        assert(count == 1)
        return FerryDay.STRINGS[rawValue.trailingZeroBitCount]
    }
}

typealias FerryDays = FerryDay
