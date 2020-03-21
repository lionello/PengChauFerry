//
//  JsonDataTest.swift
//  PengChauFerryTests
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import XCTest
@testable import PengChauFerry

class JsonDataTest: XCTestCase {

    func testExample() {
        let d = JsonData.load(from: Bundle(for: JsonDataTest.self))
        XCTAssertNotNil(d)
    }

}
