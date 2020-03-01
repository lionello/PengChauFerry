package com.lunesu.pengchauferry.ui.ferry

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lunesu.pengchauferry.*
import kotlinx.coroutines.*
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class FerryViewModel(application: Application, private val ferryRepository: FerryRepository, private val holidayRepository: HolidayRepository) : AndroidViewModel(application) {

    private var db: DbOpenHelper? = null

    constructor(application: Application, db: DbOpenHelper) :
        this(application,
            FerryRepository(db),
            HolidayRepository(db)
        ) {
        this.db = db
    }

    // Invoked by lazy viewModels()
    constructor(application: Application) : this(application, DbOpenHelper(application))

    companion object {
        fun now(): LocalDateTime =
//            if (BuildConfig.DEBUG && Utils.isEmulator)
//                LocalDateTime.now().withTime(23,40, 0, 0)
//            else
                LocalDateTime.now()
    }

    private val countDownTimer = object: CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _time.value = now()
        }

        override fun onFinish() {}
    }

    data class State(val ferries: List<Ferry>, val from: FerryPier, val day: FerryDay)

    private val _state = MutableLiveData<State>() // TODO: could be lazyMap
    private val _time = MutableLiveData<LocalDateTime>(now())

    val state : LiveData<State> get() = _state
    val time : LiveData<LocalDateTime> get() = _time

    private fun updateState(from: FerryPier, dow: FerryDay) {
        val ferries = ferryRepository.getFerries(from, dow)
        _state.value = State(
            ferries,
            from,
            dow
        )
        if (ferries.isEmpty() || holidayRepository.shouldRefresh()) {
            refresh()
        }
    }

    private val today : LocalDate get() = _time.value!!.toLocalDate()
    private fun getDay() : FerryDay =
        if (holidayRepository.getHoliday(today)) FerryDay.Holiday else FerryDay.fromDate(today)

    init {
        if (!Utils.isEmulator) {
            countDownTimer.start() // not mocked
        }
    }

    override fun onCleared() {
        if (!Utils.isEmulator) {
            countDownTimer.cancel() // not mocked
        }
        db?.close()
        super.onCleared()
    }

    fun toggleHoliday(): Boolean {
        val isHoliday = getDay() == FerryDay.Holiday
        holidayRepository.setHoliday(today, !isHoliday)
        _state.value?.let {
            updateState(it.from, getDay())
        }
        return !isHoliday
    }

    fun switchPier(pier: FerryPier) {
        updateState(pier, getDay())
    }

    fun refresh() = viewModelScope.launch {
        awaitAll(
            async { holidayRepository.refresh() },
            async { ferryRepository.refresh() }
        )
        _state.value?.let {
            updateState(it.from, getDay())
        }
    }

}
