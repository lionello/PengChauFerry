//
//  FerryRow.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import SwiftUI

struct FerryRow: View {
    var ferry: Ferry
    var mins: Int
    var selected: Bool

    private var company: FerryPier {
        ferry.to != .PengChau ? ferry.to : ferry.from
    }

    private var dest: String {
        if let via = ferry.via {
            return "to \(STRINGS[ferry.to]!) via \(STRINGS[via]!)"
        } else {
            return "to \(STRINGS[ferry.to]!)"
        }
    }

    static let uiFormatter: DateFormatter = {
        let df = DateFormatter()
        df.dateStyle = .none
        df.timeStyle = .short
        return df
    }()

    static let COLORS: [FerryPier:Color] = [
        .Central: Color(rgb:0x016AA9),
        .TrappistMonastery: Color(rgb:0xED1C23),
        .DiscoveryBay: Color(rgb:0xED1C23),
        .MuiWo: Color(rgb:0xEA771D),
        .CheungChau: Color(rgb:0xEA771D),
        .ChiMaWan: Color(rgb:0xEA771D),
        .HeiLingChau: Color(rgb:0x016AA9)
    ]

    var body: some View {
        HStack(alignment: .firstTextBaseline) {
            Image(systemName: "clock")
            VStack(alignment: .leading) {
                HStack {
                    Text("\(ferry.time.toString(FerryRow.uiFormatter)) - \(ferry.endTime.toString(FerryRow.uiFormatter))")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                    if mins >= 0 && mins < 120 {
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
        .listRowBackground(selected ? Color(rgb:0x223344).opacity(0x11/255.0) : nil)
    }
}

struct FerryRow_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            FerryRow(ferry: Ferry.DUMMY, mins: -2, selected: false)
            FerryRow(ferry: Ferry.DUMMY, mins: 2, selected: true)
            FerryRow(ferry: Ferry.DUMMY, mins: 222, selected: false)
//            FerryRow()
        }
        .previewLayout(.fixed(width: 300, height: 70))
    }
}


