package com.lunesu.pengchauferry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.os.Build
import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import org.joda.time.LocalTime

class FerryViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        fun now() = if (BuildConfig.DEBUG && Utils.isEmulator) LocalTime(23,40) else LocalTime.now()
    }

    private val countDownTimer = object: CountDownTimer(Long.MAX_VALUE, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _time.value = now()
        }

        override fun onFinish() {}
    }

    private val db = DbOpenHelper(application)
    private val ferryRepository = FerryRepository(db)
    private val holidaysRepository = HolidaysRepository(db)

    private val _ferries = MutableLiveData<List<Ferry>>()
    private val _time = MutableLiveData<LocalTime>()

    val ferries : LiveData<List<Ferry>> = _ferries
    val time : LiveData<LocalTime> = _time
    val from = MutableLiveData<FerryPier>()
    val isHoliday = MutableLiveData<Boolean>()

    val today = LocalDate.now()
    val dow : FerryDay get() = if (isHoliday.value!!) FerryDay.Holiday else FerryDay.fromDate(today)

    init {
        from.value = FerryPier.PengChau // TODO: from GPS or last saved
        _time.value = now()
        isHoliday.value = holidaysRepository.getHoliday(today)
        _ferries.value = ferryRepository.getFerries(from.value!!, dow)
        countDownTimer.start()

        from.observeForever {
            _ferries.value = ferryRepository.getFerries(it!!, dow)
        }
        isHoliday.observeForever {
            holidaysRepository.setHoliday(today, it)
            _ferries.value = ferryRepository.getFerries(from.value!!, dow)
        }
    }

    fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        holidaysRepository.refresh()
        isHoliday.postValue(holidaysRepository.getHoliday(today))
        ferryRepository.refresh()
        _ferries.postValue(ferryRepository.getFerries(from.value!!, dow))
    }

}
