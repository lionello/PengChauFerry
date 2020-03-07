package com.lunesu.pengchauferry.ui.ferry

import android.Manifest.permission
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

    private fun hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(getApplication(), permission) == PackageManager.PERMISSION_GRANTED

    private val criteria = Criteria().apply {
        powerRequirement = Criteria.POWER_LOW // avoid GPS
        accuracy = Criteria.ACCURACY_MEDIUM
    }
    private val locationManager = getSystemService(application, LocationManager::class.java)
    private val provider get() = locationManager?.getBestProvider(criteria, true)
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

    @SuppressLint("MissingPermission")
    fun refresh() {
        if (hasPermission(permission.ACCESS_COARSE_LOCATION) || hasPermission(permission.ACCESS_FINE_LOCATION)) {
            val lastLocation = locationManager?.getLastKnownLocation(provider)
            if (lastLocation != null) {
                _location.value = lastLocation
            }

            stop()
//            locationManager?.requestSingleUpdate(provider, this, null)//getApplication<Application>().mainLooper)
            locationManager?.requestLocationUpdates(provider, 500, 0.0f, this)
        }
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            _location.value?.let {
                if (location.accuracy >= it.accuracy) {
                    stop()
                }
            }
            _location.postValue(location)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}
}