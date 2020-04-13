//
//  FerryViewModelTest.swift
//  PengChauFerryTests
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import XCTest
@testable import PengChauFerry

class FerryViewModelTest: XCTestCase {

    class FR: FerryRepository {
        let ferry = Ferry(time: .MIDNIGHT, from: .Central, to: .PengChau, dur: 1, days: .MondayToSaturday, fare: "1.2", via: nil)
        var ferries: [Ferry] = []
        override func getFerries(from: FerryPier, dow: FerryDay) -> [Ferry] {
            ferries.filter { $0.from == from && $0.days.contains(dow) }
        }
        override func refresh(completion: @escaping () -> Void) { ferries.append(Ferry.DUMMY); completion() }
    }

    class HR: HolidayRepository {
        var holidays = Set<LocalDate>()
        let newYear2020 = LocalDate.parse("2020-01-01")!
        override func getHoliday(day: LocalDate) -> Bool { holidays.contains(day) }
        override func setHoliday(day: LocalDate, isHoliday: Bool) { holidays.insert(day) }
        override func refresh(completion: () -> Void) { holidays.insert(newYear2020); completion() }
        override func shouldRefresh() -> Bool { false }
    }

    func testSwitchPier() {
        let vm = FerryViewModel(ferryRepository: FR(), holidayRepository: HR())
        let expectation = self.expectation(description: "switchPier")
        let cancel = vm.$state.sink { state in
            if state != nil {
                expectation.fulfill()
            }
        }
        vm.switchPier(.Central)
        waitForExpectations(timeout: 1.0)
        XCTAssertNotNil(vm.state)
        XCTAssertNotEqual(FerryDay.Holiday, vm.state!.day)
        cancel.cancel()
    }

}
