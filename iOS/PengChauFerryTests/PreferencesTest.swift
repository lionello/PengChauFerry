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

    func testPreferences() {
        Preferences().language = "en"
        XCTAssertEqual("en", Preferences().language)

        Preferences().language = nil
        XCTAssertNil(Preferences().language)
    }

}
