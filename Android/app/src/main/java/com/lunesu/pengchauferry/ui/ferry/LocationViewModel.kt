package com.lunesu.pengchauferry.ui.ferry

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LocationViewModel(application: Application): AndroidViewModel(application), LocationListener {

    private val locationManager = getSystemService(application, LocationManager::class.java)
    private val _location = MutableLiveData<Location>()

    val location : LiveData<Location> get() = _location

    init {
        refresh()
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }

    private fun stop() {
        locationManager?.removeUpdates(this)
    }

    private val _accuracy : Float get() = _location.value?.accuracy ?: Float.MAX_VALUE

    @SuppressLint("MissingPermission")
    fun refresh() {
        stop()

        val oneMinuteAgo = System.currentTimeMillis() / 1000L - 60
        locationManager?.getProviders(true)?.forEach {
            val lastLocation = locationManager.getLastKnownLocation(it)
            if (lastLocation != null && lastLocation.time >= oneMinuteAgo && lastLocation.accuracy <= _accuracy) {
                _location.value = lastLocation
            }
        }

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val bestProvider = locationManager?.getBestProvider(criteria, true)
        if (bestProvider != null) {
            locationManager?.requestLocationUpdates(bestProvider, 1000, 0.0f, this)
        }
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            // Stop the periodic updates if the new accuracy is the same or worse
            if (_accuracy <= location.accuracy) {
                stop()
            }
            _location.postValue(location)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}
}