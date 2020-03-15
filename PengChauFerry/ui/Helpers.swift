//
//  Helpers.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation
import SwiftUI

extension Color {
    @available(*, deprecated, message: "Use named colors instead")
    init(rgb: UInt) {
        let ooFF = 1.0/255.0
        self.init(red: Double(rgb>>16)*ooFF, green: Double((rgb>>8)&255)*ooFF, blue: Double(rgb&255)*ooFF)
//        self.opacity(Double(rgb>>24)*ooFF)
    }
}
