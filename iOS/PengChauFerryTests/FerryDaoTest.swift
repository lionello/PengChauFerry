//
//  FerryDaoTest.swift
//  PengChauFerryTests
//
//  Created by Lionello Lunesu on 2/8/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import XCTest
@testable import PengChauFerry

class FerryDaoTest: XCTestCase {

    private let ferry = Ferry(time: .MIDNIGHT, from: .MuiWo, to: .PengChau, dur: 0, days: .MondayToSaturday, fare: "1.2", via: .HeiLingChau)

    func testQuery() {
        let dao = FerryDao()
        XCTAssertEqual([], dao.query(from: .MuiWo, dow: .Holiday))
    }

    func testInsert() {
        let dao = FerryDao()
        dao.insert(ferry: ferry)
        XCTAssertEqual([ferry], dao.query(from: ferry.from, dow: .Monday))
    }

    func testQueryFilter() {
        let dao = FerryDao()
        dao.insert(ferry: ferry)
        XCTAssertEqual([], dao.query(from: ferry.from, dow: .Holiday))
        XCTAssertEqual([], dao.query(from: .ChiMaWan, dow: .Monday))
    }

    func testInsertTwice() {
        let dao = FerryDao()
        dao.insert(ferry: ferry)
        dao.insert(ferry: ferry)
    }

    func testDelete() {
        let dao = FerryDao()
        dao.insert(ferry: ferry)
        dao.delete(piers: ferry.from, ferry.to)
        XCTAssertEqual([], dao.query(from: ferry.from, dow: .Monday))
    }

    func testSave() {
        let dao = FerryDao()
        dao.save(result: [ferry], piers: ferry.from, ferry.to)
        XCTAssertEqual([ferry], dao.query(from: .MuiWo, dow: .Monday))
    }

    func testGet() {
        let dao = FerryDao()
        dao.insert(ferry: ferry)
        XCTAssertEqual(ferry, dao.get(from: ferry.from, to: ferry.to, dow: .Monday, time: LocalTime.MIDNIGHT))
        XCTAssertNil(dao.get(from: ferry.from, to: ferry.to, dow: .Sunday, time: LocalTime.MIDNIGHT))
        XCTAssertNil(dao.get(from: ferry.from, to: ferry.from, dow: .Monday, time: LocalTime.MIDNIGHT))
        XCTAssertNil(dao.get(from: ferry.from, to: ferry.from, dow: .Monday, time: LocalTime(secs: 671)))
        XCTAssertNil(dao.get(from: ferry.to, to: ferry.to, dow: .Monday, time: LocalTime.MIDNIGHT))
    }

}
