//
//  HolidayFetcher.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

class HongKongHolidayFetcher {
    static let YEAR = 2021
    private static let url = "https://www.gov.hk/en/about/abouthk/holiday/\(YEAR).htm"

    static func fetch(completion: ([LocalDate], Error?) -> Void) {
        if let data = JsonData.load(from: Bundle.main) {
            let dates = data.holidays.map { LocalDate.parse($0)! }
            completion(dates, nil)
        } else {
            completion([], ApiError.NoData)
        }
    }
}
