package com.lunesu.pengchauferry

import kotlinx.coroutines.*

class FerryRepository(db: DbOpenHelper) {
    private val ferry = FerryDao(db)

    fun getFerries(from: FerryPier, dow: FerryDay): List<Ferry> {
        return ferry.query(from, dow)
    }

    suspend fun refresh() = GlobalScope.launch {
        awaitAll(
            async { ferry.save(PengChauToCentralFetcher.fetch(), FerryPier.Central, FerryPier.PengChau) },
            async { ferry.save(KaitoFetcher.fetch(), FerryPier.PengChau, FerryPier.DiscoveryBay, FerryPier.TrappistMonastery) },
            async { ferry.save(InterIslandsFetcher.fetch(), FerryPier.PengChau, FerryPier.MuiWo, FerryPier.CheungChau, FerryPier.ChiMaWan) }
//            async { ferry.save(KaitoFetcher2.fetch(), FerryPier.MuiWo, FerryPier.DiscoveryBay) },
        )
    }

}
