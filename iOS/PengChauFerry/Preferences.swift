//
//  Preferences.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 11/4/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

class Preferences {
    private static let LANGUAGE_PREF = "language"
    private static let LAST_REFRESH_PREF = "lastRefresh"

    private let shared = UserDefaults.standard

    var language: String? {
        get { shared.string(forKey: Preferences.LANGUAGE_PREF) }
        set(value) { shared.set(value, forKey: Preferences.LANGUAGE_PREF) }
    }

    var lastRefresh: LocalDateTime? {
        get { shared.string(forKey: Preferences.LAST_REFRESH_PREF).flatMap { ISO8601DateFormatter().date(from: $0) } }
        set(value) { shared.set(value.map { ISO8601DateFormatter().string(from: $0) }, forKey: Preferences.LAST_REFRESH_PREF) }
    }

}
