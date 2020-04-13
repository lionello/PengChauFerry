//
//  DummyFetcher.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

enum ApiError: Error {
    case NoData
}

class DummyFetcher {

    static func fetch(completion: ([Ferry], Error?) -> Void) {
        if let data = JsonData.load(from: Bundle.main) {
            completion(data.ferries, nil)
        } else {
            completion([], ApiError.NoData)
        }
    }
}
