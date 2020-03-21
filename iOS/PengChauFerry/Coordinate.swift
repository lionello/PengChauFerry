//
//  Coordinate.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation
import CoreLocation

typealias Coordinate = CLLocationCoordinate2D

extension Coordinate {
//    let latitude: Double
//    let longitude: Double

    private static let RADIUS = 6372.8e3

    private static func toRadians(_ number: Double) -> Double { number * .pi / 180.0 }

    private static func hav(_ Θ: Double) -> Double {
        return pow(sin(Θ / 2), 2)
    }

    private static func haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double) -> Float {
        let φ1 = toRadians(lat1)
        let φ2 = toRadians(lat2)
        let λ1 = toRadians(lon1)
        let λ2 = toRadians(lon2)
        // From https://en.wikipedia.org/wiki/Haversine_formula; similar to Location.distanceBetween
        return Float(2 * RADIUS * asin(sqrt(hav(φ1 - φ2) + cos(φ1) * cos(φ2) * hav(λ1 - λ2))))
    }

    func distance(lat: Double, long: Double) -> Float {
        return Coordinate.haversine(lat1: lat, lon1: long, lat2: latitude, lon2: longitude)
    }
}

