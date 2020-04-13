//
//  FerryPier.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

public enum FerryPier: String, Codable {
    case Central
    case PengChau
    case TrappistMonastery
    case DiscoveryBay
    case MuiWo
    case CheungChau
    case ChiMaWan
    case HeiLingChau

    static let ENUMS = [Central, PengChau, TrappistMonastery, DiscoveryBay, MuiWo, CheungChau, ChiMaWan, HeiLingChau]

    static let COORDS: [FerryPier: Coordinate] = [
        .Central: Coordinate(latitude: 22.2871, longitude: 114.1602),
        .PengChau: Coordinate(latitude: 22.2846, longitude: 114.0379),
        .TrappistMonastery: Coordinate(latitude: 22.2819, longitude: 114.0225),
        .DiscoveryBay: Coordinate(latitude: 22.2937, longitude: 114.0210),
        .MuiWo: Coordinate(latitude: 22.2647, longitude: 114.0019),
        .CheungChau: Coordinate(latitude: 22.2084, longitude: 114.0287),
        .ChiMaWan: Coordinate(latitude: 22.2393, longitude: 113.9995),
        .HeiLingChau: Coordinate(latitude: 22.2579, longitude: 114.0280)
    ]

    var coordinate: Coordinate {
        get { FerryPier.COORDS[self]! }
    }

    static func findNearest(latitude: Double, longitude: Double, piers: [FerryPier] = ENUMS) -> FerryPier? {
        let dists = piers.map { (key: $0, value: COORDS[$0]!.distance(lat: latitude, long: longitude)) }
        return dists.min { $0.value < $1.value }?.key
    }

    static func valueOf(_ s: String) -> FerryPier? {
        FerryPier(rawValue: s)
    }

    func toString() -> String {
        return rawValue
    }
}

extension FerryPier: Identifiable {
    public var id: String {
        get { self.rawValue }
    }
}
