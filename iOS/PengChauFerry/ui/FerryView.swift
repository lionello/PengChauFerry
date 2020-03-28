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
    @Environment(\.locale) var locale

    private static let LANGUAGE_PREF = "language"

    private static var languagePref: String? {
        get { UserDefaults.standard.string(forKey: FerryView.LANGUAGE_PREF) }
        set(value) { UserDefaults.standard.set(value, forKey: FerryView.LANGUAGE_PREF) }
    }

    private var bundle: Bundle {
        let path = Bundle.main.path(forResource: languageCode, ofType: "lproj")!
        return Bundle(path: path) ?? Bundle.main
    }

    @State private var showPicker = false
    @State private var loading = false
    @State private var now : LocalTime = LocalTime.now()
    @State private var walkingTime = 0
    @State private var selected: Ferry?
    @State private var shouldSwitchPier = true
    @State private var languageCode: String? = FerryView.languagePref

    static let WALKING_SPEED: Float = 0.018

    static let PIERS: [FerryPier] = [
        .Central,
        .PengChau,
        .DiscoveryBay,
        .MuiWo
    ]

    private var title: LocalizedStringKey {
        if let day = viewModel.state?.day {
            return "Peng Chau Ferries (\(Strings.localized(day, bundle)))"
        } else {
            return "Peng Chau Ferries"
        }
    }

    private func updateSelected(ferries: [Ferry]?) {
        if let ferries = ferries {
            let now = self.now.plus(minutes: self.walkingTime)
            self.selected = ferries.first { $0.time >= now } ?? ferries.last
        }
    }

    private func updateWalkingTime(from: CLLocation, to: FerryPier) {
        let minutes = Int(ceil(to.coordinate.distance(lat: from.coordinate.latitude, long: from.coordinate.longitude) * FerryView.WALKING_SPEED))
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

        return ZStack {
            VStack(spacing:0) {
                VStack(alignment: .leading) {
                    HStack {
                        Text(self.title)
                            .foregroundColor(.white)
                            .font(.headline)
                        Spacer()
                        Button(self.languageCode != "zh" ? "中" : "EN") {
                            let lang = (self.languageCode != "zh" ? "zh" : "en")
                            FerryView.languagePref = lang
                            self.languageCode = lang
                        }
                        .foregroundColor(.white)
                    }

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
                            Text(Strings.PIERS[viewModel.state?.from] ?? "Select departure pier")
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
                    .background(Color("colorPrimary"))
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
            .background(Color("colorPrimary"))
            .overlay(self.showPicker ? Color.black.opacity(0.5) : nil)
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

                if let nowPier = FerryPier.findNearest(latitude: it.coordinate.latitude, longitude: it.coordinate.longitude, piers: FerryView.PIERS) {
                    if nowPier != self.viewModel.state?.from {
                        // TODO: Toast
                    }
                    if self.shouldSwitchPier {
                        self.shouldSwitchPier = false
                        self.viewModel.switchPier(nowPier)
                    }
                }
            }

            if showPicker {
                Picker("起點 DEPART FROM", selection: binding) {
                    ForEach(FerryView.PIERS) { pier in
                        Text(Strings.PIERS[pier]!).tag(pier)
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
        .environment(\.locale, languageCode != nil ? .init(identifier: languageCode!) : locale)
    }
}

struct FerryView_Previews: PreviewProvider {
    static var previews: some View {
        FerryView()
            .environmentObject(FerryViewModel())
    }
}
