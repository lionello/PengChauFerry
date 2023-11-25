//
//  Json.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

struct JsonData: Decodable {
    var holidays: [String] = []
    var ferries: [Ferry] = []

    static func load(from bundle: Bundle) -> JsonData? {
        guard let url = bundle.url(forResource: "db", withExtension: "json"),
            let data = try? Data(contentsOf: url) else {
            return nil
        }

        var jsonData = try? JSONDecoder().decode(JsonData.self, from: data)
        // The JSON file has the duration in minutes, but our Duration type is in seconds; convert it now
        if let ferries = jsonData?.ferries {
            jsonData!.ferries = ferries.map { f in
                Ferry(time: f.time, from: f.from, to: f.to, dur: f.dur*60, days: f.days, fare: f.fare, via: f.via)
            }
        }
        return jsonData
    }

}
