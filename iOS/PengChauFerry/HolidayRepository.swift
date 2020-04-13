//
//  HolidayRepository.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

open class HolidayRepository {
    private let holidayDao = HolidayDao()
    private let BUDDHA = LocalDate.parse("2020-04-11T00:00:00Z")!

    open func getHoliday(day: LocalDate) -> Bool {
        return holidayDao.query(today: day)
    }

    open func setHoliday(day: LocalDate, isHoliday: Bool) {
        if isHoliday {
            holidayDao.insert(date: day)
        } else {
            holidayDao.delete(date: day)
        }
    }

    open func shouldRefresh() -> Bool {
        return !getHoliday(day: BUDDHA)
    }

    open func refresh(completion: () -> Void) {
        HongKongHolidayFetcher.fetch { holidays, _ in
            if holidays.count >= 15 {
                holidayDao.save(holidays: holidays + [BUDDHA])
            }
            completion()
        }
    }

}
