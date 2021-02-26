package com.natigbabayev.foursquare.search.domain.repository

import com.natigbabayev.foursquare.search.domain.model.LocationData
import io.reactivex.Single

interface LocationRepository {
    fun hasLocationPermission(): Boolean
    fun getLocation(): Single<LocationData>
}
