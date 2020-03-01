package com.lunesu.pengchauferry.ui.ferry

import android.annotation.SuppressLint
import android.app.Application
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lunesu.pengchauferry.Utils

class LocationViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        private val criteria: Criteria get() {
            val criteria = Criteria()
            if (!Utils.isEmulator) criteria.accuracy = Criteria.ACCURACY_COARSE
            return criteria
        }
    }

    private val _location = MutableLiveData<Location>()
    private val locationManager = getSystemService(application, LocationManager::class.java)
    private val provider = locationManager?.getBestProvider(criteria, true)

    val location : LiveData<Location> get() = _location

    init {
        @SuppressLint("MissingPermission")
        val lastLocation = locationManager?.getLastKnownLocation(provider)
        if (lastLocation != null) {
            _location.value = lastLocation
        }
    }

    @SuppressLint("MissingPermission")
    fun refresh() {
//        canRequestLocationUpdate = true
        locationManager?.requestSingleUpdate(provider, object: LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    _location.value = location
                }
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderDisabled(provider: String?) {}
            override fun onProviderEnabled(provider: String?) {}
        }, null)
    }
}