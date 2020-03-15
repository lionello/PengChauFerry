//
//  LocalTime.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

struct LocalTime {
    let secs: TimeInterval

    static let MIDNIGHT = LocalTime(secs: 0.0)

    static func now() -> LocalTime { Date.now().toLocalTime() }

    private static let formatter: ISO8601DateFormatter = {
        let dtf = ISO8601DateFormatter()
        dtf.formatOptions = [.withTime, .withColonSeparatorInTime]
        return dtf
    }()
    private static let ZERO = formatter.date(from: "00:00:00")!

    static func parse(_ s: String) -> LocalTime? {
        return LocalTime(secs: LocalTime.formatter.date(from: s + ":00")!.timeIntervalSince(LocalTime.ZERO))
    }

    static func minutesBetween(_ t1: LocalTime, _ t2: LocalTime) -> Int {
        return Int((t2.secs - t1.secs + 30.0)/60.0)
    }

    func plus(minutes: Int) -> LocalTime {
        LocalTime(secs: self.secs + Double(minutes) * 60.0)
    }

    func toString(_ df: DateFormatter) -> String {
        df.string(from: Date(timeInterval: secs, since: Date().toLocalDate()))
    }
}

extension LocalTime: Decodable {
    init(from decoder: Decoder) throws {
        let v = try decoder.singleValueContainer()
        let s = try v.decode(String.self)
        secs = LocalTime.formatter.date(from: s)!.timeIntervalSince(LocalTime.ZERO)
    }
}

extension LocalTime: CustomStringConvertible, CustomDebugStringConvertible {
    var debugDescription: String {
        return "\(secs)"
    }

    var description: String {
        let dtf = DateFormatter()
        dtf.dateStyle = .none
        return dtf.string(from: Date(timeIntervalSinceReferenceDate: secs))
    }
}

extension LocalTime: Equatable {
    static func == (lhs: LocalTime, rhs: LocalTime) -> Bool {
        return lhs.secs == rhs.secs
    }
}

extension LocalTime: Comparable {
    static func < (lhs: LocalTime, rhs: LocalTime) -> Bool {
        return lhs.secs < rhs.secs
    }

    static func <= (lhs: LocalTime, rhs: LocalTime) -> Bool {
        return lhs.secs <= rhs.secs
    }

    static func >= (lhs: LocalTime, rhs: LocalTime) -> Bool {
        return lhs.secs >= rhs.secs
    }
    static func > (lhs: LocalTime, rhs: LocalTime) -> Bool {
        return lhs.secs > rhs.secs
    }
}

//typealias LocalTime = Date
public typealias LocalDate = Date
typealias LocalDateTime = Date

extension LocalDateTime {
    static func now() -> LocalDateTime { return LocalDateTime() }

    func toLocalDate() -> LocalDate {
        var dc = Calendar.current.dateComponents(in: TimeZone.current, from: self)
        dc.hour = nil
        dc.minute = nil
        dc.second = nil
        dc.nanosecond = nil
        return dc.date!
    }

    func toLocalTime() -> LocalTime {
        let dc = Calendar.current.dateComponents(in: TimeZone.current, from: self)
        return LocalTime(secs: Double((dc.hour! * 60 + dc.minute!) * 60 + dc.second!) + Double(dc.nanosecond!) * 1e-9)
    }

//    func parse(_ s: String) -> LocalDateTime {
//        let df = DateFormatter()
//        df.dateStyle = .short
//        df.timeStyle = .none
//        return df.date(from: $0)!
//    }
}


