//
//  Ferry.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

struct Ferry: Decodable {
    let time: LocalTime
    let from: FerryPier
    let to: FerryPier
    let dur: Int
    let days: FerryDays
    let fare: String
    let via: FerryPier?

    var endTime : LocalTime {
        get { LocalTime(secs: time.secs + TimeInterval(dur * 60)) /*time.advanced(by: dur)*/ }
    }

    static let DUMMY = Ferry(time: LocalTime.now(), from: .PengChau, to: .Central, dur: 600, days: FerryDay.EVERYDAY, fare: "11.0", via: nil)
}

extension Ferry: Identifiable {
    var id : String {
        get { "\(time.secs) \(from.rawValue) \(to.rawValue) \(days.rawValue)" } // fixme
    }
}

extension Ferry: Equatable {
    static func == (lhs: Self, rhs: Self) -> Bool {
        return lhs.time == rhs.time && lhs.from == rhs.from && lhs.to == rhs.to && lhs.days == rhs.days
    }
}
