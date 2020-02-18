package com.lunesu.pengchauferry

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

open class FerryRepository(db: DbOpenHelper) {
    private val ferryDao = FerryDao(db)

    open fun getFerries(from: FerryPier, dow: FerryDay): List<Ferry> {
        return ferryDao.query(from, dow)
    }

    open suspend fun refresh() = coroutineScope {
        awaitAll(
            async { ferryDao.save(PengChauToCentralFetcher.fetch(), FerryPier.Central, FerryPier.PengChau) },
            async { ferryDao.save(KaitoFetcher.fetch(), FerryPier.PengChau, FerryPier.DiscoveryBay, FerryPier.TrappistMonastery) },
            async { ferryDao.save(InterIslandsFetcher.fetch(), FerryPier.PengChau, FerryPier.MuiWo, FerryPier.CheungChau, FerryPier.ChiMaWan) }
//            async { ferry.save(KaitoFetcher2.fetch(), FerryPier.MuiWo, FerryPier.DiscoveryBay) },
        )
        Unit
    }

}
