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

    private let shared = UserDefaults.standard

    var language: String? {
        get { shared.string(forKey: Preferences.LANGUAGE_PREF) }
        set(value) { shared.set(value, forKey: Preferences.LANGUAGE_PREF) }
    }

}
