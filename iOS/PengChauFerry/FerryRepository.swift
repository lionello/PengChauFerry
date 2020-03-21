//
//  FerryRepository.swift
//  PengChauFerry
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

class FerryRepository {
    private let ferryDao = FerryDao()

    open func getFerries(from: FerryPier, dow: FerryDay) -> [Ferry] {
        return ferryDao.query(from: from, dow: dow)
    }

    open func refresh(completion: @escaping () -> Void) {
        let group = DispatchGroup()
        
        group.enter()
        DummyFetcher.fetch { ferries, _ in
            if ferries.count > 80 {
                ferryDao.save(result: ferries, piers: .Central, .PengChau, .HeiLingChau, .DiscoveryBay, .TrappistMonastery, .MuiWo, .CheungChau, .ChiMaWan)
//                ferryDao.save(result: ferries, piers: .Central, .PengChau, .HeiLingChau)
            }
            group.leave()
        }

//        group.enter()
//        DummyFetcher.fetch { ferries, _ in
//            if ferries.count > 50 {
//                ferryDao.save(result: ferries, piers: .PengChau, .DiscoveryBay, .TrappistMonastery)
//            }
//            group.leave()
//        }

//        group.enter()
//        DummyFetcher.fetch { ferries, _ in
//            if ferries.count > 10 {
//                ferryDao.save(result: ferries, piers: .PengChau, .MuiWo, .CheungChau, .ChiMaWan)
//            }
//            group.leave()
//        }

        group.notify(queue: .main) {
            completion()
        }
    }


}
