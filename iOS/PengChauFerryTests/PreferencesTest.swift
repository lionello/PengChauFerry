//
//  PreferencesTest.swift
//  PengChauFerryTests
//
//  Created by Lionello Lunesu on 11/4/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import XCTest

@testable import PengChauFerry

class PreferencesTest: XCTestCase {

    func testLanguage() {
        let lang = "en"
        Preferences().language = lang
        XCTAssertEqual(lang, Preferences().language)

        Preferences().language = nil
        XCTAssertNil(Preferences().language)
    }

    func testLastRefresh() {
        let now = LocalDateTime(timeIntervalSince1970: 42.0)
        Preferences().lastRefresh = now
        XCTAssertEqual(now, Preferences().lastRefresh)

        Preferences().lastRefresh = nil
        XCTAssertNil(Preferences().lastRefresh)
    }

}
