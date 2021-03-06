//
//  JsonFetcher.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

enum ApiError: Error {
    case NoData
}

class JsonFetcher {

    static func fetch(completion: ([Ferry], ApiError?) -> Void) {
        if let data = JsonData.load(from: Bundle.main) {
            completion(data.ferries, nil)
        } else {
            completion([], .NoData)
        }
    }
}
