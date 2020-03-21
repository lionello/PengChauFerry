//
//  Json.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

//fileprivate struct FerryX: Decodable {
//    let time: String
//    let from: FerryPier
//    let to: FerryPier
//    let dur: TimeInterval
//    let days: FerryDays
//    let fare: String
//    let via: FerryPier?
//}
//
struct JsonData: Decodable {
    var holidays: [String] = []
    var ferries: [Ferry] = []

    static func load(from bundle: Bundle) -> JsonData? {
        guard let url = bundle.url(forResource: "db", withExtension: "json"),
            let data = try? Data(contentsOf: url) else {
            return nil
        }

        return try? JSONDecoder().decode(JsonData.self, from: data)
    }

}
