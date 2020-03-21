//
//  FerryPierTest.swift
//  PengChauFerryTests
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import XCTest
@testable import PengChauFerry

class FerryPierTest: XCTestCase {

    func testName() {
        XCTAssertEqual("PengChau", FerryPier.PengChau.rawValue)
    }

    func testToString() {
        XCTAssertEqual("PengChau", FerryPier.PengChau.toString())
    }

    func testValueOf() {
        XCTAssertEqual(FerryPier.PengChau, FerryPier.valueOf("PengChau"))
    }

    func testNearest() {
        XCTAssertEqual(FerryPier.PengChau, FerryPier.findNearest(latitude: 22.279, longitude: 114.046))
        XCTAssertEqual(FerryPier.Central, FerryPier.findNearest(latitude: 22.286, longitude: 114.161))
    }

    func testNearestCustom() {
        XCTAssertEqual(FerryPier.MuiWo, FerryPier.findNearest(latitude: 22.279, longitude: 114.046, piers: [.MuiWo]))
    }
}
