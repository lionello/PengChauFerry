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
            async {
                runCatching {
                    ferryDao.save(
                        Utils.atLeast(PengChauToCentralFetcher.fetch(), 80),
                        FerryPier.Central,
                        FerryPier.PengChau,
                        FerryPier.HeiLingChau
                    )
                }
            },
            async {
                runCatching {
                    ferryDao.save(
                        Utils.atLeast(KaitoFetcher.fetch(), 50),
                        FerryPier.PengChau,
                        FerryPier.DiscoveryBay,
                        FerryPier.TrappistMonastery
                    )
                }
            },
            async {
                runCatching {
                    ferryDao.save(
                        Utils.atLeast(KaitoMuiWoFetcher.fetch(), 20),
                        FerryPier.DiscoveryBay,
                        FerryPier.MuiWo
                    )
                }
            },
            async {
                runCatching {
                    ferryDao.save(
                        Utils.atLeast(InterIslandsFetcher.fetch(), 10),
                        FerryPier.PengChau,
                        FerryPier.MuiWo,
                        FerryPier.CheungChau,
                        FerryPier.ChiMaWan
                    )
                }
            }
        )
        Unit
    }
}
