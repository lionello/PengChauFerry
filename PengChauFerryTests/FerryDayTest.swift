//
//  FerryDayTest.swift
//  PengChauFerryTests
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import XCTest
@testable import PengChauFerry

class FerryDayTest: XCTestCase {

    func testEveryday() {
        XCTAssertEqual(8, FerryDay.intToDays(255).count)
    }

    func testToday() {
        let weekdays = FerryDay.intToDays(127)
        XCTAssertEqual(true, weekdays.contains(FerryDay.today()))
    }

    func testIntAndBack() {
        for i in 1...255 {
            XCTAssertEqual(UInt8(i), FerryDay.daysToInt(FerryDay.intToDays(UInt8(i))))
        }
    }

    func testToString() {
        XCTAssertEqual("Monday", FerryDay.Monday.toString())
        XCTAssertEqual("Tuesday", FerryDay.Tuesday.toString())
        XCTAssertEqual("Wednesday", FerryDay.Wednesday.toString())
        XCTAssertEqual("Thursday", FerryDay.Thursday.toString())
        XCTAssertEqual("Friday", FerryDay.Friday.toString())
        XCTAssertEqual("Saturday", FerryDay.Saturday.toString())
        XCTAssertEqual("Sunday", FerryDay.Sunday.toString())
        XCTAssertEqual("Holiday", FerryDay.Holiday.toString())
    }

}
