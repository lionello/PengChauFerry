//
//  FerryRow.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import SwiftUI

struct FerryRow: View {
    @Environment(\.locale) var locale

    private var bundle: Bundle {
        let path = Bundle.main.path(forResource: locale.languageCode, ofType: "lproj")!
        return Bundle(path: path) ?? Bundle.main
    }

    var ferry: Ferry
    var mins: Int
    var selected: Bool

    private var company: FerryPier {
        ferry.to != .PengChau ? ferry.to : ferry.from
    }

    private var dest: LocalizedStringKey {
        let ferryTo = Strings.localized(ferry.to, bundle)
        if let via = ferry.via {
            let ferryVia = Strings.localized(via, bundle)
            return "to \(ferryTo) via \(ferryVia)"
        } else {
            return "to \(ferryTo)"
        }
    }

    static let uiFormatter: DateFormatter = {
        let df = DateFormatter()
        df.dateStyle = .none
        df.timeStyle = .short
        return df
    }()

    static let COLORS: [FerryPier:Color] = [
        .Central: Color("colorHKKF"),
        .TrappistMonastery: Color("colorKaito"),
        .DiscoveryBay: Color("colorKaito"),
        .MuiWo: Color("colorNWFF"),
        .CheungChau: Color("colorNWFF"),
        .ChiMaWan: Color("colorNWFF"),
        .HeiLingChau: Color("colorHKKF")
    ]

    var body: some View {
        HStack(alignment: .firstTextBaseline) {
            Image(systemName: "clock")
            VStack(alignment: .leading) {
                HStack {
                    Text("\(ferry.time.toString(FerryRow.uiFormatter)) - \(ferry.endTime.toString(FerryRow.uiFormatter))")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                    if (mins >= 0 && mins < 60) || selected {
                        Text("in \(mins) mins")
                            .foregroundColor(.red)
                            .font(.subheadline)
                    }
                    Spacer()
                    Text("$\(ferry.fare)")
                        .foregroundColor(.blue)
                        .font(.subheadline)
                }
                .fixedSize(horizontal: false, vertical: true)
                Text(dest)
                    .foregroundColor(FerryRow.COLORS[company])
                    .font(.subheadline)
                    .lineLimit(1)
                    .truncationMode(.tail)
            }
        }
        .padding([.top, .bottom], 4)
        .listRowBackground(selected ? Color("colorAccent") : nil)
    }
}

struct FerryRow_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            FerryRow(ferry: Ferry.DUMMY, mins: -2, selected: false)
            FerryRow(ferry: Ferry.DUMMY, mins: 2, selected: true)
            FerryRow(ferry: Ferry.DUMMY, mins: 222, selected: false)
        }
        .previewLayout(.fixed(width: 300, height: 70))
    }
}


