//
//  LocalTime.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

typealias Duration = TimeInterval

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
        guard let date = LocalTime.formatter.date(from: s + ":00") else {
            return nil
        }
        return LocalTime(secs: date.timeIntervalSince(LocalTime.ZERO))
    }

    static func secondsBetween(_ t1: LocalTime, _ t2: LocalTime) -> Int {
        Int(t2.secs - t1.secs)
    }

    static func minutesBetween(_ t1: LocalTime, _ t2: LocalTime) -> Int {
        secondsBetween(t1, t2) / 60
    }

    func plus(seconds: Duration) -> LocalTime {
        LocalTime(secs: self.secs + seconds)
    }

    func plus(minutes: Int) -> LocalTime {
        plus(seconds: Double(minutes) * 60.0)
    }

    func toString(_ df: DateFormatter) -> String {
        let today = Calendar.autoupdatingCurrent.date(bySettingHour: 0, minute: 0, second: 0, of: Date())!
        return df.string(from: Date(timeInterval: secs, since: today))
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
    var debugDescription: String { "\(secs)" }

    var description: String { LocalTime.formatter.string(from: Date(timeIntervalSinceReferenceDate: secs)) }
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

public typealias LocalDate = DateComponents

extension LocalDate {
    fileprivate static let formatter: ISO8601DateFormatter = {
        let dtf = ISO8601DateFormatter()
        dtf.formatOptions = [ .withFullDate ]
        dtf.timeZone = .autoupdatingCurrent
        return dtf
    }()

    static func now() -> LocalDate { LocalDateTime.now().toLocalDate() }

    func toString() -> String {
        LocalDate.formatter.string(from: self.date!)
    }

    static func parse(_ s: String) -> LocalDate? {
        LocalDate.formatter.date(from: s)?.toLocalDate()
    }
}

extension TimeZone {
    static let UTC = TimeZone(secondsFromGMT: 0)!
}

public typealias LocalDateTime = Date

extension LocalDateTime {
    static func now() -> LocalDateTime { LocalDateTime() }

    fileprivate func toDateComponents() -> DateComponents {
        Calendar.autoupdatingCurrent.dateComponents(in: .autoupdatingCurrent, from: self)
    }

    func toLocalDate() -> LocalDate {
        var dc = toDateComponents()
        dc.hour = nil
        dc.minute = nil
        dc.second = nil
        dc.nanosecond = nil
        return dc
    }

    func toLocalTime() -> LocalTime {
        let dc = toDateComponents()
        return LocalTime(secs: Double((dc.hour! * 60 + dc.minute!) * 60 + dc.second!) + Double(dc.nanosecond!) * 1e-9)
    }

}
