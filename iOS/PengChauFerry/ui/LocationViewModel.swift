//
//  LocationViewModel.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 15/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation
import Combine
import CoreLocation

class LocationViewModel: NSObject, ObservableObject {
    private let locationManager = CLLocationManager()

    @Published private(set) var location: CLLocation?

    override init() {
        super.init()
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters
        self.locationManager.requestWhenInUseAuthorization()
        self.locationManager.startUpdatingLocation()
    }
}

extension LocationViewModel: CLLocationManagerDelegate {
//    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
//        self.status = status
//    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let location = locations.last {
            // "The methods of your delegate object are called from the thread in which you started the corresponding
            // location services. That thread must itself have an active run loop, like the one found in your
            // application’s main thread."
            self.location = location
        }
    }
}
