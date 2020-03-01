package com.lunesu.pengchauferry.ui.ferry

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lunesu.pengchauferry.Utils

class LocationViewModel(application: Application): AndroidViewModel(application) {

    private fun hasPermission(permission: String) = ContextCompat.checkSelfPermission(getApplication(), permission) == PackageManager.PERMISSION_GRANTED

    private val hasCoarse get()= hasPermission(permission.ACCESS_COARSE_LOCATION)
    private val hasFine get() = hasPermission(permission.ACCESS_FINE_LOCATION)
    private val criteria: Criteria get() {
        val criteria = Criteria()
        criteria.powerRequirement = Criteria.POWER_LOW // avoid GPS
        if (hasCoarse) criteria.accuracy = Criteria.ACCURACY_COARSE
        return criteria
    }

    private val _location = MutableLiveData<Location>()
    private val locationManager = getSystemService(application, LocationManager::class.java)
    private val provider get() = locationManager?.getBestProvider(criteria, true)

    val location : LiveData<Location> get() = _location

    init {
        refresh()
    }

    @SuppressLint("MissingPermission")
    fun refresh() {
        if (hasFine || hasCoarse) {
            val lastLocation = locationManager?.getLastKnownLocation(provider)
            if (lastLocation != null) {
                _location.value = lastLocation
            }

            locationManager?.requestSingleUpdate(provider, object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (location != null) {
                        _location.value = location
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderDisabled(provider: String?) {}
                override fun onProviderEnabled(provider: String?) {}
            }, getApplication<Application>().mainLooper)
        }
    }
}