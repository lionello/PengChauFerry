//
//  FerryViewModel.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation
import Combine

final class FerryViewModel: ObservableObject {
    private let ferryRepository: FerryRepository
    private let holidayRepository: HolidayRepository

    init(ferryRepository: FerryRepository, holidayRepository: HolidayRepository) {
        self.ferryRepository = ferryRepository
        self.holidayRepository = holidayRepository
    }

    convenience init() {
        self.init(ferryRepository: FerryRepository(), holidayRepository: HolidayRepository())
    }

    struct State {
        let ferries: [Ferry]
        let from: FerryPier
        let day: FerryDay
    }

    @Published private(set) var state: State?
    private(set) var time = Timer.publish(every: 1, on: .main, in: .common).autoconnect()

    private var _time: LocalDateTime {
        LocalDateTime.now()
    }

    private func updateState(from: FerryPier, dow: FerryDay, autoRefresh: Bool, filtered: Bool) {
        var ferries = ferryRepository.getFerries(from: from, dow: dow)
        if autoRefresh && (ferries.isEmpty || holidayRepository.shouldRefresh()) {
            refreshAndUpdate(from: from)
        } else {
            if filtered {
                let now = _time.toLocalTime()
                ferries = ferries.filter { $0.time >= now }
            }
            self.state = State(ferries: ferries, from: from, day: dow)
        }
    }

    private var today: LocalDate {
        _time.toLocalDate()
    }

    func getDay(date: LocalDate) -> FerryDay {
        return holidayRepository.getHoliday(day: date) ? FerryDay.Holiday : FerryDay.fromDate(date)
    }

    func switchPier(_ pier: FerryPier) {
        updateState(from: pier, dow: getDay(date: today), autoRefresh: true, filtered: true)
    }

    private func refreshAndUpdate(from: FerryPier?) {
        let group = DispatchGroup()

        group.enter()
        ferryRepository.refresh {
            group.leave()
        }

        group.enter()
        holidayRepository.refresh {
            group.leave()
        }

        group.notify(queue: .main) {
            if let it = from {
                self.updateState(from: it, dow: self.getDay(date: self.today), autoRefresh: false, filtered: true)
            }
        }
    }

    func refresh() {
        refreshAndUpdate(from: state?.from)
    }

    func fetchAll() {
        if let it = state {
            updateState(from: it.from, dow: it.day, autoRefresh: true, filtered: false)
        }
    }

    func toggleHoliday() -> Bool {
        let today = self.today
        let isHoliday = getDay(date: today) == .Holiday
        holidayRepository.setHoliday(day: today, isHoliday: !isHoliday)
        if let it = state {
            updateState(from: it.from, dow: getDay(date: today), autoRefresh: true, filtered: true)
        }
        return !isHoliday
    }

}
