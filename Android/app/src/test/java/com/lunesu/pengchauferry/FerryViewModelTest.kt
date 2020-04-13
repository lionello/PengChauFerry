package com.lunesu.pengchauferry

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lunesu.pengchauferry.ui.ferry.FerryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FerryViewModelTest {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule() // testImplementation 'android.arch.core:core-testing:1.1.1'

    private val db = DbOpenHelper(null)

    private val ferryRepository = object : FerryRepository(db) {
        val ferry = Ferry(LocalTime.MIDNIGHT, FerryPier.Central, FerryPier.PengChau, Duration.standardMinutes(1), FerryDay.MondayToSaturday, "1.2", null)
        var ferries = mutableListOf<Ferry>()
        override fun getFerries(from: FerryPier, dow: FerryDay): List<Ferry> = ferries.filter { it.from == from && it.days.contains(dow) }
        override suspend fun refresh() { ferries.add(ferry) }
    }

    private val holidayRepository = object : HolidayRepository(db) {
        val newYear2020 = LocalDate(2020, 1, 1)
        val holidays = HashSet<LocalDate>()
        override fun getHoliday(day: LocalDate) = holidays.contains(day)
        override fun setHoliday(day: LocalDate, isHoliday: Boolean) {
            if (isHoliday) holidays.add(day) else holidays.remove(day)
        }
        override suspend fun refresh() { holidays.add(newYear2020) }
        override fun shouldRefresh(): Boolean = false
    }

    @Test
    fun testSwitchPier() = runBlockingTest {
        val vm = FerryViewModel(
            Application(),
            ferryRepository,
            holidayRepository
        )
        var state: FerryViewModel.State? = null
        vm.state.observeForever {
            state = it
        }
        vm.switchPier(FerryPier.Central)
        assertNotNull(state)
        assertNotEquals(FerryDay.Holiday, state!!.day)
    }

    @Test
    fun testToggleHoliday() {
        val vm = FerryViewModel(
            Application(),
            ferryRepository,
            holidayRepository
        )
        var state: FerryViewModel.State? = null
        vm.state.observeForever {
            state = it
        }
        vm.toggleHoliday()
        assertNull(state)
//        vm.switchPier(FerryPier.Central)
//        assertNotNull(state)
//        assertTrue(state!!.isHoliday)
    }

    @Test
    fun testTime() {
        val vm = FerryViewModel(
            Application(),
            ferryRepository,
            holidayRepository
        )
        var time: LocalDateTime? = null
        vm.time.observeForever {
            time = it
        }
        assertNotNull(time)
    }

    @Test
    fun testRefresh() = runBlockingTest {
        val vm = FerryViewModel(
            Application(),
            ferryRepository,
            holidayRepository
        )
        vm.refresh()
        assertEquals(1, ferryRepository.ferries.size)
        assertEquals(1, holidayRepository.holidays.size)
    }

    @Test
    fun testUpdateTriggersRefresh() = runBlockingTest {
        val vm = FerryViewModel(
            Application(),
            ferryRepository,
            holidayRepository
        )
        var state: FerryViewModel.State? = null
        vm.state.observeForever {
            state = it
        }
        vm.switchPier(ferryRepository.ferry.from)
        assertNotNull(state)
    }
}
