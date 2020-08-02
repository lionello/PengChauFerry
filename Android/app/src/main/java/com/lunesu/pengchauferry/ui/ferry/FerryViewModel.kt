package com.lunesu.pengchauferry.ui.ferry

import android.app.Application
import android.os.CountDownTimer
import androidx.annotation.Keep
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lunesu.pengchauferry.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class FerryViewModel(
    application: Application,
    private val ferryRepository: FerryRepository,
    private val holidayRepository: HolidayRepository
) : AndroidViewModel(application) {

    private var db: DbOpenHelper? = null

    constructor(application: Application, db: DbOpenHelper) :
        this(application,
            FerryRepository(db),
            HolidayRepository(db)
        ) {
        this.db = db
    }

    // Invoked by lazy viewModels()
    @Keep
    constructor(application: Application) : this(application, DbOpenHelper(application))

    companion object {
        fun now(): LocalDateTime =
//            if (BuildConfig.DEBUG && Utils.isEmulator)
//                LocalDateTime.now().withTime(23, 40, 0, 0)
//            else
                LocalDateTime.now()
    }

    private val countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _time.value = now()
        }

        override fun onFinish() {}
    }

    data class State(val ferries: List<Ferry>, val from: FerryPier, val day: FerryDay)

    private val _state = MutableLiveData<State>() // TODO: could be lazyMap
    private val _time = MutableLiveData(now())

    val state: LiveData<State> get() = _state
    val time: LiveData<LocalDateTime> get() = _time

    fun shouldRefresh(lastRefresh: LocalDateTime?): Boolean =
        lastRefresh == null || (today.dayOfMonth == 1 && lastRefresh.toLocalDate() != today)

    private fun shouldRefresh() : Boolean =
        holidayRepository.shouldRefresh()
            || ferryRepository.shouldRefresh()
            || shouldRefresh(Preferences(getApplication()).lastRefresh)

    private fun updateState(from: FerryPier, dow: FerryDay, autoRefresh: Boolean, filtered: Boolean) {
        var ferries = ferryRepository.getFerries(from, dow)
        if (autoRefresh && (ferries.isEmpty() || shouldRefresh())) {
            refreshAndUpdate(from)
        } else {
            if (filtered) {
                val now = _time.value?.toLocalTime()
                ferries = ferries.filter { it.time >= now }
            }
            _state.value = State(
                ferries,
                from,
                dow
            )
        }
    }

    private val today: LocalDate get() = _time.value!!.toLocalDate()

    fun getDay(date: LocalDate) =
        if (holidayRepository.getHoliday(date)) FerryDay.Holiday else FerryDay.fromDate(date)

    init {
        countDownTimer.start() // not mocked
    }

    override fun onCleared() {
        countDownTimer.cancel() // not mocked
        db?.close()
        super.onCleared()
    }

    fun toggleHoliday(): Boolean {
        val today = today
        val isHoliday = getDay(today) == FerryDay.Holiday
        holidayRepository.setHoliday(today, !isHoliday)
        _state.value?.let {
            updateState(it.from, getDay(today), true, true)
        }
        return !isHoliday
    }

    fun switchPier(pier: FerryPier) {
        updateState(pier, getDay(today), true, true)
    }

    private fun refreshAndUpdate(from: FerryPier?) = viewModelScope.launch {
        awaitAll(
            async { holidayRepository.refresh() },
            async { ferryRepository.refresh() }
        )
        Preferences(getApplication()).lastRefresh = _time.value
        from?.let {
            updateState(it, getDay(today), false, true)
        }
    }

    fun refresh() {
        refreshAndUpdate(state.value?.from)
    }

    fun fetchAll() {
        _state.value?.let {
            updateState(it.from, it.day, true, false)
        }
    }
}
