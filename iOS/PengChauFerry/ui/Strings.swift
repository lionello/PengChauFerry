//
//  Strings.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import SwiftUI

class Strings {
    static let PIERS : [FerryPier?:LocalizedStringKey] = [
        .Central : "中環 Central",
        .PengChau : "坪洲 Peng Chau",
        .TrappistMonastery : "聖母神樂院 Trappist Monastery",
        .DiscoveryBay : "愉景灣 Discovery Bay",
        .MuiWo : "梅窩 Mui Wo",
        .CheungChau : "長洲 Cheung Chau",
        .ChiMaWan : "芝麻灣 Chi Ma Wan",
        .HeiLingChau : "喜靈洲 HeiLingChau"
    ]

    static let DAYS : [FerryDay?:LocalizedStringKey] = [
        .Monday : "Monday",
        .Tuesday : "Tuesday",
        .Wednesday : "Wednesday",
        .Thursday : "Thursday",
        .Friday : "Friday",
        .Saturday : "Saturday",
        .Sunday : "Sunday",
        .Holiday : "Holiday"
    ]

    static func localized(_ pier: FerryPier, _ bundle: Bundle) -> String {
        bundle.localizedString(forKey: pier.rawValue, value: nil, table: nil)
    }

    static func localized(_ day: FerryDay, _ bundle: Bundle) -> String {
        bundle.localizedString(forKey: day.toString(), value: nil, table: nil)
    }

}
