//
//  LocalDateTimeTest.swift
//  PengChauFerryTests
//
//  Created by Lionello Lunesu on 10/4/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import XCTest
@testable import PengChauFerry

class LocalDateTimeTest: XCTestCase {

    let ldt = Calendar.current.date(bySettingHour: 0, minute: 1, second: 2, of: LocalDateTime.now())!

    func testToLocalDate() throws {
        let ld = LocalDate.now()
        XCTAssertEqual(ld, ldt.toLocalDate())
    }

    func testLocalDate() throws {
        let ld = LocalDate.parse("2020-04-10")
        XCTAssertEqual(2020, ld?.year)
        XCTAssertEqual(04, ld?.month)
        XCTAssertEqual(10, ld?.day)
        XCTAssertEqual("2020-04-10", ld?.toString())
    }

    func testToLocalTime() throws {
        let lt = LocalTime(secs: 62.0)
        XCTAssertEqual(lt, ldt.toLocalTime())
    }

    func testLocalTime() throws {
        let lt = LocalTime(secs: 62.0)
        XCTAssertEqual(62.0, lt.secs)
        XCTAssertEqual("62.0", lt.debugDescription)
        XCTAssertEqual("00:01:02", lt.description)
        XCTAssertEqual(LocalTime(secs: 122.0), lt.plus(minutes: 1))
        XCTAssertEqual(1, LocalTime.minutesBetween(LocalTime(secs: 1), lt))
        XCTAssertEqual(61, LocalTime.secondsBetween(LocalTime(secs: 1), lt))
        XCTAssertEqual(lt, LocalTime.parse("00:01:02"))

        let zero = LocalTime(secs: 0)
        XCTAssertTrue(lt == lt)
        XCTAssertFalse(lt == zero)
        XCTAssertTrue(lt != zero)
        XCTAssertFalse(lt != lt)
        XCTAssertTrue(zero < lt)
        XCTAssertFalse(lt < zero)
        XCTAssertTrue(lt <= lt)
        XCTAssertFalse(lt <= zero)
        XCTAssertTrue(lt > zero)
        XCTAssertFalse(lt > lt)
        XCTAssertTrue(lt >= lt)
        XCTAssertFalse(zero >= lt)
    }

    func testLocalTimeDecode() throws {
        let lt = try JSONDecoder().decode(LocalTime.self, from: "\"00:01:02\"".data(using: .utf8)!)
        XCTAssertEqual(lt, ldt.toLocalTime())
    }

    func testLocalTimeToString() throws {
        let lt = LocalTime(secs: 62.0)
        let df = DateFormatter()
        df.timeStyle = .medium
        XCTAssertEqual("12:01:02 AM", lt.toString(df))
    }

}
