package com.lunesu.pengchauferry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.joda.time.LocalDate
import org.joda.time.LocalTime

class FerryViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        fun now(): LocalTime = if (BuildConfig.DEBUG && Utils.isEmulator) LocalTime(23,40) else LocalTime.now()
    }

    private val countDownTimer = object: CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _time.value = now()
        }

        override fun onFinish() {}
    }

    data class State(val ferries: List<Ferry>, val from: FerryPier, val isHoliday: Boolean)

    private val db = DbOpenHelper(application)
    private val ferryRepository = FerryRepository(db)
    private val holidaysRepository = HolidaysRepository(db)

    private val _state = MutableLiveData<State>() // TODO: could be lazyMap
    private val _time = MutableLiveData<LocalTime>()

    val state : LiveData<State> = _state
    val time : LiveData<LocalTime> = _time
    val today = LocalDate.now()

    private fun updateState(from: FerryPier, dow: FerryDay) {
        val ferries = ferryRepository.getFerries(from, dow)
        _state.value = State(ferries, from, dow == FerryDay.Holiday)
    }

    private fun getDay() : FerryDay = if (holidaysRepository.getHoliday(today)) FerryDay.Holiday else FerryDay.fromDate(today)

    init {
        _time.value = now()
        countDownTimer.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer.cancel()
    }

    fun toggleHoliday(): Boolean {
        val isHoliday = getDay() == FerryDay.Holiday
        holidaysRepository.setHoliday(today, !isHoliday)
        updateState(_state.value!!.from, getDay())
        return !isHoliday
    }

    fun switchPier(pier: FerryPier) {
        updateState(pier, getDay())
    }

    fun refresh() = viewModelScope.launch {
        awaitAll(
            async { holidaysRepository.refresh() },
            async { ferryRepository.refresh() }
        )
        updateState(_state.value!!.from, getDay())
    }

}
