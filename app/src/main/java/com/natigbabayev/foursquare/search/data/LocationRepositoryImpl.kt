package com.natigbabayev.foursquare.search.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.natigbabayev.foursquare.search.domain.model.LocationData
import com.natigbabayev.foursquare.search.domain.repository.LocationRepository
import io.reactivex.Single
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val application: Application,
    private val locationManager: LocationManager
) : LocationRepository {

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override fun getLocation(): Single<LocationData> {
        return Single.create {
            if (!it.isDisposed) {
                val location =
                    locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                it.onSuccess(
                    LocationData(
                        latitude = location?.latitude ?: 0.0,
                        longitude = location?.longitude ?: 0.0
                    )
                )
            }
        }
    }
}
