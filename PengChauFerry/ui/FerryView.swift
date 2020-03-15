//
//  FerryView.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import SwiftUI
import CoreLocation

struct FerryView: View {
    @EnvironmentObject var viewModel: FerryViewModel
    @ObservedObject var locationViewModel = LocationViewModel()

    @State private var showPicker = false
    @State private var loading = false
    @State private var now : LocalTime = LocalTime.now()
    @State private var walkingTime = 0
    @State private var selected: Ferry?
    @State private var shouldSwitchPier = true

    static let WALKING_SPEED: Float = 0.018

    static let PIERS: [FerryPier] = [
        .Central,
        .PengChau,
        .DiscoveryBay,
        .MuiWo
    ]

    private var title: String {
        if let day = viewModel.state?.day {
            return "Peng Chau Ferries (\(day.toString()))"
        } else {
            return "Peng Chau Ferries"
        }
    }

//    private var firstFerry: Ferry? {
//        let now = self.now.plus(minutes: walkingTime)
//        return viewModel.state?.ferries.first { $0.time > now }
//    }

    private func updateSelected(ferries: [Ferry]?) {
        if let ferries = ferries {
            let now = self.now.plus(minutes: self.walkingTime)
            self.selected = ferries.first { $0.time >= now } ?? ferries.last
        }
    }

    private func updateWalkingTime(from: CLLocation, to: FerryPier) {
        let minutes = Int(ceil(from.coordinate.distance(lat: to.latitude, long: to.longitude) * FerryView.WALKING_SPEED))
        if minutes < 60 {
            self.walkingTime = minutes
        } else {
            self.walkingTime = 0
        }
    }

    var body: some View {
        let binding = Binding<FerryPier>(
            get: { self.viewModel.state?.from ?? .PengChau },
            set: { self.viewModel.switchPier($0) }
        )
//        let binding = Binding<Int>(
//            get: { return 0 },
//            set: { self.viewModel.switchPier(FerryView.PIERS[$0]) }
//        )
        return ZStack {
            VStack(spacing:0) {
                VStack(alignment: .leading) {
                    Text(self.title)
                        .foregroundColor(.white)
                        .font(.headline)

                    HStack(alignment: .bottom) {
                        Image("boat_white")
                        Text("起點 DEPART FROM")
                            .foregroundColor(.white)
                        Spacer()
                    }//HStack

                    Button(action: {
                        self.showPicker = true
                    }) {
                        HStack {
                            Text(STRINGS[viewModel.state?.from] ?? "Select departure pier")
                            Spacer()
                            //Image()
                            if self.walkingTime > 0 && self.walkingTime < 60 {
                                Text("~\(walkingTime) mins")
                                    .foregroundColor(.gray)
                            }
                            Text("▼")
                        }
                        .padding()
                    }
                    .background(Color.white)
                    .cornerRadius(7)
                }//VStack
                    .padding()
                    .background(Color(rgb:0x008577))
                    .clipped()
                    .shadow(color: .black, radius: 10, x: 0, y: 1)
                List{
                    // Per item listRowBackground only works with ForEach
                    ForEach(viewModel.state?.ferries ?? []) { ferry in
                        FerryRow(ferry: ferry, mins: LocalTime.minutesBetween(self.now, ferry.time), selected: ferry == self.selected)
                    }
                }.pullToRefresh(isShowing: $loading) {
                    self.viewModel.fetchAll()
                    self.loading = false
                }
                .padding([.leading, .trailing])
                .onReceive(viewModel.time) { date in
                    self.now = date.toLocalTime()
                    self.updateSelected(ferries: self.viewModel.state?.ferries)
                }
            }//VStack
            .background(Color(rgb:0x008577))
            .overlay(self.showPicker ? Color.black.opacity(0.2) : nil)
            .onTapGesture {
                self.showPicker = false
            }
            .onReceive(viewModel.$state) { state in
                if let loc = self.locationViewModel.location {
                    if let pier = state?.from {
                        self.updateWalkingTime(from: loc, to: pier)
                    }
                }

                self.updateSelected(ferries: state?.ferries)
            }
            .onReceive(locationViewModel.$location) { 
                guard let it = $0 else { return }
                if let from = self.viewModel.state?.from {
                    self.updateWalkingTime(from: it, to: from)
                }

                let nowPier = FerryPier.findNearest(latitude: it.coordinate.latitude, longitude: it.coordinate.longitude)
                if nowPier != self.viewModel.state?.from {
                    // Toast
                    if self.shouldSwitchPier {
                        self.shouldSwitchPier = false
                        self.viewModel.switchPier(nowPier)
                    }
                }
            }

            if showPicker {
                Picker("起點 FROM", selection: binding) {
                    ForEach(FerryView.PIERS) { pier in
                        Text(STRINGS[pier]!).tag(pier)
                    }
//                    ForEach(0..<FerryView.PIERS.count) { i in
//                        Text(FerryView.PIERS[i].description).tag(i)
//                    }
                }//Picker
                .labelsHidden()
                .background(Color.white)
                .onTapGesture {
                    self.showPicker = false
                }
//                .onReceive(selection.publisher, perform: { i in
//                    self.viewModel.switchPier(PIERS[i])
//                })
            }
        }//ZStack
    }
}

struct FerryView_Previews: PreviewProvider {
    static var previews: some View {
        FerryView()
            .environmentObject(FerryViewModel())
    }
}
